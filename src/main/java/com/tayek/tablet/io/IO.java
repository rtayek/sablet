package com.tayek.tablet.io;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import com.tayek.*;
import com.tayek.tablet.*;
import com.tayek.tablet.model.Message;
public class IO {
    public static class Client {
        public Client(InetAddress inetAddress,int service) throws UnknownHostException {
            this(new InetSocketAddress(inetAddress,service),defaultTimeout);
        }
        public Client(SocketAddress socketAddress) {
            this(socketAddress,defaultTimeout);
        }
        public Client(InetAddress inetAddress,int service,int timeout) throws UnknownHostException {
            this(new InetSocketAddress(inetAddress,service),timeout);
        }
        public Client(SocketAddress socketAddress,int timeout) {
            this.socketAddress=socketAddress;
            this.timeout=timeout;
        }
        private boolean send_(Message message,int id) {
            Socket socket=connect(id,socketAddress,timeout);
            if(socket!=null) {
                try {
                    Writer out=new OutputStreamWriter(socket.getOutputStream());
                    out.write(message.toString()+"\n");
                    out.flush();
                    // out.close();
                    socket.shutdownInput(); // can this be done earlier?
                    socket.shutdownOutput();
                    socket.close();
                    logger.fine("sent: "+message+" at: "+System.currentTimeMillis());
                    Main.toaster.toast("sent: "+message+" at: "+System.currentTimeMillis());
                    return true;
                } catch(IOException e) {
                    e.printStackTrace();
                }
            } else logger.warning("tablet "+id+", send to: "+socketAddress+" failed due to no socket!");
            return false;
        }
        static class Holder {
            Boolean ok=null;
        }
        public boolean send(final Message message,final int id) { // future or
                                                                  // async?
            final Holder holder=new Holder();
            Thread thread=new Thread(new Runnable() {
                @Override public void run() {
                    holder.ok=send_(message,id);
                }
            },"send thread");
            thread.start();
            while(holder.ok==null)
                Thread.yield();
            if(true) try {
                thread.join(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            return holder.ok;
        }
        public Socket connect(int tabletId,int timeout) {
            return connect(tabletId,socketAddress,timeout);
        }
        private static Socket connect(int tabletId,SocketAddress socketAddress,int timeout) {
            Socket socket=new Socket();
            try {
                staticLogger.info("tablet "+tabletId+", connecting to: "+socketAddress+" "+timeout);
                socket.connect(socketAddress,timeout);
                return socket;
            } catch(IOException e) {
                staticLogger.warning("tablet "+tabletId+", connecting to: "+socketAddress+", caught: "+e);
            }
            return null;
        }
        final SocketAddress socketAddress;
        final int timeout;
        static Integer service0=10_000;
        static Integer defaultTimeout=200;
        public final Logger logger=Logger.getLogger(getClass().getName());
        public static final Logger staticLogger=Logger.getLogger(Client.class.getName());
    }
    // maybe make server just pass the string along
    // then what do i do in server to pass info along to group
    // looks like server should have a tablet, then it all works
    //
    public static class Server extends Thread {
        public Server(Tablet tablet,InetAddress inetAddress,int service,Receiver receiver) throws IOException {
            this(tablet,new InetSocketAddress(inetAddress,service),receiver);
        }
        public Server(Tablet tablet,SocketAddress socketAddress,Receiver receiver) throws IOException {
            serverSocket=new ServerSocket();
            logger.info("binding to: "+socketAddress);
            serverSocket.bind(socketAddress);
            this.tablet=tablet;
            this.receiver=receiver;
        }
        @Override public void run() {
            logger.info("starting server");
            while(true)
                try {
                    logger.info("server is accepting on: "+serverSocket);
                    Socket socket=serverSocket.accept();
                    logger.info("server accepted connection from: "+socket.getRemoteSocketAddress());
                    Main.toaster.toast("server accepted connection from: "+socket.getRemoteSocketAddress());
                    BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String string=in.readLine();
                    if(string!=null&&!string.isEmpty()) {
                        synchronized(received) {
                            received++;
                        }
                        Message message=Message.staticFrom(string);
                        logger.fine("received: "+message+" at: "+System.currentTimeMillis());
                        Main.toaster.toast("received: "+message+" at: "+System.currentTimeMillis());
                        if(tablet!=null) tablet.group.checkForNewInetAddress(message.tabletId,socket.getInetAddress());
                        if(receiver!=null) receiver.receive(message);
                    }
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } catch(IOException e) {
                    if(!isShuttingDown) throw new RuntimeException(e);
                    break;
                }
            try {
                serverSocket.close();
            } catch(IOException e) {
                if(!isShuttingDown) throw new RuntimeException(e);
            }
        }
        public void stopServer() {
            logger.info("stopping server");
            isShuttingDown=true;
            try {
                serverSocket.close();
            } catch(IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public static Integer port(int tabletId) {
            return service0+tabletId;
        }
        public Integer received() {
            synchronized(received) {
                return received;
            }
        }
        final Tablet tablet;
        final ServerSocket serverSocket;
        final Receiver receiver;
        boolean isShuttingDown;
        private Integer received=0;
        static Integer service0=10_000;
        public final Logger logger=Logger.getLogger(getClass().getName());
    }
    public static void pn(String string) {
        System.out.print(string);
        System.out.flush();
    }
    public static void p(String string) {
        pn(string);
        pn(System.getProperty("line.separator"));
    }
    static void printNetworkInterface(NetworkInterface netint) {
        p("Display name: "+netint.getDisplayName()+", Name: "+netint.getName());
        Enumeration<InetAddress> inetAddresses=netint.getInetAddresses();
        for(InetAddress inetAddress:Collections.list(inetAddresses))
            p("\tInetAddress: "+inetAddress+" "+inetAddress.isSiteLocalAddress());
    }
    public static void printThreads() {
        int big=2*Thread.activeCount();
        Thread[] threads=new Thread[big];
        Thread.enumerate(threads);
        for(Thread thread:threads)
            if(thread!=null) p(thread.toString());
    }
    public static void printNetworkInterfaces() {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces=NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface networkInterface:Collections.list(networkInterfaces))
                printNetworkInterface(networkInterface);
        } catch(SocketException e) {
            p("caught: "+e);
        }
    }
    public static void main(String args[]) {
        printNetworkInterfaces();
    }
}
