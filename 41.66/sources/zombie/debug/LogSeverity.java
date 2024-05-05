package zombie.debug;



public enum LogSeverity {

	Trace,
	General,
	Warning,
	Error;

	private static LogSeverity[] $values() {
		return new LogSeverity[]{Trace, General, Warning, Error};
	}
}
