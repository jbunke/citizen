package com.redsquare.citizen.systems.language;

public enum Meaning {
  // CORE MORPHEMES // LESSER

  // Relationships
  PARENT(0, LexClass.NOUN), CHILD_OF(0, LexClass.NOUN), SIBLING(0, LexClass.NOUN),
  LOVER(0, LexClass.NOUN), FRIEND(0, LexClass.NOUN),
  // LESSER
  MOTHER(1, LexClass.NOUN), FATHER(1, LexClass.NOUN),
  SON(1, LexClass.NOUN), DAUGHTER(1, LexClass.NOUN),
  BROTHER(1, LexClass.NOUN), SISTER(1, LexClass.NOUN),
  BOYFRIEND(2, LexClass.NOUN), GIRLFRIEND(2, LexClass.NOUN),
  MARRIAGE(1, LexClass.NOUN), SPOUSE(1, LexClass.NOUN),
  HUSBAND(2, LexClass.NOUN), WIFE(2, LexClass.NOUN),
  COUSIN(2, LexClass.NOUN), UNCLE(2, LexClass.NOUN), AUNT(2, LexClass.NOUN),
  GRANDFATHER(2, LexClass.NOUN), GRANDMOTHER(2, LexClass.NOUN),
  ENEMY(1, LexClass.NOUN),

  // Time
  DAY_OF_YEAR(0, LexClass.NOUN), YEAR(0, LexClass.NOUN),
  DAY(0, LexClass.NOUN), NIGHT(0, LexClass.NOUN),
  EARLY(0, LexClass.ADJECTIVE), LATE(0, LexClass.ADJECTIVE),

  // World
  STATE(0, LexClass.NOUN), REGION(0, LexClass.NOUN), TOWN(0, LexClass.NOUN),
  CITY(0, LexClass.NOUN), FARM(0, LexClass.NOUN),
  // LESSER
  CAPITAL(1, LexClass.NOUN),

  // People
  PERSON(0, LexClass.NOUN),
  YOUNG(0, LexClass.ADJECTIVE), OLD(0, LexClass.ADJECTIVE),
  MALE(0, LexClass.ADJECTIVE), FEMALE(0, LexClass.ADJECTIVE),
  // LESSER
  MAN(1, LexClass.NOUN), WOMAN(1, LexClass.NOUN),
  CHILD(1, LexClass.NOUN), BOY(1, LexClass.NOUN), GIRL(1, LexClass.NOUN),
  ADOLESCENT(1, LexClass.NOUN), SENIOR_PERSON(1, LexClass.NOUN),

  // Colours
  BLACK(0, LexClass.ADJECTIVE), WHITE(0, LexClass.ADJECTIVE),
  GREY(0, LexClass.ADJECTIVE), YELLOW(0, LexClass.ADJECTIVE),
  GREEN(0, LexClass.ADJECTIVE), BLUE(0, LexClass.ADJECTIVE),
  PURPLE(0, LexClass.ADJECTIVE), RED(0, LexClass.ADJECTIVE),
  // LESSER
  DARK(1, LexClass.ADJECTIVE), LIGHT_QLTY(1, LexClass.ADJECTIVE),

  // Concept
  MAIN(0, LexClass.ADJECTIVE), VERY_COMP(0, LexClass.ADVERB),
  GREAT_COMP(0, LexClass.ADJECTIVE),
  PROXIMAL(0, LexClass.ADJECTIVE), OPPOSITE(0, LexClass.ADJECTIVE),
  DISTANT(1, LexClass.ADJECTIVE),

  PLACEHOLDER(0, LexClass.INTERJECTION);

  private final int degree;
  private final LexClass lexClass;

  Meaning(int degree, LexClass lexClass) {
    this.degree = degree;
    this.lexClass = lexClass;
  }

  public LexClass getLexClass() {
    return lexClass;
  }

  public int getDegree() {
    return degree;
  }

  enum LexClass {
    NOUN, VERB, ADJECTIVE, ADVERB, CONJUNCTION, ARTICLE, PRONOUN,
    PREPOSITION, INTERJECTION
  }
}
