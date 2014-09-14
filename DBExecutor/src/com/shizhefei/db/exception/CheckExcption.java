package com.shizhefei.db.exception;

/**
 * 检查异常，extands运行时异常
 * 
 * @author 试着飞 </br> Date: 13-11-18
 */
public class CheckExcption extends RuntimeException {
	private static final long serialVersionUID = 409746655014305330L;

	public CheckExcption() {
		super();
	}

	public CheckExcption(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CheckExcption(String detailMessage) {
		super(detailMessage);
	}

	public CheckExcption(Throwable throwable) {
		super(throwable);
	}

}
