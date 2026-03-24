package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModLogManager {

	private static final int MAX_WORLD_LOGS = 10000;
	private static final Map<String, List<String>> worldLogs = new LinkedHashMap<>();
	private static final Map<String, WorldStatus> worldStatus = new LinkedHashMap<>();
	private static final List<String> consoleLogs = new ArrayList<>();
	private static final int MAX_CONSOLE_LOGS = 5000;
	private static final Object consoleLock = new Object();

	private static class WorldStatus {
		private final String level;
		private final String message;
		private final long timeMs;

		private WorldStatus(String level, String message, long timeMs) {
			this.level = level;
			this.message = message;
			this.timeMs = timeMs;
		}
	}

	public static synchronized void info(String worldName, String modName, String message) {
		append(worldName, "INFO", modName, message);
	}

	public static synchronized void warn(String worldName, String modName, String message) {
		append(worldName, "WARN", modName, message);
	}

	public static synchronized void error(String worldName, String modName, String message) {
		append(worldName, "ERROR", modName, message);
	}

	public static synchronized void warn(String worldName, String modName, String message, Throwable t) {
		append(worldName, "WARN", modName, message);
		appendThrowable(worldName, "WARN", modName, t);
	}

	public static synchronized void error(String worldName, String modName, String message, Throwable t) {
		append(worldName, "ERROR", modName, message);
		appendThrowable(worldName, "ERROR", modName, t);
	}

	public static synchronized void clearWorld(String worldName) {
		if (worldName != null) {
			worldLogs.remove(worldName);
			worldStatus.remove(worldName);
		}
	}
	
	// ═══════════════════════════════════════════════════════════
	// Console Logs Management
	// ═══════════════════════════════════════════════════════════
	
	public static void consoleInfo(String message) {
		addConsoleLog("INFO", message);
	}
	
	public static void consoleWarn(String message) {
		addConsoleLog("WARN", message);
	}
	
	public static void consoleError(String message) {
		addConsoleLog("ERROR", message);
	}
	
	public static void consoleError(String message, Throwable t) {
		addConsoleLog("ERROR", message);
		if (t != null) {
			// Note: StringWriter and PrintWriter are not available in TeaVM
			// We'll just log the exception message and class name
			String exMsg = t.getClass().getName();
			if (t.getMessage() != null && t.getMessage().length() > 0) {
				exMsg += ": " + t.getMessage();
			}
			addConsoleLog("ERROR", "  " + exMsg);
			// Log cause if available
			Throwable cause = t.getCause();
			if (cause != null) {
				String causeMsg = cause.getClass().getName();
				if (cause.getMessage() != null && cause.getMessage().length() > 0) {
					causeMsg += ": " + cause.getMessage();
				}
				addConsoleLog("ERROR", "  Caused by: " + causeMsg);
			}
		}
	}
	
	private static void addConsoleLog(String level, String message) {
		synchronized (consoleLock) {
			long s = System.currentTimeMillis() / 1000L;
			long hh = (s / 3600L) % 24L;
			long mm = (s / 60L) % 60L;
			long ss = s % 60L;
			String ts = two(hh) + ":" + two(mm) + ":" + two(ss);
			String line = "[" + ts + "][CONSOLE][" + level + "] " + message;
			consoleLogs.add(line);
			if (consoleLogs.size() > MAX_CONSOLE_LOGS) {
				consoleLogs.remove(0);
			}
		}
	}
	
	public static List<String> getConsoleLogs() {
		synchronized (consoleLock) {
			return new ArrayList<>(consoleLogs);
		}
	}
	
	public static String getConsoleLogsAsText() {
		synchronized (consoleLock) {
			if (consoleLogs.isEmpty()) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0, l = consoleLogs.size(); i < l; ++i) {
				if (i > 0) {
					sb.append('\n');
				}
				sb.append(consoleLogs.get(i));
			}
			return sb.toString();
		}
	}
	
	public static void clearConsoleLogs() {
		synchronized (consoleLock) {
			consoleLogs.clear();
		}
	}
	
	public static int getConsoleLogCount() {
		synchronized (consoleLock) {
			return consoleLogs.size();
		}
	}

	public static synchronized void setWorldStatus(String worldName, String level, String message) {
		if (worldName == null || worldName.length() == 0) {
			worldName = "global";
		}
		String lvl = (level == null || level.length() == 0) ? "INFO" : level;
		String msg = (message == null) ? "" : message;
		worldStatus.put(worldName, new WorldStatus(lvl, msg, System.currentTimeMillis()));
		append(worldName, lvl, "-", msg);
	}

	public static synchronized String getWorldStatusMessage(String worldName) {
		if (worldName == null || worldName.length() == 0) {
			worldName = "global";
		}
		WorldStatus st = worldStatus.get(worldName);
		return st != null ? st.message : null;
	}

	public static synchronized String getWorldStatusLevel(String worldName) {
		if (worldName == null || worldName.length() == 0) {
			worldName = "global";
		}
		WorldStatus st = worldStatus.get(worldName);
		return st != null ? st.level : null;
	}

	public static synchronized int getWorldStatusAgeSeconds(String worldName) {
		if (worldName == null || worldName.length() == 0) {
			worldName = "global";
		}
		WorldStatus st = worldStatus.get(worldName);
		if (st == null) {
			return -1;
		}
		long ageMs = System.currentTimeMillis() - st.timeMs;
		if (ageMs <= 0L) {
			return 0;
		}
		return (int) (ageMs / 1000L);
	}

	public static synchronized List<String> getWorldLogs(String worldName) {
		List<String> logs = worldLogs.get(worldName);
		if (logs == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(logs);
	}

	public static synchronized String getWorldLogsAsText(String worldName) {
		List<String> logs = worldLogs.get(worldName);
		if (logs == null || logs.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0, l = logs.size(); i < l; ++i) {
			if (i > 0) {
				sb.append('\n');
			}
			sb.append(logs.get(i));
		}
		return sb.toString();
	}

	private static void append(String worldName, String level, String modName, String message) {
		if (worldName == null || worldName.length() == 0) {
			worldName = "global";
		}
		String mod = (modName == null || modName.length() == 0) ? "-" : modName;
		String msg = (message == null) ? "" : message;
		long s = System.currentTimeMillis() / 1000L;
		long hh = (s / 3600L) % 24L;
		long mm = (s / 60L) % 60L;
		long ss = s % 60L;
		String ts = two(hh) + ":" + two(mm) + ":" + two(ss);
		String line = "[" + ts + "][" + level + "][" + mod + "] " + msg;
		List<String> logs = worldLogs.get(worldName);
		if (logs == null) {
			logs = new ArrayList<>();
			worldLogs.put(worldName, logs);
		}
		logs.add(line);
		if (logs.size() > MAX_WORLD_LOGS) {
			logs.remove(0);
		}
	}

	private static void appendThrowable(String worldName, String level, String modName, Throwable t) {
		if (t == null) {
			return;
		}
		int causeDepth = 0;
		Throwable cur = t;
		while (cur != null && causeDepth < 5) {
			String header = cur.getClass().getName();
			if (cur.getMessage() != null && cur.getMessage().length() > 0) {
				header += ": " + cur.getMessage();
			}
			append(worldName, level, modName, header);
			StackTraceElement[] st = cur.getStackTrace();
			int max = st != null ? Math.min(st.length, 25) : 0;
			for (int i = 0; i < max; ++i) {
				append(worldName, level, modName, "  at " + String.valueOf(st[i]));
			}
			cur = cur.getCause();
			if (cur != null) {
				append(worldName, level, modName, "Caused by:");
			}
			++causeDepth;
		}
	}

	private static String two(long v) {
		return v < 10L ? ("0" + v) : String.valueOf(v);
	}
}
