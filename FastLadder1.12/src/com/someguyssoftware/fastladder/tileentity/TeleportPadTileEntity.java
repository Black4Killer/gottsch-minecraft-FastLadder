/**
 * 
 */
package com.someguyssoftware.fastladder.tileentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.tileentity.AbstractModTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * @author Mark Gottschling onDec 26, 2017
 * NOTE had a real issue extending AbstractModTileEntity into another abstract class and implementing IInventory,
 * then extending that class into TeleportPadTileEntity.  Gradle kept throwing errors that TeleportPadTileEntity wasn't
 * implementing required methods.
 *
 */
public class TeleportPadTileEntity extends AbstractModTileEntity implements IInventory {
	/**/
	public static final int FUEL_SLOT = 0;

	/*
	 * Coordinates of the linked TeleportLadder.
	 */
	private ICoords linkedTeleportLadderCoords;
	
	/*
	 * A list of player names that are colliding with the block
	 */
	private List<String> collidingPlayers = new ArrayList<>();
	
	/*
	 * A list of teleport "transactions" - an object that holds the source and destination pad coords, mapped by the user's name.
	 */
	private Map<String, TeleportTransaction> transactions = new HashMap<>();
	
	// Create and initialize the items variable that will store store the items
	private final int NUMBER_OF_SLOTS = 1;
	private ItemStack[] itemStacks;
	
	/**
	 * Empty constructor
	 */
	public TeleportPadTileEntity() {
		setItemStacks(new ItemStack[NUMBER_OF_SLOTS]);
		clear();
	}
	
	/**
	* This controls whether the tile entity gets replaced whenever the block state 
	* is changed. Normally only want this when block actually is replaced.
	* NOTE this method is very important!
	*/
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}
	
	/**
	 * 
	 */
	@Override
	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
	}
	
	/**
	 * 
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound parentNBT) {
		try {
			super.writeToNBT(parentNBT); // The super call is required to save the tiles location

//			parentNBT.setString("testString", "hello World!");
			
			if (getLink() != null) {
				NBTTagCompound linkPosNBT = new NBTTagCompound();
				linkPosNBT = getLink().writeToNBT(linkPosNBT);
				parentNBT.setTag("link", linkPosNBT);
			}
			
			// create a new NBTTagLis to store all the colliding players
			if (getCollidingPlayers() != null && getCollidingPlayers().size() > 0) {
				NBTTagList tagListNBT = new NBTTagList();
				for (String s : getCollidingPlayers()) {
					NBTTagCompound tagNBT = new NBTTagCompound();
					tagNBT.setString("name", s);
					tagListNBT.appendTag(tagNBT);
				}			
				// add the taglist to the compound
				parentNBT.setTag("collidingPlayers", tagListNBT);
			}
			
			// create a new NBTTagList to store all the teleport transactions
			if (getTransactions() != null && getTransactions().size() > 0) {
				NBTTagList tagListNBT = new NBTTagList();
				for (Entry<String, TeleportTransaction> e : getTransactions().entrySet()) {
					TeleportTransaction t = e.getValue();
					// create a new tag for the transaction
					NBTTagCompound tagNBT = new NBTTagCompound();

					// create a new tag for the source coords
					NBTTagCompound sourceNBT = new NBTTagCompound();
					sourceNBT = t.getSource().writeToNBT(sourceNBT);
					// create a new tag for the dest coords
					NBTTagCompound destNBT = new NBTTagCompound();
					destNBT = t.getDest().writeToNBT(destNBT);
					
					// write values to transaction tag
					tagNBT.setString("name", e.getKey());
					tagNBT.setTag("source", sourceNBT);
					tagNBT.setTag("dest", destNBT);
					
					// add the tag to the list
					tagListNBT.appendTag(tagNBT);
				}
				// add the taglist to the compound
				parentNBT.setTag("transactions", tagListNBT);
			}
			
			// save the inventory items
		    NBTTagList itemsNBT = new NBTTagList();			
		    for (int i = 0; i < this.getItemStacks().length; ++i) {
		        if (this.getItemStacks()[i] != null) {
		            NBTTagCompound itemTag = new NBTTagCompound();
		            itemTag.setByte("Slot", (byte)i);
		            this.getItemStacks()[i].writeToNBT(itemTag);
		            itemsNBT.appendTag(itemTag);
		        }
		    }	
		    parentNBT.setTag("Items", itemsNBT);			
		}
		catch(Exception e) {
			FastLadder.log.error("An error writing NBT:", e);
		}
		
		return parentNBT;
	}

	// This is where you load the data that you saved in writeToNBT
	@Override
	public void readFromNBT(NBTTagCompound parentNBT) {
		super.readFromNBT(parentNBT); // The super call is required to load the tiles location

		// important rule: never trust the data you read from NBT, make sure it can't cause a crash

		if (parentNBT.hasKey("link")) {
			NBTTagCompound linkNBT = parentNBT.getCompoundTag("link");
			this.setLink(ICoords.readFromNBT(linkNBT));
		}
		
		// read the colliding player list
		if (parentNBT.hasKey("collidingPlayers")) {
			this.getCollidingPlayers().clear();
			NBTTagList list = parentNBT.getTagList("collidingPlayers", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound c = list.getCompoundTagAt(i);
				this.getCollidingPlayers().add(c.getString("name"));
			}
		}
		
		if (parentNBT.hasKey("transactions")) {
			this.transactions.clear();
			NBTTagList list = parentNBT.getTagList("transactions", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound c = list.getCompoundTagAt(i);
				
				NBTTagCompound source = c.getCompoundTag("source");
				NBTTagCompound dest = c.getCompoundTag("dest");				
				String name = c.getString("name");
				
				// create a transaction
				TeleportTransaction t = new TeleportTransaction();
				t.setSource(ICoords.readFromNBT(source));
				t.setDest(ICoords.readFromNBT(dest));
				
				// add transaction to map, keyed by name
				this.getTransactions().put(name, t);
			}
		}
		
		// read the inventory items
		final byte NBT_TYPE_COMPOUND = 10;
		// See NBTBase.createNewByType() for a listing
		NBTTagList itemsNBT = parentNBT.getTagList("Items", NBT_TYPE_COMPOUND);
		
		// set all slots to empty EMPTY
		Arrays.fill(getItemStacks(), ItemStack.EMPTY);          
		for (int i = 0; i < itemsNBT.tagCount(); ++i) {
			NBTTagCompound itemNBT = itemsNBT.getCompoundTagAt(i);
			int slotIndex = itemNBT.getByte("Slot") & 255;

			if (slotIndex >= 0 && slotIndex < this.getItemStacks().length) {
				this.getItemStacks()[slotIndex] = new ItemStack(itemNBT);
			}
		}
	}

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
	public ICoords getLink() {
		return linkedTeleportLadderCoords;
	}

	/**
	 * 
	 * @param link
	 */
	public void setLink(ICoords link) {
		this.linkedTeleportLadderCoords = link;
	}

	/**
	 * @return the collidingPlayers
	 */
	public List<String> getCollidingPlayers() {
		return collidingPlayers;
	}

	/**
	 * @param collidingPlayers the collidingPlayers to set
	 */
	public void setCollidingPlayers(List<String> collidingPlayers) {
		this.collidingPlayers = collidingPlayers;
	}

	/**
	 * @return the transactions
	 */
	public Map<String, TeleportTransaction> getTransactions() {
		return transactions;
	}

	/**
	 * @param transactions the transactions to set
	 */
	public void setTransactions(Map<String, TeleportTransaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * 
	 */
	@Override
	public int getSizeInventory() {
		return getItemStacks().length;
	}

	/**
	 * 
	 */
	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : getItemStacks()) {
			if (!itemStack.isEmpty()) {  // isEmpty()
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
		return getItemStacks()[index];
	}

	/**
	 * Removes some of the units from itemstack in the given slot, and returns as a separate itemstack
 	 * @param slotIndex the slot number to remove the items from
	 * @param count the number of units to remove
	 * @return a new itemstack containing the units removed from the slot
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemStackInSlot = getStackInSlot(index);
		if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  // isEmpt();   EMPTY_ITEM

		ItemStack itemStackRemoved;
		if (itemStackInSlot.getCount() <= count) {  // getStackSize()
			itemStackRemoved = itemStackInSlot;
			setInventorySlotContents(index, ItemStack.EMPTY);   // EMPTY_ITEM
		} else {
			itemStackRemoved = itemStackInSlot.splitStack(count);
			if (itemStackInSlot.getCount() == 0) { // getStackSize
				setInventorySlotContents(index, ItemStack.EMPTY);   // EMPTY_ITEM
			}
		}
		markDirty();
		return itemStackRemoved;
	}	

	/**
	 * This method removes the entire contents of the given slot and returns it.
	 * Used by containers such as crafting tables which return any items in their slots when you close the GUI
	 * @param slotIndex
	 * @return
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = getStackInSlot(index);
		if (!itemStack.isEmpty()) setInventorySlotContents(index, ItemStack.EMPTY);  //isEmpty(), EMPTY_ITEM
		return itemStack;
	}

	/**
	 * 
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		getItemStacks()[index] = stack;
		if (stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) { //  isEmpty(); getStackSize()
			stack.setCount(getInventoryStackLimit());  //setStackSize
		}
		markDirty();
	}

	/**
	 * 
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/*
	 * Return true if the given player is able to use this block. In this case it checks that
	 * 1) the world tileentity hasn't been replaced in the meantime, and
	 * 2) the player isn't too far away from the centre of the block(non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isUsableByPlayer(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (this.world.getTileEntity(this.pos) != this) return false;
		final double X_CENTRE_OFFSET = 0.5;
		final double Y_CENTRE_OFFSET = 0.5;
		final double Z_CENTRE_OFFSET = 0.5;
		final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
		return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
	}

	/**
	 * 
	 */
	@Override
	public void openInventory(EntityPlayer player) {}

	/**
	 * 
	 */
	@Override
	public void closeInventory(EntityPlayer player) {}

	/*
	 *  Return true if the given stack is allowed to go in the given slot.(non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isItemValidForSlot(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		Item item = stack.getItem();
        if (item == Items.GLOWSTONE_DUST) {
            return true;
        }
        return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) { }

	@Override
	public int getFieldCount() {
		return 0;
	}

	/**
	 * 
	 */
	@Override
	public void clear() {
		Arrays.fill(getItemStacks(), ItemStack.EMPTY);  //empty item
	}

	/**
	 * 
	 */
	@Override
	public String getName() {
		return "container.fastladder:teleport_pad";
	}

	/**
	 * 
	 */
	@Override
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * @return the itemStacks
	 */
	public ItemStack[] getItemStacks() {
		return itemStacks;
	}

	/**
	 * @param itemStacks the itemStacks to set
	 */
	public void setItemStacks(ItemStack[] itemStacks) {
		this.itemStacks = itemStacks;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasFuel() {
		ItemStack itemStack = this.getItemStacks()[0];
		if (itemStack != null && itemStack != ItemStack.EMPTY && itemStack.getItem() == Items.GLOWSTONE_DUST) return true;
		return false;
	}

}
