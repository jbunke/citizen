package com.redsquare.citizen.graphics;

public enum RenderActivity {
  IDLE, WALKING, RUNNING;

  public static int frameCount(RenderActivity activity) {
    switch (activity) {
      case IDLE:
        return 2;
      case RUNNING:
      case WALKING:
        return 4;
      default:
        return 1;
    }
  }
}
