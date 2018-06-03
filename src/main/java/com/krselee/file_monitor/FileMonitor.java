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
import java.util.Date;

import com.krselee.beans.FileInfo;
import com.krselee.utils.SqlConstants;
import com.mysql.cj.core.MysqlType;

/**
 * 
 * */
public class FileMonitor {

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

	public FileInfo getDbFileInfo(String fileName, Connection conn) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(SqlConstants.QUERY_ALL_DICT_SQL);
		ResultSet rst = pst.executeQuery();
		while (rst.next()) {
			String systemName = rst.getString(1);
			String dictName = rst.getString(2);
			String lastModifyTime = rst.getString(3);
			System.out.println(systemName + "\t" + dictName + "\t" + lastModifyTime);
		}
		return null;
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
