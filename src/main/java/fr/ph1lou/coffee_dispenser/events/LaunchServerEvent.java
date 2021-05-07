
package fr.ph1lou.coffee_dispenser.events;

import fr.ph1lou.coffee_dispenser.enums.Reason;
import net.md_5.bungee.api.plugin.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LaunchServerEvent extends Event {

    private final String region;
    private final String image;
    private final String scriptName;
    private final int ram;
    private final UUID uuid;
    private final Consumer<UUID> onSuccess;
    private final BiConsumer<UUID, Reason> onFailed;
    private final Map<String,String> values = new HashMap<>();
    private boolean fail = false;

    public LaunchServerEvent(UUID uuid){
        this("FRA1",
                "debian-10-x64",
                "userdata.sh",
                3000,
                uuid,
                (id) -> {},
                (id,error) -> {});
    }

    public LaunchServerEvent(String region,
                             String image,
                             String scriptName,
                             int ram,
                             UUID uuid,
                             Consumer<UUID> onSuccess,
                             BiConsumer<UUID, Reason> onFailed){
        this.region=region;
        this.image=image;
        this.scriptName=scriptName;
        this.ram=ram;
        this.uuid=uuid;
        this.onSuccess=onSuccess;
        this.onFailed=onFailed;
    }

    public String getRegion() {
        return this.region;
    }

    public String getImage() {
        return this.image;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public int getRam() {
        return this.ram;
    }

    public Consumer<UUID> getOnSuccess() {
        return this.onSuccess;
    }

    public BiConsumer<UUID, Reason> getOnFailed() {
        return this.onFailed;
    }

    public Map<String, String> getValues() {
        return this.values;
    }

    public boolean hasFail() {
        return this.fail;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }
}

