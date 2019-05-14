package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.entity.Person;

import java.util.List;

public class House {
  private Settlement seat;

  private Person head;
  private List<Person> members;

  Settlement getSeat() {
    return seat;
  }

  void unseat(House target) {
    Settlement taken = target.seat;
    target.seat = null;

    if (taken.powerLevel() > this.seat.powerLevel()) {
      // absorb SEAT vassals into TAKEN
      taken.addVassals(this.seat.getVassals());
      this.seat.resetVassals();

      this.seat = taken;
    } else {
      // absorb TAKEN vassals into SEAT
      this.seat.addVassals(taken.getVassals());
      taken.resetVassals();
    }
  }
}
