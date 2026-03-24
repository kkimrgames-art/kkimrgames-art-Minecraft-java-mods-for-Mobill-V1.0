package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

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
public class EnchantmentDamage extends Enchantment {
	private static final String[] protectionName = new String[] { "all", "undead", "arthropods" };
	private static final int[] baseEnchantability = new int[] { 1, 5, 5 };
	private static final int[] levelEnchantability = new int[] { 11, 8, 8 };
	private static final int[] thresholdEnchantability = new int[] { 20, 20, 20 };
	public final int damageType;

	public EnchantmentDamage(int enchID, ResourceLocation enchName, int enchWeight, int classification) {
		super(enchID, enchName, enchWeight, EnumEnchantmentType.WEAPON);
		this.damageType = classification;
	}

	public int getMinEnchantability(int i) {
		return baseEnchantability[this.damageType] + (i - 1) * levelEnchantability[this.damageType];
	}

	public int getMaxEnchantability(int i) {
		return this.getMinEnchantability(i) + thresholdEnchantability[this.damageType];
	}

	public int getMaxLevel() {
		return 5;
	}

	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
		return this.damageType == 0 ? (float) level * 1.25F
				: (this.damageType == 1 && creatureType == EnumCreatureAttribute.UNDEAD ? (float) level * 2.5F
						: (this.damageType == 2 && creatureType == EnumCreatureAttribute.ARTHROPOD
								? (float) level * 2.5F
								: 0.0F));
	}

	public String getName() {
		return "enchantment.damage." + protectionName[this.damageType];
	}

	public boolean canApplyTogether(Enchantment ench) {
		return !(ench instanceof EnchantmentDamage);
	}

	public boolean canApply(ItemStack stack) {
		return stack.getItem() instanceof ItemAxe ? true : super.canApply(stack);
	}

	public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
		if (target instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) target;
			if (this.damageType == 2 && entitylivingbase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
				int i = 20 + user.getRNG().nextInt(10 * level);
				entitylivingbase.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, i, 3));
			}
		}

	}
}