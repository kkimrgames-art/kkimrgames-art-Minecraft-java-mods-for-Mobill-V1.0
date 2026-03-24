package net.lax1dude.eaglercraft.v1_8.sp.gui;

import java.lang.reflect.Field;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenModManagement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiCreateWorldWithMods extends GuiCreateWorld {

	private static final int BUTTON_MODS = 204;

	public GuiCreateWorldWithMods(GuiScreen parent) {
		super(parent);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(BUTTON_MODS, this.width / 2 - 100, this.height / 4 + 168, "Mods"));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == BUTTON_MODS) {
			this.mc.displayGuiScreen(new GuiScreenModManagement(this, resolveWorldFolderName()));
			return;
		}
		super.actionPerformed(button);
	}

	private String resolveWorldFolderName() {
		String worldDisplayName = "New World";
		try {
			Field textFieldField = GuiCreateWorld.class.getDeclaredField("field_146333_g");
			textFieldField.setAccessible(true);
			Object textFieldObj = textFieldField.get(this);
			if (textFieldObj instanceof GuiTextField) {
				String txt = ((GuiTextField) textFieldObj).getText();
				if (txt != null && txt.trim().length() > 0) {
					worldDisplayName = txt.trim();
				}
			}
		} catch (Throwable ignored) {
		}
		try {
			return GuiCreateWorld.func_146317_a(this.mc.getSaveLoader(), worldDisplayName);
		} catch (Throwable ignored) {
			return worldDisplayName;
		}
	}
}
