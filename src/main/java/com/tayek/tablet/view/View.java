package com.tayek.tablet.view;
import java.util.*;
import java.util.logging.Logger;
import com.tayek.tablet.*;
import static com.tayek.io.IO.*;
import com.tayek.tablet.model.Model;
public interface View extends Observer {
    public class CommandLine implements View {
        public CommandLine(Model model) {
            this.model=model;
        }
        @Override public void update(Observable observable,Object hint) {
            p("update: "+observable+" "+hint);
            if(observable instanceof Model) if(observable==model) {
                logger.fine(id+" received update: "+observable+" "+hint);
                if(hint instanceof Group) {
                    logger.fine("in update: ");
                    ((Group)hint).print(-1);
                }
                logger.fine("model: "+model);
            } else logger.warning(this+" "+id+" not our model!");
            else logger.warning(this+" "+id+" not a model!");
        }
        private final int id=++n;
        private final Model model;
        public final Logger logger=Logger.getLogger(getClass().getName());
        private static int n=0;
    }
}
