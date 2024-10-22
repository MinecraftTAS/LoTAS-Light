package com.minecrafttas.lotas_light.savestates.exceptions;

public class LoadstateException extends Exception {
	public LoadstateException(String s) {
		super(s);
	}

	public LoadstateException(String s, Object... args) {
		super(String.format(s, args));
	}

	public LoadstateException(Throwable t) {
		super(t);
	}
}
