package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
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
public class EntitySheep extends EntityAnimal {
	private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
		public boolean canInteractWith(EntityPlayer var1) {
			return false;
		}
	}, 2, 1);
	private static final Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);
	private int sheepTimer;
	private EntityAIEatGrass entityAIEatGrass = new EntityAIEatGrass(this);

	public static float[] func_175513_a(EnumDyeColor dyeColor) {
		return (float[]) DYE_TO_RGB.get(dyeColor);
	}

	public EntitySheep(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 1.3F);
		((PathNavigateGround) this.getNavigator()).setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.wheat, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
		this.tasks.addTask(5, this.entityAIEatGrass);
		this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
		this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
	}

	protected void updateAITasks() {
		this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
		super.updateAITasks();
	}

	public void onLivingUpdate() {
		if (this.worldObj.isRemote) {
			this.sheepTimer = Math.max(0, this.sheepTimer - 1);
		}

		super.onLivingUpdate();
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
	}

	protected void dropFewItems(boolean var1, int i) {
		if (!this.getSheared()) {
			this.entityDropItem(
					new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 0.0F);
		}

		int j = this.rand.nextInt(2) + 1 + this.rand.nextInt(1 + i);

		for (int k = 0; k < j; ++k) {
			if (this.isBurning()) {
				this.dropItem(Items.cooked_mutton, 1);
			} else {
				this.dropItem(Items.mutton, 1);
			}
		}

	}

	protected Item getDropItem() {
		return Item.getItemFromBlock(Blocks.wool);
	}

	public void handleStatusUpdate(byte b0) {
		if (b0 == 10) {
			this.sheepTimer = 40;
		} else {
			super.handleStatusUpdate(b0);
		}

	}

	public float getHeadRotationPointY(float parFloat1) {
		return this.sheepTimer <= 0 ? 0.0F
				: (this.sheepTimer >= 4 && this.sheepTimer <= 36 ? 1.0F
						: (this.sheepTimer < 4 ? ((float) this.sheepTimer - parFloat1) / 4.0F
								: -((float) (this.sheepTimer - 40) - parFloat1) / 4.0F));
	}

	public float getHeadRotationAngleX(float parFloat1) {
		if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
			float f = ((float) (this.sheepTimer - 4) - parFloat1) / 32.0F;
			return 0.62831855F + 0.21991149F * MathHelper.sin(f * 28.7F);
		} else {
			return this.sheepTimer > 0 ? 0.62831855F : this.rotationPitch / 57.295776F;
		}
	}

	public boolean interact(EntityPlayer entityplayer) {
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if (itemstack != null && itemstack.getItem() == Items.shears && !this.getSheared() && !this.isChild()) {
			if (!this.worldObj.isRemote) {
				this.setSheared(true);
				int i = 1 + this.rand.nextInt(3);

				for (int j = 0; j < i; ++j) {
					EntityItem entityitem = this.entityDropItem(
							new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()),
							1.0F);
					entityitem.motionY += (double) (this.rand.nextFloat() * 0.05F);
					entityitem.motionX += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
					entityitem.motionZ += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				}
			}

			itemstack.damageItem(1, entityplayer);
			this.playSound("mob.sheep.shear", 1.0F, 1.0F);
		}

		return super.interact(entityplayer);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Sheared", this.getSheared());
		nbttagcompound.setByte("Color", (byte) this.getFleeceColor().getMetadata());
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.setSheared(nbttagcompound.getBoolean("Sheared"));
		this.setFleeceColor(EnumDyeColor.byMetadata(nbttagcompound.getByte("Color")));
	}

	protected String getLivingSound() {
		return "mob.sheep.say";
	}

	protected String getHurtSound() {
		return "mob.sheep.say";
	}

	protected String getDeathSound() {
		return "mob.sheep.say";
	}

	protected void playStepSound(BlockPos var1, Block var2) {
		this.playSound("mob.sheep.step", 0.15F, 1.0F);
	}

	public EnumDyeColor getFleeceColor() {
		return EnumDyeColor.byMetadata(this.dataWatcher.getWatchableObjectByte(16) & 15);
	}

	public void setFleeceColor(EnumDyeColor color) {
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);
		this.dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 & 240 | color.getMetadata() & 15)));
	}

	public boolean getSheared() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
	}

	public void setSheared(boolean sheared) {
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);
		if (sheared) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 | 16)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 & -17)));
		}

	}

	public static EnumDyeColor getRandomSheepColor(EaglercraftRandom random) {
		int i = random.nextInt(100);
		return i < 5 ? EnumDyeColor.BLACK
				: (i < 10 ? EnumDyeColor.GRAY
						: (i < 15 ? EnumDyeColor.SILVER
								: (i < 18 ? EnumDyeColor.BROWN
										: (random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE))));
	}

	public EntitySheep createChild(EntityAgeable entityageable) {
		EntitySheep entitysheep = (EntitySheep) entityageable;
		EntitySheep entitysheep1 = new EntitySheep(this.worldObj);
		entitysheep1.setFleeceColor(this.getDyeColorMixFromParents(this, entitysheep));
		return entitysheep1;
	}

	public void eatGrassBonus() {
		this.setSheared(false);
		if (this.isChild()) {
			this.addGrowth(60);
		}

	}

	public IEntityLivingData onInitialSpawn(DifficultyInstance difficultyinstance,
			IEntityLivingData ientitylivingdata) {
		ientitylivingdata = super.onInitialSpawn(difficultyinstance, ientitylivingdata);
		this.setFleeceColor(getRandomSheepColor(this.worldObj.rand));
		return ientitylivingdata;
	}

	private EnumDyeColor getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother) {
		int i = ((EntitySheep) father).getFleeceColor().getDyeDamage();
		int j = ((EntitySheep) mother).getFleeceColor().getDyeDamage();
		this.inventoryCrafting.getStackInSlot(0).setItemDamage(i);
		this.inventoryCrafting.getStackInSlot(1).setItemDamage(j);
		ItemStack itemstack = CraftingManager.getInstance().findMatchingRecipe(this.inventoryCrafting,
				((EntitySheep) father).worldObj);
		int k;
		if (itemstack != null && itemstack.getItem() == Items.dye) {
			k = itemstack.getMetadata();
		} else {
			k = this.worldObj.rand.nextBoolean() ? i : j;
		}

		return EnumDyeColor.byDyeDamage(k);
	}

	public float getEyeHeight() {
		return 0.95F * this.height;
	}

	static {
		DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[] { 1.0F, 1.0F, 1.0F });
		DYE_TO_RGB.put(EnumDyeColor.ORANGE, new float[] { 0.85F, 0.5F, 0.2F });
		DYE_TO_RGB.put(EnumDyeColor.MAGENTA, new float[] { 0.7F, 0.3F, 0.85F });
		DYE_TO_RGB.put(EnumDyeColor.LIGHT_BLUE, new float[] { 0.4F, 0.6F, 0.85F });
		DYE_TO_RGB.put(EnumDyeColor.YELLOW, new float[] { 0.9F, 0.9F, 0.2F });
		DYE_TO_RGB.put(EnumDyeColor.LIME, new float[] { 0.5F, 0.8F, 0.1F });
		DYE_TO_RGB.put(EnumDyeColor.PINK, new float[] { 0.95F, 0.5F, 0.65F });
		DYE_TO_RGB.put(EnumDyeColor.GRAY, new float[] { 0.3F, 0.3F, 0.3F });
		DYE_TO_RGB.put(EnumDyeColor.SILVER, new float[] { 0.6F, 0.6F, 0.6F });
		DYE_TO_RGB.put(EnumDyeColor.CYAN, new float[] { 0.3F, 0.5F, 0.6F });
		DYE_TO_RGB.put(EnumDyeColor.PURPLE, new float[] { 0.5F, 0.25F, 0.7F });
		DYE_TO_RGB.put(EnumDyeColor.BLUE, new float[] { 0.2F, 0.3F, 0.7F });
		DYE_TO_RGB.put(EnumDyeColor.BROWN, new float[] { 0.4F, 0.3F, 0.2F });
		DYE_TO_RGB.put(EnumDyeColor.GREEN, new float[] { 0.4F, 0.5F, 0.2F });
		DYE_TO_RGB.put(EnumDyeColor.RED, new float[] { 0.6F, 0.2F, 0.2F });
		DYE_TO_RGB.put(EnumDyeColor.BLACK, new float[] { 0.1F, 0.1F, 0.1F });
	}
}