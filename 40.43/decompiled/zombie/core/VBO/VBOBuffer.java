package zombie.core.VBO;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

public class VBOBuffer {
   static int vertexBufferID = -1;
   static int colourBufferID = -1;
   static int indexBufferID = -1;
   static int numQuads = 0;
   static IntBuffer Indices = null;
   static FloatBuffer Vertices = null;
   static int stride = 0;

   public static void init() {
      stride = 32;
      Indices = BufferUtils.createIntBuffer(50000);
      Vertices = BufferUtils.createFloatBuffer('썐' * stride);
      vertexBufferID = createVBOID();
      colourBufferID = createVBOID();
      indexBufferID = createVBOID();
   }

   public static int createVBOID() {
      if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
         IntBuffer var0 = BufferUtils.createIntBuffer(1);
         ARBVertexBufferObject.glGenBuffersARB(var0);
         return var0.get(0);
      } else {
         return 0;
      }
   }

   public static void bufferData(int var0, FloatBuffer var1) {
      if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
         ARBVertexBufferObject.glBindBufferARB(34962, var0);
         ARBVertexBufferObject.glBufferDataARB(34962, var1, 35044);
      }

   }

   public static void bufferElementData(int var0, IntBuffer var1) {
      if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
         ARBVertexBufferObject.glBindBufferARB(34963, var0);
         ARBVertexBufferObject.glBufferDataARB(34963, var1, 35044);
      }

   }

   public static void draw() {
   }

   public static void render() {
      bufferData(vertexBufferID, Vertices);
      bufferElementData(indexBufferID, Indices);
      GL11.glEnableClientState(32884);
      GL11.glEnableClientState(32886);
      GL11.glEnableClientState(32888);
      byte var0 = 0;
      GL11.glVertexPointer(2, 5126, stride, (long)var0);
      var0 = 8;
      GL11.glColorPointer(4, 5126, stride, (long)var0);
      var0 = 24;
      GL11.glTexCoordPointer(2, 5126, stride, (long)var0);
      GL12.glDrawRangeElements(7, 0, numQuads, Indices);
   }
}
