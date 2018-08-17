package com.raphydaphy.betterbeacons.core;

import com.raphydaphy.betterbeacons.BetterBeaconUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
            if (lvt_5_1_.getY() >= this.server.getBuildLimit() - 1 && (lvt_6_1_ == EnumFacing.UP || lvt_5_1_.getY() >= this.server.getBuildLimit()))
            {
                ITextComponent lvt_7_1_ = (new TextComponentTranslation("build.tooHigh", this.server.getBuildLimit())).applyTextStyle(TextFormatting.RED);
                this.player.connection.sendPacket(new SPacketChat(lvt_7_1_, ChatType.GAME_INFO));
            } else if (this.targetPos == null && this.player.getDistanceSq((double) lvt_5_1_.getX() + 0.5D, (double) lvt_5_1_.getY() + 0.5D, (double) lvt_5_1_.getZ() + 0.5D) < ((BetterBeaconUtils.QOL_REACH + 3) * (BetterBeaconUtils.QOL_REACH + 3)) && !this.server.isBlockProtected(lvt_2_1_, lvt_5_1_, this.player) && lvt_2_1_.getWorldBorder().contains(lvt_5_1_))
            {
                this.player.interactionManager.processRightClickBlock(this.player, lvt_2_1_, lvt_4_1_, lvt_3_1_, lvt_5_1_, lvt_6_1_, packet.getFacingX(), packet.getFacingY(), packet.getFacingZ());
            }

            this.player.connection.sendPacket(new SPacketBlockChange(lvt_2_1_, lvt_5_1_));
            this.player.connection.sendPacket(new SPacketBlockChange(lvt_2_1_, lvt_5_1_.offset(lvt_6_1_)));
            info.cancel();
        }
    }
}
