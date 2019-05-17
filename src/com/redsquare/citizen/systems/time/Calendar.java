package com.redsquare.citizen.systems.time;

import com.redsquare.citizen.systems.language.Phonology;
import com.redsquare.citizen.systems.language.Word;

import java.util.HashMap;
import java.util.Map;

public class Calendar {
  private final Map<Integer, String> yearDayMap;

  private Calendar(Phonology v,
                   int yearLength, String dayWord) {
    yearDayMap = new HashMap<>();

    for (int i = 1; i <= yearLength; i++) {
      // ensures that there are no repeat day-names
      String potential = "";
      boolean violates = true;
      while (violates) {
        violates = false;
        potential = Word.generateRandomWord(1, 3, v) + dayWord;

        if (yearDayMap.values().contains(potential)) violates = true;
      }

      yearDayMap.put(i, potential);
    }
  }

  public static Calendar generate(Phonology v,
                                  int yearLength, String dayWord) {
    return new Calendar(v, yearLength, dayWord);
  }

  public String yearDay(int day) {
    return yearDayMap.get(day);
  }
}
