package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;

public final class GameSoundScript extends BaseScriptObject {
   public GameSound gameSound = new GameSound();

   private int readBlock(String var1, int var2, GameSoundScript.Block var3) {
      int var4;
      for(var4 = var2; var4 < var1.length(); ++var4) {
         if (var1.charAt(var4) == '{') {
            GameSoundScript.Block var5 = new GameSoundScript.Block();
            var3.children.add(var5);
            var3.elements.add(var5);
            String var6 = var1.substring(var2, var4).trim();
            String[] var7 = var6.split("\\s+");
            var5.type = var7[0];
            var5.id = var7.length > 1 ? var7[1] : null;
            var4 = this.readBlock(var1, var4 + 1, var5);
            var2 = var4;
         } else {
            if (var1.charAt(var4) == '}') {
               return var4 + 1;
            }

            if (var1.charAt(var4) == ',') {
               GameSoundScript.Value var8 = new GameSoundScript.Value();
               var8.string = var1.substring(var2, var4);
               var3.values.add(var8.string);
               var3.elements.add(var8);
               var2 = var4 + 1;
            }
         }
      }

      return var4;
   }

   public void Load(String var1, String var2) {
      this.gameSound.name = var1;
      GameSoundScript.Block var3 = new GameSoundScript.Block();
      this.readBlock(var2, 0, var3);
      var3 = (GameSoundScript.Block)var3.children.get(0);
      Iterator var4 = var3.values.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         String[] var6 = var5.split("=");
         String var7 = var6[0].trim();
         String var8 = var6[1].trim();
         if ("category".equals(var7)) {
            this.gameSound.category = var8;
         } else if ("is3D".equals(var7)) {
            this.gameSound.is3D = Boolean.parseBoolean(var8);
         } else if ("loop".equals(var7)) {
            this.gameSound.loop = Boolean.parseBoolean(var8);
         } else if ("master".equals(var7)) {
            this.gameSound.master = GameSound.MasterVolume.valueOf(var8);
         }
      }

      var4 = var3.children.iterator();

      while(var4.hasNext()) {
         GameSoundScript.Block var9 = (GameSoundScript.Block)var4.next();
         if ("clip".equals(var9.type)) {
            GameSoundClip var10 = this.LoadClip(var9);
            this.gameSound.clips.add(var10);
         }
      }

   }

   private GameSoundClip LoadClip(GameSoundScript.Block var1) {
      GameSoundClip var2 = new GameSoundClip(this.gameSound);
      Iterator var3 = var1.values.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String[] var5 = var4.split("=");
         String var6 = var5[0].trim();
         String var7 = var5[1].trim();
         if ("distanceMax".equals(var6)) {
            var2.distanceMax = (float)Integer.parseInt(var7);
            var2.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MAX;
         } else if ("distanceMin".equals(var6)) {
            var2.distanceMin = (float)Integer.parseInt(var7);
            var2.initFlags |= GameSoundClip.INIT_FLAG_DISTANCE_MIN;
         } else if ("event".equals(var6)) {
            var2.event = var7;
         } else if ("file".equals(var6)) {
            var2.file = var7;
         } else if ("pitch".equals(var6)) {
            var2.pitch = Float.parseFloat(var7);
         } else if ("volume".equals(var6)) {
            var2.volume = Float.parseFloat(var7);
         } else if ("reverbFactor".equals(var6)) {
            var2.reverbFactor = Float.parseFloat(var7);
         } else if ("reverbMaxRange".equals(var6)) {
            var2.reverbMaxRange = Float.parseFloat(var7);
         }
      }

      return var2;
   }

   public void reset() {
      this.gameSound.reset();
   }

   private static class Block implements GameSoundScript.BlockElement {
      public String type;
      public String id;
      public ArrayList elements;
      public ArrayList values;
      public ArrayList children;

      private Block() {
         this.elements = new ArrayList();
         this.values = new ArrayList();
         this.children = new ArrayList();
      }

      public GameSoundScript.Block asBlock() {
         return this;
      }

      public GameSoundScript.Value asValue() {
         return null;
      }

      public boolean isEmpty() {
         return this.elements.isEmpty();
      }

      // $FF: synthetic method
      Block(Object var1) {
         this();
      }
   }

   private static class Value implements GameSoundScript.BlockElement {
      String string;

      private Value() {
      }

      public GameSoundScript.Block asBlock() {
         return null;
      }

      public GameSoundScript.Value asValue() {
         return this;
      }

      // $FF: synthetic method
      Value(Object var1) {
         this();
      }
   }

   private interface BlockElement {
      GameSoundScript.Block asBlock();

      GameSoundScript.Value asValue();
   }
}
