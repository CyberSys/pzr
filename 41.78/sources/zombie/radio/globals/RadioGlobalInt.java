package zombie.radio.globals;


public final class RadioGlobalInt extends RadioGlobal {

	public RadioGlobalInt(int int1) {
		super(int1, RadioGlobalType.Integer);
	}

	public RadioGlobalInt(String string, int int1) {
		super(string, int1, RadioGlobalType.Integer);
	}

	public int getValue() {
		return (Integer)this.value;
	}

	public void setValue(int int1) {
		this.value = int1;
	}

	public String getString() {
		return ((Integer)this.value).toString();
	}

	public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
		if (radioGlobal instanceof RadioGlobalInt) {
			RadioGlobalInt radioGlobalInt = (RadioGlobalInt)radioGlobal;
			switch (compareMethod) {
			case equals: 
				return (Integer)this.value == radioGlobalInt.getValue() ? CompareResult.True : CompareResult.False;
			
			case notequals: 
				return (Integer)this.value != radioGlobalInt.getValue() ? CompareResult.True : CompareResult.False;
			
			case lessthan: 
				return (Integer)this.value < radioGlobalInt.getValue() ? CompareResult.True : CompareResult.False;
			
			case morethan: 
				return (Integer)this.value > radioGlobalInt.getValue() ? CompareResult.True : CompareResult.False;
			
			case lessthanorequals: 
				return (Integer)this.value <= radioGlobalInt.getValue() ? CompareResult.True : CompareResult.False;
			
			case morethanorequals: 
				return (Integer)this.value >= radioGlobalInt.getValue() ? CompareResult.True : CompareResult.False;
			
			default: 
				return CompareResult.Invalid;
			
			}
		} else {
			return CompareResult.Invalid;
		}
	}

	public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
		if (radioGlobal instanceof RadioGlobalInt) {
			RadioGlobalInt radioGlobalInt = (RadioGlobalInt)radioGlobal;
			switch (editGlobalOps) {
			case set: 
				this.value = radioGlobalInt.getValue();
				return true;
			
			case add: 
				this.value = (Integer)this.value + radioGlobalInt.getValue();
				return true;
			
			case sub: 
				this.value = (Integer)this.value - radioGlobalInt.getValue();
				return true;
			
			}
		}

		return false;
	}
}
