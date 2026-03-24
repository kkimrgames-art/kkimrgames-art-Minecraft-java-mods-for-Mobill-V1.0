package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

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
public class EntityMagmaCube extends EntitySlime {
	public EntityMagmaCube(World worldIn) {
		super(worldIn);
		this.isImmuneToFire = true;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	public boolean isNotColliding() {
		return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
				&& this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
				&& !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
	}

	public int getTotalArmorValue() {
		return this.getSlimeSize() * 3;
	}

	public int getBrightnessForRender(float var1) {
		return 15728880;
	}

	public float getBrightness(float var1) {
		return 1.0F;
	}

	protected float getEaglerDynamicLightsValueSimple(float partialTicks) {
		return 1.0f;
	}

	protected EnumParticleTypes getParticleType() {
		return EnumParticleTypes.FLAME;
	}

	protected EntitySlime createInstance() {
		return new EntityMagmaCube(this.worldObj);
	}

	protected Item getDropItem() {
		return Items.magma_cream;
	}

	protected void dropFewItems(boolean var1, int i) {
		Item item = this.getDropItem();
		if (item != null && this.getSlimeSize() > 1) {
			int j = this.rand.nextInt(4) - 2;
			if (i > 0) {
				j += this.rand.nextInt(i + 1);
			}

			for (int k = 0; k < j; ++k) {
				this.dropItem(item, 1);
			}
		}

	}

	public boolean isBurning() {
		return false;
	}

	protected int getJumpDelay() {
		return super.getJumpDelay() * 4;
	}

	protected void alterSquishAmount() {
		this.squishAmount *= 0.9F;
	}

	protected void jump() {
		this.motionY = (double) (0.42F + (float) this.getSlimeSize() * 0.1F);
		this.isAirBorne = true;
	}

	protected void handleJumpLava() {
		this.motionY = (double) (0.22F + (float) this.getSlimeSize() * 0.05F);
		this.isAirBorne = true;
	}

	public void fall(float var1, float var2) {
	}

	protected boolean canDamagePlayer() {
		return true;
	}

	protected int getAttackStrength() {
		return super.getAttackStrength() + 2;
	}

	protected String getJumpSound() {
		return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
	}

	protected boolean makesSoundOnLand() {
		return true;
	}
}