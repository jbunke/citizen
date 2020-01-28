package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.politics.Culture;

public class MonotheisticReligion extends DeisticReligion {
  private final God god;

  MonotheisticReligion(Culture culture) {
    super(culture);

    god = new God(true, null, false, this.culture);
  }
}
