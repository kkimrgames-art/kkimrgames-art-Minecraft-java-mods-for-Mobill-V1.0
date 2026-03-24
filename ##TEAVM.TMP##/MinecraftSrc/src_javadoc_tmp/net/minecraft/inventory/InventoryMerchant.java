package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

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
public class InventoryMerchant implements IInventory {
	private final IMerchant theMerchant;
	private ItemStack[] theInventory = new ItemStack[3];
	private final EntityPlayer thePlayer;
	private MerchantRecipe currentRecipe;
	private int currentRecipeIndex;

	public InventoryMerchant(EntityPlayer thePlayerIn, IMerchant theMerchantIn) {
		this.thePlayer = thePlayerIn;
		this.theMerchant = theMerchantIn;
	}

	public int getSizeInventory() {
		return this.theInventory.length;
	}

	public ItemStack getStackInSlot(int i) {
		return this.theInventory[i];
	}

	public ItemStack decrStackSize(int i, int j) {
		if (this.theInventory[i] != null) {
			if (i == 2) {
				ItemStack itemstack2 = this.theInventory[i];
				this.theInventory[i] = null;
				return itemstack2;
			} else if (this.theInventory[i].stackSize <= j) {
				ItemStack itemstack1 = this.theInventory[i];
				this.theInventory[i] = null;
				if (this.inventoryResetNeededOnSlotChange(i)) {
					this.resetRecipeAndSlots();
				}

				return itemstack1;
			} else {
				ItemStack itemstack = this.theInventory[i].splitStack(j);
				if (this.theInventory[i].stackSize == 0) {
					this.theInventory[i] = null;
				}

				if (this.inventoryResetNeededOnSlotChange(i)) {
					this.resetRecipeAndSlots();
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	private boolean inventoryResetNeededOnSlotChange(int parInt1) {
		return parInt1 == 0 || parInt1 == 1;
	}

	public ItemStack removeStackFromSlot(int i) {
		if (this.theInventory[i] != null) {
			ItemStack itemstack = this.theInventory[i];
			this.theInventory[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.theInventory[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		if (this.inventoryResetNeededOnSlotChange(i)) {
			this.resetRecipeAndSlots();
		}

	}

	public String getName() {
		return "mob.villager";
	}

	public boolean hasCustomName() {
		return false;
	}

	public IChatComponent getDisplayName() {
		return (IChatComponent) (this.hasCustomName() ? new ChatComponentText(this.getName())
				: new ChatComponentTranslation(this.getName(), new Object[0]));
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return this.theMerchant.getCustomer() == entityplayer;
	}

	public void openInventory(EntityPlayer var1) {
	}

	public void closeInventory(EntityPlayer var1) {
	}

	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	public void markDirty() {
		this.resetRecipeAndSlots();
	}

	public void resetRecipeAndSlots() {
		this.currentRecipe = null;
		ItemStack itemstack = this.theInventory[0];
		ItemStack itemstack1 = this.theInventory[1];
		if (itemstack == null) {
			itemstack = itemstack1;
			itemstack1 = null;
		}

		if (itemstack == null) {
			this.setInventorySlotContents(2, (ItemStack) null);
		} else {
			MerchantRecipeList merchantrecipelist = this.theMerchant.getRecipes(this.thePlayer);
			if (merchantrecipelist != null) {
				MerchantRecipe merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack, itemstack1,
						this.currentRecipeIndex);
				if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
					this.currentRecipe = merchantrecipe;
					this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
				} else if (itemstack1 != null) {
					merchantrecipe = merchantrecipelist.canRecipeBeUsed(itemstack1, itemstack, this.currentRecipeIndex);
					if (merchantrecipe != null && !merchantrecipe.isRecipeDisabled()) {
						this.currentRecipe = merchantrecipe;
						this.setInventorySlotContents(2, merchantrecipe.getItemToSell().copy());
					} else {
						this.setInventorySlotContents(2, (ItemStack) null);
					}
				} else {
					this.setInventorySlotContents(2, (ItemStack) null);
				}
			}
		}

		this.theMerchant.verifySellingItem(this.getStackInSlot(2));
	}

	public MerchantRecipe getCurrentRecipe() {
		return this.currentRecipe;
	}

	public void setCurrentRecipeIndex(int currentRecipeIndexIn) {
		this.currentRecipeIndex = currentRecipeIndexIn;
		this.resetRecipeAndSlots();
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
		for (int i = 0; i < this.theInventory.length; ++i) {
			this.theInventory[i] = null;
		}

	}
}