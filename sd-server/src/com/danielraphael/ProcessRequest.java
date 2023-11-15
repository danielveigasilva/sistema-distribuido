package com.danielraphael;

import java.util.Random;

public class ProcessRequest extends Thread {

	private static final int minValueMs = 50;
	private static final int maxValueMs = 200;
	
	private static final int portMutex = 5656;
	
	private int port;
	
	private String request;
	private FileDatabase fileDatabase;
	
	private ServerMutex serverMutex;
	
	public ProcessRequest(String request, FileDatabase fileDatabase, int port) {
		this.request = request;
		this.fileDatabase = fileDatabase;
		this.port = port;
	}
	
	@Override
    public void run() {
		
		try {
			
			String[] requestArgs = request.split(";");
			System.out.println(" >> LoadBalance " + requestArgs[requestArgs.length - 1] + " -> Add Stack " + request.replace(requestArgs[requestArgs.length - 1], ""));
			String pull = this.fileDatabase.pull(this.port);
			//Fazendo o pull antes do delay temos um atrado
			
			Random rand = new Random(System.currentTimeMillis());
			int sleepMs = rand.nextInt(maxValueMs - minValueMs) + minValueMs;
			Thread.sleep(sleepMs);
			
			//Fazendo o pull depois o delay é menor
			
			switch (requestArgs[0]) {
				case "w":
					
					int x = Integer.parseInt(requestArgs[1]);
					int y = Integer.parseInt(requestArgs[2]);
					int z = mdc(x, y);
					
					String newRow = "O MDC entre " + x + " e " + y + " é " + z;
					this.fileDatabase.commit(this.port, pull + this.fileDatabase.insertRow(newRow));
					break;
					
				case "r":
					System.out.println("   #LINHAS : " + this.fileDatabase.countRows());
					this.fileDatabase.commit(this.port, pull);
					break;
			}
			
		} 
		catch (Exception e) {
			System.out.println(" >> ServerDB : ERRO - " + e.getMessage());
		}
	}
	
	static int mdc(int x, int y) {
        for (int i = Math.min(x, y); i > 1; i--)
            if (x % i == 0 && y % i == 0)
                return i;
        return 1;
    }
}
