package com.redsquare.citizen.entity;

import com.redsquare.citizen.devkit.sprite_gen.SpriteUniqueColorMapping;
import com.redsquare.citizen.devkit.sprite_gen.Tilemapping;
import com.redsquare.citizen.graphics.*;
import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.systems.politics.Family;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.ColorMath;
import com.redsquare.citizen.util.FloatPoint;
import com.redsquare.citizen.util.Randoms;
import com.redsquare.citizen.util.Sets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.redsquare.citizen.GameManager.WorldMaths;

public class Person extends Animal {
  /* Person constants */

  // Animation
  private final static int SPRITE_WIDTH = 224;
  private final static int SPRITE_HEIGHT = 176;

  private final static int LAYER_AMOUNT = 15;

  private final static int BODY_LAYER = 0;
  private final static int BODY_SCARRING_LAYER = 1;
  private final static int LEGWEAR_LAYER = 2;
  private final static int FOOTWEAR_LAYER = 3;
  private final static int UPPER_BODY_LAYER = 4;
  private final static int BELT_LAYER = 5;
  private final static int GLOVES_LAYER = 6;
  private final static int HEAD_LAYER = 7;
  private final static int FACIAL_SCARRING_LAYER = 8;
  private final static int TRENCH_COAT_LAYER = 9;
  private final static int NECK_LAYER = 10;
  private final static int HAIR_LAYER = 11;
  private final static int MASK_LAYER = 12;
  private final static int HEADWEAR_LAYER = 13;
  private final static int WEAPON_LAYER = 14;


  /* Instance fields */
  protected String[] name;
  protected String called;

  /* Not final as cultures may have Family transitions during institutions
   * like marriage */
  private Family family;
  private final GameDate birthday;
  private final Settlement birthplace;

  private final Person father;
  private final Person mother;
  /* final because assignment to empty HashSet is then populated but is still
   * the same HashSet object */
  private final Set<Person> children;

  private final Color skinColor;
  private final Color hairColor;
  private final Height height;
  private final BodyType bodyType;

  private final Language motherTongue;
  private final Set<Language> languages;
  private Culture culture;

  Point worldLocation;
  Point cellLocation;
  FloatPoint subCellLocation;
  double speed;

  /* Animation */
  private RenderMood mood;
  private boolean talking;
  private boolean blinking;

  private int blinkCounter = 0;
  private int blinkDuration = 5;
  private int betweenBlinks = 50;

  protected Person(Person father, Person mother, GameDate birthday,
                   Settlement birthplace) {
    sex = Math.random() < 0.5 ? Sex.MALE : Sex.FEMALE;

    this.father = father;
    this.mother = mother;
    this.birthday = birthday;
    this.birthplace = birthplace;

    this.worldLocation = birthplace.getLocation();
    this.cellLocation = new Point(WorldMaths.CELLS_IN_WORLD_CELL_DIM / 2,
            WorldMaths.CELLS_IN_WORLD_CELL_DIM / 2);
    this.subCellLocation = new FloatPoint(WorldMaths.CELL_DIMENSION_LENGTH / 2,
            WorldMaths.CELL_DIMENSION_LENGTH);
    this.speed = 6.;

    this.direction = RenderDirection.D;
    this.posture = RenderPosture.CALM;
    this.activity = RenderActivity.IDLE;
    this.poseNum = 0;

    this.mood = RenderMood.NEUTRAL;
    this.talking = false;
    this.blinking = false;

    this.children = new HashSet<>();

    // TODO: name generation

    motherTongue = motherTongueGeneration();
    languages = Set.of(motherTongue);
    culture = cultureGeneration();
    family = familyGeneration();

    skinColor = skinColorGeneration();
    hairColor = hairColorGeneration();
    height = heightGeneration();
    bodyType = randomBodyType();

    this.mother.children.add(this);
    this.father.children.add(this);

    this.layers = new Sprite[LAYER_AMOUNT];
    spriteSetup();
  }

  protected Person(Sex sex, GameDate birthday, Settlement birthplace) {
    this.sex = sex;
    this.birthday = birthday;
    this.birthplace = birthplace;

    this.father = null;
    this.mother = null;

    this.worldLocation = birthplace.getLocation();
    this.cellLocation = new Point(WorldMaths.CELLS_IN_WORLD_CELL_DIM / 2,
            WorldMaths.CELLS_IN_WORLD_CELL_DIM / 2);
    this.subCellLocation = new FloatPoint(WorldMaths.CELL_DIMENSION_LENGTH / 2,
            WorldMaths.CELL_DIMENSION_LENGTH);
    this.speed = 6.;

    this.direction = RenderDirection.D;
    this.posture = RenderPosture.CALM;
    this.activity = RenderActivity.IDLE;
    this.poseNum = 0;

    this.mood = RenderMood.NEUTRAL;
    this.talking = false;
    this.blinking = false;

    this.children = new HashSet<>();

    motherTongue = birthplace.getState().getLanguage();
    languages = Set.of(motherTongue);
    culture = birthplace.getState().getCulture();
    family = Family.generate();

    skinColor = birthplace.getState().getCulture().
            getNativeRace().generateSkinColor();
    hairColor = birthplace.getState().getCulture().
            getNativeRace().generateHairColor();
    height = randomHeight();
    bodyType = randomBodyType();

    this.layers = new Sprite[LAYER_AMOUNT];
    spriteSetup();
  }

  public static Person create(Sex sex, GameDate birthday,
                              Settlement birthplace) {
    return new Person(sex, birthday, birthplace);
  }

  public static Person birth(Person mother, Person father, GameDate birthday,
                             Settlement birthplace) {
    return new Person(father, mother, birthday, birthplace);
  }

  private void spriteSetup() {
    /* BODY */
    layers[BODY_LAYER] = new Sprite(
            "res/img_assets/sprite_sheets/test/test_sprite_sheet.png",
            "BASIC GREY PERSON", SPRITE_WIDTH, SPRITE_HEIGHT, SemanticMaps.HOMINID_BODY);

    spriteHeadSetup();
  }

  private void spriteHeadSetup() {
    /* HEAD */
    final String SKIN_COLOUR_MAPPING =
            "res/img_assets/sprite_gen/heads/head_skin_colour_mapping.png";
    final String HEAD_MAPPING =
            "res/img_assets/sprite_gen/heads/head_mapping.png";

    final String EYEBROW_HAIR_COLOUR_MAPPING =
            "res/img_assets/sprite_gen/heads/eyebrow_thick_hair_colour_mapping.png";
    final String EYEBROW_MAPPING =
            "res/img_assets/sprite_gen/heads/eyebrow_mapping.png";

    try {
      BufferedImage skinColor = ImageIO.read(new File(SKIN_COLOUR_MAPPING));
      BufferedImage headMapping = ImageIO.read(new File(HEAD_MAPPING));
      BufferedImage intermediate =
              SpriteUniqueColorMapping.skinColorApplication(skinColor, this.skinColor, 4);
      BufferedImage img = SpriteUniqueColorMapping.expandTexture(intermediate, headMapping, 4);
      img = Tilemapping.duplicateVertically(img, RenderMood.values().length);

      BufferedImage eyebrows = ImageIO.read(new File(EYEBROW_HAIR_COLOUR_MAPPING));
      BufferedImage eyebrowMapping = ImageIO.read(new File(EYEBROW_MAPPING));
      eyebrows = SpriteUniqueColorMapping.skinColorApplication(eyebrows, this.hairColor, 4);
      eyebrows = SpriteUniqueColorMapping.expandTexture(eyebrows, eyebrowMapping, 4);

      img.getGraphics().drawImage(eyebrows, 0, 0, null);

      layers[HEAD_LAYER] = new Sprite(img, "HEAD LAYER", 80, 100, SemanticMaps.HOMINID_FACE);
    } catch (IOException e) {
      e.printStackTrace();
    }

    /* EYEBROWS */
  }

  public Settlement getBirthplace() {
    return birthplace;
  }

  Sex getSex() {
    return sex;
  }

  Height getHeight() {
    return height;
  }

  BodyType getBodyType() {
    return bodyType;
  }

  Color getSkinColor() {
    return skinColor;
  }

  Color getHairColor() {
    return hairColor;
  }

  public Set<Person> getChildren() {
    return children;
  }

  private Family familyGeneration() {
    if (culture.getInheritance() != Culture.Inheritance.MATRILINEAL)
      return father.family;

    return mother.family;
  }

  private Culture cultureGeneration() {
    if (father.culture.getInheritance() == Culture.Inheritance.PATRILINEAL ||
            mother.culture.getInheritance() == Culture.Inheritance.PATRILINEAL)
      return father.culture;
    else if (mother.culture.getInheritance() == Culture.Inheritance.MATRILINEAL)
      return mother.culture;

    /* sharedChildren is the set of children that this person's parents share
     * excluding them; their full siblings */
    Set<Person> sharedChildren =
            Sets.difference(Sets.intersection(father.children, mother.children), Set.of(this));

    /* Consistency among siblings: if parents share children already,
     * apply same culture as older siblings final case is non-deterministic */
    if (sharedChildren.size() > 0)
      return new ArrayList<>(sharedChildren).get(0).culture;

    return Math.random() < 0.5 ? mother.culture : father.culture;
  }

  private Language motherTongueGeneration() {
    Language regional = birthplace.getState().getLanguage();

    if (father.speaks(regional) || mother.speaks(regional)) return regional;

    return mother.motherTongue;
  }

  private BodyType randomBodyType() {
    double prob = Math.random();

    if (prob < 0.4) return BodyType.AVERAGE;
    else if (prob < 0.7) return BodyType.SLIM;
    else if (prob < 0.9) return BodyType.MUSCULAR;
    return BodyType.FAT;
  }

  private Height randomHeight() {
    double prob = Math.random();

    if (prob < 0.5) return Height.MEDIUM;
    else if (prob < 0.8) return Height.TALL;
    else return Height.SHORT;
  }

  private Height heightGeneration() {
    Height[] possibilities = new Height[0];
    double[] probabilities = new double[0];

    switch (father.height) {
      case TALL:
        switch (mother.height) {
          case TALL:
          case MEDIUM:
            return Height.TALL;
          case SHORT:
            possibilities = new Height[] { Height.MEDIUM, Height.TALL };
            probabilities = new double[] { 2/3.0, 1.0 };
            break;
        }
        break;
      case MEDIUM:
        switch (mother.height) {
          case TALL:
            return Height.TALL;
          case MEDIUM:
            possibilities = new Height[]
                    { Height.MEDIUM, Height.TALL, Height.SHORT };
            probabilities = new double[] { 1/2.0, 4/5.0, 1.0 };
            break;
          case SHORT:
            possibilities = new Height[]
                    { Height.MEDIUM, Height.SHORT, Height.TALL };
            probabilities = new double[] { 1/2.0, 4/5.0, 1.0 };
            break;
        }
        break;
      case SHORT:
        switch (mother.height) {
          case TALL:
            possibilities = new Height[] { Height.MEDIUM, Height.TALL };
            probabilities = new double[] { 2/3.0, 1.0 };
            break;
          case MEDIUM:
            possibilities = new Height[]
                    { Height.MEDIUM, Height.SHORT, Height.TALL };
            probabilities = new double[] { 1/2.0, 4/5.0, 1.0 };
            break;
          case SHORT:
            possibilities = new Height[] { Height.SHORT, Height.MEDIUM };
            probabilities = new double[] { 2/3.0, 1.0 };
            break;
        }
        break;
    }

    double prob = Math.random();

    for (int i = 0; i < possibilities.length; i++) {
      if (prob < probabilities[i]) return possibilities[i];
    }

    return Height.MEDIUM;
  }

  private Color skinColorGeneration() {
    double skinSkew = Randoms.bounded(0.3, 0.7);
    Color darker = mother.skinColor;
    Color lighter = father.skinColor;

    if (ColorMath.lightness(mother.skinColor) >
            ColorMath.lightness(father.skinColor)) {
      darker = father.skinColor;
      lighter = mother.skinColor;
    }

    Color diff = new Color(lighter.getRed() - darker.getRed(),
            lighter.getGreen() - darker.getGreen(),
            lighter.getBlue() - darker.getBlue());
    return new Color(
            (int)(darker.getRed() + (diff.getRed() * skinSkew)),
            (int)(darker.getGreen() + (diff.getGreen() * skinSkew)),
            (int)(darker.getBlue() + (diff.getBlue() * skinSkew)));
  }

  private Color hairColorGeneration() {
    Color darker = father.hairColor;
    Color lighter = mother.hairColor;

    if (ColorMath.lightness(father.hairColor) >
            ColorMath.lightness(mother.hairColor)) {
      lighter = father.hairColor;
      darker = mother.hairColor;
    }

    double hairSkew = Math.random() * Math.random(); // average value is .25; so skew favours darker hair

    Color diff = new Color(lighter.getRed() - darker.getRed(),
            lighter.getGreen() - darker.getGreen(),
            lighter.getBlue() - darker.getBlue());
    return new Color(
            (int)(darker.getRed() + (diff.getRed() * hairSkew)),
            (int)(darker.getGreen() + (diff.getGreen() * hairSkew)),
            (int)(darker.getBlue() + (diff.getBlue() * hairSkew)));
  }

  protected enum Height {
    TALL, MEDIUM, SHORT
  }

  protected enum BodyType {
    AVERAGE, SLIM, MUSCULAR, FAT
  }

  private boolean speaks(Language language) {
    return languages.contains(language);
  }

  private boolean ancestorOf(Person p) {
    return p.descendantOf(this);
  }

  private boolean descendantOf(Person p) {
    // Can't be a descendant of someone you were born before
    if (birthday.equals(GameDate.priorEvent(birthday, p.birthday))) return false;

    if (father == null) return false;
    else if (father.equals(p) || mother.equals(p)) return true;

    return father.descendantOf(p) || mother.descendantOf(p);
  }

  private String getFaceSpriteCode() {
    return direction.name() + "-" + mood.name() + "-" +
            (talking ? "TALK" : "NOT_TALK") + "-" +
            (blinking ? "BLINK" : "NOT_BLINK");
  }

  private void blinkingUpdate() {
    final int[] INTERVAL_RANGE = new int[] { 60, 140 };
    final int[] DURATION_RANGE = new int[] { 10, 16 };

    blinkCounter++;

    if (blinking) {
      if (blinkCounter >= blinkDuration) {
        blinking = false;
        betweenBlinks = Randoms.bounded(INTERVAL_RANGE[0], INTERVAL_RANGE[1]);
        blinkCounter = 0;
      }
    } else {
      if (blinkCounter >= betweenBlinks) {
        blinking = true;
        blinkDuration = Randoms.bounded(DURATION_RANGE[0], DURATION_RANGE[1]);
        blinkCounter = 0;
      }
    }
  }

  @Override
  int age(GameDate now) {
    return GameDate.yearsBetween(birthday, now);
  }

  @Override
  public Point worldLocation() {
    return worldLocation;
  }

  @Override
  public Point cellLocation() {
    return cellLocation;
  }

  @Override
  public FloatPoint subCellLocation() {
    return subCellLocation;
  }

  @Override
  public BufferedImage getSprite() {
    BufferedImage sprite = new BufferedImage(SPRITE_WIDTH, SPRITE_HEIGHT,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) sprite.getGraphics();
    String spriteCode = getSpriteCode();
    String faceSpriteCode = getFaceSpriteCode();

    for (int i = 0; i < LAYER_AMOUNT; i++) {
      switch (i) {
        case BODY_LAYER:
        case BODY_SCARRING_LAYER:
        case LEGWEAR_LAYER:
        case FOOTWEAR_LAYER:
        case UPPER_BODY_LAYER:
        case BELT_LAYER:
        case GLOVES_LAYER:
        case TRENCH_COAT_LAYER:
        case NECK_LAYER:
        case WEAPON_LAYER:
          // TODO: temp null checker
          if (i != BODY_LAYER) break;
          g.drawImage(layers[i].getSprite(spriteCode), 0, 0, null);
          break;
        default:
          if (i != HEAD_LAYER) break;
          Point offset = SemanticMaps.faceOffset(faceSpriteCode);
          g.drawImage(layers[i].getSprite(faceSpriteCode),
                  offset.x, offset.y, null);
          break;
      }
    }

    return sprite;
  }

  @Override
  public Point getSpriteOffset() {
    return new Point(-112, -164);
  }

  @Override
  public void renderUpdate() {
    blinkingUpdate();
  }
}
