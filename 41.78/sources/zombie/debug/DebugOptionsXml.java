package zombie.debug;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
final class DebugOptionsXml {
	public boolean setDebugMode = false;
	public boolean debugMode = true;
	public final ArrayList options = new ArrayList();

	public static final class OptionNode {
		public String name;
		public boolean value;

		public OptionNode() {
		}

		public OptionNode(String string, boolean boolean1) {
			this.name = string;
			this.value = boolean1;
		}
	}
}