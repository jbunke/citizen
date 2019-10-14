package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.entity.Sex;

public abstract class BeautyStandardComponent {
  public enum TargetSex {
    M, F, BOTH
  }

  abstract TargetSex targetSex();

  public boolean applicable(Person person) {
    switch (targetSex()) {
      case F:
        return person.getSex() == Sex.FEMALE;
      case M:
        return person.getSex() == Sex.MALE;
      case BOTH:
      default:
        return true;
    }
  }

  /** @return 0 to 1 based on how the person complies with the component
   * of the beauty standard */
  public abstract double compliance(Person person);
}
