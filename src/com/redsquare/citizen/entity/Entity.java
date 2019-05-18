package com.redsquare.citizen.entity;

import com.redsquare.citizen.graphics.Sprite;

import java.awt.*;

public abstract class Entity {

  String ID;

  Sprite[] layers;

  int mass;

  /**
   * @return The (x, y) coordinates of the cell in the WorldCell[][] that
   * the entity is located
   * */
  abstract Point worldLocation();

  /**
   * @return The (x, y) coordinates (potentially rounded) of the location in
   * the cell that the entity is located
   * */
  abstract Point cellLocation();
}
