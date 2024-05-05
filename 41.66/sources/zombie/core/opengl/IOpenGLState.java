package zombie.core.opengl;


public abstract class IOpenGLState {
	protected IOpenGLState.Value currentValue = this.defaultValue();
	private boolean dirty = true;

	public void set(IOpenGLState.Value value) {
		if (this.dirty || !value.equals(this.currentValue)) {
			this.setCurrentValue(value);
			this.Set(value);
		}
	}

	void setCurrentValue(IOpenGLState.Value value) {
		this.dirty = false;
		this.currentValue.set(value);
	}

	public void setDirty() {
		this.dirty = true;
	}

	IOpenGLState.Value getCurrentValue() {
		return this.currentValue;
	}

	abstract IOpenGLState.Value defaultValue();

	abstract void Set(IOpenGLState.Value value);

	public interface Value {

		IOpenGLState.Value set(IOpenGLState.Value value);
	}
}
