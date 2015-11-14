package com.tayek.tablet.gui.swing;
import com.tayek.audio.AudioObserver;
import com.tayek.gui.TextView;
import com.tayek.tablet.*;
import com.tayek.tablet.gui.common.GuiAdapterABC;
import com.tayek.tablet.model.*;
import com.tayek.tablet.view.View;
import com.tayek.utilities.MainGui;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import javax.swing.*;
public class Gui extends MainGui implements View {
    Gui(Tablet tablet) {
        super();
        this.tablet=tablet;
    }
    @Override public JFrame frame() {
        @SuppressWarnings("serial") JFrame frame=new JFrame() {
            @Override public void dispose() { // dispose of associated text view
                if(textView!=null) textView.frame.dispose();
                super.dispose();
            }
        };
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }
    @Override public void initialize() {
        textView=TextView.addTextView(""+tablet.tabletId);
    }
    @Override public String title() {
        return "Buttons";
    }
    @Override public void addContent() {
        JPanel top=new JPanel();
        JLabel topLabel=new JLabel(tablet.name());
        Font current=topLabel.getFont();
        System.out.println(topLabel.getFont());
        JPanel middle=new JPanel();
        middle.setLayout(new BoxLayout(middle,BoxLayout.Y_AXIS));
        for(Integer id=1;id<=tablet.group.model.buttons;id++) {
            JToggleButton button=new JCheckBox(GuiAdapterABC.pad("Room "+id));
            current=button.getFont();
            Font large=new Font(current.getName(),current.getStyle(),3*current.getSize()/2);
            button.setFont(large);
            button.setName(""+id);
            idToButton.put(id,button);
            button.addActionListener(actionListener);
            middle.add(button);
            button.setBackground(idToColor.get(id));
            // middle.add(new JLabel());
        }
        Font small=new Font(current.getName(),current.getStyle(),2*current.getSize()/3);
        topLabel.setFont(small);
        top.add(topLabel);
        JPanel bottom=new JPanel();
        JLabel bottomLabel=new JLabel("bottom");
        bottomLabel.setFont(small);
        bottom.add(bottomLabel);
        add(top,BorderLayout.PAGE_START);
        add(middle,BorderLayout.CENTER);
        add(bottom,BorderLayout.PAGE_END);
        add(new JLabel("left"),BorderLayout.LINE_START);
        add(new JLabel("right"),BorderLayout.LINE_END);
    }
    ActionListener actionListener=new ActionListener() {
        @Override public void actionPerformed(ActionEvent e) {
            int id=new Integer(((JToggleButton)e.getSource()).getName());
            boolean state=((JToggleButton)e.getSource()).isSelected();
            tablet.group.model.setState(id,state);
            Message message=new Message(tablet,Message.Type.normal,id,state);
            tablet.broadcast(message);
        }
    };
    @Override public void update(Observable o,Object hint) {
        if(!(o instanceof Model&&o.equals(tablet.group.model))) throw new RuntimeException("oops");
        adapter.update(o,hint);
    }
    public static Gui gui(Tablet tablet) {
        final Gui gui=new Gui(tablet);
        // gui.initialize(objects);
        GuiAdapterABC adapter=new GuiAdapterABC(tablet.group.model) {
            @Override public void setButtonText(final int id,final String string) {
                if(SwingUtilities.isEventDispatchThread()) {
                    gui.idToButton.get(id).setText(string);
                } else SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        gui.idToButton.get(id).setText(string);
                    }
                });
            }
            @Override public void setButtonState(final int id,final boolean state) {
                if(SwingUtilities.isEventDispatchThread()) {
                    gui.idToButton.get(id).setSelected(state);
                } else SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        gui.idToButton.get(id).setSelected(state);
                    }
                });
            }
        };
        gui.adapter=adapter;
        gui.run(); // maybe delay this?
        return gui;
    }
    public static void main(String[] args) {
        Main.log.setLevel(Level.WARNING);
        Map<Integer,String> map=new TreeMap<>();
        String host;
        try {
            host=InetAddress.getLocalHost().getHostName();
        } catch(UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        for(int tabletId=1;tabletId<=2;tabletId++) {
            map.put(tabletId,host);
            Group group=new Group(1,map);
            Tablet tablet=new Tablet(group,tabletId);
            tablet.startListening();
            Gui gui=gui(tablet);
            tablet.group.model.addObserver(gui);
            tablet.group.model.addObserver(AudioObserver.instance);
            gui.run();
        }
    }
    private static final long serialVersionUID=1L;
    Tablet tablet;
    /*final*/ GuiAdapterABC adapter;
    final Map<Integer,Color> idToColor=Tablet.defaultIdToColor;
    Map<Integer,JToggleButton> idToButton=new LinkedHashMap<>();
    TextView textView;
}
