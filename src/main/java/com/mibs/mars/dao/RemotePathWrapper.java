package com.mibs.mars.dao;

import java.util.ArrayList;
import java.util.List;

import com.mibs.mars.entity.RemotePaths;

public class RemotePathWrapper {

	private List<RemotePaths> rp;
	private int numTotal;
	private int numConnected;
	private int numDisconnected;
	public RemotePathWrapper( int t, int c, List<RemotePaths> r) {
		rp = r;
		numTotal = t;
		numConnected = c;
		
	}
	public int getNumTotal() {
		return numTotal;
	}
	public int getNumConnected() {
		return numConnected;
	}
	public int getNumDisconnected() {
		return numTotal - numConnected;
	}
	public void addRemotePaths( RemotePaths r) {
		rp.add( r );
	}
	public List<RemotePaths> getRp() {
		return rp;
	}
}
