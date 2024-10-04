package uv.uranvex.uvtrap.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import uv.uranvex.uvtrap.UVTrap;
import uv.uranvex.uvtrap.utils.ConfigUtil;
import uv.uranvex.uvtrap.utils.SchematicUtils;

import java.util.*;

public class TrapSpawnEvent implements Listener {

    private final NamespacedKey namespacedKey = new NamespacedKey(UVTrap.instance, "trap");

    private Map<UUID, Long> delayOnItem = new HashMap<>();

    public static RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    public static ProtectedCuboidRegion trapRegion;

    public static void createRegion(int x, int z, World worldBukkit) {
        int radius = 4;
        if (container != null) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(worldBukkit));

            BlockVector3 minPoint = BlockVector3.at(x - radius, 0, z - radius);
            BlockVector3 maxPoint = BlockVector3.at(x + radius, 255, z + radius);

            String nameRegion = String.valueOf(UUID.randomUUID());
            trapRegion = new ProtectedCuboidRegion(nameRegion, minPoint, maxPoint);

            trapRegion.setFlag(Flags.USE, StateFlag.State.ALLOW);
            trapRegion.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
            trapRegion.setFlag(Flags.USE, StateFlag.State.DENY);
            trapRegion.setFlag(Flags.BUILD, StateFlag.State.DENY);
            trapRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
            trapRegion.setFlag(Flags.PLACE_VEHICLE, StateFlag.State.DENY);
            trapRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);

            regionManager.addRegion(trapRegion);
            BukkitRunnable timer = new BukkitRunnable() {
                @Override
                public void run() {
                    deleteRegion(worldBukkit);
                }
            };

            timer.runTaskLater(UVTrap.instance, UVTrap.instance.getConfig().getInt("trap.duration") * 20);
        }
    }

    public static void deleteRegion(World worldBukkit) {
        if (container != null) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(worldBukkit));

            regionManager.removeRegion(trapRegion.getId());
        }
    }

    @EventHandler
    private void on(PlayerInteractEvent event) {

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
            if (!event.getPlayer().getWorld().getName().contains(ConfigUtil.getString("trap.exclude-world"))) {
                if (delayOnItem.containsKey(event.getPlayer().getUniqueId()) && !event.getPlayer().isOp()){
                    long secondsLeft = UVTrap.instance.getConfig().getInt("trap.cooldown") - (System.currentTimeMillis() - delayOnItem.get(event.getPlayer().getUniqueId())) / 1000;
                    if (secondsLeft > 0) {
                        event.getPlayer().sendMessage(ConfigUtil.getString("messages.cooldown").replace("{secondsLeft}", String.valueOf(secondsLeft)));
                        event.setCancelled(true);
                        return;
                    }
                }
            }
                event.setCancelled(true);
                delayOnItem.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                new SchematicUtils().spawnShematic(event.getPlayer().getLocation(), "trap.schem");
                createRegion(event.getPlayer().getLocation().getBlockX(), event.getPlayer().getLocation().getBlockZ(), event.getPlayer().getWorld());
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(ConfigUtil.getString("trap.sound")), SoundCategory.MASTER, 1.0F, 1.0F);

                int amount = event.getPlayer().getInventory().getItemInMainHand().getAmount();
                event.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
            }
        }
    }