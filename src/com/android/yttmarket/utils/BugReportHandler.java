package com.android.yttmarket.utils;


import com.lib.uil.ToastUtils;

import android.content.Context;
import android.os.Looper;


public class BugReportHandler implements Thread.UncaughtExceptionHandler {

	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandler实例 */
	private static BugReportHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;


	/** 获取MyUncaughtExceptionHandler实例 ,单例模式 */
	public static BugReportHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BugReportHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器,
	 * 设置该MyUncaughtExceptionHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		
		
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				ToastUtils.showToast(mContext, "抱歉，程序出错了…");
				Looper.loop();
			}

		}.start();
		return true;
	}
}
