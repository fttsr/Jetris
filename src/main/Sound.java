package main;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    Clip musicClip;
    URL url[] = new URL[10];

    public Sound() {
        url[0] = getClass().getResource("/Jetris.wav");
        url[1] = getClass().getResource("/deleteline.wav");
        url[2] = getClass().getResource("/game-over.wav");
        url[3] = getClass().getResource("/rotation.wav");
        url[4] = getClass().getResource("/touchfloor.wav");
    }

    public void play(int i, boolean music) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();  // we get dir bu url and put it in clip object

            if (music) {
                musicClip = clip;
            }

            clip.open(ais);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();   // when audio is finished
                    }
                }
            });
            ais.close();
            clip.start();

        } catch (Exception e) {

        }
    }
    public void loop() {
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop() {
        musicClip.stop();
        musicClip.close();
    }
}
