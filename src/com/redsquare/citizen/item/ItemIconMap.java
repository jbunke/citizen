package com.redsquare.citizen.item;

import com.redsquare.citizen.debug.GameDebug;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItemIconMap {
  private static final Map<String, BufferedImage> itemIconMap = new HashMap<>();

  static {
    try {
      itemIconMap.put("", ImageIO.read(new File("res/items/item_notavailable.png")));
      // TODO ...
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static BufferedImage lookUp(String itemID) {
    if (itemIconMap.containsKey(itemID))
      return itemIconMap.get(itemID);
    else {
      GameDebug.printMessage("Item ID \"" + itemID + "\" was not found in the map", GameDebug::printError);
      return null;
    }
  }
}
