package com.tayek.io.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import static com.tayek.io.IO.*;
import static java.lang.Math.*;
// shapes, colors, styles, arrangement
enum Where {
    top(BorderLayout.PAGE_START),bottom(BorderLayout.PAGE_END),right(BorderLayout.LINE_END),left(BorderLayout.LINE_START),center(BorderLayout.CENTER);
    Where(String k) {
        this.k=k;
    }
    final String k;
    Color color;
    static void init() {
        for(Where where:values())
            where.color=Color.getHSBColor((float)(where.ordinal()*1./values().length),.9f,.9f);
    }
    static Boolean[] from(Integer x) {
        x%=(int)round(pow(2,values().length));
        String string=Integer.toBinaryString(x);
        while(string.length()<values().length)
            string='0'+string;
        Boolean[] b=new Boolean[values().length];
        for(int i=0;i<values().length;i++)
            b[i]=string.charAt(i)=='1';
        p(Arrays.asList(b).toString());
        return b;
    }
    static EnumSet<Where> set(Boolean[] bits) {
        EnumSet<Where> set=EnumSet.noneOf(Where.class);
        for(int i=0;i<values().length;i++)
            if(bits[i]) set.add(values()[i]);
        return set;
    }
}
public class Arranger extends MainGui {
    Arranger(MyJApplet applet) {
        super(applet);
    }
    @Override public void initialize() {
        // TODO Auto-generated method stub
    }
    @Override public String title() {
        return "Arrangements";
    }
    @Override public void addContent() {
        JPanel grid=new JPanel();
        int n=(int)ceil(sqrt(pow(2,Where.values().length)));
        p("n="+n);
        GridLayout experimentLayout=new GridLayout(n,n);
        grid.setLayout(experimentLayout);
        for(int i=0;i<(int)pow(2,Where.values().length);i++) {
            JPanel panel=makeArrangement(Where.set(Where.from(i)));
            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            grid.add(panel,i);
            // panel.setPreferredSize(new Dimension(800,600));
        }
        add(grid);
    }
    JPanel makeArrangement(Set<Where> set) {
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());
        for(Where where:set) {
            JButton button=new JButton();
            button.setBackground(where.color);
            panel.add(button,where.k);
        }
        return panel;
    }
    void run_() {
        p(((Double)pow(2,Where.values().length)).toString());
        for(int i=0;i<(int)pow(2,Where.values().length);i++) {
            EnumSet<Where> set=Where.set(Where.from(i));
            p(set.toString());
            makeArrangement(set);
        }
    }
    public void run() {
        setLayout(new BorderLayout());
        initialize();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(isApplet()) addContent();
                else {
                    frame.setTitle(title());
                    frame.getContentPane().add(Arranger.this,BorderLayout.CENTER);
                    addContent();
                    frame.pack();
                    frame.setVisible(true);
                }
            }
        });

    }
    public static void main(String[] args) {
        Where.init();
        Arranger arranger=new Arranger(null);
        arranger.run_();
        arranger.run();
    }
    private static final long serialVersionUID=1L;
}
