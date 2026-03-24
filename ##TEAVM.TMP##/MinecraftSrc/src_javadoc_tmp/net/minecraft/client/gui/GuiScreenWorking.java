package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
	private String field_146591_a = "";
	private String field_146589_f = "";
	private int progress;
	private boolean doneWorking;

	public void displaySavingString(String s) {
		this.resetProgressAndMessage(s);
	}

	public void resetProgressAndMessage(String s) {
		this.field_146591_a = s;
		this.displayLoadingString("Working...");
	}

	public void displayLoadingString(String s) {
		this.field_146589_f = s;
		this.setLoadingProgress(0);
	}

	public void setLoadingProgress(int i) {
		this.progress = i;
	}

	public void setDoneWorking() {
		this.doneWorking = true;
	}

	public void drawScreen(int i, int j, float f) {
		if (this.doneWorking) {
			if (!this.mc.func_181540_al()) {
				this.mc.displayGuiScreen((GuiScreen) null);
			}

		} else {
			this.drawDefaultBackground();
			this.drawCenteredString(this.fontRendererObj, this.field_146591_a, this.width / 2, 70, 16777215);
			this.drawCenteredString(this.fontRendererObj, this.field_146589_f + " " + this.progress + "%",
					this.width / 2, 90, 16777215);
			super.drawScreen(i, j, f);
		}
	}
}