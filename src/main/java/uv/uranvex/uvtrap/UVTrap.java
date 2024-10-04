package uv.uranvex.uvtrap;

import org.bukkit.plugin.java.JavaPlugin;
import uv.uranvex.uvtrap.commands.TrapGive;
import uv.uranvex.uvtrap.listeners.TrapSpawnEvent;

public final class UVTrap extends JavaPlugin {
    public static UVTrap instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("UVTrap is enabled");
        getServer().getPluginManager().registerEvents(new TrapSpawnEvent(), this);
        getCommand("trap").setExecutor(new TrapGive());
        getCommand("trap").setTabCompleter(new TrapGive());
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("UVTrap is disabled");
    }


}
