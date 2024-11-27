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

public class ReplCommand implements TabCompleter, CommandExecutor {

    private BetterRepl betterRepl;
    public ReplCommand(BetterRepl betterRepl){
        this.betterRepl = betterRepl;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0){
            sender.sendMessage("§2[§aBetterRepl§2]§7 Please use §6/betterrepl [create|reload]");
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")){
            if(!sender.hasPermission("betterrepl.reload")){
                sendPermissionError(sender);
                return true;
            }

            betterRepl.loadWhitelist();
            int amountOfMaterials = betterRepl.getWhitelist().size();
            sender.sendMessage("§2[§aBetterRepl§2]§7 You reloaded the whitelist and it now has §e" + amountOfMaterials + " §7materials.");
            return true;
        }

        if(args[0].equalsIgnoreCase("create")){
            if(!sender.hasPermission("betterrepl.create")){
                sendPermissionError(sender);
                return true;
            }

            if(!(sender instanceof Player))
                return true;

            Player player = (Player) sender;
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if(mainHand == null || mainHand.getType().equals(Material.AIR)){
                player.sendMessage("§2[§aBetterRepl§2]§7 Du hast kein Item in der Hand.");
                return true;
            }

            NBTManager nbtManager = new NBTManager(betterRepl);
            nbtManager.setSpecificNBTData(mainHand,"BetterRepl","T");
            player.sendMessage("§2[§aBetterRepl§2]§7 Das ist jetzt ein REPL Tool.");
            player.updateInventory();
            return true;
        }
        return true;
    }

    private void sendPermissionError(CommandSender sender){
        sender.sendMessage("§2[§aBetterRepl§2]§7 You don't have the permission to do that.");
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 2)
            return List.of();
        List<String> list = new ArrayList<>();
        if(sender.hasPermission("betterrepl.reload"))
            list.add("reload");
        if(sender.hasPermission("betterrepl.create"))
            list.add("create");

        return list;
    }
}
