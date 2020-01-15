package com.redsquare.citizen.item;

import com.redsquare.citizen.graphics.Font;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StackableItem extends Item {
  private final static int STACK_MAXIMUM = 99;

  private int quantity;

  private StackableItem(String itemID, int quantity) {
    this.quantity = quantity;
    this.itemID = itemID;
  }

  public static StackableItem testCreateItem() {
    return new StackableItem("", STACK_MAXIMUM);
  }

  @Override
  public BufferedImage getItemIcon() {
    BufferedImage base = super.getItemIcon();
    Graphics2D g = (Graphics2D) base.getGraphics();

    g.drawImage(
            Font.CLEAN.getText(String.valueOf(quantity), 1.0,
                    new Color(255, 255, 255)),
            40, 40, null);

    return base;
  }
}
