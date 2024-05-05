package zombie.radio.globals;


public final class RadioGlobalString extends RadioGlobal {

	public RadioGlobalString(String string) {
		super(string, RadioGlobalType.String);
	}

	public RadioGlobalString(String string, String string2) {
		super(string, string2, RadioGlobalType.String);
	}

	public String getValue() {
		return (String)this.value;
	}

	public void setValue(String string) {
		this.value = string;
	}

	public String getString() {
		return (String)this.value;
	}

	public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
		if (radioGlobal instanceof RadioGlobalString) {
			RadioGlobalString radioGlobalString = (RadioGlobalString)radioGlobal;
			switch (compareMethod) {
			case equals: 
				return ((String)this.value).equals(radioGlobalString.getValue()) ? CompareResult.True : CompareResult.False;
			
			case notequals: 
				return !((String)this.value).equals(radioGlobalString.getValue()) ? CompareResult.True : CompareResult.False;
			
			default: 
				return CompareResult.Invalid;
			
			}
		} else {
			return CompareResult.Invalid;
		}
	}

	public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
		if (editGlobalOps.equals(EditGlobalOps.set) && radioGlobal instanceof RadioGlobalString) {
			this.value = ((RadioGlobalString)radioGlobal).getValue();
			return true;
		} else {
			return false;
		}
	}
}
