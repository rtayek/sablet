package com.tayek.tablet;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import com.tayek.tablet.model.Message;
class Client {
    Client(InetAddress inetAddress,int service) throws UnknownHostException {
        this(new InetSocketAddress(inetAddress,service),defaultTimeout);
    }
    Client(SocketAddress socketAddress) {
        this(socketAddress,defaultTimeout);
    }
    Client(InetAddress inetAddress,int service,int timeout) throws UnknownHostException {
        this(new InetSocketAddress(inetAddress,service),timeout);
    }
    Client(SocketAddress socketAddress,int timeout) {
        this.socketAddress=socketAddress;
        this.timeout=timeout;
    }
    final ExecutorService pool=Executors.newFixedThreadPool(10);
    public Future<Boolean> sendFuture(final Message message) {
        return pool.submit(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return send(message);
            }
        });
    }
    public boolean sendOnThread(Message message) throws InterruptedException,ExecutionException {
        Future<Boolean> future=sendFuture(message); // not used yet
        while(!future.isDone())
            ;
        boolean ok=future.get();
        return ok;
    }
    private boolean send_(Message message) {
        Socket socket=connect(message.tabletId,socketAddress,timeout);
        if(socket!=null) {
            try {
                Writer out=new OutputStreamWriter(socket.getOutputStream());
                out.write(message.toString()+"\n");
                out.flush();
                // out.close();
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
                logger.fine("sent: "+message+" at: "+System.currentTimeMillis());
                Main.toaster.toast("sent: "+message+" at: "+System.currentTimeMillis());
                return true;
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else logger.warning("tablet "+message.tabletId+", send to: "+socketAddress+" failed due to no socket!");
        return false;
    }
    static class Holder {
        Boolean ok=null;
    }
    public boolean send(final Message message) {
        final Holder holder=new Holder();
        Thread thread=new Thread(new Runnable() {
            @Override public void run() {
                holder.ok=send_(message);
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
    public static void printThreads() {
        int big=2*Thread.activeCount();
        Thread[] threads=new Thread[big];
        Thread.enumerate(threads);
        for(Thread thread:threads)
            if(thread!=null) System.out.println(thread);
    }
    final SocketAddress socketAddress;
    final int timeout;
    static Integer service0=10_000;
    static Integer defaultTimeout=200;
    public final Logger logger=Logger.getLogger(getClass().getName());
    public static final Logger staticLogger=Logger.getLogger(Client.class.getName());
}
