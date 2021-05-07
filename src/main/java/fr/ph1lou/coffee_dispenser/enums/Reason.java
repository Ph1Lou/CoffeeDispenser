package fr.ph1lou.coffee_dispenser.enums;

public enum Reason {
    PING_FAILED, //the server cannot ping (it is probably the bungee ip address which is false)
    BIND_FAILED,    //the minecraft server cannot be bind to bungee
    DIGITAL_OCEAN //the plugin cannot join digital ocean
}
