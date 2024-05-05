package zombie.debug;



public enum LogSeverity {

	Trace,
	Debug,
	General,
	Warning,
	Error;

	private static LogSeverity[] $values() {
		return new LogSeverity[]{Trace, Debug, General, Warning, Error};
	}
}
