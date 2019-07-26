package com.redsquare.citizen.systems.structures;

import com.redsquare.citizen.entity.Person;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

class MarketStall extends Building {
  private Person owner;
  private Set<Person> employees;

  private MarketStall(Street street, boolean lhs, int location) {
    super(street, lhs, location);
    this.owner = null;
    this.employees = new HashSet<>();
  }

  static MarketStall generate(Street street, boolean lhs, int location) {
    return new MarketStall(street, lhs, location);
  }

  @Override
  void draw(Graphics2D g) {
    draw(g, new Color(0, 255, 0));
  }
}
