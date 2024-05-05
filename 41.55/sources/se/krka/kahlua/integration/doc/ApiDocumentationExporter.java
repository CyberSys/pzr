package se.krka.kahlua.integration.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import se.krka.kahlua.integration.expose.ClassDebugInformation;
import se.krka.kahlua.integration.expose.MethodDebugInformation;


public class ApiDocumentationExporter implements ApiInformation {
	private final Map classes;
	private final Map classHierarchy = new HashMap();
	private final List rootClasses = new ArrayList();
	private final List allClasses = new ArrayList();
	private Comparator classSorter = new Comparator(){
    
    public int compare(Class var1, Class var2) {
        int var3 = var1.getSimpleName().compareTo(var2.getSimpleName());
        return var3 != 0 ? var3 : var1.getName().compareTo(var2.getName());
    }
};
	private Comparator methodSorter = new Comparator(){
    
    public int compare(MethodDebugInformation var1, MethodDebugInformation var2) {
        return var1.getLuaName().compareTo(var2.getLuaName());
    }
};

	public ApiDocumentationExporter(Map map) {
		this.classes = map;
		this.setupHierarchy();
	}

	public void setupHierarchy() {
		Iterator iterator;
		Class javaClass;
		for (iterator = this.classes.entrySet().iterator(); iterator.hasNext(); this.allClasses.add(javaClass)) {
			Entry entry = (Entry)iterator.next();
			javaClass = (Class)entry.getKey();
			Class javaClass2 = javaClass.getSuperclass();
			if (this.classes.get(javaClass2) != null) {
				Object object = (List)this.classHierarchy.get(javaClass2);
				if (object == null) {
					object = new ArrayList();
					this.classHierarchy.put(javaClass2, object);
				}

				((List)object).add(javaClass);
			} else {
				this.rootClasses.add(javaClass);
			}
		}

		Collections.sort(this.allClasses, this.classSorter);
		Collections.sort(this.rootClasses, this.classSorter);
		iterator = this.classHierarchy.values().iterator();
		while (iterator.hasNext()) {
			List list = (List)iterator.next();
			Collections.sort(list, this.classSorter);
		}
	}

	public List getAllClasses() {
		return this.allClasses;
	}

	public List getChildrenForClass(Class javaClass) {
		List list = (List)this.classHierarchy.get(javaClass);
		return list != null ? list : Collections.emptyList();
	}

	public List getRootClasses() {
		return this.rootClasses;
	}

	private List getMethods(Class javaClass, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		ClassDebugInformation classDebugInformation = (ClassDebugInformation)this.classes.get(javaClass);
		Iterator iterator = classDebugInformation.getMethods().values().iterator();
		while (iterator.hasNext()) {
			MethodDebugInformation methodDebugInformation = (MethodDebugInformation)iterator.next();
			if (methodDebugInformation.isMethod() == boolean1) {
				arrayList.add(methodDebugInformation);
			}
		}

		Collections.sort(arrayList, this.methodSorter);
		return arrayList;
	}

	public List getFunctionsForClass(Class javaClass) {
		return this.getMethods(javaClass, false);
	}

	public List getMethodsForClass(Class javaClass) {
		return this.getMethods(javaClass, true);
	}
}
