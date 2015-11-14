package com.tayek.tablet;
import java.io.*;
import java.net.*;
import java.util.logging.Logger;
import com.tayek.tablet.model.*;
import com.tayek.tablet.model.Message.Receiver;
//http://codeoncloud.blogspot.com/2014/06/android-tcpip-client-server-socket.html
class Server extends Thread {
    Server(InetAddress inetAddress,int service,Receiver<Message> receiver) throws IOException {
        this(new InetSocketAddress(inetAddress,service),receiver);
    }
    Server(SocketAddress socketAddress,Receiver<Message> receiver) throws IOException {
        //super("server:"+socketAddress);
        serverSocket=new ServerSocket();
        logger.info("binding to: "+socketAddress);
        serverSocket.bind(socketAddress);
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
                    Message message=Message.from(string);
                    logger.fine("received: "+message+" at: "+System.currentTimeMillis());
                    Main.toaster.toast("received: "+message+" at: "+System.currentTimeMillis());
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
    static Integer port(int tabletId) {
        return service0+tabletId;
    }
    public Integer received() {
        synchronized(received) {
            return received;
        }
    }
    final ServerSocket serverSocket;
    final Receiver<Message> receiver;
    boolean isShuttingDown;
    private Integer received=0;
    static Integer service0=10_000;
    public final Logger logger=Logger.getLogger(getClass().getName());
}
