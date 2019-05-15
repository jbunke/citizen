package com.redsquare.citizen.systems.language;

public enum Meaning {
  // CORE MORPHEMES // LESSER

  // Relationships
  PARENT(0), CHILD_OF(0), SIBLING(0),
  LOVER(0), FRIEND(0),
  // LESSER
  MOTHER(1), FATHER(1),
  SON(1), DAUGHTER(1),
  BROTHER(1), SISTER(1),
  BOYFRIEND(2), GIRLFRIEND(2),
  MARRIAGE(1), SPOUSE(1), HUSBAND(2), WIFE(2),
  COUSIN(2), UNCLE(2), AUNT(2),
  GRANDFATHER(2), GRANDMOTHER(2),
  ENEMY(1),

  // Time
  DAY_OF_YEAR(0), YEAR(0),
  DAY(0), NIGHT(0), EARLY(0), LATE(0),

  // World
  STATE(0), REGION(0), TOWN(0), CITY(0), FARM(0),
  // LESSER
  CAPITAL(1),

  // People
  PERSON(0), YOUNG(0), OLD(0), MALE(0), FEMALE(0),
  // LESSER
  MAN(1), WOMAN(1), CHILD(1), BOY(1), GIRL(1),
  ADOLESCENT(1), SENIOR_PERSON(1),

  // Colours
  BLACK(0), WHITE(0), GREY(0), YELLOW(0),
  GREEN(0), BLUE(0), PURPLE(0), RED(0),
  // LESSER
  DARK(1), LIGHT_COLOUR(1),

  PLACEHOLDER(0);

  private final int degree;

  Meaning(int degree) {
    this.degree = degree;
  }

  public int getDegree() {
    return degree;
  }
}
