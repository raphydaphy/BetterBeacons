package com.raphydaphy.betterbeacons.beacon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.ResourceLocation;

public class GuiBetterBeacon extends GuiContainer
{
    private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("betterbeacons:textures/better_beacon_gui.png");
    private static final int[] PLACEHOLDER_U = {162, 162, 144, 144};
    private static final int[] PLACEHOLDER_V = {235, 219, 235, 219};
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 331;

    private final IInventory te;
    private GuiBetterBeacon.ConfirmButton beaconConfirmButton;

    private int placeholderStage = 0;

    public GuiBetterBeacon(InventoryPlayer playerInv, IInventory inv)
    {
        super(new ContainerBetterBeacon(playerInv, inv));
        this.te = inv;
        this.xSize = 230;
        this.ySize = 219;
    }

    @Override
    protected void initGui()
    {
        super.initGui();
        this.beaconConfirmButton = new GuiBetterBeacon.ConfirmButton(-1, this.guiLeft + 69, this.guiTop + 107);
        this.addButton(new GuiBetterBeacon.CancelButton(-2, this.guiLeft + 140, this.guiTop + 107));
        this.addButton(this.beaconConfirmButton);
        this.beaconConfirmButton.enabled = false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_)
    {
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableBlend();
        int opaque = 0x262728;
        int transparent = 0x7f262728;
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.iron_category"), 32, 15, te.getField(1) > 0 ? opaque : transparent);
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.gold_category"), 32, 15 + 22, te.getField(2) > 0 ? opaque : transparent);
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.emerald_category"), 32, 15 + 22 * 2, te.getField(3) > 0 ? opaque : transparent);
        fontRenderer.func_211126_b(I18n.format("beacon.betterbeacons.diamond_category"), 32, 15 + 22 * 3, te.getField(4) > 0 ? opaque : transparent);
        GlStateManager.disableBlend();

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
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        boolean active = stage > 0;

        itemRender.renderItemAndEffectIntoGUI(new ItemStack(icon), x, y);
        for (int lvl = 0; lvl < 5; lvl++)
        {
            if (lvl < stage)
            {
                itemRender.renderItemAndEffectIntoGUI(new ItemStack(ore), x + 100 + (lvl * 21), y);
            } else
            {
                drawGreyStack(x + 100 + (lvl * 21), y,oreU, oreV, active ? 0.5f : 0.15f);
            }
        }

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void drawGreyStack(int x, int y, int u, int v, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
        GlStateManager.color(1, 1, 1, alpha);
        drawModalRectWithCustomSizedTexture(x, y, u, v, 16, 16,TEXTURE_WIDTH, TEXTURE_HEIGHT);
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawCategoryBackground(int x, int y, int category, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
        GlStateManager.color(1, 1, 1, alpha);
        drawModalRectWithCustomSizedTexture(x, y, 0, 251 + (category * 20), 206, 20,TEXTURE_WIDTH, TEXTURE_HEIGHT);
        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_drawGuiContainerBackgroundLayer_1_, int p_drawGuiContainerBackgroundLayer_2_, int p_drawGuiContainerBackgroundLayer_3_)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
        int screenX = (this.width - this.xSize) / 2;
        int screenY = (this.height - this.ySize) / 2;

        drawModalRectWithCustomSizedTexture(screenX, screenY, 0, 0, this.xSize, this.ySize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        for (int category = 0; category < 4; category++)
        {
            float alpha = 0.5f;
            if (te.getField(category + 1) > 0)
            {
                alpha = 1;
            }
            drawCategoryBackground(screenX + 12, screenY + 9 + (category * 22), category, alpha);
        }
        itemRender.zLevel = 100.0F;

        drawStage(Items.IRON_SWORD, Items.IRON_INGOT, 162, 235, te.getField(1), screenX + 14, screenY + 11);
        drawStage(Items.MUSIC_DISC_13, Items.GOLD_INGOT, 144, 235, te.getField(2), screenX + 14, screenY + 33);
        drawStage(Blocks.LILY_PAD.getItem(), Items.EMERALD, 162, 219, te.getField(3), screenX + 14, screenY + 55);
        drawStage(Items.DIAMOND_CHESTPLATE, Items.DIAMOND, 144, 219, te.getField(4), screenX + 14, screenY + 77);

        // Ingot placeholder
        if (te.getStackInSlot(0).isEmpty())
        {
            drawGreyStack(screenX+98, screenY+110, PLACEHOLDER_U[placeholderStage], PLACEHOLDER_V[placeholderStage], 0.5f);
        }
        // Nether star placeholder
        if (te.getStackInSlot(1).isEmpty())
        {
            drawGreyStack(screenX+118,screenY+ 110, 180, 219, 0.5f);
        }
        this.itemRender.zLevel = 0.0F;
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_)
    {
        this.drawDefaultBackground();
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        this.renderHoveredToolTip(p_drawScreen_1_, p_drawScreen_2_);
    }

    @Override
    public void updateScreen()
    {
        if (Minecraft.getMinecraft().world.getTotalWorldTime() % 25 == 0)
        {
            placeholderStage++;
            if (placeholderStage == 4)
            {
                placeholderStage = 0;
            }
        }
        this.beaconConfirmButton.enabled = !this.te.getStackInSlot(0).isEmpty() && !this.te.getStackInSlot(1).isEmpty();
    }

    class CancelButton extends GuiBetterBeacon.Button
    {
        CancelButton(int id, int x, int z)
        {
            super(id, x, z, GuiBetterBeacon.BEACON_GUI_TEXTURES, 112, 220);
        }

        public void mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_)
        {
            GuiBetterBeacon.this.mc.player.connection.sendPacket(new CPacketCloseWindow(GuiBetterBeacon.this.mc.player.openContainer.windowId));
            GuiBetterBeacon.this.mc.displayGuiScreen(null);
        }

        public void drawButtonForegroundLayer(int p_drawButtonForegroundLayer_1_, int p_drawButtonForegroundLayer_2_)
        {
            GuiBetterBeacon.this.drawHoveringText(I18n.format("gui.cancel"), p_drawButtonForegroundLayer_1_, p_drawButtonForegroundLayer_2_);
        }
    }

    class ConfirmButton extends GuiBetterBeacon.Button
    {
        ConfirmButton(int id, int x, int z)
        {
            super(id, x, z, GuiBetterBeacon.BEACON_GUI_TEXTURES, 90, 220);
        }

        public void mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_)
        {
            // GuiBetterBeacon.this.mc.getConnection().sendPacket(new CPacketUpdateBeacon(GuiBetterBeacon.this.te.getField(1), GuiBetterBeacon.this.te.getField(2)));
            GuiBetterBeacon.this.mc.player.connection.sendPacket(new CPacketCloseWindow(GuiBetterBeacon.this.mc.player.openContainer.windowId));
            GuiBetterBeacon.this.mc.displayGuiScreen(null);
        }

        public void drawButtonForegroundLayer(int p_drawButtonForegroundLayer_1_, int p_drawButtonForegroundLayer_2_)
        {
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

                this.drawModalRectWithCustomSizedTexture(this.x, this.y, lvt_5_1_, 219, this.width, this.height,TEXTURE_WIDTH, TEXTURE_HEIGHT);
                if (!BEACON_GUI_TEXTURES.equals(this.iconTexture))
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(this.iconTexture);
                }

                this.drawModalRectWithCustomSizedTexture(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18,TEXTURE_WIDTH, TEXTURE_HEIGHT);
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
