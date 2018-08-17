package com.raphydaphy.betterbeacons.core;

import com.raphydaphy.betterbeacons.BetterBeaconUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP
{
    @Shadow @Final private Minecraft mc;
    @Shadow private GameType currentGameType;

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    private void getBlockReachDistance(CallbackInfoReturnable<Float> info)
    {
        if (BetterBeaconUtils.shouldIncreaseReach(mc.world, mc.player))
        {
            System.out.println("d i e ");
            info.setReturnValue(currentGameType.isCreative() ? BetterBeaconUtils.QOL_REACH : BetterBeaconUtils.QOL_REACH - 0.5f);
        }
    }
}
