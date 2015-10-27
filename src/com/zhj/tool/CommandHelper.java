package com.zhj.tool;

import java.io.BufferedReader;

/**
 * 此类用来执行Command命令
 * 
 * @author zhj
 *
 */
public class CommandHelper {
	private boolean execCommand(String cmd) {
		Process process = null;
		BufferedReader br = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
//			StringBuffer sbOut = new StringBuffer(1000);
//			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				System.out.println(line);
//			}
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static void main(String[] args) {
		CommandHelper mCommandHelper = new CommandHelper();
		mCommandHelper.execCommand("dir");
	}
}
