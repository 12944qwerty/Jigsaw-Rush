package com.gmail.qwerty12944qwerty.pgjigsaw.commands;

import com.gmail.qwerty12944qwerty.pgjigsaw.core.Core;
import com.gmail.qwerty12944qwerty.pgjigsaw.main.Main;
import com.gmail.qwerty12944qwerty.pgjigsaw.nms.*;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAfk {
    public void execute(CommandSender sender) {
        Player plr = (Player) sender;
        if (plr.getGameMode() == GameMode.SPECTATOR) {
            plr.setGameMode(GameMode.ADVENTURE);
            Main.AFK.remove(plr);
            plr.teleport(Main.worldSpawn);
            plr.sendMessage(ChatColor.GRAY + "You are no longer AFK");
            NMSChat.send(plr, "", NMSChat.MessageType.ACTION_BAR);
        } else {
            plr.setGameMode(GameMode.SPECTATOR);
            plr.sendMessage(ChatColor.GRAY + "You are AFK");
            Main.AFK.add(plr);
            NMSChat.send(plr, ChatColor.BLUE + "You are currently AFK", NMSChat.MessageType.ACTION_BAR);
        }
    }
}
