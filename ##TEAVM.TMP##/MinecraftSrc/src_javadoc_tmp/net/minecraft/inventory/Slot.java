package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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
public class Slot {
	private final int slotIndex;
	public final IInventory inventory;
	public int slotNumber;
	public int xDisplayPosition;
	public int yDisplayPosition;

	public Slot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		this.inventory = inventoryIn;
		this.slotIndex = index;
		this.xDisplayPosition = xPosition;
		this.yDisplayPosition = yPosition;
	}

	public void onSlotChange(ItemStack parItemStack, ItemStack parItemStack2) {
		if (parItemStack != null && parItemStack2 != null) {
			if (parItemStack.getItem() == parItemStack2.getItem()) {
				int i = parItemStack2.stackSize - parItemStack.stackSize;
				if (i > 0) {
					this.onCrafting(parItemStack, i);
				}

			}
		}
	}

	protected void onCrafting(ItemStack var1, int var2) {
	}

	protected void onCrafting(ItemStack var1) {
	}

	public void onPickupFromSlot(EntityPlayer var1, ItemStack var2) {
		this.onSlotChanged();
	}

	public boolean isItemValid(ItemStack var1) {
		return true;
	}

	public ItemStack getStack() {
		return this.inventory.getStackInSlot(this.slotIndex);
	}

	public boolean getHasStack() {
		return this.getStack() != null;
	}

	public void putStack(ItemStack itemstack) {
		this.inventory.setInventorySlotContents(this.slotIndex, itemstack);
		this.onSlotChanged();
	}

	public void onSlotChanged() {
		this.inventory.markDirty();
	}

	public int getSlotStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}

	public int getItemStackLimit(ItemStack var1) {
		return this.getSlotStackLimit();
	}

	public String getSlotTexture() {
		return null;
	}

	public ItemStack decrStackSize(int i) {
		return this.inventory.decrStackSize(this.slotIndex, i);
	}

	public boolean isHere(IInventory iinventory, int i) {
		return iinventory == this.inventory && i == this.slotIndex;
	}

	public boolean canTakeStack(EntityPlayer var1) {
		return true;
	}

	public boolean canBeHovered() {
		return true;
	}
}