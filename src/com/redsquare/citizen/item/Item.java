package com.redsquare.citizen.item;

import java.awt.image.BufferedImage;

public abstract class Item {
  public final static int ICON_DIMENSION = 64;
  String itemID;

  public BufferedImage getItemIcon() {
    return ItemIconMap.lookUp(itemID);
  }
}
