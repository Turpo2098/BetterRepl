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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tf.tfischer.betterrepl.BetterRepl;
import tf.tfischer.betterrepl.util.NBTManager;

import java.util.Map;

public class ReplUsage implements Listener {
    private final BetterRepl betterRepl;
    boolean townyIsActive;
    boolean worldGuardIsActive;

    public ReplUsage(BetterRepl betterRepl){
        this.betterRepl     = betterRepl;
        townyIsActive       = betterRepl.isTownyActive();
        worldGuardIsActive  = betterRepl.isWorldGuardActive();
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

        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){  //Save a Block
            BlockState newBlockState = event.getClickedBlock().getState();  //Ignore warning because if-clause

            if(isForbidden(event.getClickedBlock().getState())){        //Forbid saving Inventory Blocks
                executor.sendMessage("§aDu darfst diesen nicht Block verwenden!");
                return;
            }

            blockStateMap.put(executor,newBlockState);
            executor.sendMessage("§aDu hast den Block §6" + newBlockState.getType().name() + " §agespeichert!");
            playSaveSound(executor,executor.getLocation());

            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { //Load a Block
            BlockState savedBlockState = blockStateMap.get(executor);

            boolean hasBlockStateLoaded = savedBlockState == null;
            if(hasBlockStateLoaded) {
                executor.sendMessage("§aStudiere erst einmal einen Block mit deinem Hammer!");
                return;
            }


            Block clickedBlock = event.getClickedBlock();
            if(!executor.hasPermission("betterrepl.bypass")) {   //Bypasspermission

                boolean townyAllowsBuilding = townyIsActive && !canBuildInTowny(executor, event.getClickedBlock()); //Ignore warning becuase of null
                if (townyAllowsBuilding) {
                    executor.sendMessage("§cDu kannst nicht wegen Towny bauen!");
                    return;
                }

                boolean worldGuardAllowsBuilding = worldGuardIsActive && !canBuildInWorldGuard(executor);
                if (worldGuardAllowsBuilding) {
                    executor.sendMessage("§cWorldGuard verbietet dir das!");
                    return;
                }

            }

            boolean     isTheSameBlock     = clickedBlock.getType().equals(savedBlockState.getType());

            if(!isTheSameBlock){

                boolean hasClickedDirt = clickedBlock.getType().equals(Material.DIRT);

                if(hasClickedDirt) {
                    World       world       = clickedBlock.getWorld();
                    Location    location    = savedBlockState.getLocation();
                    BlockData blockData = Bukkit.getWorld(world.getUID()).getBlockData(location);

                    boolean oldBlockHasntChanged = blockData != null && blockData.equals(savedBlockState.getBlockData());
                    if(oldBlockHasntChanged){
                        Location    oldLocation         = savedBlockState.getLocation();
                        Material    savedMaterial       = savedBlockState.getType();
                        Inventory   executorsInventory  = executor.getInventory();


                        boolean hasItemInInventory = executorsInventory.contains(savedMaterial);
                        if(hasItemInInventory){
                            removeOneItem(executor,savedMaterial);
                        } else {
                            resetSavedBlock(executor);
                            setLocationAir(oldLocation);
                        }
                        clickedBlock.setBlockData(savedBlockState.getBlockData().clone(),false);
                        givePlayerDirt(executor);
                        playUseSound(executor,clickedBlock.getLocation());
                        executor.sendMessage("§aDas den Block verändert!");
                        return;
                    }
                    executor.sendMessage("§aNichts klonen!!!!");
                    return;

                }
                executor.sendMessage("§aDas ist nicht derselbe Block! Benutze §6" + savedBlockState.getType().name() + "§a!" );
                return;
            }

            clickedBlock.setBlockData(savedBlockState.getBlockData().clone(),false);
            playUseSound(executor,clickedBlock.getLocation());
            executor.sendMessage("§aDas den Block verändert!");
        }
    }

    private void removeOneItem(Player player, Material material){
        if(player.getGameMode().equals(GameMode.CREATIVE))
            return;

        for(ItemStack item : player.getInventory().getContents()){
            if(item == null)
                continue;
            if(item.getType().equals(material)){
                item.setAmount(item.getAmount()-1);
                break;
            }
        }
        player.updateInventory();
    }

    private void setLocationAir(Location location){
        World       world           = location.getWorld();
        BlockState  oldBlockState   = world.getBlockState(location);
        oldBlockState.setType(Material.AIR);
        world.setBlockData(location,oldBlockState.getBlockData());
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

    private boolean isForbidden(BlockState blockState){
        return blockState instanceof Container;
    }

    private boolean canBuildInWorldGuard(Player player){
        //Yoinked out of https://www.spigotmc.org/threads/worldguard-7-0-0-check-if-player-can-build.356669/

        boolean result = true;

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation());
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(player.getWorld());
        if (!WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(WorldGuardPlugin.inst().wrapPlayer(player), world)) {
            result = query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
        }
        return result;
    }

    private boolean canBuildInTowny(Player player, Block block){
        //Yoinked out of https://github.com/TownyAdvanced/Towny/wiki/TownyAPI#checking-if-a-player-can-builddestroy-somewhere
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.BUILD);
    }
}
