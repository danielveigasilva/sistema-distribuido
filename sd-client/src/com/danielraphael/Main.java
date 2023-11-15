package com.danielraphael;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	
	private static final int minValueXY = 2;
	private static final int maxValueZY = 1000000;
	
	private static final int minValueMs = 50;
	private static final int maxValueMs = 200;
	
	private static final int maxTry = 1;

	public static void main(String[] args) throws InterruptedException {
		
		List<LoadBalanceConnection> lstLoadBalanceCon = new ArrayList<LoadBalanceConnection>();

		for (String arg : args)
			lstLoadBalanceCon.add(new LoadBalanceConnection(Integer.parseInt(arg)));	
		
		while (true) 
		{
			Random rand = new Random(System.currentTimeMillis());
			int indexLb = rand.nextInt(9041542) % lstLoadBalanceCon.size();

			LoadBalanceConnection loadBalanceCon = lstLoadBalanceCon.get(indexLb);
			int tryRepeat = 0;
			
			if (rand.nextBoolean()) 
			{
				int x = rand.nextInt(maxValueZY - minValueXY) + minValueXY;
				int y = rand.nextInt(maxValueZY - minValueXY) + minValueXY;
				
				while (!loadBalanceCon.requestWrite(x, y) && tryRepeat++ < maxTry)
					System.out.println(" >> Cliente : ERRO - Tentando novamente... " + tryRepeat);
			}
			else 
				while (!loadBalanceCon.requestRead() && tryRepeat++ < maxTry)
					System.out.println(" >> Cliente : ERRO - Tentando novamente..." + tryRepeat);
			
			if (tryRepeat > maxTry) {
				System.out.println(" >> Cliente : ERRO - Falha ao se comunicar com o LoadBalance " + loadBalanceCon.getPort());
				break;
			}
			
			int sleepMs = rand.nextInt(maxValueMs - minValueMs) + minValueMs;
			Thread.sleep(sleepMs);
		}
	}
	

}
