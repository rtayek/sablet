package com.tayek.tablet.io.gui.swing;
import com.tayek.io.gui.*;
import com.tayek.tablet.*;
import com.tayek.tablet.Tablet.MenuItem;
import com.tayek.tablet.io.gui.common.*;
import com.tayek.tablet.model.*;
import com.tayek.tablet.view.*;
import static com.tayek.tablet.io.IO.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.Timer;
import java.util.logging.*;
import javax.swing.*;
public class Gui extends MainGui implements View,ActionListener {
    private Gui(Tablet tablet) {
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
        p("initialize");
        textView=TextView.addTextView("Tablet "+tablet.tabletId());
        if(textView!=null) textView.frame.setVisible(false);

    }
    @Override public String title() {
        return "Buttons";
    }
    @Override public void addContent() {
        p("add content.");

        JMenuBar jMenuBar=createMenuBar();
        frame.setJMenuBar(jMenuBar);
        JPanel top=new JPanel();
        JLabel topLabel=new JLabel(tablet.name());
        Font current=topLabel.getFont();
        p(topLabel.getFont().toString());
        JPanel middle=new JPanel();
        middle.setLayout(new BoxLayout(middle,BoxLayout.Y_AXIS));
        for(Integer id=1;id<=tablet.group.model.buttons;id++) {
            // looks like button needs a name()!
            // String name=tablet.group.info()
            String name=tablet.group.buttonName(tablet.tabletId(),id);
            JToggleButton button=new JCheckBox(GuiAdapterABC.pad(name));
            current=button.getFont();
            Font large=new Font(current.getName(),current.getStyle(),4*current.getSize()/2);
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
            Message message=new Message(Message.Type.normal,tablet.group.groupId,tablet.tabletId(),id,tablet.group.model.toCharacters());
            tablet.send(message,0);
        }
    };
    @Override public void update(Observable o,Object hint) {
        if(o instanceof Model&&o.equals(tablet.group.model)) adapter.update(o,hint);
        else logger.warning("not a model or not our model!");
    }
    @Override public void actionPerformed(ActionEvent e) {
        logger.info("action performed: "+e);
        MenuItem x=MenuItem.valueOf(e.getActionCommand());
        if(x!=null) {
            if(x.equals(MenuItem.Log)) { // no text view on android
                if(textView!=null) {
                    p(tablet+" toggling: "+textView.serialNumber);
                    textView.frame.setVisible(!textView.frame.isVisible());
                } else logger.info("no log window to toggle!");
            } else x.doItem(tablet);
        } else if(e.getActionCommand().equals("Open ...")) {
            logger.info("not implemented: "+e.getActionCommand());
        } else if(e.getActionCommand().equals("Save ...")) {
            logger.info("not implemented: "+e.getActionCommand());
        } else if(e.getActionCommand().equals("New Game")) {
            logger.info("not implemented: "+e.getActionCommand());
        } else if(e.getActionCommand().equals("About")) JOptionPane.showMessageDialog(null,"Tablet (alpha)");
        else {
            logger.info("action not handled: "+e.getActionCommand());
        }
    }
    public JMenuBar createMenuBar() {
        JMenuBar menuBar=new JMenuBar();
        JMenu menu=new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File menu");
        menuBar.add(menu);
        JMenuItem menuItem=new JMenuItem("Open ...",KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Open file dialog");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem=new JMenuItem("Save ...",KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Save file dialog");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu=new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.getAccessibleContext().setAccessibleDescription("Edit menu");
        menuBar.add(menu);
        menuItem=new JMenuItem("Configure",KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Configure");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem=new JMenuItem("Buttons",KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Buttons");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu=new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription("View menu");
        menuBar.add(menu);
        menu.addSeparator();
        menuItem=new JMenuItem("Colors",KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Colors");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu=new JMenu("Options");
        menu.setMnemonic(KeyEvent.VK_O);
        menu.getAccessibleContext().setAccessibleDescription("Options menu");
        // Reset,Ping,Disconnect,Connect,Log;
        if(true) for(MenuItem x:MenuItem.values()) {
            menuItem=new JMenuItem(x.name());
            int vk=(KeyEvent.VK_A-1)+(x.name().toUpperCase().charAt(0)-'A');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(vk,ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription(x.name());
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }
        else {
            menuItem=new JMenuItem("Reset");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription("Reset");
            menuItem.addActionListener(this);
            menu.add(menuItem);
            menuItem=new JMenuItem("Ping"); // better make these enums rsn!
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription("Ping");
            menuItem.addActionListener(this);
            menu.add(menuItem);
            menuItem=new JMenuItem("Disconnect");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription("Disconnect");
            menuItem.addActionListener(this);
            menu.add(menuItem);
            menuItem=new JMenuItem("Connect");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription("Cconnect");
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }
        if(false) {
            menuItem=new JMenuItem("Log",KeyEvent.VK_C);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.ALT_MASK));
            menuItem.getAccessibleContext().setAccessibleDescription("Log");
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }
        menuBar.add(menu);
        menu=new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription("Help menu");
        menuItem=new JMenuItem("About",KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("About");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        return menuBar;
    }
    public static Gui create(Tablet tablet) { // subclass instead
        final Gui gui=new Gui(tablet);
        GuiAdapterABC adapter=new GuiAdapterABC(tablet) {
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
        return gui;
    }
    public static Tablet runGui(Map<Integer,Group.Info> map,int tabletId) {
        final Group group=new Group(1,map);
        final Tablet tablet=new Tablet(group,tabletId);
        tablet.startListening();
        Gui gui=create(tablet);
        tablet.group.model.addObserver(gui);
        tablet.group.model.addObserver(new AudioObserver(tablet.group.model));
        gui.run();
        return tablet;
    }
    public static void main(String[] args) throws Exception {
        Main.log.setLevel(Level.SEVERE);
        Map<Integer,Group.Info> map=new TreeMap<>();
        map=Group.g2;
        Map<Integer,Tablet> tablets=new LinkedHashMap<>();
        for(int tabletId:map.keySet())
            tablets.put(tabletId,Gui.runGui(map,tabletId));
        Thread.sleep(100);
        if(false) for(Tablet tablet:tablets.values())
            Tablet.startSimulating(tablet);
    }
    private static final long serialVersionUID=1L;
    Tablet tablet;
    /*final*/ GuiAdapterABC adapter;
    TextView textView;
    final Map<Integer,Color> idToColor=defaultIdToColor;
    Map<Integer,JToggleButton> idToButton=new LinkedHashMap<>();
    public final Logger logger=Logger.getLogger(getClass().getName());
    public static final Map<Integer,Color> defaultIdToColor;
    static { // this belongs in group! - maybe not, it's gui stuff? maybe tablet
        Map<Integer,Color> temp=new LinkedHashMap<>();
        temp.put(1,Color.red);
        temp.put(2,Color.orange);
        temp.put(3,Color.yellow);
        temp.put(4,Color.green);
        temp.put(5,Color.blue);
        temp.put(6,Color.magenta);
        temp.put(7,Color.cyan);
        defaultIdToColor=Collections.unmodifiableMap(temp);
    }
}
