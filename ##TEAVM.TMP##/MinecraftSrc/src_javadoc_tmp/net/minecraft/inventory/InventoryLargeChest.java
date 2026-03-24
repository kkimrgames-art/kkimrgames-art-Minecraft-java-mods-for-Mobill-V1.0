package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

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
public class InventoryLargeChest implements ILockableContainer {
	private String name;
	private ILockableContainer upperChest;
	private ILockableContainer lowerChest;

	public InventoryLargeChest(String nameIn, ILockableContainer upperChestIn, ILockableContainer lowerChestIn) {
		this.name = nameIn;
		if (upperChestIn == null) {
			upperChestIn = lowerChestIn;
		}

		if (lowerChestIn == null) {
			lowerChestIn = upperChestIn;
		}

		this.upperChest = upperChestIn;
		this.lowerChest = lowerChestIn;
		if (upperChestIn.isLocked()) {
			lowerChestIn.setLockCode(upperChestIn.getLockCode());
		} else if (lowerChestIn.isLocked()) {
			upperChestIn.setLockCode(lowerChestIn.getLockCode());
		}

	}

	public int getSizeInventory() {
		return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
	}

	public boolean isPartOfLargeChest(IInventory inventoryIn) {
		return this.upperChest == inventoryIn || this.lowerChest == inventoryIn;
	}

	public String getName() {
		return this.upperChest.hasCustomName() ? this.upperChest.getName()
				: (this.lowerChest.hasCustomName() ? this.lowerChest.getName() : this.name);
	}

	public boolean hasCustomName() {
		return this.upperChest.hasCustomName() || this.lowerChest.hasCustomName();
	}

	public IChatComponent getDisplayName() {
		return (IChatComponent) (this.hasCustomName() ? new ChatComponentText(this.getName())
				: new ChatComponentTranslation(this.getName(), new Object[0]));
	}

	public ItemStack getStackInSlot(int i) {
		return i >= this.upperChest.getSizeInventory()
				? this.lowerChest.getStackInSlot(i - this.upperChest.getSizeInventory())
				: this.upperChest.getStackInSlot(i);
	}

	public ItemStack decrStackSize(int i, int j) {
		return i >= this.upperChest.getSizeInventory()
				? this.lowerChest.decrStackSize(i - this.upperChest.getSizeInventory(), j)
				: this.upperChest.decrStackSize(i, j);
	}

	public ItemStack removeStackFromSlot(int i) {
		return i >= this.upperChest.getSizeInventory()
				? this.lowerChest.removeStackFromSlot(i - this.upperChest.getSizeInventory())
				: this.upperChest.removeStackFromSlot(i);
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (i >= this.upperChest.getSizeInventory()) {
			this.lowerChest.setInventorySlotContents(i - this.upperChest.getSizeInventory(), itemstack);
		} else {
			this.upperChest.setInventorySlotContents(i, itemstack);
		}

	}

	public int getInventoryStackLimit() {
		return this.upperChest.getInventoryStackLimit();
	}

	public void markDirty() {
		this.upperChest.markDirty();
		this.lowerChest.markDirty();
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return this.upperChest.isUseableByPlayer(entityplayer) && this.lowerChest.isUseableByPlayer(entityplayer);
	}

	public void openInventory(EntityPlayer entityplayer) {
		this.upperChest.openInventory(entityplayer);
		this.lowerChest.openInventory(entityplayer);
	}

	public void closeInventory(EntityPlayer entityplayer) {
		this.upperChest.closeInventory(entityplayer);
		this.lowerChest.closeInventory(entityplayer);
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

	public boolean isLocked() {
		return this.upperChest.isLocked() || this.lowerChest.isLocked();
	}

	public void setLockCode(LockCode lockcode) {
		this.upperChest.setLockCode(lockcode);
		this.lowerChest.setLockCode(lockcode);
	}

	public LockCode getLockCode() {
		return this.upperChest.getLockCode();
	}

	public String getGuiID() {
		return this.upperChest.getGuiID();
	}

	public Container createContainer(InventoryPlayer inventoryplayer, EntityPlayer entityplayer) {
		return new ContainerChest(inventoryplayer, this, entityplayer);
	}

	public void clear() {
		this.upperChest.clear();
		this.lowerChest.clear();
	}
}