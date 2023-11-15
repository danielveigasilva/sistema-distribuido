package com.danielraphael;

import java.util.Random;

public class ProcessRequest extends Thread {

	private static final int minValueMs = 50;
	private static final int maxValueMs = 200;
	
	private int port;
	private String mode;
	
	private String request;
	private FileDatabase fileDatabase;
	
	public ProcessRequest(String request, FileDatabase fileDatabase, int port, String mode) {
		this.request = request;
		this.fileDatabase = fileDatabase;
		this.port = port;
		this.mode = mode;
	}
	
	@Override
    public void run() {
		
		try {
			
			String[] requestArgs = request.split(";");
			String pull = "";
			
			if (mode.equals("-rf")) {
				System.out.println(" >> LB " + requestArgs[requestArgs.length - 1] + " : (" + Thread.currentThread().getId() + ") " + request.replace(requestArgs[requestArgs.length - 1], ""));
				pull = this.fileDatabase.pull(this.port);
			}
			
			Random rand = new Random(System.currentTimeMillis());
			int sleepMs = rand.nextInt(maxValueMs - minValueMs) + minValueMs;
			Thread.sleep(sleepMs);
			
			if (mode.equals("-pf")) {
				System.out.println(" >> LB " + requestArgs[requestArgs.length - 1] + " : (" + Thread.currentThread().getId() + ") " + request.replace(requestArgs[requestArgs.length - 1], ""));
				pull = this.fileDatabase.pull(this.port);
			}
			
			switch (requestArgs[0]) {
				case "w":
					
					int x = Integer.parseInt(requestArgs[1]);
					int y = Integer.parseInt(requestArgs[2]);
					int z = mdc(x, y);
					
					String newRow = "O MDC entre " + x + " e " + y + " é " + z;
					
					System.out.println("  (" + Thread.currentThread().getId() + ") " + request.replace(requestArgs[requestArgs.length - 1], ""));
					this.fileDatabase.commit(this.port, pull + this.fileDatabase.insertRow(newRow));
					break;
					
				case "r":
					System.out.println("  (" + Thread.currentThread().getId() + ") " + request.replace(requestArgs[requestArgs.length - 1], "") + " - " + this.fileDatabase.countRows());
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
