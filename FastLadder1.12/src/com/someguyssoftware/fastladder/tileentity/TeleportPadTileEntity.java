/**
 * 
 */
package com.someguyssoftware.fastladder.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * @author Mark Gottschling onDec 26, 2017
 *
 */
public class TeleportPadTileEntity extends TileEntity {
	/*
	 * Coordinates of the linked TeleportLadder.
	 */
	private ICoords linkedTeleportLadderCoords;
	
	/*
	 * A list of player names that are being teleported to the block.
	 */
//	private List<String> receivingPlayerList = new ArrayList<>();
	
	/*
	 * A list of player names that are colliding with the block
	 */
	private List<String> collidingPlayers = new ArrayList<>();
	
	/*
	 * A list of teleport "transactions" - an object that holds the source and destination pad coords, mapped by the user's name.
	 */
	private Map<String, TeleportTransaction> transactions = new HashMap<>();
	
	
	/**
	* This controls whether the tile entity gets replaced whenever the block state 
	* is changed. Normally only want this when block actually is replaced.
	* NOTE this method is very important!
	*/
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound parentNBT) {
		try {
			super.writeToNBT(parentNBT); // The super call is required to save the tiles location
	
			// alternatively - could use parentNBTTagCompound.setTag("ticksLeft", new NBTTagInt(ticksLeftTillDisappear));
	
			// some examples of other NBT tags - browse NBTTagCompound or search for the subclasses of NBTBase for more examples
	
			parentNBT.setString("testString", "hello World!");
	//		parentNBT.setString("message", message);
			
	//		NBTTagCompound blockPosNBT = new NBTTagCompound();        // NBTTagCompound is similar to a Java HashMap
	//		blockPosNBT.setInteger("myX", 1);
	//		parentNBT.setTag("testBlockPos", blockPosNBT);
			
			if (getLink() != null) {
				NBTTagCompound linkPosNBT = new NBTTagCompound();
				linkPosNBT.setInteger("x", getLink().getX());
				linkPosNBT.setInteger("y", getLink().getY());
				linkPosNBT.setInteger("z", getLink().getZ());
				linkPosNBT.setInteger("myX", this.getPos().getX());
				linkPosNBT.setInteger("myY", this.getPos().getY());
				linkPosNBT.setInteger("myZ", this.getPos().getZ());
				parentNBT.setTag("link", linkPosNBT);

			}
			
			// create a new NBTTagList to store all the individual entries
//			if (getReceivingPlayerList() != null && getReceivingPlayerList().size() > 0) {
//				NBTTagList tagListNBT = new NBTTagList();
//				for (String s : getReceivingPlayerList()) {
//					NBTTagCompound tagNBT = new NBTTagCompound();
//					tagNBT.setString("name", s);
//					tagListNBT.appendTag(tagNBT);
//				}			
//				// add the taglist to the compound
//				parentNBT.setTag("receivingPlayerList", tagListNBT);
//			}
			
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
					NBTTagCompound tagNBT = new NBTTagCompound();
					TeleportTransaction t = e.getValue();
					tagNBT.setInteger("sourceX", t.getSource().getX());
					tagNBT.setInteger("sourceY", t.getSource().getY());
					tagNBT.setInteger("sourceZ", t.getSource().getZ());
					tagNBT.setInteger("destX", t.getDest().getX());
					tagNBT.setInteger("destY", t.getDest().getY());
					tagNBT.setInteger("destZ", t.getDest().getZ());
					tagNBT.setString("name", e.getKey());
					tagListNBT.appendTag(tagNBT);				
				}
				// add the taglist to the compound
				parentNBT.setTag("transactions", tagListNBT);
			}
			
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

		// some examples of other NBT tags - browse NBTTagCompound or search for the subclasses of NBTBase for more

//		String readTestString = null;
//		if (parentNBTTagCompound.hasKey("testString", 3)) {
//			readTestString = parentNBTTagCompound.getString("testString");
//		}
//		String testString = "hello World!";
//		if (!testString.equals(readTestString)) {
//			System.err.println("testString mismatch:" + readTestString);
//		}
		Integer x = null;
		Integer y = null;
		Integer z = null;
		if (parentNBT.hasKey("link")) {
			NBTTagCompound linkNBT = parentNBT.getCompoundTag("link");
	
			if (linkNBT.hasKey("x")) {
				x = linkNBT.getInteger("x");
			}
			if (linkNBT.hasKey("y")) {
				y = linkNBT.getInteger("y");
			}
			if (linkNBT.hasKey("z")) {
				z = linkNBT.getInteger("z");
			}
			if (x != null && y != null && z != null) {
				ICoords link = new Coords(x, y, z);
				this.setLink(link);
			}
		}
		// read the receiving player list
//		if (parentNBT.hasKey("receivingPlayerList")) {
//			this.getReceivingPlayerList().clear();
//			NBTTagList list = parentNBT.getTagList("receivingPlayerList", Constants.NBT.TAG_COMPOUND);
//			for (int i = 0; i < list.tagCount(); i++) {
//				NBTTagCompound c = list.getCompoundTagAt(i);
//				this.getReceivingPlayerList().add(c.getString("name"));
//			}			
//		}
		
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
				Integer sx = c.getInteger("sourceX");
				Integer sy = c.getInteger("sourceY");
				Integer sz = c.getInteger("sourceZ");

				Integer dx = c.getInteger("destX");
				Integer dy = c.getInteger("destY");
				Integer dz = c.getInteger("destZ");
				
				String name = c.getString("name");
				TeleportTransaction t = new TeleportTransaction();
				t.setSource(new Coords(sx, sy, sz));
				t.setDest(new Coords(dx, dy, dz));
				this.getTransactions().put(name, t);
			}
		}
	}
	
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		int metadata = getBlockMetadata();
		return new SPacketUpdateTileEntity(this.pos, metadata, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	/* Creates a tag containing the TileEntity information, used by vanilla to transmit from server to client
	 */
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return nbtTagCompound;
	}

	/* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
	 */
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
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

//	/**
//	 * @return the receivingPlayerList
//	 */
//	public List<String> getReceivingPlayerList() {
//		return receivingPlayerList;
//	}
//
//	/**
//	 * @param receivingPlayerList the receivingPlayerList to set
//	 */
//	public void setReceivingPlayerList(List<String> receivingPlayerList) {
//		this.receivingPlayerList = receivingPlayerList;
//	}

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

}
