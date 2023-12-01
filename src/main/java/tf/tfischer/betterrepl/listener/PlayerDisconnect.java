package tf.tfischer.betterrepl.listener;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PlayerDisconnect implements Listener {

    Map<Player, BlockState> playerBlockStateMap;

    public PlayerDisconnect(Map<Player,BlockState> blockStateMap){
        this.playerBlockStateMap = blockStateMap;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(player == null)
            return;
        playerBlockStateMap.remove(player);
    }

}
