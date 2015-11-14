package com.tayek.audio;
import java.io.BufferedInputStream;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
public class Audio implements Runnable {
    public enum Sound {
        electronic,glass,door;
    }
    private Audio(String filename) {
        this.filename=filename;
    }
    @Override public void run() {
        started=true;
        completed=false;
        try {
            Clip clip=AudioSystem.getClip();
            AudioInputStream inputStream=AudioSystem.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream(filename)));
            if(inputStream!=null) {
                clip.open(inputStream);
                FloatControl gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(+6.0f); // ?
                clip.start();
                // probably need a timeout here!!
                while(clip.getMicrosecondLength()!=clip.getMicrosecondPosition())
                    Thread.yield(); // wait
            } else {
                logger.warning("audio"+" "+" null inpit stream!");
            }
            completed=true;
        } catch(Exception e) {
            System.err.println(e);
            completed=false;
        }
    }
    private static Audio play(String filename) {
        Audio audio=new Audio(filename);
        Thread thread=new Thread(audio,"audio");
        audio.thread=thread;
        thread.start();
        return audio;
    }
    public static Audio play(Sound sound) {
        switch(sound) {
            case electronic:
                return play("Electronic_Chime-KevanGC-495939803.wav");
            case glass:
                return play("glass_ping-Go445-1207030150.wav");
            case door:
                return play("Store_Door_Chime-Mike_Koenig-570742973.wav");
            default:
                logger.warning(""+" "+"default where!");
                return null;
        }
    }
    public static void main(String[] args) throws InterruptedException {
        for(Sound sound:Sound.values()) {
            System.out.println(sound);
            Audio audio=play(sound);
            if(audio!=null) audio.thread.join();
            else System.out.println("no sound for: "+sound);
        }
    }
    final String filename;
    transient boolean started,completed;
    Thread thread;
    public static final Logger logger=Logger.getLogger(Audio.class.getName());
}
