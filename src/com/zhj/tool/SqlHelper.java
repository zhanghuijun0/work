package com.zhj.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

public class SqlHelper {
	private String driver;
	private String url;
	private String user;
	private String pass;
	private String mysqlConf = "mysql.conf";

	/**
	 * 加载mysql驱动配置文件
	 * 
	 * @param paramFile
	 */
	private void initParam(String paramFile) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(paramFile));
			driver = props.getProperty("driver");
			url = props.getProperty("url");
			user = props.getProperty("user");
			pass = props.getProperty("pass");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param sql
	 *            查询操作的sql语句
	 * @param option
	 *            参数列表，为null的时候，不需要占位符
	 * @return 结果集
	 */
	public ResultSet queryUsePrepare(String sql, String[] option) {
		initParam(mysqlConf);
		ResultSet rs = null;
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			if (option != null) {
				for (int i = 0; i < option.length; i++) {
					pstmt.setString(i + 1, option[i]);
				}
			}
			rs = pstmt.executeQuery();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 
	 * @param sql
	 *            更新操作的sql语句
	 * @param option
	 *            参数列表
	 * @return 受影响的行数
	 */
	public int updateUsePrepare(String sql, String[] option) {
		int count = 0;
		initParam(mysqlConf);
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			if (option != null) {
				for (int i = 0; i < option.length; i++) {
					pstmt.setString(i + 1, option[i]);
				}
			}
			count = pstmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 调用存储过程
	 * 
	 * @param sql
	 * @param option
	 *            参数列表的n-1个参数是输入参数，最后一个参数是传出参数
	 * @return
	 */
	public int callProceduce(String sql, String[] option) {
		int result = 0;
		initParam(mysqlConf);
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			CallableStatement cstmt = conn.prepareCall(sql);
			if (option != null) {
				for (int i = 0; i < option.length - 1; i++) {
					cstmt.setString(i + 1, option[i]);// 输入参数
				}
			}
			cstmt.registerOutParameter(option.length - 1, Types.INTEGER);// 注册输出参数的类型
			cstmt.execute();// 执行存储过程
			result = cstmt.getInt(option.length - 1);// 获取存储过程输出的参数
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/*******************************************************************************/
	/** 以下为调用示例 **/
	/*******************************************************************************/
	// 调用示例：查询数据
	private void queryDataDemo() {
		ResultSet rs = queryUseStatement("select * from fund;");
		try {
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getString(2)
						+ "\t" + rs.getString(3) + "\t" + rs.getString(4)
						+ "\t" + rs.getString(5) + "\t" + rs.getString(6));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 调用示例：使用此方法调用更新操作的时候，需要传入参数，这样防止sql注入，增加了安全性
	private void insertDataDemo() {
		String[] aa = { "2" };
		int status = updateUsePrepare("insert into test1(id) values (1);", null);
		if (status != 0) {
			System.out.println("数据插入成功");
		}
	}

	// 调用示例：使用此方法调用更新操作的时候，需要传入完整的sql语句
	private void insertDataDemo1() {
		int status = updateUseStatement("insert into test1(id) values (110);");
		if (status != 0) {
			System.out.println("数据插入成功");
		}
	}

	// 主方法
	public static void main(String[] args) {
		SqlHelper mSqlHelper = new SqlHelper();
		// mSqlHelper.insertDataDemo();
		// mSqlHelper.queryDataDemo();
		// mSqlHelper.insertDataDemo1();
		mSqlHelper.callProceduceToShowCount("CALL show_counts(?,?,?,?);");
	}

	/**
	 * 
	 * 【测试用例】
	 * 调用存储过程查询各个表中的数据条数
	 * 
	 * @param sql
	 * @return fund、fund_dailyprice、fund_type、institution的数据的条数
	 */
	private int[] callProceduceToShowCount(String sql) {
		int[] result = new int[4];
		initParam(mysqlConf);
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			CallableStatement cstmt = conn.prepareCall(sql);
			for (int i = 1; i <= result.length; i++) {
				cstmt.registerOutParameter(i, Types.INTEGER);// 注册输出参数的类型
			}

			cstmt.execute();// 执行存储过程

			for (int i = 1; i <= result.length; i++) {
				result[i - 1] = cstmt.getInt(i);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/*******************************************************************************/
	/** 以下调用方法不建议使用 **/
	/*******************************************************************************/

	/**
	 * 执行数据库查询操作
	 * 
	 * @param sql
	 * @return
	 */
	private ResultSet queryUseStatement(String sql) {
		initParam(mysqlConf);
		ResultSet rs = null;
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	private int updateUseStatement(String sql) {
		int count = 0;
		initParam(mysqlConf);
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pass);
			Statement stmt = conn.createStatement();
			count = stmt.executeUpdate(sql);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

}
