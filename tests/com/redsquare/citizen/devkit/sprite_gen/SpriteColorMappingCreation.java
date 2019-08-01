package com.redsquare.citizen.devkit.sprite_gen;

import com.redsquare.citizen.graphics.RenderMood;
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
    final Color hairColor = new Color(219, 170, 81);

    final String SKIN_MAPPING = "res/img_assets/sprite_gen/heads/head_skin_colour_mapping_round.png";
    final String EYEBROW_MAPPING = "res/img_assets/sprite_gen/heads/eyebrow_mapping.png";
    final String EYEBROW_COLOUR_MAPPING = "res/img_assets/sprite_gen/heads/eyebrow_thick_hair_colour_mapping.png";
    final String MAPPING = "res/img_assets/sprite_gen/heads/head_mapping_round.png";
    final String PATH = "res/devkit_output/sprite_gen/example_head_texture.png";
    final String PNG = "png";

    try {
      BufferedImage texture = ImageIO.read(new File(SKIN_MAPPING));
      BufferedImage mapping = ImageIO.read(new File(MAPPING));
      texture = SpriteUniqueColorMapping.skinColorApplication(texture, skinColor, 4);
      BufferedImage img = SpriteUniqueColorMapping.expandTexture(texture, mapping, 4);
      img = Tilemapping.duplicateVertically(img, RenderMood.values().length);

      BufferedImage eyebrows = ImageIO.read(new File(EYEBROW_COLOUR_MAPPING));
      BufferedImage mapping2 = ImageIO.read(new File(EYEBROW_MAPPING));
      eyebrows = SpriteUniqueColorMapping.skinColorApplication(eyebrows, hairColor, 4);
      eyebrows = SpriteUniqueColorMapping.expandTexture(eyebrows, mapping2, 4);

      img.getGraphics().drawImage(eyebrows, 0, 0, null);

      ImageIO.write(img, PNG, new File(PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
