package zombie.input;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.GameWindow;
import zombie.iso.Vector2;

public class JoypadManager {
   public static JoypadManager instance = new JoypadManager();
   public JoypadManager.Joypad[] Joypads = new JoypadManager.Joypad[4];
   public JoypadManager.Joypad[] JoypadsController = new JoypadManager.Joypad[100];
   public ArrayList JoypadList = new ArrayList();
   public HashSet ActiveControllerNames = new HashSet();

   public JoypadManager.Joypad addJoypad(int var1, String var2) {
      JoypadManager.Joypad var3 = new JoypadManager.Joypad();
      var3.ID = var1;
      var3.name = var2;
      this.JoypadsController[var1] = var3;
      this.doControllerFile(var2, var3);
      if (!var3.isDisabled() && this.ActiveControllerNames.contains(var2)) {
         this.JoypadList.add(var3);
      }

      return var3;
   }

   private void doControllerFile(String var1, JoypadManager.Joypad var2) {
      File var3 = new File(GameWindow.getCacheDir() + File.separator + "joypads");
      if (!var3.exists()) {
         var3.mkdir();
      }

      var3 = new File(GameWindow.getCacheDir() + File.separator + "joypads" + File.separator + var1 + ".config");
      if (var3.exists()) {
         System.out.println("reloading " + var3.getAbsolutePath());
         BufferedReader var4 = null;

         try {
            var4 = new BufferedReader(new FileReader(var3.getAbsolutePath()));
         } catch (FileNotFoundException var8) {
            var8.printStackTrace();
         }

         if (var4 != null) {
            int var5 = -1;

            try {
               String var6 = "";

               while(var6 != null) {
                  var6 = var4.readLine();
                  if (var6 != null && var6.trim().length() != 0 && !var6.trim().startsWith("//")) {
                     String[] var7 = var6.split("=");
                     if (var7.length == 2) {
                        var7[0] = var7[0].trim();
                        var7[1] = var7[1].trim();
                        if (var7[0].equals("Version")) {
                           var5 = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("MovementAxisX")) {
                           var2.MovementAxisX = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("MovementAxisXFlipped")) {
                           var2.MovementAxisXFlipped = var7[1].equals("true");
                        } else if (var7[0].equals("MovementAxisY")) {
                           var2.MovementAxisY = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("MovementAxisYFlipped")) {
                           var2.MovementAxisYFlipped = var7[1].equals("true");
                        } else if (var7[0].equals("MovementAxisDeadZone")) {
                           var2.MovementAxisDeadZone = Float.parseFloat(var7[1]);
                        } else if (var7[0].equals("AimingAxisX")) {
                           var2.AimingAxisX = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("AimingAxisXFlipped")) {
                           var2.AimingAxisXFlipped = var7[1].equals("true");
                        } else if (var7[0].equals("AimingAxisY")) {
                           var2.AimingAxisY = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("AimingAxisYFlipped")) {
                           var2.AimingAxisYFlipped = var7[1].equals("true");
                        } else if (var7[0].equals("AimingAxisDeadZone")) {
                           var2.AimingAxisDeadZone = Float.parseFloat(var7[1]);
                        } else if (var7[0].equals("AButton")) {
                           var2.AButton = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("BButton")) {
                           var2.BButton = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("XButton")) {
                           var2.XButton = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("YButton")) {
                           var2.YButton = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("LBumper")) {
                           var2.BumperLeft = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("RBumper")) {
                           var2.BumperRight = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("L3")) {
                           var2.LeftStickButton = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("R3")) {
                           var2.RightStickButton = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("Back")) {
                           var2.Back = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("Start")) {
                           var2.Start = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("DPadUp")) {
                           var2.DPadUp = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("DPadDown")) {
                           var2.DPadDown = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("DPadLeft")) {
                           var2.DPadLeft = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("DPadRight")) {
                           var2.DPadRight = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("Triggers")) {
                           var2.Trigger = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("TriggersFlipped")) {
                           var2.TriggersFlipped = var7[1].equals("true");
                        } else if (var7[0].equals("TriggerLeft")) {
                           var2.TriggerLeft = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("TriggerRight")) {
                           var2.TriggerRight = Integer.parseInt(var7[1]);
                        } else if (var7[0].equals("Disabled")) {
                           var2.Disabled = var7[1].equals("true");
                        } else if (var7[0].equals("Sensitivity")) {
                           var2.setDeadZone(Float.parseFloat(var7[1]));
                        }
                     }
                  }
               }

               var4.close();
            } catch (Exception var9) {
               var9.printStackTrace();
            }

            if (var5 == -1) {
               var2.AButton = 0;
               var2.BButton = 1;
               var2.XButton = 2;
               var2.YButton = 3;
               var2.DPadUp = var2.DPadDown = var2.DPadLeft = var2.DPadRight = -1;
            }
         }
      }

      this.saveFile(var1, var2);
   }

   private void saveFile(String var1, JoypadManager.Joypad var2) {
      File var3 = new File(GameWindow.getCacheDir() + File.separator + "joypads");
      if (!var3.exists()) {
         var3.mkdir();
      }

      var3 = new File(GameWindow.getCacheDir() + File.separator + "joypads" + File.separator + var1 + ".config");

      try {
         BufferedWriter var4 = new BufferedWriter(new FileWriter(var3.getAbsolutePath()));
         String var5 = System.getProperty("line.separator");
         var4.write("Version=1" + var5);
         var4.write("MovementAxisX=" + var2.MovementAxisX + var5);
         var4.write("MovementAxisXFlipped=" + var2.MovementAxisXFlipped + var5);
         var4.write("MovementAxisY=" + var2.MovementAxisY + var5);
         var4.write("MovementAxisYFlipped=" + var2.MovementAxisYFlipped + var5);
         var4.write("// Set the dead zone to the smallest number between 0.0 and 1.0." + var5);
         var4.write("// This is to fix \"loose sticks\"." + var5);
         var4.write("MovementAxisDeadZone=" + var2.MovementAxisDeadZone + var5);
         var4.write("AimingAxisX=" + var2.AimingAxisX + var5);
         var4.write("AimingAxisXFlipped=" + var2.AimingAxisXFlipped + var5);
         var4.write("AimingAxisY=" + var2.AimingAxisY + var5);
         var4.write("AimingAxisYFlipped=" + var2.AimingAxisYFlipped + var5);
         var4.write("AimingAxisDeadZone=" + var2.AimingAxisDeadZone + var5);
         var4.write("AButton=" + var2.AButton + var5);
         var4.write("BButton=" + var2.BButton + var5);
         var4.write("XButton=" + var2.XButton + var5);
         var4.write("YButton=" + var2.YButton + var5);
         var4.write("LBumper=" + var2.BumperLeft + var5);
         var4.write("RBumper=" + var2.BumperRight + var5);
         var4.write("L3=" + var2.LeftStickButton + var5);
         var4.write("R3=" + var2.RightStickButton + var5);
         var4.write("Back=" + var2.Back + var5);
         var4.write("Start=" + var2.Start + var5);
         var4.write("// Normally the D-pad is treated as a single axis (the POV Hat), and these should be -1." + var5);
         var4.write("// If your D-pad is actually 4 separate buttons, set the button numbers here." + var5);
         var4.write("DPadUp=" + var2.DPadUp + var5);
         var4.write("DPadDown=" + var2.DPadDown + var5);
         var4.write("DPadLeft=" + var2.DPadLeft + var5);
         var4.write("DPadRight=" + var2.DPadRight + var5);
         var4.write("// Triggers= is the axis number of both the left and right triggers." + var5);
         var4.write("Triggers=" + var2.Trigger + var5);
         var4.write("TriggersFlipped=" + var2.TriggersFlipped + var5);
         var4.write("// If your triggers are buttons, set the button numbers here." + var5);
         var4.write("// If these are set to something other than -1, then Triggers= is ignored." + var5);
         var4.write("TriggerLeft=" + var2.TriggerLeft + var5);
         var4.write("TriggerRight=" + var2.TriggerRight + var5);
         var4.write("Disabled=" + var2.Disabled + var5);
         var4.write("Sensitivity=" + var2.getDeadZone(0) + var5);
         var4.close();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public void reloadControllerFiles() {
      for(int var1 = 0; var1 < GameWindow.GameInput.getControllerCount(); ++var1) {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         } else {
            this.doControllerFile(this.JoypadsController[var1].name, this.JoypadsController[var1]);
         }
      }

   }

   public void assignJoypad(int var1, int var2) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      this.Joypads[var2] = this.JoypadsController[var1];
      this.Joypads[var2].player = var2;
   }

   public JoypadManager.Joypad getFromPlayer(int var1) {
      return this.Joypads[var1];
   }

   public JoypadManager.Joypad getFromControllerID(int var1) {
      return this.JoypadsController[var1];
   }

   public void onPressed(int var1, int var2) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      this.JoypadsController[var1].onPressed(var2);
   }

   public boolean isDownPressed(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].isDownPressed();
   }

   public boolean isUpPressed(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].isUpPressed();
   }

   public boolean isRightPressed(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].isRightPressed();
   }

   public boolean isLeftPressed(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].isLeftPressed();
   }

   public boolean isLBPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isLBPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isLBPressed();
      }
   }

   public boolean isRBPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isRBPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isRBPressed();
      }
   }

   public boolean isL3Pressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isL3Pressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isL3Pressed();
      }
   }

   public boolean isR3Pressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isR3Pressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isR3Pressed();
      }
   }

   public boolean isRTPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isRTPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isRTPressed();
      }
   }

   public boolean isLTPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isLTPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isLTPressed();
      }
   }

   public boolean isAPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isAPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isAPressed();
      }
   }

   public boolean isBPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isBPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isBPressed();
      }
   }

   public boolean isXPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isXPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isXPressed();
      }
   }

   public boolean isYPressed(int var1) {
      if (var1 < 0) {
         for(int var2 = 0; var2 < this.JoypadList.size(); ++var2) {
            if (((JoypadManager.Joypad)this.JoypadList.get(var2)).isYPressed()) {
               return true;
            }
         }

         return false;
      } else {
         if (this.JoypadsController[var1] == null) {
            this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
         }

         return this.JoypadsController[var1].isYPressed();
      }
   }

   public float getMovementAxisX(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].getMovementAxisX();
   }

   public float getMovementAxisY(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].getMovementAxisY();
   }

   public float getAimingAxisX(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].getAimingAxisX();
   }

   public float getAimingAxisY(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].getAimingAxisY();
   }

   public void onPressedAxis(int var1, int var2) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      this.JoypadsController[var1].onPressedAxis(var2);
   }

   public void onPressedAxisNeg(int var1, int var2) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      this.JoypadsController[var1].onPressedAxisNeg(var2);
   }

   public void onPressedPov(int var1) {
      if (this.JoypadsController[var1] != null) {
         this.JoypadsController[var1].onPressedPov();
      }

   }

   public float getDeadZone(int var1, int var2) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      return this.JoypadsController[var1].getDeadZone(var2);
   }

   public void setDeadZone(int var1, int var2, float var3) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      this.JoypadsController[var1].setDeadZone(var2, var3);
   }

   public void saveControllerSettings(int var1) {
      if (this.JoypadsController[var1] == null) {
         this.addJoypad(var1, GameWindow.GameInput.getController(var1).getName());
      }

      this.saveFile(this.JoypadsController[var1].name, this.JoypadsController[var1]);
   }

   public long getLastActivity(int var1) {
      return this.JoypadsController[var1] == null ? 0L : this.JoypadsController[var1].lastActivity;
   }

   public void setControllerActive(String var1, boolean var2) {
      if (var2) {
         this.ActiveControllerNames.add(var1);
      } else {
         this.ActiveControllerNames.remove(var1);
      }

      this.syncActiveControllers();
   }

   public void syncActiveControllers() {
      this.JoypadList.clear();

      for(int var1 = 0; var1 < this.JoypadsController.length; ++var1) {
         JoypadManager.Joypad var2 = this.JoypadsController[var1];
         if (var2 != null && !var2.isDisabled() && this.ActiveControllerNames.contains(var2.name)) {
            this.JoypadList.add(var2);
         }
      }

   }

   public void Reset() {
      for(int var1 = 0; var1 < this.Joypads.length; ++var1) {
         this.Joypads[var1] = null;
      }

   }

   public static class Joypad {
      String name;
      int ID;
      int player = -1;
      int MovementAxisX = 1;
      boolean MovementAxisXFlipped = false;
      int MovementAxisY = 0;
      boolean MovementAxisYFlipped = false;
      float MovementAxisDeadZone = 0.0F;
      int AimingAxisX = 3;
      boolean AimingAxisXFlipped = false;
      int AimingAxisY = 2;
      boolean AimingAxisYFlipped = false;
      float AimingAxisDeadZone = 0.0F;
      int AButton = 0;
      int BButton = 1;
      int XButton = 2;
      int YButton = 3;
      int DPadUp = -1;
      int DPadDown = -1;
      int DPadLeft = -1;
      int DPadRight = -1;
      int BumperLeft = 4;
      int BumperRight = 5;
      int Back = 6;
      int Start = 7;
      int LeftStickButton = 8;
      int RightStickButton = 9;
      int Trigger = 4;
      boolean TriggersFlipped = false;
      int TriggerLeft = -1;
      int TriggerRight = -1;
      boolean Disabled = false;
      long lastActivity;
      private static Vector2 tempVec2 = new Vector2();

      public boolean isDownPressed() {
         return this.DPadDown != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadDown, this.ID) : GameWindow.GameInput.isControllerDownD(this.ID);
      }

      public boolean isUpPressed() {
         return this.DPadUp != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadUp, this.ID) : GameWindow.GameInput.isControllerUpD(this.ID);
      }

      public boolean isRightPressed() {
         return this.DPadRight != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadRight, this.ID) : GameWindow.GameInput.isControllerRightD(this.ID);
      }

      public boolean isLeftPressed() {
         return this.DPadLeft != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadLeft, this.ID) : GameWindow.GameInput.isControllerLeftD(this.ID);
      }

      public boolean isLBPressed() {
         return GameWindow.GameInput.isButtonPressedD(this.BumperLeft, this.ID);
      }

      public boolean isRBPressed() {
         return GameWindow.GameInput.isButtonPressedD(this.BumperRight, this.ID);
      }

      public boolean isL3Pressed() {
         return GameWindow.GameInput.isButtonPressedD(this.LeftStickButton, this.ID);
      }

      public boolean isR3Pressed() {
         return GameWindow.GameInput.isButtonPressedD(this.RightStickButton, this.ID);
      }

      public boolean isRTPressed() {
         if (this.TriggerRight != -1) {
            return GameWindow.GameInput.isButtonPressedD(this.TriggerRight, this.ID);
         } else if (GameWindow.GameInput.getAxisCount(this.ID) <= this.Trigger) {
            return this.isRBPressed();
         } else if (!this.TriggersFlipped) {
            return GameWindow.GameInput.getAxisValue(this.ID, this.Trigger) < -0.7F;
         } else {
            return GameWindow.GameInput.getAxisValue(this.ID, this.Trigger) > 0.7F;
         }
      }

      public boolean isLTPressed() {
         if (this.TriggerLeft != -1) {
            return GameWindow.GameInput.isButtonPressedD(this.TriggerLeft, this.ID);
         } else if (GameWindow.GameInput.getAxisCount(this.ID) <= this.Trigger) {
            return this.isLBPressed();
         } else if (!this.TriggersFlipped) {
            return GameWindow.GameInput.getAxisValue(this.ID, this.Trigger) > 0.7F;
         } else {
            return GameWindow.GameInput.getAxisValue(this.ID, this.Trigger) < -0.7F;
         }
      }

      public boolean isAPressed() {
         return GameWindow.GameInput.isButtonPressedD(this.AButton, this.ID);
      }

      public boolean isBPressed() {
         return GameWindow.GameInput.isButtonPressedD(this.BButton, this.ID);
      }

      public boolean isXPressed() {
         return GameWindow.GameInput.isButtonPressedD(this.XButton, this.ID);
      }

      public boolean isYPressed() {
         return GameWindow.GameInput.isButtonPressedD(this.YButton, this.ID);
      }

      public float getMovementAxisX() {
         if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisX) {
            return 0.0F;
         } else {
            float var1 = this.MovementAxisDeadZone;
            if (var1 > 0.0F && var1 < 1.0F) {
               float var2 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
               float var3 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
               Vector2 var4 = tempVec2.set(var2, var3);
               if (var4.getLength() < var1) {
                  var4.set(0.0F, 0.0F);
               } else {
                  var4.setLength((var4.getLength() - var1) / (1.0F - var1));
               }

               return this.MovementAxisXFlipped ? -var4.getX() : var4.getX();
            } else {
               return this.MovementAxisXFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX) : GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
            }
         }
      }

      public float getMovementAxisY() {
         if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisY) {
            return 0.0F;
         } else {
            float var1 = this.MovementAxisDeadZone;
            if (var1 > 0.0F && var1 < 1.0F) {
               float var2 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
               float var3 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
               Vector2 var4 = tempVec2.set(var2, var3);
               if (var4.getLength() < var1) {
                  var4.set(0.0F, 0.0F);
               } else {
                  var4.setLength((var4.getLength() - var1) / (1.0F - var1));
               }

               return this.MovementAxisYFlipped ? -var4.getY() : var4.getY();
            } else {
               return this.MovementAxisYFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY) : GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
            }
         }
      }

      public float getAimingAxisX() {
         if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisX) {
            return 0.0F;
         } else {
            float var1 = this.AimingAxisDeadZone;
            if (var1 > 0.0F && var1 < 1.0F) {
               float var2 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
               float var3 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
               Vector2 var4 = tempVec2.set(var2, var3);
               if (var4.getLength() < var1) {
                  var4.set(0.0F, 0.0F);
               } else {
                  var4.setLength((var4.getLength() - var1) / (1.0F - var1));
               }

               return this.AimingAxisXFlipped ? -var4.getX() : var4.getX();
            } else {
               return this.AimingAxisXFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX) : GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
            }
         }
      }

      public float getAimingAxisY() {
         if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisY) {
            return 0.0F;
         } else {
            float var1 = this.AimingAxisDeadZone;
            if (var1 > 0.0F && var1 < 1.0F) {
               float var2 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
               float var3 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
               Vector2 var4 = tempVec2.set(var2, var3);
               if (var4.getLength() < var1) {
                  var4.set(0.0F, 0.0F);
               } else {
                  var4.setLength((var4.getLength() - var1) / (1.0F - var1));
               }

               return this.AimingAxisYFlipped ? -var4.getY() : var4.getY();
            } else {
               return this.AimingAxisYFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY) : GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
            }
         }
      }

      public void onPressed(int var1) {
         this.lastActivity = System.currentTimeMillis();
      }

      public void onPressedAxis(int var1) {
         if (var1 == this.Trigger) {
         }

         this.lastActivity = System.currentTimeMillis();
      }

      public void onPressedAxisNeg(int var1) {
         if (var1 == this.Trigger) {
         }

         this.lastActivity = System.currentTimeMillis();
      }

      public void onPressedPov() {
         this.lastActivity = System.currentTimeMillis();
      }

      public float getDeadZone(int var1) {
         if (var1 >= 0 && var1 < GameWindow.GameInput.getAxisCount(this.ID)) {
            float var2 = GameWindow.GameInput.getController(this.ID).getDeadZone(var1);
            float var3 = 0.0F;
            if ((var1 == this.MovementAxisX || var1 == this.MovementAxisY) && this.MovementAxisDeadZone > 0.0F && this.MovementAxisDeadZone < 1.0F) {
               var3 = this.MovementAxisDeadZone;
            }

            if ((var1 == this.AimingAxisX || var1 == this.AimingAxisY) && this.AimingAxisDeadZone > 0.0F && this.AimingAxisDeadZone < 1.0F) {
               var3 = this.AimingAxisDeadZone;
            }

            return Math.max(var2, var3);
         } else {
            return 0.0F;
         }
      }

      public void setDeadZone(int var1, float var2) {
         if (var1 >= 0 && var1 < GameWindow.GameInput.getAxisCount(this.ID)) {
            GameWindow.GameInput.getController(this.ID).setDeadZone(var1, var2);
         }
      }

      public void setDeadZone(float var1) {
         for(int var2 = 0; var2 < GameWindow.GameInput.getAxisCount(this.ID); ++var2) {
            GameWindow.GameInput.getController(this.ID).setDeadZone(var2, var1);
         }

      }

      public int getID() {
         return this.ID;
      }

      public boolean isDisabled() {
         return this.Disabled;
      }

      public int getAButton() {
         return this.AButton;
      }

      public int getBButton() {
         return this.BButton;
      }

      public int getXButton() {
         return this.XButton;
      }

      public int getYButton() {
         return this.YButton;
      }

      public int getLBumper() {
         return this.BumperLeft;
      }

      public int getRBumper() {
         return this.BumperRight;
      }

      public int getL3() {
         return this.LeftStickButton;
      }

      public int getR3() {
         return this.RightStickButton;
      }

      public int getBackButton() {
         return this.Back;
      }

      public int getStartButton() {
         return this.Start;
      }
   }
}
