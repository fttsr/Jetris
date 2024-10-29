package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

// Draws the UI (Play area), manages tetrominoes, handles gameplay actions
                                                // (deleting lines, adding scores, etc.)
public class PlayManager {

    // Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;

    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Others
    public static int dropInterval = 60; // mino drops in every 60 frames (1 second)
    // game over
    boolean gameOver;

    // Effect when line deletes
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Color array for the random effect
    Color[] colors = new Color[5];

    // Score
    int level = 1;
    int lines;
    int score;

    public PlayManager() {

        // Main Play Area Frame
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2); // 460
        right_x = left_x + WIDTH;
        top_y = (GamePanel.HEIGHT / 2) - (HEIGHT / 2); // 60 ? 50
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        // Set the Starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);

        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

        // Fill colors arr for effects
        colors[0] = Color.red;
        colors[1] = Color.cyan;
        colors[2] = Color.green;
        colors[3] = Color.yellow;
        colors[4] = Color.white;

    }

    private Mino pickMino() {
        // Pick a random Mino
        Mino mino = null;
        int i = new Random().nextInt(7);    // between 0 to 6

        switch(i) {
            case 0:
                mino = new Mino_L1(); break;
            case 1:
                mino = new Mino_L2(); break;
            case 2:
                mino = new Mino_Square(); break;
            case 3:
                mino = new Mino_Bar(); break;
            case 4:
                mino = new Mino_T(); break;
            case 5:
                mino = new Mino_Z1(); break;
            case 6:
                mino = new Mino_Z2(); break;
        }
        return mino;
    }

    public void update() {

        //Check if the current mino is active
        if (!currentMino.active) {
            // if the mino is not active, put it in static blocks array
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            // check if the game is over
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.sfx.play(2, true);
            }

            currentMino.deactivating = false;

            // replace curr mino with next mino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            // when Mino becomes inactive, check if line can be deleted
            checkDelete();

        } else {
            currentMino.update();
        }

    }

    private void checkDelete() {

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {

            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                    // increase the counter of blocks
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if (x == right_x) {

                // if the blockCount = 12, it means that y line is completely filled so we can delete it
                if (blockCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size() - 1; i > 0; i --) {
                        // remove all blocks in the current y line
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;
                    // Drop Speed incr
                    // 1 is the fastest
                    if (lines % 3 == 0 && dropInterval > 1) {   // every 3 lines +1 level
                        level++;
                        if (dropInterval > 10) {
                            dropInterval -= 10;
                        } else {
                            dropInterval -= 1;
                        }
                    }

                    // a line has been deleted; shifting all blocks down
                    for (int i = 0; i < staticBlocks.size(); i++) {
                        if (staticBlocks.get(i).y < y) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }

                }


                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        // Add Score
        if (lineCount > 0) {
            GamePanel.sfx.play(1, false);
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics2D g2) {

        // Draw Main Play Area Frame
        g2.setColor(Color.getHSBColor(20, 84, 64));
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

        // Draw Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Open Sans", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 60, y + 60);

        // Draw Score Frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y); y += 70;
        g2.drawString("LINES: " + lines, x, y); y += 70;
        g2.drawString("SCORE: " + score, x, y);

        // Draw the currentMino
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        // Draw the next Mino
        nextMino.draw(g2);

        // Draw static blocks
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }

        // Random color
        Random random = new Random();
        int randomIndex = random.nextInt(colors.length);
        Color randomColor = colors[randomIndex];


        // Draw delete effect
        if (effectCounterOn) {
            effectCounter++;


            g2.setColor(randomColor);
            for (int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if (effectCounter == 10) {  // 10 frames
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Draw pause or gameover
        if (gameOver) {
            g2.setFont(new Font("Courier New", Font.BOLD, 50));
            g2.setColor(randomColor);
            g2.drawString("G", WIDTH / 2 + 308, HEIGHT / 2);
            g2.setColor(randomColor);
            g2.drawString("A", WIDTH / 2 + 332, HEIGHT / 2);
            g2.setColor(randomColor);
            g2.drawString("M", WIDTH / 2 + 356, HEIGHT / 2);
            g2.setColor(randomColor);
            g2.drawString("E", WIDTH / 2 + 380, HEIGHT / 2);
            g2.setColor(randomColor);
            g2.drawString("O", WIDTH / 2 + 500, HEIGHT / 2);
            g2.setColor(randomColor);
            g2.drawString("V", WIDTH / 2 + 524, HEIGHT / 2);
            g2.setColor(randomColor);
            g2.drawString("E", WIDTH / 2 + 548, HEIGHT / 2);
            g2.setColor(randomColor);
            g2.drawString("R", WIDTH / 2 + 572, HEIGHT / 2);
            g2.setFont(new Font("Courier New", Font.BOLD, 42));
        } else if (KeyHandler.pausePressed) {
            g2.setColor(Color.getHSBColor(0, 100 / 100f, 100 / 100f));
            g2.drawString("P", WIDTH / 2 - 75, HEIGHT / 2);
            g2.setColor(Color.getHSBColor(30 / 360f, 100 / 100f, 100 / 100f));
            g2.drawString("A", WIDTH / 2 - 45, HEIGHT / 2);
            g2.setColor(Color.getHSBColor(120 / 360f, 100 / 100f, 100 / 100f));
            g2.drawString("U", WIDTH / 2 - 15, HEIGHT / 2);
            g2.setColor(Color.getHSBColor(240 / 360f, 100 / 100f, 100 / 100f));
            g2.drawString("S", WIDTH / 2 + 15, HEIGHT / 2);
            g2.setColor(Color.getHSBColor(270 / 360f, 100 / 100f, 100 / 100f));
            g2.drawString("E", WIDTH / 2 + 45, HEIGHT / 2);
        }

        // Draw the game title
        x = 35;
        y = top_y + 320;
        g2.setFont(new Font("Montserrat", Font.ITALIC, 60));
        g2.drawString("Jetris.", x + 20 ,y - 275);
        g2.setFont(new Font("Montserrat", Font.ITALIC, 40));
        g2.drawString("Tetris made in Java", x + 20, y - 215 );
        g2.setFont(new Font("Montserrat", Font.ITALIC, 20));
        g2.drawString("By: Prosvirkin Nikita", x + 20, y + 285);

    }
}
