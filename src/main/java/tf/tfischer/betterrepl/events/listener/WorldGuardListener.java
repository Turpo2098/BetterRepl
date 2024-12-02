package tf.tfischer.betterrepl.events.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tf.tfischer.betterrepl.events.ReplEvent;

import java.util.Objects;

public class WorldGuardListener implements Listener {

    @EventHandler
    public void onRepl(ReplEvent event){
        //Yoinked out of https://www.spigotmc.org/threads/worldguard-7-0-0-check-if-player-can-build.356669/
        Location location = event.getLocation();
        Player player = event.getPlayer();

        boolean canBuild = true;

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
        if (!WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(WorldGuardPlugin.inst().wrapPlayer(player), world)) {
            canBuild = query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
        }
        if(canBuild)
            return;

        event.setCancelled(true);
        player.sendMessage("§2[§aBetterRepl§2]§7 WorldGuard verbietet dir das!");
    }
}
