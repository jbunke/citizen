package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.entity.animal.Species;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import java.util.Set;

public class ChimeraForm extends GodForm {
  private final Species.Classification[] animalComponents;

  ChimeraForm(God.Attribute attribute) {
    super(attribute);

    Set<Species.Classification> selectionSet = Species.Classification.all();
    animalComponents = new Species.Classification[Randoms.prob(0.7) ? 2 : 3];

    for (int i = 0; i < animalComponents.length; i++) {
      Species.Classification selection = Sets.randomEntry(selectionSet);
      selectionSet.remove(selection);

      animalComponents[i] = selection;
    }
  }
}
