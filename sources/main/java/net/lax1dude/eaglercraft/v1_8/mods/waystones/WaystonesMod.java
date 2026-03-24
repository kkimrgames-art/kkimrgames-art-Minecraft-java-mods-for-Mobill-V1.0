package net.lax1dude.eaglercraft.v1_8.mods.waystones;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.forge.ModernRegistry;
import net.lax1dude.eaglercraft.v1_8.forge.ModernEventBus;
import net.lax1dude.eaglercraft.v1_8.forge.event.BlockEvent;
import net.minecraft.util.ChatComponentText;

public class WaystonesMod {
    public static Block waystone;

    public static void init() {
        System.out.println("Initializing Waystones Mod Logic (Dormant until assets are downloaded)");

        waystone = new BlockWaystone(Material.rock);

        ModernRegistry.registerBlock(new ResourceLocation("waystones", "waystone"), waystone);
        ModernRegistry.createAndRegisterItemBlock(new ResourceLocation("waystones", "waystone"), waystone);

        ModernEventBus.addListener(BlockEvent.RightClickBlock.class, event -> {
            if (event.state.getBlock() == waystone) {
                if (net.lax1dude.eaglercraft.v1_8.forge.ForgeBridge.isModEnabled("waystones")) {
                    event.player.addChatMessage(new ChatComponentText("§a[Waystones] You activated a waystone!"));
                } else {
                    event.player.addChatMessage(new ChatComponentText("§c[Waystones] Asset pack not downloaded from Mod Store! Waystone is physically dormant."));
                }
            }
        });
    }
}
