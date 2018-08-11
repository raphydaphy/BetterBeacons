package com.raphydaphy.betterbeacons;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import org.dimdev.rift.listener.BlockAdder;

public class BetterBeacons implements BlockAdder
{
    private static final Block BETTER_BEACON = new BlockBetterBeacon(Block.Builder.create(Material.ROCK));

    @Override
    public void registerBlocks()
    {
        Block.registerBlock(new ResourceLocation("beacon"), BETTER_BEACON);
    }
}
