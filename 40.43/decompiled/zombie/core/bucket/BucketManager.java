package zombie.core.bucket;

import java.util.HashMap;

public class BucketManager {
   static HashMap BucketMap = new HashMap();
   static Bucket ActiveBucket = null;
   static Bucket SharedBucket = new Bucket();

   public static void ActivateBucket(String var0) {
   }

   public static Bucket Active() {
      return SharedBucket;
   }

   public static void AddBucket(String var0, Bucket var1) {
      var1.setName(var0);
   }

   public static void DisposeBucket(String var0) {
   }

   public static Bucket Shared() {
      return SharedBucket;
   }
}
