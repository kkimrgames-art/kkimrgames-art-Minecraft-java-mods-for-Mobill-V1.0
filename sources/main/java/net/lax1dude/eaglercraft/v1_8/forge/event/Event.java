package net.lax1dude.eaglercraft.v1_8.forge.event;

public class Event {
	private boolean canceled = false;

	public boolean isCancelable() {
		return false;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		if (isCancelable()) {
			this.canceled = canceled;
		}
	}
}
