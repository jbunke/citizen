package com.redsquare.citizen.entity.building;

import com.redsquare.citizen.util.Randoms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingLayouts {
  private static final String RESIDENTIAL_PATH =
          "res/text_assets/buildings/residential_layouts.txt";
  private static final char[][][] RESIDENTIAL_LAYOUTS;

  static {
    RESIDENTIAL_LAYOUTS = readLayouts(RESIDENTIAL_PATH);
  }

  public static char[][] randomResidentialLayout() {
    int index = Randoms.bounded(0, RESIDENTIAL_LAYOUTS.length);

    return RESIDENTIAL_LAYOUTS[index];
  }

  private static char[][][] readLayouts(String filepath) {
    List<List<String>> readings = new ArrayList<>();
    readings.add(new ArrayList<>());

    try {
      BufferedReader br = new BufferedReader(new FileReader(filepath));

      while (br.ready()) {
        String line = br.readLine();

        if (line.trim().equals("")) {
          readings.add(new ArrayList<>());
          continue;
        }

        readings.get(readings.size() - 1).add(line);
      }

      br.close();

      char[][][] layouts = new char[readings.size()][][];

      for (int i = 0; i < layouts.length; i++) {
        layouts[i] = readLayout(readings.get(i));
      }

      return layouts;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new char[0][0][0];
  }

  private static char[][] readLayout(List<String> lines) {
    char[][] layout = new char[lines.size()][];

    for (int i = 0; i < layout.length; i++) {
      String[] chars = lines.get(i).split(" ");
      layout[i] = new char[chars.length];

      for (int j = 0; j < layout[i].length; j++) {
        layout[i][j] = chars[j].charAt(0);
      }
    }

    return layout;
  }
}
