package com.redsquare.citizen.devkit.sprite_gen;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteColorMappingCreation {

  @Test
  public void headFrontCreation() {
    final String SOURCE = "res/img_assets/sprite_gen/heads/all_heads_grey.png";
    final String PATH = "res/devkit_output/sprite_gen/all_heads_unique.png";
    final String PNG = "png";

    try {
      BufferedImage ref = ImageIO.read(new File(SOURCE));
      BufferedImage img = SpriteUniqueColorMapping.mapToColors(ref, 1);
      ImageIO.write(img, PNG, new File(PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void expansionExample() {
    final String SOURCE = "res/img_assets/sprite_gen/heads/head_example_texture.png";
    final String MAPPING = "res/img_assets/sprite_gen/heads/all_heads_mapping.png";
    final String PATH = "res/devkit_output/sprite_gen/expanded_head_example.png";
    final String PNG = "png";

    try {
      BufferedImage texture = ImageIO.read(new File(SOURCE));
      BufferedImage mapping = ImageIO.read(new File(MAPPING));
      BufferedImage img = SpriteUniqueColorMapping.expandTexture(texture, mapping, 1);
      ImageIO.write(img, PNG, new File(PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void generateHeadTexture() {
    final Color skinColor = new Color(167, 113, 68);

    final String SOURCE = "res/img_assets/sprite_gen/heads/head_skin_colour_mapping.png";
    final String MAPPING = "res/img_assets/sprite_gen/heads/head_mapping.png";
    final String PATH = "res/devkit_output/sprite_gen/example_head_texture.png";
    final String PNG = "png";

    try {
      BufferedImage texture = ImageIO.read(new File(SOURCE));
      BufferedImage mapping = ImageIO.read(new File(MAPPING));
      texture = SpriteUniqueColorMapping.skinColorApplication(texture, skinColor, 4);
      BufferedImage img = SpriteUniqueColorMapping.expandTexture(texture, mapping, 4);
      ImageIO.write(img, PNG, new File(PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
