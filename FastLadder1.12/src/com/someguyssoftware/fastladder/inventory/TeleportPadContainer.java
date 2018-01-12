/**
 * 
 */
package com.someguyssoftware.fastladder.inventory;

import com.someguyssoftware.fastladder.tileentity.TeleportPadTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Mark Gottschling onJan 2, 2018
 *
 */
public class TeleportPadContainer extends Container {
	// Stores a reference to the tile entity instance for later use
	private TeleportPadTileEntity tileEntity;

	private final int HOTBAR_SLOT_COUNT = 9;
	private final int PLAYER_INVENTORY_ROW_COUNT = 3;
	private final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	private final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

	private final int VANILLA_FIRST_SLOT_INDEX = 0;
	private final int CONTAINER_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	private final int CONTAINER_INVENTORY_SLOT_COUNT = 1;
	
	/**
	 * 
	 * @param invPlayer
	 * @param tileEntity
	 */
	public TeleportPadContainer(InventoryPlayer invPlayer, TeleportPadTileEntity tileEntity) {
		this.tileEntity = tileEntity;

		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;
		final int HOTBAR_XPOS = 8;
		final int HOTBAR_YPOS = 109;
		
		// Add the players hotbar to the gui - the [xpos, ypos] location of each item
		for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
			int slotNumber = x;
			addSlotToContainer(new Slot(invPlayer, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
		}

		final int PLAYER_INVENTORY_XPOS = 8;
		final int PLAYER_INVENTORY_YPOS = 51;
		// Add the rest of the players inventory to the gui
		for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
			for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
				int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
				int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
				int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
				addSlotToContainer(new Slot(invPlayer, slotNumber,  xpos, ypos));
			}
		}

		if (CONTAINER_INVENTORY_SLOT_COUNT != tileEntity.getSizeInventory()) {
			System.err.println("Mismatched slot count in ContainerBasic(" + CONTAINER_INVENTORY_SLOT_COUNT
												  + ") and TileInventory (" + tileEntity.getSizeInventory()+")");
		}
		
		final int TILE_INVENTORY_XPOS = 80;//8;
		final int TILE_INVENTORY_YPOS = 20;		
		// Add the tile inventory container to the gui
		for (int x = 0; x < CONTAINER_INVENTORY_SLOT_COUNT; x++) {
			int slotNumber = x;
			addSlotToContainer(new Slot(tileEntity, slotNumber, TILE_INVENTORY_XPOS + SLOT_X_SPACING * x, TILE_INVENTORY_YPOS));
		}
	}
	
	// This is where you specify what happens when a player shift clicks a slot in the gui
	//  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
	//    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
	//    position in the TileEntity inventory)
	// At the very least you must override this and return EMPTY_ITEM or the game will crash when the player shift clicks a slot
	// returns EMPTY_ITEM if the source slot is empty, or if none of the the source slot items could be moved
	//   otherwise, returns a copy of the source stack
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
		Slot sourceSlot = (Slot)inventorySlots.get(sourceSlotIndex);
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

	 
		// Check if the slot clicked is one of the vanilla container slots
		if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
			/*
			 *  This is a vanilla container slot so merge the stack into the tile inventory
			 */
			
			// first ensure that the sourcStack is a valid item for the container
			if (!tileEntity.isItemValidForSlot(sourceSlotIndex, sourceStack)) {
				return ItemStack.EMPTY;
			}
			
			if (!mergeItemStack(sourceStack, CONTAINER_INVENTORY_FIRST_SLOT_INDEX, CONTAINER_INVENTORY_FIRST_SLOT_INDEX + CONTAINER_INVENTORY_SLOT_COUNT, false)){
				return ItemStack.EMPTY;
			}
		} else if (sourceSlotIndex >= CONTAINER_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < CONTAINER_INVENTORY_FIRST_SLOT_INDEX + CONTAINER_INVENTORY_SLOT_COUNT) {
			// This is a TE slot so merge the stack into the players inventory
			if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			System.err.print("Invalid slotIndex:" + sourceSlotIndex);
			return ItemStack.EMPTY;
		}

		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {  // getStackSize
			sourceSlot.putStack(ItemStack.EMPTY);
		} else {
			sourceSlot.onSlotChanged();
		}

		sourceSlot.onTake(player, sourceStack);  //onPickupFromSlot()
		return copyOfSourceStack;
	}
	
	/*
	 * pass the close container message to the tileEntity
	 * @see ContainerChest
	 * @see TileEntityChest
	 * @see net.minecraft.inventory.Container#onContainerClosed(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.tileEntity.closeInventory(playerIn);
	}

	/**
	 * 
	 */
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUsableByPlayer(player);
	}
}
