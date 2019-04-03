package com.mibs.mars.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DicomImage {

	private List<Series> series;
	private String explorationName;
	public DicomImage(String s) {
		explorationName = s;
		series = new ArrayList<>();
	}
	public void addSeries(Series s) {
		series.add(s);
	}
	public List<Series> getSeries(){
		return series;
	}
	public String getExplorationName() {
		return explorationName;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}	
	
}
