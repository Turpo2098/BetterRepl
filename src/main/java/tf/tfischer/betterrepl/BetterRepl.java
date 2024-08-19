package tf.tfischer.betterrepl;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import tf.tfischer.betterrepl.commands.CreateTool;
import tf.tfischer.betterrepl.listener.PlayerDisconnect;
import tf.tfischer.betterrepl.listener.ReplUsage;
import tf.tfischer.betterrepl.util.NBTManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class BetterRepl extends JavaPlugin {
    private HashMap<Player, BlockState> playerStateHashMap = new HashMap<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        //getCommand("betterrepl").setExecutor(new CreateTool(this));                     //The creator for BetterRepl
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ReplUsage(this),this);                         //The Listener for the ReplTool Event
        pluginManager.registerEvents(new PlayerDisconnect(playerStateHashMap),this);                  //The Listener to remove a player from the hashmap
        registerReplToolRecipe();
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

    private void registerReplToolRecipe() {
        ItemStack item = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Repl Tool");
        List<String> lore = new ArrayList<>();
        lore.add("Benutze dieses Werkzeug um Dinge zu kopieren");
        lore.add("Hammer");
        meta.setLore(lore);
        item.setItemMeta(meta);

        // Set the NBT data
        NBTManager nbtManager = new NBTManager(this);
        item = nbtManager.setSpecificNBTData(item, "BetterRepl", "T");

        NamespacedKey key = new NamespacedKey(this, "hammer");
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("D D", "DSD", " S ");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('S', Material.STICK);

        getServer().addRecipe(recipe);
    }
}
