package zombie.util.lambda;

import zombie.util.Pool;
import zombie.util.PooledObject;


public class Invokers {

	public static final class Params4 {

		public static final class CallbackStackItem extends Invokers.Params4.StackItem implements Runnable {
			private Invokers.Params4.ICallback invoker;
			private static final Pool s_pool = new Pool(Invokers.Params4.CallbackStackItem::new);

			public void run() {
				this.invoker.accept(this.val1, this.val2, this.val3, this.val4);
			}

			public static Invokers.Params4.CallbackStackItem alloc(Object object, Object object2, Object object3, Object object4, Invokers.Params4.ICallback iCallback) {
				Invokers.Params4.CallbackStackItem callbackStackItem = (Invokers.Params4.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.val4 = object4;
				callbackStackItem.invoker = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.val4 = null;
				this.invoker = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
			Object val3;
			Object val4;
		}

		public interface ICallback {

			void accept(Object object, Object object2, Object object3, Object object4);
		}
	}

	public static final class Params3 {

		public static final class CallbackStackItem extends Invokers.Params3.StackItem implements Runnable {
			private Invokers.Params3.ICallback invoker;
			private static final Pool s_pool = new Pool(Invokers.Params3.CallbackStackItem::new);

			public void run() {
				this.invoker.accept(this.val1, this.val2, this.val3);
			}

			public static Invokers.Params3.CallbackStackItem alloc(Object object, Object object2, Object object3, Invokers.Params3.ICallback iCallback) {
				Invokers.Params3.CallbackStackItem callbackStackItem = (Invokers.Params3.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.invoker = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.invoker = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
			Object val3;
		}

		public interface ICallback {

			void accept(Object object, Object object2, Object object3);
		}
	}

	public static final class Params2 {

		public static final class CallbackStackItem extends Invokers.Params2.StackItem implements Runnable {
			private Invokers.Params2.ICallback invoker;
			private static final Pool s_pool = new Pool(Invokers.Params2.CallbackStackItem::new);

			public void run() {
				this.invoker.accept(this.val1, this.val2);
			}

			public static Invokers.Params2.CallbackStackItem alloc(Object object, Object object2, Invokers.Params2.ICallback iCallback) {
				Invokers.Params2.CallbackStackItem callbackStackItem = (Invokers.Params2.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.invoker = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.invoker = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
		}

		public interface ICallback {

			void accept(Object object, Object object2);
		}
	}

	public static final class Params1 {

		public static final class CallbackStackItem extends Invokers.Params1.StackItem implements Runnable {
			private Invokers.Params1.ICallback invoker;
			private static final Pool s_pool = new Pool(Invokers.Params1.CallbackStackItem::new);

			public void run() {
				this.invoker.accept(this.val1);
			}

			public static Invokers.Params1.CallbackStackItem alloc(Object object, Invokers.Params1.ICallback iCallback) {
				Invokers.Params1.CallbackStackItem callbackStackItem = (Invokers.Params1.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.invoker = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.invoker = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
		}

		public interface ICallback {

			void accept(Object object);
		}
	}
}
