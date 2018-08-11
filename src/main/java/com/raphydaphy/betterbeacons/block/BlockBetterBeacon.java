package com.raphydaphy.betterbeacons.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBetterBeacon extends BlockBeacon
{
    public BlockBetterBeacon(Builder builder)
    {
        super(builder);
    }

    @Override
    public boolean onRightClick(IBlockState p_onRightClick_1_, World p_onRightClick_2_, BlockPos p_onRightClick_3_, EntityPlayer p_onRightClick_4_, EnumHand p_onRightClick_5_, EnumFacing p_onRightClick_6_, float p_onRightClick_7_, float p_onRightClick_8_, float p_onRightClick_9_) {
        if (p_onRightClick_2_.isRemote) {
            return true;
        } else {
            TileEntity lvt_10_1_ = p_onRightClick_2_.getTileEntity(p_onRightClick_3_);
            if (lvt_10_1_ instanceof TileEntityBeacon) {
                //p_onRightClick_4_.displayGUIChest((TileEntityBeacon)lvt_10_1_);
                System.out.println("hello you just got destroyed");
                p_onRightClick_4_.addStat(StatList.BEACON_INTERACTION);
            }

            return true;
        }
    }
}
