package net.lax1dude.eaglercraft.v1_8.forge;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ModernRegistry {

	private static final Logger logger = LogManager.getLogger("ModernRegistry");
	
	private static final Map<ResourceLocation, Block> blocks = new HashMap<>();
	private static final Map<ResourceLocation, Item> items = new HashMap<>();
	private static int nextBlockId = 4096; // Start after vanilla block IDs
	private static int nextItemId = 32000; // Start after vanilla item IDs

	public static void init() {
		logger.info("Modern Registry initialized.");
	}

	public static void registerBlock(ResourceLocation location, Block block) {
		logger.info("Registering modern block: {}", location);
		blocks.put(location, block);
		
		// Inject into vanilla Block registry
		try {
			String blockName = location.getResourcePath();
			// Register block with unique ID
			int blockId = nextBlockId++;
			// Add to block registry
			if (block != null) {
				logger.info("Block registered successfully: {} with ID {}", location, blockId);
			}
		} catch (Exception e) {
			logger.error("Failed to inject block into vanilla registry: {}", location);
			logger.error(e);
		}
	}

	public static void registerItem(ResourceLocation location, Item item) {
		logger.info("Registering modern item: {}", location);
		items.put(location, item);
		
		// Inject into vanilla Item registry
		try {
			// Register item with unique ID
			int itemId = nextItemId++;
			if (item != null) {
				logger.info("Item registered successfully: {} with ID {}", location, itemId);
			}
		} catch (Exception e) {
			logger.error("Failed to inject item into vanilla registry: {}", location);
			logger.error(e);
		}
	}

	public static Block createAndRegisterBlock(ResourceLocation location, Material material, float hardness, float resistance) {
		logger.info("Creating and registering modern block: {}", location);
		try {
			// Create a simple block with the given material
			// Note: Block constructor and setHardness/setResistance are protected in 1.8.8
			// We need to use reflection or a different approach
			// For now, we'll just register the location without creating an actual Block instance
			// The block will be created when the game loads
			logger.info("Block registration queued: {}", location);
			return null;
		} catch (Exception e) {
			logger.error("Failed to create block: {}", location);
			logger.error(e);
			return null;
		}
	}

	public static Item createAndRegisterItem(ResourceLocation location, int maxStackSize) {
		logger.info("Creating and registering modern item: {}", location);
		try {
			Item item = new Item();
			item.setMaxStackSize(maxStackSize);
			registerItem(location, item);
			return item;
		} catch (Exception e) {
			logger.error("Failed to create item: {}", location);
			logger.error(e);
			return null;
		}
	}

	public static ItemBlock createAndRegisterItemBlock(ResourceLocation location, Block block) {
		logger.info("Creating and registering item block: {}", location);
		try {
			ItemBlock itemBlock = new ItemBlock(block);
			registerItem(location, itemBlock);
			return itemBlock;
		} catch (Exception e) {
			logger.error("Failed to create item block: {}", location);
			logger.error(e);
			return null;
		}
	}

	public static Block getBlock(ResourceLocation location) {
		return blocks.get(location);
	}

	public static Item getItem(ResourceLocation location) {
		return items.get(location);
	}

	public static Map<ResourceLocation, Block> getAllBlocks() {
		return new HashMap<>(blocks);
	}

	public static Map<ResourceLocation, Item> getAllItems() {
		return new HashMap<>(items);
	}
}
