package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.forge.ForgeBridge;
import net.lax1dude.eaglercraft.v1_8.forge.ForgeDataRuntime;
import net.lax1dude.eaglercraft.v1_8.forge.ModernRegistry;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import org.json.JSONArray;
import org.json.JSONObject;

public class ModManager {

    private static final Logger logger = LogManager.getLogger("ModManager");
    private static final Map<String, Integer> worldModsRevision = new ConcurrentHashMap<>();
    private static final Set<String> softDependencyIds = new HashSet<>(Arrays.asList(
            "architectury", "cloth_config", "cloth-config", "curios", "curios_api", "geckolib", "jade",
            "wthit", "theoneprobe", "patchouli", "resourcefullib", "bookshelf", "citadel", "moonlight", "selene",
            "supermartijn642corelib", "placebo", "kotlinforforge", "owo", "emi", "jei"));
    private static final Map<String, String> itemIdFallbacks;
    private static final Map<String, String> tagFallbacks;

    static {
        Map<String, String> itemMap = new HashMap<>();
        itemMap.put("minecraft:stone_bricks", "minecraft:stonebrick");
        itemMap.put("minecraft:mossy_stone_bricks", "minecraft:stonebrick");
        itemMap.put("minecraft:cracked_stone_bricks", "minecraft:stonebrick");
        itemMap.put("minecraft:chiseled_stone_bricks", "minecraft:stonebrick");
        itemMap.put("minecraft:smooth_stone", "minecraft:stone");
        itemMap.put("minecraft:deepslate", "minecraft:stone");
        itemMap.put("minecraft:cobbled_deepslate", "minecraft:cobblestone");
        itemMap.put("minecraft:polished_deepslate", "minecraft:stone");
        itemMap.put("minecraft:blackstone", "minecraft:cobblestone");
        itemMap.put("minecraft:polished_blackstone", "minecraft:stone");
        itemMap.put("minecraft:polished_blackstone_bricks", "minecraft:stonebrick");
        itemMap.put("minecraft:iron_nugget", "minecraft:gold_nugget");
        itemMap.put("minecraft:netherite_ingot", "minecraft:diamond");
        itemMap.put("minecraft:netherite_scrap", "minecraft:gold_ingot");
        itemMap.put("minecraft:ancient_debris", "minecraft:obsidian");
        itemMap.put("minecraft:amethyst_shard", "minecraft:quartz");
        itemMap.put("minecraft:copper_ingot", "minecraft:gold_ingot");
        itemMap.put("minecraft:raw_iron", "minecraft:iron_ingot");
        itemMap.put("minecraft:raw_gold", "minecraft:gold_ingot");
        itemMap.put("minecraft:raw_copper", "minecraft:gold_ingot");
        itemMap.put("minecraft:echo_shard", "minecraft:quartz");
        itemMap.put("minecraft:prismarine_shard", "minecraft:quartz");
        itemMap.put("minecraft:prismarine_crystals", "minecraft:quartz");
        itemIdFallbacks = Collections.unmodifiableMap(itemMap);

        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("balm:emeralds", "minecraft:emerald");
        tagMap.put("balm:gold_nuggets", "minecraft:gold_nugget");
        tagMap.put("balm:black_dyes", "minecraft:dye");
        tagMap.put("balm:blue_dyes", "minecraft:dye");
        tagMap.put("balm:brown_dyes", "minecraft:dye");
        tagMap.put("balm:cyan_dyes", "minecraft:dye");
        tagMap.put("balm:gray_dyes", "minecraft:dye");
        tagMap.put("balm:green_dyes", "minecraft:dye");
        tagMap.put("balm:light_blue_dyes", "minecraft:dye");
        tagMap.put("balm:light_gray_dyes", "minecraft:dye");
        tagMap.put("balm:lime_dyes", "minecraft:dye");
        tagMap.put("balm:magenta_dyes", "minecraft:dye");
        tagMap.put("balm:orange_dyes", "minecraft:dye");
        tagMap.put("balm:pink_dyes", "minecraft:dye");
        tagMap.put("balm:purple_dyes", "minecraft:dye");
        tagMap.put("balm:red_dyes", "minecraft:dye");
        tagMap.put("balm:white_dyes", "minecraft:dye");
        tagMap.put("balm:yellow_dyes", "minecraft:dye");
        tagMap.put("c:ingots/copper", "minecraft:gold_ingot");
        tagMap.put("c:ingots/netherite", "minecraft:diamond");
        tagMap.put("c:nuggets/iron", "minecraft:gold_nugget");
        tagMap.put("c:gems/amethyst", "minecraft:quartz");
        tagMap.put("forge:ingots/copper", "minecraft:gold_ingot");
        tagMap.put("forge:ingots/netherite", "minecraft:diamond");
        tagMap.put("forge:nuggets/iron", "minecraft:gold_nugget");
        tagMap.put("forge:gems/amethyst", "minecraft:quartz");
        tagFallbacks = Collections.unmodifiableMap(tagMap);
    }

    public static class ModDependency {
        public final String modId;
        public final boolean mandatory;
        public final String versionRange;

        public ModDependency(String modId, boolean mandatory, String versionRange) {
            this.modId = modId;
            this.mandatory = mandatory;
            this.versionRange = versionRange;
        }
    }

    public static class ModMetadata {
        public String modLoader;
        public String loaderVersion;
        public String modId;
        public String version;
        public String displayName;
        public String minecraftVersionRange;
        public String forgeVersionRange;
        public boolean hasMixins;
        public int assetFileCount;
        public int dataFileCount;
        public int recipesCount;
        public int tagsCount;
        public int worldgenCount;
        public final List<ModDependency> dependencies = new ArrayList<>();
    }

    public static class ModEntry {
        public String name;
        public ModMetadata metadata;
        public boolean compatible = true;
        public final List<String> issues = new ArrayList<>();

        public ModEntry(String name) {
            this.name = name;
        }
    }

    public static class InstallValidationResult {
        public boolean compatible = true;
        public int compatibilityScore = 100; // 0-100 score
        public final List<String> issues = new ArrayList<>();
        public final List<String> warnings = new ArrayList<>();
        public final List<String> tips = new ArrayList<>();
        public final Map<String, Integer> categoryScores = new HashMap<>();
        
        public String getCompatibilityLevel() {
            if (compatibilityScore >= 90) return "ممتاز";
            if (compatibilityScore >= 75) return "جيد جداً";
            if (compatibilityScore >= 60) return "جيد";
            if (compatibilityScore >= 40) return "مقبول";
            if (compatibilityScore >= 20) return "ضعيف";
            return "غير متوافق";
        }
        
        public String getCompatibilityEmoji() {
            if (compatibilityScore >= 90) return "✅";
            if (compatibilityScore >= 75) return "🟢";
            if (compatibilityScore >= 60) return "🟡";
            if (compatibilityScore >= 40) return "🟠";
            if (compatibilityScore >= 20) return "🔴";
            return "❌";
        }
    }
    
    public static class ModAnalysisReport {
        public String modId;
        public String version;
        public String displayName;
        public int totalBlocks = 0;
        public int totalItems = 0;
        public int totalRecipes = 0;
        public int totalTextures = 0;
        public int totalModels = 0;
        public int totalBlockstates = 0;
        public int totalTags = 0;
        public int totalWorldgen = 0;
        public int totalLangFiles = 0;
        public boolean hasMixins = false;
        public boolean hasConfig = false;
        public boolean hasNetworkCode = false;
        public boolean hasCustomRenderers = false;
        public final List<String> dependencies = new ArrayList<>();
        public final List<String> features = new ArrayList<>();
        
        public void printReport(String worldName, String fileName) {
            ModLogManager.info(worldName, fileName, "╔════════════════════════════════════════════════════════════╗");
            ModLogManager.info(worldName, fileName, "║           تقرير تحليل المود الشامل                      ║");
            ModLogManager.info(worldName, fileName, "╠════════════════════════════════════════════════════════════╣");
            ModLogManager.info(worldName, fileName, "║ المعرف: " + safe(modId));
            ModLogManager.info(worldName, fileName, "║ الإصدار: " + safe(version));
            ModLogManager.info(worldName, fileName, "║ الاسم: " + safe(displayName));
            ModLogManager.info(worldName, fileName, "╠════════════════════════════════════════════════════════════╣");
            ModLogManager.info(worldName, fileName, "║ 📊 إحصائيات المحتوى:");
            ModLogManager.info(worldName, fileName, "║   🧱 الكتل: " + totalBlocks);
            ModLogManager.info(worldName, fileName, "║   🎁 العناصر: " + totalItems);
            ModLogManager.info(worldName, fileName, "║   📝 الوصفات: " + totalRecipes);
            ModLogManager.info(worldName, fileName, "║   🎨 الـ Textures: " + totalTextures);
            ModLogManager.info(worldName, fileName, "║   📦 الـ Models: " + totalModels);
            ModLogManager.info(worldName, fileName, "║   🔲 الـ Blockstates: " + totalBlockstates);
            ModLogManager.info(worldName, fileName, "║   🏷️ الوسوم: " + totalTags);
            ModLogManager.info(worldName, fileName, "║   🌍 Worldgen: " + totalWorldgen);
            ModLogManager.info(worldName, fileName, "║   🌐 ملفات اللغة: " + totalLangFiles);
            ModLogManager.info(worldName, fileName, "╠════════════════════════════════════════════════════════════╣");
            ModLogManager.info(worldName, fileName, "║ 🔧 الميزات التقنية:");
            ModLogManager.info(worldName, fileName, "║   Mixins: " + (hasMixins ? "⚠️ نعم" : "✅ لا"));
            ModLogManager.info(worldName, fileName, "║   Config: " + (hasConfig ? "✅ نعم" : "❌ لا"));
            ModLogManager.info(worldName, fileName, "║   Network: " + (hasNetworkCode ? "✅ نعم" : "❌ لا"));
            ModLogManager.info(worldName, fileName, "║   Renderers: " + (hasCustomRenderers ? "✅ نعم" : "❌ لا"));
            if (!dependencies.isEmpty()) {
                ModLogManager.info(worldName, fileName, "╠════════════════════════════════════════════════════════════╣");
                ModLogManager.info(worldName, fileName, "║ 📦 الاعتمادات:");
                for (String dep : dependencies) {
                    ModLogManager.info(worldName, fileName, "║   • " + dep);
                }
            }
            if (!features.isEmpty()) {
                ModLogManager.info(worldName, fileName, "╠════════════════════════════════════════════════════════════╣");
                ModLogManager.info(worldName, fileName, "║ ✨ الميزات:");
                for (String feature : features) {
                    ModLogManager.info(worldName, fileName, "║   • " + feature);
                }
            }
            ModLogManager.info(worldName, fileName, "╚════════════════════════════════════════════════════════════╝");
        }
    }

    public static List<ModEntry> getWorldMods(String worldName) {
        List<ModEntry> ret = new ArrayList<>();
        VFile2 modsDir = new VFile2("mods", worldName);
        ModLogManager.info(worldName, "-", "Listing mods folder: " + modsDir.getPath());
        List<String> files = modsDir.listFilenames(false);
        ModLogManager.info(worldName, "-", "Mods folder entries=" + files.size());
        for (String file : files) {
            String fileName = VFile2.getNameFromPath(file);
            ModEntry entry = new ModEntry(fileName);
            if (fileName != null && fileName.toLowerCase().endsWith(".jar")) {
                try {
                    ModLogManager.info(worldName, fileName, "Inspecting mod metadata");
                    VFile2 vf = new VFile2(file);
                    ModLogManager.info(worldName, fileName, "Reading jar: " + vf.getPath() + " size=" + vf.length());
                    byte[] jarBytes = vf.getAllBytes();
                    ModLogManager.info(worldName, fileName, "Read bytes=" + (jarBytes == null ? -1 : jarBytes.length));
                    entry.metadata = parseJarMetadata(jarBytes);
                    if (entry.metadata != null) {
                        translateModData(worldName, fileName, jarBytes, entry.metadata);
                        ModLogManager.info(worldName, fileName, "Metadata loaded, data translated");
                    } else {
                        ModLogManager.warn(worldName, fileName, "mods.toml missing or unreadable");
                    }
                } catch (Throwable t) {
                    entry.compatible = false;
                    entry.issues.add("Failed to inspect JAR metadata");
                    ModLogManager.error(worldName, fileName, "Failed to inspect metadata", t);
                    logger.error("Failed to inspect mod jar: {}", fileName);
                    logger.error(t);
                }
            } else {
                entry.compatible = false;
                entry.issues.add("Only .jar Forge mods are supported");
                ModLogManager.warn(worldName, fileName, "Skipped: only .jar forge mods supported");
            }
            ret.add(entry);
        }
        evaluateCompatibility(ret);
        ForgeDataRuntime.reloadWorldIfActive(worldName);
        return ret;
    }

    public static int getWorldModsRevision(String worldName) {
        if (worldName == null) {
            return 0;
        }
        Integer v = worldModsRevision.get(worldName);
        return v != null ? v.intValue() : 0;
    }

    public static int countWorldMods(String worldName) {
        VFile2 modsDir = new VFile2("mods", worldName);
        List<String> files = modsDir.listFilenames(false);
        int cnt = 0;
        for (int i = 0, l = files.size(); i < l; ++i) {
            String fileName = VFile2.getNameFromPath(files.get(i));
            if (fileName != null && fileName.toLowerCase().endsWith(".jar")) {
                ++cnt;
            }
        }
        return cnt;
    }

    public static void addMod(String worldName, String filename, byte[] data) {
        VFile2 modsDir = new VFile2("mods", worldName);
        VFile2 modFile = new VFile2("mods", worldName, filename);
        int beforeCount = countWorldMods(worldName);
        ModLogManager.info(worldName, filename, "Starting installation");
        ModLogManager.info(worldName, filename, "World folder=" + safe(worldName));
        ModLogManager.info(worldName, filename, "Target file=" + modFile.getPath());
        ModLogManager.info(worldName, filename, "Incoming bytes=" + (data == null ? -1 : data.length));
        ModLogManager.info(worldName, filename, "Mods dir exists=" + modsDir.exists() + " path=" + modsDir.getPath() + " beforeCount=" + beforeCount);
        try {
            ModMetadata metadata = parseJarMetadata(data);
            InstallValidationResult validation = validateInstall(worldName, filename, data, metadata);
            if (!validation.warnings.isEmpty()) {
                for (int i = 0, l = validation.warnings.size(); i < l; ++i) {
                    ModLogManager.warn(worldName, filename, validation.warnings.get(i));
                }
            }
            if (!validation.compatible) {
                for (int i = 0, l = validation.issues.size(); i < l; ++i) {
                    ModLogManager.error(worldName, filename, validation.issues.get(i), null);
                }
                String reason = validation.issues.isEmpty() ? "مود غير متوافق مع محرك اللعبة الحالي" : validation.issues.get(0);
                ModLogManager.setWorldStatus(worldName, "ERROR", "تم رفض المود غير المتوافق: " + filename + " - " + reason);
                throw new IllegalArgumentException("Incompatible mod rejected: " + reason);
            }

            modFile.setAllBytes(data);
            bumpWorldRevision(worldName);
            ModLogManager.info(worldName, filename, "Write completed, exists=" + modFile.exists() + " size=" + modFile.length());
            int afterCount = countWorldMods(worldName);
            ModLogManager.info(worldName, filename, "Mods dir afterCount=" + afterCount);
            if (metadata != null) {
                ModLogManager.info(worldName, filename, "mods.toml parsed: modId=" + safe(metadata.modId) + " version=" + safe(metadata.version));
                ModLogManager.info(worldName, filename, "modLoader=" + safe(metadata.modLoader) + " loaderVersion=" + safe(metadata.loaderVersion));
                if (metadata.displayName != null && metadata.displayName.length() > 0) {
                    ModLogManager.info(worldName, filename, "displayName=" + metadata.displayName);
                }
                if (metadata.minecraftVersionRange != null && metadata.minecraftVersionRange.length() > 0) {
                    ModLogManager.info(worldName, filename, "minecraftVersionRange=" + metadata.minecraftVersionRange);
                }
                if (metadata.forgeVersionRange != null && metadata.forgeVersionRange.length() > 0) {
                    ModLogManager.info(worldName, filename, "forgeVersionRange=" + metadata.forgeVersionRange);
                }
                if (metadata.hasMixins) {
                    ModLogManager.warn(worldName, filename, "This mod contains mixins and may not work without translation");
                }
                if (!metadata.dependencies.isEmpty()) {
                    int shown = 0;
                    StringBuilder deps = new StringBuilder();
                    for (int i = 0, l = metadata.dependencies.size(); i < l; ++i) {
                        ModDependency d = metadata.dependencies.get(i);
                        if (!d.mandatory) {
                            continue;
                        }
                        if (shown > 0) {
                            deps.append(", ");
                        }
                        deps.append(d.modId).append(":").append(d.versionRange);
                        ++shown;
                        if (shown >= 8) {
                            break;
                        }
                    }
                    if (shown > 0) {
                        ModLogManager.info(worldName, filename, "mandatoryDependencies=" + deps.toString());
                    }
                }
                translateModData(worldName, filename, data, metadata);
                ForgeDataRuntime.reloadWorldIfActive(worldName);
                ModLogManager.info(worldName, filename, "Installation completed");
                ModLogManager.setWorldStatus(worldName, "INFO", "تم تركيب المود بنجاح: " + filename);
            } else {
                ModLogManager.warn(worldName, filename, "Installed, but metadata not detected");
                ModLogManager.setWorldStatus(worldName, "WARN", "تم حفظ المود لكن لم يتم التعرف على mods.toml: " + filename);
            }
        } catch (Throwable t) {
            ModLogManager.error(worldName, filename, "Installation failed", t);
            ModLogManager.setWorldStatus(worldName, "ERROR", "فشل تركيب المود: " + filename + " (" + t.getClass().getSimpleName() + ")");
            logger.error("Failed to pre-translate mod data: {}", filename);
            logger.error(t);
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new RuntimeException(t);
        }
    }

    public static void deleteMod(String worldName, String filename) {
        VFile2 modFile = new VFile2("mods", worldName, filename);
        if (modFile.exists()) {
            modFile.delete();
            ModLogManager.info(worldName, filename, "Mod deleted");
            bumpWorldRevision(worldName);
            ModLogManager.setWorldStatus(worldName, "INFO", "تم حذف المود: " + filename);
        } else {
            ModLogManager.warn(worldName, filename, "Delete requested but file not found");
            ModLogManager.setWorldStatus(worldName, "WARN", "لم يتم العثور على المود لحذفه: " + filename);
        }
        ForgeDataRuntime.reloadWorldIfActive(worldName);
    }

    private static void bumpWorldRevision(String worldName) {
        if (worldName == null) {
            return;
        }
        Integer v = worldModsRevision.get(worldName);
        if (v == null) {
            worldModsRevision.put(worldName, Integer.valueOf(1));
        } else {
            worldModsRevision.put(worldName, Integer.valueOf(v.intValue() + 1));
        }
    }

    private static void evaluateCompatibility(List<ModEntry> mods) {
        Map<String, ModEntry> installedModIds = new HashMap<>();
        for (ModEntry entry : mods) {
            if (entry.metadata != null && entry.metadata.modId != null && entry.metadata.modId.length() > 0) {
                installedModIds.put(entry.metadata.modId.toLowerCase(), entry);
            }
        }

        for (ModEntry entry : mods) {
            if (entry.metadata == null) {
                entry.compatible = false;
                if (entry.issues.isEmpty()) {
                    entry.issues.add("mods.toml is missing or unreadable");
                }
                continue;
            }

            ModMetadata metadata = entry.metadata;
            if (!"javafml".equalsIgnoreCase(metadata.modLoader)) {
                entry.compatible = false;
                entry.issues.add("Unsupported modLoader: " + safe(metadata.modLoader));
            }

            if (metadata.minecraftVersionRange != null && !isVersionInRange(ForgeBridge.getTargetMinecraftVersion(), metadata.minecraftVersionRange)) {
                entry.compatible = false;
                entry.issues.add("Minecraft range mismatch: " + metadata.minecraftVersionRange);
            }

            if (metadata.forgeVersionRange != null && !isVersionInRange(ForgeBridge.getTargetForgeVersion(), metadata.forgeVersionRange)) {
                entry.compatible = false;
                entry.issues.add("Forge range mismatch: " + metadata.forgeVersionRange);
            }

            for (ModDependency dep : metadata.dependencies) {
                if (!dep.mandatory || dep.modId == null || dep.modId.length() == 0) {
                    continue;
                }
                String normalized = dep.modId.toLowerCase();
                String providedVersion = ForgeBridge.getProvidedModVersion(normalized);
                if (providedVersion != null) {
                    if (dep.versionRange != null && dep.versionRange.length() > 0 && !isVersionInRange(providedVersion, dep.versionRange)) {
                        entry.compatible = false;
                        entry.issues.add("Bridge dependency range mismatch: " + dep.modId + " " + dep.versionRange);
                    }
                    continue;
                }
                ModEntry depMod = installedModIds.get(normalized);
                if (depMod == null) {
                    if (isSoftDependency(normalized)) {
                        entry.issues.add("Soft dependency not installed: " + dep.modId);
                    } else {
                        entry.compatible = false;
                        entry.issues.add("Missing dependency: " + dep.modId);
                    }
                    continue;
                }
                String depVersion = depMod.metadata != null ? depMod.metadata.version : null;
                if (dep.versionRange != null && dep.versionRange.length() > 0 && depVersion != null && !isVersionInRange(depVersion, dep.versionRange)) {
                    entry.compatible = false;
                    entry.issues.add("Dependency version mismatch: " + dep.modId + " " + dep.versionRange);
                }
            }

            if (metadata.hasMixins) {
                entry.issues.add("Contains Mixin patches that need translation");
            }
        }
    }

    private static InstallValidationResult validateInstall(String worldName, String filename, byte[] data, ModMetadata metadata) {
        InstallValidationResult result = new InstallValidationResult();
        ModAnalysisReport report = new ModAnalysisReport();
        
        if (metadata == null) {
            result.compatible = false;
            result.compatibilityScore = 0;
            result.issues.add("❌ META-INF/mods.toml مفقود أو غير صالح");
            result.categoryScores.put("metadata", 0);
            return result;
        }

        // Initialize report
        report.modId = metadata.modId;
        report.version = metadata.version;
        report.displayName = metadata.displayName;
        
        // ═══════════════════════════════════════════════════════════
        // فحص ModLoader (20 نقطة)
        // ═══════════════════════════════════════════════════════════
        int modLoaderScore = 20;
        if (!"javafml".equalsIgnoreCase(metadata.modLoader)) {
            result.compatible = false;
            modLoaderScore = 0;
            result.issues.add("❌ modLoader غير مدعوم: " + safe(metadata.modLoader) + " (المطلوب: javafml)");
        } else {
            result.tips.add("✅ modLoader متوافق: javafml");
        }
        result.categoryScores.put("modLoader", modLoaderScore);
        
        // ═══════════════════════════════════════════════════════════
        // فحص إصدار Minecraft (25 نقطة)
        // ═══════════════════════════════════════════════════════════
        int minecraftScore = 25;
        String targetMC = ForgeBridge.getTargetMinecraftVersion();
        if (metadata.minecraftVersionRange != null && !isVersionInRange(targetMC, metadata.minecraftVersionRange)) {
            result.compatible = false;
            minecraftScore = 0;
            result.issues.add("❌ إصدار Minecraft غير متوافق");
            result.issues.add("   المطلوب: " + metadata.minecraftVersionRange);
            result.issues.add("   المتوفر: " + targetMC);
        } else {
            result.tips.add("✅ إصدار Minecraft متوافق: " + targetMC);
        }
        result.categoryScores.put("minecraft", minecraftScore);
        
        // ═══════════════════════════════════════════════════════════
        // فحص إصدار Forge (20 نقطة)
        // ═══════════════════════════════════════════════════════════
        int forgeScore = 20;
        String targetForge = ForgeBridge.getTargetForgeVersion();
        if (metadata.forgeVersionRange != null && !isVersionInRange(targetForge, metadata.forgeVersionRange)) {
            result.compatible = false;
            forgeScore = 0;
            result.issues.add("❌ إصدار Forge غير متوافق");
            result.issues.add("   المطلوب: " + metadata.forgeVersionRange);
            result.issues.add("   المتوفر: " + targetForge);
        } else {
            result.tips.add("✅ إصدار Forge متوافق: " + targetForge);
        }
        result.categoryScores.put("forge", forgeScore);
        
        // ═══════════════════════════════════════════════════════════
        // فحص javafml loader version (5 نقطة)
        // ═══════════════════════════════════════════════════════════
        int loaderVersionScore = 5;
        if (metadata.loaderVersion != null && metadata.loaderVersion.length() > 0
                && !isVersionInRange(targetForge, metadata.loaderVersion)) {
            loaderVersionScore = 2;
            result.warnings.add("⚠️ إصدار javafml قد لا يكون متوافقاً تماماً: " + metadata.loaderVersion);
        }
        result.categoryScores.put("loaderVersion", loaderVersionScore);
        
        // ═══════════════════════════════════════════════════════════
        // فحص Mixins (10 نقاط)
        // ═══════════════════════════════════════════════════════════
        int mixinScore = 10;
        report.hasMixins = metadata.hasMixins;
        if (metadata.hasMixins) {
            mixinScore = 5;
            result.warnings.add("⚠️ المود يحتوي على Mixin patches");
            result.warnings.add("   قد تحتاج إلى ترجمة يدوية للعمل بشكل كامل");
            result.tips.add("💡 نصيحة: راقب السجلات بعد التحميل للتأكد من عمل Mixins");
        } else {
            result.tips.add("✅ لا يحتوي على Mixins");
        }
        result.categoryScores.put("mixins", mixinScore);
        
        // ═══════════════════════════════════════════════════════════
        // فحص الاعتمادات (15 نقطة)
        // ═══════════════════════════════════════════════════════════
        int dependencyScore = 15;
        int mandatoryDeps = 0;
        int satisfiedDeps = 0;
        int bridgeDeps = 0;
        
        Map<String, ModMetadata> installed = collectInstalledModsMetadata(worldName, filename);
        
        for (ModDependency dep : metadata.dependencies) {
            if (!dep.mandatory) {
                report.dependencies.add(dep.modId + " (اختياري)");
                continue;
            }
            
            mandatoryDeps++;
            String depId = dep.modId.toLowerCase();
            String bridgeVersion = ForgeBridge.getProvidedModVersion(depId);
            
            if (bridgeVersion != null) {
                bridgeDeps++;
                report.dependencies.add(dep.modId + " ✅ (جسر)");
                if (dep.versionRange != null && dep.versionRange.length() > 0 && !isVersionInRange(bridgeVersion, dep.versionRange)) {
                    result.compatible = false;
                    result.issues.add("❌ اعتمادية الجسر غير متوافقة: " + dep.modId + " " + dep.versionRange);
                } else {
                    satisfiedDeps++;
                }
                continue;
            }
            
            ModMetadata installedMeta = installed.get(depId);
            if (installedMeta == null) {
                if (isSoftDependency(depId)) {
                    result.warnings.add("⚠️ اعتمادية اختيارية غير مثبتة: " + dep.modId);
                    report.dependencies.add(dep.modId + " ⚠️ (اختياري مفقود)");
                } else {
                    result.compatible = false;
                    result.issues.add("❌ اعتمادية مفقودة: " + dep.modId);
                    report.dependencies.add(dep.modId + " ❌ (مطلوب مفقود)");
                }
                continue;
            }
            
            satisfiedDeps++;
            report.dependencies.add(dep.modId + " ✅");
            
            if (dep.versionRange != null && dep.versionRange.length() > 0 && installedMeta.version != null
                    && !isVersionInRange(installedMeta.version, dep.versionRange)) {
                result.compatible = false;
                result.issues.add("❌ نسخة اعتمادية غير مطابقة: " + dep.modId);
                result.issues.add("   المطلوب: " + dep.versionRange);
                result.issues.add("   المتوفر: " + installedMeta.version);
            }
        }
        
        if (mandatoryDeps > 0) {
            dependencyScore = (int)((double)satisfiedDeps / mandatoryDeps * 15);
            result.tips.add("📦 الاعتمادات: " + satisfiedDeps + "/" + mandatoryDeps + " متوفرة");
            if (bridgeDeps > 0) {
                result.tips.add("🌉 اعتمادات الجسر: " + bridgeDeps);
            }
        }
        result.categoryScores.put("dependencies", dependencyScore);
        
        // ═══════════════════════════════════════════════════════════
        // فحص سلامة JSON (5 نقاط)
        // ═══════════════════════════════════════════════════════════
        int jsonScore = 5;
        validateDataJsonIntegrity(data, result);
        if (!result.issues.isEmpty()) {
            for (String issue : result.issues) {
                if (issue.contains("JSON")) {
                    jsonScore = Math.max(0, jsonScore - 2);
                }
            }
        }
        result.categoryScores.put("json", jsonScore);
        
        // ═══════════════════════════════════════════════════════════
        // حساب النتيجة الإجمالية
        // ═══════════════════════════════════════════════════════════
        int totalScore = 0;
        for (Integer score : result.categoryScores.values()) {
            totalScore += score;
        }
        result.compatibilityScore = totalScore;
        
        // ═══════════════════════════════════════════════════════════
        // طباعة تقرير التحليل الشامل
        // ═══════════════════════════════════════════════════════════
        ModLogManager.info(worldName, filename, "");
        ModLogManager.info(worldName, filename, "╔════════════════════════════════════════════════════════════╗");
        ModLogManager.info(worldName, filename, "║        تقرير التوافق الشامل للمود                       ║");
        ModLogManager.info(worldName, filename, "╠════════════════════════════════════════════════════════════╣");
        ModLogManager.info(worldName, filename, "║ الملف: " + filename);
        ModLogManager.info(worldName, filename, "║ المعرف: " + safe(metadata.modId));
        ModLogManager.info(worldName, filename, "║ الإصدار: " + safe(metadata.version));
        ModLogManager.info(worldName, filename, "║ الاسم: " + safe(metadata.displayName));
        ModLogManager.info(worldName, filename, "╠════════════════════════════════════════════════════════════╣");
        ModLogManager.info(worldName, filename, "║ " + result.getCompatibilityEmoji() + " نتيجة التوافق: " + result.compatibilityScore + "/100 (" + result.getCompatibilityLevel() + ")");
        ModLogManager.info(worldName, filename, "╠════════════════════════════════════════════════════════════╣");
        ModLogManager.info(worldName, filename, "║ 📊 تفصيل النقاط:");
        ModLogManager.info(worldName, filename, "║   ModLoader: " + result.categoryScores.get("modLoader") + "/20");
        ModLogManager.info(worldName, filename, "║   Minecraft: " + result.categoryScores.get("minecraft") + "/25");
        ModLogManager.info(worldName, filename, "║   Forge: " + result.categoryScores.get("forge") + "/20");
        ModLogManager.info(worldName, filename, "║   Loader Version: " + result.categoryScores.get("loaderVersion") + "/5");
        ModLogManager.info(worldName, filename, "║   Mixins: " + result.categoryScores.get("mixins") + "/10");
        ModLogManager.info(worldName, filename, "║   Dependencies: " + result.categoryScores.get("dependencies") + "/15");
        ModLogManager.info(worldName, filename, "║   JSON Integrity: " + result.categoryScores.get("json") + "/5");
        
        if (!result.issues.isEmpty()) {
            ModLogManager.info(worldName, filename, "╠════════════════════════════════════════════════════════════╣");
            ModLogManager.info(worldName, filename, "║ ❌ المشاكل (" + result.issues.size() + "):");
            for (String issue : result.issues) {
                ModLogManager.info(worldName, filename, "║   " + issue);
            }
        }
        
        if (!result.warnings.isEmpty()) {
            ModLogManager.info(worldName, filename, "╠════════════════════════════════════════════════════════════╣");
            ModLogManager.info(worldName, filename, "║ ⚠️ التحذيرات (" + result.warnings.size() + "):");
            for (String warning : result.warnings) {
                ModLogManager.info(worldName, filename, "║   " + warning);
            }
        }
        
        if (!result.tips.isEmpty()) {
            ModLogManager.info(worldName, filename, "╠════════════════════════════════════════════════════════════╣");
            ModLogManager.info(worldName, filename, "║ 💡 النصائح والمعلومات:");
            for (String tip : result.tips) {
                ModLogManager.info(worldName, filename, "║   " + tip);
            }
        }
        
        ModLogManager.info(worldName, filename, "╚════════════════════════════════════════════════════════════╝");
        ModLogManager.info(worldName, filename, "");
        
        // ═══════════════════════════════════════════════════════════
        // طباعة تقرير تحليل المود إذا كان متوافقاً
        // ═══════════════════════════════════════════════════════════
        if (result.compatible) {
            report.totalRecipes = metadata.recipesCount;
            report.totalTags = metadata.tagsCount;
            report.totalWorldgen = metadata.worldgenCount;
            report.printReport(worldName, filename);
        }
        
        return result;
    }

    private static Map<String, ModMetadata> collectInstalledModsMetadata(String worldName, String excludeFilename) {
        Map<String, ModMetadata> map = new LinkedHashMap<>();
        VFile2 modsDir = new VFile2("mods", worldName);
        List<String> files = modsDir.listFilenames(false);
        String excludeLower = excludeFilename != null ? excludeFilename.toLowerCase() : null;
        for (int i = 0, l = files.size(); i < l; ++i) {
            String path = files.get(i);
            String fileName = VFile2.getNameFromPath(path);
            if (fileName == null || !fileName.toLowerCase().endsWith(".jar")) {
                continue;
            }
            if (excludeLower != null && excludeLower.equals(fileName.toLowerCase())) {
                continue;
            }
            try {
                byte[] bytes = (new VFile2(path)).getAllBytes();
                ModMetadata md = parseJarMetadata(bytes);
                if (md != null && md.modId != null && md.modId.length() > 0) {
                    map.put(md.modId.toLowerCase(), md);
                }
            } catch (Throwable ignored) {
            }
        }
        return map;
    }

    private static void validateDataJsonIntegrity(byte[] jarBytes, InstallValidationResult result) {
        if (jarBytes == null || jarBytes.length == 0) {
            result.compatible = false;
            result.issues.add("ملف المود فارغ أو تالف");
            return;
        }
        int checked = 0;
        int invalid = 0;
        try (ZipInputStream zis = new ZipInputStream(new EaglerInputStream(jarBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.startsWith("data/") || !name.endsWith(".json")) {
                    continue;
                }
                if (!(name.contains("/recipes/") || name.contains("/tags/") || name.contains("/worldgen/") || name.contains("/loot_tables/"))) {
                    continue;
                }
                ++checked;
                String s = new String(EaglerInputStream.inputStreamToBytesNoClose(zis), StandardCharsets.UTF_8);
                try {
                    new JSONObject(s);
                } catch (Throwable t) {
                    ++invalid;
                    if (invalid <= 3) {
                        result.issues.add("JSON غير صالح في: " + name);
                    }
                }
            }
        } catch (Throwable t) {
            result.compatible = false;
            result.issues.add("تعذر قراءة أرشيف المود للتحقق");
            return;
        }
        if (invalid > 0) {
            result.compatible = false;
            if (invalid > 3) {
                result.issues.add("ملفات JSON غير صالحة إضافية: " + (invalid - 3));
            }
        }
        if (checked == 0) {
            result.warnings.add("لا توجد بيانات JSON قابلة للترجمة داخل data/");
        }
    }

    private static ModMetadata parseJarMetadata(byte[] jarBytes) throws IOException {
        if (jarBytes == null || jarBytes.length == 0) {
            return null;
        }
        ModMetadata metadata = new ModMetadata();
        String modsToml = null;
        try (ZipInputStream zis = new ZipInputStream(new EaglerInputStream(jarBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if ("META-INF/mods.toml".equals(name)) {
                    modsToml = new String(EaglerInputStream.inputStreamToBytesNoClose(zis), StandardCharsets.UTF_8);
                } else if (name.endsWith(".mixins.json")) {
                    metadata.hasMixins = true;
                } else if (name.startsWith("assets/")) {
                    metadata.assetFileCount++;
                } else if (name.startsWith("data/")) {
                    metadata.dataFileCount++;
                    if (name.contains("/recipes/")) {
                        metadata.recipesCount++;
                    } else if (name.contains("/tags/")) {
                        metadata.tagsCount++;
                    } else if (name.contains("/worldgen/")) {
                        metadata.worldgenCount++;
                    }
                }
            }
        }
        if (modsToml == null) {
            return null;
        }
        parseModsToml(modsToml, metadata);
        return metadata;
    }

    private static void parseModsToml(String toml, ModMetadata metadata) {
        String[] lines = toml.replace("\r", "").split("\n");
        boolean inModsSection = false;
        boolean inDependencySection = false;
        boolean depOpen = false;
        String depModId = null;
        boolean depMandatory = false;
        String depVersionRange = null;
        for (String rawLine : lines) {
            String line = normalizeLine(rawLine);
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("[[mods]]")) {
                if (depOpen) {
                    metadata.dependencies.add(new ModDependency(depModId, depMandatory, depVersionRange));
                    depOpen = false;
                }
                inModsSection = true;
                inDependencySection = false;
                continue;
            }
            if (line.startsWith("[[dependencies.")) {
                if (depOpen) {
                    metadata.dependencies.add(new ModDependency(depModId, depMandatory, depVersionRange));
                }
                inModsSection = false;
                inDependencySection = true;
                depOpen = true;
                depModId = null;
                depMandatory = false;
                depVersionRange = null;
                continue;
            }
            int eq = line.indexOf('=');
            if (eq == -1) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            String value = parseTomlValue(line.substring(eq + 1).trim());
            if (inModsSection) {
                if ("modId".equals(key)) {
                    metadata.modId = value;
                } else if ("version".equals(key)) {
                    metadata.version = value;
                } else if ("displayName".equals(key)) {
                    metadata.displayName = value;
                }
            } else if (inDependencySection) {
                if ("modId".equals(key)) {
                    depModId = value;
                } else if ("mandatory".equals(key)) {
                    depMandatory = "true".equalsIgnoreCase(value);
                } else if ("versionRange".equals(key)) {
                    depVersionRange = value;
                }
            } else {
                if ("modLoader".equals(key)) {
                    metadata.modLoader = value;
                } else if ("loaderVersion".equals(key)) {
                    metadata.loaderVersion = value;
                }
            }
        }
        if (depOpen) {
            metadata.dependencies.add(new ModDependency(depModId, depMandatory, depVersionRange));
        }
        for (ModDependency dep : metadata.dependencies) {
            if ("minecraft".equalsIgnoreCase(dep.modId)) {
                metadata.minecraftVersionRange = dep.versionRange;
            } else if ("forge".equalsIgnoreCase(dep.modId)) {
                metadata.forgeVersionRange = dep.versionRange;
            }
        }
    }

    private static String normalizeLine(String rawLine) {
        String line = rawLine;
        int commentIndex = line.indexOf('#');
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex);
        }
        return line.trim();
    }

    private static String parseTomlValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }
        if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static boolean isVersionInRange(String version, String range) {
        if (version == null || range == null) {
            return true;
        }
        String trimmed = range.trim();
        if (trimmed.length() == 0 || "*".equals(trimmed)) {
            return true;
        }
        if (!(trimmed.startsWith("[") || trimmed.startsWith("(")) || !(trimmed.endsWith("]") || trimmed.endsWith(")"))) {
            return compareVersions(version, trimmed) == 0;
        }
        int comma = trimmed.indexOf(',');
        if (comma == -1) {
            return true;
        }
        boolean lowerInclusive = trimmed.startsWith("[");
        boolean upperInclusive = trimmed.endsWith("]");
        String lower = trimmed.substring(1, comma).trim();
        String upper = trimmed.substring(comma + 1, trimmed.length() - 1).trim();
        if (lower.length() > 0) {
            int cmp = compareVersions(version, lower);
            if (cmp < 0 || (!lowerInclusive && cmp == 0)) {
                return false;
            }
        }
        if (upper.length() > 0) {
            int cmp = compareVersions(version, upper);
            if (cmp > 0 || (!upperInclusive && cmp == 0)) {
                return false;
            }
        }
        return true;
    }

    private static int compareVersions(String a, String b) {
        int[] left = parseVersionNumbers(a);
        int[] right = parseVersionNumbers(b);
        int len = Math.max(left.length, right.length);
        for (int i = 0; i < len; ++i) {
            int l = i < left.length ? left[i] : 0;
            int r = i < right.length ? right[i] : 0;
            if (l != r) {
                return l < r ? -1 : 1;
            }
        }
        return 0;
    }

    private static int[] parseVersionNumbers(String version) {
        if (version == null || version.length() == 0) {
            return new int[0];
        }
        String[] split = version.split("[^0-9]+");
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < split.length; ++i) {
            String s = split[i];
            if (s.length() == 0) {
                continue;
            }
            try {
                nums.add(Integer.valueOf(s));
            } catch (NumberFormatException ignored) {
            }
        }
        int[] arr = new int[nums.size()];
        for (int i = 0; i < nums.size(); ++i) {
            arr[i] = nums.get(i).intValue();
        }
        return arr;
    }

    private static String safe(String str) {
        return str == null ? "unknown" : str;
    }

    private static void translateModData(String worldName, String fileName, byte[] jarBytes, ModMetadata metadata) throws IOException {
        if (jarBytes == null || jarBytes.length == 0) {
            ModLogManager.warn(worldName, fileName, "No data to translate");
            return;
        }
        String modId = metadata.modId != null && metadata.modId.length() > 0 ? metadata.modId : stripJarExtension(fileName);
        String modFolder = normalizePathSegment(modId + "-" + safe(metadata.version));
        VFile2 baseDir = new VFile2("mods_translated", worldName, modFolder);
        ModLogManager.info(worldName, fileName, "Translate output folder: " + baseDir.getPath());
        JSONArray translatedFiles = new JSONArray();
        int translatedCount = 0;
        int jsonFailCount = 0;
        int idRemapCount = 0;
        int tagFallbackCount = 0;
        int blocksRegistered = 0;
        int itemsRegistered = 0;
        List<String> jsonFailSamples = new ArrayList<>();
        Map<String, JSONObject> blockstates = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new EaglerInputStream(jarBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                
                // Process assets (blockstates, models, textures)
                if (name.startsWith("assets/")) {
                    byte[] raw = EaglerInputStream.inputStreamToBytesNoClose(zis);
                    VFile2 out = new VFile2(baseDir, name);
                    out.setAllBytes(raw);
                    if (translatedFiles.length() < 2048) {
                        translatedFiles.put(name);
                    }
                    translatedCount++;
                    
                    // Collect blockstates for block registration
                    if (name.contains("/blockstates/") && name.endsWith(".json")) {
                        String original = new String(raw, StandardCharsets.UTF_8);
                        try {
                            JSONObject blockstate = new JSONObject(original);
                            String blockName = extractBlockName(name);
                            if (blockName != null) {
                                blockstates.put(blockName, blockstate);
                            }
                        } catch (Throwable ignored) {}
                    }
                    continue;
                }
                
                // Process data (recipes, tags, worldgen)
                if (!name.startsWith("data/")) {
                    continue;
                }
                String category = classifyDataPath(name);
                if (category == null) {
                    continue;
                }
                byte[] raw = EaglerInputStream.inputStreamToBytesNoClose(zis);
                if (name.endsWith(".json")) {
                    String original = new String(raw, StandardCharsets.UTF_8);
                    try {
                        JSONObject json = new JSONObject(original);
                        CompatRewriteStats rewriteStats = new CompatRewriteStats();
                        normalizeDataJson(name, json, rewriteStats);
                        idRemapCount += rewriteStats.idRemaps;
                        tagFallbackCount += rewriteStats.tagFallbacks;
                        json.put("_eaglerBridgeCategory", category);
                        json.put("_eaglerBridgeFormat", "forge_1_20_1");
                        raw = json.toString().getBytes(StandardCharsets.UTF_8);
                    } catch (Throwable t) {
                        ++jsonFailCount;
                        if (jsonFailSamples.size() < 5) {
                            jsonFailSamples.add(name);
                        }
                        raw = original.getBytes(StandardCharsets.UTF_8);
                    }
                }
                VFile2 out = new VFile2(baseDir, name);
                out.setAllBytes(raw);
                if (translatedFiles.length() < 2048) {
                    translatedFiles.put(name);
                }
                translatedCount++;
            }
        }
        
        // Register blocks and items from blockstates
        if (!blockstates.isEmpty()) {
            ModLogManager.info(worldName, fileName, "Found " + blockstates.size() + " blockstates, registering blocks...");
            for (Map.Entry<String, JSONObject> entry : blockstates.entrySet()) {
                String blockName = entry.getKey();
                try {
                    ResourceLocation location = new ResourceLocation(modId, blockName);
                    Material material = inferMaterial(blockName);
                    Block block = ModernRegistry.createAndRegisterBlock(location, material, 2.0f, 10.0f);
                    if (block != null) {
                        ModernRegistry.createAndRegisterItemBlock(location, block);
                        blocksRegistered++;
                        ModLogManager.info(worldName, fileName, "Registered block: " + location);
                    }
                } catch (Exception e) {
                    ModLogManager.error(worldName, fileName, "Failed to register block: " + blockName, e);
                }
            }
        }
        
        JSONObject manifest = new JSONObject();
        manifest.put("world", worldName);
        manifest.put("modFile", fileName);
        manifest.put("modId", metadata.modId == null ? JSONObject.NULL : metadata.modId);
        manifest.put("version", metadata.version == null ? JSONObject.NULL : metadata.version);
        manifest.put("loader", metadata.modLoader == null ? JSONObject.NULL : metadata.modLoader);
        manifest.put("targetMinecraft", ForgeBridge.getTargetMinecraftVersion());
        manifest.put("translatedDataFiles", translatedCount);
        manifest.put("recipes", metadata.recipesCount);
        manifest.put("tags", metadata.tagsCount);
        manifest.put("worldgen", metadata.worldgenCount);
        manifest.put("remappedIds", idRemapCount);
        manifest.put("tagFallbacks", tagFallbackCount);
        manifest.put("blocksRegistered", blocksRegistered);
        manifest.put("itemsRegistered", itemsRegistered);
        manifest.put("files", translatedFiles);
        (new VFile2(baseDir, "_translation_manifest.json")).setAllChars(manifest.toString());
        ModLogManager.info(worldName, fileName, "Translated files=" + translatedCount + " recipes=" + metadata.recipesCount + " tags=" + metadata.tagsCount + " worldgen=" + metadata.worldgenCount + " blocks=" + blocksRegistered + " items=" + itemsRegistered);
        if (idRemapCount > 0 || tagFallbackCount > 0) {
            ModLogManager.info(worldName, fileName, "Compatibility rewrites: idRemaps=" + idRemapCount + " tagFallbacks=" + tagFallbackCount);
        }
        if (jsonFailCount > 0) {
            ModLogManager.warn(worldName, fileName, "Some JSON files could not be parsed: count=" + jsonFailCount + " samples=" + String.join(", ", jsonFailSamples));
        }
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
        if (path.contains("/loot_tables/")) {
            return "loot_tables";
        }
        return null;
    }
    
    private static String extractBlockName(String path) {
        // Extract block name from path like "assets/waystones/blockstates/waystone.json"
        if (path == null || !path.contains("/blockstates/")) {
            return null;
        }
        int start = path.indexOf("/blockstates/") + "/blockstates/".length();
        int end = path.lastIndexOf(".json");
        if (start < 0 || end < 0 || start >= end) {
            return null;
        }
        return path.substring(start, end);
    }
    
    private static Material inferMaterial(String blockName) {
        if (blockName == null) {
            return Material.rock;
        }
        String lower = blockName.toLowerCase();
        if (lower.contains("stone") || lower.contains("deepslate") || lower.contains("blackstone")) {
            return Material.rock;
        }
        if (lower.contains("wood") || lower.contains("plank")) {
            return Material.wood;
        }
        if (lower.contains("sand")) {
            return Material.sand;
        }
        if (lower.contains("glass")) {
            return Material.glass;
        }
        if (lower.contains("metal") || lower.contains("iron")) {
            return Material.iron;
        }
        // Default to rock for waystones
        return Material.rock;
    }

    private static boolean isSoftDependency(String modId) {
        return modId != null && softDependencyIds.contains(modId);
    }

    private static class CompatRewriteStats {
        int idRemaps = 0;
        int tagFallbacks = 0;
    }

    private static void normalizeDataJson(String path, JSONObject json, CompatRewriteStats stats) {
        remapIdsRecursive(json, stats);
        if (path.contains("/recipes/")) {
            normalizeRecipeJson(json, stats);
        }
        if (path.contains("/tags/")) {
            normalizeTagJson(json, stats);
        }
    }

    private static void normalizeRecipeJson(JSONObject recipe, CompatRewriteStats stats) {
        JSONObject key = recipe.optJSONObject("key");
        if (key != null) {
            Iterator<String> it = key.keys();
            while (it.hasNext()) {
                String k = it.next();
                Object value = key.opt(k);
                if (value instanceof JSONObject) {
                    normalizeIngredient((JSONObject) value, stats);
                } else if (value instanceof JSONArray) {
                    normalizeIngredientArray((JSONArray) value, stats);
                }
            }
        }
        JSONArray ingredients = recipe.optJSONArray("ingredients");
        if (ingredients != null) {
            normalizeIngredientArray(ingredients, stats);
        }
    }

    private static void normalizeTagJson(JSONObject tag, CompatRewriteStats stats) {
        JSONArray values = tag.optJSONArray("values");
        if (values == null) {
            return;
        }
        for (int i = 0, l = values.length(); i < l; ++i) {
            String s = values.optString(i, null);
            if (s == null) {
                continue;
            }
            String mapped = remapId(s);
            if (!mapped.equals(s)) {
                values.put(i, mapped);
                stats.idRemaps++;
            }
        }
    }

    private static void normalizeIngredientArray(JSONArray arr, CompatRewriteStats stats) {
        for (int i = 0, l = arr.length(); i < l; ++i) {
            Object obj = arr.opt(i);
            if (obj instanceof JSONObject) {
                normalizeIngredient((JSONObject) obj, stats);
            } else if (obj instanceof JSONArray) {
                normalizeIngredientArray((JSONArray) obj, stats);
            }
        }
    }

    private static void normalizeIngredient(JSONObject ingredient, CompatRewriteStats stats) {
        String item = ingredient.optString("item", null);
        if (item != null && item.length() > 0) {
            String mapped = remapId(item);
            if (!mapped.equals(item)) {
                ingredient.put("item", mapped);
                stats.idRemaps++;
            }
        }
        String tag = ingredient.optString("tag", null);
        if ((item == null || item.length() == 0) && tag != null && tag.length() > 0) {
            String fallback = tagFallbacks.get(tag.toLowerCase());
            if (fallback != null) {
                ingredient.put("item", fallback);
                ingredient.remove("tag");
                stats.tagFallbacks++;
            }
        }
    }

    private static void remapIdsRecursive(Object node, CompatRewriteStats stats) {
        if (node instanceof JSONObject) {
            JSONObject obj = (JSONObject) node;
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                String key = it.next();
                Object value = obj.opt(key);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    remapIdsRecursive(value, stats);
                } else if (value instanceof String) {
                    String str = (String) value;
                    String mapped = remapId(str);
                    if (!mapped.equals(str)) {
                        obj.put(key, mapped);
                        stats.idRemaps++;
                    }
                }
            }
            return;
        }
        if (node instanceof JSONArray) {
            JSONArray arr = (JSONArray) node;
            for (int i = 0, l = arr.length(); i < l; ++i) {
                Object value = arr.opt(i);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    remapIdsRecursive(value, stats);
                } else if (value instanceof String) {
                    String str = (String) value;
                    String mapped = remapId(str);
                    if (!mapped.equals(str)) {
                        arr.put(i, mapped);
                        stats.idRemaps++;
                    }
                }
            }
        }
    }

    private static String remapId(String id) {
        if (id == null || id.length() == 0) {
            return id;
        }
        String mapped = itemIdFallbacks.get(id.toLowerCase());
        return mapped != null ? mapped : id;
    }

    private static String stripJarExtension(String fileName) {
        if (fileName == null) {
            return "unknown_mod";
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jar") && fileName.length() > 4) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }

    private static String normalizePathSegment(String str) {
        if (str == null || str.length() == 0) {
            return "mod";
        }
        StringBuilder b = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.') {
                b.append(c);
            } else {
                b.append('_');
            }
        }
        return b.toString();
    }
}