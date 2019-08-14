package com.redsquare.citizen.game_states;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.GameManager;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class SplashScreenGameState extends GameState {
  private static final int TICK_DURATION = 400;

  private static final String S_S_FOLDER = "res/img_assets/splash_screens/";
  private static final int S_S_AMOUNT = 5;

  private static final String[] S_S = new String[S_S_AMOUNT];

  private final BufferedImage image;
  private int counter;

  public static SplashScreenGameState init() {
    for (int i = 0; i < S_S_AMOUNT; i++) {
      S_S[i] = S_S_FOLDER + "SPLASH_SCREEN_" + i + ".png";
    }

    return new SplashScreenGameState();
  }

  private SplashScreenGameState() {
    counter = 0;

    double prob = Math.random();
    final double shrink = 0.65;
    double comp = shrink;

    String splashScreen = S_S[0];

    for (int i = 0; i < S_S_AMOUNT; i++) {
      if (i + 1 == S_S_AMOUNT) {
        splashScreen = S_S[S_S_AMOUNT - 1];
        break;
      }

      if (prob < comp) {
        splashScreen = S_S[i];
        break;
      }

      comp = comp + (shrink * (1 - comp));
    }

    BufferedImage baseImage;
    try {
      baseImage = ImageIO.read(new File(splashScreen));
    } catch (IOException e) {
      GameDebug.printMessage(
              "Could not find splash screen resource file \"" +
                      splashScreen + "\"", GameDebug::printError);
      this.image = null;
      return;
    }

    this.image = baseImage;
  }

  @Override
  public void update() {
    counter++;

    if (counter >= TICK_DURATION) GameManager.get().setGameState(GameManager.MENU);
  }

  @Override
  public void render(Graphics2D g) {
    final int X = 0, Y = 1;

    g.setColor(new Color(0, 0, 0));
    g.fillRect(0, 0, Settings.SCREEN_DIM[0], Settings.SCREEN_DIM[1]);

    int x = (Settings.SCREEN_DIM[X] / 2) - (image.getWidth() / 2);
    int y = (Settings.SCREEN_DIM[Y] / 2) - (image.getHeight() / 2);

    g.drawImage(image, x, y, null);
    g.setColor(new Color(0, 0, 0, 2 * Math.max(0, 127 - counter)));
    g.fillRect(0, 0, Settings.SCREEN_DIM[X], Settings.SCREEN_DIM[Y]);
  }

  @Override
  public void input(InputHandler inputHandler) {

  }
}
