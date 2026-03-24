package net.lax1dude.eaglercraft.v1_8.forge.registries;

import net.lax1dude.eaglercraft.v1_8.forge.ModernRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ForgeRegistries {

	public static final RegistryHolder<Block> BLOCKS = new RegistryHolder<>(ModernRegistry::registerBlock, ModernRegistry::getBlock);
	public static final RegistryHolder<Item> ITEMS = new RegistryHolder<>(ModernRegistry::registerItem, ModernRegistry::getItem);

	public static class RegistryHolder<T> {
		private final RegisterFunc<T> registerFunc;
		private final GetFunc<T> getFunc;

		public RegistryHolder(RegisterFunc<T> registerFunc, GetFunc<T> getFunc) {
			this.registerFunc = registerFunc;
			this.getFunc = getFunc;
		}

		public void register(String name, T object) {
			registerFunc.register(new ResourceLocation(name), object);
		}

		public void register(ResourceLocation location, T object) {
			registerFunc.register(location, object);
		}

		public T get(ResourceLocation location) {
			return getFunc.get(location);
		}
	}

	@FunctionalInterface
	private interface RegisterFunc<T> {
		void register(ResourceLocation location, T object);
	}

	@FunctionalInterface
	private interface GetFunc<T> {
		T get(ResourceLocation location);
	}
}
