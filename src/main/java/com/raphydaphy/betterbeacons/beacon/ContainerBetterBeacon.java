package com.raphydaphy.betterbeacons.beacon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerBetterBeacon extends Container
{
    public final IInventory tileBeacon;
    private final ContainerBetterBeacon.BeaconSlot oreSlot;
    private final ContainerBetterBeacon.BeaconSlot netherStarSlot;

    public ContainerBetterBeacon(IInventory playerInv, IInventory beaconInv)
    {
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

    public void addListener(IContainerListener p_addListener_1_)
    {
        super.addListener(p_addListener_1_);
        p_addListener_1_.sendAllWindowProperties(this, this.tileBeacon);
    }

    public void updateProgressBar(int p_updateProgressBar_1_, int p_updateProgressBar_2_)
    {
        this.tileBeacon.setField(p_updateProgressBar_1_, p_updateProgressBar_2_);
    }

    public IInventory getTileEntity()
    {
        return this.tileBeacon;
    }

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

    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tileBeacon.isUsableByPlayer(player);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int slot)
    {
        ItemStack lvt_3_1_ = ItemStack.EMPTY;
        Slot lvt_4_1_ = (Slot) this.inventorySlots.get(slot);
        if (lvt_4_1_ != null && lvt_4_1_.getHasStack())
        {
            ItemStack lvt_5_1_ = lvt_4_1_.getStack();
            lvt_3_1_ = lvt_5_1_.copy();
            if (slot == 0 || slot == 1)
            {
                System.out.println("boom");
                if (!this.mergeItemStack(lvt_5_1_, 1, 37, true))
                {
                    return ItemStack.EMPTY;
                }

                lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
            } else if (!this.oreSlot.getHasStack() && this.oreSlot.isItemValid(lvt_5_1_) && lvt_5_1_.getCount() == 1)
            {
                if (!this.mergeItemStack(lvt_5_1_, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.netherStarSlot.getHasStack() && this.netherStarSlot.isItemValid(lvt_5_1_) && lvt_5_1_.getCount() == 1)
            {
                if (!this.mergeItemStack(lvt_5_1_, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (slot < 28)
            {
                if (!this.mergeItemStack(lvt_5_1_, 28, 37, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (slot < 37)
            {
                if (!this.mergeItemStack(lvt_5_1_, 1, 28, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(lvt_5_1_, 1, 37, false))
            {
                return ItemStack.EMPTY;
            }

            if (lvt_5_1_.isEmpty())
            {
                lvt_4_1_.putStack(ItemStack.EMPTY);
            } else
            {
                lvt_4_1_.onSlotChanged();
            }

            if (lvt_5_1_.getCount() == lvt_3_1_.getCount())
            {
                return ItemStack.EMPTY;
            }

            lvt_4_1_.onTake(player, lvt_5_1_);
        }

        return lvt_3_1_;
    }

    class BeaconSlot extends Slot
    {
        private final boolean isStarSlot;
        BeaconSlot(IInventory inv, int id, int x, int z, boolean isStarSlot)
        {
            super(inv, id, x, z);
            this.isStarSlot = isStarSlot;
        }

        public boolean isItemValid(ItemStack stack)
        {
            Item item = stack.getItem();
            return isStarSlot ? item == Items.NETHER_STAR : (item == Items.EMERALD || item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.IRON_INGOT);
        }

        public int getSlotStackLimit()
        {
            return 1;
        }
    }
}
