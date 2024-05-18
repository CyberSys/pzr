package com.sixlegs.png;

import java.io.IOException;
import java.lang.reflect.Method;


public class PngException extends IOException {
	private static final Method initCause = getInitCause();
	private final boolean fatal;

	private static Method getInitCause() {
		try {
			return PngException.class.getMethod("initCause", Throwable.class);
		} catch (Exception exception) {
			return null;
		}
	}

	PngException(String string, boolean boolean1) {
		this(string, (Throwable)null, boolean1);
	}

	PngException(String string, Throwable throwable, boolean boolean1) {
		super(string);
		this.fatal = boolean1;
		if (throwable != null && initCause != null) {
			try {
				initCause.invoke(this, throwable);
			} catch (RuntimeException runtimeException) {
				throw runtimeException;
			} catch (Exception exception) {
				throw new IllegalStateException("Error invoking initCause: " + exception.getMessage());
			}
		}
	}

	public boolean isFatal() {
		return this.fatal;
	}
}
