package com.raphydaphy.betterbeacons.core;

import com.raphydaphy.betterbeacons.BetterBeaconUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer
{
    @Shadow
    public EntityPlayerMP player;
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    private Vec3d targetPos;

    @Inject(method = "processPlayerDigging", at = @At("HEAD"), cancellable = true)
    private void processPlayerDigging(CPacketPlayerDigging packet, CallbackInfo info)
    {
        if (BetterBeaconUtils.shouldIncreaseReach(this.player.world, this.player))
        {
            PacketThreadUtil.checkThreadAndEnqueue(packet, (NetHandlerPlayServer) (Object) this, this.player.getServerWorld());
            WorldServer worldServer = this.server.getWorld(this.player.dimension);
            BlockPos pos = packet.getPosition();
            this.player.markPlayerActive();
            if (packet.getAction().ordinal() == 7)
            {
                double lvt_4_2_ = this.player.posX - ((double) pos.getX() + 0.5D);
                double lvt_6_1_ = this.player.posY - ((double) pos.getY() + 0.5D) + 1.5D;
                double lvt_8_1_ = this.player.posZ - ((double) pos.getZ() + 0.5D);
                double lvt_10_1_ = lvt_4_2_ * lvt_4_2_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_;
                if (lvt_10_1_ <= ((BetterBeaconUtils.QOL_REACH + 1) * (BetterBeaconUtils.QOL_REACH + 1)) && pos.getY() < this.server.getBuildLimit())
                {
                    if (packet.getAction() == net.minecraft.network.play.client.CPacketPlayerDigging.Action.START_DESTROY_BLOCK)
                    {
                        if (!this.server.isBlockProtected(worldServer, pos, this.player) && worldServer.getWorldBorder().contains(pos))
                        {
                            this.player.interactionManager.onBlockClicked(pos, packet.getFacing());
                        } else
                        {
                            this.player.connection.sendPacket(new SPacketBlockChange(worldServer, pos));
                        }
                    } else
                    {
                        if (packet.getAction() == net.minecraft.network.play.client.CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
                        {
                            this.player.interactionManager.blockRemoving(pos);
                        } else if (packet.getAction() == net.minecraft.network.play.client.CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK)
                        {
                            this.player.interactionManager.cancelDestroyingBlock();
                        }

                        if (!worldServer.getBlockState(pos).isAir())
                        {
                            this.player.connection.sendPacket(new SPacketBlockChange(worldServer, pos));
                        }
                    }
                }


                info.cancel();
            }
        }
    }

    @Inject(method = "processTryUseItemOnBlock", at = @At("HEAD"), cancellable = true)
    private void processTryUseItemOnBlock(CPacketPlayerTryUseItemOnBlock packet, CallbackInfo info)
    {
        if (BetterBeaconUtils.shouldIncreaseReach(player.world, player))
        {
            PacketThreadUtil.checkThreadAndEnqueue(packet, ((NetHandlerPlayServer) (Object) this), this.player.getServerWorld());
            WorldServer lvt_2_1_ = this.server.getWorld(this.player.dimension);
            EnumHand lvt_3_1_ = packet.getHand();
            ItemStack lvt_4_1_ = this.player.getHeldItem(lvt_3_1_);
            BlockPos lvt_5_1_ = packet.getPos();
            EnumFacing lvt_6_1_ = packet.getDirection();
            this.player.markPlayerActive();
            if (lvt_5_1_.getY() < this.server.getBuildLimit() - 1 && (lvt_6_1_ != EnumFacing.UP && lvt_5_1_.getY() < this.server.getBuildLimit()) &&
                    targetPos == null && lvt_2_1_.getWorldBorder().contains(lvt_5_1_) && !this.server.isBlockProtected(lvt_2_1_, lvt_5_1_, this.player))
            {
                if (this.player.getDistanceSq((double) lvt_5_1_.getX() + 0.5D, (double) lvt_5_1_.getY() + 0.5D, (double) lvt_5_1_.getZ() + 0.5D) < ((BetterBeaconUtils.QOL_REACH + 3) * (BetterBeaconUtils.QOL_REACH + 3)))
                {
                    this.player.interactionManager.processRightClickBlock(this.player, lvt_2_1_, lvt_4_1_, lvt_3_1_, lvt_5_1_, lvt_6_1_, packet.getFacingX(), packet.getFacingY(), packet.getFacingZ());
                }

                info.cancel();
            }

            if (info.isCancelled())
            {
                this.player.connection.sendPacket(new SPacketBlockChange(lvt_2_1_, lvt_5_1_));
                this.player.connection.sendPacket(new SPacketBlockChange(lvt_2_1_, lvt_5_1_.offset(lvt_6_1_)));
            }
        }
    }
}
