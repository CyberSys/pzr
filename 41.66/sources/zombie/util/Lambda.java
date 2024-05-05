package zombie.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import zombie.util.lambda.Comparators;
import zombie.util.lambda.Consumers;
import zombie.util.lambda.IntSupplierFunction;
import zombie.util.lambda.Invokers;
import zombie.util.lambda.Predicates;
import zombie.util.lambda.ReturnValueContainer;
import zombie.util.lambda.ReturnValueContainerPrimitives;
import zombie.util.lambda.Stacks;


public final class Lambda {

	public static Predicate predicate(Object object, Predicates.Params1.ICallback iCallback) {
		return Predicates.Params1.CallbackStackItem.alloc(object, iCallback);
	}

	public static Predicate predicate(Object object, Object object2, Predicates.Params2.ICallback iCallback) {
		return Predicates.Params2.CallbackStackItem.alloc(object, object2, iCallback);
	}

	public static Predicate predicate(Object object, Object object2, Object object3, Predicates.Params3.ICallback iCallback) {
		return Predicates.Params3.CallbackStackItem.alloc(object, object2, object3, iCallback);
	}

	public static Comparator comparator(Object object, Comparators.Params1.ICallback iCallback) {
		return Comparators.Params1.CallbackStackItem.alloc(object, iCallback);
	}

	public static Comparator comparator(Object object, Object object2, Comparators.Params2.ICallback iCallback) {
		return Comparators.Params2.CallbackStackItem.alloc(object, object2, iCallback);
	}

	public static Consumer consumer(Object object, Consumers.Params1.ICallback iCallback) {
		return Consumers.Params1.CallbackStackItem.alloc(object, iCallback);
	}

	public static Consumer consumer(Object object, Object object2, Consumers.Params2.ICallback iCallback) {
		return Consumers.Params2.CallbackStackItem.alloc(object, object2, iCallback);
	}

	public static Consumer consumer(Object object, Object object2, Object object3, Consumers.Params3.ICallback iCallback) {
		return Consumers.Params3.CallbackStackItem.alloc(object, object2, object3, iCallback);
	}

	public static Consumer consumer(Object object, Object object2, Object object3, Object object4, Consumers.Params4.ICallback iCallback) {
		return Consumers.Params4.CallbackStackItem.alloc(object, object2, object3, object4, iCallback);
	}

	public static Consumer consumer(Object object, Object object2, Object object3, Object object4, Object object5, Consumers.Params5.ICallback iCallback) {
		return Consumers.Params5.CallbackStackItem.alloc(object, object2, object3, object4, object5, iCallback);
	}

	public static Runnable invoker(Object object, Invokers.Params1.ICallback iCallback) {
		return Invokers.Params1.CallbackStackItem.alloc(object, iCallback);
	}

	public static Runnable invoker(Object object, Object object2, Invokers.Params2.ICallback iCallback) {
		return Invokers.Params2.CallbackStackItem.alloc(object, object2, iCallback);
	}

	public static Runnable invoker(Object object, Object object2, Object object3, Invokers.Params3.ICallback iCallback) {
		return Invokers.Params3.CallbackStackItem.alloc(object, object2, object3, iCallback);
	}

	public static Runnable invoker(Object object, Object object2, Object object3, Object object4, Invokers.Params4.ICallback iCallback) {
		return Invokers.Params4.CallbackStackItem.alloc(object, object2, object3, object4, iCallback);
	}

	public static void capture(Object object, Stacks.Params1.ICallback iCallback) {
		Stacks.Params1.CallbackStackItem callbackStackItem = Stacks.Params1.CallbackStackItem.alloc(object, iCallback);
		callbackStackItem.invokeAndRelease();
	}

	public static void capture(Object object, Object object2, Stacks.Params2.ICallback iCallback) {
		Stacks.Params2.CallbackStackItem callbackStackItem = Stacks.Params2.CallbackStackItem.alloc(object, object2, iCallback);
		callbackStackItem.invokeAndRelease();
	}

	public static void capture(Object object, Object object2, Object object3, Stacks.Params3.ICallback iCallback) {
		Stacks.Params3.CallbackStackItem callbackStackItem = Stacks.Params3.CallbackStackItem.alloc(object, object2, object3, iCallback);
		callbackStackItem.invokeAndRelease();
	}

	public static void capture(Object object, Object object2, Object object3, Object object4, Stacks.Params4.ICallback iCallback) {
		Stacks.Params4.CallbackStackItem callbackStackItem = Stacks.Params4.CallbackStackItem.alloc(object, object2, object3, object4, iCallback);
		callbackStackItem.invokeAndRelease();
	}

	public static void capture(Object object, Object object2, Object object3, Object object4, Object object5, Stacks.Params5.ICallback iCallback) {
		Stacks.Params5.CallbackStackItem callbackStackItem = Stacks.Params5.CallbackStackItem.alloc(object, object2, object3, object4, object5, iCallback);
		callbackStackItem.invokeAndRelease();
	}

	public static void capture(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Stacks.Params6.ICallback iCallback) {
		Stacks.Params6.CallbackStackItem callbackStackItem = Stacks.Params6.CallbackStackItem.alloc(object, object2, object3, object4, object5, object6, iCallback);
		callbackStackItem.invokeAndRelease();
	}

	public static void forEach(Consumer consumer, Object object, Consumers.Params1.ICallback iCallback) {
		capture(consumer, object, iCallback, (consumerx,objectx,iCallbackx,var3)->{
			objectx.accept(consumerx.consumer(iCallbackx, var3));
		});
	}

	public static void forEach(Consumer consumer, Object object, Object object2, Consumers.Params2.ICallback iCallback) {
		capture(consumer, object, object2, iCallback, (consumerx,objectx,object2x,iCallbackx,var4)->{
			objectx.accept(consumerx.consumer(object2x, iCallbackx, var4));
		});
	}

	public static void forEachFrom(BiConsumer biConsumer, List list, Object object, Consumers.Params1.ICallback iCallback) {
		capture(biConsumer, list, object, iCallback, (biConsumerx,listx,objectx,iCallbackx,var4)->{
			listx.accept(objectx, biConsumerx.consumer(iCallbackx, var4));
		});
	}

	public static void forEachFrom(BiConsumer biConsumer, List list, Object object, Object object2, Consumers.Params2.ICallback iCallback) {
		capture(biConsumer, list, object, object2, iCallback, (biConsumerx,listx,objectx,object2x,iCallbackx,var5)->{
			listx.accept(objectx, biConsumerx.consumer(object2x, iCallbackx, var5));
		});
	}

	public static void forEachFrom(BiConsumer biConsumer, Object object, Object object2, Consumers.Params1.ICallback iCallback) {
		capture(biConsumer, object, object2, iCallback, (biConsumerx,objectx,object2x,iCallbackx,var4)->{
			objectx.accept(object2x, biConsumerx.consumer(iCallbackx, var4));
		});
	}

	public static void forEachFrom(BiConsumer biConsumer, Object object, Object object2, Object object3, Consumers.Params2.ICallback iCallback) {
		capture(biConsumer, object, object2, object3, iCallback, (biConsumerx,objectx,object2x,object3x,iCallbackx,var5)->{
			objectx.accept(object2x, biConsumerx.consumer(object3x, iCallbackx, var5));
		});
	}

	public static Object find(Function function, Object object, Predicates.Params1.ICallback iCallback) {
		ReturnValueContainer returnValueContainer = ReturnValueContainer.alloc();
		capture(function, object, iCallback, returnValueContainer, (functionx,objectx,iCallbackx,returnValueContainerx,object2x)->{
			object2x.ReturnVal = objectx.apply(functionx.predicate(iCallbackx, returnValueContainerx));
		});
		Object object2 = returnValueContainer.ReturnVal;
		returnValueContainer.release();
		return object2;
	}

	public static int indexOf(IntSupplierFunction intSupplierFunction, Object object, Predicates.Params1.ICallback iCallback) {
		ReturnValueContainerPrimitives.RVInt rVInt = ReturnValueContainerPrimitives.RVInt.alloc();
		capture(intSupplierFunction, object, iCallback, rVInt, (intSupplierFunctionx,objectx,iCallbackx,rVIntx,int1x)->{
			int1x.ReturnVal = objectx.getInt(intSupplierFunctionx.predicate(iCallbackx, rVIntx));
		});
		int int1 = rVInt.ReturnVal;
		rVInt.release();
		return int1;
	}

	public static boolean contains(Predicate predicate, Object object, Predicates.Params1.ICallback iCallback) {
		ReturnValueContainerPrimitives.RVBoolean rVBoolean = ReturnValueContainerPrimitives.RVBoolean.alloc();
		capture(predicate, object, iCallback, rVBoolean, (predicatex,objectx,iCallbackx,rVBooleanx,Boolean1x)->{
			Boolean1x.ReturnVal = objectx.test(predicatex.predicate(iCallbackx, rVBooleanx));
		});
		Boolean Boolean1 = rVBoolean.ReturnVal;
		rVBoolean.release();
		return Boolean1;
	}

	public static boolean containsFrom(BiPredicate biPredicate, Iterable iterable, Object object, Predicates.Params1.ICallback iCallback) {
		ReturnValueContainerPrimitives.RVBoolean rVBoolean = ReturnValueContainerPrimitives.RVBoolean.alloc();
		capture(biPredicate, iterable, object, iCallback, rVBoolean, (biPredicatex,iterablex,objectx,iCallbackx,rVBooleanx,Boolean1x)->{
			Boolean1x.ReturnVal = iterablex.test(objectx, biPredicatex.predicate(iCallbackx, rVBooleanx));
		});
		Boolean Boolean1 = rVBoolean.ReturnVal;
		rVBoolean.release();
		return Boolean1;
	}

	public static void invoke(Consumer consumer, Object object, Invokers.Params1.ICallback iCallback) {
		capture(consumer, object, iCallback, (consumerx,objectx,iCallbackx,var3)->{
			objectx.accept(consumerx.invoker(iCallbackx, var3));
		});
	}

	public static void invoke(Consumer consumer, Object object, Object object2, Invokers.Params2.ICallback iCallback) {
		capture(consumer, object, object2, iCallback, (consumerx,objectx,object2x,iCallbackx,var4)->{
			objectx.accept(consumerx.invoker(object2x, iCallbackx, var4));
		});
	}
}
