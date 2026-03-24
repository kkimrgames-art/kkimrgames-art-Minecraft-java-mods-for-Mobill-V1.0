package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.json.JSONArray;
import org.json.JSONObject;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;

public class GuiScreenModBrowser extends GuiScreen {
    private static final Logger logger = LogManager.getLogger("ModBrowser");
    private static final String SUPABASE_URL = "https://hjzbpbrchkgctxiijpmc.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhqemJwYnJjaGtnY3R4aWlqcG1jIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQyMDkzMjEsImV4cCI6MjA4OTc4NTMyMX0.fcKEnVcugLBcjd-POKv-iP4O8VER0nyxTKytTkYnmj0";
    
    private final GuiScreen parentScreen;
    private final String worldName;
    private boolean loading = true;
    private boolean error = false;
    private String statusMsg = "Loading mods from database...";
    
    private List<ModInfo> availableMods = new ArrayList<>();
    
    public static class ModInfo {
        public String id;
        public String modId;
        public String name;
        public String version;
        public String description;
        public String bucketPath;
        public boolean downloading = false;
        public boolean installed = false;
    }

    public GuiScreenModBrowser(GuiScreen parent, String worldName) {
        this.parentScreen = parent;
        this.worldName = worldName;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, "Done"));
        
        if (loading && availableMods.isEmpty() && !error) {
            fetchModsAsync();
        } else {
            updateModButtons();
        }
    }
    
    private void updateModButtons() {
        this.buttonList.removeIf(b -> b.id != 0);
        int y = 50;
        int i = 1;
        for (ModInfo mod : availableMods) {
            String btnText = mod.downloading ? "Downloading..." : (mod.installed ? "Installed" : "Download");
            GuiButton btn = new GuiButton(i, this.width / 2 + 50, y, 100, 20, btnText);
            btn.enabled = !mod.downloading && !mod.installed;
            this.buttonList.add(btn);
            y += 30;
            i++;
        }
    }

    private void fetchModsAsync() {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "/rest/v1/mods?select=*");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                conn.setRequestProperty("Accept", "application/json");
                
                int code = conn.getResponseCode();
                if (code == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                    in.close();
                    
                    JSONArray arr = new JSONArray(response.toString());
                    List<ModInfo> list = new ArrayList<>();
                    for (int j = 0; j < arr.length(); j++) {
                        JSONObject obj = arr.getJSONObject(j);
                        ModInfo m = new ModInfo();
                        m.id = obj.optString("id");
                        m.modId = obj.optString("mod_id");
                        m.name = obj.optString("name");
                        m.version = obj.optString("version");
                        m.description = obj.optString("description");
                        m.bucketPath = obj.optString("bucket_path");
                        
                        VFile2 modFile = new VFile2("mods", worldName, m.bucketPath);
                        m.installed = modFile.exists();
                        list.add(m);
                    }
                    this.availableMods = list;
                    this.loading = false;
                } else {
                    this.error = true;
                    this.statusMsg = "Error: " + code;
                }
            } catch (Exception e) {
                logger.error("Failed to fetch mods", e);
                this.error = true;
                this.statusMsg = "Failed to connect to database";
            }
        }).start();
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id > 0 && button.id <= availableMods.size()) {
            ModInfo mod = availableMods.get(button.id - 1);
            if (!mod.installed && !mod.downloading) {
                downloadModAsync(mod, button.id);
            }
        }
    }
    
    private void downloadModAsync(ModInfo mod, int btnId) {
        mod.downloading = true;
        updateModButtons();
        new Thread(() -> {
            try {
                String encodedPath = mod.bucketPath.replace(" ", "%20");
                URL url = new URL(SUPABASE_URL + "/storage/v1/object/public/mods/" + encodedPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream in = conn.getInputStream();
                    byte[] data = EaglerInputStream.inputStreamToBytesNoClose(in);
                    in.close();
                    
                    VFile2 modFile = new VFile2("mods", worldName, mod.bucketPath);
                    modFile.setAllBytes(data);
                    
                    mod.downloading = false;
                    mod.installed = true;
                    ModLogManager.info(worldName, mod.bucketPath, "Downloaded from Mod Store successfully");
                } else {
                    logger.error("Mod download failed HTTP code: " + code);
                    mod.downloading = false;
                }
            } catch (Exception e) {
                logger.error("Failed to download mod: " + mod.name, e);
                mod.downloading = false;
            }
        }).start();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Mod Browser (Online) - " + worldName, this.width / 2, 15, 16777215);
        
        if (loading) {
            this.drawCenteredString(this.fontRendererObj, statusMsg, this.width / 2, this.height / 2, 0xAAAAAA);
        } else if (error) {
            this.drawCenteredString(this.fontRendererObj, statusMsg, this.width / 2, this.height / 2, 0xFF5555);
        } else if (availableMods.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, "No mods available in database.", this.width / 2, this.height / 2, 0xAAAAAA);
        } else {
            updateModButtons();
            int y = 50;
            for (ModInfo mod : availableMods) {
                this.drawString(this.fontRendererObj, mod.name + " v" + mod.version, this.width / 2 - 150, y + 2, 0x55FF55);
                this.drawString(this.fontRendererObj, mod.description, this.width / 2 - 150, y + 12, 0xAAAAAA);
                y += 30;
            }
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
