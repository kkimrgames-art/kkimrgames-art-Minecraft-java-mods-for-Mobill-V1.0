package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

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
public interface IWorldAccess {
	void markBlockForUpdate(BlockPos var1);

	void notifyLightSet(BlockPos var1);

	void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6);

	void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

	void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9,
			float var10);

	void spawnParticle(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11,
			double var13, int... var15);

	void onEntityAdded(Entity var1);

	void onEntityRemoved(Entity var1);

	void playRecord(String var1, BlockPos var2);

	void broadcastSound(int var1, BlockPos var2, int var3);

	void playAuxSFX(EntityPlayer var1, int var2, BlockPos var3, int var4);

	void sendBlockBreakProgress(int var1, BlockPos var2, int var3);
}