package zombie.core;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import zombie.GameWindow;


public class LaunchDialog extends JDialog {
	private static String lev;
	static LaunchDialog d;
	public static String[] split = null;
	private JButton jButton1;
	private JButton jButton2;
	private JComboBox jComboBox1;
	private JComboBox jComboBox2;
	private JLabel jLabel1;
	private JLabel jLabel2;

	public LaunchDialog(Frame frame, boolean boolean1) {
		super(frame, boolean1);
		this.initComponents();
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int int1 = this.getSize().width;
		int int2 = this.getSize().height;
		int int3 = (dimension.width - int1) / 2;
		int int4 = (dimension.height - int2) / 2;
		this.setLocation(int3, int4);
		this.setVisible(true);
	}

	private void initComponents() {
		this.jComboBox1 = new JComboBox();
		this.jLabel1 = new JLabel();
		this.jComboBox2 = new JComboBox();
		this.jLabel2 = new JLabel();
		this.jButton1 = new JButton();
		this.jButton2 = new JButton();
		this.setDefaultCloseOperation(2);
		this.setTitle("Project Zomboid 0.2.0r Launch Options");
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.jComboBox1.setModel(new DefaultComboBoxModel(new String[]{"Low", "Medium", "High"}));
		this.jComboBox1.setSelectedIndex(2);
		this.jLabel1.setText("Graphics:");
		this.jComboBox2.setModel(new DefaultComboBoxModel(new String[]{"Windowed", "1024 x 768", "1280 x 720", "1280 x 800", "1280 x 960", "1280 x 1024", "1366 x 768", "1400 x 1050", "1440 x 900", "1600 x 1200", "1680 x 1050", "1920 x 1080", "1920 x 1200", "2560 x 1440"}));
		this.jLabel2.setText("Resolution:");
		this.jButton1.setText("Quit");
		this.jButton1.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent groupLayout) {
				LaunchDialog.this.jButton1ActionPerformed(groupLayout);
			}
		});
		this.jButton2.setText("Run");
		this.jButton2.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent groupLayout) {
				LaunchDialog.this.jButton2ActionPerformed(groupLayout);
			}
		});
		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(groupLayout);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addContainerGap(37, 32767).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addComponent(this.jButton1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton2)).addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(this.jLabel1).addComponent(this.jLabel2)).addGap(18, 18, 18).addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false).addComponent(this.jComboBox1, 0, -1, 32767).addComponent(this.jComboBox2, 0, 161, 32767)))).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(50, 50, 50).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.jComboBox1, -2, -1, -2).addComponent(this.jLabel1)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.jComboBox2, -2, -1, -2).addComponent(this.jLabel2)).addPreferredGap(ComponentPlacement.RELATED, 32, 32767).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton1).addComponent(this.jButton2)).addContainerGap()));
		this.pack();
	}

	private void jButton2ActionPerformed(ActionEvent actionEvent) {
		d = this;
		SwingUtilities.invokeLater(new Runnable(){
			
			public void run() {
				String[] actionEvent = LaunchDialog.d.jComboBox2.getSelectedItem().toString().split("x");
				LaunchDialog.this.dispose();
				LaunchDialog actionEvent0000 = LaunchDialog.d;
				LaunchDialog.split = actionEvent;
				actionEvent0000 = LaunchDialog.d;
				LaunchDialog.lev = LaunchDialog.d.jComboBox1.getSelectedItem().toString();
			}
		});
	}

	private void jButton1ActionPerformed(ActionEvent actionEvent) {
		this.dispose();
		System.exit(0);
	}

	public static void main(String[] stringArray) {
		try {
			LookAndFeelInfo[] lookAndFeelInfoArray = UIManager.getInstalledLookAndFeels();
			int int1 = lookAndFeelInfoArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				LookAndFeelInfo lookAndFeelInfo = lookAndFeelInfoArray[int2];
				if ("Nimbus".equals(lookAndFeelInfo.getName())) {
					UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException classNotFoundException) {
			Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, classNotFoundException);
		} catch (InstantiationException instantiationException) {
			Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, instantiationException);
		} catch (IllegalAccessException illegalAccessException) {
			Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, illegalAccessException);
		} catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
			Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, unsupportedLookAndFeelException);
		}

		LaunchDialog launchDialog = new LaunchDialog(new JFrame(), true);
		launchDialog.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent lookAndFeelInfoArray) {
				System.exit(0);
			}
		});
		byte byte1 = 0;
		if ("High".equals(lev)) {
			byte1 = 5;
		}

		if ("Medium".equals(lev)) {
			byte1 = 3;
		}

		if (split.length == 1) {
			try {
				GameWindow.maina(false, 960, 540, byte1);
			} catch (Exception exception) {
				Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, exception);
			}
		} else {
			try {
				GameWindow.maina(true, Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()), byte1);
			} catch (Exception exception2) {
				Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, exception2);
			}
		}
	}
}
