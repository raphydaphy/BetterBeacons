package com.raphydaphy.betterbeacons.core;

import com.google.common.collect.Lists;
import com.raphydaphy.betterbeacons.BetterBeaconsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityLockable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TileEntityBeacon.class)
public abstract class MixinTileEntityBeacon extends TileEntityLockable
{
    @Shadow private ItemStack payment;

    @Shadow protected List<TileEntityBeacon.BeamSegment> beamSegments;

    @Shadow protected int levels;

    /**
     * @author raphydaphy
     * @reason I need this to change the TileEntityType from the vanilla beacon's to my custom one
     */
    @Overwrite
    public MixinTileEntityBeacon()
    {
        super(BetterBeaconsMod.BETTER_BEACON_TE);
        this.payment = ItemStack.EMPTY;
        this.beamSegments = Lists.newArrayList();
        this.levels = -1;
    }
}
