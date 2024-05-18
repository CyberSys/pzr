package zombie;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.UIManager;
import zombie.core.LaunchDialog;


public class OptionsLauncher {

	public static void main(String[] stringArray) {
		EventQueue.invokeLater(new Runnable(){
			
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				Dimension var1 = Toolkit.getDefaultToolkit().getScreenSize();
				new LaunchDialog((Frame)null, true);
			}
		});
	}
}
