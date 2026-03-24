package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

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
public interface INetHandlerPlayServer extends INetHandler {
	void handleAnimation(C0APacketAnimation var1);

	void processChatMessage(C01PacketChatMessage var1);

	void processTabComplete(C14PacketTabComplete var1);

	void processClientStatus(C16PacketClientStatus var1);

	void processClientSettings(C15PacketClientSettings var1);

	void processConfirmTransaction(C0FPacketConfirmTransaction var1);

	void processEnchantItem(C11PacketEnchantItem var1);

	void processClickWindow(C0EPacketClickWindow var1);

	void processCloseWindow(C0DPacketCloseWindow var1);

	void processVanilla250Packet(C17PacketCustomPayload var1);

	void processUseEntity(C02PacketUseEntity var1);

	void processKeepAlive(C00PacketKeepAlive var1);

	void processPlayer(C03PacketPlayer var1);

	void processPlayerAbilities(C13PacketPlayerAbilities var1);

	void processPlayerDigging(C07PacketPlayerDigging var1);

	void processEntityAction(C0BPacketEntityAction var1);

	void processInput(C0CPacketInput var1);

	void processHeldItemChange(C09PacketHeldItemChange var1);

	void processCreativeInventoryAction(C10PacketCreativeInventoryAction var1);

	void processUpdateSign(C12PacketUpdateSign var1);

	void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement var1);

	void handleSpectate(C18PacketSpectate var1);

	void handleResourcePackStatus(C19PacketResourcePackStatus var1);
}