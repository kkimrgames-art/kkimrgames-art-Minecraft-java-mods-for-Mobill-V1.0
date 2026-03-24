package net.lax1dude.eaglercraft.v1_8.forge.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockEvent extends Event {
	public final World world;
	public final BlockPos pos;
	public final IBlockState state;

	public BlockEvent(World world, BlockPos pos, IBlockState state) {
		this.world = world;
		this.pos = pos;
		this.state = state;
	}

	public static class RightClickBlock extends BlockEvent {
		public final EntityPlayer player;
		public final EnumFacing face;

		public RightClickBlock(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
			super(world, pos, state);
			this.player = player;
			this.face = face;
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}
}
