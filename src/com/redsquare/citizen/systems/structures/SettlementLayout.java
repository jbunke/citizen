package com.redsquare.citizen.systems.structures;

import com.redsquare.citizen.systems.politics.Settlement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class SettlementLayout {
  final int maxDepth;
  final boolean[][] streetMap;
  final Stack<StreetNode> priority = new Stack<>();
  int iterCounter = 0;

  private final StreetNode anchor;

  private SettlementLayout(Settlement settlement) {
    this.maxDepth = setupPowerToStreetMaxDepth(settlement);
    this.streetMap = new boolean[100][100];
    this.anchor = StreetNode.startNode(this);

    anchor.generate();
  }

  public static SettlementLayout generate(Settlement settlement) {
    return new SettlementLayout(settlement);
  }

  private static int setupPowerToStreetMaxDepth(Settlement settlement) {
    int setupPower = settlement.getSetupPower();

    if (setupPower > 400) return setupPower / 50;

    return 7;
  }

  public BufferedImage draw() {
    BufferedImage layout = new BufferedImage(2000, 2000,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) layout.getGraphics();
    g.setColor(new Color(0, 150, 200));
    g.fillRect(0, 0, 2000, 2000);

    anchor.draw(g);

    g.setColor(new Color(0, 200, 0, 100));
    g.fillRect(0, 0, 2000, 2000);

    g.setColor(new Color(200, 0, 0, 200));
    for (int x = 0; x < 100; x++) {
      for (int y = 0; y < 100; y++) {
        int tx = 995 + (30 * (49 - x));
        int ty = 995 + (30 * (49 - y));

        if (streetMap[x][y]) g.fillRect(tx, ty, 10, 10);
      }
    }

    return layout;
  }
}
