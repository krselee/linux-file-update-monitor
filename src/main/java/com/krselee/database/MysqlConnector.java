package com.krselee.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.krselee.utils.ConfUtil;

public class MysqlConnector {

	protected static Logger logger;

	private static ConfUtil confUtil = new ConfUtil();

	private MysqlManager manager;

	static {
		// log4j
		String customizedPath = "conf/log4j.properties";
		System.setProperty("log4j.configuration", customizedPath);
		logger = LogManager.getLogger(MysqlConnector.class);
	}

	public Connection getConnect() {
		manager = new MysqlManager();
		return manager.getConnect();
	}

	public void close() {
		if (manager != null) {
			manager.close();
		}
	}

	class MysqlManager {
		/**
		 * 内部类，连接mysql
		 */
		public String db_url;

		public static final String name = "com.mysql.cj.jdbc.Driver";

		public String user;

		public String password;

		public Connection conn = null;

		public PreparedStatement pst = null;

		public MysqlManager() {
			try {
				db_url = confUtil.getProperty("db_url");
				user = confUtil.getProperty("user");
				password = confUtil.getProperty("password");
				Class.forName(name);// 指定连接类型
				logger.info("url[" + db_url + "] user[" + user + "] password[" + password + "]");
				conn = (Connection) DriverManager.getConnection(db_url, user, password);// 获取连接
			} catch (Exception e) {
				logger.error("init MysqlManager Failed!", e);
				System.exit(1);
			}
		}

		public Connection getConnect() {
			return conn;
		}

		public void close() {
			try {
				conn.close();
				pst.close();
			} catch (SQLException e) {
				logger.warn("close mysql connector failed!", e);
				e.printStackTrace();
			}
		}
	}
}
