package net.lax1dude.eaglercraft.v1_8.forge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ModernEventBus {

	private static final Logger logger = LogManager.getLogger("ModernEventBus");
	
	private static final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

	public static void init() {
		logger.info("Modern Event Bus initialized.");
	}

	public static <T> void addListener(Class<T> eventType, Consumer<T> listener) {
		listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
	}

	@SuppressWarnings("unchecked")
	public static <T> void post(T event) {
		List<Consumer<?>> eventListeners = listeners.get(event.getClass());
		if (eventListeners != null) {
			for (Consumer<?> listener : eventListeners) {
				((Consumer<T>) listener).accept(event);
			}
		}
	}
}
