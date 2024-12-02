package tf.tfischer.betterrepl;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tf.tfischer.betterrepl.commands.ReplCommand;
import tf.tfischer.betterrepl.events.listener.TownyListener;
import tf.tfischer.betterrepl.events.listener.WhitelistListener;
import tf.tfischer.betterrepl.events.listener.WorldGuardListener;
import tf.tfischer.betterrepl.listener.PlayerDisconnect;
import tf.tfischer.betterrepl.listener.ReplUsage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class BetterRepl extends JavaPlugin {
    private HashMap<Player, BlockState> playerStateHashMap = new HashMap<>();
    private Set<Material> whitelist;

    @Override
    public void onEnable() {
        loadWhitelist();
        // Plugin startup logic
        getCommand("betterrepl").setExecutor(new ReplCommand(this));                     //The creator for BetterRepl
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ReplUsage(this,whitelist),this);                         //The Listener for the ReplTool Event
        pluginManager.registerEvents(new PlayerDisconnect(playerStateHashMap),this);                  //The Listener to remove a player from the hashmap

        //Register Repl Events
        getServer().getPluginManager().registerEvents(new WhitelistListener(this),this);
        if(isTownyActive())
            getServer().getPluginManager().registerEvents(new TownyListener(),this);
        if(isWorldGuardActive())
            getServer().getPluginManager().registerEvents(new WorldGuardListener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public HashMap<Player, BlockState> getPlayerStateHashMap() {
        return playerStateHashMap;
    }


    public boolean isWorldGuardActive(){
        Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");
        return worldGuard != null;
    }

    public boolean isTownyActive(){
        Plugin plugin = getServer().getPluginManager().getPlugin("Towny");
        return plugin != null;
    }

    public Set<Material> getWhitelist() {
        return whitelist;
    }

    public void loadWhitelist(){
        whitelist = new HashSet<>();
        File file = new File("plugins/betterrepl/whitelist.yml");
        if(!file.exists()){
            try {
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                configuration.set("whitelist", getStandardWhitelist());
                configuration.save(file);
                System.out.println("[BetterRepl] Created a config file");
            } catch (IOException e) {
                System.out.println("[BetterRepl] Couldn't create a config file.");
            }
            return;
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        List<String> strings = configuration.getStringList("whitelist");
        System.out.println("[BetterRepl] Loading Whitelist.");
        strings.stream().parallel().forEach(str -> {
            try {
                whitelist.add(Material.valueOf(str));
            } catch (Exception e){
                System.out.println("Couldn't parse " + str + " as Material.");
            }
        });
        System.out.println("[BetterRepl] Finished parsing whitelist successfully.");
    }

    private List<String> getStandardWhitelist(){
        return getConfig().getStringList("standard_whitelist");
    }

}
