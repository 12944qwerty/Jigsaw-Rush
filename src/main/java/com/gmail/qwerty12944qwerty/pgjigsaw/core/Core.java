package com.gmail.qwerty12944qwerty.pgjigsaw.core;

import com.gmail.qwerty12944qwerty.pgjigsaw.main.Main;
import com.gmail.qwerty12944qwerty.pgjigsaw.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;

public class Core {

    public static Instant boardBegin;
    public static Instant boardEnd;
    public static Instant gameBegin;
    public static Instant gameEnd;

    public static int currentScore = 0;
    public static List<Player> playersDone = new ArrayList<>();
    public static List<Player> playersPlaying = new ArrayList<>();

    public static Material[] order;

    public static void start() {
        Main.gameRunning = true;
        List<Location> spawnsc = new ArrayList<Location>(Main.spawns); // Copy
        playersPlaying.clear();
        playersPlaying.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawnsc.remove(Util.random(0, spawnsc.size())));
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            playersPlaying.add(player);
        }
        Main.world.setDifficulty(Difficulty.PEACEFUL);
        Utils.cleanBoard();
        Utils.countdown();

        new BukkitRunnable() {
            @Override
            public void run() {
                gameBegin = Instant.now();
                Utils.generateBoard();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Utils.giveItems(player);
                }
                Main.maxScore = Bukkit.getOnlinePlayers().size();
            }
        }.runTaskLater(Main.getPlugin(Main.class), 60L);
    }

    public static void end() {
        Main.gameRunning = false;
        gameEnd = Instant.now();
        Utils.cleanBoard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§aGame completed "+currentScore+" board in: §b"+Utils.ROUND.format(((float) Duration.between(Core.gameBegin, Core.gameEnd).toMillis() / 1000.0f))+"s");
            p.getInventory().clear();
            p.teleport(Main.worldSpawn);
        }
    }
}
