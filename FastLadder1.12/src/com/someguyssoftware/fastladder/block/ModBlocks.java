package com.someguyssoftware.fastladder.block;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.block.material.FastMaterial;
import com.someguyssoftware.fastladder.block.material.FasterMaterial;
import com.someguyssoftware.fastladder.block.material.FastestMaterial;
import com.someguyssoftware.fastladder.config.FastLadderConfig;
import com.someguyssoftware.fastladder.config.FastLadderConfig;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * 
 * @author Mark Gottschling on Jul 15, 2017
 *
 */
// NOTE not sure that @ObjectHolder is necessary here are the blocks are set on intialization
@ObjectHolder(FastLadder.MODID)
public class ModBlocks {
	// materials
	public static Material FAST_MATERIAL = new FastMaterial(MapColor.AIR);
	public static Material FASTER_MATERIAL = new FasterMaterial(MapColor.GOLD);
	public static Material FASTEST_MATERIAL = new FastestMaterial(MapColor.DIAMOND);

	// blocks
	public static Block FAST_LADDER = new FastLadderBlock()
			.setHardness(0.6F)
			.setUnlocalizedName(((FastLadderConfig)FastLadder.instance.getConfig()).getFastLadderBlockId())
			.setRegistryName(((FastLadderConfig)FastLadder.instance.getConfig()).getFastLadderBlockId());
	
	public static Block FASTER_LADDER = new FastLadderBlock(FASTER_MATERIAL)
			.setHardness(0.4F)
			.setUnlocalizedName(((FastLadderConfig)FastLadder.instance.getConfig()).getFasterLadderBlockId())
			.setRegistryName(((FastLadderConfig)FastLadder.instance.getConfig()).getFasterLadderBlockId());
	
	public static Block FASTEST_LADDER = new FastLadderBlock(FASTEST_MATERIAL)
			.setHardness(0.8F)
			.setUnlocalizedName(((FastLadderConfig)FastLadder.instance.getConfig()).getFastestLadderBlockId())
			.setRegistryName(((FastLadderConfig)FastLadder.instance.getConfig()).getFastestLadderBlockId());

	
	@Mod.EventBusSubscriber(modid = FastLadder.MODID)
	public static class RegistrationHandler {
		public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();
		
		/**
		 * Register this mod's {@link Block}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			final IForgeRegistry<Block> registry = event.getRegistry();

			final Block[] blocks = {
					FAST_LADDER,
					FASTER_LADDER,
					FASTEST_LADDER
			};
			registry.registerAll(blocks);			
		}
		
		/**
		 * Register this mod's {@link ItemBlock}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
			
			final ItemBlock[] items = {
					new ItemBlock(FAST_LADDER),
					new ItemBlock(FASTER_LADDER),
					new ItemBlock(FASTEST_LADDER)
			};
			
			for (final ItemBlock item : items) {
				final Block block = item.getBlock();
				final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
				registry.register(item.setRegistryName(registryName));
				ITEM_BLOCKS.add(item);
			}
		}
	}
}
