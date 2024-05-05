package zombie.util.lambda;

import java.util.function.Consumer;
import zombie.util.Pool;
import zombie.util.PooledObject;


public final class Consumers {

	public static final class Params5 {

		public static final class CallbackStackItem extends Consumers.Params5.StackItem implements Consumer {
			private Consumers.Params5.ICallback consumer;
			private static final Pool s_pool = new Pool(Consumers.Params5.CallbackStackItem::new);

			public void accept(Object object) {
				this.consumer.accept(object, this.val1, this.val2, this.val3, this.val4, this.val5);
			}

			public static Consumers.Params5.CallbackStackItem alloc(Object object, Object object2, Object object3, Object object4, Object object5, Consumers.Params5.ICallback iCallback) {
				Consumers.Params5.CallbackStackItem callbackStackItem = (Consumers.Params5.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.val4 = object4;
				callbackStackItem.val5 = object5;
				callbackStackItem.consumer = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.val4 = null;
				this.val5 = null;
				this.consumer = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
			Object val3;
			Object val4;
			Object val5;
		}

		public interface ICallback {

			void accept(Object object, Object object2, Object object3, Object object4, Object object5, Object object6);
		}
	}

	public static final class Params4 {

		public static final class CallbackStackItem extends Consumers.Params4.StackItem implements Consumer {
			private Consumers.Params4.ICallback consumer;
			private static final Pool s_pool = new Pool(Consumers.Params4.CallbackStackItem::new);

			public void accept(Object object) {
				this.consumer.accept(object, this.val1, this.val2, this.val3, this.val4);
			}

			public static Consumers.Params4.CallbackStackItem alloc(Object object, Object object2, Object object3, Object object4, Consumers.Params4.ICallback iCallback) {
				Consumers.Params4.CallbackStackItem callbackStackItem = (Consumers.Params4.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.val4 = object4;
				callbackStackItem.consumer = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.val4 = null;
				this.consumer = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
			Object val3;
			Object val4;
		}

		public interface ICallback {

			void accept(Object object, Object object2, Object object3, Object object4, Object object5);
		}
	}

	public static final class Params3 {

		public static final class CallbackStackItem extends Consumers.Params3.StackItem implements Consumer {
			private Consumers.Params3.ICallback consumer;
			private static final Pool s_pool = new Pool(Consumers.Params3.CallbackStackItem::new);

			public void accept(Object object) {
				this.consumer.accept(object, this.val1, this.val2, this.val3);
			}

			public static Consumers.Params3.CallbackStackItem alloc(Object object, Object object2, Object object3, Consumers.Params3.ICallback iCallback) {
				Consumers.Params3.CallbackStackItem callbackStackItem = (Consumers.Params3.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.consumer = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.consumer = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
			Object val3;
		}

		public interface ICallback {

			void accept(Object object, Object object2, Object object3, Object object4);
		}
	}

	public static class Params2 {

		public static final class CallbackStackItem extends Consumers.Params2.StackItem implements Consumer {
			private Consumers.Params2.ICallback consumer;
			private static final Pool s_pool = new Pool(Consumers.Params2.CallbackStackItem::new);

			public void accept(Object object) {
				this.consumer.accept(object, this.val1, this.val2);
			}

			public static Consumers.Params2.CallbackStackItem alloc(Object object, Object object2, Consumers.Params2.ICallback iCallback) {
				Consumers.Params2.CallbackStackItem callbackStackItem = (Consumers.Params2.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.consumer = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.consumer = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
			Object val2;
		}

		public interface ICallback {

			void accept(Object object, Object object2, Object object3);
		}
	}

	public static final class Params1 {

		public static final class CallbackStackItem extends Consumers.Params1.StackItem implements Consumer {
			private Consumers.Params1.ICallback consumer;
			private static final Pool s_pool = new Pool(Consumers.Params1.CallbackStackItem::new);

			public void accept(Object object) {
				this.consumer.accept(object, this.val1);
			}

			public static Consumers.Params1.CallbackStackItem alloc(Object object, Consumers.Params1.ICallback iCallback) {
				Consumers.Params1.CallbackStackItem callbackStackItem = (Consumers.Params1.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.consumer = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.consumer = null;
			}
		}

		private static class StackItem extends PooledObject {
			Object val1;
		}

		public interface ICallback {

			void accept(Object object, Object object2);
		}
	}
}
