/**
 * 
 */
package com.someguyssoftware.fastladder.client.gui.inventory;

import java.awt.Color;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.inventory.TeleportPadContainer;
import com.someguyssoftware.fastladder.tileentity.TeleportPadTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * @author Mark Gottschling onJan 2, 2018
 *
 */
public class TeleportPadGui extends GuiContainer {

	// This is the resource location for the background image for the GUI
	private static final ResourceLocation texture = new ResourceLocation(FastLadder.MODID, "textures/gui/mbe30_inventory_basic_bg.png");
	private TeleportPadTileEntity tileEntity;

	public TeleportPadGui(InventoryPlayer invPlayer, TeleportPadTileEntity tile) {
		super(new TeleportPadContainer(invPlayer, tile));
		tileEntity = tile;
		
		// Set the width and height of the gui.  Should match the size of the texture!
		xSize = 176;
		ySize = 133;
	}	
	
	/* (non-Javadoc)
	 * @see net.minecraft.client.gui.inventory.GuiContainer#drawGuiContainerBackgroundLayer(float, int, int)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// Bind the image texture of our custom container
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		// Draw the image
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	// draw the foreground for the GUI - rendered after the slots, but before the dragged items and tooltips
	// renders relative to the top left corner of the background
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		final int LABEL_XPOS = 5;
		final int LABEL_YPOS = 5;
		fontRenderer.drawString(tileEntity.getDisplayName().getUnformattedText(), LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
	}
}
