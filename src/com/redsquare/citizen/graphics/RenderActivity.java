package com.redsquare.citizen.graphics;

public enum RenderActivity {
  IDLE(2), WALKING(4), RUNNING(4);

  public final int frameCount;

  RenderActivity(int frameCount) {
    this.frameCount = frameCount;
  }
}
