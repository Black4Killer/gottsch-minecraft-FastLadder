/**
 * 
 */
package com.someguyssoftware.fastladder.client.gui;

import com.someguyssoftware.fastladder.client.gui.inventory.TeleportPadGui;
import com.someguyssoftware.fastladder.inventory.TeleportPadContainer;
import com.someguyssoftware.fastladder.tileentity.TeleportPadTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**

 *
 */
/**
 * @author Mark Gottschling onJan 2, 2018
 *
 * This class is used to get the client and server gui elements when a player opens a gui. There can only be one registered
 *   IGuiHandler instance handler per mod.
 */
public class GuiHandler implements IGuiHandler {
	// TODO update these to be correct
	private static final int GUIID_MBE_30 = 30;
	public static int getGuiID() {return GUIID_MBE_30;}
	
	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.network.IGuiHandler#getServerGuiElement(int, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int)
	 */
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID != getGuiID()) {
			System.err.println("Invalid ID: expected " + getGuiID() + ", received " + ID);
		}
		
		BlockPos xyz = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(xyz);
		if (tileEntity instanceof TeleportPadTileEntity) {
			TeleportPadTileEntity t = (TeleportPadTileEntity) tileEntity;
			return new TeleportPadContainer(player.inventory, t);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.network.IGuiHandler#getClientGuiElement(int, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int)
	 */
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID != getGuiID()) {
			System.err.println("Invalid ID: expected " + getGuiID() + ", received " + ID);
		}

		BlockPos xyz = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(xyz);
		if (tileEntity instanceof TeleportPadTileEntity) {
			TeleportPadTileEntity t = (TeleportPadTileEntity) tileEntity;
			return new TeleportPadGui(player.inventory, t);
		}
		return null;
	}
}
