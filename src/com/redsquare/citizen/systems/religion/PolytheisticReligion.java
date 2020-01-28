package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.util.Randoms;

import java.util.HashSet;
import java.util.Set;

public class PolytheisticReligion extends DeisticReligion {
  private final boolean IS_ATTRIBUTIVE;
  private final Set<God> pantheon;

  PolytheisticReligion(Culture culture) {
    super(culture);

    IS_ATTRIBUTIVE = Randoms.random();
    pantheon = new HashSet<>();

    // TODO - temporary
    final int GOD_AMOUNT = Randoms.bounded(3, 15);

    for (int i = 0; i < GOD_AMOUNT; i++) {
      pantheon.add(
              new God(false, pantheon,
                      IS_ATTRIBUTIVE, this.culture));
    }
  }
}
