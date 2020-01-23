package com.redsquare.citizen.item;

import com.redsquare.citizen.graphics.Font;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StackableItem extends Item {
  final static int STACK_MAXIMUM = 16;

  private int quantity;

  private StackableItem(String itemID, int quantity) {
    this.quantity = quantity;
    this.itemID = itemID;
  }

  public static StackableItem testCreateItem() {
    return new StackableItem("", STACK_MAXIMUM);
  }

  int getQuantity() {
    return quantity;
  }

  void decreaseBy(int amount) {
    this.quantity -= amount;
  }

  void increaseBy(int amount) {
    this.quantity += amount;
  }

  private void quantitySanityCheck() {
    if (quantity > STACK_MAXIMUM)
      quantity = STACK_MAXIMUM;
  }

  @Override
  public BufferedImage getItemIcon() {
    BufferedImage base = super.getItemIcon();
    Graphics2D g = (Graphics2D) base.getGraphics();

    g.drawImage(
            Font.CLEAN.getText(String.valueOf(quantity), 1.8,
                    new Color(255, 255, 255)),
            0, (int)(ICON_DIMENSION * 0.6), null);

    return base;
  }
}
