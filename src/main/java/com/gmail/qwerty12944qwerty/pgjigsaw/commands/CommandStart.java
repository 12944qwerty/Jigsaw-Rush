package com.gmail.qwerty12944qwerty.pgjigsaw.commands;

import com.gmail.qwerty12944qwerty.pgjigsaw.core.Core;
import com.gmail.qwerty12944qwerty.pgjigsaw.main.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandStart {

    public void execute(CommandSender sender) {
        if (!Main.gameRunning) {
            if (Main.AFK.containsAll(Bukkit.getOnlinePlayers()) && Bukkit.getOnlinePlayers().containsAll(Main.AFK))
                sender.sendMessage(ChatColor.RED + "There are no active players to play!");
            else 
                Core.start();
        } else
            sender.sendMessage("§cYou must end the previous game before starting a new one!");
    }

}
