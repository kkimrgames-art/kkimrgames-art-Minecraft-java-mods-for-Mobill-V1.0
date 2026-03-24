package net.minecraft.inventory;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

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
public class InventoryBasic implements IInventory {
	private String inventoryTitle;
	private int slotsCount;
	private ItemStack[] inventoryContents;
	private List<IInvBasic> field_70480_d;
	private boolean hasCustomName;

	public InventoryBasic(String title, boolean customName, int slotCount) {
		this.inventoryTitle = title;
		this.hasCustomName = customName;
		this.slotsCount = slotCount;
		this.inventoryContents = new ItemStack[slotCount];
	}

	public InventoryBasic(IChatComponent title, int slotCount) {
		this(title.getUnformattedText(), true, slotCount);
	}

	public void func_110134_a(IInvBasic parIInvBasic) {
		if (this.field_70480_d == null) {
			this.field_70480_d = Lists.newArrayList();
		}

		this.field_70480_d.add(parIInvBasic);
	}

	public void func_110132_b(IInvBasic parIInvBasic) {
		this.field_70480_d.remove(parIInvBasic);
	}

	public ItemStack getStackInSlot(int i) {
		return i >= 0 && i < this.inventoryContents.length ? this.inventoryContents[i] : null;
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.inventoryContents[i] != null) {
			if (this.inventoryContents[i].stackSize <= j) {
				ItemStack itemstack1 = this.inventoryContents[i];
				this.inventoryContents[i] = null;
				this.markDirty();
				return itemstack1;
			} else {
				ItemStack itemstack = this.inventoryContents[i].splitStack(j);
				if (this.inventoryContents[i].stackSize == 0) {
					this.inventoryContents[i] = null;
				}

				this.markDirty();
				return itemstack;
			}
		} else {
			return null;
		}
	}

	public ItemStack func_174894_a(ItemStack stack) {
		ItemStack itemstack = stack.copy();

		for (int i = 0; i < this.slotsCount; ++i) {
			ItemStack itemstack1 = this.getStackInSlot(i);
			if (itemstack1 == null) {
				this.setInventorySlotContents(i, itemstack);
				this.markDirty();
				return null;
			}

			if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
				int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
				int k = Math.min(itemstack.stackSize, j - itemstack1.stackSize);
				if (k > 0) {
					itemstack1.stackSize += k;
					itemstack.stackSize -= k;
					if (itemstack.stackSize <= 0) {
						this.markDirty();
						return null;
					}
				}
			}
		}

		if (itemstack.stackSize != stack.stackSize) {
			this.markDirty();
		}

		return itemstack;
	}

	public ItemStack removeStackFromSlot(int i) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack = this.inventoryContents[i];
			this.inventoryContents[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.inventoryContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	public int getSizeInventory() {
		return this.slotsCount;
	}

	public String getName() {
		return this.inventoryTitle;
	}

	public boolean hasCustomName() {
		return this.hasCustomName;
	}

	public void setCustomName(String inventoryTitleIn) {
		this.hasCustomName = true;
		this.inventoryTitle = inventoryTitleIn;
	}

	public IChatComponent getDisplayName() {
		return (IChatComponent) (this.hasCustomName() ? new ChatComponentText(this.getName())
				: new ChatComponentTranslation(this.getName(), new Object[0]));
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void markDirty() {
		if (this.field_70480_d != null) {
			for (int i = 0; i < this.field_70480_d.size(); ++i) {
				((IInvBasic) this.field_70480_d.get(i)).onInventoryChanged(this);
			}
		}

	}

	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	public void openInventory(EntityPlayer var1) {
	}

	public void closeInventory(EntityPlayer var1) {
	}

	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	public int getField(int var1) {
		return 0;
	}

	public void setField(int var1, int var2) {
	}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		for (int i = 0; i < this.inventoryContents.length; ++i) {
			this.inventoryContents[i] = null;
		}

	}
}