package zombie.util.lambda;

import java.util.function.Predicate;
import zombie.util.Pool;
import zombie.util.PooledObject;


public final class Predicates {

	public static final class Params3 {

		public static final class CallbackStackItem extends Predicates.Params3.StackItem implements Predicate {
			private Predicates.Params3.ICallback predicate;
			private static final Pool s_pool = new Pool(Predicates.Params3.CallbackStackItem::new);

			public boolean test(Object object) {
				return this.predicate.test(object, this.val1, this.val2, this.val3);
			}

			public static Predicates.Params3.CallbackStackItem alloc(Object object, Object object2, Object object3, Predicates.Params3.ICallback iCallback) {
				Predicates.Params3.CallbackStackItem callbackStackItem = (Predicates.Params3.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.predicate = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.predicate = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
			Object val3;
		}

		public interface ICallback {

			boolean test(Object object, Object object2, Object object3, Object object4);
		}
	}

	public static final class Params2 {

		public static final class CallbackStackItem extends Predicates.Params2.StackItem implements Predicate {
			private Predicates.Params2.ICallback predicate;
			private static final Pool s_pool = new Pool(Predicates.Params2.CallbackStackItem::new);

			public boolean test(Object object) {
				return this.predicate.test(object, this.val1, this.val2);
			}

			public static Predicates.Params2.CallbackStackItem alloc(Object object, Object object2, Predicates.Params2.ICallback iCallback) {
				Predicates.Params2.CallbackStackItem callbackStackItem = (Predicates.Params2.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.predicate = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.predicate = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
		}

		public interface ICallback {

			boolean test(Object object, Object object2, Object object3);
		}
	}

	public static final class Params1 {

		public static final class CallbackStackItem extends Predicates.Params1.StackItem implements Predicate {
			private Predicates.Params1.ICallback predicate;
			private static final Pool s_pool = new Pool(Predicates.Params1.CallbackStackItem::new);

			public boolean test(Object object) {
				return this.predicate.test(object, this.val1);
			}

			public static Predicates.Params1.CallbackStackItem alloc(Object object, Predicates.Params1.ICallback iCallback) {
				Predicates.Params1.CallbackStackItem callbackStackItem = (Predicates.Params1.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.predicate = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.predicate = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
		}

		public interface ICallback {

			boolean test(Object object, Object object2);
		}
	}
}
