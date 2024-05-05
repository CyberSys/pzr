package se.krka.kahlua.integration.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.integration.expose.MethodParameter;


public class DokuWikiPrinter {
	private final ApiInformation information;
	private final PrintWriter writer;

	public DokuWikiPrinter(File file, ApiInformation apiInformation) throws IOException {
		this((Writer)(new FileWriter(file)), apiInformation);
	}

	public DokuWikiPrinter(Writer writer, ApiInformation apiInformation) {
		this.information = apiInformation;
		this.writer = new PrintWriter(writer);
	}

	public void process() {
		this.printClassHierarchy();
		this.printFunctions();
		this.writer.close();
	}

	private void printFunctions() {
		this.writer.println("====== Global functions ======");
		List list = this.information.getAllClasses();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Class javaClass = (Class)iterator.next();
			this.printClassFunctions(javaClass);
		}
	}

	private void printClassFunctions(Class javaClass) {
		List list = this.information.getFunctionsForClass(javaClass);
		if (list.size() > 0) {
			this.writer.printf("===== %s ====\n", javaClass.getSimpleName());
			this.writer.printf("In package: %s\n", javaClass.getPackage().getName());
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				MethodDebugInformation methodDebugInformation = (MethodDebugInformation)iterator.next();
				this.printFunction(methodDebugInformation, "====");
			}

			this.writer.printf("\n----\n\n");
		}
	}

	private void printFunction(MethodDebugInformation methodDebugInformation, String string) {
		this.writer.printf("%s %s %s\n", string, methodDebugInformation.getLuaName(), string);
		this.writer.printf("<code lua>%s</code>\n", methodDebugInformation.getLuaDescription());
		Iterator iterator = methodDebugInformation.getParameters().iterator();
		while (iterator.hasNext()) {
			MethodParameter methodParameter = (MethodParameter)iterator.next();
			String string2 = methodParameter.getName();
			String string3 = methodParameter.getType();
			String string4 = methodParameter.getDescription();
			if (string4 == null) {
				this.writer.printf("  - **\'\'%s\'\'** \'\'%s\'\'\n", string3, string2);
			} else {
				this.writer.printf("  - **\'\'%s\'\'** \'\'%s\'\': %s\n", string3, string2, string4);
			}
		}

		String string5 = methodDebugInformation.getReturnDescription();
		if (string5 == null) {
			this.writer.printf("  * returns \'\'%s\'\'\n", methodDebugInformation.getReturnType());
		} else {
			this.writer.printf("  * returns \'\'%s\'\': %s\n", methodDebugInformation.getReturnType(), string5);
		}
	}

	private void printClassHierarchy() {
		this.writer.println("====== Class hierarchy ======");
		List list = this.information.getRootClasses();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Class javaClass = (Class)iterator.next();
			this.printClassHierarchy(javaClass, (Class)null);
		}
	}

	private void printClassHierarchy(Class javaClass, Class javaClass2) {
		List list = this.information.getChildrenForClass(javaClass);
		List list2 = this.information.getMethodsForClass(javaClass);
		if (list.size() > 0 || list2.size() > 0 || javaClass2 != null) {
			this.writer.printf("===== %s =====\n", javaClass.getSimpleName());
			this.writer.printf("In package: \'\'%s\'\'\n", javaClass.getPackage().getName());
			if (javaClass2 != null) {
				this.writer.printf("\nSubclass of [[#%s|%s]]\n", javaClass2.getSimpleName(), javaClass2.getSimpleName());
			}

			if (list.size() > 0) {
				this.writer.printf("\nChildren: ");
				boolean boolean1 = false;
				Class javaClass3;
				for (Iterator iterator = list.iterator(); iterator.hasNext(); this.writer.printf("[[#%s|%s]]", javaClass3.getSimpleName(), javaClass3.getSimpleName())) {
					javaClass3 = (Class)iterator.next();
					if (boolean1) {
						this.writer.print(", ");
					} else {
						boolean1 = true;
					}
				}
			}

			this.printMethods(javaClass);
			this.writer.printf("\n----\n\n");
			Iterator iterator2 = list.iterator();
			while (iterator2.hasNext()) {
				Class javaClass4 = (Class)iterator2.next();
				this.printClassHierarchy(javaClass4, javaClass);
			}
		}
	}

	private void printMethods(Class javaClass) {
		List list = this.information.getMethodsForClass(javaClass);
		if (list.size() > 0) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				MethodDebugInformation methodDebugInformation = (MethodDebugInformation)iterator.next();
				this.printFunction(methodDebugInformation, "====");
			}
		}
	}
}
