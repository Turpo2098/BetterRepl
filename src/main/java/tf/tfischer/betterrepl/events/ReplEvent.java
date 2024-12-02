package tf.tfischer.betterrepl.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ReplEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    boolean canceled = false;
    Location location;
    Player player;
    Material clickedBlock;

    public ReplEvent(Location location, Player player, Material material) {
        this.location = location;
        this.player = player;
        this.clickedBlock = material;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    public Material getClickedBlock() {
        return clickedBlock;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
