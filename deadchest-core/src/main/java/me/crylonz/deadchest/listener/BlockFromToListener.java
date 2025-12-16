package me.crylonz.deadchest.listener;

import me.crylonz.deadchest.ChestData;
import me.crylonz.deadchest.DeadChestLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.List;

import static me.crylonz.deadchest.utils.Utils.isGraveBlock;

public class BlockFromToListener implements Listener {

    /**
     * Disable water destruction of Deadchest (for head)
     **/
    @EventHandler
    public void onBlockFromToEvent(BlockFromToEvent e) {
        if (isGraveBlock(e.getToBlock().getType())) {
            final List<ChestData> chestDataList = DeadChestLoader.getChestDataList();
            for (ChestData cd : chestDataList) {
                if (cd.getChestLocation().equals(e.getToBlock().getLocation())) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
}
