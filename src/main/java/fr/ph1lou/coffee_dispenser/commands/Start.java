package fr.ph1lou.coffee_dispenser.commands;

import fr.ph1lou.coffee_dispenser.Main;
import fr.ph1lou.coffee_dispenser.events.CloseServerEvent;
import fr.ph1lou.coffee_dispenser.events.LaunchServerEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Start extends Command {

    private final Main main;

    public Start(Main main) {
        super("server-start");
        this.main=main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(this.main.getCurrentGame().isPresent()){
            sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f Un serveur est déjà lancé, pour le stopper §b/server-stop"));
            return;
        }

        UUID game = UUID .randomUUID();

        main.setCurrentGame(game);

        LaunchServerEvent event = new LaunchServerEvent(
                game,
                (u) -> sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f Le serveur est disponible")),
                (u,e) -> sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f Le démarrage du serveur a échoué")));

        main.getProxy().getPluginManager().callEvent(event);

        if(event.hasFail()){
            sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f Le serveur n'a pas pu être lancé. Regarde les logs de ton Bungee"));

        }
        else{
            sender.sendMessage(new TextComponent("§6Ph1Lou §b»§f Un serveur vient d'etre lancé, pour le stopper §b/server-stop"));
        }

        main.getProxy().getScheduler().schedule(main,() -> {
            if(main.getCurrentGame().isPresent()){
                if(main.getCurrentGame().get().equals(game)){
                    main.getProxy().getPluginManager().callEvent(new CloseServerEvent(UUID.fromString(args[0])));
                }
            }
        },235, TimeUnit.MINUTES);
    }
}