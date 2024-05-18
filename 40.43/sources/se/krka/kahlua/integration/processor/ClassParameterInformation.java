package se.krka.kahlua.integration.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class ClassParameterInformation implements Serializable {
	private static final long serialVersionUID = 7634190901254143200L;
	private final String packageName;
	private final String simpleClassName;
	public Map methods = new HashMap();

	private ClassParameterInformation() {
		this.packageName = null;
		this.simpleClassName = null;
	}

	public ClassParameterInformation(String string, String string2) {
		this.packageName = string;
		this.simpleClassName = string2;
	}

	public ClassParameterInformation(Class javaClass) {
		Package javaPackage = javaClass.getPackage();
		this.packageName = javaPackage == null ? null : javaPackage.getName();
		this.simpleClassName = javaClass.getSimpleName();
		Constructor[] constructorArray = javaClass.getConstructors();
		int int1 = constructorArray.length;
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			Constructor constructor = constructorArray[int2];
			this.methods.put(DescriptorUtil.getDescriptor(constructor), MethodParameterInformation.EMPTY);
		}

		Method[] methodArray = javaClass.getMethods();
		int1 = methodArray.length;
		for (int2 = 0; int2 < int1; ++int2) {
			Method method = methodArray[int2];
			this.methods.put(DescriptorUtil.getDescriptor(method), MethodParameterInformation.EMPTY);
		}
	}

	public String getPackageName() {
		return this.packageName;
	}

	public String getSimpleClassName() {
		return this.simpleClassName;
	}

	public String getFullClassName() {
		return this.packageName != null && !this.packageName.equals("") ? this.packageName + "." + this.simpleClassName : this.simpleClassName;
	}

	public static ClassParameterInformation getFromStream(Class javaClass) throws IOException, ClassNotFoundException {
		String string = getFileName(javaClass);
		InputStream inputStream = javaClass.getResourceAsStream(string);
		if (inputStream == null) {
			return null;
		} else {
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			return (ClassParameterInformation)objectInputStream.readObject();
		}
	}

	private static String getFileName(Class javaClass) {
		return "/" + javaClass.getPackage().getName().replace('.', '/') + "/" + getSimpleName(javaClass) + ".luadebugdata";
	}

	private static String getSimpleName(Class javaClass) {
		return javaClass.getEnclosingClass() != null ? getSimpleName(javaClass.getEnclosingClass()) + "_" + javaClass.getSimpleName() : javaClass.getSimpleName();
	}

	public void saveToStream(OutputStream outputStream) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(this);
	}

	public String getFileName() {
		return getFileName(this.getClass());
	}
}
