package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public final class MemoryFileDevice implements IFileDevice {

	public IFile createFile(IFile iFile) {
		return new MemoryFileDevice.MemoryFile(iFile, this);
	}

	public void destroyFile(IFile iFile) {
	}

	public InputStream createStream(String string, InputStream inputStream) throws IOException {
		return null;
	}

	public void destroyStream(InputStream inputStream) {
	}

	public String name() {
		return "memory";
	}

	private static class MemoryFile implements IFile {
		final MemoryFileDevice m_device;
		byte[] m_buffer;
		long m_size;
		long m_pos;
		IFile m_file;
		boolean m_write;

		MemoryFile(IFile iFile, MemoryFileDevice memoryFileDevice) {
			this.m_device = memoryFileDevice;
			this.m_buffer = null;
			this.m_size = 0L;
			this.m_pos = 0L;
			this.m_file = iFile;
			this.m_write = false;
		}

		public boolean open(String string, int int1) {
			assert this.m_buffer == null;
			this.m_write = (int1 & 2) != 0;
			if (this.m_file != null) {
				if (this.m_file.open(string, int1)) {
					if ((int1 & 1) != 0) {
						this.m_size = this.m_file.size();
						this.m_buffer = new byte[(int)this.m_size];
						this.m_file.read(this.m_buffer, this.m_size);
						this.m_pos = 0L;
					}

					return true;
				}
			} else if ((int1 & 2) != 0) {
				return true;
			}

			return false;
		}

		public void close() {
			if (this.m_file != null) {
				if (this.m_write) {
					this.m_file.seek(FileSeekMode.BEGIN, 0L);
					this.m_file.write(this.m_buffer, this.m_size);
				}

				this.m_file.close();
			}

			this.m_buffer = null;
		}

		public boolean read(byte[] byteArray, long long1) {
			long long2 = this.m_pos + long1 < this.m_size ? long1 : this.m_size - this.m_pos;
			System.arraycopy(this.m_buffer, (int)this.m_pos, byteArray, 0, (int)long2);
			this.m_pos += long2;
			return false;
		}

		public boolean write(byte[] byteArray, long long1) {
			long long2 = this.m_pos;
			long long3 = (long)this.m_buffer.length;
			long long4 = this.m_size;
			if (long2 + long1 > long3) {
				long long5 = Math.max(long3 * 2L, long2 + long1);
				this.m_buffer = Arrays.copyOf(this.m_buffer, (int)long5);
			}

			System.arraycopy(byteArray, 0, this.m_buffer, (int)long2, (int)long1);
			this.m_pos += long1;
			this.m_size = long2 + long1 > long4 ? long2 + long1 : long4;
			return true;
		}

		public byte[] getBuffer() {
			return this.m_buffer;
		}

		public long size() {
			return this.m_size;
		}

		public boolean seek(FileSeekMode fileSeekMode, long long1) {
			switch (fileSeekMode) {
			case BEGIN: 
				assert long1 <= this.m_size;
				this.m_pos = long1;
				break;
			
			case CURRENT: 
				assert 0L <= this.m_pos + long1 && this.m_pos + long1 <= this.m_size;
				this.m_pos += long1;
				break;
			
			case END: 
				assert long1 <= this.m_size;
				this.m_pos = this.m_size - long1;
			
			}
			boolean boolean1 = this.m_pos <= this.m_size;
			this.m_pos = Math.min(this.m_pos, this.m_size);
			return boolean1;
		}

		public long pos() {
			return this.m_pos;
		}

		public InputStream getInputStream() {
			return this.m_file != null ? this.m_file.getInputStream() : null;
		}

		public IFileDevice getDevice() {
			return this.m_device;
		}

		public void release() {
			this.m_buffer = null;
		}
	}
}
