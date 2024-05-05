package zombie.util.lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import zombie.util.IPooledObject;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.PooledObject;


public final class Stacks {

	public static final class Params6 {

		public static final class CallbackStackItem extends Stacks.Params6.StackItem {
			private Stacks.Params6.ICallback callback;
			private static final Pool s_pool = new Pool(Stacks.Params6.CallbackStackItem::new);

			public void invoke() {
				this.callback.accept(this, this.val1, this.val2, this.val3, this.val4, this.val5, this.val6);
			}

			public static Stacks.Params6.CallbackStackItem alloc(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Stacks.Params6.ICallback iCallback) {
				Stacks.Params6.CallbackStackItem callbackStackItem = (Stacks.Params6.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.val4 = object4;
				callbackStackItem.val5 = object5;
				callbackStackItem.val6 = object6;
				callbackStackItem.callback = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.val4 = null;
				this.val5 = null;
				this.val6 = null;
				this.callback = null;
				super.onReleased();
			}
		}

		private abstract static class StackItem extends Stacks.GenericStack {
			Object val1;
			Object val2;
			Object val3;
			Object val4;
			Object val5;
			Object val6;
		}

		public interface ICallback {

			void accept(Stacks.GenericStack genericStack, Object object, Object object2, Object object3, Object object4, Object object5, Object object6);
		}
	}

	public static final class Params5 {

		public static final class CallbackStackItem extends Stacks.Params5.StackItem {
			private Stacks.Params5.ICallback callback;
			private static final Pool s_pool = new Pool(Stacks.Params5.CallbackStackItem::new);

			public void invoke() {
				this.callback.accept(this, this.val1, this.val2, this.val3, this.val4, this.val5);
			}

			public static Stacks.Params5.CallbackStackItem alloc(Object object, Object object2, Object object3, Object object4, Object object5, Stacks.Params5.ICallback iCallback) {
				Stacks.Params5.CallbackStackItem callbackStackItem = (Stacks.Params5.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.val4 = object4;
				callbackStackItem.val5 = object5;
				callbackStackItem.callback = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.val4 = null;
				this.val5 = null;
				this.callback = null;
				super.onReleased();
			}
		}

		private abstract static class StackItem extends Stacks.GenericStack {
			Object val1;
			Object val2;
			Object val3;
			Object val4;
			Object val5;
		}

		public interface ICallback {

			void accept(Stacks.GenericStack genericStack, Object object, Object object2, Object object3, Object object4, Object object5);
		}
	}

	public static final class Params4 {

		public static final class CallbackStackItem extends Stacks.Params4.StackItem {
			private Stacks.Params4.ICallback callback;
			private static final Pool s_pool = new Pool(Stacks.Params4.CallbackStackItem::new);

			public void invoke() {
				this.callback.accept(this, this.val1, this.val2, this.val3, this.val4);
			}

			public static Stacks.Params4.CallbackStackItem alloc(Object object, Object object2, Object object3, Object object4, Stacks.Params4.ICallback iCallback) {
				Stacks.Params4.CallbackStackItem callbackStackItem = (Stacks.Params4.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.val4 = object4;
				callbackStackItem.callback = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.val4 = null;
				this.callback = null;
				super.onReleased();
			}
		}

		private abstract static class StackItem extends Stacks.GenericStack {
			Object val1;
			Object val2;
			Object val3;
			Object val4;
		}

		public interface ICallback {

			void accept(Stacks.GenericStack genericStack, Object object, Object object2, Object object3, Object object4);
		}
	}

	public static final class Params3 {

		public static final class CallbackStackItem extends Stacks.Params3.StackItem {
			private Stacks.Params3.ICallback callback;
			private static final Pool s_pool = new Pool(Stacks.Params3.CallbackStackItem::new);

			public void invoke() {
				this.callback.accept(this, this.val1, this.val2, this.val3);
			}

			public static Stacks.Params3.CallbackStackItem alloc(Object object, Object object2, Object object3, Stacks.Params3.ICallback iCallback) {
				Stacks.Params3.CallbackStackItem callbackStackItem = (Stacks.Params3.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.val3 = object3;
				callbackStackItem.callback = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.val3 = null;
				this.callback = null;
				super.onReleased();
			}
		}

		private abstract static class StackItem extends Stacks.GenericStack {
			Object val1;
			Object val2;
			Object val3;
		}

		public interface ICallback {

			void accept(Stacks.GenericStack genericStack, Object object, Object object2, Object object3);
		}
	}

	public static final class Params2 {

		public static final class CallbackStackItem extends Stacks.Params2.StackItem {
			private Stacks.Params2.ICallback callback;
			private static final Pool s_pool = new Pool(Stacks.Params2.CallbackStackItem::new);

			public void invoke() {
				this.callback.accept(this, this.val1, this.val2);
			}

			public static Stacks.Params2.CallbackStackItem alloc(Object object, Object object2, Stacks.Params2.ICallback iCallback) {
				Stacks.Params2.CallbackStackItem callbackStackItem = (Stacks.Params2.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.val2 = object2;
				callbackStackItem.callback = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.val2 = null;
				this.callback = null;
				super.onReleased();
			}
		}

		private abstract static class StackItem extends Stacks.GenericStack {
			Object val1;
			Object val2;
		}

		public interface ICallback {

			void accept(Stacks.GenericStack genericStack, Object object, Object object2);
		}
	}

	public static final class Params1 {

		public static final class CallbackStackItem extends Stacks.Params1.StackItem {
			private Stacks.Params1.ICallback callback;
			private static final Pool s_pool = new Pool(Stacks.Params1.CallbackStackItem::new);

			public void invoke() {
				this.callback.accept(this, this.val1);
			}

			public static Stacks.Params1.CallbackStackItem alloc(Object object, Stacks.Params1.ICallback iCallback) {
				Stacks.Params1.CallbackStackItem callbackStackItem = (Stacks.Params1.CallbackStackItem)s_pool.alloc();
				callbackStackItem.val1 = object;
				callbackStackItem.callback = iCallback;
				return callbackStackItem;
			}

			public void onReleased() {
				this.val1 = null;
				this.callback = null;
				super.onReleased();
			}
		}

		private abstract static class StackItem extends Stacks.GenericStack {
			Object val1;
		}

		public interface ICallback {

			void accept(Stacks.GenericStack genericStack, Object object);
		}
	}

	public abstract static class GenericStack extends PooledObject {
		private final List m_stackItems = new ArrayList();

		public abstract void invoke();

		public void invokeAndRelease() {
			try {
				this.invoke();
			} finally {
				this.release();
			}
		}

		private Object push(Object object) {
			this.m_stackItems.add((IPooledObject)object);
			return object;
		}

		public void onReleased() {
			this.m_stackItems.forEach(Pool::tryRelease);
			this.m_stackItems.clear();
		}

		public Predicate predicate(Object object, Predicates.Params1.ICallback iCallback) {
			return (Predicate)this.push(Lambda.predicate(object, iCallback));
		}

		public Predicate predicate(Object object, Object object2, Predicates.Params2.ICallback iCallback) {
			return (Predicate)this.push(Lambda.predicate(object, object2, iCallback));
		}

		public Predicate predicate(Object object, Object object2, Object object3, Predicates.Params3.ICallback iCallback) {
			return (Predicate)this.push(Lambda.predicate(object, object2, object3, iCallback));
		}

		public Comparator comparator(Object object, Comparators.Params1.ICallback iCallback) {
			return (Comparator)this.push(Lambda.comparator(object, iCallback));
		}

		public Comparator comparator(Object object, Object object2, Comparators.Params2.ICallback iCallback) {
			return (Comparator)this.push(Lambda.comparator(object, object2, iCallback));
		}

		public Consumer consumer(Object object, Consumers.Params1.ICallback iCallback) {
			return (Consumer)this.push(Lambda.consumer(object, iCallback));
		}

		public Consumer consumer(Object object, Object object2, Consumers.Params2.ICallback iCallback) {
			return (Consumer)this.push(Lambda.consumer(object, object2, iCallback));
		}

		public Runnable invoker(Object object, Invokers.Params1.ICallback iCallback) {
			return (Runnable)this.push(Lambda.invoker(object, iCallback));
		}

		public Runnable invoker(Object object, Object object2, Invokers.Params2.ICallback iCallback) {
			return (Runnable)this.push(Lambda.invoker(object, object2, iCallback));
		}

		public Runnable invoker(Object object, Object object2, Object object3, Invokers.Params3.ICallback iCallback) {
			return (Runnable)this.push(Lambda.invoker(object, object2, object3, iCallback));
		}

		public Runnable invoker(Object object, Object object2, Object object3, Object object4, Invokers.Params4.ICallback iCallback) {
			return (Runnable)this.push(Lambda.invoker(object, object2, object3, object4, iCallback));
		}
	}
}
