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

        int lvt_5_2_;
        for (lvt_5_2_ = 0; lvt_5_2_ < 3; ++lvt_5_2_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_)
            {
                this.addSlotToContainer(new Slot(playerInv, lvt_6_1_ + lvt_5_2_ * 9 + 9, 36 + lvt_6_1_ * 18, 137 + lvt_5_2_ * 18));
            }
        }

        for (lvt_5_2_ = 0; lvt_5_2_ < 9; ++lvt_5_2_)
        {
            this.addSlotToContainer(new Slot(playerInv, lvt_5_2_, 36 + lvt_5_2_ * 18, 195));
        }

    }

    @Override
    public void addListener(IContainerListener p_addListener_1_)
    {
        super.addListener(p_addListener_1_);
        p_addListener_1_.sendAllWindowProperties(this, this.tileBeacon);
    }

    @Override
    public void updateProgressBar(int p_updateProgressBar_1_, int p_updateProgressBar_2_)
    {
        this.tileBeacon.setField(p_updateProgressBar_1_, p_updateProgressBar_2_);
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
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0 || index == 1)
            {
                if (!this.mergeItemStack(itemstack1, 2, 38, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (this.mergeItemStack(itemstack1, 0, 2, false))
            {
                return ItemStack.EMPTY;
            }
            else if (index < 29)
            {
                if (!this.mergeItemStack(itemstack1, 29, 38, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index < 38)
            {
                if (!this.mergeItemStack(itemstack1, 2, 29, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 2, 38, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
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
