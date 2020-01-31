package com.redsquare.citizen.entity;

import com.redsquare.citizen.systems.time.GameDate;

abstract class LivingEntity extends Entity {
  /**
   * @return The age of the life form in game-years
   * */
  public abstract int age(GameDate now);

  public abstract boolean isAlive();
}
