package com.tayek.tablet.io;

import java.net.*;
import com.tayek.io.IO.Client;
import com.tayek.tablet.model.Message;

public class Telnet {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress inetAddress=InetAddress.getByName("192.168.1.22");
        Client<Message> client=new Client<>(inetAddress,8080,0);
        Socket socket=client.connect(1,1000);
        System.out.println(socket);
    }
}
