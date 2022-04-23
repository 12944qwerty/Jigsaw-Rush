package com.gmail.qwerty12944qwerty.pgjigsaw.main;

import com.gmail.qwerty12944qwerty.pgjigsaw.commands.CommandEnd;
import com.gmail.qwerty12944qwerty.pgjigsaw.commands.CommandStart;
import com.gmail.qwerty12944qwerty.pgjigsaw.commands.CommandAfk;
import com.gmail.qwerty12944qwerty.pgjigsaw.commands.CommandAfkTime;
import com.gmail.qwerty12944qwerty.pgjigsaw.commands.CommandDelay;
import com.gmail.qwerty12944qwerty.pgjigsaw.core.Core;
import com.gmail.qwerty12944qwerty.pgjigsaw.nms.NMSChat;
import com.gmail.qwerty12944qwerty.pgjigsaw.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Main  extends JavaPlugin implements Listener {
    public static boolean gameRunning = false;

    public static List<Location> spawns = new ArrayList<>();
    public static Location worldSpawn;

    public static final List<Location> BOARD = new ArrayList<>();
    public static final ArrayList<ArrayList<Location>> PLAYER_BOARDS = new ArrayList<ArrayList<Location>>();

    public static List<Player> AFK = new ArrayList<>();
    public static Hashtable<Player, Instant> checkAFK = new Hashtable<>();

    public static int autoAFKTime;
    public static int delayStart;

    public static final Material[] MATERIALS = new Material[] {Material.DIRT, Material.STONE, Material.COBBLESTONE, Material.LOG, Material.WOOD, Material.BRICK, Material.GOLD_BLOCK, Material.NETHERRACK, Material.ENDER_STONE};

    public static World world;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        world = Bukkit.getWorlds().get(0);

        worldSpawn = new Location(world, 263.5f, 2, 1816, 0, 0);

        YamlConfiguration file = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/board.yml"));
        Set<String> respawns = file.getConfigurationSection("loc").getKeys(false);
        for (final String i : respawns) {
            final double x = file.getDouble("loc."+i+".x");
            final double y = file.getDouble("loc."+i+".y");
            final double z = file.getDouble("loc."+i+".z");
            BOARD.add(new Location(world, x, y, z));
        }

        file = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/player_board.yml"));
        respawns = file.getKeys(false);
        for (final String place : respawns) {
            Set<String> jigsaw = file.getConfigurationSection(place).getKeys(false);
            ArrayList<Location> temp = new ArrayList<>();
            String mystr = place.replaceAll("[^\\d]", "");
            for (final String i : jigsaw) {
                final double x = file.getDouble(place+"."+i+".x");
                final double y = file.getDouble(place+"."+i+".y");
                final double z = file.getDouble(place+"."+i+".z");
                if (i.equals("spawn")) {
                    spawns.add(new Location(world, x, y, z, (float) file.getDouble(place+"."+i+".yaw"), 0));
                    continue;
                }
                temp.add(new Location(world, x, y, z));
            }
            PLAYER_BOARDS.add(temp);
        }

        file = YamlConfiguration.loadConfiguration(getClass().getResourceAsStream("/config.yml"));
        autoAFKTime = file.getInt("autoafktime");
        delayStart = file.getInt("delaystart");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : AFK) {
                    NMSChat.send(p, ChatColor.BLUE + "You are currently AFK", NMSChat.MessageType.ACTION_BAR);
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : checkAFK.keySet()) {
                    if (AFK.contains(p)) continue;

                    Instant now = Instant.now();
                    if (((float) Duration.between(checkAFK.get(p), now).toMillis() / 1000.0f) > autoAFKTime) { // Two Minutes
                        AFK.add(p);
                        p.setGameMode(GameMode.SPECTATOR);
                        p.sendMessage(ChatColor.GRAY + "You are AFK");
                        NMSChat.send(p, ChatColor.BLUE + "You are currently AFK", NMSChat.MessageType.ACTION_BAR);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        checkAFK.replace(event.getPlayer(), Instant.now());
        if (Core.playersDone.contains(event.getPlayer())) return;
        if (event.getClickedBlock() == null) return;
        if (event.getPlayer().getItemInHand() == null) return;
        if (Core.playersDone.contains(event.getPlayer())) return;
        final Block clickedBlock = event.getClickedBlock();

        if (Utils.isFromPlayerBoard(clickedBlock.getLocation()) && !event.getPlayer().getItemInHand().getType().equals(clickedBlock.getType())) {
            if (!event.getPlayer().getItemInHand().getType().equals(Material.AIR))
                clickedBlock.setType(event.getPlayer().getItemInHand().getType());
            if (clickedBlock.getType().equals(Core.order[Utils.boardIndex(clickedBlock.getLocation())])) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
                if (Utils.correctBoard(clickedBlock.getLocation())) {
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                    for (Player plr : Bukkit.getOnlinePlayers()) {
                        plr.sendMessage("§a" + (plr.equals(event.getPlayer()) ? "You" : event.getPlayer().getName()) + " completed this board in: §b"+Utils.ROUND.format(((float) Duration.between(Core.boardBegin, Instant.now()).toMillis() / 1000.0f))+"s");
                    }
                    Core.playersDone.add(event.getPlayer());
                    if (Core.playersDone.containsAll(Core.playersPlaying) && Core.playersPlaying.containsAll(Core.playersDone)) {
                        Core.currentScore++;
                        Core.end();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AFK.remove(event.getPlayer());
        Player plr = event.getPlayer();
        plr.teleport(worldSpawn);
        plr.setGameMode(GameMode.ADVENTURE);
        plr.getInventory().clear(); 
        checkAFK.put(event.getPlayer(), Instant.now());
    }

    @EventHandler
    public void onGamemodeSwitch(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.SPECTATOR) return;
        if (Core.playersDone.contains(event.getPlayer())) return;
        Core.playersPlaying.remove(event.getPlayer());

        if (Core.playersPlaying.size() == 0) {
            Core.end();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AFK.remove(event.getPlayer());
        if (Core.playersDone.contains(event.getPlayer())) return;
        Core.playersPlaying.remove(event.getPlayer());
        checkAFK.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        AFK.remove(event.getPlayer());
        if (Core.playersDone.contains(event.getPlayer())) return;
        Core.playersPlaying.remove(event.getPlayer());
        checkAFK.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        checkAFK.replace(event.getPlayer(), Instant.now());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(gameRunning);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(gameRunning);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(gameRunning);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(gameRunning);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(gameRunning);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(gameRunning);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "start": new CommandStart().execute(sender); break;
            case "end": new CommandEnd().execute(sender); break;
            case "afk": new CommandAfk().execute(sender); break;
            case "afktime": new CommandAfkTime().execute(sender, args); break;
            case "delay": new CommandDelay().execute(sender, args); break;
            default: return (true);
        }
        return (true);
    }

}
