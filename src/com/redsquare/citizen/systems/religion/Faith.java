package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.entity.Person;

public class Faith {
  private final Person associated;
  private Religion religion;

  private Faith(Person associated) {
    this.associated = associated;
    // TODO: temp
    this.religion = associated.getCulture().getReligiousProfile().getReligions()[0];
  }
}
