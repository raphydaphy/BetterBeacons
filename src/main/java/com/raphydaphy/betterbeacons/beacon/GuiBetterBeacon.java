package com.raphydaphy.betterbeacons.beacon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.ResourceLocation;

public class GuiBetterBeacon extends GuiContainer
{
    private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("betterbeacons:textures/better_beacon.png");

    private final IInventory te;
    private GuiBetterBeacon.ConfirmButton beaconConfirmButton;
    private boolean buttonsNotDrawn;

    public GuiBetterBeacon(InventoryPlayer playerInv, IInventory inv)
    {
        super(new ContainerBeacon(playerInv, inv));
        this.te = inv;
        this.xSize = 230;
        this.ySize = 219;
    }

    @Override
    protected void initGui() {
        super.initGui();
        this.beaconConfirmButton = new GuiBetterBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107);
        this.addButton(this.beaconConfirmButton);
        this.buttonsNotDrawn = true;
        this.beaconConfirmButton.enabled = false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_)
    {
        RenderHelper.disableStandardItemLighting();
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.iron_category"), 32, 15, 2500392);
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.gold_category"), 32, 15 + 22, 2500392);
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.emerald_category"), 32, 15 + 22 * 2, 2500392);
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.diamond_category"), 32, 15 + 22 * 3, 2500392);

        for (GuiButton button : this.buttonList)
        {
            if (button.isMouseOver())
            {
                button.drawButtonForegroundLayer(p_drawGuiContainerForegroundLayer_1_ - this.guiLeft, p_drawGuiContainerForegroundLayer_2_ - this.guiTop);
                break;
            }
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    private void drawStage(Item icon, Item ore, int oreU, int oreV, int stage, int x, int y)
    {
        itemRender.renderItemAndEffectIntoGUI(new ItemStack(icon), x, y);
        for (int lvl = 0; lvl < 5; lvl++)
        {
            if (lvl < stage)
            {
                itemRender.renderItemAndEffectIntoGUI(new ItemStack(ore), x + 100 + (lvl * 21), y);
            }
            else
            {
                this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
                GlStateManager.color(1, 1, 1, 0.5f);
                drawTexturedModalRect(x + 100 + (lvl * 21), y, oreU, oreV,16, 16);
                GlStateManager.color(1, 1, 1, 1);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_drawGuiContainerBackgroundLayer_1_, int p_drawGuiContainerBackgroundLayer_2_, int p_drawGuiContainerBackgroundLayer_3_)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
        int screenX = (this.width - this.xSize) / 2;
        int screenY = (this.height - this.ySize) / 2;

        drawTexturedModalRect(screenX, screenY, 0, 0, this.xSize, this.ySize);
        itemRender.zLevel = 100.0F;

        itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.EMERALD), screenX + 42, screenY + 109);
        itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.DIAMOND), screenX + 42 + 22, screenY + 109);
        itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLD_INGOT), screenX + 42 + 44, screenY + 109);
        itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_INGOT), screenX + 42 + 66, screenY + 109);

        drawStage(Items.IRON_SWORD, Items.IRON_INGOT, 162, 235, te.getField(1), screenX + 14, screenY + 11);
        drawStage(Items.MUSIC_DISC_WAIT, Items.GOLD_INGOT, 144, 235, te.getField(2), screenX + 14, screenY + 33);
        drawStage(Blocks.LILY_PAD.getItem(), Items.EMERALD, 162, 219, te.getField(3), screenX + 14, screenY + 55);
        drawStage(Items.DIAMOND_CHESTPLATE, Items.DIAMOND,144,219, te.getField(4), screenX + 14, screenY + 77);

        this.itemRender.zLevel = 0.0F;
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_)
    {
        this.drawDefaultBackground();
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        this.renderHoveredToolTip(p_drawScreen_1_, p_drawScreen_2_);
    }

    class ConfirmButton extends GuiBetterBeacon.Button {
        public ConfirmButton(int p_i1075_2_, int p_i1075_3_, int p_i1075_4_) {
            super(p_i1075_2_, p_i1075_3_, p_i1075_4_, GuiBetterBeacon.BEACON_GUI_TEXTURES, 90, 220);
        }

        public void mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_) {
           // GuiBetterBeacon.this.mc.getConnection().sendPacket(new CPacketUpdateBeacon(GuiBetterBeacon.this.te.getField(1), GuiBetterBeacon.this.te.getField(2)));
            GuiBetterBeacon.this.mc.player.connection.sendPacket(new CPacketCloseWindow(GuiBetterBeacon.this.mc.player.openContainer.windowId));
            GuiBetterBeacon.this.mc.displayGuiScreen((GuiScreen)null);
        }

        public void drawButtonForegroundLayer(int p_drawButtonForegroundLayer_1_, int p_drawButtonForegroundLayer_2_) {
            GuiBetterBeacon.this.drawHoveringText(I18n.format("gui.done"), p_drawButtonForegroundLayer_1_, p_drawButtonForegroundLayer_2_);
        }
    }

    abstract static class Button extends GuiButton
    {
        private final ResourceLocation iconTexture;
        private final int iconX;
        private final int iconY;
        private boolean selected;

        protected Button(int id, int x, int y, ResourceLocation tex, int u, int v)
        {
            super(id, x, y, 22, 22, "");
            this.iconTexture = tex;
            this.iconX = u;
            this.iconY = v;
        }

        @Override
        public void func_194828_a(int p_194828_1_, int p_194828_2_, float p_194828_3_)
        {
            if (this.visible)
            {
                Minecraft.getMinecraft().getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = p_194828_1_ >= this.x && p_194828_2_ >= this.y && p_194828_1_ < this.x + this.width && p_194828_2_ < this.y + this.height;
                int lvt_5_1_ = 0;
                if (!this.enabled)
                {
                    lvt_5_1_ += this.width * 2;
                } else if (this.selected)
                {
                    lvt_5_1_ += this.width * 1;
                } else if (this.hovered)
                {
                    lvt_5_1_ += this.width * 3;
                }

                this.drawTexturedModalRect(this.x, this.y, lvt_5_1_, 219, this.width, this.height);
                if (!BEACON_GUI_TEXTURES.equals(this.iconTexture))
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(this.iconTexture);
                }

                this.drawTexturedModalRect(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
            }
        }

        public boolean isSelected()
        {
            return this.selected;
        }

        public void setSelected(boolean p_setSelected_1_)
        {
            this.selected = p_setSelected_1_;
        }
    }
}
