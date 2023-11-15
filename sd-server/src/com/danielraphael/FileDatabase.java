package com.danielraphael;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class FileDatabase {
	
	private String filename;
	private int portMutex = 5656;
	Socket socket = null;
	
	public FileDatabase(String filename) {
		this.filename = filename;
		File bd = new File(this.filename);
		
		try {
			bd.createNewFile();
		}
		catch(Exception e) {
			System.out.println(" >> DB : ERRO - " + e.getMessage());
		}
	}
	
	public int countRows() {
		int nRows = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.filename));
			while (reader.readLine() != null) 
				nRows++;
			reader.close();
		}
		catch(Exception e) {
			System.out.println(" >> DB : ERRO - " + e.getMessage());
		}

		return nRows;
	}
	
	public String insertRow(String row) {
		try {
		     FileWriter myWriter = new FileWriter(this.filename, true);
		     myWriter.write(row + "\n");
		     myWriter.close();
		     return row + "\n";
		} 
		catch (Exception e) {
			System.out.println(" >> DB : ERRO - " + e.getMessage());
			return null;
		}
	}
	
	public String pull(int idServer) {
		try {
			if (socket == null)
				socket = new Socket(InetAddress.getLocalHost(), this.portMutex);
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(idServer);
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			String rowsLag = (String) ois.readObject();
			int idServerOrigin = (Integer) ois.readObject();
			
			if (rowsLag != null && idServerOrigin != idServer) {
				
				rowsLag.replace("\n\n", "\n");
				String [] rowsToMerge = rowsLag.split("\n");
				for (String row : rowsToMerge)
					if (!row.isBlank() && !row.isEmpty())
						insertRow(row);
				
				System.out.println("   + Merge -  " + rowsToMerge.length + " Linhas alteradas!");
			}
			//else 
			//	System.out.println(" >> DB : Base já atualizada, 0 Linhas alteradas.");
			
			if (idServerOrigin == idServer)
				return rowsLag == null ? "" : rowsLag + "\n";
			return "";
		}
		catch(Exception e) {
			System.out.println("   >> DB : ERRO PULL - " + e.getMessage());
			return "";
		}
	}
	
	public void commit(int idServer, String commit) {
		try {
			
			if (socket == null)
				socket = new Socket(InetAddress.getLocalHost(), this.portMutex);
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(commit);
			
			//if (!commit.isBlank() && !commit.isEmpty())
			//System.out.println("   >> DB : Enviando push, 1 Linha.");
			socket = null;
		}
		catch(Exception e ) {
			System.out.println("   >> DB : ERRO PUSH - " + e.getMessage());
		}
	}
}
