package com.danielraphael;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerVRRP extends Thread{

	private int portVrrp;
	private int portVipBackup;
	private ServerLB serverBackup = null;
	private List<Integer> lstServersDatabase;
	
	public ServerVRRP(int portVrrp, int portVipBackup, List<Integer> lstServersDatabase) {
		this.portVipBackup = portVipBackup;
		this.portVrrp = portVrrp;
		this.lstServersDatabase = lstServersDatabase;
	}
	
	@Override
    public void run() {
		
		Socket socket = hasOtherServerVrrp();
		if (socket != null)
			clientVrrpLoop(socket);
		else	
			serverVrrpLoop();
    }
	
	private Socket hasOtherServerVrrp() {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), this.portVrrp);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        oos.writeObject("ok");
			
			System.out.println(" >> Serviço VRRP: Encontrado");
			return socket;
		}
		catch(Exception e) {
			System.out.println(" >> Serviço VRRP: Não Encontrado");
			return null;
		}
	}
	
	private void serverVrrpLoop(){
		
		try {
			startThreadServerBackup();
			System.out.println(" >> Serviço VRRP: Ligando...");
			
			ServerSocket server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(this.portVrrp));
			
			while (true) {
				Socket socket = server.accept();
		        System.out.println(" >> Serviço VRRP: Passivo Conectado!");
		        boolean readState = true;
		        
		        killThreadServerBackup();
				while(readState) {
					try {
			            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			            ois.readObject();
					}
					catch(Exception e) {
						startThreadServerBackup();
						readState = false;
					}
					Thread.sleep(10);
		        }
				
				if (socket != null)
					socket.close();
			}
		}
		catch(Exception e) {
			System.out.println(" >> Serviço VRRP: Falha ao Iniciar Servidor");
		}
		
	}
	
	private void clientVrrpLoop(Socket socket){
		
		while (true) {
			try {
				
				if (socket.isClosed())
					socket = new Socket(InetAddress.getLocalHost(), this.portVrrp);
				
				boolean sendState = true;
				killThreadServerBackup();
				
				
				while (sendState) {
					try {
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject("ok");
					}
					catch(Exception e) {
						startThreadServerBackup();
						sendState = false;
					}
					Thread.sleep(10);
				}
				
				if (socket != null)
					socket.close();
			}
			catch(Exception e) {
				startThreadServerBackup();
				serverVrrpLoop();
			}
		}
	}
	
	private void killThreadServerBackup() {
		if (this.serverBackup != null)
        	this.serverBackup.endServer();
		this.serverBackup = null;
	}
	
	private void startThreadServerBackup() {
		if (this.serverBackup == null) {
			this.serverBackup = new ServerLB(this.portVipBackup, this.lstServersDatabase);
			this.serverBackup.start();
		}
	}
}