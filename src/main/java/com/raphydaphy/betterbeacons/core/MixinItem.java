package com.raphydaphy.betterbeacons.core;

import com.raphydaphy.betterbeacons.BetterBeaconUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem
{
    @Inject(method = "rayTrace", at = @At("HEAD"), cancellable = true)
    private void rayTrace(World world, EntityPlayer player, boolean dontcare, CallbackInfoReturnable<RayTraceResult> info)
    {
        if (BetterBeaconUtils.shouldIncreaseReach(world, player))
        {
            float lvt_4_1_ = player.rotationPitch;
            float lvt_5_1_ = player.rotationYaw;
            double lvt_6_1_ = player.posX;
            double lvt_8_1_ = player.posY + (double) player.getEyeHeight();
            double lvt_10_1_ = player.posZ;
            Vec3d lvt_12_1_ = new Vec3d(lvt_6_1_, lvt_8_1_, lvt_10_1_);
            float lvt_13_1_ = MathHelper.cos(-lvt_5_1_ * 0.017453292F - 3.1415927F);
            float lvt_14_1_ = MathHelper.sin(-lvt_5_1_ * 0.017453292F - 3.1415927F);
            float lvt_15_1_ = -MathHelper.cos(-lvt_4_1_ * 0.017453292F);
            float lvt_16_1_ = MathHelper.sin(-lvt_4_1_ * 0.017453292F);
            float lvt_17_1_ = lvt_14_1_ * lvt_15_1_;
            float lvt_19_1_ = lvt_13_1_ * lvt_15_1_;
            double REACH_DIST = 10.0D;
            Vec3d lvt_22_1_ = lvt_12_1_.add((double) lvt_17_1_ * REACH_DIST, (double) lvt_16_1_ * REACH_DIST, (double) lvt_19_1_ * REACH_DIST);
            info.setReturnValue(world.rayTraceBlocks(lvt_12_1_, lvt_22_1_, dontcare ? RayTraceFluidMode.SOURCE_ONLY : RayTraceFluidMode.NEVER, false, false));
        }
    }
}
