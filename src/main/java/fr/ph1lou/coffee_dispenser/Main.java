package fr.ph1lou.coffee_dispenser;

import fr.ph1lou.coffee_dispenser.commands.Start;
import fr.ph1lou.coffee_dispenser.commands.Stop;
import fr.ph1lou.coffee_dispenser.utils.FilesUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class Main extends Plugin {

    private DigitalOceanManager digitalOcean;
    private UUID currentGame;

    @Override
    public void onEnable() {

        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new Start(this));
        pluginManager.registerCommand(this, new Stop(this));
        pluginManager.registerListener(this, new BungeeListener(this));

        try {
            if (!getDataFolder().exists()){
                getDataFolder().mkdir();
            }
            FilesUtils.generate(this,"config.yml");
            FilesUtils.generate(this, "userdata.sh");
            this.digitalOcean = new DigitalOceanManager(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.digitalOcean.clearAllDroplets();
    }

    public DigitalOceanManager getDigitalOceanManager() {
        return this.digitalOcean;
    }

    public Optional<UUID> getCurrentGame(){
        return Optional.ofNullable(this.currentGame);
    }

    public void setCurrentGame(UUID uuid){
        this.currentGame=uuid;
    }
}
