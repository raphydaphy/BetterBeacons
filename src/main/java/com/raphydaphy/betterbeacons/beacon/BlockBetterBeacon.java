package com.raphydaphy.betterbeacons.beacon;

import net.minecraft.block.BlockBeacon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBetterBeacon extends BlockBeacon
{
    public BlockBetterBeacon(Builder builder)
    {
        super(builder);
    }

    @Override
    public TileEntity getTileEntity(IBlockReader reader)
    {
        return new TileEntityBetterBeacon();
    }

    @Override
    public boolean onRightClick(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand p_onRightClick_5_, EnumFacing p_onRightClick_6_, float p_onRightClick_7_, float p_onRightClick_8_, float p_onRightClick_9_)
    {
        if (world.isRemote)
        {
            return true;
        } else
        {
            TileEntity te = world.getTileEntity(pos);
            System.out.println(te);
            if (te instanceof TileEntityBetterBeacon)
            {
                player.displayGUIChest((IInventory)te);
                player.addStat(StatList.BEACON_INTERACTION);
            }

            return true;
        }
    }
}
