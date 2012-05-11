package com.jayway.android.robotium.solo;

class Sleeper {
  private final int DEFAULT_PAUSE = 500;
  private final int DEFAULT_MINIPAUSE = 300;
  private int pause = -1;
  private int miniPause = -1;

  public Sleeper() {
    super();
  }

  /**
   * configurable pause & miniPause 
   * @param pause in milliseconds
   * @param miniPause in milliseconds
   */
  
  public Sleeper(final int pause, final int miniPause) {
    super();
    this.pause = pause;
    this.miniPause = miniPause;
  }

  /**
   * Sleeps the current thread for a default pause length.
   *
   */

  public void sleep() {
    sleep((pause != -1) ? pause : DEFAULT_PAUSE);
  }


  /**
   * Sleeps the current thread for a default mini pause length.
   *
   */

  public void sleepMini() {
    sleep((miniPause != -1) ? miniPause : DEFAULT_MINIPAUSE);
  }


  /**
   * Sleeps the current thread for <code>time</code> milliseconds.
   *
   * @param time the length of the sleep in milliseconds
   *
   */

  public void sleep(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException ignored) {
    }
  }

}
