package zombie;

import org.lwjgl.opengl.Display;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.TextureID;

public class SpriteTest {
   public static void main(String[] var0) throws Exception {
      Display.setResizable(true);
      Display.setTitle("SpriteTest");
      Display.setFullscreen(false);
      Rand.init();
      Core.getInstance().init(FrameLoader.FullX, FrameLoader.FullY);
      SpriteRenderer.instance = new SpriteRenderer();
      SpriteRenderer.instance.create();
      TextureID.UseFiltering = false;
      TextureID.UseFiltering = true;
      RenderThread.init();

      while(!Display.isCloseRequested()) {
         Display.sync(60);
      }

   }
}
