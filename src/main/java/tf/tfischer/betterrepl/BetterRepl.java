package tf.tfischer.betterrepl;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tf.tfischer.betterrepl.commands.CreateTool;
import tf.tfischer.betterrepl.listener.PlayerDisconnect;
import tf.tfischer.betterrepl.listener.ReplUsage;

import java.util.HashMap;

public final class BetterRepl extends JavaPlugin {
    private HashMap<Player, BlockState> playerStateHashMap = new HashMap<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("betterrepl").setExecutor(new CreateTool(this));                     //The creator for BetterRepl
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ReplUsage(this),this);                         //The Listener for the ReplTool Event
        pluginManager.registerEvents(new PlayerDisconnect(this),this);                  //The Listener to remove a player from the hashmap
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
}
