package zombie.core;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


public class Clipboard {

	public static String getClipboard() {
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);
		try {
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String string = (String)transferable.getTransferData(DataFlavor.stringFlavor);
				return string;
			}
		} catch (UnsupportedFlavorException unsupportedFlavorException) {
		} catch (IOException ioException) {
		}

		return null;
	}

	public static void setClipboard(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, (ClipboardOwner)null);
	}
}
