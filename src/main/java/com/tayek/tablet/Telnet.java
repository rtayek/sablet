package com.tayek.tablet;

import java.net.*;

public class Telnet {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress inetAddress=InetAddress.getByName("192.168.1.22");
        Client client=new Client(inetAddress,8080,0);
        Socket socket=client.connect(1,1000);
        System.out.println(socket);
    }
}
