package com.danielraphael;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMutex extends Thread{
	
	private int portMutex;
	
	public ServerMutex(int portMutex) {
		this.portMutex = portMutex;
	}
	
	@Override
    public void run() {
		serverMutexLoopTemp();
    }
	
	
	private void serverMutexLoopTemp(){
		
		try {
			ServerSocket server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(this.portMutex));
			
			Socket masterSocket = null;
			int masterServerID = 0;
			
			while (true) {
				
				Socket branchSocket = server.accept();
				ObjectInputStream ois = new ObjectInputStream(branchSocket.getInputStream());
				
				int branchServerID = (Integer) ois.readObject();
				
				(new ClientMutexThread(masterSocket, masterServerID, branchSocket)).start();
				
	            
				masterSocket = branchSocket;
				masterServerID = branchServerID;
			}
		}
		catch(Exception e) {
			System.out.println(" >> MUTEX : ERRO - " + e.getMessage());
		}
		
	}

}

class ClientMutexThread extends Thread {
	
	private Socket masterSocket;
	private Socket branchSocket;
	private int masterServerID;
	
	public ClientMutexThread(Socket masterSocket, int masterServerID, Socket branchSocket) {
		this.masterSocket = masterSocket;
		this.masterServerID = masterServerID;
		this.branchSocket = branchSocket;
	}
	
	@Override
    public void run() {
		
		try {
			if (masterSocket != null) {
				ObjectInputStream ois = new ObjectInputStream(masterSocket.getInputStream());
				String commit = (String) ois.readObject();
					
				ObjectOutputStream oos = new ObjectOutputStream(branchSocket.getOutputStream());
				oos.writeObject(commit);
				oos.writeObject(masterServerID);
			}
			else {
				ObjectOutputStream oos = new ObjectOutputStream(branchSocket.getOutputStream());
				oos.writeObject(null);
				oos.writeObject(0);
			}
		} 
		catch (Exception e) {
			System.out.println(" >> MUTEX : ERRO - " + e.getMessage());
		}
	}
}
