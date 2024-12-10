package tf.tfischer.betterrepl.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tf.tfischer.betterrepl.BetterRepl;
import tf.tfischer.betterrepl.util.NBTManager;

import java.util.ArrayList;
import java.util.List;

public class CreateTool implements TabCompleter, CommandExecutor {

    private BetterRepl betterRepl;
    public CreateTool(BetterRepl betterRepl){
        this.betterRepl = betterRepl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player))
            return true;

        if(args.length == 1 && args[0].equals("reload")){
            betterRepl.loadWhitelist();
            sender.sendMessage("You reloaded the whitelist");
            return true;
        }

        Player player = (Player) sender;
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if(mainHand == null || mainHand.getType().equals(Material.AIR)){
            player.sendMessage("§aDu hast kein Item in der Hand!");
            return true;
        }

        NBTManager nbtManager = new NBTManager(betterRepl);
        nbtManager.setSpecificNBTData(mainHand,"BetterRepl","T");
        player.sendMessage("§aDas ist jetzt ein REPL Tool");
        player.updateInventory();
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
