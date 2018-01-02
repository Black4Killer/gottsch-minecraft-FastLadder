package com.someguyssoftware.fastladder.tileentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.event.entity.living.LivingEvent;

@Deprecated
public class TeleportLadderTileEntity extends TileEntity implements IInventory, IInteractionObject{
	/*
	 * Coordinates of the linked TeleportLadder.
	 */
	private ICoords linkedTeleportLadderCoords;
	
	/*
	 * 
	 */
	private List<String> receivingPlayerList;
	
    /** The number of ticks that the contraption will keep burning */
    private int furnaceBurnTime;
    /** The number of ticks that a fresh copy of the fuel item would keep the contraption burning for */
    private int currentItemBurnTime;
	private String customName;
	
	/**
	 * 
	 */
	public TeleportLadderTileEntity() {
		receivingPlayerList = new ArrayList<>();
	}
	
	/**
	 * 
	 */
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		int metadata = getBlockMetadata();
		return new SPacketUpdateTileEntity(this.pos, metadata, nbtTagCompound);
	}

	/**
	 * 
	 */
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	
	/**
	 * 
	 */
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return nbtTagCompound;
	}

	/* 
	 * Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
	 */
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
	  
	/**
	 * 
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		try {
			FastLadder.log.debug("Writing to NBT");
			// write mod specific values
			if (getLink() != null) {
				compound.setInteger("linkX", getLink().getX());
				compound.setInteger("linkY", getLink().getY());
				compound.setInteger("linkZ", getLink().getZ());
			}
			
			FastLadder.log.debug("Wrote Link: " + getLink());
			
			// create a new NBTTagList to store all the individual entries
			NBTTagList tagList = new NBTTagList();
			for (String s : getReceivingPlayerList()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setString("name", s);
				tagList.appendTag(tag);
			}
			
			// add the taglist to the compound
			compound.setTag("receivingPlayerList", tagList);
		}
		catch(Exception e) {
			FastLadder.log.error("Error writing NBT:", e);
		}

		return compound;
	}
	
	/**
	 * 
	 */
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		try {
			super.readFromNBT(compound);
			
			FastLadder.log.debug("Reading NBT");
			// specific stuff
			if (compound.hasKey("linkX")) {
				int x = compound.getInteger("linkX");
				int y = compound.getInteger("linkY");
				int z = compound.getInteger("linkZ");
				ICoords coords = new Coords(x, y, z);
				this.setLink(coords);
				FastLadder.log.debug("Read Link: " + getLink());
			}
		}
		catch(Exception e) {
			FastLadder.log.error("Error reading NBT:", e);
		}	}
	
	/**
	 * Check if this TeleportLadderContraption is linked to another Contraction or Pad.
	 * @return
	 */
	public boolean isLinked() {
		if (getLink() != null) return true;
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasFuel() {
		if (furnaceBurnTime > 0) return true;
		return false;
	}
	
//	@Override
//	public String getName() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public boolean hasCustomName() {
//		// TODO Auto-generated method stub
//		return false;
//	}

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.teleportladder";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    /**
     * 
     * @param name
     */
    public void setCustomInventoryName(String name) {
        this.customName = name;
    }
    
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGuiID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the receivingPlayerList
	 */
	public List<String> getReceivingPlayerList() {
		return receivingPlayerList;
	}

	/**
	 * @param receivingPlayerList the receivingPlayerList to set
	 */
	public void setReceivingPlayerList(List<String> receivingPlayerList) {
		this.receivingPlayerList = receivingPlayerList;
	}

	public void setLink(ICoords coords) {
		this.linkedTeleportLadderCoords = coords;
	}
	
	/**
	 * 
	 * @return
	 */
	public ICoords getLink() {
		return this.linkedTeleportLadderCoords;
	}
}
