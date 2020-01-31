package com.redsquare.citizen.systems.psychology;

import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.util.MathExt;
import com.redsquare.citizen.util.Randoms;

// TODO: Refactor class to use PersonTraits in a map from PersonTrait to tendency double

public class Psychology {
  private static final int TENDENCY_COUNT = 13;
  public static final int PARENTAL_INSTINCTS = 0, AMBITION = 1, IDEALISM = 2,
          CHARISMA = 3, SELF_ESTEEM = 4, INTELLECT = 5, EMPATHY = 6,
          AGREEABLENESS = 7, RUTHLESSNESS = 8, RISK_TAKING = 9, LOYALTY = 10,
          ATTRACTIVENESS = 11, OPEN_MINDEDNESS = 12;

  private final double[] tendencies = new double[TENDENCY_COUNT];

  // TODO: Identity, Ideologies, Mental health, Relationships

  private final Person associated;

  private final Person father;
  private final Person mother;

  private Psychology(Person associated, Person father, Person mother) {
    this.associated = associated;

    this.father = father;
    this.mother = mother;

    for (int i = 0; i < TENDENCY_COUNT; i++) {
      if (i == INTELLECT || i == ATTRACTIVENESS)
        tendencies[i] = MathExt.bounded(
                ((father.getPsychology().tendencies[i] +
                        mother.getPsychology().tendencies[i]) / 2.0) *
                        Randoms.bounded(0.8, 1.25),
                0.0, 1.0);
      else
        tendencies[i] = Randoms.bounded(0.1, 0.9);
    }
  }

  private Psychology(Person associated) {
    this.associated = associated;

    this.father = null;
    this.mother = null;

    for (int i = 0; i < TENDENCY_COUNT; i++) {
      tendencies[i] = Randoms.bounded(0.1, 0.9);
    }
  }

  public static Psychology init(Person person, Person father, Person mother) {
    return new Psychology(person, father, mother);
  }

  public static Psychology init(Person person) {
    return new Psychology(person);
  }

  public void macroUpdate(GameDate now) {
    tendencyAnnualUpdate(now);


  }

  private void tendencyAnnualUpdate(GameDate now) {
    int age = associated.age(now);
    boolean fatherIsAlive = father != null && father.isAlive();
    boolean motherIsAlive = mother != null && mother.isAlive();
    boolean fatherIsClose = fatherIsAlive &&
            associated.position().isCloseTo(father.position());
    boolean motherIsClose = motherIsAlive &&
            associated.position().isCloseTo(mother.position());

    if (age < 10) {
      if (fatherIsClose) {
        tendencies[AMBITION] += Randoms.bounded(-0.02, 0.04);
        tendencies[RISK_TAKING] += Randoms.bounded(-0.02, 0.04);
      } else {
        tendencies[AMBITION] += Randoms.bounded(-0.04, 0.02);
        tendencies[RISK_TAKING] += Randoms.bounded(-0.04, 0.02);
      }

      if (motherIsClose) {
        tendencies[RUTHLESSNESS] += Randoms.bounded(-0.04, 0.02);
        tendencies[EMPATHY] += Randoms.bounded(-0.02, 0.04);
        tendencies[OPEN_MINDEDNESS] += Randoms.bounded(-0.02, 0.04);
        tendencies[INTELLECT] += Randoms.bounded(-0.02, 0.04);
      } else {
        tendencies[RUTHLESSNESS] += Randoms.bounded(-0.02, 0.04);
        tendencies[EMPATHY] += Randoms.bounded(-0.04, 0.02);
        tendencies[OPEN_MINDEDNESS] += Randoms.bounded(-0.04, 0.02);
        tendencies[INTELLECT] += Randoms.bounded(-0.04, 0.02);
      }
    } else if (age < 16) {
      if (fatherIsClose) {
        tendencies[IDEALISM] += Randoms.bounded(-0.02, 0.04);
        tendencies[CHARISMA] += Randoms.bounded(-0.02, 0.04);
        tendencies[RISK_TAKING] += Randoms.bounded(-0.04, 0.02);
        tendencies[OPEN_MINDEDNESS] += Randoms.bounded(-0.04, 0.02);
      } else {
        tendencies[IDEALISM] += Randoms.bounded(-0.04, 0.02);
        tendencies[CHARISMA] += Randoms.bounded(-0.04, 0.02);
        tendencies[RISK_TAKING] += Randoms.bounded(-0.02, 0.04);
        tendencies[OPEN_MINDEDNESS] += Randoms.bounded(-0.02, 0.04);
      }

      if (motherIsClose) {
        tendencies[RISK_TAKING] += Randoms.bounded(-0.04, 0.02);
        tendencies[AGREEABLENESS] += Randoms.bounded(-0.02, 0.04);
        tendencies[PARENTAL_INSTINCTS] += Randoms.bounded(-0.02, 0.04);
        tendencies[SELF_ESTEEM] += Randoms.bounded(-0.02, 0.04);
        tendencies[LOYALTY] += Randoms.bounded(-0.02, 0.04);
      } else {
        tendencies[RISK_TAKING] += Randoms.bounded(-0.02, 0.04);
        tendencies[AGREEABLENESS] += Randoms.bounded(-0.04, 0.02);
        tendencies[PARENTAL_INSTINCTS] += Randoms.bounded(-0.04, 0.02);
        tendencies[SELF_ESTEEM] += Randoms.bounded(-0.04, 0.02);
        tendencies[LOYALTY] += Randoms.bounded(-0.04, 0.02);
      }
    } else if (age < 40) {
      tendencies[IDEALISM] += Randoms.bounded(-0.02, 0.04 * tendencies[IDEALISM]);
    }

    tendenciesBounding();
  }

  private void tendenciesBounding() {
    for (int i = 0; i < TENDENCY_COUNT; i++) {
      tendencies[i] = MathExt.bounded(tendencies[i], 0.0, 1.0);
    }
  }
}
