package com.someguyssoftware.fastladder.block;


import com.someguyssoftware.fastladder.tileentity.TeleportLadderContraptionTileEntity;
import com.someguyssoftware.fastladder.tileentity.TeleportLadderTileEntity;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Dec 12, 2017
 *
 */
//class TeleportLadderBlock extends BlockContainer {
class TeleportLadderBlock extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyEnum<Link> LINK = PropertyEnum.create("link", Link.class);
			
	// custom bounding boxes for the different directions the block faces.
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

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
		
		// face the contraption towards the palyer
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        // set the custom inventory name of the contraption
        if (stack.hasDisplayName()) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityFurnace) {
                ((TileEntityFurnace)tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }
        
        // seach up until the max, then to the bottom until min, searching for another Contraption
        boolean foundTeleportLadder = false;
        ICoords coords = thisCoords.up(1);
        
        while (coords.getY() < world.getHeight() && !foundTeleportLadder) {
        	if (isUnlinkedTeleportLadder(world, coords)) {
        		foundTeleportLadder = true;
        	}
        	else {        		
        		coords = coords.up(1);
        	}
        }
        if (!foundTeleportLadder) {
        	coords = thisCoords.down(1);
        	while (coords.getY() > 1 && !foundTeleportLadder) {
            	if (isUnlinkedTeleportLadder(world, coords)) {
            		foundTeleportLadder = true;
            	}
            	else {
            		coords = coords.down(1);
            	}
        	}
        }
        
        if (foundTeleportLadder) {
    		// update the linked property of the backing title entity
    		((TeleportLadderTileEntity)world.getTileEntity(pos)).setLink(coords);
    		// update the discovered TeleportLadder with this coords
    		((TeleportLadderTileEntity)world.getTileEntity(coords.toPos())).setLink(thisCoords);
        }        
	}
	
	/**
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	private boolean isUnlinkedTeleportLadder(World world, ICoords coords) {
    	TileEntity t = world.getTileEntity(coords.toPos());
    	if (t instanceof TeleportLadderTileEntity) {
    		// TODO check if the discovered teleport ladder is already linked (and valid link)

    		return true;
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

            if (tileentity instanceof TileEntityFurnace) {
                playerIn.displayGUIChest((TileEntityFurnace)tileentity);
//                playerIn.addStat(StatList.FURNACE_INTERACTION);
            }

            return true;
        }
    }
    
    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
       TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TeleportLadderContraptionTileEntity) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TeleportLadderContraptionTileEntity)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
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
