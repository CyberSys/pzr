package se.krka.kahlua.integration.expose;

import java.util.Iterator;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;


public class IterableExposer {

	@LuaMethod(global = true)
	public Object iter(Iterable iterable) {
		final Iterator iterator = iterable.iterator();
		return new JavaFunction(){
			
			public int call(LuaCallFrame iterable, int iteratorx) {
				return !iterator.hasNext() ? 0 : iterable.push(iterator.next());
			}
		};
	}
}
