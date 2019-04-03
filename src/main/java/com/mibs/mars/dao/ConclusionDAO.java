package com.mibs.mars.dao;

public class ConclusionDAO {

	public Long id;
	public Long explorationid; 
	public String filename;
	public ConclusionDAO(Long i, Long e, String f ) {
		id = i;
		explorationid = e;
		filename = f;
	}
	public Long getId() {
		return id;
	}
	public Long getExplorationid() {
		return explorationid;
	}
	public String getFilename() {
		return filename;
	}
	
}
