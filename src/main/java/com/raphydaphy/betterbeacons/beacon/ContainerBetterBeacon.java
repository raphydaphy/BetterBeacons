package com.raphydaphy.betterbeacons.beacon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerBetterBeacon extends Container
{
    private final InventoryPlayer playerInv;
    public final IInventory tileBeacon;
    private final ContainerBetterBeacon.BeaconSlot oreSlot;
    private final ContainerBetterBeacon.BeaconSlot netherStarSlot;

    public ContainerBetterBeacon(IInventory playerInv, IInventory beaconInv)
    {
        this.playerInv = (InventoryPlayer) playerInv;
        this.tileBeacon = beaconInv;
        this.oreSlot = new ContainerBetterBeacon.BeaconSlot(beaconInv, 0, 98, 110, false);
        this.netherStarSlot = new ContainerBetterBeacon.BeaconSlot(beaconInv, 1, 118, 110, true);
        this.addSlotToContainer(this.oreSlot);
        this.addSlotToContainer(this.netherStarSlot);

        int counter;
        for (counter = 0; counter < 3; ++counter)
        {
            for (int slot = 0; slot < 9; ++slot)
            {
                this.addSlotToContainer(new Slot(playerInv, slot + counter * 9 + 9, 36 + slot * 18, 137 + counter * 18));
            }
        }

        for (counter = 0; counter < 9; ++counter)
        {
            this.addSlotToContainer(new Slot(playerInv, counter, 36 + counter * 18, 195));
        }

    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileBeacon);
    }

    @Override
    public void updateProgressBar(int field, int progress)
    {
        this.tileBeacon.setField(field, progress);
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if (!player.world.isRemote)
        {
            ItemStack oreStack = this.oreSlot.decrStackSize(this.oreSlot.getSlotStackLimit());
            if (!oreStack.isEmpty())
            {
                player.dropItem(oreStack, false);
            }
            ItemStack netherStarStack = this.netherStarSlot.decrStackSize(this.netherStarSlot.getSlotStackLimit());
            if (!netherStarStack.isEmpty())
            {
                player.dropItem(netherStarStack, false);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tileBeacon.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot)
    {
        ItemStack ret = ItemStack.EMPTY;
        Slot realSlot = this.inventorySlots.get(slot);
        if (realSlot != null && realSlot.getHasStack())
        {
            ItemStack stackInSlot = realSlot.getStack();
            ret = stackInSlot.copy();
            if (slot < 2)
            {
                if (!this.mergeItemStack(stackInSlot, 2, 38, true))
                {
                    return ItemStack.EMPTY;
                }

                realSlot.onSlotChange(stackInSlot, ret);
            } else if (!this.oreSlot.getHasStack() && this.oreSlot.isItemValid(stackInSlot) && this.mergeItemStack(stackInSlot, 0, 1, false))
            {
                return ItemStack.EMPTY;
            } else if (!this.netherStarSlot.getHasStack() && this.netherStarSlot.isItemValid(stackInSlot) && this.mergeItemStack(stackInSlot, 1, 2, false))
            {
                return ItemStack.EMPTY;
            } else if (slot >= 2 && slot < 29)
            {
                if (!this.mergeItemStack(stackInSlot, 29, 38, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (slot >= 29 && slot < 38)
            {
                if (!this.mergeItemStack(stackInSlot, 2, 29, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stackInSlot, 2, 38, false))
            {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty())
            {
                realSlot.putStack(ItemStack.EMPTY);
            } else
            {
                realSlot.onSlotChanged();
            }

            if (stackInSlot.getCount() == ret.getCount())
            {
                return ItemStack.EMPTY;
            }

            realSlot.onTake(player, stackInSlot);
        }

        return ret;
    }

    class BeaconSlot extends Slot
    {
        private final boolean isStarSlot;

        BeaconSlot(IInventory inv, int id, int x, int z, boolean isStarSlot)
        {
            super(inv, id, x, z);
            this.isStarSlot = isStarSlot;
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            Item item = stack.getItem();
            return isStarSlot ? item == Items.NETHER_STAR : (item == Items.EMERALD || item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.IRON_INGOT);
        }

        @Override
        public int getSlotStackLimit()
        {
            return 1;
        }
    }
}
