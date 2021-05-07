package fr.ph1lou.coffee_dispenser;

import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import fr.ph1lou.coffee_dispenser.enums.Reason;
import fr.ph1lou.coffee_dispenser.events.CloseServerEvent;
import fr.ph1lou.coffee_dispenser.events.LaunchServerEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeListener implements Listener {

    private final Main main;
    private final Map<UUID, ScheduledTask> tasks = new HashMap<>();

    BungeeListener(Main main){
        this.main=main;
    }

    @EventHandler
    public void onServerLaunch(LaunchServerEvent event){

        UUID uuid = event.getUUID();

        try {
            DigitalOceanManager digitalOceanManager = this.main.getDigitalOceanManager();

            if(digitalOceanManager.getDropletIds().containsKey(event.getUUID())) return;

            digitalOceanManager.createDroplet(main,
                    event.getRegion(),
                    event.getImage(),
                    event.getScriptName(),
                    event.getRam(),
                    event.getUUID(),
                    event.getValues());

            this.tasks.put(uuid,main.getProxy().getScheduler()
                    .schedule(this.main,() -> {
                        if(this.main.getDigitalOceanManager()
                                .addDropletInBungee(uuid,event.getOnSuccess(),event.getOnFailed())){
                            this.tasks.remove(uuid).cancel();
                        }
                    },0,10, TimeUnit.SECONDS));

            this.main.getProxy().getScheduler()
                    .schedule(this.main,() -> {
                        if(this.tasks.containsKey(uuid)){
                            this.tasks.remove(uuid).cancel();
                            this.main.getProxy().getLogger().warning("Ip not recovered Close "+uuid);
                            event.getOnFailed().accept(uuid, Reason.PING_FAILED);
                            digitalOceanManager.deleteDroplet(uuid);
                        }
                    },1000, TimeUnit.SECONDS);


        } catch (RequestUnsuccessfulException | DigitalOceanException e) {
            e.printStackTrace();
            event.getOnFailed().accept(uuid, Reason.DIGITAL_OCEAN);
            event.setFail(true);
        }
    }

    @EventHandler
    public void onServerStop(CloseServerEvent event){
        event.setFail(!this.main
                .getDigitalOceanManager()
                .deleteDroplet(event.getUUID()));
    }


}
