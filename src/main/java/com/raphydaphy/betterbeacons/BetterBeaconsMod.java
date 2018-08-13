package com.raphydaphy.betterbeacons;

import com.raphydaphy.betterbeacons.beacon.BlockBetterBeacon;
import com.raphydaphy.betterbeacons.beacon.GuiBetterBeacon;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import org.dimdev.rift.listener.BlockAdder;
import org.dimdev.rift.listener.client.GameGuiAdder;

public class BetterBeaconsMod implements BlockAdder, GameGuiAdder
{
    private static final Block BETTER_BEACON = new BlockBetterBeacon(Block.Builder.create(Material.ROCK));

    @Override
    public void registerBlocks()
    {
        Block.registerBlock(new ResourceLocation("beacon"), BETTER_BEACON);
    }

    @Override
    public void displayGui(EntityPlayerSP player, String id, IInteractionObject interactionObject) { }

    @Override
    public void displayContainerGui(EntityPlayerSP player, String id, IInventory inventory)
    {
        if (id.equals("betterbeacons:better_beacon"))
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiBetterBeacon(player.inventory, inventory));
        }
    }
}
