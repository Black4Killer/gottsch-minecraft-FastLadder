/**
 * 
 */
package com.someguyssoftware.fastladder.proxy;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.block.FastLadderBlocks;
import com.someguyssoftware.fastladder.config.FastLadderConfig;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.proxy.AbstractClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * TODO remove rendering and move to another class that is registered by events
 * 
 * @author Mark Gottschling on Jul 15, 2017
 *
 */
public class ClientProxy extends AbstractClientProxy {

	@Override
	public void registerRenderers(IConfig config) {
		registerItemRenderers((FastLadderConfig) config);
	}
	
	/**
	 * 
	 * @param config
	 */
	public void registerItemRenderers(FastLadderConfig config) {
		// register item renderers
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(FastLadderBlocks.FAST_LADDER), 0, new ModelResourceLocation(FastLadder.MODID + ":" + config.getFastLadderBlockID(), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(FastLadderBlocks.FASTER_LADDER), 0, new ModelResourceLocation(FastLadder.MODID + ":" + config.getFasterLadderBlockID(), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(FastLadderBlocks.FASTEST_LADDER), 0, new ModelResourceLocation(FastLadder.MODID + ":" + config.getFastestLadderBlockID(), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(FastLadderBlocks.TELEPORT_PAD), 0, new ModelResourceLocation(FastLadder.MODID + ":" + config.getTeleportPadBlockID(), "inventory"));


	}
}
