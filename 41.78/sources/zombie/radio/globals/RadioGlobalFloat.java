package zombie.radio.globals;


public final class RadioGlobalFloat extends RadioGlobal {

	public RadioGlobalFloat(float float1) {
		super(float1, RadioGlobalType.Float);
	}

	public RadioGlobalFloat(String string, float float1) {
		super(string, float1, RadioGlobalType.Float);
	}

	public float getValue() {
		return (Float)this.value;
	}

	public void setValue(float float1) {
		this.value = float1;
	}

	public String getString() {
		return ((Float)this.value).toString();
	}

	public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
		if (radioGlobal instanceof RadioGlobalFloat) {
			RadioGlobalFloat radioGlobalFloat = (RadioGlobalFloat)radioGlobal;
			switch (compareMethod) {
			case equals: 
				return (Float)this.value == radioGlobalFloat.getValue() ? CompareResult.True : CompareResult.False;
			
			case notequals: 
				return (Float)this.value != radioGlobalFloat.getValue() ? CompareResult.True : CompareResult.False;
			
			case lessthan: 
				return (Float)this.value < radioGlobalFloat.getValue() ? CompareResult.True : CompareResult.False;
			
			case morethan: 
				return (Float)this.value > radioGlobalFloat.getValue() ? CompareResult.True : CompareResult.False;
			
			case lessthanorequals: 
				return (Float)this.value <= radioGlobalFloat.getValue() ? CompareResult.True : CompareResult.False;
			
			case morethanorequals: 
				return (Float)this.value >= radioGlobalFloat.getValue() ? CompareResult.True : CompareResult.False;
			
			default: 
				return CompareResult.Invalid;
			
			}
		} else {
			return CompareResult.Invalid;
		}
	}

	public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
		if (radioGlobal instanceof RadioGlobalFloat) {
			RadioGlobalFloat radioGlobalFloat = (RadioGlobalFloat)radioGlobal;
			switch (editGlobalOps) {
			case set: 
				this.value = radioGlobalFloat.getValue();
				return true;
			
			case add: 
				this.value = (Float)this.value + radioGlobalFloat.getValue();
				return true;
			
			case sub: 
				this.value = (Float)this.value - radioGlobalFloat.getValue();
				return true;
			
			}
		}

		return false;
	}
}
