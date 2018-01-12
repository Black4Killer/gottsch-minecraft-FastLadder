package com.someguyssoftware.fastladder.block;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.someguyssoftware.fastladder.FastLadder;
import com.someguyssoftware.fastladder.block.material.FastMaterial;
import com.someguyssoftware.fastladder.block.material.FasterMaterial;
import com.someguyssoftware.fastladder.block.material.FastestMaterial;
import com.someguyssoftware.fastladder.block.material.TeleportMaterial;
import com.someguyssoftware.fastladder.config.FastLadderConfig;
import com.someguyssoftware.fastladder.tileentity.TeleportPadTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * 
 * @author Mark Gottschling on Jul 15, 2017
 *
 */
public class FastLadderBlocks {
	// materials
	public static final Material FAST_MATERIAL = new FastMaterial(MapColor.AIR);
	public static final Material FASTER_MATERIAL = new FasterMaterial(MapColor.GOLD);
	public static final Material FASTEST_MATERIAL = new FastestMaterial(MapColor.DIAMOND);
	public static final Material TELEPORT_MATERIAL = new TeleportMaterial(MapColor.EMERALD);
	
	// blocks
	public static final Block FAST_LADDER = new FastLadderBlock(FastLadder.MODID, FastLadderConfig.fastLadderBlockID, FAST_MATERIAL)
			.setHardness(0.6F);

	public static final Block FASTER_LADDER = new FastLadderBlock(FastLadder.MODID, FastLadderConfig.fasterLadderBlockID, FASTER_MATERIAL)
			.setHardness(0.4F);
	
	public static final Block FASTEST_LADDER = new FastLadderBlock(FastLadder.MODID, FastLadderConfig.fastestLadderBlockID, FASTEST_MATERIAL)
			.setHardness(0.8F);

	public static final Block TELEPORT_PAD = new TeleportPadBlock(FastLadder.MODID, FastLadderConfig.teleportPadBlockID, TELEPORT_MATERIAL)
			.setHardness(1.0F);
	
//	public static final Block TELEPORT_LADDER = 
//			new TeleportLadderBlock(FastLadder.MODID, FastLadderConfig.teleportLadderBlockID)
//				.setCreativeTab(CreativeTabs.MISC);
	
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
					FASTEST_LADDER,
					TELEPORT_PAD					
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
					new ItemBlock(FASTEST_LADDER),
					new ItemBlock(TELEPORT_PAD
				)
			};
			
			for (final ItemBlock item : items) {
				final Block block = item.getBlock();
				final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
				registry.register(item.setRegistryName(registryName));
				ITEM_BLOCKS.add(item);
			}
			
			// register the tile entities
//			GameRegistry.registerTileEntity(TeleportLadderTileEntity.class, "teleportLadderTileEntity");
			GameRegistry.registerTileEntity(TeleportPadTileEntity.class, FastLadderConfig.teleportPadTileEntityID);
		}
	}
}
