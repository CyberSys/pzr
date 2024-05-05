package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import zombie.GameWindow;
import zombie.core.logger.ExceptionLogger;
import zombie.gameStates.GameLoadingState;


public final class FileSystemImpl extends FileSystem {
	private final ArrayList m_devices = new ArrayList();
	private final ArrayList m_in_progress = new ArrayList();
	private final ArrayList m_pending = new ArrayList();
	private int m_last_id = 0;
	private DiskFileDevice m_disk_device = new DiskFileDevice("disk");
	private MemoryFileDevice m_memory_device = new MemoryFileDevice();
	private final HashMap m_texturepack_devices = new HashMap();
	private final HashMap m_texturepack_devicelists = new HashMap();
	private DeviceList m_default_device = new DeviceList();
	private final ExecutorService executor;
	private final AtomicBoolean lock = new AtomicBoolean(false);
	private final ArrayList m_added = new ArrayList();
	public static final HashMap TexturePackCompression = new HashMap();

	public FileSystemImpl() {
		this.m_default_device.add(this.m_disk_device);
		this.m_default_device.add(this.m_memory_device);
		int int1 = Runtime.getRuntime().availableProcessors() <= 4 ? 2 : 4;
		this.executor = Executors.newFixedThreadPool(int1);
	}

	public boolean mount(IFileDevice iFileDevice) {
		return true;
	}

	public boolean unMount(IFileDevice iFileDevice) {
		return this.m_devices.remove(iFileDevice);
	}

	public IFile open(DeviceList deviceList, String string, int int1) {
		IFile iFile = deviceList.createFile();
		if (iFile != null) {
			if (iFile.open(string, int1)) {
				return iFile;
			} else {
				iFile.release();
				return null;
			}
		} else {
			return null;
		}
	}

	public void close(IFile iFile) {
		iFile.close();
		iFile.release();
	}

	public int openAsync(DeviceList deviceList, String string, int int1, IFileTask2Callback iFileTask2Callback) {
		IFile iFile = deviceList.createFile();
		if (iFile != null) {
			FileSystemImpl.OpenTask openTask = new FileSystemImpl.OpenTask(this);
			openTask.m_file = iFile;
			openTask.m_path = string;
			openTask.m_mode = int1;
			openTask.m_cb = iFileTask2Callback;
			return this.runAsync((FileTask)openTask);
		} else {
			return -1;
		}
	}

	public void closeAsync(IFile iFile, IFileTask2Callback iFileTask2Callback) {
		FileSystemImpl.CloseTask closeTask = new FileSystemImpl.CloseTask(this);
		closeTask.m_file = iFile;
		closeTask.m_cb = iFileTask2Callback;
		this.runAsync((FileTask)closeTask);
	}

	public void cancelAsync(int int1) {
		if (int1 != -1) {
			int int2;
			FileSystemImpl.AsyncItem asyncItem;
			for (int2 = 0; int2 < this.m_pending.size(); ++int2) {
				asyncItem = (FileSystemImpl.AsyncItem)this.m_pending.get(int2);
				if (asyncItem.m_id == int1) {
					asyncItem.m_future.cancel(false);
					return;
				}
			}

			for (int2 = 0; int2 < this.m_in_progress.size(); ++int2) {
				asyncItem = (FileSystemImpl.AsyncItem)this.m_in_progress.get(int2);
				if (asyncItem.m_id == int1) {
					asyncItem.m_future.cancel(false);
					return;
				}
			}

			while (!this.lock.compareAndSet(false, true)) {
				Thread.onSpinWait();
			}

			for (int2 = 0; int2 < this.m_added.size(); ++int2) {
				asyncItem = (FileSystemImpl.AsyncItem)this.m_added.get(int2);
				if (asyncItem.m_id == int1) {
					asyncItem.m_future.cancel(false);
					break;
				}
			}

			this.lock.set(false);
		}
	}

	public InputStream openStream(DeviceList deviceList, String string) throws IOException {
		return deviceList.createStream(string);
	}

	public void closeStream(InputStream inputStream) {
	}

	private int runAsync(FileSystemImpl.AsyncItem asyncItem) {
		Thread thread = Thread.currentThread();
		if (thread != GameWindow.GameThread && thread != GameLoadingState.loader) {
			boolean boolean1 = true;
		}

		while (!this.lock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		asyncItem.m_id = this.m_last_id++;
		if (this.m_last_id < 0) {
			this.m_last_id = 0;
		}

		this.m_added.add(asyncItem);
		this.lock.set(false);
		return asyncItem.m_id;
	}

	public int runAsync(FileTask fileTask) {
		FileSystemImpl.AsyncItem asyncItem = new FileSystemImpl.AsyncItem();
		asyncItem.m_task = fileTask;
		asyncItem.m_future = new FutureTask(fileTask);
		return this.runAsync(asyncItem);
	}

	public void updateAsyncTransactions() {
		int int1 = Math.min(this.m_in_progress.size(), 16);
		int int2;
		FileSystemImpl.AsyncItem asyncItem;
		for (int2 = 0; int2 < int1; ++int2) {
			asyncItem = (FileSystemImpl.AsyncItem)this.m_in_progress.get(int2);
			if (asyncItem.m_future.isDone()) {
				this.m_in_progress.remove(int2--);
				--int1;
				if (asyncItem.m_future.isCancelled()) {
					boolean boolean1 = true;
				} else {
					Object object = null;
					try {
						object = asyncItem.m_future.get();
					} catch (Throwable throwable) {
						ExceptionLogger.logException(throwable, asyncItem.m_task.getErrorMessage());
					}

					asyncItem.m_task.handleResult(object);
				}

				asyncItem.m_task.done();
				asyncItem.m_task = null;
				asyncItem.m_future = null;
			}
		}

		while (!this.lock.compareAndSet(false, true)) {
			Thread.onSpinWait();
		}

		boolean boolean2 = true;
		if (boolean2) {
			for (int int3 = 0; int3 < this.m_added.size(); ++int3) {
				FileSystemImpl.AsyncItem asyncItem2 = (FileSystemImpl.AsyncItem)this.m_added.get(int3);
				int int4 = this.m_pending.size();
				for (int int5 = 0; int5 < this.m_pending.size(); ++int5) {
					FileSystemImpl.AsyncItem asyncItem3 = (FileSystemImpl.AsyncItem)this.m_pending.get(int5);
					if (asyncItem2.m_task.m_priority > asyncItem3.m_task.m_priority) {
						int4 = int5;
						break;
					}
				}

				this.m_pending.add(int4, asyncItem2);
			}
		} else {
			this.m_pending.addAll(this.m_added);
		}

		this.m_added.clear();
		this.lock.set(false);
		int2 = 16 - this.m_in_progress.size();
		while (int2 > 0 && !this.m_pending.isEmpty()) {
			asyncItem = (FileSystemImpl.AsyncItem)this.m_pending.remove(0);
			if (!asyncItem.m_future.isCancelled()) {
				this.m_in_progress.add(asyncItem);
				this.executor.submit(asyncItem.m_future);
				--int2;
			}
		}
	}

	public boolean hasWork() {
		if (this.m_pending.isEmpty() && this.m_in_progress.isEmpty()) {
			while (!this.lock.compareAndSet(false, true)) {
				Thread.onSpinWait();
			}

			boolean boolean1 = !this.m_added.isEmpty();
			this.lock.set(false);
			return boolean1;
		} else {
			return true;
		}
	}

	public DeviceList getDefaultDevice() {
		return this.m_default_device;
	}

	public void mountTexturePack(String string, FileSystem.TexturePackTextures texturePackTextures, int int1) {
		TexturePackDevice texturePackDevice = new TexturePackDevice(string, int1);
		if (texturePackTextures != null) {
			try {
				texturePackDevice.getSubTextureInfo(texturePackTextures);
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			}
		}

		this.m_texturepack_devices.put(string, texturePackDevice);
		DeviceList deviceList = new DeviceList();
		deviceList.add(texturePackDevice);
		this.m_texturepack_devicelists.put(texturePackDevice.name(), deviceList);
	}

	public DeviceList getTexturePackDevice(String string) {
		return (DeviceList)this.m_texturepack_devicelists.get(string);
	}

	public int getTexturePackFlags(String string) {
		return ((TexturePackDevice)this.m_texturepack_devices.get(string)).getTextureFlags();
	}

	public boolean getTexturePackAlpha(String string, String string2) {
		return ((TexturePackDevice)this.m_texturepack_devices.get(string)).isAlpha(string2);
	}

	private static final class OpenTask extends FileTask {
		IFile m_file;
		String m_path;
		int m_mode;
		IFileTask2Callback m_cb;

		OpenTask(FileSystem fileSystem) {
			super(fileSystem);
		}

		public Object call() throws Exception {
			return this.m_file.open(this.m_path, this.m_mode);
		}

		public void handleResult(Object object) {
			if (this.m_cb != null) {
				this.m_cb.onFileTaskFinished(this.m_file, object);
			}
		}

		public void done() {
			if ((this.m_mode & 5) == 5) {
				this.m_file_system.closeAsync(this.m_file, (IFileTask2Callback)null);
			}

			this.m_file = null;
			this.m_path = null;
			this.m_cb = null;
		}
	}

	private static final class CloseTask extends FileTask {
		IFile m_file;
		IFileTask2Callback m_cb;

		CloseTask(FileSystem fileSystem) {
			super(fileSystem);
		}

		public Object call() throws Exception {
			this.m_file.close();
			this.m_file.release();
			return null;
		}

		public void handleResult(Object object) {
			if (this.m_cb != null) {
				this.m_cb.onFileTaskFinished(this.m_file, object);
			}
		}

		public void done() {
			this.m_file = null;
			this.m_cb = null;
		}
	}

	private static final class AsyncItem {
		int m_id;
		FileTask m_task;
		FutureTask m_future;
	}
}
