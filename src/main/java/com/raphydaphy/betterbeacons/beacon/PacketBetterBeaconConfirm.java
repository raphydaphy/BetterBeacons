package com.raphydaphy.betterbeacons.beacon;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class PacketBetterBeaconConfirm implements Packet<INetHandlerPlayServer>
{
    private int index;

    public PacketBetterBeaconConfirm()
    {
    }

    public PacketBetterBeaconConfirm(int index)
    {
        this.index = index;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer)
    {
        index = packetBuffer.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer)
    {
        packetBuffer.writeInt(index);
    }

    @Override
    public void processPacket(INetHandlerPlayServer connection)
    {
        NetHandlerPlayServer server = (NetHandlerPlayServer) connection;
        if (server.player.openContainer instanceof ContainerBetterBeacon)
        {
            ContainerBetterBeacon container = (ContainerBetterBeacon) server.player.openContainer;
            container.tileBeacon.setField(index + 5, 1);
            container.tileBeacon.clear();
        }
    }
}
