package com.gmail.qwerty12944qwerty.pgjigsaw.commands;

import com.gmail.qwerty12944qwerty.pgjigsaw.core.Core;
import com.gmail.qwerty12944qwerty.pgjigsaw.main.Main;
import org.bukkit.command.CommandSender;

public class CommandEnd {

    public void execute(CommandSender sender) {
        if (Main.gameRunning)
            Core.end();
        else
            sender.sendMessage("§cThere is no game running right now!");
    }

}
