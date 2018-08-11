package main.java.com.raphydaphy.betterbeacons;

import main.java.com.raphydaphy.betterbeacons.block.BlockBetterBeacon;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import org.dimdev.rift.listener.BlockAdder;
import org.dimdev.rift.listener.ItemAdder;

public class BetterBeacons implements BlockAdder, ItemAdder
{
    private static final Block BETTER_BEACON = new BlockBetterBeacon(Block.Builder.create(Blocks.BEACON));

    @Override
    public void registerBlocks()
    {
        Block.registerBlock(new ResourceLocation("beacon"), BETTER_BEACON);
    }

    @Override
    public void registerItems()
    {
        Item.registerItemBlock(BETTER_BEACON, ItemGroup.BUILDING_BLOCKS);
    }
}
