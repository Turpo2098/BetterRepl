package tf.tfischer.betterrepl;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tf.tfischer.betterrepl.commands.ReplCommand;
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

    public void loadWhitelist(){
        whitelist = new HashSet<>();
        File file = new File("plugins/betterrepl/whitelist.yml");
        if(!file.exists()){
            try {
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                configuration.set("whitelist",List.of("AIR"));
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

}
