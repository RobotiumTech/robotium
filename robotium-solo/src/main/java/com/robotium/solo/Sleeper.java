package com.robotium.solo;

class Sleeper {

	private int pauseDuration;
	private int miniPauseDuration;

	private Sleeper() {

	}

	/**
	 * Constructs this object.
	 *
	 * @param pauseDuration pause duration used in {@code sleep}
	 * @param miniPauseDuration pause duration used in {@code sleepMini}
	 */

	public Sleeper(int pauseDuration, int miniPauseDuration) {
		this.pauseDuration = pauseDuration;
		this.miniPauseDuration = miniPauseDuration;
	}

	/**
	 * Sleeps the current thread for the pause length.
	 */

	public void sleep() {
        sleep(pauseDuration);
	}


	/**
	 * Sleeps the current thread for the mini pause length.
	 */

	public void sleepMini() {
        sleep(miniPauseDuration);
	}


	/**
	 * Sleeps the current thread for <code>time</code> milliseconds.
	 *
	 * @param time the length of the sleep in milliseconds
	 */

	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ignored) {}
	}

}
