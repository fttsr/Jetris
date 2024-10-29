package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    public static Sound music = new Sound(); // Music
    public static Sound sfx = new Sound(); // Sound effect

    Thread gameThread; // Thread to run game loop
    PlayManager pm;

    public GamePanel() {

        // Panel settings
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setLayout(null);   // No presets

        // Implement KeyListener
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true); // GamePanel focus to receive the key

        pm = new PlayManager();
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();

        music.play(0, true);
        music.loop();
    }

    @Override
    public void run() { // ---- GAME LOOP DELTA/ACCUMULATOR METHOD

        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0; // this and count for displaying fps
        long drawCount = 0;

        while (gameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {  // FPS display
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }

        }
    }

    private void update() {
        if (!KeyHandler.pausePressed && !pm.gameOver) {
            pm.update();
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        pm.draw(g2);

        g2.setStroke(new BasicStroke(6f));
        g2.setFont(new Font("Courier New", Font.BOLD, 42));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    }

}
