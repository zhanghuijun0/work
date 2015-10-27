package com.zhj.tool;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.ResultSetMetaData;

public class JsonHelper {
	SqlHelper mSqlHelper = new SqlHelper();

	public static void main(String[] args) {
		JsonHelper json = new JsonHelper();
		json.getData();
	}

	private void getData() {
		ResultSet rs = mSqlHelper.queryUsePrepare(
				"SELECT * FROM fund_dailyprice;", null);
		try {
			System.out.println(resultSetToJson(rs));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// while (rs.next()) {
		// System.out.println(rs.getString(1) + "\t" + rs.getString(2)
		// + "\t" + rs.getString(3) + "\t" + rs.getString(4));
		// }
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 把数据库中查找的数据集转化为JSON格式的字符串
	 * 
	 * @param rs
	 *            数据集
	 * @return json格式的字符串
	 * @throws SQLException
	 *             数据库异常
	 * @throws JSONException
	 *             JSON异常
	 */
	public String resultSetToJson(ResultSet rs) throws SQLException,
			JSONException {
		// json数组
		JSONArray array = new JSONArray();

		// 获取列数
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// 遍历ResultSet中的每条数据
		while (rs.next()) {
			JSONObject jsonObj = new JSONObject();

			// 遍历每一列
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);
				jsonObj.put(columnName, value);
			}
			array.put(jsonObj);
		}

		return array.toString();
	}
}
