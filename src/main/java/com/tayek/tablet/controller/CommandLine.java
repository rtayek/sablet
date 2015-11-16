package com.tayek.tablet.controller;
import java.io.*;
import java.net.*;
import java.util.*;
import com.tayek.tablet.*;
import com.tayek.tablet.io.gui.swing.*;
import com.tayek.tablet.model.*;
import com.tayek.tablet.model.Model.Observer;
import com.tayek.tablet.view.View;
public class CommandLine {
    CommandLine(Group group,int tabletId) {
        tablet=new Tablet<>(group,tabletId);
    }
    private static void usage() {
        System.out.println("usage:");
        System.out.println("a add/remove audio observer");
        System.out.println("b <buttonId> <boolean> - set button state");
        System.out.println("c - add/remove a command line view");
        System.out.println("g - add/remove a gui");
        System.out.println("p - print view");
        System.out.println("q - quit");
        System.out.println("r - reset");
        System.out.println("s - start client");
        System.out.println("t - stop client");
    }
    private String[] splitNext(String command,int i) {
        while(command.charAt(i)==' ')
            i++;
        String[] tokens=command.substring(i).split(" ");
        return tokens;
    }
    boolean process(String command) {
        if(command.length()==0) return true;
        String[] tokens=null;
        switch(command.charAt(0)) {
            case 'h':
                usage();
                break;
            case 'a':
                if(audioObserver==null) {
                    audioObserver=new Observer(tablet.group.model);
                    tablet.group.model.addObserver(audioObserver);
                } else {
                    tablet.group.model.deleteObserver(audioObserver);
                    audioObserver=null;
                }
                break;
            case 'b':
                if(command.charAt(1)==' ') {
                    tokens=splitNext(command,2);
                    if(tokens.length==2) try {
                        int buttonId=Integer.valueOf(tokens[0]);
                        boolean state=Boolean.valueOf(tokens[1]);
                        tablet.group.model.setState(buttonId,state);
                    } catch(Exception e) {
                        System.out.println("caught: "+e);
                        System.out.println("syntax error: "+command);
                    }
                    else System.out.println("too many tokens!");
                } else System.out.println("syntax error: "+command);
                break;
            case 'o': // send start form foreign group
                //tablet.send(Message.dummy,0);
                break;
            case 'c':
                if(commandLineView==null) {
                    commandLineView=new View.CommandLine(tablet.group.model);
                    tablet.group.model.addObserver(commandLineView);
                } else {
                    tablet.group.model.deleteObserver(commandLineView);
                    commandLineView=null;
                }
                break;
            case 'g':
                if(gui==null) {
                    gui=OldGui.gui(tablet);
                    gui.run();
                    tablet.group.model.addObserver(gui);
                } else {
                    tablet.group.model.deleteObserver(gui);
                    gui.frame.dispose();
                    gui=null;
                }
                break;
            case 'G':
                if(newGui==null) {
                    newGui=Gui.gui(tablet);
                    tablet.group.model.addObserver(newGui);
                } else {
                    tablet.group.model.deleteObserver(newGui);
                    newGui.frame.dispose();
                    newGui=null;
                }
                break;
            case 'p':
                System.out.println(tablet.group.model);
                break;
            case 'r':
                tablet.group.model.reset();
                break;
            case 's':
                boolean ok=tablet.startListening();
                if(!ok) System.out.println("badness");
                break;
            case 't':
                tablet.stopListening();
                break;
            case 'q':
                return false;
            default:
                System.out.println("unimplemented: "+command.charAt(0));
                usage();
                break;
        }
        return true;
    }
    void run() {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        String string=null;
        usage();
        prompt();
        try {
            while((string=bufferedReader.readLine())!=null) {
                if(!process(string)) {
                    System.out.println("quitting.");
                    return;
                }
                prompt();
            }
        } catch(IOException e) {
            System.out.println("caught: "+e);
            System.out.println("quitting.");
            return;
        }
        System.out.println("end of file.");
    }
    static void prompt() {
        System.out.print(lineSeparator+">");
        System.out.flush();
    }
    public static void main(String[] arguments) throws UnknownHostException {
        Main.log.init();
        InetAddress[] x=InetAddress.getAllByName("rays8350");
        System.out.println(Arrays.asList(x));
        String host=InetAddress.getLocalHost().getHostName();
        Map<Integer,String> map=new TreeMap<>();
        map.put(1,host);
        System.out.println(map);
        Group group=new Group(1,map);
        new CommandLine(group,1).run();
    }
    final Tablet<Message> tablet;
    View.CommandLine commandLineView;
    Observer audioObserver;
    OldGui gui;
    Gui newGui;
    public static final String lineSeparator=System.getProperty("line.separator");
}
