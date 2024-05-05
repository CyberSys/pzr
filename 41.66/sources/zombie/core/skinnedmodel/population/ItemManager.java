package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.ZomboidFileSystem;
import zombie.core.Rand;


@XmlRootElement
public class ItemManager {
	public ArrayList m_Items = new ArrayList();
	@XmlTransient
	public static ItemManager instance;

	public static void init() {
		File file = ZomboidFileSystem.instance.getMediaFile("items" + File.separator + "items.xml");
		instance = Parse(file.getPath());
	}

	public CarriedItem GetRandomItem() {
		int int1 = Rand.Next(this.m_Items.size() + 1);
		return int1 < this.m_Items.size() ? (CarriedItem)this.m_Items.get(int1) : null;
	}

	public static ItemManager Parse(String string) {
		try {
			return parse(string);
		} catch (JAXBException jAXBException) {
			jAXBException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return null;
	}

	public static ItemManager parse(String string) throws JAXBException, IOException {
		FileInputStream fileInputStream = new FileInputStream(string);
		ItemManager itemManager;
		try {
			JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{ItemManager.class});
			Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
			ItemManager itemManager2 = (ItemManager)unmarshaller.unmarshal(fileInputStream);
			itemManager = itemManager2;
		} catch (Throwable throwable) {
			try {
				fileInputStream.close();
			} catch (Throwable throwable2) {
				throwable.addSuppressed(throwable2);
			}

			throw throwable;
		}

		fileInputStream.close();
		return itemManager;
	}

	public static void Write(ItemManager itemManager, String string) {
		try {
			write(itemManager, string);
		} catch (JAXBException jAXBException) {
			jAXBException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static void write(ItemManager itemManager, String string) throws IOException, JAXBException {
		FileOutputStream fileOutputStream = new FileOutputStream(string);
		try {
			JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{ItemManager.class});
			Marshaller marshaller = jAXBContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			marshaller.marshal(itemManager, fileOutputStream);
		} catch (Throwable throwable) {
			try {
				fileOutputStream.close();
			} catch (Throwable throwable2) {
				throwable.addSuppressed(throwable2);
			}

			throw throwable;
		}

		fileOutputStream.close();
	}
}
