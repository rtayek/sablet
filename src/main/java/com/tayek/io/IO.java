package com.tayek.io;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import com.tayek.*;
import com.tayek.tablet.Main;
public class IO {
    public static class Client<T> implements Sender<T> {
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
        final ExecutorService pool=Executors.newFixedThreadPool(10);
        public Future<Boolean> sendFuture(final T t,final int id) {
            return pool.submit(new Callable<Boolean>() {
                @Override public Boolean call() throws Exception {
                    return send(t,id);
                }
            });
        }
        public boolean sendOnThread(T t,int id) throws InterruptedException,ExecutionException {
            Future<Boolean> future=sendFuture(t,id); // not used yet
            while(!future.isDone())
                ;
            boolean ok=future.get();
            return ok;
        }
        private boolean sendTo(T t,int id) {
            Socket socket=connect(id,socketAddress,timeout);
            if(socket!=null) {
                try {
                    Writer out=new OutputStreamWriter(socket.getOutputStream());
                    out.write(t.toString()+"\n");
                    out.flush();
                    // out.close();
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                    logger.fine("sent: "+t+" at: "+System.currentTimeMillis());
                    Main.toaster.toast("sent: "+t+" at: "+System.currentTimeMillis());
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
        public boolean send(final T t,final int id) {
            final Holder holder=new Holder();
            Thread thread=new Thread(new Runnable() {
                @Override public void run() {
                    holder.ok=sendTo(t,id);
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
    public static class Server<T extends From<T>>extends Thread {
        public Server(InetAddress inetAddress,int service,Receiver<T> receiver,T t) throws IOException {
            this(new InetSocketAddress(inetAddress,service),receiver,t);
        }
        public Server(SocketAddress socketAddress,Receiver<T> receiver,T t) throws IOException {
            // super("server:"+socketAddress);
            serverSocket=new ServerSocket();
            logger.info("binding to: "+socketAddress);
            serverSocket.bind(socketAddress);
            this.receiver=receiver;
            this.t=t;
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
                        T t=this.t.from(string);
                        logger.fine("received: "+t+" at: "+System.currentTimeMillis());
                        Main.toaster.toast("received: "+t+" at: "+System.currentTimeMillis());
                        if(receiver!=null) receiver.receive(t);
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
        final ServerSocket serverSocket;
        final Receiver<T> receiver;
        final T t; // android can't use java 8 yet.
        boolean isShuttingDown;
        private Integer received=0;
        static Integer service0=10_000;
        public final Logger logger=Logger.getLogger(getClass().getName());
    }
    public static void printThreads() {
        int big=2*Thread.activeCount();
        Thread[] threads=new Thread[big];
        Thread.enumerate(threads);
        for(Thread thread:threads)
            if(thread!=null) System.out.println(thread);
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
