package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

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
public class DerivedWorldInfo extends WorldInfo {
	private final WorldInfo theWorldInfo;

	public DerivedWorldInfo(WorldInfo parWorldInfo) {
		this.theWorldInfo = parWorldInfo;
	}

	public NBTTagCompound getNBTTagCompound() {
		return this.theWorldInfo.getNBTTagCompound();
	}

	public NBTTagCompound cloneNBTCompound(NBTTagCompound nbt) {
		return this.theWorldInfo.cloneNBTCompound(nbt);
	}

	public long getSeed() {
		return this.theWorldInfo.getSeed();
	}

	public int getSpawnX() {
		return this.theWorldInfo.getSpawnX();
	}

	public int getSpawnY() {
		return this.theWorldInfo.getSpawnY();
	}

	public int getSpawnZ() {
		return this.theWorldInfo.getSpawnZ();
	}

	public long getWorldTotalTime() {
		return this.theWorldInfo.getWorldTotalTime();
	}

	public long getWorldTime() {
		return this.theWorldInfo.getWorldTime();
	}

	public long getSizeOnDisk() {
		return this.theWorldInfo.getSizeOnDisk();
	}

	public NBTTagCompound getPlayerNBTTagCompound() {
		return this.theWorldInfo.getPlayerNBTTagCompound();
	}

	public String getWorldName() {
		return this.theWorldInfo.getWorldName();
	}

	public int getSaveVersion() {
		return this.theWorldInfo.getSaveVersion();
	}

	public long getLastTimePlayed() {
		return this.theWorldInfo.getLastTimePlayed();
	}

	public boolean isThundering() {
		return this.theWorldInfo.isThundering();
	}

	public int getThunderTime() {
		return this.theWorldInfo.getThunderTime();
	}

	public boolean isRaining() {
		return this.theWorldInfo.isRaining();
	}

	public int getRainTime() {
		return this.theWorldInfo.getRainTime();
	}

	public WorldSettings.GameType getGameType() {
		return this.theWorldInfo.getGameType();
	}

	public void setSpawnX(int x) {
	}

	public void setSpawnY(int y) {
	}

	public void setSpawnZ(int z) {
	}

	public void setWorldTotalTime(long time) {
	}

	public void setWorldTime(long time) {
	}

	public void setSpawn(BlockPos spawnPoint) {
	}

	public void setWorldName(String worldName) {
	}

	public void setSaveVersion(int version) {
	}

	public void setThundering(boolean thunderingIn) {
	}

	public void setThunderTime(int time) {
	}

	public void setRaining(boolean isRaining) {
	}

	public void setRainTime(int time) {
	}

	public boolean isMapFeaturesEnabled() {
		return this.theWorldInfo.isMapFeaturesEnabled();
	}

	public boolean isHardcoreModeEnabled() {
		return this.theWorldInfo.isHardcoreModeEnabled();
	}

	public WorldType getTerrainType() {
		return this.theWorldInfo.getTerrainType();
	}

	public void setTerrainType(WorldType type) {
	}

	public boolean areCommandsAllowed() {
		return this.theWorldInfo.areCommandsAllowed();
	}

	public void setAllowCommands(boolean allow) {
	}

	public boolean isInitialized() {
		return this.theWorldInfo.isInitialized();
	}

	public void setServerInitialized(boolean initializedIn) {
	}

	public GameRules getGameRulesInstance() {
		return this.theWorldInfo.getGameRulesInstance();
	}

	public EnumDifficulty getDifficulty() {
		return this.theWorldInfo.getDifficulty();
	}

	public void setDifficulty(EnumDifficulty newDifficulty) {
	}

	public boolean isDifficultyLocked() {
		return this.theWorldInfo.isDifficultyLocked();
	}

	public void setDifficultyLocked(boolean locked) {
	}
}