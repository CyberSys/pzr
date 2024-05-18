package zombie.vehicles;

import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.ListIterator;
import org.joml.Quaternionf;
import zombie.GameTime;

public class VehicleInterpolation {
   static final boolean PR = false;
   static final boolean DEBUG = false;
   BufferedWriter DebugInDataWriter;
   BufferedWriter DebugOutDataWriter;
   public int physicsDelayMs;
   public int physicsBufferMs;
   boolean buffering;
   long serverDelay;
   VehicleInterpolationData lastData;
   LinkedList dataList = new LinkedList();
   private static final ArrayDeque pool = new ArrayDeque();
   private float[] currentVehicleData = new float[23];
   private float[] tempVehicleData = new float[23];
   private boolean isSetCurrentVehicleData = false;
   private Quaternionf javaxQuat4f = new Quaternionf();

   VehicleInterpolation(int var1) {
      this.physicsDelayMs = var1;
      this.physicsBufferMs = var1;
      this.buffering = true;
   }

   protected void finalize() {
   }

   public void interpolationDataAdd(ByteBuffer var1) {
      VehicleInterpolationData var4 = pool.isEmpty() ? new VehicleInterpolationData() : (VehicleInterpolationData)pool.pop();
      var4.time = var1.getLong();
      var4.x = var1.getFloat();
      var4.y = var1.getFloat();
      var4.z = var1.getFloat();
      var4.qx = var1.getFloat();
      var4.qy = var1.getFloat();
      var4.qz = var1.getFloat();
      var4.qw = var1.getFloat();
      var4.vx = var1.getFloat();
      var4.vy = var1.getFloat();
      var4.vz = var1.getFloat();
      var4.setNumWheels(var1.getShort());

      for(int var5 = 0; var5 < var4.w_count; ++var5) {
         var4.w_st[var5] = var1.getFloat();
         var4.w_rt[var5] = var1.getFloat();
         var4.w_si[var5] = var1.getFloat();
      }

      long var2 = GameTime.getServerTime() - var4.time;
      if (Math.abs(this.serverDelay - var2) > 2000000000L) {
         this.serverDelay = var2;
      }

      this.serverDelay = (long)((double)this.serverDelay + (double)(var2 - this.serverDelay) * 0.1D);
      ListIterator var9 = this.dataList.listIterator();
      long var6 = 0L;

      while(var9.hasNext()) {
         VehicleInterpolationData var8 = (VehicleInterpolationData)var9.next();
         if (var8.time > var4.time) {
            if (var9.hasPrevious()) {
               var9.previous();
               var9.add(var4);
            } else {
               this.dataList.addFirst(var4);
            }

            return;
         }

         if (var4.time - var8.time > (long)((this.physicsBufferMs + this.physicsDelayMs) * 1000000)) {
            pool.push(var8);
            var9.remove();
         } else if (var8.time > var6) {
            var6 = var8.time;
         }
      }

      if (var6 == 0L || var4.time - var6 > (long)((this.physicsBufferMs + this.physicsDelayMs) * 1000000)) {
         if (!this.dataList.isEmpty()) {
            pool.addAll(this.dataList);
            this.dataList.clear();
         }

         this.buffering = true;
      }

      this.dataList.addLast(var4);
   }

   public boolean interpolationDataGet(float[] var1) {
      VehicleInterpolationData var7;
      if (!this.buffering) {
         if (this.dataList.size() == 0) {
            this.buffering = true;
            return false;
         }
      } else {
         ListIterator var2 = this.dataList.listIterator();
         long var3 = 0L;
         long var5 = 0L;

         while(true) {
            if (!var2.hasNext()) {
               if (var3 != 0L && var5 - var3 >= (long)(this.physicsDelayMs * 1000000)) {
                  this.buffering = false;
                  break;
               }

               return false;
            }

            var7 = (VehicleInterpolationData)var2.next();
            if (var3 == 0L || var7.time < var3) {
               var3 = var7.time;
            }

            if (var7.time > var5) {
               var5 = var7.time;
            }
         }
      }

      VehicleInterpolationData var10 = null;
      VehicleInterpolationData var11 = null;
      long var4 = GameTime.getServerTime() - this.serverDelay - (long)(this.physicsDelayMs * 1000000);
      if (this.physicsDelayMs > 0) {
         ListIterator var6 = this.dataList.listIterator();

         while(var6.hasNext()) {
            var7 = (VehicleInterpolationData)var6.next();
            if (var7.time >= var4) {
               var11 = var7;
               if (!var6.hasPrevious()) {
                  return false;
               }

               var6.previous();
               if (!var6.hasPrevious()) {
                  return false;
               }

               var10 = (VehicleInterpolationData)var6.previous();
               break;
            }
         }

         while(var6.hasPrevious()) {
            var7 = (VehicleInterpolationData)var6.previous();
            pool.push(var7);
            var6.remove();
         }
      } else {
         var11 = (VehicleInterpolationData)this.dataList.getFirst();
      }

      if (var11 == null) {
         this.buffering = true;
         if (!this.dataList.isEmpty()) {
            pool.addAll(this.dataList);
            this.dataList.clear();
         }

         return false;
      } else {
         int var16;
         if (var10 == null) {
            byte var13 = 0;
            int var14 = var13 + 1;
            var1[var13] = var11.x;
            var1[var14++] = var11.y;
            var1[var14++] = var11.z;
            var1[var14++] = var11.qx;
            var1[var14++] = var11.qy;
            var1[var14++] = var11.qz;
            var1[var14++] = var11.qw;
            var1[var14++] = var11.vx;
            var1[var14++] = var11.vy;
            var1[var14++] = var11.vz;
            var1[var14++] = (float)var11.w_count;

            for(var16 = 0; var16 < var11.w_count; ++var16) {
               var1[var14++] = var11.w_st[var16];
               var1[var14++] = var11.w_rt[var16];
               var1[var14++] = var11.w_si[var16];
            }

            return true;
         } else {
            float var12 = (float)(var4 - var10.time) / (float)(var11.time - var10.time);
            byte var15 = 0;
            var16 = var15 + 1;
            var1[var15] = (var11.x - var10.x) * var12 + var10.x;
            var1[var16++] = (var11.y - var10.y) * var12 + var10.y;
            var1[var16++] = (var11.z - var10.z) * var12 + var10.z;
            float var8 = var11.qx * var10.qx + var11.qy * var10.qy + var11.qz * var10.qz + var11.qw * var10.qw;
            if (var8 < 0.0F) {
               var11.qx *= -1.0F;
               var11.qy *= -1.0F;
               var11.qz *= -1.0F;
               var11.qw *= -1.0F;
            }

            var1[var16++] = var10.qx * (1.0F - var12) + var11.qx * var12;
            var1[var16++] = var10.qy * (1.0F - var12) + var11.qy * var12;
            var1[var16++] = var10.qz * (1.0F - var12) + var11.qz * var12;
            var1[var16++] = var10.qw * (1.0F - var12) + var11.qw * var12;
            var1[var16++] = (var11.vx - var10.vx) * var12 + var10.vx;
            var1[var16++] = (var11.vy - var10.vy) * var12 + var10.vy;
            var1[var16++] = (var11.vz - var10.vz) * var12 + var10.vz;
            var1[var16++] = (float)var11.w_count;

            for(int var9 = 0; var9 < var11.w_count; ++var9) {
               var1[var16++] = (var11.w_st[var9] - var10.w_st[var9]) * var12 + var10.w_st[var9];
               var1[var16++] = (var11.w_rt[var9] - var10.w_rt[var9]) * var12 + var10.w_rt[var9];
               var1[var16++] = (var11.w_si[var9] - var10.w_si[var9]) * var12 + var10.w_si[var9];
            }

            return true;
         }
      }
   }

   public boolean interpolationDataGetPR(float[] var1) {
      return this.interpolationDataGet(var1);
   }

   public void setVehicleData(BaseVehicle var1) {
      if (!this.dataList.isEmpty()) {
         pool.addAll(this.dataList);
         this.dataList.clear();
      }

   }

   public void poolData() {
      if (!this.dataList.isEmpty()) {
         pool.addAll(this.dataList);
         this.dataList.clear();
      }
   }
}
