package com.tayek.tablet.io.gui.swing;
import static com.tayek.tablet.io.IO.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.tayek.tablet.*;
import com.tayek.tablet.Tablet.MenuItem;
import com.tayek.tablet.io.gui.common.*;
import com.tayek.tablet.model.*;
import com.tayek.tablet.view.*;
import com.tayek.utilities.*;
import com.tayek.io.gui.*;

// put explicit view back in
// along with command line view and controller
// make this use the main gui class
public class OldGui implements View,ActionListener {
    public OldGui(Tablet tablet) {
        this.tablet=tablet;
        this.model=tablet.group.model;
        String prefix="tablet "+tablet.tabletId();
        if(true) {
            textView=TextView.addTextView(prefix);
        } else {
            Tee tee=Tee.tee(new File("out.txt"));
            textView=TextView.createAndShowGui(prefix);
            tee.addOutputStream(textView.taOutputStream);
        }
        if(textView!=null) textView.frame.setVisible(false);
    }
    public void run() {
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                @Override public void run() {
                    createAndShowGUI();
                }
            });
        } catch(InvocationTargetException|InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override public void actionPerformed(ActionEvent e) {
        logger.info("action performed: "+e);
        MenuItem x=MenuItem.valueOf(e.getActionCommand());
        if(x!=null) {
            if(x.equals(MenuItem.Log)) textView.setVisible(!textView.isVisible());
            else x.doItem(tablet);
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
    void build(ChangeListener changeListener,ActionListener actionListener) {
        JPanel middle=new JPanel();
        middle.setLayout(new BoxLayout(middle,BoxLayout.X_AXIS));
        middle.setPreferredSize(new Dimension(600,50));
        for(Integer id:idToColor.keySet()) {
            JToggleButton button=new JCheckBox(""+id);
            Font current=button.getFont();
            Font large=new Font(current.getName(),current.getStyle(),3*current.getSize()/2);
            button.setFont(large);
            button.setName(""+id);
            idToButton.put(id,button);
            button.setBackground(idToColor.get(id));
            // button.setPreferredSize(new Dimension(100,25));
            button.addChangeListener(changeListener);
            button.addActionListener(actionListener);
            middle.add(button);
        }
        JPanel screen=new JPanel();
        screen.setLayout(new BoxLayout(screen,BoxLayout.Y_AXIS));
        JPanel top=new JPanel();
        JLabel topLabel=new JLabel("top");
        Font current=topLabel.getFont();
        p(topLabel.getFont().toString());
        Font small=new Font(current.getName(),current.getStyle(),2*current.getSize()/3);
        topLabel.setFont(small);
        top.add(topLabel);
        JPanel bottom=new JPanel();
        JLabel bottomLabel=new JLabel("bottom");
        bottomLabel.setFont(small);
        bottom.add(bottomLabel);
        screen.add(top);
        screen.add(middle);
        screen.add(bottom);
        frame.getContentPane().add(screen,BorderLayout.CENTER);
    }
    void build2(ChangeListener changeListener,ActionListener actionListener) {
        JPanel top=new JPanel();
        JLabel topLabel=new JLabel("top");
        Font current=topLabel.getFont();
        p(topLabel.getFont().toString());
        JPanel middle=new JPanel();
        middle.setLayout(new BoxLayout(middle,BoxLayout.Y_AXIS));
        for(Integer id=1;id<=model.buttons;id++) {
            JToggleButton button=new JCheckBox(GuiAdapterABC.pad("Room "+id));
            current=button.getFont();
            Font large=new Font(current.getName(),current.getStyle(),3*current.getSize()/2);
            button.setFont(large);
            button.setName(""+id);
            idToButton.put(id,button);
            button.addChangeListener(changeListener);
            button.addActionListener(actionListener);
            middle.add(button);
            button.setBackground(idToColor.get(id));
            middle.add(new JLabel());
        }
        Font small=new Font(current.getName(),current.getStyle(),2*current.getSize()/3);
        topLabel.setFont(small);
        top.add(topLabel);
        JPanel bottom=new JPanel();
        JLabel bottomLabel=new JLabel("bottom");
        bottomLabel.setFont(small);
        bottom.add(bottomLabel);
        frame.getContentPane().add(top,BorderLayout.PAGE_START);
        frame.getContentPane().add(middle,BorderLayout.CENTER);
        frame.getContentPane().add(bottom,BorderLayout.PAGE_END);
    }
    void createAndShowGUI() {
        frame=new JFrame("Tablet "+tablet.tabletId());
        frame.setUndecorated(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addComponentListener(new ComponentAdapter() {
            @Override public void componentMoved(ComponentEvent ce) {
                // Component c=ce.getComponent();
                // logger.info("frame moved to "+c.getLocation());
            }
        });
        JMenuBar jMenuBar=createMenuBar();
        frame.setJMenuBar(jMenuBar);
        ChangeListener changeListener=new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                logger.finer(
                        "model "+tablet.tabletId()+", button "+((JToggleButton)e.getSource()).getName()+" is "+((JToggleButton)e.getSource()).isSelected());
            }
        };
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                logger.fine("model "+tablet.tabletId()+", button "+((JToggleButton)e.getSource()).getName()+" is "+((JToggleButton)e.getSource()).isSelected());
                int id=new Integer(((JToggleButton)e.getSource()).getName());
                boolean state=((JToggleButton)e.getSource()).isSelected();
                p("calling set state");
                model.setState(id,state);
                Message message=new Message(Message.Type.normal,tablet.group.groupId,tablet.tabletId(),id,tablet.group.model.toCharacters());
                tablet.send(message,0);
            }
        };
        build2(changeListener,actionListener);
        frame.pack();
        frame.setVisible(true);
    }
    @Override public void update(Observable o,Object hint) {
        logger.fine("model "+tablet.tabletId()+", hint: "+hint);
        if(!(o instanceof Model&&o.equals(model))) throw new RuntimeException("oops");
        adapter.update(o,hint);
    }
    static int length=20;
    public static String pad2(String string) {
        for(;string.length()<length;string+=' ')
            ;
        return string;
    }
    public static Integer toInteger(String argument) {
        Integer n=null;
        try {
            n=Integer.valueOf(argument);
        } catch(NumberFormatException e) {
            p(argument+" is not a valid tabletId");
        }
        return n;
    }
    public static OldGui gui(Tablet tablet) {
        final OldGui gui=new OldGui(tablet);
        GuiAdapterABC adapter=new GuiAdapterABC(tablet) {
            @Override public void setButtonText(int id,String string) {
                gui.idToButton.get(id).setText(string);
            }
            @Override public void setButtonState(int id,boolean state) {
                gui.idToButton.get(id).setSelected(state);
            }
        };
        gui.adapter=adapter;
        return gui;
    }
    public static void main(String[] arguments) throws IOException,InterruptedException {
        Main.log.setLevel(Level.WARNING);
        Map<Integer,Group.Info> map=new TreeMap<>();
        String host=InetAddress.getLocalHost().getHostName();
        for(int tabletId=1;tabletId<=2;tabletId++) {
            map.put(tabletId,new Group.Info(host,"Tablet 1 on PC"));
            Group group=new Group(1,map);
            Tablet tablet=new Tablet(group,tabletId);
            tablet.startListening();
            final OldGui gui=gui(tablet);
            tablet.group.model.addObserver(gui);
            tablet.group.model.addObserver(new AudioObserver(tablet.group.model));
            gui.run();
        }
    }
    final Model model;
    public Tablet tablet;
    public final TextView textView;
    /*final*/ GuiAdapterABC adapter;
    final Map<Integer,Color> idToColor=Gui.defaultIdToColor;
    final Map<Integer,JToggleButton> idToButton=new LinkedHashMap<>();
    @SuppressWarnings("serial") public JFrame frame=new JFrame() {
        @Override public void dispose() {
            if(textView!=null) textView.frame.dispose();
            super.dispose();
        }
    };
    final Logger logger=Logger.getLogger(getClass().getName());
    static Integer tablets=0;
}
