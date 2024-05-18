package zombie.core.VBO;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBBufferObject;

public final class GLBufferObjectARB implements IGLBufferObject {
   public int GL_ARRAY_BUFFER() {
      return 34962;
   }

   public int GL_ELEMENT_ARRAY_BUFFER() {
      return 34963;
   }

   public int GL_STATIC_DRAW() {
      return 35044;
   }

   public int GL_STREAM_DRAW() {
      return 35040;
   }

   public int GL_BUFFER_SIZE() {
      return 34660;
   }

   public int GL_WRITE_ONLY() {
      return 35001;
   }

   public int glGenBuffers() {
      return ARBBufferObject.glGenBuffersARB();
   }

   public void glBindBuffer(int var1, int var2) {
      ARBBufferObject.glBindBufferARB(var1, var2);
   }

   public void glDeleteBuffers(int var1) {
      ARBBufferObject.glDeleteBuffersARB(var1);
   }

   public void glBufferData(int var1, ByteBuffer var2, int var3) {
      ARBBufferObject.glBufferDataARB(var1, var2, var3);
   }

   public void glBufferData(int var1, long var2, int var4) {
      ARBBufferObject.glBufferDataARB(var1, var2, var4);
   }

   public ByteBuffer glMapBuffer(int var1, int var2, long var3, ByteBuffer var5) {
      return ARBBufferObject.glMapBufferARB(var1, var2, var3, var5);
   }

   public boolean glUnmapBuffer(int var1) {
      return ARBBufferObject.glUnmapBufferARB(var1);
   }

   public void glGetBufferParameter(int var1, int var2, IntBuffer var3) {
      ARBBufferObject.glGetBufferParameterARB(var1, var2, var3);
   }
}
