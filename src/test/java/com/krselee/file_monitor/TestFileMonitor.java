package com.krselee.file_monitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.krselee.beans.FileInfo;
import com.krselee.database.MysqlConnector;

public class TestFileMonitor {

	private FileMonitor fileMonitor;

	@Before
	public void setUp() {
		fileMonitor = new FileMonitor();
	}

	@Test
	public void testGetFileInfo() {
		FileInfo fileInfo;
		try {
			fileInfo = fileMonitor.getFileInfo("/Users/KrseLee/weka.log");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetDictInfo() throws SQLException {
		MysqlConnector connector = new MysqlConnector();
		Connection conn = connector.getConnect();
		fileMonitor.getDbFileInfo(conn);
		connector.close();
	}

	@After
	public void tearDown() {
	}
}
