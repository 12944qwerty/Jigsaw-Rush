package com.gmail.qwerty12944qwerty.pgjigsaw.commands;

import com.gmail.qwerty12944qwerty.pgjigsaw.main.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CommandAfkTime {
    public void execute(CommandSender sender, String[] args) {
        Player plr = (Player) sender;
        if (args.length == 0) {
            plr.sendMessage(ChatColor.YELLOW + "The current time it takes to go AFK is: " + ChatColor.BLUE + Main.autoAFKTime + ChatColor.YELLOW + " seconds");
        } else if (args.length > 1) {
            plr.sendMessage(ChatColor.RED + "Too many arguments");
        } else {
            try {
                int seconds = Integer.parseInt(args[0]);

                if (seconds < 30) {
                    plr.sendMessage(ChatColor.RED + "Argument must be over 30 seconds.");
                } else {

                    Main.autoAFKTime = seconds;

                    YamlConfiguration file = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/config.yml"));
                    file.set("autoafktime", seconds);
                    file.save("/config.yml");
                    plr.sendMessage(ChatColor.YELLOW + "The time it takes to go AFK is now: " + ChatColor.BLUE + Main.autoAFKTime + ChatColor.YELLOW + " seconds");
                }
            } catch (Exception e) {
                plr.sendMessage(ChatColor.RED + "Error: " + e.toString());
            }
        }
    }
}
