package com.redsquare.citizen.entity;

import com.redsquare.citizen.graphics.Sprite;

import java.awt.*;

public abstract class Entity {

  String ID;

  Sprite[] layers;

  Point worldLocation;
  Point location;
  int mass;
}
