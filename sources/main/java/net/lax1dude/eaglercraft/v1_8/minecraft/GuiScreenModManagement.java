package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.io.IOException;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.forge.ForgeDataRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.FileChooserResult;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenModManagement extends GuiScreen {

    private final GuiScreen parentScreen;
    private final String worldName;
    private List<ModManager.ModEntry> mods;
    private int lastModsRevision = -1;

    public GuiScreenModManagement(GuiScreen parentScreen, String worldName) {
        this.parentScreen = parentScreen;
        this.worldName = worldName;
    }

    @Override
    public void initGui() {
        this.mods = ModManager.getWorldMods(worldName);
        this.lastModsRevision = ModManager.getWorldModsRevision(worldName);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 48, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 32, 95, 20, "Add Mod (.jar)"));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 5, 32, 95, 20, "Mod Browser"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height - 72, "Logs"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Mod Management - " + worldName, this.width / 2, 8, 16777215);
        String runtimeInfo = "runtime mods=" + ForgeDataRuntime.getLoadedModsCount() + " data=" + ForgeDataRuntime.getLoadedDataFilesCount()
                + " recipes=" + ForgeDataRuntime.getRecipesCount() + " tags=" + ForgeDataRuntime.getTagsCount()
                + " worldgen=" + ForgeDataRuntime.getWorldgenCount();
        this.drawCenteredString(this.fontRendererObj, runtimeInfo, this.width / 2, 20, 0x9999FF);

        String statusMsg = ModLogManager.getWorldStatusMessage(worldName);
        String statusLvl = ModLogManager.getWorldStatusLevel(worldName);
        int statusAge = ModLogManager.getWorldStatusAgeSeconds(worldName);
        if (statusMsg != null && statusMsg.length() > 0) {
            int color = 0x99CCFF;
            if ("ERROR".equalsIgnoreCase(statusLvl)) {
                color = 0xFF6666;
            } else if ("WARN".equalsIgnoreCase(statusLvl)) {
                color = 0xFFAA55;
            } else if ("INFO".equalsIgnoreCase(statusLvl)) {
                color = 0x55FF55;
            }
            String ageTxt = statusAge >= 0 ? (" (قبل " + statusAge + " ث)") : "";
            this.drawCenteredString(this.fontRendererObj, statusMsg + ageTxt, this.width / 2, 44, color);
        }
        
        int y = (statusMsg != null && statusMsg.length() > 0) ? 74 : 60;
        if (mods.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "No mods added yet.", this.width / 2, y, 0xAAAAAA);
        } else {
            for (ModManager.ModEntry mod : mods) {
                String displayName = mod.name;
                if (mod.metadata != null && mod.metadata.displayName != null && mod.metadata.displayName.length() > 0) {
                    displayName = mod.metadata.displayName + " (" + mod.name + ")";
                }
                this.drawString(this.fontRendererObj, displayName, this.width / 2 - 150, y, mod.compatible ? 0x55FF55 : 0xFF6666);
                this.drawString(this.fontRendererObj, "[Delete]", this.width / 2 + 100, y, 0xFFFF0000);
                if (mod.metadata != null) {
                    String meta = "modId=" + valueOrUnknown(mod.metadata.modId) + " loader=" + valueOrUnknown(mod.metadata.modLoader);
                    this.drawString(this.fontRendererObj, meta, this.width / 2 - 140, y + 10, 0xAAAAAA);
                    y += 10;
                    String dataMeta = "recipes=" + mod.metadata.recipesCount + " tags=" + mod.metadata.tagsCount + " worldgen=" + mod.metadata.worldgenCount;
                    this.drawString(this.fontRendererObj, dataMeta, this.width / 2 - 140, y + 10, 0x8888AA);
                    y += 10;
                }
                if (!mod.issues.isEmpty()) {
                    this.drawString(this.fontRendererObj, mod.issues.get(0), this.width / 2 - 140, y + 10, 0xFFAA55);
                    if (mod.issues.size() > 1) {
                        this.drawString(this.fontRendererObj, "+" + (mod.issues.size() - 1) + " more", this.width / 2 - 140, y + 20, 0xAA8855);
                        y += 10;
                    }
                    y += 10;
                }
                y += 20;
                if (y > this.height - 70) {
                    this.drawCenteredString(this.fontRendererObj, "...", this.width / 2, y, 0xAAAAAA);
                    break;
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id == 1) {
            EagRuntime.displayFileChooser("application/java-archive", "jar");
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiScreenModLogs(this, worldName));
        } else if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiScreenModBrowser(this, worldName));
        }
    }

    @Override
    public void updateScreen() {
        int rev = ModManager.getWorldModsRevision(worldName);
        if (rev != lastModsRevision) {
            this.initGui();
            return;
        }
        if (EagRuntime.fileChooserHasResult()) {
            FileChooserResult result = EagRuntime.getFileChooserResult();
            if (result != null) {
                ModLogManager.info(worldName, result.fileName, "File selected for install");
                EagRuntime.clearFileChooserResult();
                this.mc.displayGuiScreen(new GuiScreenModLogs(this, worldName, result.fileName, result.fileData));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int y = 60;
        for (ModManager.ModEntry mod : mods) {
            if (mouseX > this.width / 2 + 90 && mouseX < this.width / 2 + 150 && mouseY > y - 5 && mouseY < y + 15) {
                ModManager.deleteMod(worldName, mod.name);
                this.initGui();
                break;
            }
            if (mod.metadata != null) {
                y += 20;
            }
            if (!mod.issues.isEmpty()) {
                y += 10;
                if (mod.issues.size() > 1) {
                    y += 10;
                }
            }
            y += 20;
        }
    }

    private String valueOrUnknown(String value) {
        return value == null || value.length() == 0 ? "unknown" : value;
    }
}
