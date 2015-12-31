package com.zhj.tool;

public class RCommandHelper {
	public static void main(String[] args) {
		String path = "/root/R/test/test.R";
		String[] options = { "options1", "options2" };
		RCommandHelper mCommandHelper = new RCommandHelper();
		if (mCommandHelper.CallR(path, options)) {
			System.out.println("Command调用R执行成功！");
		}
	}

	/**
	 * 执行Command命令，运行R程序
	 * 
	 * @param cmd
	 * @return
	 */
	public boolean CallR(String path, String[] options) {
		Process process = null;
		StringBuilder builder = new StringBuilder();
		builder.append("R -f ").append(path)
				.append(" --vanilla --slave --args");
		for (String string : options) {
			builder.append(" ").append(string);
		}
		String cmd = builder.toString();
		System.out.println(cmd);
		try {
			process = Runtime.getRuntime().exec(cmd);
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}
}
