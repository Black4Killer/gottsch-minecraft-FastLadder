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
	public static final int TELEPORT_PAD_GUIID = 1;

	
	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.network.IGuiHandler#getServerGuiElement(int, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int)
	 */
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos xyz = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(xyz);
		
		switch(ID) {
			case TELEPORT_PAD_GUIID:
				if (tileEntity instanceof TeleportPadTileEntity) {
					return new TeleportPadContainer(player.inventory, (TeleportPadTileEntity) tileEntity);
				}
				break;
			default: return null;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.network.IGuiHandler#getClientGuiElement(int, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int)
	 */
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos xyz = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(xyz);
		switch(ID) {
			case TELEPORT_PAD_GUIID:
				if (tileEntity instanceof TeleportPadTileEntity) {
					return new TeleportPadGui(player.inventory, (TeleportPadTileEntity) tileEntity);
				}
				break;
			default: return null;
		}
		return null;
	}
}
