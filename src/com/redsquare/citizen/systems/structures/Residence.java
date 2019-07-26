package com.redsquare.citizen.systems.structures;

import com.redsquare.citizen.entity.Person;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

class Residence extends Building {
  private Person owner;
  private Set<Person> residents;

  private Residence(Street street, boolean lhs, int location) {
    super(street, lhs, location);
    this.owner = null;
    this.residents = new HashSet<>();
  }

  static Residence generate(Street street, boolean lhs, int location) {
    return new Residence(street, lhs, location);
  }

  @Override
  void draw(Graphics2D g) {
    draw(g, new Color(255, 0, 0));
  }
}
