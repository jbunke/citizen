package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.psychology.PersonTraits;

public enum Virtues {
  HUMILITY(
          new PersonTraits[] {
                  PersonTraits.SELF_ESTEEM, PersonTraits.IDEALISM,
                  PersonTraits.CHARISMA
          },
          new PersonTraits[] {
                  PersonTraits.INTELLECT, PersonTraits.ATTRACTIVENESS,
                  PersonTraits.AMBITION
          }, new double[] { 0.5, 0.7, 0.1 }),
  FILIAL_PIETY(
          new PersonTraits[] {
                  PersonTraits.EMPATHY, PersonTraits.IDEALISM, PersonTraits.CHARISMA,
                  PersonTraits.LOYALTY
          },
          new PersonTraits[] {
                  PersonTraits.RUTHLESSNESS, PersonTraits.AMBITION
          }, new double[] { 0.4, 0.2, 0.6 }),
  PIETY(
          new PersonTraits[] {
                  PersonTraits.IDEALISM
          },
          new PersonTraits[] {
                  PersonTraits.OPEN_MINDEDNESS
          }, new double[] { 1.0, 0.3, 0. }),
  KINDNESS(
          new PersonTraits[] {
                  PersonTraits.EMPATHY, PersonTraits.CHARISMA
          },
          new PersonTraits[] {
                  PersonTraits.RUTHLESSNESS, PersonTraits.AMBITION
          }, new double[] { 0.2, 0.3, 0.9 }),
  DEDICATION(
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.INTELLECT, PersonTraits.IDEALISM
          },
          new PersonTraits[] {
                  PersonTraits.RISK_TAKING, PersonTraits.AGREEABLENESS
          }, new double[] { 0.1, 0.9, 0.1 }),
  CHASTITY(
          new PersonTraits[] {
                  PersonTraits.IDEALISM
          },
          new PersonTraits[] {
                  PersonTraits.ATTRACTIVENESS, PersonTraits.CHARISMA, PersonTraits.OPEN_MINDEDNESS
          }, new double[] { 0.5, 0.2, 0.5 }),
  CHARITY(
          new PersonTraits[] {
                  PersonTraits.IDEALISM, PersonTraits.EMPATHY,
                  PersonTraits.AGREEABLENESS
          },
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.RUTHLESSNESS
          }, new double[] { 0.3, -0.2, 0.8 }),
  HONESTY(
          new PersonTraits[] {
                  PersonTraits.IDEALISM, PersonTraits.INTELLECT,
                  PersonTraits.EMPATHY
          },
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.CHARISMA,
                  PersonTraits.ATTRACTIVENESS, PersonTraits.RISK_TAKING
          }, new double[] { -0.1, 0.5, 0.4 }),
  SOBRIETY(
          new PersonTraits[] {
                  PersonTraits.IDEALISM, PersonTraits.INTELLECT
          },
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.CHARISMA,
                  PersonTraits.ATTRACTIVENESS, PersonTraits.RISK_TAKING
          }, new double[] { 0.1, 0.6, 0.5 }),
  HAVE_MANY_CHILDREN(
          new PersonTraits[] {
                  PersonTraits.CHARISMA, PersonTraits.PARENTAL_INSTINCTS
          },
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.RUTHLESSNESS
          }, new double[] { 0.6, 0.5, 0.2 }),
  TREAT_PARENTING_AS_MOST_IMPORTANT_TASK(
          new PersonTraits[] {
                  PersonTraits.PARENTAL_INSTINCTS, PersonTraits.IDEALISM
          },
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.RUTHLESSNESS
          }, new double[] { 0.2, 0.7, 0.7 }),
  MINISTRY(
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.IDEALISM,
                  PersonTraits.LOYALTY
          },
          new PersonTraits[] {
                  PersonTraits.RISK_TAKING, PersonTraits.ATTRACTIVENESS
          }, new double[] { 0.8, 0.2, 0.3 }),
  CONQUEST(
          new PersonTraits[] {
                  PersonTraits.RUTHLESSNESS, PersonTraits.IDEALISM, PersonTraits.AMBITION
          },
          new PersonTraits[] {
                  PersonTraits.AGREEABLENESS
          }, new double[] { 1., -0.2, -0.4 }),
  PILGRIMAGE(
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.IDEALISM, PersonTraits.RISK_TAKING
          },
          new PersonTraits[] {
                  PersonTraits.AGREEABLENESS
          }, new double[] { 1., 0.3, 0. }),
  RESPECT_FOR_ANIMALS(
          new PersonTraits[] {
                  PersonTraits.AMBITION, PersonTraits.IDEALISM, PersonTraits.RISK_TAKING
          },
          new PersonTraits[] {
                  PersonTraits.AGREEABLENESS
          }, new double[] { 0d, 0.1, 1. });

  private final PersonTraits[] correlated;
  private final PersonTraits[] negativelyCorrelated;
  private final double[] priorities;

  Virtues(final PersonTraits[] correlated,
          final PersonTraits[] negativelyCorrelated,
          final double[] priorities) {
    this.correlated = correlated;
    this.negativelyCorrelated = negativelyCorrelated;
    this.priorities = priorities;
  }

  public PersonTraits[] getCorrelatedTraits() {
    return correlated;
  }

  public PersonTraits[] getNegativelyCorrelatedTraits() {
    return negativelyCorrelated;
  }

  public double[] getPriorities() {
    return priorities;
  }
}
