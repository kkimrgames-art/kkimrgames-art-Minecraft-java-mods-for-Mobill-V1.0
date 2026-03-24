package net.lax1dude.eaglercraft.v1_8.forge;

import net.lax1dude.eaglercraft.v1_8.forge.event.BlockEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ForgeHooks {

	public ForgeHooks() {
	}

	public static boolean onRightClickBlock(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
		BlockEvent.RightClickBlock event = new BlockEvent.RightClickBlock(player, world, pos, state, face);
		ModernEventBus.post(event);
		return event.isCanceled();
	}

}
