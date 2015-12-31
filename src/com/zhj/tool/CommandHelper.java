package com.zhj.tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 此类用来执行Command命令
 * 
 * @author zhj
 *
 */
public class CommandHelper {
	CreateFolderHelper mFolderHelper = new CreateFolderHelper();
	WriteFileHelper mWriteFileHelper = new WriteFileHelper();
	String mLogPath = "log/command.log";

	public boolean execCommand(String cmd) {
		mWriteFileHelper.writer(mLogPath, cmd+"/n", true);
		mFolderHelper.createFile(mLogPath);
		Process process = null;
		BufferedReader br = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				mWriteFileHelper.writer(mLogPath, line+"/n", true);
			}
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
		mCommandHelper.execCommand("ls /opt");
	}
}
