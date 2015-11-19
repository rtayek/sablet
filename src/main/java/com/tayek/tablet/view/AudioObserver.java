package com.tayek.tablet.view;
import java.util.Observable;
import java.util.logging.Logger;
import com.tayek.io.Audio;
import com.tayek.io.Audio.Sound;
import com.tayek.tablet.Main;
import com.tayek.tablet.model.Model;
public class AudioObserver implements View {
    public AudioObserver(Model model) {
        this.model=model;
    }
    @Override public void update(Observable model,Object hint) {
        logger.info("hint: "+hint);
        if(model instanceof Model) if(model.equals(this.model))
            if(hint instanceof Sound) Main.audio.play((Sound)hint);
        else logger.warning("not our hint: "+hint);
        else logger.warning("not our model!");
        else logger.warning("not a model!");
    }
    private final Model model;
    public final Logger logger=Logger.getLogger(getClass().getName());
}
