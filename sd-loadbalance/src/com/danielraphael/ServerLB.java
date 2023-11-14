package com.danielraphael;

import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerLB extends Thread{

	private int port;
	private ServerSocket server = null;
	private boolean active = true;
	
	public ServerLB(int port) {
		this.port = port;
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
		            String msg = (String) ois.readObject();
		            System.out.println(" >> LoadBalance " + this.port + ": " + msg);
		            //sendToDataServer(msg)
		            //TODO: Redistribuir para o servidor escolhido, criar critério
		            
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
}
