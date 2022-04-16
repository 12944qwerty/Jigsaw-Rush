package com.gmail.qwerty12944qwerty.pgjigsaw.utils;

import com.gmail.qwerty12944qwerty.pgjigsaw.core.Core;
import com.gmail.qwerty12944qwerty.pgjigsaw.main.Main;
import nms.NMSTitle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;


public class Utils {

    public static final DecimalFormat ROUND = new DecimalFormat("0.00");

    public static void giveItems(Player player) {
        List<Material> tmp = Arrays.asList(Main.MATERIALS.clone());

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, new ItemStack(tmp.get(i), 1));
        }
    }

    public static void generateBoard() {
        List<Material> tmp = Arrays.asList(Main.MATERIALS.clone());
        Collections.shuffle(tmp);
        Core.order = tmp.toArray(Main.MATERIALS.clone());

        int i = 0;
        int materialIndex = 0;
        for (Location l : Main.BOARD) {
            Main.world.getBlockAt(l).setType(Core.order[materialIndex]);
            i++;

            if (i >= 18) {
                i = 0;
                materialIndex++;
            }
        }
        Core.boardBegin = Instant.now();
    }

    public static void cleanBoard() {
        for (Location l : Main.BOARD) {
            Main.world.getBlockAt(l).setType(Material.WOOL);
        }
        for (ArrayList<Location> board : Main.PLAYER_BOARDS) {
            for (Location l : board) {
                Main.world.getBlockAt(l).setType(Material.SNOW_BLOCK);
            }
        }
    }

    public static int getBoardIndex(Location block) {
        int index = 0;

        for (ArrayList<Location> i: Main.PLAYER_BOARDS) {
            if (i.contains(block)) {
                return (index);
            }
            index++;
        }

        return -1;
    }

    public static int boardIndex(Location block) {
        int index = 0;
        for (Location l : Main.PLAYER_BOARDS.get(getBoardIndex(block))) {
            if (block.getBlockX() == l.getBlockX() &&
                    block.getBlockY() == l.getBlockY() &&
                    block.getBlockZ() == l.getBlockZ()) {
                break;
            }
            index++;
        }
        return (index);
    }

    public static boolean correctBoard(Location block) {
        int i = 0;
        if (getBoardIndex(block) < 0) {
            return (false);
        }
        for (Location l : Main.PLAYER_BOARDS.get(getBoardIndex(block))) {
            if (!Main.world.getBlockAt(l).getType().equals(Core.order[i++])) {
                return (false);
            }
        }
        return (true);
    }

    public static boolean isFromPlayerBoard(Location block) {
        boolean fromBoard = false;

        for (ArrayList<Location> board : Main.PLAYER_BOARDS) {
            for (Location l : board) {
                if (block.getBlockX() == l.getBlockX() &&
                    block.getBlockY() == l.getBlockY() &&
                    block.getBlockZ() == l.getBlockZ()) {
                    fromBoard = true;
                }
            }
        }

        return (fromBoard);
    }

    public static void countdown() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            NMSTitle.send(p.getPlayer(), "§e3", "", 0, 30, 0);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    NMSTitle.send(p.getPlayer(), "§62", "", 0, 30, 0);
                }
            }
        }.runTaskLater(Main.getPlugin(Main.class), 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    NMSTitle.send(p.getPlayer(), "§c1", "", 0, 20, 0);
                }
            }
        }.runTaskLater(Main.getPlugin(Main.class), 40L);
    }

	public static int randInt(int low, int high){
      return (int)(Math.random()*high)+low;
    }
}
