package com.someguyssoftware.fastladder.block;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.tileentity.TeleportLadderContraptionTileEntity;
import com.someguyssoftware.fastladder.tileentity.TeleportLadderTileEntity;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Dec 12, 2017
 *
 */@Deprecated
class TeleportLadderBlock extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyEnum<Link> LINK = PropertyEnum.create("link", Link.class);
			
	// custom bounding boxes for the different directions the block faces.
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    
    /** This bounding box is used to check for entities in a certain area and then determine the pressed state. */
    // TODO this should be in the inner pad
    protected static final AxisAlignedBB PAD_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

    
	/**
	 * 
	 */
	public TeleportLadderBlock(String modID, String name) {
		super(Material.IRON);
		setBlockName(modID, name);
		this.setDefaultState(this.blockState
				.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(LINK, Link.UNLINKED));
        this.setTickRandomly(true);
	}
	
	/**
	 * 
	 * @param material
	 */
	public TeleportLadderBlock(String modID, String name, Material material) {
		super(material);
		setBlockName(modID, name);
		this.setDefaultState(this.blockState
				.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(LINK, Link.UNLINKED));
        this.setTickRandomly(true);
	}

	/**
	 * 
	 */
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;		
	}
	
    /**
     * How many world ticks before ticking
     */
	@Override
    public int tickRate(World worldIn) {
        return 20;
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }
    
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /**
     * 
     */
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    /**
     * Determines if an entity can path through this block
     */
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }
    
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
    
	// TODO
	public boolean hasFuel() {
		return false;
	}
	
	// TODO
	public void removeFuel(int count) {
		
	}
	
	// TODO
	public void addFuel(int count) {
		
	}
	
	/**
	 * 
	 * @param modID
	 * @param name
	 */
	public void setBlockName(String modID, String name) {
		setRegistryName(modID, name);
		setUnlocalizedName(getRegistryName().toString());
		
	}
	
	/**
	 * 
	 */
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING, LINK});
    }
    
	/**
	 * 
	 */
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		FastLadder.log.debug("Creating TE");
		return new TeleportLadderTileEntity();
	}
    
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    
	/**
	 * 
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		ICoords thisCoords = new Coords(pos);
		
		FastLadder.log.debug("Placing block @ " + thisCoords.toShortString());
		
		// face the teleport ladder towards the palyer (there isn't really a front)
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        // set the custom inventory name of the teleport ladder
        if (stack.hasDisplayName()) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TeleportLadderTileEntity) {
                ((TeleportLadderTileEntity)tileentity).setCustomInventoryName(stack.getDisplayName());
                TileEntityFurnace f;
            }
        }
        
        // seach up until the max, then to the bottom until min, searching for another Contraption
        boolean foundTeleportLadder = false;
        ICoords targetCoords = thisCoords.up(1);
        
        while (targetCoords.getY() < world.getHeight() && !foundTeleportLadder) {
        	if (isUnlinkedTeleportLadder(world, targetCoords)) {
        		foundTeleportLadder = true;
        		FastLadder.log.debug("Found it! @ " + targetCoords.toShortString());
        	}
        	else {        		
        		targetCoords = targetCoords.up(1);
//        		FastLadder.log.debug("going up to:" + coords.getY());
        	}
        }
        
        if (!foundTeleportLadder) {
        	targetCoords = thisCoords.down(1);
        	while (targetCoords.getY() > 1 && !foundTeleportLadder) {
            	if (isUnlinkedTeleportLadder(world, targetCoords)) {
            		foundTeleportLadder = true;
            		FastLadder.log.debug("Found it! @ " + targetCoords.toShortString());
            	}
            	else {
            		targetCoords = targetCoords.down(1);
//            		FastLadder.log.debug("going down to:" + coords.getY());
            	}
        	}
        }
        
        if (foundTeleportLadder) {
    		// update the linked property of the backing title entity
    		TeleportLadderTileEntity te1 = ((TeleportLadderTileEntity)world.getTileEntity(pos));
    		te1.setLink(targetCoords);
    		world.setBlockState(pos, state.withProperty(LINK, Link.LINKED), 2);
    		FastLadder.log.debug("Is TE1 Linked:" + te1.isLinked());
    		FastLadder.log.debug("TE1.link:" + te1.getLink());
    		
    		// update the discovered TeleportLadder with this coords
    		TeleportLadderTileEntity te2 = ((TeleportLadderTileEntity)world.getTileEntity(targetCoords.toPos()));
    		te2.setLink(thisCoords);
    		world.setBlockState(targetCoords.toPos(), state.withProperty(LINK, Link.LINKED), 2);
    		FastLadder.log.debug("Is TE2 Linked:" + te2.isLinked());
    		FastLadder.log.debug("TE2.link:" + te2.getLink());
    		
    		FastLadder.log.debug("Linked with: " + targetCoords.toShortString());
    		FastLadder.log.debug("Target Linked with: " + te2.getLink().toShortString());
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
    	if (t instanceof TeleportLadderTileEntity) {
    		// check if the discovered teleport ladder is already linked (and valid link)
    		return !((TeleportLadderTileEntity)t).isLinked();
    	}
    	return false;
	}
	
    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        else {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            // TODO cheange to TeleportLadderTileEntity and implement the gui 
            if (tileentity instanceof TileEntityFurnace) {
                playerIn.displayGUIChest((TileEntityFurnace)tileentity);
//                playerIn.addStat(StatList.FURNACE_INTERACTION);
            }

            return true;
        }
    }
    
    /**
     * Called after x number of ticks. If the receiving list has values then update the state.
     */
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
        	FastLadder.log.debug("Updating Tick");
        	// if the receiving player list has values
        	TeleportLadderTileEntity t = (TeleportLadderTileEntity) worldIn.getTileEntity(pos);
        	if (t != null) {
        		if (t.getReceivingPlayerList() != null && t.getReceivingPlayerList().size() > 0) {
        			FastLadder.log.debug("Updating Tick - calling update state");
        			this.updateState(worldIn, pos, state);
        		}
            }
        }
    }
    
    /**
     * Called When an Entity Collided with the Block. Teleport and flag target.
     */
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {
        	// TODO test if ladder has fuel/enough fuel
        	// TODO test if ladder is linked
        	FastLadder.log.debug("Colliding with teleport block @ " + pos);
        	
        	// get the backing tile entity
        	TeleportLadderTileEntity t = (TeleportLadderTileEntity) worldIn.getTileEntity(pos);
        	
        	FastLadder.log.debug("is linked: " + t.isLinked());
        	FastLadder.log.debug("te.link: " + t.getLink());
        	FastLadder.log.debug("receiving list:");
        	for (String s : t.getReceivingPlayerList()) {
        		FastLadder.log.debug("name: " + s);
        	}
        	
        	// test if the entity is a player
        	if (t != null && t.isLinked() && entityIn instanceof EntityPlayer) {
        		// test if the player is on the receiving list ie. player arrived by teleportation from linked teleportladder and hasn't moved off yet
        		if (!t.getReceivingPlayerList().contains(entityIn.getName())) {
        			FastLadder.log.debug("receiving list doesn't contain name: " + entityIn.getName());
        			// get the target teleportladder tile entity
        			TeleportLadderTileEntity target = (TeleportLadderTileEntity) worldIn.getTileEntity(t.getLink().toPos());
        			if (target != null) {
        				FastLadder.log.debug("Target TeleportLadder @ " + t.getLink().toShortString());
        				FastLadder.log.debug("adding entity name to target receiving list.");
        				target.getReceivingPlayerList().add(entityIn.getName());
        				// teleport user        				
        	        	entityIn.setPosition(target.getLink().getX(), target.getLink().getY(), target.getLink().getZ());
        	        	// update the target state ??
        	        	FastLadder.log.debug("Player *should* be @ " + target.getLink().toShortString());
        			}
        		}
	        	// update the state
	            this.updateState(worldIn, pos, state);
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
        
        FastLadder.log.debug("Updating state");
        
        // get the tile entity
        TeleportLadderTileEntity t = (TeleportLadderTileEntity) worldIn.getTileEntity(pos);
        
        if (list == null || list.size() == 0) {
        	FastLadder.log.debug("AABB list is empty. Clearing receiving list.");
        	// clear the receiving player list from tile entity        	
        	t.getReceivingPlayerList().clear();
        }
        else {
        	// TODO test who is still on the pad
        	List<String> remaining = new ArrayList<>();
        	for (Entity e : list) {
        		String entityName = e.getName();
        		if (t.getReceivingPlayerList().contains(entityName)) remaining.add(entityName);
        	}
        	// clear the receiving player list
        	t.getReceivingPlayerList().clear();
        	
        	// if no one is remaining, exit without scheduling an update
        	if (remaining.size() == 0) return;
        	// otherwise add back the remaining list
        	else t.getReceivingPlayerList().addAll(remaining);
        	
        	// schedule another update
        	worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        }
    }
    
    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
       TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TeleportLadderTileEntity) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TeleportLadderTileEntity)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
            
            // unlinked target teleport ladder
            TeleportLadderTileEntity t = (TeleportLadderTileEntity)tileentity;
            if (t.isLinked()) {
            	// get the target tile entity
            	TeleportLadderTileEntity target = (TeleportLadderTileEntity) worldIn.getTileEntity(t.getLink().toPos());
            	if (target != null) {
            		// TODO could replace the next 3 lines with a single method resetTeleportState();
            		target.setLink(null);
            		target.getReceivingPlayerList().clear();
            	}
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    /**
     * Derived from BlockFurance
     */
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    /**
     * Derived from BlockFurnace
     */
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    /**
     * 
     */
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(FastLadderBlocks.TELEPORT_LADDER);
    }
    
    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(LINK, (meta & 4) != 0 ? Link.LINKED : Link.UNLINKED);
        state.withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
    	int meta = 0;
    	if (state.getValue(LINK) == Link.LINKED) {
    		meta |= 4;
    	}

    	meta = meta | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
    	
    	return meta;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    /**
     * 
     * @author Mark Gottschling on Dec 12, 2017
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
}
