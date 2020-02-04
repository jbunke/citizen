package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.entity.animal.AnimalSpecies;
import com.redsquare.citizen.util.Sets;

import java.util.Set;

public class AnimalForm extends GodForm {
  private final AnimalSpecies.Classification animalType;

  AnimalForm(God.Attribute attribute) {
    super(attribute);

    Set<AnimalSpecies.Classification> selectionSet = AnimalSpecies.Classification.all();

    this.animalType = Sets.randomEntry(selectionSet);
  }

}
