package se.krka.kahlua.integration.expose;

import java.util.Iterator;
import java.util.List;


public class MethodDebugInformation {
	private final String luaName;
	private final boolean isMethod;
	private final List parameters;
	private final String returnType;
	private final String returnDescription;

	public MethodDebugInformation(String string, boolean boolean1, List list, String string2, String string3) {
		this.parameters = list;
		this.luaName = string;
		this.isMethod = boolean1;
		this.returnDescription = string3;
		if (list.size() > 0 && ((MethodParameter)list.get(0)).getType().equals(ReturnValues.class.getName())) {
			string2 = "...";
			list.remove(0);
		}

		this.returnType = string2;
	}

	public String getLuaName() {
		return this.luaName;
	}

	public String getLuaDescription() {
		String string = this.isMethod ? "obj:" : "";
		String string2 = TypeUtil.removePackages(this.returnType) + " " + string + this.luaName + "(" + this.getLuaParameterList() + ")\n";
		if (this.getReturnDescription() != null) {
			string2 = string2 + this.getReturnDescription() + "\n";
		}

		return string2;
	}

	public boolean isMethod() {
		return this.isMethod;
	}

	public List getParameters() {
		return this.parameters;
	}

	public String getReturnDescription() {
		return this.returnDescription;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public String toString() {
		return this.getLuaDescription();
	}

	private String getLuaParameterList() {
		StringBuilder stringBuilder = new StringBuilder();
		boolean boolean1 = true;
		Iterator iterator = this.parameters.iterator();
		while (iterator.hasNext()) {
			MethodParameter methodParameter = (MethodParameter)iterator.next();
			if (boolean1) {
				boolean1 = false;
			} else {
				stringBuilder.append(", ");
			}

			String string = TypeUtil.removePackages(methodParameter.getType());
			stringBuilder.append(string).append(" ").append(methodParameter.getName());
		}

		return stringBuilder.toString();
	}

	private String getParameterList() {
		StringBuilder stringBuilder = new StringBuilder();
		boolean boolean1 = true;
		MethodParameter methodParameter;
		for (Iterator iterator = this.parameters.iterator(); iterator.hasNext(); stringBuilder.append(methodParameter.getType()).append(" ").append(methodParameter.getName())) {
			methodParameter = (MethodParameter)iterator.next();
			if (boolean1) {
				boolean1 = false;
			} else {
				stringBuilder.append(", ");
			}
		}

		return stringBuilder.toString();
	}
}
