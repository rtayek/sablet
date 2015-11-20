package com.tayek.io.gui;
import static com.tayek.tablet.io.IO.p;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class X extends MainGui {
    static class Arrangement {
        public Arrangement(int rows,int columns,boolean isRows) {
            this.isRows=isRows;
            this.rows=isRows?rows:columns;
            this.columns=!isRows?rows:columns;
            int n=rows*columns;
            states=new Boolean[n];
            for(int i=0;i<states.length;i++)
                states[i]=new Boolean(false);
            hues=new Float[isRows?columns:rows];
            for(int i=0;i<hues.length;i++)
                hues[i]=(float)(i*1./(isRows?columns:rows));
            buttons=new JButton[n];
        }
        int index(int i,int j) {
            return columns*i+j;
        }
        int row(int index) {
            return index/columns;
        }
        int column(int index) {
            return index%columns;
        }
        void resizeWidgets(Dimension d) {
            p("sizeing to: "+d);
            p("isRows: "+isRows);
            dx=isRows?(d.width/(columns+1)):(d.width/columns);
            dy=!isRows?(d.height/(rows+1)):(d.height/rows);
            x0=0; // (d.width-dx*(columns+1))/2;
            y0=0; // (d.height-dy*rows)/2;
            int fontsize=dx/7;
            for(int i=0;i<rows;i++)
                for(int j=0;j<columns;j++) {
                    int x=x0+(isRows?(j==0?0:dx):0)+dx*j;
                    int y=y0+(!isRows?(i==0?0:dy):0)+dy*i;
                    int w=(isRows&&j==0?dx:0)+dx;
                    int h=(!isRows&&i==0?dy:0)+dy;
                    p(i+" "+j+"  "+x+" "+y+"  "+w+" "+h);
                    buttons[index(i,j)].setBounds(x,y,w,h);
                    Font font=buttons[index(i,j)].getFont();
                    Font font2=new Font(font.getName(),font.getStyle(),fontsize);
                    buttons[index(i,j)].setFont(font2);
                }
        }
        int extraX(Dimension d) { // broken
            return(d.width-((columns+1)*dx+2*x0));
        }
        int extraY(Dimension d) { // broken
            return(d.height-(rows*dy+2*y0));
        }
        void addContent(Container container) {
            for(int i=0;i<rows;i++)
                for(int j=0;j<columns;j++) {
                    String string=isRows?(j==0?("Room: "+(i+1)):("Action: "+j)):(i==0?("Room: "+j):("Action: "+i));
                    JButton button=new JButton(string);
                    button.setName(""+index(i,j));
                    button.addActionListener(actionListener);
                    Color color=null;
                    double hue=isRows?((j-1)*1./(columns-1)):((i-1)*1./(rows-1));
                    color=isRows&&j==0||!isRows&&i==0?button.getBackground():Color.getHSBColor((float)hue,intensity,saturation);
                    button.setBackground(color);
                    container.add(button);
                    buttons[index(i,j)]=button;
                }
        }
        void printColors() {
            for(float hue:hues) {
                Color on=Color.getHSBColor(hue,1,1);
                Color off=Color.getHSBColor(hue,.8f,.4f);
                p(on+" "+off);
            }
        }
        final ActionListener actionListener=new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() instanceof JButton) {
                    JButton b=(JButton)e.getSource();
                    String name=b.getName();
                    Integer i=Integer.valueOf(name);
                    states[i]=!states[i];
                    int column=column(i);
                    int row=row(i);
                    Color color=null;
                    double hue=isRows?((column-1)*1./(columns-1)):((row-1)*1./(rows-1));
                    // need backgroud for white button here
                    color=states[i]?Color.getHSBColor((float)hue,1,1):Color.getHSBColor((float)hue,intensity,saturation);
                    p("set color of "+i+" to "+color);
                    b.setBackground(color);
                    p(b.getName());
                }
            }
        };
        boolean isRows=false;
        Integer rows,columns,x0,y0,dx,dy;
        Boolean[] states;
        JButton[] buttons;
        float intensity=.99f,saturation=.7f;
        Float[] hues;
    }
    X() {
        super(null);
    }
    @Override public void initialize() {
        arrangement=new Arrangement(10,2,false);
    }
    @Override public String title() {
        return "Experiment";
    }
    @Override public void addContent() {
        JMenuBar menuBar=new JMenuBar();
        JMenu menu=new JMenu("Options");
        menu.setMnemonic(KeyEvent.VK_O);
        menu.getAccessibleContext().setAccessibleDescription("Options menu");
        menuBar.add(menu);
        JMenuItem menuItem=new JMenuItem("Orientation");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Toggle orientation.");
        menuItem.setName("Toggle orientation.");
        menuItem.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                System.out.println(e.getSource());
                String name=((JMenuItem)e.getSource()).getName();
                System.out.println(name);
                p("work here!");
                // kill the center panel and rebuild it
                Dimension d=null;
                if(panel!=null) {
                    d=panel.getSize();
                    remove(panel);
                    panel=null;
                }
                arrangement=new Arrangement(arrangement.rows,arrangement.columns,!arrangement.isRows);
                panel=new JPanel();
                panel.setLayout(null);
                arrangement.addContent(panel);
                p("size: "+getSize());
                Dimension d1=new Dimension(600,300);
                // get size from ???
                arrangement.resizeWidgets(d1);
                setPreferredSize(d1);
                add(panel,Where.center.k);
                arrangement.resizeWidgets(d);
                // frame.pack();
                panel.revalidate();
                panel.repaint();
            }
        });
        menu.add(menuItem);
        frame.setJMenuBar(menuBar);
        setLayout(new BorderLayout());
        for(Where where:Where.values())
            if(where.equals(Where.center)) {
                panel=new JPanel();
                panel.setLayout(null);
                arrangement.addContent(panel);
                p("size: "+getSize());
                Dimension d=new Dimension(600,300);
                arrangement.resizeWidgets(d);
                setPreferredSize(d);
                add(panel,where.k);
            } else add(new JPanel().add(new JButton(where.toString())),where.k);
        frame.getContentPane().addHierarchyBoundsListener(hierarchyBoundsListener);
    }
    public static void main(String[] args) {
        new X().run();
    }
    Arrangement arrangement;
    HierarchyBoundsListener hierarchyBoundsListener=new HierarchyBoundsListener() {
        @Override public void ancestorMoved(HierarchyEvent e) {
            p(e.toString());
        }
        @Override public void ancestorResized(HierarchyEvent e) {
            if(e.getID()==HierarchyEvent.ANCESTOR_RESIZED) {
                frame.removeHierarchyBoundsListener(hierarchyBoundsListener);
                p("---");
                p("this: "+getSize());
                p("panel: "+panel.getSize());
                Dimension d=frame.getContentPane().getSize();
                p("pane: "+d);
                d=panel.getSize();
                p("frame: "+frame+" "+d);
                arrangement.resizeWidgets(d);
                int extraX=(d.width-((arrangement.columns+1)*arrangement.dx+2*arrangement.x0));
                p("extra: "+extraX);
                int extraY=(d.height-(arrangement.rows*arrangement.dy+2*arrangement.y0));
                p("extra: "+extraY);
                d=frame.getSize();
                p("after: frame: "+frame.getSize());
                // frame.setSize(d.width-extraX,d.height-extraY);
                p("after2: frame: "+frame.getSize());
                frame.addHierarchyBoundsListener(hierarchyBoundsListener);
                // setPreferredSize(d);
                // frame.pack(); // causes infinite loop!
            }
        }
    };
    JPanel panel;
    private static final long serialVersionUID=1L;
}
