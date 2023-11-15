package com.danielraphael;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class LoadBalanceConnection {
	
	private int port;
	
	public LoadBalanceConnection(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public boolean requestWrite(int x, int y) {
		return sendText("w;" + x + ";" + y);
	}
	
	public boolean requestRead() {
		return sendText("r");
	}
	
	private boolean sendText(String text) {
		try 
		{
			Socket socket = new Socket(InetAddress.getLocalHost(), this.port);	
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        oos.writeObject(text);
	        oos.close();
	        socket.close();
	        
	        System.out.println(" >> Cliente -> LoadBalance " + this.port + " : " + text);
	        return true;
		}
		catch(Exception e) 
		{
			return false;
		}
	}

}
