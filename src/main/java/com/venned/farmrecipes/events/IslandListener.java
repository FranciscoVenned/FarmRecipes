package com.venned.farmrecipes.events;

import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.venned.farmrecipes.FarmRecipes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class IslandListener implements Listener
{
    private final FarmRecipes plugin;

    public IslandListener(final FarmRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIslandCreate(final IslandCreateEvent event) {
        final UUID islandUUID = event.getIsland().getUniqueId();
        this.plugin.getFarmingUtil().getFarmingCropMap().forEach((material, farmingCrop) -> this.plugin.getDataFile().set(farmingCrop.getDataPath(islandUUID), (Object)0));
        this.plugin.saveDataFile();
    }

    @EventHandler
    public void onIslandDelete(final IslandDisbandEvent event) {
        final UUID islandUUID = event.getIsland().getUniqueId();
        this.plugin.getDataFile().set(islandUUID.toString(), (Object)null);
        this.plugin.saveDataFile();
    }
}