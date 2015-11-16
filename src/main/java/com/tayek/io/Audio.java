package com.tayek.io;
import java.io.BufferedInputStream;
import java.util.logging.Logger;
import javax.sound.sampled.*;
public interface Audio {
    enum Sound {
        electronic_chime_kevangc_495939803,glass_ping_go445_1207030150,store_door_chime_mike_koenig_570742973;
    }
    void play(Sound sound);
    static class Windows implements Audio {
        private Windows() {}
        @Override public void play(final Sound sound) {
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        String filename=sound.name()+".wav";
                        Clip clip=AudioSystem.getClip();
                        System.out.println("clip: "+clip);
                        AudioInputStream inputStream=AudioSystem.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream(filename)));
                        if(inputStream!=null) {
                            System.out.println(inputStream);
                            clip.open(inputStream);
                            System.out.println("is open");
                            FloatControl gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                            gainControl.setValue(+6.0f); // ?
                            clip.start();
                            while(clip.getMicrosecondLength()!=clip.getMicrosecondPosition())
                                Thread.yield(); // wait
                            Thread.sleep(300);
                        }
                    } catch(Exception e) {
                        System.out.println("failed to play: "+sound);
                    }
                };
            },"play sound").start();
        }
    }
    public static class Android implements Audio {
        public interface Callback<T> {
            void call(T t);
        }
        private Android() {}
        @Override public void play(Sound sound) {
            if(callback!=null) callback.call(sound);
            else {
                System.out.println("set callback!");
                StackTraceElement[] x=Thread.currentThread().getStackTrace();
                for(int i=0;i<Math.min(10,x.length);i++)
                    System.out.println(x[i]);
            }
        }
        public void setCallback(Callback<Sound> callback) {
            this.callback=callback;
        }
        public Callback<Sound> callback;
    }
    interface Factory {
        abstract Audio create();
        static class Implementation implements Factory {
            private Implementation() {}
            @Override public Audio create() {
                // says linux, so look for something that says android!
                if(System.getProperty("os.name").contains("indows")) return new Audio.Windows();
                else return new Audio.Android();
            }
            private static Factory instance() {
                return factory;
            }
            private static Factory factory=new Implementation();
        }
    }
    Factory factory=Factory.Implementation.instance();
    Logger logger=Logger.getLogger(Audio.class.getName());
}
