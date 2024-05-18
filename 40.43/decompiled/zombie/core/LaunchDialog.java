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

   public LaunchDialog(Frame var1, boolean var2) {
      super(var1, var2);
      this.initComponents();
      Dimension var3 = Toolkit.getDefaultToolkit().getScreenSize();
      int var4 = this.getSize().width;
      int var5 = this.getSize().height;
      int var6 = (var3.width - var4) / 2;
      int var7 = (var3.height - var5) / 2;
      this.setLocation(var6, var7);
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
      this.jButton1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            LaunchDialog.this.jButton1ActionPerformed(var1);
         }
      });
      this.jButton2.setText("Run");
      this.jButton2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            LaunchDialog.this.jButton2ActionPerformed(var1);
         }
      });
      GroupLayout var1 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, var1.createSequentialGroup().addContainerGap(37, 32767).addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, var1.createSequentialGroup().addComponent(this.jButton1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jButton2)).addGroup(Alignment.TRAILING, var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.TRAILING).addComponent(this.jLabel1).addComponent(this.jLabel2)).addGap(18, 18, 18).addGroup(var1.createParallelGroup(Alignment.LEADING, false).addComponent(this.jComboBox1, 0, -1, 32767).addComponent(this.jComboBox2, 0, 161, 32767)))).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addGap(50, 50, 50).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jComboBox1, -2, -1, -2).addComponent(this.jLabel1)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jComboBox2, -2, -1, -2).addComponent(this.jLabel2)).addPreferredGap(ComponentPlacement.RELATED, 32, 32767).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.jButton1).addComponent(this.jButton2)).addContainerGap()));
      this.pack();
   }

   private void jButton2ActionPerformed(ActionEvent var1) {
      d = this;
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            String[] var1 = LaunchDialog.d.jComboBox2.getSelectedItem().toString().split("x");
            LaunchDialog.this.dispose();
            LaunchDialog var10000 = LaunchDialog.d;
            LaunchDialog.split = var1;
            var10000 = LaunchDialog.d;
            LaunchDialog.lev = LaunchDialog.d.jComboBox1.getSelectedItem().toString();
         }
      });
   }

   private void jButton1ActionPerformed(ActionEvent var1) {
      this.dispose();
      System.exit(0);
   }

   public static void main(String[] var0) {
      try {
         LookAndFeelInfo[] var1 = UIManager.getInstalledLookAndFeels();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            LookAndFeelInfo var4 = var1[var3];
            if ("Nimbus".equals(var4.getName())) {
               UIManager.setLookAndFeel(var4.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException var7) {
         Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, var7);
      } catch (InstantiationException var8) {
         Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, var8);
      } catch (IllegalAccessException var9) {
         Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, var9);
      } catch (UnsupportedLookAndFeelException var10) {
         Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, var10);
      }

      LaunchDialog var11 = new LaunchDialog(new JFrame(), true);
      var11.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            System.exit(0);
         }
      });
      byte var12 = 0;
      if ("High".equals(lev)) {
         var12 = 5;
      }

      if ("Medium".equals(lev)) {
         var12 = 3;
      }

      if (split.length == 1) {
         try {
            GameWindow.maina(false, 960, 540, var12);
         } catch (Exception var6) {
            Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, var6);
         }
      } else {
         try {
            GameWindow.maina(true, Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()), var12);
         } catch (Exception var5) {
            Logger.getLogger(LaunchDialog.class.getName()).log(Level.SEVERE, (String)null, var5);
         }
      }

   }
}
