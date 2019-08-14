package com.redsquare.citizen.graphics;

public enum RenderActivity {
  IDLE(2, 25, 35, 0),
  WALKING(4, 6, 6, 0),
  RUNNING(4, 6, 6, 0);

  public final int frameCount;
  public final int[] frameDurationRange;

  /** If an activity has a higher priority than one trying to replace it,
   * the activity will finish its cycle before delegating to the replacement.
   * For example, roll has a higher priority than idle, because you cannot
   * transition into an idle animation from halfway through a roll */
  public final int priority;

  RenderActivity(int frameCount, int low, int high, int priority) {
    this.frameCount = frameCount;
    this.frameDurationRange = new int[] { low, high };
    this.priority = priority;
  }
}
