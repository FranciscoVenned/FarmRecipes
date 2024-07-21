package com.venned.farmrecipes.events;

import org.bukkit.CropState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Crops;

import java.util.Objects;

public class IdentifyItem implements Listener {

    @EventHandler
    public void getItem(PlayerInteractEvent event){
        Block clickedBlock = event.getClickedBlock();

        if(event.getClickedBlock() != null) {
            BlockState blockState = clickedBlock.getState();
            if (blockState.getBlockData() instanceof Ageable) {
                org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) blockState.getBlockData();
                int age = ageable.getAge();
                int maxAge = ageable.getMaximumAge();
                event.getPlayer().sendMessage("Crop State: " + age + " / " + maxAge);

            }
            String Material = Objects.requireNonNull(event.getClickedBlock()).getType().toString();
            event.getPlayer().sendMessage("material " + Material);
            String Data = event.getClickedBlock().getBlockData().toString();
            event.getPlayer().sendMessage("Data " + Data);
        }
        if(event.getPlayer().getMainHand() != null){
            String Material = event.getPlayer().getItemInHand().getType().toString();
            event.getPlayer().sendMessage("material hand " + Material);
        }
    }

}
