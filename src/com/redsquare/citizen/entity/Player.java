package com.redsquare.citizen.entity;

import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.politics.State;
import com.redsquare.citizen.systems.time.GameDate;

import java.awt.*;

public final class Player extends Person {
  private Player(Sex sex, GameDate birthday, Settlement birthplace) {
    super(sex, birthday, birthplace);
  }

  public static Player temp() {
    State state = new State();
    Settlement settlement = new Settlement(new Point(0, 0), state);
    return new Player(Sex.MALE, new GameDate(1, 1), settlement);
  }
}
