package com.krselee.beans;

import java.sql.Timestamp;

/**
 * File informations
 */
public class FileInfo {

	private String fileName;

	private Timestamp lastModifyTime;

	private long size;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Timestamp getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Timestamp lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("fileName=" + fileName);
		sBuilder.append(", ");
		sBuilder.append("lastModifyTime=" + lastModifyTime.toString());
		sBuilder.append(", ");
		sBuilder.append("size=" + size);
		return sBuilder.toString();
	}
}
