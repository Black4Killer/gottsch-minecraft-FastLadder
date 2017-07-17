/**
 * 
 */
package com.someguyssoftware.fastladder.eventhandler;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.block.ModBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * @author Mark Gottschling on Jul 15, 2017
 *
 */
@Mod.EventBusSubscriber(modid = FastLadder.MODID)
public class PlayerEventHandler {
	public static float FAST_SPEED = 0.06F + 0.03F;
	public static float FASTER_SPEED = 0.1176F + 0.03F;
	public static float FASTEST_SPEED = 0.176F + 0.03F;
	
	@SubscribeEvent
	public void onClimbFastLadder(LivingUpdateEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {
			// get the player
			EntityPlayer player = (EntityPlayer) event.getEntity();
			// get the world
			World world = player.getEntityWorld();
			
			// round down the player's position
			int x = (int) Math.floor(player.posX);
			int y = (int) Math.floor(player.posY);
			int z = (int) Math.floor(player.posZ);
			BlockPos pos = new BlockPos(x, y, z);
			
			// get the block at that position
			IBlockState state = world.getBlockState(pos);

//			FastLadder.log.info("Is Player Entity.");
//			FastLadder.log.info("Is On Ladder:" + player.isOnLadder());
//			FastLadder.log.info("Block's Material:" + state.getBlock().getMaterial()) ;
			
			// if on a ladder and the ladder is made from the 'fast' material
			if (player.isOnLadder()) {
				// record the player's Y motion
				double playerMotion = player.motionY;
					if (state.getMaterial() == ModBlocks.FAST_MATERIAL) {
//						FastLadder.log.info("FAST");
						//moveOnLadder(player);
						//moveOnLadderDoubleSpeed(player);
						moveOnLadder(player, FAST_SPEED); // = 1.5x
						
						//reset the player's motion
						player.motionY = playerMotion;
						
						// set the entity speed attribute
						// NOTE this doesn't affect ladder climb speed!
						//player.capabilities.setPlayerWalkSpeed(player.capabilities.getWalkSpeed() * 2f);
					}
					else if (state.getMaterial() == ModBlocks.FASTER_MATERIAL) {
						//FastLadder.log.info("FASTER");
						moveOnLadder(player, FASTER_SPEED); // = 2x
						//reset the player's motion
						player.motionY = playerMotion;
					}
					else if (state.getMaterial() == ModBlocks.FASTEST_MATERIAL) {
						//FastLadder.log.info("FASTEST");
						moveOnLadder(player, FASTEST_SPEED); // =2.5x
						//reset the player's motion
						player.motionY = playerMotion;
					}
			}
			
//			else if (isSpeedEffectOn) {
//				FastLadder.log.info("SpeedEffect is ON.");
//				// reset the entity speed attribute
//				player.capabilities.setPlayerWalkSpeed(player.capabilities.getWalkSpeed() / 2f);
//				FastLadder.log.info("Reduced Player Walk Speed to Normal.");
//				// turn off the flag
//				isSpeedEffectOn = false;
//				FastLadder.log.info("Set SpeedEffect to OFF.");
//			}
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param speed
	 */
	public void moveOnLadder(EntityPlayer player, float speed) {
		float motion = 0.15F;
		//FastLadder.log.info("Player.motionY IN:" + player.motionY);
        player.motionX = MathHelper.clamp(player.motionX, (double)(-motion), (double)motion);
        player.motionZ = MathHelper.clamp(player.motionZ, (double)(-motion), (double)motion);
        player.fallDistance = 0.0F;
		
        if (player.motionY < -((double)motion)) {
            player.motionY = -((double)motion);
        }
        else if (player.motionY > 0) {
        	//player.motionY = (double)(speed * player.motionY);  //NEW speed = boost factor
        	player.motionY = speed;
        }
        
        boolean flag = player.isSneaking();

        if (flag && player.motionY < 0.0D) {
            player.motionY = 0.0D;
        }

        player.move(MoverType.PLAYER, player.motionX, player.motionY, player.motionZ);
        //FastLadder.log.info("Player.motionY OUT:" + player.motionY);
	}

	/**
	 * This portion is copied from EntityLivingBase. Calling this method effectively double ladder speed.
	 * @param player
	 */
	@Deprecated
	public void moveOnLadderDoubleSpeed(EntityPlayer player) {
        float f5 = 0.15F;
        player.motionX = MathHelper.clamp(player.motionX, (double)(-f5), (double)f5);
        player.motionZ = MathHelper.clamp(player.motionZ, (double)(-f5), (double)f5);
        player.fallDistance = 0.0F;

        if (player.motionY < -0.15D) {
            player.motionY = -0.15D;
        }

        boolean flag = player.isSneaking();

        if (flag && player.motionY < 0.0D) {
            player.motionY = 0.0D;
        }

        player.move(MoverType.PLAYER, player.motionX, player.motionY, player.motionZ);
    }   
}
