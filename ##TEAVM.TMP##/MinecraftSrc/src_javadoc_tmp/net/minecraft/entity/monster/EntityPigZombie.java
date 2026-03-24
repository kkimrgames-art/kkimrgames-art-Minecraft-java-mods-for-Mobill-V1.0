package net.minecraft.entity.monster;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
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
public class EntityPigZombie extends EntityZombie {
	private static final EaglercraftUUID ATTACK_SPEED_BOOST_MODIFIER_UUID = EaglercraftUUID
			.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final AttributeModifier ATTACK_SPEED_BOOST_MODIFIER = (new AttributeModifier(
			ATTACK_SPEED_BOOST_MODIFIER_UUID, "Attacking speed boost", 0.05D, 0)).setSaved(false);
	private int angerLevel;
	private int randomSoundDelay;
	private EaglercraftUUID angerTargetUUID;

	public EntityPigZombie(World worldIn) {
		super(worldIn);
		this.isImmuneToFire = true;
	}

	public void setRevengeTarget(EntityLivingBase entitylivingbase) {
		super.setRevengeTarget(entitylivingbase);
		if (entitylivingbase != null) {
			this.angerTargetUUID = entitylivingbase.getUniqueID();
		}

	}

	protected void applyEntityAI() {
		this.targetTasks.addTask(1, new EntityPigZombie.AIHurtByAggressor(this));
		this.targetTasks.addTask(2, new EntityPigZombie.AITargetAggressor(this));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(reinforcementChance).setBaseValue(0.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0D);
	}

	public void onUpdate() {
		super.onUpdate();
	}

	protected void updateAITasks() {
		IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		if (this.isAngry()) {
			if (!this.isChild() && !iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
				iattributeinstance.applyModifier(ATTACK_SPEED_BOOST_MODIFIER);
			}

			--this.angerLevel;
		} else if (iattributeinstance.hasModifier(ATTACK_SPEED_BOOST_MODIFIER)) {
			iattributeinstance.removeModifier(ATTACK_SPEED_BOOST_MODIFIER);
		}

		if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
			this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F,
					((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
		}

		if (this.angerLevel > 0 && this.angerTargetUUID != null && this.getAITarget() == null) {
			EntityPlayer entityplayer = this.worldObj.getPlayerEntityByUUID(this.angerTargetUUID);
			this.setRevengeTarget(entityplayer);
			this.attackingPlayer = entityplayer;
			this.recentlyHit = this.getRevengeTimer();
		}

		super.updateAITasks();
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	public boolean isNotColliding() {
		return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
				&& this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
				&& !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setShort("Anger", (short) this.angerLevel);
		if (this.angerTargetUUID != null) {
			nbttagcompound.setString("HurtBy", this.angerTargetUUID.toString());
		} else {
			nbttagcompound.setString("HurtBy", "");
		}

	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.angerLevel = nbttagcompound.getShort("Anger");
		String s = nbttagcompound.getString("HurtBy");
		if (s.length() > 0) {
			this.angerTargetUUID = EaglercraftUUID.fromString(s);
			EntityPlayer entityplayer = this.worldObj.getPlayerEntityByUUID(this.angerTargetUUID);
			this.setRevengeTarget(entityplayer);
			if (entityplayer != null) {
				this.attackingPlayer = entityplayer;
				this.recentlyHit = this.getRevengeTimer();
			}
		}

	}

	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		if (this.isEntityInvulnerable(damagesource)) {
			return false;
		} else {
			Entity entity = damagesource.getEntity();
			if (entity instanceof EntityPlayer) {
				this.becomeAngryAt(entity);
			}

			return super.attackEntityFrom(damagesource, f);
		}
	}

	private void becomeAngryAt(Entity parEntity) {
		this.angerLevel = 400 + this.rand.nextInt(400);
		this.randomSoundDelay = this.rand.nextInt(40);
		if (parEntity instanceof EntityLivingBase) {
			this.setRevengeTarget((EntityLivingBase) parEntity);
		}

	}

	public boolean isAngry() {
		return this.angerLevel > 0;
	}

	protected String getLivingSound() {
		return "mob.zombiepig.zpig";
	}

	protected String getHurtSound() {
		return "mob.zombiepig.zpighurt";
	}

	protected String getDeathSound() {
		return "mob.zombiepig.zpigdeath";
	}

	protected void dropFewItems(boolean var1, int i) {
		int j = this.rand.nextInt(2 + i);

		for (int k = 0; k < j; ++k) {
			this.dropItem(Items.rotten_flesh, 1);
		}

		j = this.rand.nextInt(2 + i);

		for (int l = 0; l < j; ++l) {
			this.dropItem(Items.gold_nugget, 1);
		}

	}

	public boolean interact(EntityPlayer var1) {
		return false;
	}

	protected void addRandomDrop() {
		this.dropItem(Items.gold_ingot, 1);
	}

	protected void setEquipmentBasedOnDifficulty(DifficultyInstance var1) {
		this.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
	}

	public IEntityLivingData onInitialSpawn(DifficultyInstance difficultyinstance,
			IEntityLivingData ientitylivingdata) {
		super.onInitialSpawn(difficultyinstance, ientitylivingdata);
		this.setVillager(false);
		return ientitylivingdata;
	}

	static class AIHurtByAggressor extends EntityAIHurtByTarget {
		public AIHurtByAggressor(EntityPigZombie parEntityPigZombie) {
			super(parEntityPigZombie, true, new Class[0]);
		}

		protected void setEntityAttackTarget(EntityCreature entitycreature, EntityLivingBase entitylivingbase) {
			super.setEntityAttackTarget(entitycreature, entitylivingbase);
			if (entitycreature instanceof EntityPigZombie) {
				((EntityPigZombie) entitycreature).becomeAngryAt(entitylivingbase);
			}

		}
	}

	static class AITargetAggressor extends EntityAINearestAttackableTarget<EntityPlayer> {
		public AITargetAggressor(EntityPigZombie parEntityPigZombie) {
			super(parEntityPigZombie, EntityPlayer.class, true);
		}

		public boolean shouldExecute() {
			return ((EntityPigZombie) this.taskOwner).isAngry() && super.shouldExecute();
		}
	}
}