package com.redsquare.citizen.entity;

public abstract class Animal extends Lifeform {

  protected Sex sex;

  protected enum Sex {
    MALE, FEMALE
  }
}
