package com.redsquare.citizen.input_events;

import com.redsquare.citizen.debug.GameDebug;

public class KeyPressEvent extends Event {
  public final char key;
  public final EventType eventType;

  public KeyPressEvent(char key, EventType eventType) {
    this.key = key;
    this.eventType = eventType;

    GameDebug.printMessage(this.toString(), GameDebug::printDebug);
  }

  public enum EventType {
    PRESSED, RELEASED, TYPED
  }

  @Override
  public String toString() {
    return eventType.name() + " (" + key + ")";
  }
}
