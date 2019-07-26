package com.redsquare.citizen;

import com.redsquare.citizen.input_events.ClickEvent;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements KeyListener,
        MouseListener, MouseMotionListener {

  private boolean mouseDown;

  private int mouseX;
  private int mouseY;

  private List<Event> unprocessed;

  private InputHandler(GamePanel gamePanel) {
    gamePanel.addKeyListener(this);
    gamePanel.addMouseListener(this);
    gamePanel.addMouseMotionListener(this);

    mouseX = 0;
    mouseY = 0;

    mouseDown = false;

    unprocessed = new ArrayList<>();
  }

  public List<Event> getUnprocessedEvents() {
    return unprocessed;
  }

  void clearUnprocessedEvents() {
    unprocessed = new ArrayList<>();
  }

  public int getMouseX() {
    return mouseX;
  }

  public int getMouseY() {
    return mouseY;
  }

  public boolean isMouseDown() {
    return mouseDown;
  }

  static InputHandler create(GamePanel gamePanel) {
    return new InputHandler(gamePanel);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    unprocessed.add(new KeyPressEvent(e.getKeyChar(),
            KeyPressEvent.EventType.TYPED));
  }

  @Override
  public void keyPressed(KeyEvent e) {
    unprocessed.add(new KeyPressEvent(e.getKeyChar(),
            KeyPressEvent.EventType.PRESSED));
  }

  @Override
  public void keyReleased(KeyEvent e) {
    unprocessed.add(new KeyPressEvent(e.getKeyChar(),
            KeyPressEvent.EventType.RELEASED));
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    setLocation(e);

    unprocessed.add(new ClickEvent(mouseX, mouseY));
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mouseDown = true;
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mouseDown = false;
  }

  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {

  }

  @Override
  public void mouseDragged(MouseEvent e) {
    setLocation(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    setLocation(e);
  }

  private void setLocation(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }
}
