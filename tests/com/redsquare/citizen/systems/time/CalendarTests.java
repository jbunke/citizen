package com.redsquare.citizen.systems.time;

import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Meaning;
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

    Language language = Language.generate();

    Calendar c = Calendar.generate(language.getVocabulary(),
            YEAR_LENGTH_IN_DAYS,
            language.lookUpWord(Meaning.DAY_OF_YEAR).toString());

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
