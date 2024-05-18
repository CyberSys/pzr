package zombie.core.textures;

import java.nio.IntBuffer;
import java.util.Stack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugLog;
import zombie.interfaces.ITexture;

public class TextureFBO {
   private static IGLFramebufferObject funcs;
   static int lastID = 0;
   static int lastRID = 0;
   boolean collecting;
   int id = 0;
   Stack stack = new Stack();
   Stack stackR = new Stack();
   ITexture texture;
   int depth = 0;
   private static Boolean checked = null;
   int lviewwid = -1;
   int lviewhei = -1;

   public TextureFBO(ITexture var1) throws Exception {
      try {
         RenderThread.borrowContext();
         TextureID.USE_MIPMAP = false;
         this.texture = var1;
         if (!checkFBOSupport() || this.texture == null) {
            throw new RuntimeException("Could not create FBO!");
         }

         DebugLog.log("FBO: creating " + this.texture.getWidthHW() + "x" + this.texture.getHeightHW());
         this.texture.bind();
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 10240, 9728);
         GL11.glTexParameteri(3553, 10241, 9728);
         Texture.lastTextureID = 0;
         GL11.glBindTexture(3553, 0);
         this.id = funcs.glGenFramebuffers();
         Util.checkGLError();
         funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), this.id);
         Util.checkGLError();
         funcs.glFramebufferTexture2D(funcs.GL_FRAMEBUFFER(), funcs.GL_COLOR_ATTACHMENT0(), 3553, this.texture.getID(), 0);
         Util.checkGLError();
         this.depth = funcs.glGenRenderbuffers();
         Util.checkGLError();
         funcs.glBindRenderbuffer(funcs.GL_RENDERBUFFER(), this.depth);
         Util.checkGLError();
         funcs.glRenderbufferStorage(funcs.GL_RENDERBUFFER(), funcs.GL_DEPTH24_STENCIL8(), this.texture.getWidthHW(), this.texture.getHeightHW());
         Util.checkGLError();
         funcs.glBindRenderbuffer(funcs.GL_RENDERBUFFER(), 0);
         Util.checkGLError();
         funcs.glFramebufferRenderbuffer(funcs.GL_FRAMEBUFFER(), funcs.GL_DEPTH_ATTACHMENT(), funcs.GL_RENDERBUFFER(), this.depth);
         Util.checkGLError();
         funcs.glFramebufferRenderbuffer(funcs.GL_FRAMEBUFFER(), funcs.GL_STENCIL_ATTACHMENT(), funcs.GL_RENDERBUFFER(), this.depth);
         Util.checkGLError();
         int var2 = funcs.glCheckFramebufferStatus(funcs.GL_FRAMEBUFFER());
         if (var2 != funcs.GL_FRAMEBUFFER_COMPLETE()) {
            if (var2 == funcs.GL_FRAMEBUFFER_UNDEFINED()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_UNDEFINED");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_INCOMPLETE_FORMATS()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_FORMATS");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_UNSUPPORTED()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_UNSUPPORTED");
            }

            if (var2 == funcs.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
            }

            throw new RuntimeException("Could not create FBO!");
         }

         funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), 0);
      } catch (Exception var3) {
         funcs.glDeleteFramebuffers(this.id);
         funcs.glDeleteRenderbuffers(this.depth);
         this.id = 0;
         this.depth = 0;
         this.texture = null;
         RenderThread.returnContext();
         throw var3;
      }

      RenderThread.returnContext();
   }

   public TextureFBO(ITexture var1, boolean var2) throws Exception {
      try {
         RenderThread.borrowContext();
         TextureID.USE_MIPMAP = false;
         this.texture = var1;
         if (!checkFBOSupport() || this.texture == null) {
            throw new RuntimeException("Could not create FBO!");
         }

         this.texture.bind();
         GL11.glTexImage2D(3553, 0, 6408, this.texture.getWidthHW(), this.texture.getHeightHW(), 0, 6408, 5121, (IntBuffer)null);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
         this.id = funcs.glGenFramebuffers();
         Util.checkGLError();
         funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), this.id);
         Util.checkGLError();
         funcs.glFramebufferTexture2D(funcs.GL_FRAMEBUFFER(), funcs.GL_COLOR_ATTACHMENT0(), 3553, this.texture.getID(), 0);
         Util.checkGLError();
         this.depth = funcs.glGenRenderbuffers();
         Util.checkGLError();
         funcs.glBindRenderbuffer(funcs.GL_RENDERBUFFER(), this.depth);
         Util.checkGLError();
         funcs.glRenderbufferStorage(funcs.GL_RENDERBUFFER(), 6402, this.texture.getWidthHW(), this.texture.getHeightHW());
         Util.checkGLError();
         funcs.glBindRenderbuffer(funcs.GL_RENDERBUFFER(), 0);
         funcs.glFramebufferRenderbuffer(funcs.GL_FRAMEBUFFER(), funcs.GL_DEPTH_ATTACHMENT(), funcs.GL_RENDERBUFFER(), this.depth);
         Util.checkGLError();
         int var3 = funcs.glCheckFramebufferStatus(funcs.GL_FRAMEBUFFER());
         if (var3 != funcs.GL_FRAMEBUFFER_COMPLETE()) {
            if (var3 == funcs.GL_FRAMEBUFFER_UNDEFINED()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_UNDEFINED");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_INCOMPLETE_FORMATS()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_FORMATS");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_UNSUPPORTED()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_UNSUPPORTED");
            }

            if (var3 == funcs.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE()) {
               DebugLog.log("glCheckFramebufferStatus = GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
            }

            throw new RuntimeException("Could not create FBO!");
         }

         funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), 0);
      } catch (Exception var4) {
         funcs.glDeleteFramebuffers(this.id);
         funcs.glDeleteRenderbuffers(this.depth);
         this.id = 0;
         this.depth = 0;
         this.texture = null;
         RenderThread.returnContext();
         throw var4;
      }

      RenderThread.returnContext();
   }

   public static final boolean checkFBOSupport() {
      if (checked != null) {
         return checked;
      } else if (GLContext.getCapabilities().OpenGL30) {
         System.out.println("OpenGL 3.0 framebuffer objects supported");
         funcs = new GLFramebufferObject30();
         return checked = Boolean.TRUE;
      } else if (GLContext.getCapabilities().GL_ARB_framebuffer_object) {
         System.out.println("GL_ARB_framebuffer_object supported");
         funcs = new GLFramebufferObjectARB();
         return checked = Boolean.TRUE;
      } else if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
         System.out.println("GL_EXT_framebuffer_object supported");
         if (!GLContext.getCapabilities().GL_EXT_packed_depth_stencil) {
            System.out.println("GL_EXT_packed_depth_stencil not supported");
         }

         funcs = new GLFramebufferObjectEXT();
         return checked = Boolean.TRUE;
      } else {
         System.out.println("None of OpenGL 3.0, GL_ARB_framebuffer_object or GL_EXT_framebuffer_object are supported, zoom disabled");
         return checked = Boolean.TRUE;
      }
   }

   public void destroy() {
      if (this.texture != null && this.id != 0 && this.depth != 0) {
         RenderThread.borrowContext();
         this.texture.destroy();
         this.texture = null;
         funcs.glDeleteFramebuffers(this.id);
         funcs.glDeleteRenderbuffers(this.depth);
         this.id = 0;
         this.depth = 0;
         RenderThread.returnContext();
      }
   }

   public void endDrawing() {
      if (!this.stack.isEmpty()) {
         lastID = (Integer)this.stack.pop();
         lastRID = (Integer)this.stackR.pop();
      } else {
         lastID = 0;
         lastRID = 0;
      }

      funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), lastID);
   }

   public ITexture getTexture() {
      return this.texture;
   }

   public int getBufferId() {
      return this.id;
   }

   public boolean isDestroyed() {
      return this.texture == null || this.id == 0 || this.depth == 0;
   }

   public void startDrawing() {
      this.startDrawing(false, false);
   }

   public void startDrawingBasic(boolean var1) {
      this.stack.push(lastID);
      this.stackR.push(lastRID);
      lastID = this.id;
      lastRID = this.depth;
      funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), this.id);
   }

   public void startDrawing(boolean var1, boolean var2) {
      this.stack.push(lastID);
      this.stackR.push(lastRID);
      lastID = this.id;
      lastRID = this.depth;
      funcs.glBindFramebuffer(funcs.GL_FRAMEBUFFER(), this.id);
      GL11.glViewport(0, 0, this.texture.getWidth(), this.texture.getHeight());
      this.lviewhei = this.texture.getHeight();
      this.lviewwid = this.texture.getWidth();
      if (var1) {
         GL11.glClearColor(0.0F, 0.0F, 0.0F, var2 ? 0.0F : 1.0F);
         GL11.glClear(16640);
         if (var2) {
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
         }
      }

      GL11.glLoadIdentity();
   }

   public void swapTexture(ITexture var1) {
      if (var1 != null && var1 != this.texture) {
         if (var1.getWidth() == this.texture.getWidth() && var1.getHeight() == this.texture.getHeight()) {
            funcs.glFramebufferTexture2D(funcs.GL_FRAMEBUFFER(), funcs.GL_COLOR_ATTACHMENT0(), 3553, var1.getID(), 0);
            Util.checkGLError();
            this.texture = var1;
         }
      }
   }
}
