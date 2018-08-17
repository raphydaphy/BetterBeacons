package com.raphydaphy.betterbeacons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class PacketLongReachFinished implements Packet<INetHandlerPlayClient>
{
    private int amplifier;

    public PacketLongReachFinished() { }

    public PacketLongReachFinished(int amplifier)
    {
        this.amplifier = amplifier;
    }

    @Override
    public void readPacketData(PacketBuffer buf)
    {
        amplifier = buf.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf)
    {
        buf.writeInt(amplifier);
    }

    @Override
    public void processPacket(INetHandlerPlayClient iNetHandlerPlayClient)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        BetterBeaconsMod.LONG_REACH_POTION.removeAttributesModifiersFromEntity(player, player.getAttributeMap(), amplifier);
    }
}
