package com.redsquare.citizen.devkit.visualisation;

import java.awt.*;

public class Visualisation {

  private static boolean visualisation = false;

  public static void activateVisualisation() {
    visualisation = true;
  }

  public static void set() {
    if (visualisation) {
      VisualisationPanel.getInstance().set();
    }
  }

  public static void updatePlates(final int x, final int y, final int index,
                                  final int t) {
    if (visualisation) {
      Color c = indexToColorMapping(index);
      VisualisationPanel.getInstance().updateCell(x, y, c, t);
//      try {
//        Thread.sleep(1);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
    }
  }

  public static void updateCell(final int x, final int y, final Color c,
                                final int t) {
    if (visualisation) {
      VisualisationPanel.getInstance().updateCell(x, y, c, t);
//      try {
//        Thread.sleep(1);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
    }
  }

  private static Color indexToColorMapping(final int index) {
    return new Color(15 + ((index % 9) * 30),
            15 + (((index / 9) * 30) % 240), 0);

//    return new Color(
//            (((index % 5) * 50) + ((index % 6) * 50) + 30) % 200,
//            (((index % 7) * 50) + ((index % 4) * 50) + 70) % 200,
//            (((index % 3) * 50) + ((index % 8) * 50) + 50) % 200
//    );
  }
}
