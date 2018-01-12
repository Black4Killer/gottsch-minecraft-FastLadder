/**
 * 
 */
package com.someguyssoftware.fastladder.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.client.gui.GuiHandler;
import com.someguyssoftware.fastladder.tileentity.TeleportPadTileEntity;
import com.someguyssoftware.fastladder.tileentity.TeleportTransaction;
import com.someguyssoftware.gottschcore.block.ModBlock;
import com.someguyssoftware.gottschcore.block.AbstractModContainerBlock;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Mark Gottschling onDec 26, 2017
 *
 */
public class TeleportPadBlock extends AbstractModContainerBlock {
//	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyEnum<Link> LINK = PropertyEnum.create("link", Link.class);
	public static final PropertyEnum<Placement> PLACEMENT = PropertyEnum.create("placement",  Placement.class);
	
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    
    /** This bounding box is used to check for entities in a certain area and then determine the pressed state. */
    protected static final AxisAlignedBB PAD_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.D, 1.0D, 0.125D, 1.0D);
    
    /**
     * 
     * @param modID
     * @param name
     */
	public TeleportPadBlock(String modID, String name) {
		super(modID, name, Material.IRON);
		this.setCreativeTab(CreativeTabs.MISC);
		this.setDefaultState(this.blockState
				.getBaseState()
//				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(PLACEMENT, Placement.BOTTOM)
				.withProperty(LINK, Link.UNLINKED));
		this.setNormalCube(false);
	}
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param material
	 */
	public TeleportPadBlock(String modID, String name, Material material) {
		super(modID, name, material);
		this.setCreativeTab(CreativeTabs.MISC);
		this.setDefaultState(this.blockState
				.getBaseState()
//				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(PLACEMENT, Placement.BOTTOM)
				.withProperty(LINK, Link.UNLINKED));
		this.setNormalCube(false);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	// Called when the block is placed or loaded client side to get the tile entity for the block
	// Should return a new instance of the tile entity for the block
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TeleportPadTileEntity();	  
	}

	/**
	 * 
	 * @param worldIn
	 * @param meta
	 * @return
	 */
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TeleportPadTileEntity();	  
	}
	
    /**
     * 
     */
	@Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(FastLadderBlocks.TELEPORT_PAD);
    }
    
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
//	@Override
//    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
//        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
//    }
	
	/**
	 * 
	 */
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return NORTH_AABB;
//		switch ((EnumFacing)state.getValue(FACING)) {
//		    case NORTH:
//		        return NORTH_AABB;
//		    case SOUTH:
//		        return SOUTH_AABB;
//		    case WEST:
//		        return WEST_AABB;
//		    case EAST:
//		    default:
//		        return NORTH_AABB;
//		}
	}
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return PAD_AABB;
    }
    
//    /**
//     * 
//     * @param worldIn
//     * @param pos
//     * @param facing
//     * @return
//     */
//    protected boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing) {
//        return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP);
//    }
    
    /**
     * 
     */
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return this.canBePlacedOn(worldIn, pos.down());
    }
    
    /**
     * 
     * @param worldIn
     * @param pos
     * @return
     */
    private boolean canBePlacedOn(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP) || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
    }
    
    /**
     * 
     */
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    	super.onBlockAdded(worldIn, pos, state);
    }
    
    /**
     * Called just after the player places a block.  Attempt to link teleport pads.
     */
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		ICoords coords = new Coords(pos);
		boolean isTop = false;
		
		// face the teleport ladder towards the palyer (there isn't really a front)
//        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 3);
        
        // get the backing tile entity
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TeleportPadTileEntity) { // prevent a crash if not the right type, or is null
			TeleportPadTileEntity t = (TeleportPadTileEntity) tileEntity;
			
	        // seach up until the max, then to the bottom until min, searching for another Contraption
	        boolean foundTeleportLadder = false;
	        ICoords targetCoords = coords.up(1);
	        
	        while (targetCoords.getY() < worldIn.getHeight() && !foundTeleportLadder) {
	        	if (isUnlinkedTeleportLadder(worldIn, targetCoords)) {
	        		foundTeleportLadder = true;
	        		isTop = false;
	        	}
	        	else {        		
	        		targetCoords = targetCoords.up(1);
	        	}
	        }
	        
	        // search down
	        if (!foundTeleportLadder) {
	        	targetCoords = coords.down(1);
	        	while (targetCoords.getY() > 1 && !foundTeleportLadder) {
	            	if (isUnlinkedTeleportLadder(worldIn, targetCoords)) {
	            		foundTeleportLadder = true;
	            		isTop = true;
	            	}
	            	else {
	            		targetCoords = targetCoords.down(1);
	            	}
	        	}
	        }
	        
	        if (foundTeleportLadder) {
	        	// update the block state
	    		if (isTop) {
	    			worldIn.setBlockState(pos, state.withProperty(LINK, Link.LINKED).withProperty(PLACEMENT, Placement.TOP), 3);
	    		}
	    		else {
	    			worldIn.setBlockState(pos, state.withProperty(LINK, Link.LINKED).withProperty(PLACEMENT, Placement.BOTTOM), 3);
	    		}
	 			this.setLightLevel(1.0F);
	    		
	    		// update the linked property of the backing title entity
	    		t.setLink(targetCoords);
	    		
	    		// update the discovered TeleportLadder with this coords
	    		// get the state at target coords
	    		IBlockState targetState = worldIn.getBlockState(targetCoords.toPos());
	    		if (isTop) {
	    			worldIn.setBlockState(targetCoords.toPos(), targetState.withProperty(LINK, Link.LINKED).withProperty(PLACEMENT, Placement.BOTTOM), 3);
	    		}
	    		else {
	    			worldIn.setBlockState(targetCoords.toPos(), targetState.withProperty(LINK, Link.LINKED).withProperty(PLACEMENT, Placement.TOP), 3);
	    		}
	    		TeleportPadTileEntity te2 = ((TeleportPadTileEntity)worldIn.getTileEntity(targetCoords.toPos()));
	    		if (te2 != null) {
	    			te2.setLink(coords);
	    		}
	    		worldIn.getBlockState(targetCoords.toPos()).getBlock().setLightLevel(1.0F);
	        }
		}
	
	}
	
    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
	@Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBePlacedOn(worldIn, pos.down())) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
    
    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
       TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity != null && tileEntity instanceof TeleportPadTileEntity) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TeleportPadTileEntity)tileEntity);
 
            // unlinked target teleport ladder
            TeleportPadTileEntity t = (TeleportPadTileEntity)tileEntity;    
            if (t.isLinked()) {
            	t.getCollidingPlayers().clear();
            	t.getTransactions().clear();
            	
            	FastLadder.log.debug("Unlinking target @ " + t.getLink().toShortString());
            	// get the target tile entity
            	TeleportPadTileEntity target = (TeleportPadTileEntity) worldIn.getTileEntity(t.getLink().toPos());
            	if (target != null) {
            		// TODO could replace the next 3 lines with a single method resetTeleportState();
            		target.setLink(null);
            		target.getCollidingPlayers().clear();
            		target.getTransactions().clear();
            	}
            	// get the target state
            	IBlockState targetState = worldIn.getBlockState(target.getPos());
            	// update the target state
            	worldIn.setBlockState(target.getPos(), targetState.withProperty(LINK, Link.UNLINKED), 3);
            	// update the target block light level
            	worldIn.getBlockState(target.getPos()).getBlock().setLightLevel(0F);
            	
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    /**
     * Called after x number of ticks. If the receiving list has values then update the state.
     */
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
//        	FastLadder.log.debug("Updating Tick");
        	// if the receiving player list has values
        	TeleportPadTileEntity t = (TeleportPadTileEntity) worldIn.getTileEntity(pos);
        	if (t != null) {
        		updateState(worldIn, pos, state);
            }
        }
    }
    
    /**
     * Called When an Entity Collided with the Block. Teleport and flag target.
     */
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
    	// only teleport on server
        if (!worldIn.isRemote) {
        	// TODO test if ladder has fuel/enough fuel
        	// TODO test if ladder is linked
//        	FastLadder.log.debug("Colliding with teleport block @ " + pos);

        	// get the backing tile entity
        	TeleportPadTileEntity t = (TeleportPadTileEntity) worldIn.getTileEntity(pos);
        	if (!t.isLinked()) {
//        		FastLadder.log.debug("Teleport Pad is not linked @ " + pos);
        		return;
        	}
        	
        	if (t!= null && entityIn instanceof EntityPlayer) {		

    			
        		if (!t.getCollidingPlayers().contains(entityIn.getName())) {
//        			FastLadder.log.debug("Not in colliding list");
             		
            		TeleportTransaction trans = t.getTransactions().get(entityIn.getName()); 		
            		if (trans != null) {
//            			FastLadder.log.debug("Found transaction" + trans);
            			
            			// add to the colliding list
                		t.getCollidingPlayers().add(entityIn.getName());
                		
                    	// schedule another update
                    	worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
            		}
            		else {
            			// ensure the teleporter has fuel
            			if (!t.hasFuel()) { return; }
            			
            			// add to the colliding list
                		t.getCollidingPlayers().add(entityIn.getName());
                		
            			TeleportPadTileEntity destTE = (TeleportPadTileEntity) worldIn.getTileEntity(t.getLink().toPos());
            			if (destTE != null ) {
            				// decrement the fuel count
            				t.decrStackSize(TeleportPadTileEntity.FUEL_SLOT, 1);
            				
	            			// create a transaction
	            			trans = new TeleportTransaction(new Coords(pos), t.getLink());
	            			// add the transaction to both teleporters
	            			t.getTransactions().put(entityIn.getName(), trans);
	            			destTE.getTransactions().put(entityIn.getName(), trans);

	            			// remove from colliding list
	            			if (t.getCollidingPlayers().contains(entityIn.getName())) {
	            				t.getCollidingPlayers().remove(entityIn.getName());
	            			}
	            			
	            			// teleport
	            			entityIn.setPositionAndUpdate(t.getLink().getX() + 0.5F, t.getLink().getY(), t.getLink().getZ() + 0.5F);
            			}
            		}
        		}
        	}
        }
    } 

    /**
     * Updates the TeleportLadder. Check if user has stepped off (if target ladder) and if so, then clear the flag.
     */
    protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
        AxisAlignedBB aabb = PAD_AABB.offset(pos);
        List <? extends Entity > list;
        list = worldIn.<Entity>getEntitiesWithinAABB(EntityPlayer.class, aabb);
        
//        FastLadder.log.debug("Updating state of " + pos);
        
        // get the tile entity
        TeleportPadTileEntity t = (TeleportPadTileEntity) worldIn.getTileEntity(pos);
        
        if (list == null || list.size() == 0) {
//        	FastLadder.log.debug("AABB list is empty. Clearing colliding players.");
    		t.getCollidingPlayers().clear();
    		
        	// check if this position is a destination for any transactions
    		List<String> remove = new ArrayList<>();
        	if (t.getTransactions() != null && t.getTransactions().size() > 0) {
        		// process each transaction
        		for (Entry<String, TeleportTransaction> e : t.getTransactions().entrySet()) {
        			TeleportTransaction trans = e.getValue();
        			// if this TE is a destination in the transaction
        			if (trans.getDest().equals(new Coords(pos))) {
        				remove.add(e.getKey());
        			}
        		}
                TeleportPadTileEntity destTE = (TeleportPadTileEntity) worldIn.getTileEntity(t.getLink().toPos());
        		// remove all found transactions from both TEs
        		for (String key : remove) {
        			t.getTransactions().remove(key);
        			destTE.getTransactions().remove(key);
        		}
        	}
        }
        else {
        	// players still colliding
        	List<String> remaining = new ArrayList<>();
        	for (Entity e : list) {
        		String entityName = e.getName();
        		if (t.getCollidingPlayers().contains(entityName)) remaining.add(entityName);
//        		FastLadder.log.debug("Player " + entityName + " is in the AABB");
        	}
        	
        	// clear the receiving player list
        	t.getCollidingPlayers().clear();
//        	FastLadder.log.debug("Cleared the colliding list...");
        	// if no one is remaining, exit without scheduling an update
        	if (remaining.size() == 0) {
//            	FastLadder.log.debug("No one is remaining.");
        	}        	
        	// otherwise add back the remaining list
        	else {
//        		FastLadder.log.debug("Size of remaing:" + remaining.size());
        		t.getCollidingPlayers().addAll(remaining);
//            	FastLadder.log.debug("Added remaining list to receiving list.");
//            	FastLadder.log.debug("Size of colliding:" + t.getCollidingPlayers().size());
        	}
        	
        	// remove the transactions for the missing people
        	Map<String, TeleportTransaction> newMap = new HashMap<>();
        	for (String key : remaining) {
        		if (t.getTransactions().containsKey(key)) {
        			newMap.put(key, t.getTransactions().get(key));
        		}
        	}
        	t.setTransactions(newMap);
        	TeleportPadTileEntity destTE = (TeleportPadTileEntity) worldIn.getTileEntity(t.getLink().toPos());
        	destTE.setTransactions(newMap);
        	
        	worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        }
    }
    
    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        playerIn.openGui(FastLadder.instance, GuiHandler.TELEPORT_PAD_GUIID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
    }
    
    /**
     * Add particles from the teleporter
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 3; ++i) {
            int j = rand.nextInt(2) * 2 - 1;
            int k = rand.nextInt(2) * 2 - 1;
            double d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
            double d1 = (double)((float)pos.getY() + rand.nextFloat());
            double d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
            double d3 = (double)(rand.nextFloat() * (float)j);
            double d4 = ((double)rand.nextFloat() - 0.5D) * 0.125D;
            double d5 = (double)(rand.nextFloat() * (float)k);
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }
    
	/**
	 * Tests whether the tile entity at the coords is a) a teleport ladder and b) is already linked.
	 * @param world
	 * @param coords
	 * @return
	 */
	private boolean isUnlinkedTeleportLadder(World world, ICoords coords) {
    	TileEntity t = world.getTileEntity(coords.toPos());
    	if (t instanceof TeleportPadTileEntity) {
    		// check if the discovered teleport ladder is already linked (and valid link)
    		return !((TeleportPadTileEntity)t).isLinked();
    	}
    	return false;
	}
	
	// the block will render in the SOLID layer.  See http://greyminecraftcoder.blogspot.co.at/2014/12/block-rendering-18.html for more information.
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	// used by the renderer to control lighting and visibility of other blocks.
	// set to false because this block doesn't fill the entire 1x1x1 space
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// used by the renderer to control lighting and visibility of other blocks, also by
	// (eg) wall or fence to control whether the fence joins itself to this block
	// set to false because this block doesn't fill the entire 1x1x1 space
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}
	
	// render using a BakedModel
	// not required because the default (super method) is MODEL
	@Override
	public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
		return EnumBlockRenderType.MODEL;
	}
	
	/**
	 * 
	 */
	@Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {/*FACING, */LINK, PLACEMENT});
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
	@Override
    public IBlockState getStateFromMeta(int meta) {
//        IBlockState state = this.getDefaultState().withProperty(LINK, (meta & 4) != 0 ? Link.LINKED : Link.UNLINKED);
//        state.withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
		
      IBlockState state = this.getDefaultState().withProperty(LINK, (meta & 2) != 0 ? Link.LINKED : Link.UNLINKED);
      state = state.withProperty(PLACEMENT, (meta & 1) != 0 ? Placement.TOP : Placement.BOTTOM);
      
      FastLadder.log.debug("Link state is: " + ((meta & 2) != 0 ? Link.LINKED : Link.UNLINKED + " : " + (meta&2)));
      FastLadder.log.debug("Placement state is: " + ((meta & 1) != 0 ? Placement.TOP : Placement.BOTTOM + " : " + (meta&1)));
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
	@Override
    public int getMetaFromState(IBlockState state) {
    	int meta = 0;
    	
//    	if (state.getValue(LINK) == Link.LINKED) {
//    		meta |= 4;
//    	}
//    	meta = meta | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
    	
    	FastLadder.log.debug("State value of LINK: " + state.getValue(LINK));
    	if (state.getValue(LINK) == Link.LINKED) {
    		meta |= 2;
    	}
		meta = meta | ((Placement)state.getValue(PLACEMENT)).getValue();
	      FastLadder.log.debug("Block meta is: " + meta);
    	return meta;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
//	@Override
//    public IBlockState withRotation(IBlockState state, Rotation rot) {
//        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
//    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
//	@Override
//    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
//        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
//    }
    
	
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
    
	/**
	 * 
	 * @author Mark Gottschling onDec 26, 2017
	 *
	 */
	public static enum Link implements IStringSerializable {
		// TODO may have to add index values so the meta <--> property can be easily determined.
		UNLINKED("unlinked", 0),
		LINKED("linked", 1);

	    private final String name;
	    private final int value;
	    
	    private Link(String name, int value) {
	        this.name = name;
	        this.value = value;
	    }
	
	    @Override
		public String toString() {
	        return this.name;
	    }
	
	    @Override
		public String getName() {
	        return this.name;
	    }

		public int getValue() {
			return value;
		}
	}
	
	/**
	 * 
	 * @author Mark Gottschling onJan 3, 2018
	 *
	 */
	public static enum Placement implements IStringSerializable {
		// TODO may have to add index values so the meta <--> property can be easily determined.
		BOTTOM("bottom", 0),
		TOP("top", 1);

	    private final String name;
	    private final int value;
	    
	    private Placement(String name, int value) {
	        this.name = name;
	        this.value = value;
	    }

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}
	}
}
