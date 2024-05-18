package zombie.util.util;

import java.util.NoSuchElementException;
import zombie.util.map.NoSuchMappingException;


public class Exceptions {

	public static void indexOutOfBounds(int int1, int int2, int int3) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Index out of bounds: " + int1 + ", valid range is " + int2 + " to " + int3);
	}

	public static void nullArgument(String string) throws NullPointerException {
		throw new NullPointerException("The specified " + string + " is null");
	}

	public static void negativeArgument(String string, Object object) throws IllegalArgumentException {
		throw new IllegalArgumentException(string + " cannot be negative: " + object);
	}

	public static void negativeOrZeroArgument(String string, Object object) throws IllegalArgumentException {
		throw new IllegalArgumentException(string + " must be a positive value: " + object);
	}

	public static void endOfIterator() throws NoSuchElementException {
		throw new NoSuchElementException("Attempt to iterate past iterator\'s last element.");
	}

	public static void startOfIterator() throws NoSuchElementException {
		throw new NoSuchElementException("Attempt to iterate past iterator\'s first element.");
	}

	public static void noElementToRemove() throws IllegalStateException {
		throw new IllegalStateException("Attempt to remove element from iterator that has no current element.");
	}

	public static void noElementToGet() throws IllegalStateException {
		throw new IllegalStateException("Attempt to get element from iterator that has no current element. Call next() first.");
	}

	public static void noElementToSet() throws IllegalStateException {
		throw new IllegalStateException("Attempt to set element in iterator that has no current element.");
	}

	public static void noLastElement() throws IllegalStateException {
		throw new IllegalStateException("No value to return. Call containsKey() first.");
	}

	public static void noSuchMapping(Object object) throws NoSuchMappingException {
		throw new NoSuchMappingException("No such key in map: " + String.valueOf(object));
	}

	public static void dequeNoFirst() throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Attempt to get first element of empty deque");
	}

	public static void dequeNoLast() throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Attempt to get last element of empty deque");
	}

	public static void dequeNoFirstToRemove() throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Attempt to remove last element of empty deque");
	}

	public static void dequeNoLastToRemove() throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Attempt to remove last element of empty deque");
	}

	public static void nullElementNotAllowed() throws IllegalArgumentException {
		throw new IllegalArgumentException("Attempt to add a null value to an adapted primitive set.");
	}

	public static void cannotAdapt(String string) throws IllegalStateException {
		throw new IllegalStateException("The " + string + " contains values preventing it from being adapted to a primitive " + string);
	}

	public static void unsupported(String string) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Attempt to invoke unsupported operation: " + string);
	}

	public static void unmodifiable(String string) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Attempt to modify unmodifiable " + string);
	}

	public static void cloning() throws RuntimeException {
		throw new RuntimeException("Clone is not supported");
	}

	public static void invalidRangeBounds(Object object, Object object2) throws IllegalArgumentException {
		throw new IllegalArgumentException("First (" + object + ") cannot be greater than last (" + object2 + ")");
	}

	public static void cannotMergeRanges(Object object, Object object2) throws IllegalArgumentException {
		throw new IllegalArgumentException("Ranges cannot be merged: " + object.toString() + " and " + object2.toString());
	}

	public static void setNoFirst() throws NoSuchElementException {
		throw new NoSuchElementException("Attempt to get first element of empty set");
	}

	public static void setNoLast() throws NoSuchElementException {
		throw new NoSuchElementException("Attempt to get last element of empty set");
	}

	public static void invalidSetBounds(Object object, Object object2) throws IllegalArgumentException {
		throw new IllegalArgumentException("Lower bound (" + object + ") cannot be greater than upper bound (" + object2 + ")");
	}

	public static void valueNotInSubRange(Object object) throws IllegalArgumentException {
		throw new IllegalArgumentException("Attempt to add a value outside valid range: " + object);
	}

	public static void invalidUpperBound(Object object) throws IllegalArgumentException {
		throw new IllegalArgumentException("Upper bound is not in valid sub-range: " + object);
	}

	public static void invalidLowerBound(Object object) throws IllegalArgumentException {
		throw new IllegalArgumentException("Lower bound is not in valid sub-range: " + object);
	}
}
