package com.krselee.file_monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.krselee.beans.FileInfo;
import com.krselee.database.MysqlConnector;
import com.krselee.utils.ConfUtil;
import com.krselee.utils.SqlConstants;
import com.mysql.cj.core.MysqlType;
import com.mysql.cj.core.util.StringUtils;

/**
 * 文件监控器
 * 
 * @author: krselee
 * 
 */
public class FileMonitor {

	protected static Logger logger;

	private static ConfUtil confUtil = new ConfUtil();

	static {
		// log4j
		String customizedPath = "conf/log4j.properties";
		System.setProperty("log4j.configuration", customizedPath);
		logger = LogManager.getLogger(FileMonitor.class);
	}

	/**
	 * check file update state is right
	 */
	public void checkFileUpdate() {
		MysqlConnector connector = new MysqlConnector();

		try {
			// get files to check
			List<FileInfo> infoList = getDbFileInfo(connector.getConnect());
			String basePath = confUtil.getProperty("base_path");

			for (FileInfo fileInfo : infoList) {
				String path = basePath + File.separator + fileInfo.getSystemName() + File.separator
						+ fileInfo.getFileName();
				FileInfo compareInfo = getFileInfo(path);

				if (compareInfo == null) {
					logger.warn("file[" + fileInfo.getFileName() + "] is not exist.");
					continue;
				}

				System.out.println(compareInfo.toString());
			}
		} catch (SQLException | IOException e) {
			logger.warn(e);
		}

		connector.close();
	}

	/**
	 * 读取路径文件信息
	 */
	public FileInfo getFileInfo(String path) throws IOException {
		File file = new File(path);

		if (!file.exists()) {
			return null;
		}

		Path filePath = Paths.get(path);

		BasicFileAttributeView basicView = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);

		BasicFileAttributes basicFileAttributes = basicView.readAttributes();

		FileInfo fileInfo = new FileInfo();
		// 最后修改时间
		Date lastzModifiedDate = new Date(basicFileAttributes.lastModifiedTime().toMillis());
		Timestamp timeStamp = new Timestamp(lastzModifiedDate.getTime());
		fileInfo.setLastModifyTime(timeStamp);
		// 文件名
		fileInfo.setFileName(file.getName());
		fileInfo.setSize(basicFileAttributes.size());

		return fileInfo;
	}

	/**
	 * 查询数据库，获取所有需要监控的词表信息
	 */
	public List<FileInfo> getDbFileInfo(Connection conn) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(SqlConstants.QUERY_ALL_DICT_SQL);
		ResultSet rst = pst.executeQuery();
		List<FileInfo> infoList = new ArrayList<>();
		while (rst.next()) {
			FileInfo info = new FileInfo();

			// system name
			String systemName = rst.getString(1);
			info.setSystemName(systemName);

			// dict name
			String dictName = rst.getString(2);
			info.setFileName(dictName);

			// timestamp
			String lastModifyTime = rst.getString(3);
			Timestamp timeStamp = null;
			if (!StringUtils.isEmptyOrWhitespaceOnly(lastModifyTime)) {
				timeStamp = Timestamp.valueOf(lastModifyTime);
			}
			info.setLastModifyTime(timeStamp);

			// file size
			String size = rst.getString(4);
			if (!StringUtils.isEmptyOrWhitespaceOnly(size)) {
				info.setSize(Long.valueOf(size));
			} else {
				info.setSize(0);
			}

			// update interval
			String updateInterval = rst.getString(5);
			info.setUpdateInterval(Integer.valueOf(updateInterval));

			// print
			System.out.println(info.toString());

			// add into list
			infoList.add(info);
		}
		return infoList;
	}

	/**
	 * 根据类型不同写入pst
	 */
	private void setPstValue(PreparedStatement pst, int idx, String value, MysqlType type) throws Exception {
		switch (type) {
		case INT:
			pst.setInt(idx, Integer.valueOf(value));
			break;
		case VARCHAR:
			pst.setString(idx, value);
			break;
		case DOUBLE:
			pst.setDouble(idx, Double.valueOf(value));
			break;
		case DATE:
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date = format.parse(value);
			pst.setDate(idx, new java.sql.Date(date.getTime()));
			break;
		case DATETIME:
			pst.setString(idx, value);
			break;
		default:
			break;
		}
	}
}
