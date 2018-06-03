package com.krselee.database;

import org.junit.Before;
import org.junit.Test;

public class TestMysqlConnector {

	MysqlConnector connector;

	@Before
	public void setUp() {
		connector = new MysqlConnector();
	}

	@Test
	public void testConnect() {
		connector.getConnect();
	}

}
