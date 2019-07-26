package com.redsquare.citizen.entity;

import com.redsquare.citizen.graphics.RenderDirection;
import com.redsquare.citizen.graphics.RenderPosture;
import com.redsquare.citizen.systems.time.GameDate;

public abstract class Lifeform extends Entity {
  RenderDirection direction;
  RenderPosture posture;


  /**
   * @return The age of the life form in game-years
   * */
  abstract int age(GameDate now);
}
