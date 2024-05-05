package zombie.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;


public final class PZXmlUtil {
	private static boolean s_debugLogging = false;
	private static final ThreadLocal documentBuilders = ThreadLocal.withInitial(()->{
    try {
        DocumentBuilderFactory var0 = DocumentBuilderFactory.newInstance();
        return var0.newDocumentBuilder();
    } catch (ParserConfigurationException var1) {
        ExceptionLogger.logException(var1);
        throw new RuntimeException(var1);
    }
});

	public static Element parseXml(String string) throws PZXmlParserException {
		String string2 = ZomboidFileSystem.instance.resolveFileOrGUID(string);
		Element element;
		try {
			element = parseXmlInternal(string2);
		} catch (IOException | SAXException error) {
			throw new PZXmlParserException("Exception thrown while parsing XML file \"" + string2 + "\"", error);
		}

		element = includeAnotherFile(element, string2);
		String string3 = element.getAttribute("x_extends");
		if (string3 != null && string3.trim().length() != 0) {
			if (!ZomboidFileSystem.instance.isValidFilePathGuid(string3)) {
				string3 = ZomboidFileSystem.instance.resolveRelativePath(string2, string3);
			}

			Element element2 = parseXml(string3);
			Element element3 = resolve(element, element2);
			return element3;
		} else {
			return element;
		}
	}

	private static Element includeAnotherFile(Element element, String string) throws PZXmlParserException {
		String string2 = element.getAttribute("x_include");
		if (string2 != null && string2.trim().length() != 0) {
			if (!ZomboidFileSystem.instance.isValidFilePathGuid(string2)) {
				string2 = ZomboidFileSystem.instance.resolveRelativePath(string, string2);
			}

			Element element2 = parseXml(string2);
			if (!element2.getTagName().equals(element.getTagName())) {
				return element;
			} else {
				Document document = createNewDocument();
				Node node = document.importNode(element, true);
				Node node2 = node.getFirstChild();
				for (Node node3 = element2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
					if (node3 instanceof Element) {
						Element element3 = (Element)document.importNode(node3, true);
						node.insertBefore(element3, node2);
					}
				}

				node.normalize();
				return (Element)node;
			}
		} else {
			return element;
		}
	}

	private static Element resolve(Element element, Element element2) {
		Document document = createNewDocument();
		Element element3 = resolve(element, element2, document);
		document.appendChild(element3);
		if (s_debugLogging) {
			PrintStream printStream = System.out;
			String string = elementToPrettyStringSafe(element2);
			printStream.println("PZXmlUtil.resolve> \r\n<Parent>\r\n" + string + "\r\n</Parent>\r\n<Child>\r\n" + elementToPrettyStringSafe(element) + "\r\n</Child>\r\n<Resolved>\r\n" + elementToPrettyStringSafe(element3) + "\r\n</Resolved>");
		}

		return element3;
	}

	private static Element resolve(Element element, Element element2, Document document) {
		Element element3;
		if (isTextOnly(element)) {
			element3 = (Element)document.importNode(element, true);
			return element3;
		} else {
			element3 = document.createElement(element.getTagName());
			ArrayList arrayList = new ArrayList();
			NamedNodeMap namedNodeMap = element2.getAttributes();
			Node node;
			Attr attr;
			for (int int1 = 0; int1 < namedNodeMap.getLength(); ++int1) {
				node = namedNodeMap.item(int1);
				if (!(node instanceof Attr)) {
					if (s_debugLogging) {
						System.out.println("PZXmlUtil.resolve> Skipping parent.attrib: " + node);
					}
				} else {
					attr = (Attr)document.importNode(node, true);
					arrayList.add(attr);
				}
			}

			NamedNodeMap namedNodeMap2 = element.getAttributes();
			int int2;
			for (int int3 = 0; int3 < namedNodeMap2.getLength(); ++int3) {
				Node node2 = namedNodeMap2.item(int3);
				if (!(node2 instanceof Attr)) {
					if (s_debugLogging) {
						System.out.println("PZXmlUtil.resolve> Skipping attrib: " + node2);
					}
				} else {
					Attr attr2 = (Attr)document.importNode(node2, true);
					String string = attr2.getName();
					boolean boolean1 = true;
					for (int2 = 0; int2 < arrayList.size(); ++int2) {
						Attr attr3 = (Attr)arrayList.get(int2);
						String string2 = attr3.getName();
						if (string2.equals(string)) {
							arrayList.set(int2, attr2);
							boolean1 = false;
							break;
						}
					}

					if (boolean1) {
						arrayList.add(attr2);
					}
				}
			}

			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				attr = (Attr)iterator.next();
				element3.setAttributeNode(attr);
			}

			arrayList = new ArrayList();
			HashMap hashMap = new HashMap();
			for (Node node3 = element2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
				if (!(node3 instanceof Element)) {
					if (s_debugLogging) {
						System.out.println("PZXmlUtil.resolve> Skipping parent.node: " + node3);
					}
				} else {
					Element element4 = (Element)document.importNode(node3, true);
					String string3 = element4.getTagName();
					hashMap.put(string3, 1 + (Integer)hashMap.getOrDefault(string3, 0));
					arrayList.add(element4);
				}
			}

			HashMap hashMap2 = new HashMap();
			Element element5;
			for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (!(node instanceof Element)) {
					if (s_debugLogging) {
						System.out.println("PZXmlUtil.resolve> Skipping node: " + node);
					}
				} else {
					element5 = (Element)document.importNode(node, true);
					String string4 = element5.getTagName();
					int int4 = (Integer)hashMap2.getOrDefault(string4, 0);
					int int5 = 1 + int4;
					hashMap2.put(string4, int5);
					int2 = (Integer)hashMap.getOrDefault(string4, 0);
					if (int2 < int5) {
						arrayList.add(element5);
					} else {
						int int6 = 0;
						for (int int7 = 0; int6 < arrayList.size(); ++int6) {
							Element element6 = (Element)arrayList.get(int6);
							String string5 = element6.getTagName();
							if (string5.equals(string4)) {
								if (int7 == int4) {
									Element element7 = resolve(element5, element6, document);
									arrayList.set(int6, element7);
									break;
								}

								++int7;
							}
						}
					}
				}
			}

			iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				element5 = (Element)iterator.next();
				element3.appendChild(element5);
			}

			return element3;
		}
	}

	private static boolean isTextOnly(Element element) {
		boolean boolean1 = false;
		for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
			boolean boolean2 = false;
			if (node instanceof Text) {
				String string = node.getTextContent();
				boolean boolean3 = StringUtils.isNullOrWhitespace(string);
				boolean2 = !boolean3;
			}

			if (!boolean2) {
				boolean1 = false;
				break;
			}

			boolean1 = true;
		}

		return boolean1;
	}

	private static String elementToPrettyStringSafe(Element element) {
		try {
			return elementToPrettyString(element);
		} catch (TransformerException transformerException) {
			return null;
		}
	}

	private static String elementToPrettyString(Element element) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty("indent", "yes");
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		StreamResult streamResult = new StreamResult(new StringWriter());
		DOMSource dOMSource = new DOMSource(element);
		transformer.transform(dOMSource, streamResult);
		String string = streamResult.getWriter().toString();
		return string;
	}

	public static Document createNewDocument() {
		DocumentBuilder documentBuilder = (DocumentBuilder)documentBuilders.get();
		Document document = documentBuilder.newDocument();
		return document;
	}

	private static Element parseXmlInternal(String string) throws SAXException, IOException {
		try {
			FileInputStream fileInputStream = new FileInputStream(string);
			Element element;
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					DocumentBuilder documentBuilder = (DocumentBuilder)documentBuilders.get();
					Document document = documentBuilder.parse(bufferedInputStream);
					bufferedInputStream.close();
					Element element2 = document.getDocumentElement();
					element2.normalize();
					element = element2;
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
			return element;
		} catch (SAXException sAXException) {
			System.err.println("Exception parsing filename: " + string);
			throw sAXException;
		}
	}

	public static void forEachElement(Element element, Consumer consumer) {
		for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof Element) {
				consumer.accept((Element)node);
			}
		}
	}

	public static Object parse(Class javaClass, String string) throws PZXmlParserException {
		Element element = parseXml(string);
		try {
			Unmarshaller unmarshaller = PZXmlUtil.UnmarshallerAllocator.get(javaClass);
			Object object = unmarshaller.unmarshal(element);
			return object;
		} catch (JAXBException jAXBException) {
			throw new PZXmlParserException("Exception thrown loading source: \"" + string + "\". Loading for type \"" + javaClass + "\"", jAXBException);
		}
	}

	public static void write(Object object, File file) throws TransformerException, IOException, JAXBException {
		Document document = createNewDocument();
		Marshaller marshaller = PZXmlUtil.MarshallerAllocator.get(object);
		marshaller.marshal(object, document);
		write(document, file);
	}

	public static void write(Document document, File file) throws TransformerException, IOException {
		Element element = document.getDocumentElement();
		String string = elementToPrettyString(element);
		FileOutputStream fileOutputStream = new FileOutputStream(file, false);
		PrintWriter printWriter = new PrintWriter(fileOutputStream);
		printWriter.write(string);
		printWriter.flush();
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public static boolean tryWrite(Object object, File file) {
		try {
			write(object, file);
			return true;
		} catch (IOException | JAXBException | TransformerException error) {
			ExceptionLogger.logException(error, "Exception thrown writing data: \"" + object + "\". Out file: \"" + file + "\"");
			return false;
		}
	}

	public static boolean tryWrite(Document document, File file) {
		try {
			write(document, file);
			return true;
		} catch (IOException | TransformerException error) {
			ExceptionLogger.logException(error, "Exception thrown writing document: \"" + document + "\". Out file: \"" + file + "\"");
			return false;
		}
	}

	private static final class UnmarshallerAllocator {
		private static final ThreadLocal instance = ThreadLocal.withInitial(PZXmlUtil.UnmarshallerAllocator::new);
		private final Map m_map = new HashMap();

		public static Unmarshaller get(Class javaClass) throws JAXBException {
			return ((PZXmlUtil.UnmarshallerAllocator)instance.get()).getOrCreate(javaClass);
		}

		private Unmarshaller getOrCreate(Class javaClass) throws JAXBException {
			Unmarshaller unmarshaller = (Unmarshaller)this.m_map.get(javaClass);
			if (unmarshaller == null) {
				JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{javaClass});
				unmarshaller = jAXBContext.createUnmarshaller();
				unmarshaller.setListener(new Listener(){
					
					public void beforeUnmarshal(Object javaClass, Object unmarshaller) {
						super.beforeUnmarshal(javaClass, unmarshaller);
					}
				});

				this.m_map.put(javaClass, unmarshaller);
			}

			return unmarshaller;
		}
	}

	private static final class MarshallerAllocator {
		private static final ThreadLocal instance = ThreadLocal.withInitial(PZXmlUtil.MarshallerAllocator::new);
		private final Map m_map = new HashMap();

		public static Marshaller get(Object object) throws JAXBException {
			return get(object.getClass());
		}

		public static Marshaller get(Class javaClass) throws JAXBException {
			return ((PZXmlUtil.MarshallerAllocator)instance.get()).getOrCreate(javaClass);
		}

		private Marshaller getOrCreate(Class javaClass) throws JAXBException {
			Marshaller marshaller = (Marshaller)this.m_map.get(javaClass);
			if (marshaller == null) {
				JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{javaClass});
				marshaller = jAXBContext.createMarshaller();
				marshaller.setListener(new javax.xml.bind.Marshaller.Listener(){
					
					public void beforeMarshal(Object javaClass) {
						super.beforeMarshal(javaClass);
					}
				});

				this.m_map.put(javaClass, marshaller);
			}

			return marshaller;
		}
	}
}
