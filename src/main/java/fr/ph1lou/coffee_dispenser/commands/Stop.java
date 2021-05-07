package fr.ph1lou.coffee_dispenser.commands;

import fr.ph1lou.coffee_dispenser.Main;
import fr.ph1lou.coffee_dispenser.events.CloseServerEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Stop extends Command {

    private final Main main;

    public Stop(Main main) {
        super("server-stop");
        this.main=main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(main.getCurrentGame().isPresent()){
            CloseServerEvent event = new CloseServerEvent(main.getCurrentGame().get());
            main.getProxy().getPluginManager().callEvent(event);

            if(event.hasFail()){
                sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f Erreur lors de la suppression du serveur"));
            }
            else{
                sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f Le serveur a bien été fermé"));
            }

            main.setCurrentGame(null);
        }
        else {
            sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f  Aucun serveur n'est lancé actuellement"));
        }

    }
}