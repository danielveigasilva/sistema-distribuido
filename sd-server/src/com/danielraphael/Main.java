package com.danielraphael;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	public static void main(String[] args) throws IOException {
		
		int port = Integer.parseInt(args[0]);
		
		//TODO: Tirar gambiarra
		if (port == 3131) {
			ServerMutex serverMutex = new ServerMutex(5656);
			serverMutex.start();
		}
		
		ServerSocket server = new ServerSocket();
		server.setReuseAddress(true);
		server.bind(new InetSocketAddress(port));
		
		System.out.println(" >> Ligando ServerDB " + port);
		
		while(true)
		{
			try {
				Socket socket = server.accept();
	            
	            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	            String request = (String) ois.readObject();
	            
	            (new ProcessRequest(request, new FileDatabase("serverdb" + port + ".dat"), port)).start();
	            
	            ois.close();
	            socket.close();
			}
			catch(Exception e) {
				System.out.println(" >> ServerDB : ERRO - " + e.getMessage());
			}
        }
	}

}
