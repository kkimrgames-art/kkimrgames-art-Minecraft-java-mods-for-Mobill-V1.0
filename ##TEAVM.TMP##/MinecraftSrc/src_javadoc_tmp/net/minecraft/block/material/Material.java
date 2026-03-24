package net.minecraft.block.material;

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
public class Material {
	public static final Material air = new MaterialTransparent(MapColor.airColor);
	public static final Material grass = new Material(MapColor.grassColor);
	public static final Material ground = new Material(MapColor.dirtColor);
	public static final Material wood = (new Material(MapColor.woodColor)).setBurning();
	public static final Material rock = (new Material(MapColor.stoneColor)).setRequiresTool();
	public static final Material iron = (new Material(MapColor.ironColor)).setRequiresTool();
	public static final Material anvil = (new Material(MapColor.ironColor)).setRequiresTool().setImmovableMobility();
	public static final Material water = (new MaterialLiquid(MapColor.waterColor)).setNoPushMobility();
	public static final Material lava = (new MaterialLiquid(MapColor.tntColor)).setNoPushMobility();
	public static final Material leaves = (new Material(MapColor.foliageColor)).setBurning().setTranslucent()
			.setNoPushMobility();
	public static final Material plants = (new MaterialLogic(MapColor.foliageColor)).setNoPushMobility();
	public static final Material vine = (new MaterialLogic(MapColor.foliageColor)).setBurning().setNoPushMobility()
			.setReplaceable();
	public static final Material sponge = new Material(MapColor.yellowColor);
	public static final Material cloth = (new Material(MapColor.clothColor)).setBurning();
	public static final Material fire = (new MaterialTransparent(MapColor.airColor)).setNoPushMobility();
	public static final Material sand = new Material(MapColor.sandColor);
	public static final Material circuits = (new MaterialLogic(MapColor.airColor)).setNoPushMobility();
	public static final Material carpet = (new MaterialLogic(MapColor.clothColor)).setBurning();
	public static final Material glass = (new Material(MapColor.airColor)).setTranslucent().setAdventureModeExempt();
	public static final Material redstoneLight = (new Material(MapColor.airColor)).setAdventureModeExempt();
	public static final Material tnt = (new Material(MapColor.tntColor)).setBurning().setTranslucent();
	public static final Material coral = (new Material(MapColor.foliageColor)).setNoPushMobility();
	public static final Material ice = (new Material(MapColor.iceColor)).setTranslucent().setAdventureModeExempt();
	public static final Material packedIce = (new Material(MapColor.iceColor)).setAdventureModeExempt();
	public static final Material snow = (new MaterialLogic(MapColor.snowColor)).setReplaceable().setTranslucent()
			.setRequiresTool().setNoPushMobility();
	public static final Material craftedSnow = (new Material(MapColor.snowColor)).setRequiresTool();
	public static final Material cactus = (new Material(MapColor.foliageColor)).setTranslucent().setNoPushMobility();
	public static final Material clay = new Material(MapColor.clayColor);
	public static final Material gourd = (new Material(MapColor.foliageColor)).setNoPushMobility();
	public static final Material dragonEgg = (new Material(MapColor.foliageColor)).setNoPushMobility();
	public static final Material portal = (new MaterialPortal(MapColor.airColor)).setImmovableMobility();
	public static final Material cake = (new Material(MapColor.airColor)).setNoPushMobility();
	public static final Material web = (new Material(MapColor.clothColor) {
		public boolean blocksMovement() {
			return false;
		}
	}).setRequiresTool().setNoPushMobility();
	public static final Material piston = (new Material(MapColor.stoneColor)).setImmovableMobility();
	public static final Material barrier = (new Material(MapColor.airColor)).setRequiresTool().setImmovableMobility();
	private boolean canBurn;
	private boolean replaceable;
	private boolean isTranslucent;
	private final MapColor materialMapColor;
	private boolean requiresNoTool = true;
	private int mobilityFlag;
	private boolean isAdventureModeExempt;

	public Material(MapColor color) {
		this.materialMapColor = color;
	}

	public boolean isLiquid() {
		return false;
	}

	public boolean isSolid() {
		return true;
	}

	public boolean blocksLight() {
		return true;
	}

	public boolean blocksMovement() {
		return true;
	}

	private Material setTranslucent() {
		this.isTranslucent = true;
		return this;
	}

	protected Material setRequiresTool() {
		this.requiresNoTool = false;
		return this;
	}

	protected Material setBurning() {
		this.canBurn = true;
		return this;
	}

	public boolean getCanBurn() {
		return this.canBurn;
	}

	public Material setReplaceable() {
		this.replaceable = true;
		return this;
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	public boolean isOpaque() {
		return this.isTranslucent ? false : this.blocksMovement();
	}

	public boolean isToolNotRequired() {
		return this.requiresNoTool;
	}

	public int getMaterialMobility() {
		return this.mobilityFlag;
	}

	protected Material setNoPushMobility() {
		this.mobilityFlag = 1;
		return this;
	}

	protected Material setImmovableMobility() {
		this.mobilityFlag = 2;
		return this;
	}

	protected Material setAdventureModeExempt() {
		this.isAdventureModeExempt = true;
		return this;
	}

	public MapColor getMaterialMapColor() {
		return this.materialMapColor;
	}
}