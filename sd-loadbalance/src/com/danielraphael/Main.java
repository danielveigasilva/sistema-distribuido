package com.danielraphael;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		int portVipMaster = Integer.parseInt(args[0]);
		int portVipBackup = Integer.parseInt(args[1]);
		
		List<Integer> lstServersDatabase = new ArrayList<Integer>();
		
		if (args.length > 3 && args[2].equals("-s"))
			for (int i = 3; i < args.length; i ++)
				lstServersDatabase.add(Integer.parseInt(args[i]));
		
		ServerLB serverMaster = new ServerLB(portVipMaster, lstServersDatabase);
		ServerVRRP serverVRRP = new ServerVRRP(1234, portVipBackup, lstServersDatabase);
		
		serverVRRP.start();
		serverMaster.start();
	}

}
