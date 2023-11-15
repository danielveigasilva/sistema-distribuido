package com.danielraphael;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException {
		
		int port = Integer.parseInt(args[0]);
		
		String mode = "rf";
		if (args.length > 1)
			mode = (String) args[1];
			
		
		//TODO: Tirar gambiarra
		if (port == 3131) {
			ServerMutex serverMutex = new ServerMutex(5656);
			serverMutex.start();
		}
		
		ServerSocket server = new ServerSocket();
		server.setReuseAddress(true);
		server.bind(new InetSocketAddress(port));
		
		System.out.println(" >> Ligando ServerDB " + port);
		List<ProcessRequest> lstProcessRequest = new ArrayList<ProcessRequest>();
		
		while(true)
		{
			try {
				Socket socket = server.accept();
	            
	            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	            String request = (String) ois.readObject();
	            
	            if (request.equals("processUse")) {
	            	long nProcessActive = lstProcessRequest.stream().filter(process -> process.getState() != Thread.State.TERMINATED).count();
	            	System.out.println(" >> ServerDB : nProcessActive - " + nProcessActive);
	            	ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	            	oos.writeObject(nProcessActive);
	            	continue;
	            }
	            
	            //(new ProcessRequest(request, new FileDatabase("serverdb" + port + ".dat"), port, mode)).start();
	            lstProcessRequest.add(new ProcessRequest(request, new FileDatabase("serverdb" + port + ".dat"), port, mode));
	            lstProcessRequest.get(lstProcessRequest.size() - 1).start();
	            
	            ois.close();
	            socket.close();
			}
			catch(Exception e) {
				System.out.println(" >> ServerDB : ERRO - " + e.getMessage());
			}
        }
	}

}
