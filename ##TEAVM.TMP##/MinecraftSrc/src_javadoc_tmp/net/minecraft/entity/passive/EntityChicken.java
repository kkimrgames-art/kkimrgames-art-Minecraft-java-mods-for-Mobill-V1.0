package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
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
public class EntityChicken extends EntityAnimal {
	public float wingRotation;
	public float destPos;
	public float field_70884_g;
	public float field_70888_h;
	public float wingRotDelta = 1.0F;
	public int timeUntilNextEgg;
	public boolean chickenJockey;

	public EntityChicken(World worldIn) {
		super(worldIn);
		this.setSize(0.4F, 0.7F);
		this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.4D));
		this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
		this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	public float getEyeHeight() {
		return this.height;
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.field_70888_h = this.wingRotation;
		this.field_70884_g = this.destPos;
		this.destPos = (float) ((double) this.destPos + (double) (this.onGround ? -1 : 4) * 0.3D);
		this.destPos = MathHelper.clamp_float(this.destPos, 0.0F, 1.0F);
		if (!this.onGround && this.wingRotDelta < 1.0F) {
			this.wingRotDelta = 1.0F;
		}

		this.wingRotDelta = (float) ((double) this.wingRotDelta * 0.9D);
		if (!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		this.wingRotation += this.wingRotDelta * 2.0F;
		if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 0) {
			this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Items.egg, 1);
			this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		}

	}

	public void fall(float var1, float var2) {
	}

	protected String getLivingSound() {
		return "mob.chicken.say";
	}

	protected String getHurtSound() {
		return "mob.chicken.hurt";
	}

	protected String getDeathSound() {
		return "mob.chicken.hurt";
	}

	protected void playStepSound(BlockPos var1, Block var2) {
		this.playSound("mob.chicken.step", 0.15F, 1.0F);
	}

	protected Item getDropItem() {
		return Items.feather;
	}

	protected void dropFewItems(boolean var1, int i) {
		int j = this.rand.nextInt(3) + this.rand.nextInt(1 + i);

		for (int k = 0; k < j; ++k) {
			this.dropItem(Items.feather, 1);
		}

		if (this.isBurning()) {
			this.dropItem(Items.cooked_chicken, 1);
		} else {
			this.dropItem(Items.chicken, 1);
		}

	}

	public EntityChicken createChild(EntityAgeable var1) {
		return new EntityChicken(this.worldObj);
	}

	public boolean isBreedingItem(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Items.wheat_seeds;
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.chickenJockey = nbttagcompound.getBoolean("IsChickenJockey");
		if (nbttagcompound.hasKey("EggLayTime")) {
			this.timeUntilNextEgg = nbttagcompound.getInteger("EggLayTime");
		}

	}

	protected int getExperiencePoints(EntityPlayer entityplayer) {
		return this.isChickenJockey() ? 10 : super.getExperiencePoints(entityplayer);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("IsChickenJockey", this.chickenJockey);
		nbttagcompound.setInteger("EggLayTime", this.timeUntilNextEgg);
	}

	protected boolean canDespawn() {
		return this.isChickenJockey() && this.riddenByEntity == null;
	}

	public void updateRiderPosition() {
		super.updateRiderPosition();
		float f = MathHelper.sin(this.renderYawOffset * 3.1415927F / 180.0F);
		float f1 = MathHelper.cos(this.renderYawOffset * 3.1415927F / 180.0F);
		float f2 = 0.1F;
		float f3 = 0.0F;
		this.riddenByEntity.setPosition(this.posX + (double) (f2 * f),
				this.posY + (double) (this.height * 0.5F) + this.riddenByEntity.getYOffset() + (double) f3,
				this.posZ - (double) (f2 * f1));
		if (this.riddenByEntity instanceof EntityLivingBase) {
			((EntityLivingBase) this.riddenByEntity).renderYawOffset = this.renderYawOffset;
		}

	}

	public boolean isChickenJockey() {
		return this.chickenJockey;
	}

	public void setChickenJockey(boolean jockey) {
		this.chickenJockey = jockey;
	}
}