package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
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
public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
	protected Block spawnableBlock = Blocks.grass;
	private int inLove;
	private EntityPlayer playerInLove;

	public EntityAnimal(World worldIn) {
		super(worldIn);
	}

	protected void updateAITasks() {
		if (this.getGrowingAge() != 0) {
			this.inLove = 0;
		}

		super.updateAITasks();
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.getGrowingAge() != 0) {
			this.inLove = 0;
		}

		if (this.inLove > 0) {
			--this.inLove;
			if (this.inLove % 10 == 0) {
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				double d2 = this.rand.nextGaussian() * 0.02D;
				this.worldObj.spawnParticle(EnumParticleTypes.HEART,
						this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
						this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height),
						this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1,
						d2, new int[0]);
			}
		}

	}

	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		if (this.isEntityInvulnerable(damagesource)) {
			return false;
		} else {
			this.inLove = 0;
			return super.attackEntityFrom(damagesource, f);
		}
	}

	public float getBlockPathWeight(BlockPos blockpos) {
		return this.worldObj.getBlockState(blockpos.down()).getBlock() == Blocks.grass ? 10.0F
				: this.worldObj.getLightBrightness(blockpos) - 0.5F;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("InLove", this.inLove);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.inLove = nbttagcompound.getInteger("InLove");
	}

	public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor_double(this.posZ);
		BlockPos blockpos = new BlockPos(i, j, k);
		return this.worldObj.getBlockState(blockpos.down()).getBlock() == this.spawnableBlock
				&& this.worldObj.getLight(blockpos) > 8 && super.getCanSpawnHere();
	}

	public int getTalkInterval() {
		return 120;
	}

	protected boolean canDespawn() {
		return false;
	}

	protected int getExperiencePoints(EntityPlayer var1) {
		return 1 + this.worldObj.rand.nextInt(3);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack == null ? false : stack.getItem() == Items.wheat;
	}

	public boolean interact(EntityPlayer entityplayer) {
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if (itemstack != null) {
			if (this.isBreedingItem(itemstack) && this.getGrowingAge() == 0 && this.inLove <= 0) {
				this.consumeItemFromStack(entityplayer, itemstack);
				this.setInLove(entityplayer);
				return true;
			}

			if (this.isChild() && this.isBreedingItem(itemstack)) {
				this.consumeItemFromStack(entityplayer, itemstack);
				this.func_175501_a((int) ((float) (-this.getGrowingAge() / 20) * 0.1F), true);
				return true;
			}
		}

		return super.interact(entityplayer);
	}

	protected void consumeItemFromStack(EntityPlayer player, ItemStack stack) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
			if (stack.stackSize <= 0) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
			}
		}

	}

	public void setInLove(EntityPlayer player) {
		this.inLove = 600;
		this.playerInLove = player;
		this.worldObj.setEntityState(this, (byte) 18);
	}

	public EntityPlayer getPlayerInLove() {
		return this.playerInLove;
	}

	public boolean isInLove() {
		return this.inLove > 0;
	}

	public void resetInLove() {
		this.inLove = 0;
	}

	public boolean canMateWith(EntityAnimal otherAnimal) {
		return otherAnimal == this ? false
				: (otherAnimal.getClass() != this.getClass() ? false : this.isInLove() && otherAnimal.isInLove());
	}

	public void handleStatusUpdate(byte b0) {
		if (b0 == 18) {
			for (int i = 0; i < 7; ++i) {
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				double d2 = this.rand.nextGaussian() * 0.02D;
				this.worldObj.spawnParticle(EnumParticleTypes.HEART,
						this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
						this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height),
						this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1,
						d2, new int[0]);
			}
		} else {
			super.handleStatusUpdate(b0);
		}

	}
}