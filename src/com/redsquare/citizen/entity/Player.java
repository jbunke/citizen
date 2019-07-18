package com.redsquare.citizen.entity;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.worldgen.World;

public final class Player extends Person {
  private Player(Sex sex, GameDate birthday, Settlement birthplace) {
    super(sex, birthday, birthplace);
  }

  public static Player temp(World world) {
    Settlement settlement = world.randomSettlement();
    return new Player(Sex.MALE, new GameDate(1, 1), settlement);
  }

  public void input(InputHandler inputHandler) {

  }
}
