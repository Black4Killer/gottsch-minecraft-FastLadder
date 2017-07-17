/**
 * 
 */
package com.someguyssoftware.fastladder.proxy;

import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.block.ModBlocks;
import com.someguyssoftware.fastladder.config.FastLadderConfig;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.proxy.AbstractClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
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
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.FAST_LADDER), 0, new ModelResourceLocation(FastLadder.MODID + ":" + config.getFastLadderBlockId(), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.FASTER_LADDER), 0, new ModelResourceLocation(FastLadder.MODID + ":" + config.getFasterLadderBlockId(), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.FASTEST_LADDER), 0, new ModelResourceLocation(FastLadder.MODID + ":" + config.getFastestLadderBlockId(), "inventory"));

	}
}
