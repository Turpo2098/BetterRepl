package tf.tfischer.betterrepl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tf.tfischer.betterrepl.BetterRepl;

public class PlayerDisconnect implements Listener {

    BetterRepl betterRepl;

    public PlayerDisconnect(BetterRepl betterRepl){
        this.betterRepl = betterRepl;
    }

    public PlayerDisconnect(tf.tfischer.betterrepl.BetterRepl betterRepl) {

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(player == null)
            return;
        betterRepl.getPlayerStateHashMap().remove(player);
    }

}
