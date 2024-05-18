package zombie.core.utils;

import java.nio.ByteBuffer;

public final class WrappedBuffer {
   private ByteBuffer buf;
   private boolean disposed;

   public WrappedBuffer(ByteBuffer var1) {
      this.buf = var1;
   }

   public ByteBuffer getBuffer() {
      if (this.disposed) {
         throw new IllegalStateException("Can't get buffer after disposal");
      } else {
         return this.buf;
      }
   }

   void allocate() {
      if (!this.disposed) {
         throw new IllegalStateException("Can't allocate if not disposed");
      } else {
         this.disposed = false;
         this.buf.clear();
      }
   }

   public void dispose() {
      this.disposed = true;
   }

   public boolean isDisposed() {
      return this.disposed;
   }

   void clear() {
      this.buf = null;
   }
}
