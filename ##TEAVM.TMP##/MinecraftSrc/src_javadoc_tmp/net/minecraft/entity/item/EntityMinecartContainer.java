package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
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
public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
	private ItemStack[] minecartContainerItems = new ItemStack[36];
	private boolean dropContentsWhenDead = true;

	public EntityMinecartContainer(World worldIn) {
		super(worldIn);
	}

	public EntityMinecartContainer(World worldIn, double parDouble1, double parDouble2, double parDouble3) {
		super(worldIn, parDouble1, parDouble2, parDouble3);
	}

	public void killMinecart(DamageSource damagesource) {
		super.killMinecart(damagesource);
		if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
			InventoryHelper.func_180176_a(this.worldObj, this, this);
		}

	}

	public ItemStack getStackInSlot(int i) {
		return this.minecartContainerItems[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.minecartContainerItems[i] != null) {
			if (this.minecartContainerItems[i].stackSize <= j) {
				ItemStack itemstack1 = this.minecartContainerItems[i];
				this.minecartContainerItems[i] = null;
				return itemstack1;
			} else {
				ItemStack itemstack = this.minecartContainerItems[i].splitStack(j);
				if (this.minecartContainerItems[i].stackSize == 0) {
					this.minecartContainerItems[i] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	public ItemStack removeStackFromSlot(int i) {
		if (this.minecartContainerItems[i] != null) {
			ItemStack itemstack = this.minecartContainerItems[i];
			this.minecartContainerItems[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.minecartContainerItems[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

	}

	public void markDirty() {
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return this.isDead ? false : entityplayer.getDistanceSqToEntity(this) <= 64.0D;
	}

	public void openInventory(EntityPlayer var1) {
	}

	public void closeInventory(EntityPlayer var1) {
	}

	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	public String getName() {
		return this.hasCustomName() ? this.getCustomNameTag() : "container.minecart";
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void travelToDimension(int i) {
		this.dropContentsWhenDead = false;
		super.travelToDimension(i);
	}

	public void setDead() {
		if (this.dropContentsWhenDead) {
			InventoryHelper.func_180176_a(this.worldObj, this, this);
		}

		super.setDead();
	}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.minecartContainerItems.length; ++i) {
			if (this.minecartContainerItems[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.minecartContainerItems[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
	}

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
		this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			if (j >= 0 && j < this.minecartContainerItems.length) {
				this.minecartContainerItems[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

	}

	public boolean interactFirst(EntityPlayer entityplayer) {
		if (!this.worldObj.isRemote) {
			entityplayer.displayGUIChest(this);
		}

		return true;
	}

	protected void applyDrag() {
		int i = 15 - Container.calcRedstoneFromInventory(this);
		float f = 0.98F + (float) i * 0.001F;
		this.motionX *= (double) f;
		this.motionY *= 0.0D;
		this.motionZ *= (double) f;
	}

	public int getField(int var1) {
		return 0;
	}

	public void setField(int var1, int var2) {
	}

	public int getFieldCount() {
		return 0;
	}

	public boolean isLocked() {
		return false;
	}

	public void setLockCode(LockCode var1) {
	}

	public LockCode getLockCode() {
		return LockCode.EMPTY_CODE;
	}

	public void clear() {
		for (int i = 0; i < this.minecartContainerItems.length; ++i) {
			this.minecartContainerItems[i] = null;
		}

	}
}