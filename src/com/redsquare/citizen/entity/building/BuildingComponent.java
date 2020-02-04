package com.redsquare.citizen.entity.building;

import com.redsquare.citizen.entity.Building;
import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.worldgen.WorldPosition;

public abstract class BuildingComponent extends Entity {
  private final Building building;

  BuildingComponent(final Building building) {
    this.building = building;
  }

  public void setPosition(WorldPosition position) {
    this.position = position;
  }
}
