package com.tayek.tablet.controller;
import static com.tayek.tablet.io.IO.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.tayek.tablet.*;
import com.tayek.tablet.Group.Info;
import com.tayek.tablet.io.gui.swing.*;
import com.tayek.tablet.model.*;
import com.tayek.tablet.view.*;
public class CommandLine {
    CommandLine(Group group,int tabletId) {
        tablet=new Tablet(group,tabletId);
    }
    private static void usage() {
        p("usage:");
        p("a add/remove audio observer");
        p("b <buttonId> <boolean> - set button state");
        p("c - add/remove a command line view");
        p("g - add/remove a gui");
        p("p - print view");
        p("q - quit");
        p("r - reset");
        p("s - start client");
        p("t - stop client");
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
                    audioObserver=new AudioObserver(tablet.group.model);
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
                        p("caught: "+e);
                        p("syntax error: "+command);
                    }
                    else p("too many tokens!");
                } else p("syntax error: "+command);
                break;
            case 'o': // send start form foreign group
                // tablet.send(Message.dummy,0);
                break;
            case 'c':
                if(commandLineView==null) {
                    commandLineView=new View.CommandLine(tablet.group.model);
                    tablet.group.model.addObserver(commandLineView);
                    p("added command line view: "+commandLineView);
                } else {
                    tablet.group.model.deleteObserver(commandLineView);
                    p("removed command line view: "+commandLineView);
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
                    newGui=Gui.create(tablet);
                    tablet.group.model.addObserver(newGui);
                } else {
                    tablet.group.model.deleteObserver(newGui);
                    newGui.frame.dispose();
                    newGui=null;
                }
                break;
            case 'p':
                p(tablet.group.model.toString());
                break;
            case 'r':
                tablet.group.model.reset();
                break;
            case 's':
                boolean ok=tablet.startListening();
                if(!ok) p("badness");
                break;
            case 't':
                tablet.stopListening();
                break;
            case 'q':
                return false;
            default:
                p("unimplemented: "+command.charAt(0));
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
                    p("quitting.");
                    return;
                }
                prompt();
            }
        } catch(IOException e) {
            p("caught: "+e);
            p("quitting.");
            return;
        }
        p("end of file.");
    }
    static void prompt() {
        System.out.print(lineSeparator+">");
        System.out.flush();
    }
    public static void main(String[] arguments) throws UnknownHostException {
        Main.log.init();
        InetAddress[] x=InetAddress.getAllByName("rays8350");
        p(Arrays.asList(x).toString());
        String host=InetAddress.getLocalHost().getHostName();
        p(Arrays.asList(arguments).toString());
        Map<Integer,Info> map=arguments.length==0?Group.g1:Group.groups.get(arguments[0]);
        if(map==null) map=Group.g1;
        // won't work!
        Group group=new Group(1,map);
        p(""+arguments.length);
        Integer id=arguments.length<2?group.tablets().iterator().next():Integer.valueOf(arguments[1]);
        System.out.println(group.tablets());
        System.out.println("id="+id);
        if(!group.tablets().contains(id))
            id=group.tablets().iterator().next();
        p("Tablet: "+id+" of group: "+group);
        p("---");
        List<InetAddress> inetAddresses=group.checkHost(host);
        p("check("+host+"): "+host+":"+inetAddresses);
        for(InetAddress inetAddress:inetAddresses)
            group.checkHost("check: "+inetAddress+":"+inetAddress.getHostAddress());
        p("---");
        inetAddresses=group.checkHost("192.168.1.2");
        p("check("+host+"): "+host+":"+inetAddresses);
        for(InetAddress inetAddress:inetAddresses)
            group.checkHost("check: "+inetAddress+":"+inetAddress.getHostAddress());
        p("---");
        new CommandLine(group,id).run();
    }
    final Tablet tablet;
    View.CommandLine commandLineView;
    Observer audioObserver;
    OldGui gui;
    Gui newGui;
    public static final String lineSeparator=System.getProperty("line.separator");
}
