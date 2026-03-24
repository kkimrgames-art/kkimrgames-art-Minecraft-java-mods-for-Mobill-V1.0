package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenModLogs extends GuiScreen {

	private final GuiScreen parentScreen;
	private final String worldName;
	private final String installFileName;
	private final byte[] installData;
	private boolean installDone = false;
	private boolean installSuccess = false;
	private GuiButton backButton;
	private int scroll = 0;
	private boolean autoScroll = true;
	private String statusLine = "";
	private int filterMode = 0; // 0=All, 1=Errors, 2=Warnings, 3=Info
	private int logViewMode = 0; // 0=Mod Logs, 1=Console Logs
	
	private static final String[] FILTER_NAMES = {"All", "Errors", "Warnings", "Info"};
	private static final int FILTER_COUNT = FILTER_NAMES.length;
	private static final String[] VIEW_NAMES = {"Mod Logs", "Console"};

	public GuiScreenModLogs(GuiScreen parentScreen, String worldName) {
		this(parentScreen, worldName, null, null);
	}

	public GuiScreenModLogs(GuiScreen parentScreen, String worldName, String installFileName, byte[] installData) {
		this.parentScreen = parentScreen;
		this.worldName = worldName;
		this.installFileName = installFileName;
		this.installData = installData;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		String backLabel = installDone ? "Done" : "Back";
		if (installData != null && installFileName != null) {
			backLabel = installDone ? (installSuccess ? "Done ✓" : "Back") : "Installing...";
		}
		this.buttonList.add(backButton = new GuiButton(0, 8, this.height - 28, 70, 20, backLabel));
		this.buttonList.add(new GuiButton(1, 84, this.height - 28, 60, 20, "Copy"));
		this.buttonList.add(new GuiButton(2, 150, this.height - 28, 60, 20, "Clear"));
		this.buttonList.add(new GuiButton(3, 216, this.height - 28, 100, 20, autoScroll ? "AutoScroll: ON" : "AutoScroll: OFF"));
		this.buttonList.add(new GuiButton(4, this.width - 155, this.height - 28, 70, 20, FILTER_NAMES[filterMode]));
		this.buttonList.add(new GuiButton(5, this.width - 80, this.height - 28, 70, 20, VIEW_NAMES[logViewMode]));
	}

	@Override
	public void updateScreen() {
		if (!installDone && installData != null && installFileName != null) {
			installDone = true;
			statusLine = "⏳ Installing " + installFileName + "...";
			ModLogManager.info(worldName, installFileName, "════════════════════════════════════════");
			ModLogManager.info(worldName, installFileName, "🚀 Installation requested: " + installFileName);
			ModLogManager.info(worldName, installFileName, "════════════════════════════════════════");
			try {
				ModManager.addMod(worldName, installFileName, installData);
				installSuccess = true;
				statusLine = "✅ Installation completed successfully!";
				ModLogManager.info(worldName, installFileName, "════════════════════════════════════════");
				ModLogManager.info(worldName, installFileName, "✅ Installation completed successfully");
				ModLogManager.info(worldName, installFileName, "════════════════════════════════════════");
				if (backButton != null) {
					backButton.displayString = "Done ✓";
				}
			} catch (Throwable t) {
				installSuccess = false;
				statusLine = "❌ Installation failed!";
				ModLogManager.error(worldName, installFileName, "════════════════════════════════════════");
				ModLogManager.error(worldName, installFileName, "❌ Installation failed: " + t.getMessage());
				ModLogManager.error(worldName, installFileName, "════════════════════════════════════════", t);
				if (backButton != null) {
					backButton.displayString = "Back";
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			this.mc.displayGuiScreen(parentScreen);
		} else if (button.id == 1) {
			EagRuntime.setClipboard(ModLogManager.getWorldLogsAsText(worldName));
			statusLine = "📋 Logs copied to clipboard";
		} else if (button.id == 2) {
			ModLogManager.clearWorld(worldName);
			scroll = 0;
			statusLine = "🗑️ Logs cleared";
		} else if (button.id == 3) {
			autoScroll = !autoScroll;
			button.displayString = autoScroll ? "AutoScroll: ON" : "AutoScroll: OFF";
		} else if (button.id == 4) {
			filterMode = (filterMode + 1) % FILTER_COUNT;
			button.displayString = FILTER_NAMES[filterMode];
			scroll = 0;
		} else if (button.id == 5) {
			logViewMode = (logViewMode + 1) % VIEW_NAMES.length;
			button.displayString = VIEW_NAMES[logViewMode];
			scroll = 0;
			filterMode = 0;
			// Update filter button
			for (GuiButton btn : this.buttonList) {
				if (btn.id == 4) {
					btn.displayString = FILTER_NAMES[filterMode];
					break;
				}
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int wheel = Mouse.getEventDWheel();
		if (wheel != 0) {
			autoScroll = false;
			if (wheel > 0) {
				scroll = Math.max(0, scroll - 3);
			} else {
				scroll += 3;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		
		// Title
		String title = logViewMode == 0 ? "║     سجلات المودات - " + worldName + "     ║" : "║         سجلات Console         ║";
		this.drawCenteredString(this.fontRendererObj, "╔═══════════════════════════════════════╗", this.width / 2, 4, 0x00AAFF);
		this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 14, 0x00AAFF);
		this.drawCenteredString(this.fontRendererObj, "╚═══════════════════════════════════════╝", this.width / 2, 24, 0x00AAFF);
		
		// Status line
		if (statusLine.length() > 0) {
			int statusColor = 0x99CCFF;
			if (statusLine.startsWith("✅")) {
				statusColor = 0x55FF55;
			} else if (statusLine.startsWith("❌")) {
				statusColor = 0xFF5555;
			} else if (statusLine.startsWith("⏳")) {
				statusColor = 0xFFFF55;
			}
			this.drawCenteredString(this.fontRendererObj, statusLine, this.width / 2, 32, statusColor);
		}

		// Filter info
		String filterInfo = "View: " + VIEW_NAMES[logViewMode] + " | Filter: " + FILTER_NAMES[filterMode];
		int errorCount, warnCount, infoCount;
		if (logViewMode == 0) {
			errorCount = countLogsByType("ERROR");
			warnCount = countLogsByType("WARN");
			infoCount = countLogsByType("INFO");
		} else {
			errorCount = countConsoleLogsByType("ERROR");
			warnCount = countConsoleLogsByType("WARN");
			infoCount = countConsoleLogsByType("INFO");
		}
		filterInfo += " | Errors: " + errorCount + " | Warnings: " + warnCount + " | Info: " + infoCount;
		this.drawCenteredString(this.fontRendererObj, filterInfo, this.width / 2, 40, 0xAAAAAA);

		// Logs area
		int left = 8;
		int right = this.width - 8;
		int top = 48;
		int bottom = this.height - 36;
		drawRect(left - 2, top - 2, right + 2, bottom + 2, 0xAA000000);

		List<String> allLines;
		if (logViewMode == 0) {
			allLines = ModLogManager.getWorldLogs(worldName);
		} else {
			allLines = ModLogManager.getConsoleLogs();
		}
		List<String> filteredLines = filterLogs(allLines);
		List<String> wrappedLines = buildWrappedLines(filteredLines, right - left - 4);
		
		int lineHeight = this.fontRendererObj.FONT_HEIGHT + 1;
		int maxVisible = Math.max(1, (bottom - top) / lineHeight);
		int maxScroll = Math.max(0, wrappedLines.size() - maxVisible);
		if (autoScroll) {
			scroll = maxScroll;
		} else if (scroll > maxScroll) {
			scroll = maxScroll;
		}

		int y = top;
		for (int i = scroll; i < wrappedLines.size() && i < scroll + maxVisible; ++i) {
			String line = wrappedLines.get(i);
			int color = getLineColor(line);
			this.drawString(this.fontRendererObj, line, left, y, color);
			y += lineHeight;
		}
		
		if (wrappedLines.isEmpty()) {
			String emptyMsg = filterMode == 0 ? "No logs yet" : "No " + FILTER_NAMES[filterMode].toLowerCase() + " found";
			this.drawCenteredString(this.fontRendererObj, emptyMsg, this.width / 2, top + 8, 0xAAAAAA);
		}
		
		// Scroll indicator
		if (wrappedLines.size() > maxVisible) {
			String scrollInfo = "Line " + (scroll + 1) + "/" + wrappedLines.size();
			this.drawString(this.fontRendererObj, scrollInfo, right - 60, top - 12, 0x888888);
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private List<String> filterLogs(List<String> logs) {
		if (filterMode == 0) {
			return logs;
		}
		List<String> filtered = new ArrayList<>();
		String filterTag = FILTER_NAMES[filterMode].toUpperCase();
		if (filterTag.equals("ERRORS")) {
			filterTag = "ERROR";
		} else if (filterTag.equals("WARNINGS")) {
			filterTag = "WARN";
		} else if (filterTag.equals("INFO")) {
			filterTag = "INFO";
		}
		for (String line : logs) {
			if (line.contains("[" + filterTag + "]")) {
				filtered.add(line);
			}
		}
		return filtered;
	}
	
	private int countConsoleLogsByType(String type) {
		List<String> logs = ModLogManager.getConsoleLogs();
		int count = 0;
		for (String line : logs) {
			if (line.contains("[" + type + "]")) {
				count++;
			}
		}
		return count;
	}

	private int countLogsByType(String type) {
		List<String> logs = ModLogManager.getWorldLogs(worldName);
		int count = 0;
		for (String line : logs) {
			if (line.contains("[" + type + "]")) {
				count++;
			}
		}
		return count;
	}

	private int getLineColor(String line) {
		if (line.contains("[ERROR]")) {
			return 0xFF5555; // Red for errors
		} else if (line.contains("[WARN]")) {
			return 0xFFFF55; // Yellow for warnings
		} else if (line.contains("[INFO]")) {
			return 0x55FF55; // Green for info
		} else if (line.contains("═══")) {
			return 0x00AAFF; // Blue for separators
		} else if (line.contains("✅")) {
			return 0x55FF55; // Green for success
		} else if (line.contains("❌")) {
			return 0xFF5555; // Red for failure
		} else if (line.contains("⚠️")) {
			return 0xFFFF55; // Yellow for warning emoji
		} else if (line.contains("💡")) {
			return 0x55FFFF; // Cyan for tips
		} else if (line.contains("📊")) {
			return 0xFF55FF; // Magenta for stats
		}
		return 0xE0E0E0; // Default color
	}

	private List<String> buildWrappedLines(List<String> raw, int widthPx) {
		List<String> out = new ArrayList<>();
		for (int i = 0, l = raw.size(); i < l; ++i) {
			List<String> wrapped = this.fontRendererObj.listFormattedStringToWidth(raw.get(i), widthPx);
			if (wrapped == null || wrapped.isEmpty()) {
				out.add("");
			} else {
				out.addAll(wrapped);
			}
		}
		return out;
	}
}
