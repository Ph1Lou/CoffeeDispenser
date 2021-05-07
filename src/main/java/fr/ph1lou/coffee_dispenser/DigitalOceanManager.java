package fr.ph1lou.coffee_dispenser;


import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Delete;
import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Image;
import com.myjeeva.digitalocean.pojo.Network;
import com.myjeeva.digitalocean.pojo.Region;
import fr.ph1lou.coffee_dispenser.enums.Reason;
import fr.ph1lou.coffee_dispenser.enums.Slug;
import fr.ph1lou.coffee_dispenser.utils.FilesUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class DigitalOceanManager {

    private final DigitalOceanClient apiClient;

    private final Main main;

    private final String bungeeIp;

    private final String serverName;

    private final boolean developerUsage;

    private final Map<UUID, ScheduledTask> tasks = new HashMap<>();

    private final Map<UUID,Integer> dropletIds = new HashMap<>();

    public DigitalOceanManager(Main main) throws IOException {

        this.main=main;

        Configuration configuration = ConfigurationProvider
                .getProvider(YamlConfiguration.class)
                .load(new File(main.getDataFolder(),
                        "config.yml"));

        String token = configuration.getString("digital.token");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        this.apiClient = new DigitalOceanClient("v2", token, httpClient);
        this.serverName = configuration.getString("server.name");
        this.bungeeIp = configuration.getString("bungee.ip");
        this.developerUsage = configuration.getBoolean("developer");
    }

    public void createDroplet(Main main,
                              String region,
                              String image,
                              String scriptName,
                              int ram,
                              UUID uuid,
                              Map<String,String> values)
            throws RequestUnsuccessfulException, DigitalOceanException {

        Slug size;
        if(ram<=1024){
            size=Slug.S_1GO;
        }
        else if(ram<=4096){
            size=Slug.C_4GO;
        }
        else if(ram <= 8192){
            size=Slug.C_8GO;
        }
        else if(ram <= 16384){
            size=Slug.C_16GO;
        }
        else if(ram <= 32768){
            size=Slug.C_32GO;
        }
        else {
            size=Slug.C_64GO;
        }

        Droplet droplet = new Droplet();
        String script = FilesUtils.loadContent(main,scriptName);
        script = script.replace("&ram&",String.valueOf(size.getRam()));
        script = script.replace("&bungee_ip&", bungeeIp);
        script = script.replace("&uuid&", uuid.toString());
        for(String key:values.keySet()){
            script=script.replace(key,values.get(key));
        }
        droplet.setSize(size.getSlug());

        droplet.setName(uuid.toString());
        droplet.setUserData(script);
        droplet.setRegion(new Region(region));
        droplet.setImage(new Image(image));
        droplet.setEnableBackup(false);
        droplet.setEnableIpv6(false);
        droplet.setEnablePrivateNetworking(false);
        droplet = this.apiClient.createDroplet(droplet);
        this.dropletIds.put(uuid,droplet.getId());
    }

    public boolean deleteDroplet(UUID uuid)  {

        try {
            int id =dropletIds.remove(uuid);
            ScheduledTask task = this.tasks.remove(uuid);
            if(task!=null){
                task.cancel();
            }
            Delete result = apiClient.deleteDroplet(id);
            if(result.getIsRequestSuccess()){
                main.getProxy().getLogger().config(String.format("Server %s has been removed successfully",uuid.toString()));
                ProxyServer.getInstance().getServers().remove(this.getName(uuid));
                return true;
            }
            else {
                main.getProxy().getLogger().warning(String.format("Error when deleting the Server %s",uuid.toString()));
                return false;
            }
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            e.printStackTrace();

        }

        return false;

    }

    private String getName(UUID uuid) {
        if(this.serverName.isEmpty() || this.developerUsage){
            return String.valueOf(uuid);
        }
        return this.serverName;
    }

    public DigitalOceanClient getApiClient() {
        return apiClient;
    }

    public void clearAllDroplets() {
        dropletIds.keySet().forEach(this::deleteDroplet);
    }


    public boolean addDropletInBungee(UUID uuid, Consumer<UUID> onSuccess, BiConsumer<UUID, Reason> onFailed) {

        try{

            int id = dropletIds.get(uuid);

            Droplet droplet = getApiClient().getDropletInfo(id);

            Network network =droplet.getNetworks().getVersion4Networks()
                    .stream()
                    .filter(network1 -> network1.getType().equalsIgnoreCase("public"))
                    .findFirst()
                    .orElse(null);

            if(network==null){
                return false;
            }

            InetSocketAddress socketAddress = new InetSocketAddress(network.getIpAddress(), 25565);
            ServerInfo info = ProxyServer
                    .getInstance()
                    .constructServerInfo(this.getName(uuid),
                            socketAddress,
                            this.getName(uuid),
                            false);

            this.tasks.put(uuid,main.getProxy().getScheduler().schedule(main,() ->
                    info.ping((serverPing, throwable) -> {
                if(throwable==null){
                    tasks.remove(uuid).cancel();
                    ProxyServer.getInstance().getServers().put(this.getName(uuid), info);
                    onSuccess.accept(uuid);
                }
            }),0,10, TimeUnit.SECONDS));

            this.main.getProxy().getScheduler()
                    .schedule(this.main,() -> {
                        if(this.tasks.containsKey(uuid)){
                            this.tasks.remove(uuid).cancel();
                            this.main.getProxy().getLogger().warning("Server does not ping, close "+uuid.toString());
                            onFailed.accept(uuid,Reason.BIND_FAILED);
                            deleteDroplet(uuid);
                        }
                    },1000, TimeUnit.SECONDS);

            return true;
        }
        catch (DigitalOceanException | RequestUnsuccessfulException ignored) {
            return false;
        }

    }

    public Map<UUID, Integer> getDropletIds() {
        return this.dropletIds;
    }
}
