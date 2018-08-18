package com.raphydaphy.betterbeacons;

import com.raphydaphy.betterbeacons.beacon.GuiBetterBeacon;
import com.raphydaphy.betterbeacons.beacon.PacketBetterBeaconConfirm;
import com.raphydaphy.betterbeacons.beacon.TileEntityBetterBeacon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IInteractionObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.rift.listener.MobEffectAdder;
import org.dimdev.rift.listener.PacketAdder;
import org.dimdev.rift.listener.TileEntityTypeAdder;
import org.dimdev.rift.listener.client.GameGuiAdder;
import pl.asie.protocharset.rift.hooks.CharsetAttributes;

public class BetterBeaconsMod implements GameGuiAdder, TileEntityTypeAdder, PacketAdder, MobEffectAdder
{
    public static final String MOD_ID = "betterbeacons";
    private static final Logger log = LogManager.getLogger(MOD_ID);

    public static Potion LONG_REACH_POTION = new ModPotion(false, 0xced343).registerPotionAttributeModifier(CharsetAttributes.BLOCK_REACH_DISTANCE, "5b59b1e9-67b0-4192-9073-3550fb47c269", 1, 0).setBeneficial();
    public static Potion FAST_ATTACK_POTION = new ModPotion(false, 0x936a4c).setBeneficial();

    public static TileEntityType<TileEntityBetterBeacon> BETTER_BEACON_TE;

    @Override
    public void displayGui(EntityPlayerSP player, String id, IInteractionObject interactionObject) { }

    @Override
    public void displayContainerGui(EntityPlayerSP player, String id, IInventory inventory)
    {
        if (id.equals(MOD_ID + ":better_beacon"))
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiBetterBeacon(player.inventory,inventory));
        }
    }

    @Override
    public void registerTileEntityTypes()
    {
        BETTER_BEACON_TE = TileEntityType.registerTileEntityType(MOD_ID + ":better_beacon", TileEntityType.Builder.create(TileEntityBetterBeacon::new));
    }

    @Override
    public void registerHandshakingPackets(PacketRegistrationReceiver receiver) { }

    @Override
    public void registerStatusPackets(PacketRegistrationReceiver receiver) { }

    @Override
    public void registerLoginPackets(PacketRegistrationReceiver receiver) { }

    @Override
    public void registerPlayPackets(PacketRegistrationReceiver receiver)
    {
        receiver.registerPacket(EnumPacketDirection.SERVERBOUND, PacketBetterBeaconConfirm.class);
        receiver.registerPacket(EnumPacketDirection.CLIENTBOUND, PacketLongReachFinished.class);
    }

    @Override
    public void registerMobEffects()
    {
        int nextID = 1;
        for (Potion ignored : Potion.REGISTRY)
        {
            nextID++;
        }

        log.info("[BetterBeacons] Registering potions from ID " + nextID);

        Potion.registerPotion(nextID++, MOD_ID + ":long_reach", (LONG_REACH_POTION));
        Potion.registerPotion(nextID++, MOD_ID + ":fast_attack", (FAST_ATTACK_POTION));
    }

    private static class ModPotion extends Potion
    {
        // Dumb solution to protected constructor in Potion
        ModPotion(boolean isBad, int color)
        {
            super(isBad, color);
        }
    }
}
