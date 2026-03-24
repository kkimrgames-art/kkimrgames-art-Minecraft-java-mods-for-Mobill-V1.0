package net.lax1dude.eaglercraft.v1_8.mods.waystones;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.lax1dude.eaglercraft.v1_8.forge.ForgeHooks;

public class BlockWaystone extends Block {
    public BlockWaystone(Material materialIn) {
        super(materialIn);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setUnlocalizedName("waystone");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        ForgeHooks.onRightClickBlock(playerIn, worldIn, pos, state, side);
        return true;
    }
}
