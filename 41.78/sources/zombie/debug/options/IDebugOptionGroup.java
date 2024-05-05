package zombie.debug.options;


public interface IDebugOptionGroup extends IDebugOption {

	Iterable getChildren();

	void addChild(IDebugOption iDebugOption);

	void onChildAdded(IDebugOption iDebugOption);

	void onDescendantAdded(IDebugOption iDebugOption);
}
