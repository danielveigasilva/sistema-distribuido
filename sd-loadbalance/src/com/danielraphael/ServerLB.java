package com.danielraphael;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class ServerLB extends Thread{

	private int port;
	private ServerSocket server = null;
	private boolean active = true;
	private List<Integer> lstServersDatabase;
	
	public ServerLB(int port, List<Integer> lstServersDatabase) {
		this.port = port;
		this.lstServersDatabase = lstServersDatabase;
	}
	
	public void endServer() {
		try {
			if (this.server != null && !this.server.isClosed()) {
				active = false;
				this.server.close();
			}
		}
		catch(Exception e) {
			System.out.println(" >> LoadBalance " + this.port + ": ERRO " + e.getMessage());
		}
	}
	
	@Override
    public void run() {
		while (active) {
			try {
				this.server = new ServerSocket();
				this.server.setReuseAddress(true);
				this.server.bind(new InetSocketAddress(this.port));
				System.out.println(" >> Ligando LoadBalance " + this.port);
				
				boolean active = true;
				
				while(active)
				{
		            Socket socket = server.accept();
		            
		            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		            String request = (String) ois.readObject();
		            
		            sendToDataServer(request);
		            
		            ois.close();
		            socket.close();
		        }
			} 
			catch (Exception e) {
				if (e.getMessage().equals("Socket closed"))
					System.out.println(" >> LoadBalance " + this.port + ": Encerrando...");
			}
		}
    }
	
	private void sendToDataServer(String request) {
		
		//TODO: Adicionar novos critérios a distribuição
		Random rand = new Random(System.currentTimeMillis());
		int indexServerDb = rand.nextInt(9041542) % this.lstServersDatabase.size();
		int portServerDb = this.lstServersDatabase.get(indexServerDb);
		
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), portServerDb);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			
			oos.writeObject(request + ";" + this.port);
			oos.close();
			socket.close();
			
			System.out.println(" >> LoadBalance " + this.port + " -> ServerDB " + portServerDb + " : " + request);
		}
		catch(Exception e) {
			System.out.println(" >> LoadBalance " + this.port + " -> ServerDB " + portServerDb + " : ERRO - " + e.getMessage());
		}
	}
}
