package net.lax1dude.eaglercraft.v1_8.forge;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ForgeBridge is the primary compatibility layer between modern Forge 1.20.1 
 * mod logic and the Eaglercraft 1.8.8 engine.
 */
public class ForgeBridge {

	private static final Logger logger = LogManager.getLogger("ForgeBridge");
	private static final String TARGET_MINECRAFT_VERSION = "1.20.1";
	private static final String TARGET_FORGE_VERSION = "46.0.0";
	private static final String TARGET_BALM_VERSION = "7.3.28";
	private static final Map<String, String> providedModVersions;
	private static boolean initialized = false;

	static {
		Map<String, String> versions = new HashMap<>();
		versions.put("minecraft", TARGET_MINECRAFT_VERSION);
		versions.put("forge", TARGET_FORGE_VERSION);
		versions.put("balm", TARGET_BALM_VERSION);
		providedModVersions = Collections.unmodifiableMap(versions);
	}

	public static void init() {
		if (initialized) return;
		logger.info("Initializing Forge {} Compatibility Bridge...", TARGET_MINECRAFT_VERSION);
		
		ModernRegistry.init();
		
		ModernEventBus.init();
		
		// Load built-in ported mods
		net.lax1dude.eaglercraft.v1_8.mods.waystones.WaystonesMod.init();
		
		initialized = true;
		logger.info("Forge compatibility modules available: {}", providedModVersions);
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static String getTargetMinecraftVersion() {
		return TARGET_MINECRAFT_VERSION;
	}

	public static String getTargetForgeVersion() {
		return TARGET_FORGE_VERSION;
	}

	public static String getProvidedModVersion(String modId) {
		if (modId == null) {
			return null;
		}
		return providedModVersions.get(modId.toLowerCase());
	}

	public static Map<String, String> getProvidedModVersions() {
		return providedModVersions;
	}

	public static String getRuntimeLoadedWorld() {
		return ForgeDataRuntime.getLoadedWorld();
	}

	public static int getRuntimeRecipesCount() {
		return ForgeDataRuntime.getRecipesCount();
	}

	public static int getRuntimeTagsCount() {
		return ForgeDataRuntime.getTagsCount();
	}

	public static int getRuntimeWorldgenCount() {
		return ForgeDataRuntime.getWorldgenCount();
	}

	public static boolean runtimeTagContains(String tagId, String value) {
		return ForgeDataRuntime.isTagContains(tagId, value);
	}

	public static boolean isModEnabled(String modId) {
		if (modId == null) return false;
		String lowerId = modId.toLowerCase();
		for (String key : ForgeDataRuntime.listMods()) {
			if (key.toLowerCase().startsWith(lowerId + "@") || key.toLowerCase().equals(lowerId)) {
				return true;
			}
		}
		return false;
	}
}
