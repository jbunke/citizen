package com.redsquare.citizen.input_events;

public class KeyPressEvent extends Event {
  public final char key;
  public final EventType eventType;

  public KeyPressEvent(char key, EventType eventType) {
    this.key = key;
    this.eventType = eventType;
  }

  public enum EventType {
    PRESSED, RELEASED, TYPED
  }
}
