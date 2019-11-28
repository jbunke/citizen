package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.entity.Person;

public class HeightBeautyStandardComponent extends BeautyStandardComponent {
  private final TargetSex targetSex;
  private final Person.Height ideal;

  HeightBeautyStandardComponent(TargetSex targetSex) {
    this.targetSex = targetSex;

    switch (targetSex) {
      case BOTH:
      case F:
        ideal = Math.random() < 1/3. ? Person.Height.TALL :
                (Math.random() < 0.5 ? Person.Height.MEDIUM : Person.Height.SHORT);
        break;
      default:
        ideal = Math.random() < 0.5 ? Person.Height.TALL :
                (Math.random() < 0.7 ? Person.Height.MEDIUM : Person.Height.SHORT);
        break;
    }
  }

  @Override
  TargetSex targetSex() {
    return targetSex;
  }

  @Override
  public double compliance(Person person) {
    if (ideal == person.getHeight())
      return 1.0;
    else if (ideal == Person.Height.MEDIUM || person.getHeight() == Person.Height.MEDIUM)
      return 0.5;
    else
      return 0.0;
  }
}
