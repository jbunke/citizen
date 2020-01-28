package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.entity.animal.Species;
import com.redsquare.citizen.util.Sets;

import java.util.Set;

public class BasicForm extends GodForm {
  private final Species.Classification animalType;

  BasicForm(God.Attribute attribute) {
    super(attribute);

    Set<Species.Classification> selectionSet = Species.Classification.all();

    this.animalType = Sets.randomEntry(selectionSet);
  }


}
