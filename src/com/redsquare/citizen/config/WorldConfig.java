package com.redsquare.citizen.config;

public class WorldConfig {
  private static int xDim = 480;
  private static int plateCount = 40;
  private static int simulationYears = 0;

  public static void setXDim(int xDim) {
    WorldConfig.xDim = xDim;
  }

  public static void setPlateCount(int plateCount) {
    WorldConfig.plateCount = plateCount;
  }

  public static int getXDim() {
    return xDim;
  }

  public static int getPlateCount() {
    return plateCount;
  }

  public static int getSimulationYears() {
    return simulationYears;
  }
}
