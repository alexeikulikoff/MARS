package com.mibs.mars.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mibs.mars.controllers.MarsController;
import com.mortennobel.imagescaling.ResampleOp;

public class SerializedDicom implements Serializable{
	
	static Logger logger = LoggerFactory.getLogger(SerializedDicom.class);
	
	private static final long serialVersionUID = 1L;
	private byte[] Image;
	private int Seria;
	private int Instance;
	private String PatientName;
	private String PatientBirthDate;
	private String PatientID;
	private String PatientAge;
	private String PatientWeight;
	private String PatientSex;
	private String InstitutionName;
	private String StudyDate;
	private String StudyDescription;
	private String StudyComment;
	private String PerformingPhysicianName;
	private String SliceLocation;
	private String SliceThickness;
	private int Rows;
	private int Columns;
	private String PixelSpacing;
	private String ManufactoreModelName;
	
	public byte[] getImage() {
		return Image;
	}
	public void setImage(byte[] o) {
		Image = o;
	}
	public int getSeria() {
		return Seria;
	}
	public void setSeria(int s) {
		Seria = s;
	}
	public int getInstance() {
		return Instance;
	}
	public void setInstance(int s) {
		Instance = s;
	}
	public int getRows() {
		return Rows;
	}
	public void setRows(int s) {
		Rows = s;
	}
	public int getColumns() {
		return Columns;
	}
	public void setColumns(int s) {
		Columns = s;
	}
	public String getPatientName() {
		return PatientName;
	}
	public void setPatientName(String p) {
		PatientName = p;
	}
	public String getInstitutionName() {
		return InstitutionName;
	}
	public void setInstitutionName(String p) {
		InstitutionName = p;
	}
	public String getPatientBirthDate() {
		return PatientBirthDate;
	}
	public void setPatientBirthDate(String p) {
		PatientBirthDate = p;
	}
	public String getPatientID() {
		return PatientID;
	}
	public void setPatientID(String p) {
		PatientID = p;
	}
	public String getPatientAge() {
		return PatientAge;
	}
	public void setPatientAge(String p) {
		PatientAge = p;
	}
	public String getPatientWeight() {
		return PatientWeight;
	}
	public void setPatientWeight(String p) {
		PatientWeight = p;
	}
	public String getPatientSex() {
		return PatientSex;
	}
	public void setPatientSex(String p) {
		PatientSex = p;
	}
	public String getStudyDate() {
		return StudyDate;
	}
	public void setStudyDate(String p) {
		StudyDate = p;
	}
	public String getStudyDescription() {
		return StudyDescription;
	}
	public void setStudyDescription(String p) {
		StudyDescription = p;
	}
	public String getStudyComment() {
		return StudyComment;
	}
	public void setStudyComment(String p) {
		StudyComment = p;
	}
	public String getPerformingPhysicianName() {
		return PerformingPhysicianName;
	}
	public void setPerformingPhysicianName(String p) {
		PerformingPhysicianName = p;
	}
	public String getSliceLocation() {
		return SliceLocation;
	}
	public void setSliceLocation(String p) {
		SliceLocation = p;
	}
	public String getSliceThickness() {
		return SliceThickness;
	}
	public void setSliceThickness(String p) {
		SliceThickness = p;
	}
	public String getPixelSpacing() {
		return PixelSpacing;
	}
	public void setPixelSpacing(String p) {
		PixelSpacing = p;
	}
	public String getManufactoreModelName() {
		return ManufactoreModelName;
	}
	public void setManufactoreModelName(String p) {
		ManufactoreModelName = p;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}	
	
}
