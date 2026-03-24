package net.lax1dude.eaglercraft.v1_8.forge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ForgeDataRuntime {

	private static final Logger logger = LogManager.getLogger("ForgeDataRuntime");

	private static String loadedWorld = null;
	private static final Map<String, JSONObject> manifestsByMod = new LinkedHashMap<>();
	private static final Map<String, List<String>> filesByCategory = new HashMap<>();
	private static final List<String> allDataFiles = new ArrayList<>();
	private static final Map<String, JSONObject> recipesById = new LinkedHashMap<>();
	private static final Map<String, List<String>> tagsById = new LinkedHashMap<>();
	private static final List<String> worldgenIds = new ArrayList<>();
	private static final Map<String, String> resolvedByKey = new LinkedHashMap<>();
	private static final Map<String, List<String>> resolvedByLogicalPath = new HashMap<>();

	public static synchronized void loadWorld(String worldName) {
		clear();
		loadedWorld = worldName;
		if (worldName == null || worldName.length() == 0) {
			return;
		}
		VFile2 root = new VFile2("mods_translated", worldName);
		if (!root.exists()) {
			logger.info("No translated mods for world '{}'", worldName);
			return;
		}
		List<String> files = root.listFilenames(true);
		for (int i = 0, l = files.size(); i < l; ++i) {
			String path = files.get(i);
			if (path == null || !path.endsWith("/_translation_manifest.json")) {
				continue;
			}
			loadManifest(path);
		}
		logger.info("Loaded translated forge runtime for world '{}': {} mods, {} data files", worldName,
				Integer.valueOf(manifestsByMod.size()), Integer.valueOf(allDataFiles.size()));
	}

	public static synchronized void clear() {
		loadedWorld = null;
		manifestsByMod.clear();
		filesByCategory.clear();
		allDataFiles.clear();
		recipesById.clear();
		tagsById.clear();
		worldgenIds.clear();
		resolvedByKey.clear();
		resolvedByLogicalPath.clear();
	}

	public static synchronized String getLoadedWorld() {
		return loadedWorld;
	}

	public static synchronized int getLoadedModsCount() {
		return manifestsByMod.size();
	}

	public static synchronized int getLoadedDataFilesCount() {
		return allDataFiles.size();
	}

	public static synchronized int getRecipesCount() {
		return recipesById.size();
	}

	public static synchronized int getTagsCount() {
		return tagsById.size();
	}

	public static synchronized int getWorldgenCount() {
		return worldgenIds.size();
	}

	public static synchronized List<String> listMods() {
		return new ArrayList<>(manifestsByMod.keySet());
	}

	public static synchronized JSONObject getManifest(String modKey) {
		JSONObject manifest = manifestsByMod.get(modKey);
		if (manifest == null) {
			return null;
		}
		return new JSONObject(manifest.toString());
	}

	public static synchronized List<String> getFilesByCategory(String category) {
		List<String> files = filesByCategory.get(category);
		if (files == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(files);
	}

	public static synchronized String getTranslatedDataFile(String path) {
		if (loadedWorld == null || path == null || path.length() == 0) {
			return null;
		}
		VFile2 file = new VFile2("mods_translated", loadedWorld, path);
		String chars = file.getAllChars();
		if (chars != null) {
			return chars;
		}
		List<String> resolved = resolvedByLogicalPath.get(path);
		if (resolved == null || resolved.isEmpty()) {
			return null;
		}
		return new VFile2(resolved.get(0)).getAllChars();
	}

	public static synchronized String getTranslatedDataFile(String modKey, String logicalPath) {
		if (modKey == null || logicalPath == null) {
			return null;
		}
		String resolved = resolvedByKey.get(modKey + ":" + logicalPath);
		if (resolved == null) {
			return null;
		}
		return new VFile2(resolved).getAllChars();
	}

	public static synchronized JSONObject getRecipe(String recipeId) {
		JSONObject recipe = recipesById.get(recipeId);
		if (recipe == null) {
			return null;
		}
		return new JSONObject(recipe.toString());
	}

	public static synchronized List<String> findRecipesByOutput(String outputItemId) {
		if (outputItemId == null || outputItemId.length() == 0) {
			return Collections.emptyList();
		}
		List<String> out = new ArrayList<>();
		for (Map.Entry<String, JSONObject> etr : recipesById.entrySet()) {
			JSONObject recipe = etr.getValue();
			String result = extractRecipeResultItem(recipe);
			if (outputItemId.equals(result)) {
				out.add(etr.getKey());
			}
		}
		return out;
	}

	public static synchronized List<String> getTagValues(String tagId) {
		List<String> tagValues = tagsById.get(tagId);
		if (tagValues == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(tagValues);
	}

	public static synchronized boolean isTagContains(String tagId, String value) {
		if (tagId == null || value == null) {
			return false;
		}
		List<String> values = tagsById.get(tagId);
		return values != null && values.contains(value);
	}

	public static synchronized List<String> listWorldgenIds() {
		return new ArrayList<>(worldgenIds);
	}

	public static synchronized void reloadWorldIfActive(String worldName) {
		if (worldName != null && worldName.equals(loadedWorld)) {
			loadWorld(worldName);
		}
	}

	private static void loadManifest(String fullPath) {
		VFile2 manifestFile = new VFile2(fullPath);
		String data = manifestFile.getAllChars();
		if (data == null || data.length() == 0) {
			return;
		}
		JSONObject json;
		try {
			json = new JSONObject(data);
		} catch (Throwable t) {
			logger.error("Invalid translated manifest: {}", fullPath);
			logger.error(t);
			return;
		}
		String modId = json.optString("modId", null);
		String modFile = json.optString("modFile", "unknown.jar");
		String modVersion = json.optString("version", "unknown");
		String key = normalizeModKey(modId, modFile, modVersion);
		manifestsByMod.put(key, json);
		String manifestPrefix = fullPath.substring(0, fullPath.length() - "/_translation_manifest.json".length());
		JSONArray files = json.optJSONArray("files");
		if (files == null) {
			return;
		}
		for (int i = 0, l = files.length(); i < l; ++i) {
			String p = files.optString(i, null);
			if (p == null || p.length() == 0) {
				continue;
			}
			allDataFiles.add(p);
			String category = classifyDataPath(p);
			if (category != null) {
				List<String> list = filesByCategory.get(category);
				if (list == null) {
					list = new ArrayList<>();
					filesByCategory.put(category, list);
				}
				list.add(p);
			}
			String resolvedPath = manifestPrefix + "/" + p;
			String resolvedKey = key + ":" + p;
			resolvedByKey.put(resolvedKey, resolvedPath);
			List<String> resolvedList = resolvedByLogicalPath.get(p);
			if (resolvedList == null) {
				resolvedList = new ArrayList<>();
				resolvedByLogicalPath.put(p, resolvedList);
			}
			resolvedList.add(resolvedPath);
			ingestDataFile(key, p, resolvedPath);
		}
	}

	private static String normalizeModKey(String modId, String modFile, String version) {
		String base = modId != null && modId.length() > 0 ? modId : modFile;
		if (base == null || base.length() == 0) {
			base = "unknown_mod";
		}
		String suffix = version == null || version.length() == 0 ? "unknown" : version;
		return base + "@" + suffix;
	}

	private static String classifyDataPath(String path) {
		if (path.contains("/recipes/")) {
			return "recipes";
		}
		if (path.contains("/tags/")) {
			return "tags";
		}
		if (path.contains("/worldgen/")) {
			return "worldgen";
		}
		return null;
	}

	private static void ingestDataFile(String modKey, String logicalPath, String resolvedPath) {
		if (!logicalPath.endsWith(".json")) {
			return;
		}
		String content = new VFile2(resolvedPath).getAllChars();
		if (content == null || content.length() == 0) {
			return;
		}
		JSONObject json;
		try {
			json = new JSONObject(content);
		} catch (Throwable t) {
			return;
		}
		if (logicalPath.contains("/recipes/")) {
			String recipeId = resourceIdFromDataPath(logicalPath, "/recipes/");
			if (recipeId != null) {
				recipesById.put(recipeId, json);
			}
			return;
		}
		if (logicalPath.contains("/tags/")) {
			String tagId = resourceIdFromDataPath(logicalPath, "/tags/");
			if (tagId == null) {
				return;
			}
			JSONArray values = json.optJSONArray("values");
			if (values == null) {
				return;
			}
			List<String> out = new ArrayList<>();
			for (int i = 0, l = values.length(); i < l; ++i) {
				String v = values.optString(i, null);
				if (v != null && v.length() > 0) {
					out.add(v);
				}
			}
			tagsById.put(tagId, out);
			return;
		}
		if (logicalPath.contains("/worldgen/")) {
			String worldgenId = resourceIdFromDataPath(logicalPath, "/worldgen/");
			if (worldgenId != null) {
				worldgenIds.add(worldgenId);
			}
		}
	}

	private static String extractRecipeResultItem(JSONObject recipe) {
		JSONObject resultObj = recipe.optJSONObject("result");
		if (resultObj != null) {
			String item = resultObj.optString("item", null);
			if (item != null && item.length() > 0) {
				return item;
			}
		}
		String resultStr = recipe.optString("result", null);
		if (resultStr != null && resultStr.length() > 0) {
			return resultStr;
		}
		return null;
	}

	private static String resourceIdFromDataPath(String path, String marker) {
		if (path == null || marker == null) {
			return null;
		}
		if (!path.startsWith("data/")) {
			return null;
		}
		int markerIndex = path.indexOf(marker);
		if (markerIndex == -1) {
			return null;
		}
		String namespace = path.substring(5, markerIndex);
		if (namespace.length() == 0) {
			return null;
		}
		int relStart = markerIndex + marker.length();
		if (relStart >= path.length()) {
			return null;
		}
		String rel = path.substring(relStart);
		if (rel.endsWith(".json")) {
			rel = rel.substring(0, rel.length() - 5);
		}
		if (rel.length() == 0) {
			return null;
		}
		return namespace + ":" + rel;
	}
}
