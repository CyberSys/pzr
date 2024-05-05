package zombie.fileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import zombie.core.logger.ExceptionLogger;


public final class DiskFileDevice implements IFileDevice {
	private final String m_name;

	public DiskFileDevice(String string) {
		this.m_name = string;
	}

	public IFile createFile(IFile iFile) {
		return new DiskFileDevice.DiskFile(iFile, this);
	}

	public void destroyFile(IFile iFile) {
	}

	public InputStream createStream(String string, InputStream inputStream) throws IOException {
		return new FileInputStream(string);
	}

	public void destroyStream(InputStream inputStream) {
	}

	public String name() {
		return this.m_name;
	}

	private static final class DiskFile implements IFile {
		final DiskFileDevice m_device;
		RandomAccessFile m_file;
		InputStream m_inputStream;
		final IFile m_fallthrough;
		boolean m_use_fallthrough;

		DiskFile(IFile iFile, DiskFileDevice diskFileDevice) {
			this.m_device = diskFileDevice;
			this.m_fallthrough = iFile;
			this.m_use_fallthrough = false;
		}

		public boolean open(String string, int int1) {
			File file = new File(string);
			boolean boolean1 = (int1 & 1) != 0;
			if (boolean1 && !file.exists() && this.m_fallthrough != null) {
				this.m_use_fallthrough = true;
				return this.m_fallthrough.open(string, int1);
			} else {
				try {
					if ((int1 & 16) == 0) {
						this.m_file = new RandomAccessFile(string, FileOpenMode.toStringMode(int1));
					} else {
						this.m_inputStream = new FileInputStream(string);
					}

					return true;
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
					return false;
				}
			}
		}

		public void close() {
			if (this.m_fallthrough != null) {
				this.m_fallthrough.close();
			}

			if (this.m_file != null || this.m_inputStream != null) {
				try {
					if (this.m_file != null) {
						this.m_file.close();
					}

					if (this.m_inputStream != null) {
						this.m_inputStream.close();
					}
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
				}

				this.m_file = null;
				this.m_inputStream = null;
				this.m_use_fallthrough = false;
			}
		}

		public boolean read(byte[] byteArray, long long1) {
			if (this.m_use_fallthrough) {
				return this.m_fallthrough.read(byteArray, long1);
			} else if (this.m_file == null) {
				return false;
			} else {
				try {
					return (long)this.m_file.read(byteArray, 0, (int)long1) == long1;
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
					return false;
				}
			}
		}

		public boolean write(byte[] byteArray, long long1) {
			if (this.m_use_fallthrough) {
				return this.m_fallthrough.write(byteArray, long1);
			} else if (this.m_file == null) {
				return false;
			} else {
				try {
					this.m_file.write(byteArray, 0, (int)long1);
					return true;
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
					return false;
				}
			}
		}

		public byte[] getBuffer() {
			return this.m_use_fallthrough ? this.m_fallthrough.getBuffer() : null;
		}

		public long size() {
			if (this.m_use_fallthrough) {
				return this.m_fallthrough.size();
			} else if (this.m_file == null) {
				return 0L;
			} else {
				try {
					return this.m_file.length();
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
					return 0L;
				}
			}
		}

		public boolean seek(FileSeekMode fileSeekMode, long long1) {
			if (this.m_use_fallthrough) {
				return this.m_fallthrough.seek(fileSeekMode, long1);
			} else if (this.m_file == null) {
				return false;
			} else {
				try {
					switch (fileSeekMode) {
					case BEGIN: 
					
					default: 
						break;
					
					case CURRENT: 
						long1 += this.m_file.getFilePointer();
						break;
					
					case END: 
						long1 += this.m_file.length();
					
					}

					this.m_file.seek(long1);
					return true;
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
					return false;
				}
			}
		}

		public long pos() {
			if (this.m_use_fallthrough) {
				return this.m_fallthrough.pos();
			} else if (this.m_file == null) {
				return 0L;
			} else {
				try {
					return this.m_file.getFilePointer();
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
					return 0L;
				}
			}
		}

		public InputStream getInputStream() {
			return this.m_inputStream;
		}

		public IFileDevice getDevice() {
			return this.m_device;
		}

		public void release() {
			this.getDevice().destroyFile(this);
		}
	}
}
