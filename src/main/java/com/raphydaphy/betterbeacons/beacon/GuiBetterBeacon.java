package com.raphydaphy.betterbeacons.beacon;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;

public class GuiBetterBeacon extends GuiContainer
{
    private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("betterbeacons:textures/gui/better_beacon.png");

    private final IInventory tileBeacon;

    public GuiBetterBeacon(InventoryPlayer p_i45507_1_, IInventory p_i45507_2_) {
        super(new ContainerBeacon(p_i45507_1_, p_i45507_2_));
        this.tileBeacon = p_i45507_2_;
        this.xSize = 230;
        this.ySize = 219;
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_) {
        RenderHelper.disableStandardItemLighting();
        Iterator var3 = this.buttonList.iterator();

        while(var3.hasNext()) {
            GuiButton lvt_4_1_ = (GuiButton)var3.next();
            if (lvt_4_1_.isMouseOver()) {
                lvt_4_1_.drawButtonForegroundLayer(p_drawGuiContainerForegroundLayer_1_ - this.guiLeft, p_drawGuiContainerForegroundLayer_2_ - this.guiTop);
                break;
            }
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_drawGuiContainerBackgroundLayer_1_, int p_drawGuiContainerBackgroundLayer_2_, int p_drawGuiContainerBackgroundLayer_3_) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
        this.itemRender.zLevel = 100.0F;
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.EMERALD), lvt_4_1_ + 42, lvt_5_1_ + 109);
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.DIAMOND), lvt_4_1_ + 42 + 22, lvt_5_1_ + 109);
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLD_INGOT), lvt_4_1_ + 42 + 44, lvt_5_1_ + 109);
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_INGOT), lvt_4_1_ + 42 + 66, lvt_5_1_ + 109);
        this.itemRender.zLevel = 0.0F;
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        this.drawDefaultBackground();
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        this.renderHoveredToolTip(p_drawScreen_1_, p_drawScreen_2_);
    }
}
