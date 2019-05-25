package com.redsquare.citizen.input_events;

public class ClickEvent extends Event {
  public final int mouseX;
  public final int mouseY;

  public ClickEvent(int mouseX, int mouseY) {
    this.mouseX = mouseX;
    this.mouseY = mouseY;
  }
}
