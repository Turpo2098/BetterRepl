package tf.tfischer.betterrepl.listener;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tf.tfischer.betterrepl.BetterRepl;
import tf.tfischer.betterrepl.events.ReplEvent;
import tf.tfischer.betterrepl.events.listener.WhitelistListener;
import tf.tfischer.betterrepl.util.NBTManager;

import java.util.*;

public class ReplUsage implements Listener {
    private final BetterRepl betterRepl;
    boolean townyIsActive;
    boolean worldGuardIsActive;
    WhitelistListener whitelistListener;

    public ReplUsage(BetterRepl betterRepl, Set<Material> whitelist){
        this.betterRepl     = betterRepl;
        townyIsActive       = betterRepl.isTownyActive();
        worldGuardIsActive  = betterRepl.isWorldGuardActive();
        whitelistListener = new WhitelistListener(betterRepl);
    }


    private Set<Material> getWhitelist(){
        return betterRepl.getWhitelist();
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event){
        if(event.getClickedBlock() == null)
            return;
        if(event.getItem() == null)
            return;

        NBTManager  nbtManager      = new NBTManager(betterRepl);
        ItemStack   itemStack       = event.getItem();
        String      nbt             = nbtManager.getSpecificNBTData(itemStack,"BetterRepl");
        if(nbt == null || !nbt.equals("T"))             //check if it is a REPL Tool
            return;

        Player executor = event.getPlayer();

        event.setCancelled(true);

        Map<Player,BlockState>  blockStateMap = betterRepl.getPlayerStateHashMap();

        Block clickedBlock = event.getClickedBlock();

        //Calls the Event
        ReplEvent replEvent = new ReplEvent(clickedBlock.getLocation(),executor,clickedBlock.getType());

        whitelistListener.onRepl(replEvent);
        if(replEvent.isCancelled())
            return;

        Bukkit.getPluginManager().callEvent(replEvent);
        if(replEvent.isCancelled())
            return;

        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){  //Save a Block
            saveBlockState(clickedBlock,executor,blockStateMap);
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { //Load a Block
            BlockState savedBlockState = blockStateMap.get(executor);

            boolean hasBlockStateLoaded = savedBlockState == null;
            if(hasBlockStateLoaded) {
                executor.sendMessage("§2[§aBetterRepl§2]§7 Studiere erst einmal einen Block mit einem Hammer.");
                return;
            }

            boolean     isTheSameBlock     = clickedBlock.getType().equals(savedBlockState.getType());

            if(!isTheSameBlock){

                boolean hasClickedDirt = clickedBlock.getType().equals(Material.DIRT);

                if(hasClickedDirt) {
                    changeDirtBlock(clickedBlock,savedBlockState,executor);
                    return;

                }
                executor.sendMessage("§2[§aBetterRepl§2]§7Das ist nicht derselbe Block. Benutze §e" + savedBlockState.getType().name() + "§7!" );
                return;
            }

            clickedBlock.setBlockData(savedBlockState.getBlockData().clone(),false);
            playUseSound(executor,clickedBlock.getLocation());
            executor.sendMessage("§2[§aBetterRepl§2]§7 Du hast den Block verändert!");
        }
    }

    private void changeDirtBlock(Block clickedBlock, BlockState savedBlockState, Player executor) {
        World world = clickedBlock.getWorld();
        Location location = savedBlockState.getLocation();
        BlockData blockData = Bukkit.getWorld(world.getUID()).getBlockData(location);

        boolean oldBlockHasntChanged = blockData != null && blockData.equals(savedBlockState.getBlockData());
        if (oldBlockHasntChanged) {
            Location oldLocation = savedBlockState.getLocation();
            Material savedMaterial = savedBlockState.getType();

            if (!removeOneItem(executor, savedMaterial)) {
                resetSavedBlock(executor);
                setLocationAir(oldLocation);
            }
            clickedBlock.setBlockData(savedBlockState.getBlockData().clone(), false);
            givePlayerDirt(executor);
            playUseSound(executor, clickedBlock.getLocation());
            executor.sendMessage("§2[§aBetterRepl§2]§7 Du hast den Block verändert!");
        }

    }

    private void saveBlockState(Block block, Player executor, Map<Player,BlockState> blockStateMap){
        BlockState newBlockState = block.getState();

        if(!getWhitelist().contains(newBlockState.getType())){        //Forbid saving Inventory Blocks
            executor.sendMessage("§2[§aBetterRepl§2]§7 Dieser Block ist nicht in der Whitelist.");
            return;
        }

        blockStateMap.put(executor,newBlockState);
        executor.sendMessage("§2[§aBetterRepl§2]§7 Du hast den Block §e" + newBlockState.getType().name() + " §7gespeichert!");
        playSaveSound(executor,executor.getLocation());
    }

    private boolean removeOneItem(Player player, Material material){

        for(ItemStack item : player.getInventory()){
            if(item == null)
                continue;
            if(item.getType().equals(material)){
                item.setAmount(item.getAmount()-1);
                return true;
            }
        }
        return false;
    }

    private void setLocationAir(Location location){
        World       world           = location.getWorld();
        BlockState  oldBlockState   = world.getBlockState(location);
        oldBlockState.setType(Material.AIR);
        world.getBlockAt(location).setBlockData(oldBlockState.getBlockData(),false);
    }

    private void playUseSound(Player player, Location location){
        player.playSound(location, Sound.BLOCK_NETHERITE_BLOCK_PLACE,3f,0.5f);
    }

    private void playSaveSound(Player player, Location location){
        player.playSound(location, Sound.ENTITY_VILLAGER_YES,0.5f,1f);
    }

    private void resetSavedBlock(Player player){
        betterRepl.getPlayerStateHashMap().remove(player);
    }

    private void givePlayerDirt(Player player){
        player.getInventory().addItem(new ItemStack(Material.DIRT));
        player.updateInventory();
    }

}
