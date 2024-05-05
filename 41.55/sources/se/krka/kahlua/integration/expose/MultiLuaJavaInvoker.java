package se.krka.kahlua.integration.expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;


public class MultiLuaJavaInvoker implements JavaFunction {
	private final List invokers = new ArrayList();
	private static final Comparator COMPARATOR = new Comparator(){
    
    public int compare(LuaJavaInvoker var1, LuaJavaInvoker var2) {
        if (var2.getNumMethodParams() == var1.getNumMethodParams()) {
            boolean var3 = var1.isAllInt();
            boolean var4 = var2.isAllInt();
            return var3 ? 1 : (var4 ? -1 : 0);
        } else {
            return var2.getNumMethodParams() - var1.getNumMethodParams();
        }
    }
};

	public int call(LuaCallFrame luaCallFrame, int int1) {
		MethodArguments methodArguments = null;
		int int2 = this.invokers.size();
		int int3 = -1;
		int int4;
		LuaJavaInvoker luaJavaInvoker;
		boolean boolean1;
		int int5;
		for (int4 = 0; int4 < int2; ++int4) {
			luaJavaInvoker = (LuaJavaInvoker)this.invokers.get(int4);
			if (luaJavaInvoker.matchesArgumentTypes(luaCallFrame, int1)) {
				methodArguments = luaJavaInvoker.prepareCall(luaCallFrame, int1);
				boolean1 = methodArguments.isValid();
				if (boolean1) {
					int5 = luaJavaInvoker.call(methodArguments);
					ReturnValues.put(methodArguments.getReturnValues());
					MethodArguments.put(methodArguments);
					return int5;
				}

				int3 = int4;
				break;
			}
		}

		if (int3 == -1) {
			for (int4 = 0; int4 < int2; ++int4) {
				luaJavaInvoker = (LuaJavaInvoker)this.invokers.get(int4);
				if (luaJavaInvoker.matchesArgumentTypesOrPrimitives(luaCallFrame, int1)) {
					methodArguments = luaJavaInvoker.prepareCall(luaCallFrame, int1);
					boolean1 = methodArguments.isValid();
					if (boolean1) {
						int5 = luaJavaInvoker.call(methodArguments);
						ReturnValues.put(methodArguments.getReturnValues());
						MethodArguments.put(methodArguments);
						return int5;
					}

					int3 = int4;
					break;
				}
			}
		}

		for (int4 = 0; int4 < int2; ++int4) {
			if (int4 != int3) {
				luaJavaInvoker = (LuaJavaInvoker)this.invokers.get(int4);
				methodArguments = luaJavaInvoker.prepareCall(luaCallFrame, int1);
				boolean1 = methodArguments.isValid();
				if (boolean1) {
					int5 = luaJavaInvoker.call(methodArguments);
					ReturnValues.put(methodArguments.getReturnValues());
					MethodArguments.put(methodArguments);
					return int5;
				}

				MethodArguments.put(methodArguments);
			}
		}

		if (methodArguments != null) {
			methodArguments.assertValid();
			MethodArguments.put(methodArguments);
		}

		throw new RuntimeException("No implementation found");
	}

	public void addInvoker(LuaJavaInvoker luaJavaInvoker) {
		if (!this.invokers.contains(luaJavaInvoker)) {
			this.invokers.add(luaJavaInvoker);
			Collections.sort(this.invokers, COMPARATOR);
		}
	}

	public List getInvokers() {
		return this.invokers;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			MultiLuaJavaInvoker multiLuaJavaInvoker = (MultiLuaJavaInvoker)object;
			return this.invokers.equals(multiLuaJavaInvoker.invokers);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.invokers.hashCode();
	}
}
