package uv.uranvex.uvtrap.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uv.uranvex.uvtrap.UVTrap;
import uv.uranvex.uvtrap.utils.ConfigUtil;
import uv.uranvex.uvtrap.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;

public class TrapGive implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("uvtrap.use")){
            return true;
        }
        if (args[0].equals("give")){
            final Player target = Bukkit.getPlayer(args[1]);
            if (target == null){
                sender.sendMessage(ConfigUtil.getString("messages.player-offline"));
                return true;
            }
            target.getInventory().addItem(trap());

        }

        return true;
    }
    private ItemStack trap(){
        ItemStack trap_item = new ItemStack(Material.valueOf(ConfigUtil.getString("trap.material")), 1);
        ItemMeta trap_meta = trap_item.getItemMeta();
        trap_meta.setDisplayName(ConfigUtil.getString("trap.displayname"));
        List<String> list = new ArrayList<>();
        UVTrap.instance.getConfig().getStringList("trap.lore").forEach(str -> list.add(HexUtil.color(str)));
        trap_meta.setLore(list);


        NamespacedKey namespacedKey = new NamespacedKey(UVTrap.instance, "trap");
        trap_meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, "");

        trap_item.setItemMeta(trap_meta);
        return trap_item;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender.hasPermission("uvtrap.use")) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                completions.add("give");
                return completions;
            }
        }
        return null;
    }
}
