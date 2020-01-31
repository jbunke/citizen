package com.redsquare.citizen.entity;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.devkit.sprite_gen.Tilemapping;
import com.redsquare.citizen.entity.collision.Collider;
import com.redsquare.citizen.entity.movement.MovementLogic;
import com.redsquare.citizen.graphics.*;
import com.redsquare.citizen.item.Inventory;
import com.redsquare.citizen.systems.language.Language;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.systems.politics.CulturalNameProfile;
import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.systems.politics.Family;
import com.redsquare.citizen.systems.politics.Settlement;
import com.redsquare.citizen.systems.psychology.Psychology;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.*;
import com.redsquare.citizen.worldgen.World;
import com.redsquare.citizen.worldgen.WorldPosition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Person extends LivingMoving implements ICharacter {
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
  protected Word[] name;
  protected Word called;

  private boolean alive;

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
  private Psychology psychology;

  /* Animation */
  private RenderMood mood;
  private boolean talking;
  private boolean blinking;

  private int blinkCounter = 0;
  private int blinkDuration = 5;
  private int betweenBlinks = 50;

  private Person(Person father, Person mother, GameDate birthday,
                   Settlement birthplace, World world) {
    this.movementLogic = MovementLogic.assign(this);
    this.sex = Math.random() < 0.5 ? Sex.MALE : Sex.FEMALE;
    this.inventory = Inventory.createInventory(this);

    this.father = father;
    this.mother = mother;
    this.birthday = birthday;
    this.birthplace = birthplace;

    this.position = tempSpawnPosition(world);
    this.collider = Collider.getColliderFromType(Collider.EntityType.PERSON);

    this.mood = RenderMood.NEUTRAL;
    this.talking = false;
    this.blinking = false;

    this.children = new HashSet<>();

    // TODO: name generation

    motherTongue = motherTongueGeneration();
    languages = Set.of(motherTongue);
    culture = cultureGeneration();
    family = familyGeneration();
    psychology = Psychology.init(this, father, mother);

    skinColor = skinColorGeneration();
    hairColor = hairColorGeneration();
    height = heightGeneration();
    bodyType = randomBodyType();

    this.mother.children.add(this);
    this.father.children.add(this);

    this.name = nameGeneration();
    // TODO: just have the nickname be the first given name for now
    this.called = name[0];
  }

  Person(Sex sex, GameDate birthday, Settlement birthplace, World world) {
    this.movementLogic = MovementLogic.assign(this);
    this.sex = sex;
    this.inventory = Inventory.createInventory(this);

    this.birthday = birthday;
    this.birthplace = birthplace;

    this.father = null;
    this.mother = null;

    this.position = tempSpawnPosition(world);
    this.collider = Collider.getColliderFromType(Collider.EntityType.PERSON);

    this.mood = RenderMood.NEUTRAL;
    this.talking = false;
    this.blinking = false;

    this.children = new HashSet<>();

    motherTongue = birthplace.getState().getLanguage();
    languages = Set.of(motherTongue);
    culture = birthplace.getState().getCulture();
    family = Family.generate();
    psychology = Psychology.init(this);

    skinColor = birthplace.getState().getCulture().
            getNativeRace().generateSkinColor();
    hairColor = birthplace.getState().getCulture().
            getNativeRace().generateHairColor();
    height = randomHeight();
    bodyType = randomBodyType();

    this.name = noParentsNameGeneration();
  }

  public static Person create(Sex sex, GameDate birthday,
                              Settlement birthplace, World world) {
    return new Person(sex, birthday, birthplace, world);
  }

  public static Person birth(Person mother, Person father, GameDate birthday,
                             Settlement birthplace, World world) {
    return new Person(father, mother, birthday, birthplace, world);
  }

  private WorldPosition tempSpawnPosition(World world) {
    Point worldPos = birthplace.getLocation();
    Point cell = new Point(WorldPosition.CELLS_IN_WORLD_CELL_DIM / 2,
            WorldPosition.CELLS_IN_WORLD_CELL_DIM / 2);
    FloatPoint subCell = WorldPosition.randomizeWithinSubCell();

    return new WorldPosition(worldPos, cell, subCell, world, this);
  }

  private void spriteSetup() {
    this.layers = new Sprite[LAYER_AMOUNT];
    spriteBodySetup();
    spriteHeadSetup();
  }

  private void spriteBodySetup() {
    BufferedImage texture = Tilemapping.getBody(skinColor);
    layers[BODY_LAYER] = new Sprite(texture, "BODY LAYER",
            SPRITE_WIDTH, SPRITE_HEIGHT, SemanticMaps.HOMINID_BODY);
  }

  private void spriteHeadSetup() {
    BufferedImage texture = Tilemapping.getHead(skinColor, hairColor);
    layers[HEAD_LAYER] = new Sprite(texture, "HEAD LAYER",
            80, 100, SemanticMaps.HOMINID_FACE);
  }

  public Settlement getBirthplace() {
    return birthplace;
  }

  public Sex getSex() {
    return sex;
  }

  public Height getHeight() {
    return height;
  }

  public BodyType getBodyType() {
    return bodyType;
  }

  public Color getSkinColor() {
    return skinColor;
  }

  public Color getHairColor() {
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

  private Word[] givenNameGeneration() {
    Word[] givenNames =
            new Word[culture.getNameProfile().pickGivenNameAmount()];

    for (int i = 0; i < givenNames.length; i++) {
      givenNames[i] = sex == Sex.FEMALE ?
              culture.getNameProfile().getFemaleName().getName() :
              culture.getNameProfile().getMaleName().getName();
    }

    return givenNames;
  }

  private Word[] parentalSurnameGeneration() {
    Person surnameDeterminer =
            culture.getInheritance() != Culture.Inheritance.MATRILINEAL ?
                    father : mother;

    Person[] surnameGivers =
            surnameDeterminer.getCulture().getNameProfile().
                    getSurnameConvention() ==
                    CulturalNameProfile.SurnameConvention.BOTH_PARENTS ?
                    new Person[] { father, mother } :
                    new Person[] { surnameDeterminer };

    return culture.getNameProfile().generateSurname(
            surnameGivers, sex, motherTongue);
  }

  private Word[] noParentsNameGeneration() {
    Word[] surnames = new Word[] {
            Word.generateRandomWord(2, 4,
                    motherTongue.getPhonology())
    };
    Word[] givenNames = givenNameGeneration();
    return nameCombination(givenNames, surnames);
  }

  private Word[] nameCombination(Word[] givenNames, Word[] surnames) {
    Word[] names = new Word[givenNames.length + surnames.length];

    System.arraycopy(givenNames, 0, names, 0, givenNames.length);
    System.arraycopy(surnames, 0, names, givenNames.length, surnames.length);

    return names;
  }

  private Word[] nameGeneration() {
    Word[] surnames = parentalSurnameGeneration();
    Word[] givenNames = givenNameGeneration();
    return nameCombination(givenNames, surnames);
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

    return ColorMath.colorBetween(darker, lighter, skinSkew);
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

    return ColorMath.colorBetween(darker, lighter, hairSkew);
  }

  @Override
  public String getFormalName() {
    return Formatter.properNoun(name[0].toString()) + " " +
            Formatter.properNoun(name[name.length - 1].toString());
  }

  @Override
  public String getFamiliarName() {
    return Formatter.properNoun(called.toString());
  }

  public enum Height {
    TALL, MEDIUM, SHORT
  }

  public enum BodyType {
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
    return movementLogic.renderLogic().getDirection().name() +
            "-" + mood.name() + "-" +
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
  public int age(GameDate now) {
    return GameDate.yearsBetween(now, birthday);
  }

  @Override
  public boolean isAlive() {
    return alive;
  }

  @Override
  public BufferedImage getSprite() {
    if (!spritesSetUp())
      spriteSetup();

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
          // TODO: temp null checker
          if (i != HEAD_LAYER) break;

          Point offset = SemanticMaps.faceOffset(movementLogic.renderLogic());
          g.drawImage(layers[i].getSprite(faceSpriteCode),
                  offset.x, offset.y, null);
          break;
      }
    }

    if (GameDebug.isActive()) {
      g.setColor(new Color(100, 255, 0));
      g.setStroke(new BasicStroke(1));

      Collider.CollisionBox[] boxes = collider.getBoxes();
      Point offset = getSpriteOffset();

      for (Collider.CollisionBox box : boxes) {
        int x = (box.getStart()[0] - offset.x),
                y = (box.getStart()[1] - offset.y);

        g.drawRect(x, y, box.getSize()[0], box.getSize()[1]);
      }
    }

    return sprite;
  }

  /* UTILITY FUNCTIONS */
  public Psychology getPsychology() {
    return psychology;
  }

  public Word[] getName() {
    return name;
  }

  public String formatEntireName() {
    StringBuilder sb = new StringBuilder();

    for (Word name : name) {
      sb.append(Formatter.properNoun(name.toString()));
      sb.append(" ");
    }

    return sb.toString().trim();
  }

  public Culture getCulture() {
    return culture;
  }

  @Override
  public Point getSpriteOffset() {
    return new Point((-1 * SPRITE_WIDTH) / 2, (-1 * SPRITE_HEIGHT) + 12);
  }

  @Override
  public void update() {
    super.update();
  }

  @Override
  public void renderUpdate() {
    blinkingUpdate();
  }
}
