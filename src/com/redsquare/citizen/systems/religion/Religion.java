package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.util.Randoms;

import java.util.ArrayList;
import java.util.List;

public abstract class Religion {
  static final int INSTITUTION_INDEX = 0,
          INDIVIDUAL_INDEX = 1, ENVIRONMENT_INDEX = 2;
  private static final int NUM_PRIORITIES = 3;
  final double[] priorities;
  final Virtues[] virtues;

  Religion(Culture culture) {
    this.priorities = new double[NUM_PRIORITIES];

    /* TODO: Use culture to determine how the culture generating the religion prioritizes
        the environment, the individual, and social harmony in the form of institutions.
        This implementation is temporary */

    do {
      for (int i = 0; i < NUM_PRIORITIES; i++) {
        priorities[i] = Randoms.bounded(-0.5, 1.);
      }
    } while (priorities[INSTITUTION_INDEX] + priorities[INDIVIDUAL_INDEX] +
            priorities[ENVIRONMENT_INDEX] < 1);

    final double MAX_DIFF = 1.5;

    List<Virtues> eligible = new ArrayList<>();

    for (Virtues virtue : Virtues.values()) {
      double[] virtuePriorities = virtue.getPriorities();
      double diff = Math.abs(priorities[INSTITUTION_INDEX] - virtuePriorities[INSTITUTION_INDEX]) +
              Math.abs(priorities[INDIVIDUAL_INDEX] - virtuePriorities[INDIVIDUAL_INDEX]) +
              Math.abs(priorities[ENVIRONMENT_INDEX] - virtuePriorities[ENVIRONMENT_INDEX]);

      if (diff < MAX_DIFF)
        eligible.add(virtue);
    }

    virtues = new Virtues[eligible.size()];

    for (int i = 0; i < eligible.size(); i++)
      virtues[i] = eligible.get(i);
  }
}
