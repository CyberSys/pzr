package zombie.radio.globals;


public class RadioGlobalBool extends RadioGlobal {

	public RadioGlobalBool(boolean boolean1) {
		super(boolean1, RadioGlobalType.Boolean);
	}

	public RadioGlobalBool(String string, boolean boolean1) {
		super(string, boolean1, RadioGlobalType.Boolean);
	}

	public boolean getValue() {
		return (Boolean)this.value;
	}

	public void setValue(boolean boolean1) {
		this.value = boolean1;
	}

	public String getString() {
		return ((Boolean)this.value).toString();
	}

	public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
		if (radioGlobal instanceof RadioGlobalBool) {
			RadioGlobalBool radioGlobalBool = (RadioGlobalBool)radioGlobal;
			switch (compareMethod) {
			case equals: 
				return ((Boolean)this.value).equals(radioGlobalBool.getValue()) ? CompareResult.True : CompareResult.False;
			
			case notequals: 
				return !((Boolean)this.value).equals(radioGlobalBool.getValue()) ? CompareResult.True : CompareResult.False;
			
			default: 
				return CompareResult.Invalid;
			
			}
		} else {
			return CompareResult.Invalid;
		}
	}

	public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
		if (editGlobalOps.equals(EditGlobalOps.set) && radioGlobal instanceof RadioGlobalBool) {
			this.value = ((RadioGlobalBool)radioGlobal).getValue();
			return true;
		} else {
			return false;
		}
	}
}
