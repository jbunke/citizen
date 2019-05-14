package com.redsquare.citizen.systems.time;

import com.redsquare.citizen.systems.language.PhoneticVocabulary;
import com.redsquare.citizen.systems.language.PlaceNameGenerator;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CalendarTests {

  @Test
  public void listDatesForTenYears() {
    final int YEAR_LENGTH_IN_DAYS = 10;
    final int YEARS = 10;

    String filepath = "res/test_output/calendar/dates_for_10_years.txt";

    PhoneticVocabulary v = PhoneticVocabulary.generate();

    // TODO: Generate with WordGenerator based on PhoneticVocabulary and morphemes that carry meaning of "day"
    String dayName = PlaceNameGenerator.generateRandomName(1, 1, v).toLowerCase();

    Calendar c = Calendar.generate(v, YEAR_LENGTH_IN_DAYS, dayName);

    try {
      FileWriter fw = new FileWriter(new File(filepath));
      BufferedWriter bw = new BufferedWriter(fw);

      GameDate g = new GameDate(1, 1);
      for (int y = 0; y < YEARS; y++) {
        for (int d = 0; d < YEAR_LENGTH_IN_DAYS; d++) {
          bw.write(g.written(c));
          bw.newLine();
          g = GameDate.increment(g, YEAR_LENGTH_IN_DAYS);
        }
      }

      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
