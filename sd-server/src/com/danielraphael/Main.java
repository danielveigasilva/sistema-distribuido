package com.danielraphael;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	private static final int portMutex = 5656;

	public static void main(String[] args) throws IOException {
		
		int port = Integer.parseInt(args[0]);
		
		String mode = "-rf";
		if (args.length > 1)
			mode = (String) args[1];
			
		
		if (!hasOtherServerMutex()) {
			ServerMutex serverMutex = new ServerMutex(portMutex);
			serverMutex.start();
			System.out.println(" >> Serviço Mutex: Ligando...");
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
	
	private static boolean hasOtherServerMutex() {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), portMutex);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        oos.writeObject(-1);
			
			System.out.println(" >> Serviço Mutex: Encontrado");
			socket.close();
			
			return true;
		}
		catch(Exception e) {
			System.out.println(" >> Serviço Mutex: Não Encontrado");
			return false;
		}
	}

}
