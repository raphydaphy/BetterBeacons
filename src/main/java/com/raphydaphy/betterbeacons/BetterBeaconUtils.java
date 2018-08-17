package com.raphydaphy.betterbeacons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.IWorld;

public class BetterBeaconUtils
{
    public static final int QOL_REACH = 10;

    public static boolean shouldIncreaseReach(IWorld world, EntityPlayer player)
    {
        for (PotionEffect potion : player.getActivePotionEffects())
        {
            if (potion.getPotion() == MobEffects.GLOWING)
            {
                return true;
            }
        }
        return false;
    }
}
