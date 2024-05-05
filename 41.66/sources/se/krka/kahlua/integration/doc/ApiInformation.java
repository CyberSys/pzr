package se.krka.kahlua.integration.doc;

import java.util.List;


public interface ApiInformation {

	List getAllClasses();

	List getRootClasses();

	List getChildrenForClass(Class javaClass);

	List getMethodsForClass(Class javaClass);

	List getFunctionsForClass(Class javaClass);
}
