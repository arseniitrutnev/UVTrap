package uv.uranvex.uvtrap.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import uv.uranvex.uvtrap.UVTrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class SchematicUtils {
    public final int timeUndo =  UVTrap.instance.getConfig().getInt("trap.duration") * 20;

    public void spawnShematic(Location location, String fileName) {
        File file = new File(UVTrap.instance.getDataFolder() + "/schem/" + fileName);
        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
            Clipboard clipboard = reader.read();
            BlockVector3 cord = BlockVector3.at(location.getX(), location.getY(), location.getZ());

            EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()));
            Operation operation = (new ClipboardHolder(clipboard)).createPaste(editSession)
                    .to(cord).ignoreAirBlocks(false).build();
            Operations.complete(operation);

            editSession.close();

            BukkitRunnable timer = new BukkitRunnable() {
                @Override
                public void run() {
                    editSession.undo(editSession);
                }
            };
            timer.runTaskLater(UVTrap.instance, timeUndo);

        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
    }
}
