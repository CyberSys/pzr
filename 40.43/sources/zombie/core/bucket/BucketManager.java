package zombie.core.bucket;

import java.util.HashMap;


public class BucketManager {
	static HashMap BucketMap = new HashMap();
	static Bucket ActiveBucket = null;
	static Bucket SharedBucket = new Bucket();

	public static void ActivateBucket(String string) {
	}

	public static Bucket Active() {
		return SharedBucket;
	}

	public static void AddBucket(String string, Bucket bucket) {
		bucket.setName(string);
	}

	public static void DisposeBucket(String string) {
	}

	public static Bucket Shared() {
		return SharedBucket;
	}
}
