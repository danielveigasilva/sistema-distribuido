package com.danielraphael;


public class Main {

	public static void main(String[] args) {
		
		int portVipMaster = Integer.parseInt(args[0]);
		int portVipBackup = Integer.parseInt(args[1]);
		
		ServerLB serverMaster = new ServerLB(portVipMaster);
		ServerVRRP serverVRRP = new ServerVRRP(1234, portVipBackup);
		
		serverVRRP.start();
		serverMaster.start();
	}

}
