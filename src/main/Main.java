package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame("Jetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Adding game panel to window
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();  // GamePanel size = Window size

        window.setLocationRelativeTo(null); // Window opens in the center
        window.setVisible(true);

        gp.launchGame();
    }
}