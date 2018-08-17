package com.raphydaphy.betterbeacons.core;

import com.raphydaphy.betterbeacons.BetterBeaconsMod;
import com.raphydaphy.betterbeacons.PacketLongReachFinished;
import com.raphydaphy.betterbeacons.beacon.PacketBetterBeaconConfirm;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityLivingBase extends EntityLivingBase
{
    public MixinEntityLivingBase(EntityType<?> type, World world)
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

    private void longReachFinishedPacket(int amplifier)
    {
        ((EntityPlayerMP) (Object) this).connection.sendPacket(new PacketLongReachFinished(amplifier));
    }
}
