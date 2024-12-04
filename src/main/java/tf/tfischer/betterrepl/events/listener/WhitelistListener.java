package tf.tfischer.betterrepl.events.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tf.tfischer.betterrepl.BetterRepl;
import tf.tfischer.betterrepl.events.ReplEvent;

public class WhitelistListener implements Listener {
    BetterRepl plugin;

    public WhitelistListener(BetterRepl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRepl(ReplEvent event){
        Material material = event.getClickedBlock();
        if(plugin.getWhitelist().contains(material))
            return;
        event.setCancelled(true);
        event.getPlayer().sendMessage("§2[§aBetterRepl§2]§7 Der Block ist nicht in der Whitelist.");
    }

}
