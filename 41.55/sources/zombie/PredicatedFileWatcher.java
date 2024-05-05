package zombie;

import java.io.File;
import java.util.function.Predicate;
import zombie.debug.DebugLog;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;


public final class PredicatedFileWatcher {
	private final String m_path;
	private final Predicate m_predicate;
	private final PredicatedFileWatcher.IPredicatedFileWatcherCallback m_callback;

	public PredicatedFileWatcher(Predicate predicate, PredicatedFileWatcher.IPredicatedFileWatcherCallback iPredicatedFileWatcherCallback) {
		this((String)null, (Predicate)predicate, (PredicatedFileWatcher.IPredicatedFileWatcherCallback)iPredicatedFileWatcherCallback);
	}

	public PredicatedFileWatcher(String string, PredicatedFileWatcher.IPredicatedFileWatcherCallback iPredicatedFileWatcherCallback) {
		this(string, (Predicate)null, (PredicatedFileWatcher.IPredicatedFileWatcherCallback)iPredicatedFileWatcherCallback);
	}

	public PredicatedFileWatcher(String string, Class javaClass, PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback iPredicatedDataPacketFileWatcherCallback) {
		this(string, (Predicate)null, (PredicatedFileWatcher.IPredicatedFileWatcherCallback)(new PredicatedFileWatcher.GenericPredicatedFileWatcherCallback(javaClass, iPredicatedDataPacketFileWatcherCallback)));
	}

	public PredicatedFileWatcher(String string, Predicate predicate, PredicatedFileWatcher.IPredicatedFileWatcherCallback iPredicatedFileWatcherCallback) {
		this.m_path = this.processPath(string);
		this.m_predicate = predicate != null ? predicate : this::pathsEqual;
		this.m_callback = iPredicatedFileWatcherCallback;
	}

	public String getPath() {
		return this.m_path;
	}

	private String processPath(String string) {
		return string != null ? ZomboidFileSystem.processFilePath(string, File.separatorChar) : null;
	}

	private boolean pathsEqual(String string) {
		return string.equals(this.m_path);
	}

	public void onModified(String string) {
		if (this.m_predicate.test(string)) {
			this.m_callback.call(string);
		}
	}

	public interface IPredicatedFileWatcherCallback {

		void call(String string);
	}

	public static class GenericPredicatedFileWatcherCallback implements PredicatedFileWatcher.IPredicatedFileWatcherCallback {
		private final Class m_class;
		private final PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback m_callback;

		public GenericPredicatedFileWatcherCallback(Class javaClass, PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback iPredicatedDataPacketFileWatcherCallback) {
			this.m_class = javaClass;
			this.m_callback = iPredicatedDataPacketFileWatcherCallback;
		}

		public void call(String string) {
			Object object;
			try {
				object = PZXmlUtil.parse(this.m_class, string);
			} catch (PZXmlParserException pZXmlParserException) {
				DebugLog.General.error("Exception thrown. " + pZXmlParserException);
				return;
			}

			this.m_callback.call(object);
		}
	}

	public interface IPredicatedDataPacketFileWatcherCallback {

		void call(Object object);
	}
}
