package zombie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement
public final class FileGuidTable {
	public final ArrayList files = new ArrayList();
	@XmlTransient
	private final Map guidToPath;
	@XmlTransient
	private final Map pathToGuid;

	public FileGuidTable() {
		this.guidToPath = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		this.pathToGuid = new TreeMap(String.CASE_INSENSITIVE_ORDER);
	}

	public void setModID(String string) {
		FileGuidPair fileGuidPair;
		for (Iterator iterator = this.files.iterator(); iterator.hasNext(); fileGuidPair.guid = string + "-" + fileGuidPair.guid) {
			fileGuidPair = (FileGuidPair)iterator.next();
		}
	}

	public void mergeFrom(FileGuidTable fileGuidTable) {
		this.files.addAll(fileGuidTable.files);
	}

	public void loaded() {
		Iterator iterator = this.files.iterator();
		while (iterator.hasNext()) {
			FileGuidPair fileGuidPair = (FileGuidPair)iterator.next();
			this.guidToPath.put(fileGuidPair.guid, fileGuidPair.path);
			this.pathToGuid.put(fileGuidPair.path, fileGuidPair.guid);
		}
	}

	public void clear() {
		this.files.clear();
		this.guidToPath.clear();
		this.pathToGuid.clear();
	}

	public String getFilePathFromGuid(String string) {
		return (String)this.guidToPath.get(string);
	}

	public String getGuidFromFilePath(String string) {
		return (String)this.pathToGuid.get(string);
	}
}
