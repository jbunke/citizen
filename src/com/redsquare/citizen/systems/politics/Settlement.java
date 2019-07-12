package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.systems.language.PlaceNameGenerator;
import com.redsquare.citizen.systems.language.Word;
import com.redsquare.citizen.util.Formatter;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Settlement {
  private Word name;

  private State state;
  private Set<Settlement> vassals = new HashSet<>();
  private Settlement liege;
  private House rulingHouse;
  private int setupPower;
  private double economy = 1.0;

  private Point location;

  public Settlement(Point location, State state) {
    this.location = location;
    this.state = state;

    this.name = PlaceNameGenerator.generateRandomName(
            2, 4, state.getLanguage().getPhonology());

    setupPower = 0;
  }

  public State getState() {
    return state;
  }

  public String getName() {
    return name.toString();
  }

  public int getSetupPower() {
    return setupPower;
  }

  public Point getLocation() {
    return location;
  }

  public void accruePower(int rawPower) {
    double newEconomy = (0.9 * economy) + (Math.random() * 0.2 * economy);
    economy = Math.min(Math.max(1/2.0, newEconomy), 2/1.0);

    setupPower += (int)(economy * rawPower);
  }

  void setState(State state) {
    this.state = state;
    vassals.forEach(x -> x.setState(state));
  }

  public Settlement getLiege() {
    return liege;
  }

  Set<Settlement> getVassals() {
    return vassals;
  }

  void addVassals(Set<Settlement> newVassals) {
    vassals.addAll(newVassals);
    newVassals.forEach(x -> x.liege = this);
  }

  public void addVassal(Settlement newVassal) {
    vassals.add(newVassal);
    newVassal.liege = this;
  }

  void resetVassals() {
    vassals = new HashSet<>();
  }

  boolean isCapital() {
    return state.getCapital().equals(this);
  }

  public boolean isLiege() {
    return !vassals.isEmpty();
  }

  void removeLiege() {
    liege = null;
  }

  public int powerLevel() {
    if (isCapital()) return 1;

    if (liege.isCapital()) {
      if (!vassals.isEmpty()) return 2;
      return 3;
    }

    return 1 + liege.powerLevel();
  }

  @Override
  public String toString() {
    return Formatter.capitaliseFirstLetter(name.toString()) + "; in " +
            state.toString() + " at (" + location.x + ", " + location.y + ")";
  }
}
