package zombie;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.core.logger.ExceptionLogger;


public final class DebugFileWatcher {
	private final HashMap m_watchedFiles = new HashMap();
	private final HashMap m_watchkeyMapping = new HashMap();
	private final ArrayList m_predicateWatchers = new ArrayList();
	private final ArrayList m_predicateWatchersInvoking = new ArrayList();
	private final FileSystem m_fs = FileSystems.getDefault();
	private WatchService m_watcher;
	private boolean m_predicateWatchersInvokingDirty = true;
	private long m_modificationTime = -1L;
	private final ArrayList m_modifiedFiles = new ArrayList();
	public static final DebugFileWatcher instance = new DebugFileWatcher();

	private DebugFileWatcher() {
	}

	public void init() {
		try {
			this.m_watcher = this.m_fs.newWatchService();
			this.registerDirRecursive(this.m_fs.getPath(ZomboidFileSystem.instance.getMediaRootPath()));
			this.registerDirRecursive(this.m_fs.getPath(ZomboidFileSystem.instance.getMessagingDir()));
		} catch (IOException ioException) {
			this.m_watcher = null;
		}
	}

	private void registerDirRecursive(Path path) {
		try {
			Files.walkFileTree(path, new SimpleFileVisitor(){
				
				public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes var2) {
					DebugFileWatcher.this.registerDir(path);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
			this.m_watcher = null;
		}
	}

	private void registerDir(Path path) {
		try {
			WatchKey watchKey = path.register(this.m_watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
			this.m_watchkeyMapping.put(watchKey, path);
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
			this.m_watcher = null;
		}
	}

	private void addWatchedFile(String string) {
		if (string != null) {
			this.m_watchedFiles.put(this.m_fs.getPath(string), string);
		}
	}

	public void add(PredicatedFileWatcher predicatedFileWatcher) {
		if (!this.m_predicateWatchers.contains(predicatedFileWatcher)) {
			this.addWatchedFile(predicatedFileWatcher.getPath());
			this.m_predicateWatchers.add(predicatedFileWatcher);
			this.m_predicateWatchersInvokingDirty = true;
		}
	}

	public void addDirectory(String string) {
		if (string != null) {
			this.registerDir(this.m_fs.getPath(string));
		}
	}

	public void addDirectoryRecurse(String string) {
		if (string != null) {
			this.registerDirRecursive(this.m_fs.getPath(string));
		}
	}

	public void remove(PredicatedFileWatcher predicatedFileWatcher) {
		this.m_predicateWatchers.remove(predicatedFileWatcher);
	}

	public void update() {
		if (this.m_watcher != null) {
			Iterator iterator;
			for (WatchKey watchKey = this.m_watcher.poll(); watchKey != null; watchKey = this.m_watcher.poll()) {
				try {
					Path path = (Path)this.m_watchkeyMapping.getOrDefault(watchKey, (Object)null);
					iterator = watchKey.pollEvents().iterator();
					while (iterator.hasNext()) {
						WatchEvent watchEvent = (WatchEvent)iterator.next();
						Path path2;
						Path path3;
						String string;
						if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							path2 = (Path)watchEvent.context();
							path3 = path.resolve(path2);
							string = (String)this.m_watchedFiles.getOrDefault(path3, path3.toString());
							this.m_modificationTime = System.currentTimeMillis();
							if (!this.m_modifiedFiles.contains(string)) {
								this.m_modifiedFiles.add(string);
							}
						} else if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
							path2 = (Path)watchEvent.context();
							path3 = path.resolve(path2);
							if (Files.isDirectory(path3, new LinkOption[0])) {
								this.registerDirRecursive(path3);
							} else {
								string = (String)this.m_watchedFiles.getOrDefault(path3, path3.toString());
								this.m_modificationTime = System.currentTimeMillis();
								if (!this.m_modifiedFiles.contains(string)) {
									this.m_modifiedFiles.add(string);
								}
							}
						}
					}
				} finally {
					if (!watchKey.reset()) {
						this.m_watchkeyMapping.remove(watchKey);
					}
				}
			}

			if (!this.m_modifiedFiles.isEmpty()) {
				if (this.m_modificationTime + 2000L <= System.currentTimeMillis()) {
					for (int int1 = this.m_modifiedFiles.size() - 1; int1 >= 0; --int1) {
						String string2 = (String)this.m_modifiedFiles.remove(int1);
						this.swapWatcherArrays();
						iterator = this.m_predicateWatchersInvoking.iterator();
						while (iterator.hasNext()) {
							PredicatedFileWatcher predicatedFileWatcher = (PredicatedFileWatcher)iterator.next();
							predicatedFileWatcher.onModified(string2);
						}
					}
				}
			}
		}
	}

	private void swapWatcherArrays() {
		if (this.m_predicateWatchersInvokingDirty) {
			this.m_predicateWatchersInvoking.clear();
			this.m_predicateWatchersInvoking.addAll(this.m_predicateWatchers);
			this.m_predicateWatchersInvokingDirty = false;
		}
	}
}
