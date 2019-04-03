package com.mibs.mars.utils;

import java.util.TreeSet;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Series implements Comparable<Series> {
	private Long seriaNum;
	private TreeSet<Instance> instance;
	public Series( Long s) {
		seriaNum = s;
		instance = new TreeSet<>();
	}
	public void addInstance(Instance ins) {
		instance.add( ins );
	}
	public TreeSet<Instance> getInstance(){
		return instance;
	}
	public Long getSeriaNum() {
		return seriaNum;
	}
	@Override
	public int compareTo(Series o) {
		return (int)(seriaNum - o.seriaNum);
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}	
}
