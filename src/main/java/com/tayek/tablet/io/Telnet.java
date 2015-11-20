package com.tayek.tablet.io;

import static com.tayek.tablet.io.IO.*;
import java.net.*;
import com.tayek.tablet.io.IO.Client;
import com.tayek.tablet.model.Message;


public class Telnet {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress inetAddress=InetAddress.getByName("192.168.1.22");
        Client client=new Client(inetAddress,8080,0);
        Socket socket=client.connect(1,1000);
        p(socket.toString());
    }
}
