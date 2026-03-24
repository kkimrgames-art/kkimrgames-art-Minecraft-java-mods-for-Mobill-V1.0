package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.nio.charset.StandardCharsets;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerSaveFormat;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class WorldSyncManager {

	private static final Logger logger = LogManager.getLogger("WorldSyncManager");

	public WorldSyncManager() {
	}

	public static void init() {
		logger.info("Initializing World Sync...");
		PlatformRuntime.downloadRemoteURIByteArray("/test-worlds/worlds_list.txt", (bytes) -> {
			if (bytes != null) {
				String content = new String(bytes, StandardCharsets.UTF_8);
				String[] worlds = content.split("\n");
				for (String worldName : worlds) {
					worldName = worldName.trim();
					if (worldName.length() > 0) {
						syncWorld(worldName);
					}
				}
			} else {
				logger.info("No remote worlds found to sync.");
			}
		});
	}

	private static void syncWorld(String worldName) {
		VFile2 levelDat = new VFile2("worlds", worldName, "level.dat");
		if (!levelDat.exists()) {
			logger.info("Syncing new world: " + worldName);
			downloadFile("/test-worlds/" + worldName + "/level.dat", levelDat);

			syncWorldMods(worldName);
			updateWorldsList(worldName);
		} else {
			syncWorldMods(worldName);
		}
	}

	private static void syncWorldMods(String worldName) {
		String modsListUrl = "/test-worlds/" + worldName + "/mods/mods_list.txt";
		PlatformRuntime.downloadRemoteURIByteArray(modsListUrl, (bytes) -> {
			if (bytes == null) {
				downloadFallbackWaystones(worldName);
				return;
			}
			String[] lines = new String(bytes, StandardCharsets.UTF_8).split("\n");
			boolean foundAny = false;
			for (String line : lines) {
				String fileName = line.trim();
				if (fileName.length() == 0 || fileName.contains("/") || fileName.contains("\\")) {
					continue;
				}
				foundAny = true;
				downloadFile("/test-worlds/" + worldName + "/mods/" + fileName, new VFile2("mods", worldName, fileName));
			}
			if (!foundAny) {
				downloadFallbackWaystones(worldName);
			}
		});
	}

	private static void downloadFallbackWaystones(String worldName) {
		String fallback = "waystones-forge-1.20.1-14.1.18.jar";
		downloadFile("/test-worlds/" + worldName + "/mods/" + fallback, new VFile2("mods", worldName, fallback));
	}

	private static void downloadFile(String url, VFile2 dest) {
		PlatformRuntime.downloadRemoteURIByteArray(url, (bytes) -> {
			if (bytes != null) {
				dest.setAllBytes(bytes);
				logger.info("Downloaded: " + url + " -> " + dest.getPath());
			} else {
				logger.error("Failed to download: " + url);
			}
		});
	}

	private static void updateWorldsList(String worldName) {
		VFile2 listFile = EaglerSaveFormat.worldsList;
		String content = listFile.exists() ? listFile.getAllChars() : "";
		if (!content.contains(worldName)) {
			if (content.length() > 0 && !content.endsWith("\n")) {
				content += "\n";
			}
			content += worldName + "\n";
			listFile.setAllChars(content);
		}
	}
}
