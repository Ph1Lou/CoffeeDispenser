package fr.ph1lou.coffee_dispenser.events;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class CloseServerEvent extends Event {

    private final UUID uuid;

    private boolean fail = false;

    public CloseServerEvent(UUID uuid){
        this.uuid=uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean hasFail() {
        return this.fail;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }
}
