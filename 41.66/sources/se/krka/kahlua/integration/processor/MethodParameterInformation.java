package se.krka.kahlua.integration.processor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


public class MethodParameterInformation implements Serializable {
	public static final MethodParameterInformation EMPTY;
	private static final long serialVersionUID = -3059552311721486815L;
	private final List parameterNames;

	public MethodParameterInformation(List list) {
		this.parameterNames = list;
	}

	public String getName(int int1) {
		return int1 >= this.parameterNames.size() ? "arg" + (int1 + 1) : (String)this.parameterNames.get(int1);
	}

	static  {
		EMPTY = new MethodParameterInformation(Collections.EMPTY_LIST);
	}
}
