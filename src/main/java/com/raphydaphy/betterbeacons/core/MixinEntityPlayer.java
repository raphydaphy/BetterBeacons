package com.raphydaphy.betterbeacons.core;

import com.raphydaphy.betterbeacons.BetterBeaconsMod;
import com.raphydaphy.betterbeacons.PacketLongReachFinished;
import com.raphydaphy.betterbeacons.beacon.PacketBetterBeaconConfirm;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase
{
    public MixinEntityPlayer(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Override
    protected void onNewPotionEffect(PotionEffect effect)
    {
        if (effect.getPotion() == BetterBeaconsMod.LONG_REACH_POTION && this.world.isRemote)
        {
            effect.getPotion().applyAttributesModifiersToEntity(this, this.getAttributeMap(), effect.getAmplifier());
        }

        super.onNewPotionEffect(effect);
    }

    @Override
    protected void onChangedPotionEffect(PotionEffect effect, boolean p_onChangedPotionEffect_2_)
    {
        Potion potion = effect.getPotion();
        if (potion == BetterBeaconsMod.LONG_REACH_POTION && p_onChangedPotionEffect_2_ && this.world.isRemote)
        {
            potion.removeAttributesModifiersFromEntity(this, this.getAttributeMap(), effect.getAmplifier());
            potion.applyAttributesModifiersToEntity(this, this.getAttributeMap(), effect.getAmplifier());
        }

        super.onChangedPotionEffect(effect, p_onChangedPotionEffect_2_);
    }

    @Override
    protected void onFinishedPotionEffect(PotionEffect effect)
    {
        if (effect.getPotion() == BetterBeaconsMod.LONG_REACH_POTION && !this.world.isRemote)
        {
            longReachFinishedPacket(effect.getAmplifier());
        }

        super.onFinishedPotionEffect(effect);
    }

    @Inject(method = "getCooldownPeriod", at = @At("HEAD"), cancellable = true)
    private void getCooldownPeriod(CallbackInfoReturnable<Float> info)
    {
        for (PotionEffect effect : this.getActivePotionEffects())
        {
            if (effect.getPotion() == BetterBeaconsMod.FAST_ATTACK_POTION)
            {
                info.setReturnValue((float)(1.0D / (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() + ((effect.getAmplifier() + 1) * 0.5f)) * 20.0D));
                break;
            }
        }
    }

    private void longReachFinishedPacket(int amplifier)
    {
        ((EntityPlayerMP) (Object) this).connection.sendPacket(new PacketLongReachFinished(amplifier));
    }
}
