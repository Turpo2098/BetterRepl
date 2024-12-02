package tf.tfischer.betterrepl.events.listener;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tf.tfischer.betterrepl.events.ReplEvent;

public class TownyListener implements Listener {


    @EventHandler
    public void onRepl(ReplEvent event){
        //Yoinked out of https://github.com/TownyAdvanced/Towny/wiki/TownyAPI#checking-if-a-player-can-builddestroy-somewhere

        boolean canBuild = PlayerCacheUtil.getCachePermission(event.getPlayer(),
                event.getLocation(),
                event.getClickedBlock(),
                TownyPermission.ActionType.BUILD);
        if(canBuild)
            return;
        event.setCancelled(true);
        event.getPlayer().sendMessage("§2[§aBetterRepl§2]§7 Du kannst nicht wegen Towny bauen!");
    }

}
