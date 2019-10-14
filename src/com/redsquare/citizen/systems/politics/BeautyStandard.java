package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.entity.Person;

public class BeautyStandard {
  private final BeautyStandardComponent[] components;

  BeautyStandard(Culture culture) {
    Race nativeRace = culture.getNativeRace();

    components = new BeautyStandardComponent[3];
    components[0] = new SkinColourBeautyStandardComponent(nativeRace);
    components[1] = new HeightBeautyStandardComponent(BeautyStandardComponent.TargetSex.F);
    components[2] = new HeightBeautyStandardComponent(BeautyStandardComponent.TargetSex.M);
    // TODO
  }

  static BeautyStandard generate(Culture culture) {
    return new BeautyStandard(culture);
  }

  public double compliance(Person person) {
    int eligible = 0;
    double sum = 0.0;

    for (BeautyStandardComponent component : components) {
      if (component.applicable(person)) {
        eligible++;
        sum += component.compliance(person);
      }
    }

    return sum / (double) eligible;
  }
}
