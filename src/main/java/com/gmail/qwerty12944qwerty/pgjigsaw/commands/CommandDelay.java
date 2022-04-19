package com.gmail.qwerty12944qwerty.pgjigsaw.commands;

import com.gmail.qwerty12944qwerty.pgjigsaw.main.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CommandDelay {
    public void execute(CommandSender sender, String[] args) {
        Player plr = (Player) sender;
        if (args.length == 0) {
            plr.sendMessage(ChatColor.YELLOW + "The current delay time is: " + ChatColor.BLUE + Main.delayStart + ChatColor.YELLOW + " seconds");
        } else if (args.length > 1) {
            plr.sendMessage(ChatColor.RED + "Too many arguments");
        } else {
            try {
                int seconds = Integer.parseInt(args[0]);

                if (seconds < 3 || seconds > 10) {
                    plr.sendMessage(ChatColor.RED + "Delay must be over 3 seconds and under 10.");
                } else {

                    Main.delayStart = seconds;

                    YamlConfiguration file = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/config.yml"));
                    file.set("delaystart", seconds);
                    file.save("/config.yml");
                    plr.sendMessage(ChatColor.YELLOW + "The delay time is now: " + ChatColor.BLUE + Main.delayStart + ChatColor.YELLOW + " seconds");
                }
            } catch (Exception e) {
                plr.sendMessage(ChatColor.RED + "Error: " + e.toString());
            }
        }
    }
}
