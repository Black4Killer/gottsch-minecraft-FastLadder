package com.someguyssoftware.fastladder.block;

import java.util.Iterator;
import java.util.Random;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.gottschcore.block.ModBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * @author Mark Gottschling on Jan 2, 2016
 *
 */
public class FastLadderBlock extends ModBlock {

	//public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    protected static final AxisAlignedBB LADDER_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB LADDER_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB LADDER_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    protected static final AxisAlignedBB LADDER_WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

	/**
	 * 
	 */
	public FastLadderBlock(String modID, String name) {
		// call the super with the material
		super(modID, name, FastLadderBlocks.FAST_MATERIAL);
		// set the default direction to north
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		// add to the creative tab
		this.setCreativeTab(CreativeTabs.MISC);
		this.setSoundType(SoundType.LADDER);
		this.setNormalCube(false);
	}
	
	/**
	 * 
	 * @param material
	 */
	public FastLadderBlock(String modID, String name, Material material) {
		super(modID, name, material);
		// set the default direction to north
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		// add to the creative tab
		this.setCreativeTab(CreativeTabs.MISC);
		this.setSoundType(SoundType.LADDER);
		this.setNormalCube(false);
	}

	/**
	 * 
	 */
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch ((EnumFacing)state.getValue(FACING)) {
		    case NORTH:
		        return LADDER_NORTH_AABB;
		    case SOUTH:
		        return LADDER_SOUTH_AABB;
		    case WEST:
		        return LADDER_WEST_AABB;
		    case EAST:
		    default:
		        return LADDER_EAST_AABB;
		}
	}
	
    /**
     * 
     * @param worldIn
     * @param pos
     * @param facing
     * @return
     */
    protected boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing) {
        return worldIn.getBlockState(pos.offset(facing.getOpposite())).isSideSolid(worldIn, pos.offset(facing.getOpposite()), facing);
    }
    
    /**
     * 
     */
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.west()).isSideSolid(worldIn, pos.west(), EnumFacing.EAST) ||
               worldIn.getBlockState(pos.east()).isSideSolid(worldIn, pos.east(), EnumFacing.WEST) ||
               worldIn.getBlockState(pos.north()).isSideSolid(worldIn, pos.north(), EnumFacing.SOUTH) ||
               worldIn.getBlockState(pos.south()).isSideSolid(worldIn, pos.south(), EnumFacing.NORTH);
    }
    
    /**
     * 
     */
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (facing.getAxis().isHorizontal() && this.canBlockStay(worldIn, pos, facing)) {
            return this.getDefaultState().withProperty(FACING, facing);
        }
        else {
            Iterator iterator = EnumFacing.Plane.HORIZONTAL.iterator();
            EnumFacing enumfacing1;

            do {
                if (!iterator.hasNext()) {
                    return this.getDefaultState();
                }
                enumfacing1 = (EnumFacing)iterator.next();
            }
            while (!this.canBlockStay(worldIn, pos, enumfacing1));

            return this.getDefaultState().withProperty(FACING, enumfacing1);
        }
    }

//    @Override
//    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
//        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
//
//        if (!this.canBlockStay(worldIn, pos, enumfacing)) {
//            this.dropBlockAsItem(worldIn, pos, state, 0);
//            worldIn.setBlockToAir(pos);
//        }
//        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
//    }
    
//    /**
//     * Called when a neighboring block changes.
//     */
//    @Override
//    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
//    	IBlockState state = world.getBlockState(pos);
//    	EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
//  
//        if (!this.canBlockStay((World) world, pos, enumfacing)) {
//            this.dropBlockAsItem((World) world, pos, state, 0);
//            ((World) world).setBlockToAir(pos);            
//        }        
//    	super.onNeighborChange(world, pos, neighbor);
//    }
    
    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_) {

        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

        if (!this.canBlockStay(worldIn, pos, enumfacing))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, p_189540_5_);
    }

    
    /**
     * 
     */
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
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
     */
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
    
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
    
    /**
     * 
     */
//    @Override
//    public boolean isFullCube(IBlockState state) {
//        return false;
//    }
    
    /**
     * 
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
    	return false;
    }
    
    /**
     * 
     */
    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
    	return true;
    }
    
//    static final class SwitchEnumFacing {
//        static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];
//        private static final String __OBFID = "CL_00002104";
//
//        static {
//            try {
//                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 1;
//            }
//            catch (NoSuchFieldError var4) {
//                ;
//            }
//
//            try {
//                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 2;
//            }
//            catch (NoSuchFieldError var3) {
//                ;
//            }
//
//            try {
//                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 3;
//            }
//            catch (NoSuchFieldError var2) {
//                ;
//            }
//
//            try {
//                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 4;
//            }
//            catch (NoSuchFieldError var1) {
//                ;
//            }
//        }
//    }
}
