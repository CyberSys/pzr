package zombie.util.lambda;

import java.util.Comparator;
import zombie.util.Pool;
import zombie.util.PooledObject;


public final class Comparators {

	public static final class Params2 {

		public static final class CallbackStackItem extends Comparators.Params2.StackItem implements Comparator {
			private Comparators.Params2.ICallback comparator;
			private static final Pool s_pool = new Pool(Comparators.Params2.CallbackStackItem::new);

			public int compare(Object object, Object object2) {
				return this.comparator.compare(object, object2, this.val1, this.val2);
			}

			public static Comparators.Params2.CallbackStackItem alloc(Object object, Object object2, Comparators.Params2.ICallback iCallback) {
				Comparators.Params2.CallbackStackItem callbackStackItem = (Comparators.Params2.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.comparator = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.comparator = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
		}

		public interface ICallback {

			int compare(Object object, Object object2, Object object3, Object object4);
		}
	}

	public static final class Params1 {

		public static final class CallbackStackItem extends Comparators.Params1.StackItem implements Comparator {
			private Comparators.Params1.ICallback comparator;
			private static final Pool s_pool = new Pool(Comparators.Params1.CallbackStackItem::new);

			public int compare(Object object, Object object2) {
				return this.comparator.compare(object, object2, this.val1);
			}

			public static Comparators.Params1.CallbackStackItem alloc(Object object, Comparators.Params1.ICallback iCallback) {
				Comparators.Params1.CallbackStackItem callbackStackItem = (Comparators.Params1.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.comparator = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.comparator = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
		}

		public interface ICallback {

			int compare(Object object, Object object2, Object object3);
		}
	}
}
