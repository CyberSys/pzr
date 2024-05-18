package se.krka.kahlua.integration.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import se.krka.kahlua.integration.annotations.LuaConstructor;
import se.krka.kahlua.integration.annotations.LuaMethod;


public class LuaDebugDataProcessor implements Processor,ElementVisitor {
	private HashMap classes;
	private Filer filer;

	public Iterable getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String string) {
		return new HashSet();
	}

	public Set getSupportedAnnotationTypes() {
		HashSet hashSet = new HashSet();
		hashSet.add(LuaMethod.class.getName());
		hashSet.add(LuaConstructor.class.getName());
		return hashSet;
	}

	public Set getSupportedOptions() {
		return new HashSet();
	}

	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	public void init(ProcessingEnvironment processingEnvironment) {
		this.filer = processingEnvironment.getFiler();
		this.classes = new HashMap();
	}

	public boolean process(Set set, RoundEnvironment roundEnvironment) {
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			TypeElement typeElement = (TypeElement)iterator.next();
			Set set2 = roundEnvironment.getElementsAnnotatedWith(typeElement);
			Iterator iterator2 = set2.iterator();
			while (iterator2.hasNext()) {
				Element element = (Element)iterator2.next();
				element.accept(this, (Object)null);
			}
		}

		if (roundEnvironment.processingOver()) {
			try {
				this.store();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		return true;
	}

	public Void visit(Element element) {
		return null;
	}

	public Void visit(Element element, Void javaVoid) {
		return null;
	}

	public Void visitExecutable(ExecutableElement executableElement, Void javaVoid) {
		String string = this.findClass(executableElement);
		String string2 = this.findPackage(executableElement);
		ClassParameterInformation classParameterInformation = this.getOrCreate(this.classes, string, string2, this.findSimpleClassName(executableElement));
		String string3 = executableElement.getSimpleName().toString();
		String string4 = DescriptorUtil.getDescriptor(string3, executableElement.getParameters());
		ArrayList arrayList = new ArrayList();
		Iterator iterator = executableElement.getParameters().iterator();
		while (iterator.hasNext()) {
			VariableElement variableElement = (VariableElement)iterator.next();
			arrayList.add(variableElement.getSimpleName().toString());
		}

		MethodParameterInformation methodParameterInformation = new MethodParameterInformation(arrayList);
		classParameterInformation.methods.put(string4, methodParameterInformation);
		return null;
	}

	private ClassParameterInformation getOrCreate(HashMap hashMap, String string, String string2, String string3) {
		ClassParameterInformation classParameterInformation = (ClassParameterInformation)hashMap.get(string);
		if (classParameterInformation == null) {
			classParameterInformation = new ClassParameterInformation(string2, string3);
			hashMap.put(string, classParameterInformation);
		}

		return classParameterInformation;
	}

	private String findClass(Element element) {
		return element.getKind() == ElementKind.CLASS ? element.toString() : this.findClass(element.getEnclosingElement());
	}

	private String findSimpleClassName(Element element) {
		if (element.getKind() == ElementKind.CLASS) {
			String string = element.getSimpleName().toString();
			return element.getEnclosingElement().getKind() == ElementKind.CLASS ? this.findSimpleClassName(element.getEnclosingElement()) + "_" + string : string;
		} else {
			return this.findSimpleClassName(element.getEnclosingElement());
		}
	}

	private String findPackage(Element element) {
		return element.getKind() == ElementKind.PACKAGE ? element.toString() : this.findPackage(element.getEnclosingElement());
	}

	public Void visitPackage(PackageElement packageElement, Void javaVoid) {
		return null;
	}

	public Void visitType(TypeElement typeElement, Void javaVoid) {
		return null;
	}

	public Void visitVariable(VariableElement variableElement, Void javaVoid) {
		return null;
	}

	public Void visitTypeParameter(TypeParameterElement typeParameterElement, Void javaVoid) {
		return null;
	}

	public Void visitUnknown(Element element, Void javaVoid) {
		return null;
	}

	private void store() throws IOException {
		Iterator iterator = this.classes.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			ClassParameterInformation classParameterInformation = (ClassParameterInformation)entry.getValue();
			Object object = null;
			FileObject fileObject = this.filer.createResource(StandardLocation.CLASS_OUTPUT, classParameterInformation.getPackageName(), classParameterInformation.getSimpleClassName() + ".luadebugdata", (Element[])object);
			OutputStream outputStream = fileObject.openOutputStream();
			classParameterInformation.saveToStream(outputStream);
			outputStream.close();
		}
	}
}
