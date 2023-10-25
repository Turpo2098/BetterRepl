package tf.tfischer.betterrepl.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import tf.tfischer.betterrepl.BetterRepl;

import java.util.HashMap;

public class NBTManager {

    BetterRepl main;

    public NBTManager(BetterRepl javaPlugin){
        this.main = javaPlugin;
    }

    public String getSpecificNBTData(@NotNull ItemStack itemStack, @NotNull String key) {

        NamespacedKey namespacedKey = new NamespacedKey(main, key);
        ItemMeta meta = itemStack.getItemMeta();

        if (!meta.getPersistentDataContainer().isEmpty()) {
            String nbt = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
            return nbt;
        }

        else
            return null;

    }

    public ItemStack setSpecificNBTData(@NotNull ItemStack itemStack, @NotNull String key, @NotNull String value) {

        NamespacedKey   namespacedKey   = new NamespacedKey(main, key);
        ItemMeta        itemMeta        = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);

        itemStack.setItemMeta(itemMeta);


        return itemStack;
    }

    public ItemStack setMultipleSpecificNBTData(@NotNull ItemStack itemStack, @NotNull HashMap<String, String> keyAndValue){

        ItemMeta itemMeta = itemStack.getItemMeta();

        for (String i : keyAndValue.keySet()) {
            NamespacedKey namespacedKey = new NamespacedKey(main, i);
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, keyAndValue.get(i));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}