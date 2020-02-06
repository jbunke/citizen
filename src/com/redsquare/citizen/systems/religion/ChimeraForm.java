package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.entity.biodiversity.AnimalSpecies;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.util.Set;

public class ChimeraForm extends GodForm {
  private final AnimalSpecies.Classification[] animalComponents;

  ChimeraForm(God.Attribute attribute) {
    super(attribute);

    Set<AnimalSpecies.Classification> selectionSet = AnimalSpecies.Classification.all();
    animalComponents = new AnimalSpecies.Classification[Randoms.prob(0.7) ? 2 : 3];

    for (int i = 0; i < animalComponents.length; i++) {
      AnimalSpecies.Classification selection = Sets.randomEntry(selectionSet);
      selectionSet.remove(selection);

      animalComponents[i] = selection;
    }
  }
}
