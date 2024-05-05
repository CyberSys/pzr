package zombie.radio.globals;



public enum EditGlobalEvent {

	OnSetActive,
	OnPostDelay,
	OnPlayerListens,
	OnPlayerListensOnce,
	OnBroadcastSetActive,
	OnBroadcastRemove,
	OnExit;

	private static EditGlobalEvent[] $values() {
		return new EditGlobalEvent[]{OnSetActive, OnPostDelay, OnPlayerListens, OnPlayerListensOnce, OnBroadcastSetActive, OnBroadcastRemove, OnExit};
	}
}
