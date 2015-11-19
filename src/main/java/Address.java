import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import static com.tayek.io.IO.*;
public class Address {
    static void host(String host) throws IOException {
        p(host+" ");
        InetAddress[] x=InetAddress.getAllByName(host);
        for(InetAddress inetAddress:x) {
            p("----");
            p("\t"+inetAddress);
            Socket socket=new Socket();
            SocketAddress socketAddress=new InetSocketAddress(inetAddress,80);
            try {
                socket.connect(socketAddress);
            } catch(IOException e) {
                p("\tcaught: "+e);
            }
            p("\tsocket: "+socket);
            socket.close();
        }
        p("----");
    }
    static void foo(InetAddress inetAddress,int service) {
        p(inetAddress+":"+service);
        // InetAddress.
    }
    public static void main(String[] args) throws IOException {
        InetAddress inetAddress=InetAddress.getByName("192.168.1.2");
        InetAddress localhost=InetAddress.getLocalHost();
        host("192.168.1.1");
        p("--------");
        host("localhost");
        host("192.168.1.2");
        p("--------");
        ServerSocket serverSocket=new ServerSocket(12345);
        p(serverSocket.toString());
        serverSocket.close();
    }
}
