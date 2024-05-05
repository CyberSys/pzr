package se.krka.kahlua.integration;

import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.vm.KahluaThread;


public class LuaCaller {
	private final KahluaConverterManager converterManager;

	public LuaCaller(KahluaConverterManager kahluaConverterManager) {
		this.converterManager = kahluaConverterManager;
	}

	public void pcallvoid(KahluaThread kahluaThread, Object object, Object object2) {
		kahluaThread.pcallvoid(object, object2);
	}

	public void pcallvoid(KahluaThread kahluaThread, Object object, Object object2, Object object3) {
		kahluaThread.pcallvoid(object, object2, object3);
	}

	public void pcallvoid(KahluaThread kahluaThread, Object object, Object object2, Object object3, Object object4) {
		kahluaThread.pcallvoid(object, object2, object3, object4);
	}

	public Boolean pcallBoolean(KahluaThread kahluaThread, Object object, Object object2, Object object3) {
		return kahluaThread.pcallBoolean(object, object2, object3);
	}

	public Boolean pcallBoolean(KahluaThread kahluaThread, Object object, Object object2, Object object3, Object object4) {
		return kahluaThread.pcallBoolean(object, object2, object3, object4);
	}

	public void pcallvoid(KahluaThread kahluaThread, Object object, Object[] objectArray) {
		if (objectArray != null) {
			for (int int1 = objectArray.length - 1; int1 >= 0; --int1) {
				objectArray[int1] = this.converterManager.fromJavaToLua(objectArray[int1]);
			}
		}

		kahluaThread.pcallvoid(object, objectArray);
	}

	public Object[] pcall(KahluaThread kahluaThread, Object object, Object[] objectArray) {
		if (objectArray != null) {
			for (int int1 = objectArray.length - 1; int1 >= 0; --int1) {
				objectArray[int1] = this.converterManager.fromJavaToLua(objectArray[int1]);
			}
		}

		Object[] objectArray2 = kahluaThread.pcall(object, objectArray);
		return objectArray2;
	}

	public Object[] pcall(KahluaThread kahluaThread, Object object, Object object2) {
		if (object2 != null) {
			object2 = this.converterManager.fromJavaToLua(object2);
		}

		Object[] objectArray = kahluaThread.pcall(object, new Object[]{object2});
		return objectArray;
	}

	public Boolean protectedCallBoolean(KahluaThread kahluaThread, Object object, Object object2) {
		object2 = this.converterManager.fromJavaToLua(object2);
		return kahluaThread.pcallBoolean(object, object2);
	}

	public Boolean protectedCallBoolean(KahluaThread kahluaThread, Object object, Object object2, Object object3) {
		object2 = this.converterManager.fromJavaToLua(object2);
		object3 = this.converterManager.fromJavaToLua(object3);
		return kahluaThread.pcallBoolean(object, object2, object3);
	}

	public Boolean protectedCallBoolean(KahluaThread kahluaThread, Object object, Object object2, Object object3, Object object4) {
		object2 = this.converterManager.fromJavaToLua(object2);
		object3 = this.converterManager.fromJavaToLua(object3);
		object4 = this.converterManager.fromJavaToLua(object4);
		return kahluaThread.pcallBoolean(object, object2, object3, object4);
	}

	public Boolean pcallBoolean(KahluaThread kahluaThread, Object object, Object[] objectArray) {
		if (objectArray != null) {
			for (int int1 = objectArray.length - 1; int1 >= 0; --int1) {
				objectArray[int1] = this.converterManager.fromJavaToLua(objectArray[int1]);
			}
		}

		return kahluaThread.pcallBoolean(object, objectArray);
	}

	public LuaReturn protectedCall(KahluaThread kahluaThread, Object object, Object[] objectArray) {
		return LuaReturn.createReturn(this.pcall(kahluaThread, object, objectArray));
	}

	public void protectedCallVoid(KahluaThread kahluaThread, Object object, Object object2) {
		object2 = this.converterManager.fromJavaToLua(object2);
		kahluaThread.pcallvoid(object, object2);
	}

	public void protectedCallVoid(KahluaThread kahluaThread, Object object, Object object2, Object object3) {
		object2 = this.converterManager.fromJavaToLua(object2);
		object3 = this.converterManager.fromJavaToLua(object3);
		kahluaThread.pcallvoid(object, object2, object3);
	}

	public void protectedCallVoid(KahluaThread kahluaThread, Object object, Object object2, Object object3, Object object4) {
		object2 = this.converterManager.fromJavaToLua(object2);
		object3 = this.converterManager.fromJavaToLua(object3);
		object4 = this.converterManager.fromJavaToLua(object4);
		kahluaThread.pcallvoid(object, object2, object3, object4);
	}

	public void protectedCallVoid(KahluaThread kahluaThread, Object object, Object[] objectArray) {
		this.pcallvoid(kahluaThread, object, objectArray);
	}

	public Boolean protectedCallBoolean(KahluaThread kahluaThread, Object object, Object[] objectArray) {
		return this.pcallBoolean(kahluaThread, object, objectArray);
	}
}
