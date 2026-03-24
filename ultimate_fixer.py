#!/usr/bin/env python3
import zipfile, os, re

BASE = os.path.dirname(os.path.abspath(__file__))
TMP = os.path.join(BASE, "##TEAVM.TMP##")
SRC_JAR = os.path.join(TMP, "MinecraftSrc", "minecraft_src.jar")
OUT_DIR = os.path.join(BASE, "sources", "main", "java", "net", "minecraft", "client", "gui")

def fix_gui_main_menu():
    print(f"Fixing GuiMainMenu.java...")
    with zipfile.ZipFile(SRC_JAR, 'r') as z:
        txt = z.read('net/minecraft/client/gui/GuiMainMenu.java').decode('utf-8')

    # Remove all existing imports except java.io, java.net, java.util
    lines = txt.split('\n')
    new_lines = []
    
    # Keep package
    if lines[0].startswith('package '):
        new_lines.append(lines[0])
        new_lines.append("")
    
    # Add Eaglercraft Imports
    new_lines.extend([
        "import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;",
        "import java.io.BufferedReader;",
        "import java.io.IOException;",
        "import java.io.InputStreamReader;",
        "import java.net.URI;",
        "import java.util.ArrayList;",
        "import java.util.Calendar;",
        "import java.util.Date;",
        "import java.util.List;",
        "import java.util.Random;",
        "import java.util.concurrent.atomic.AtomicInteger;",
        "import net.minecraft.client.Minecraft;",
        "import net.minecraft.client.gui.GuiButton;",
        "import net.minecraft.client.gui.GuiButtonLanguage;",
        "import net.minecraft.client.gui.GuiConfirmOpenLink;",
        "import net.minecraft.client.gui.GuiLanguage;",
        "import net.minecraft.client.gui.GuiMultiplayer;",
        "import net.minecraft.client.gui.GuiOptions;",
        "import net.minecraft.client.gui.GuiScreen;",
        "import net.minecraft.client.gui.GuiSelectWorld;",
        "import net.minecraft.client.gui.GuiYesNo;",
        "import net.minecraft.client.gui.GuiYesNoCallback;",
        "import net.lax1dude.eaglercraft.v1_8.EagRuntime;",
        "import net.lax1dude.eaglercraft.v1_8.EagUtils;",
        "import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;",
        "import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;",
        "import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;",
        "import net.lax1dude.eaglercraft.v1_8.Mouse;",
        "import com.google.common.base.Charsets;",
        "import com.google.common.collect.Lists;",
        "import net.lax1dude.eaglercraft.v1_8.crypto.SHA1Digest;",
        "import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;",
        "import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;",
        "import net.lax1dude.eaglercraft.v1_8.log4j.Logger;",
        "import net.lax1dude.eaglercraft.v1_8.minecraft.MainMenuSkyboxTexture;",
        "import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;",
        "import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;",
        "import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;",
        "import net.lax1dude.eaglercraft.v1_8.profile.GuiScreenEditProfile;",
        "import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;",
        "import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenDemoPlayWorldSelection;",
        "import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerBusy;",
        "import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerStartup;",
        "import net.lax1dude.eaglercraft.v1_8.update.GuiUpdateCheckerOverlay;",
        "import net.lax1dude.eaglercraft.v1_8.update.GuiUpdateVersionSlot;",
        "import net.lax1dude.eaglercraft.v1_8.update.UpdateCertificate;",
        "import net.lax1dude.eaglercraft.v1_8.update.UpdateService;",
        "import net.minecraft.client.audio.PositionedSoundRecord;",
        "import net.minecraft.client.renderer.Tessellator;",
        "import net.minecraft.client.renderer.texture.DynamicTexture;",
        "import net.minecraft.client.renderer.vertex.DefaultVertexFormats;",
        "import net.minecraft.client.resources.I18n;",
        "import net.minecraft.util.EnumChatFormatting;",
        "import net.minecraft.util.MathHelper;",
        "import net.minecraft.util.ResourceLocation;",
        "import net.minecraft.world.demo.DemoWorldServer;",
        "import net.minecraft.world.storage.ISaveFormat;",
        "import net.minecraft.world.storage.WorldInfo;",
        "import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;",
        ""
    ])

    # Skip vanilla imports in the original file
    body_started = False
    for line in lines[1:]:
        if not body_started:
            if line.startswith('import ') or line.strip() == '':
                continue
            body_started = True
        
        # Replace method signatures that cause IOException mismatch
        line = line.replace('void keyTyped(char par1, int par2) {', 'void keyTyped(char par1, int par2) throws IOException {')
        line = line.replace('void actionPerformed(GuiButton par1GuiButton) {', 'void actionPerformed(GuiButton par1GuiButton) throws IOException {')
        line = line.replace('void mouseClicked(int par1, int par2, int par3) {', 'void mouseClicked(int par1, int par2, int par3) throws IOException {')

        # Fix GLContext and other missing symbols globally
        line = line.replace('GLContext.getCapabilities().OpenGL20', 'true') # Eaglercraft is always GL2.0+
        line = line.replace('OpenGlHelper.areShadersSupported()', 'true')
        line = line.replace('Project.gluPerspective', 'EaglercraftGPU.gluPerspective')
        line = line.replace('Charsets.UTF_8', 'StandardCharsets.UTF_8')
        line = line.replace('import java.nio.charset.StandardCharsets;', '') # clean up if duplicate
        line = line.replace('GL11.', '') # Since we imported static PlatformOpenGL
        
        new_lines.append(line)

    # Inject doResourceReloadHack at the end of class
    final_content = '\n'.join(new_lines)
    # Find last }
    idx = final_content.rfind('}')
    if idx != -1:
        hack_method = """
	public static void doResourceReloadHack() {
		shouldReload = true;
	}
"""
        final_content = final_content[:idx] + hack_method + final_content[idx:]

    os.makedirs(OUT_DIR, exist_ok=True)
    with open(os.path.join(OUT_DIR, "GuiMainMenu.java"), 'w', encoding='utf-8') as f:
        f.write(final_content)
    print(f"  Saved fixed GuiMainMenu.java ({len(final_content)} bytes)")

if __name__ == "__main__":
    if not os.path.exists(SRC_JAR):
        print(f"Error: {SRC_JAR} missing! Run the build first.")
    else:
        fix_gui_main_menu()
