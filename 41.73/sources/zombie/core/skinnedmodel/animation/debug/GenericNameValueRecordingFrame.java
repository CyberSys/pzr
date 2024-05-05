package zombie.core.skinnedmodel.animation.debug;

import java.io.PrintStream;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.util.list.PZArrayUtil;


public abstract class GenericNameValueRecordingFrame {
	protected String[] m_columnNames = new String[0];
	protected final HashMap m_nameIndices = new HashMap();
	protected boolean m_headerDirty = false;
	protected final String m_fileKey;
	protected PrintStream m_outHeader = null;
	protected PrintStream m_outValues = null;
	private String m_headerFilePath = null;
	private String m_valuesFilePath = null;
	protected int m_frameNumber = -1;
	protected static final String delim = ",";
	protected final String m_valuesFileNameSuffix;
	private String m_previousLine = null;
	private int m_previousFrameNo = -1;
	protected final StringBuilder m_lineBuffer = new StringBuilder();

	public GenericNameValueRecordingFrame(String string, String string2) {
		this.m_fileKey = string;
		this.m_valuesFileNameSuffix = string2;
	}

	protected int addColumnInternal(String string) {
		int int1 = this.m_columnNames.length;
		this.m_columnNames = (String[])PZArrayUtil.add(this.m_columnNames, string);
		this.m_nameIndices.put(string, int1);
		this.m_headerDirty = true;
		this.onColumnAdded();
		return int1;
	}

	public int getOrCreateColumn(String string) {
		return this.m_nameIndices.containsKey(string) ? (Integer)this.m_nameIndices.get(string) : this.addColumnInternal(string);
	}

	public void setFrameNumber(int int1) {
		this.m_frameNumber = int1;
	}

	public int getColumnCount() {
		return this.m_columnNames.length;
	}

	public String getNameAt(int int1) {
		return this.m_columnNames[int1];
	}

	public abstract String getValueAt(int int1);

	protected void openHeader(boolean boolean1) {
		this.m_outHeader = AnimationPlayerRecorder.openFileStream(this.m_fileKey + "_header", boolean1, (boolean1x)->{
			this.m_headerFilePath = boolean1x;
		});
	}

	protected void openValuesFile(boolean boolean1) {
		this.m_outValues = AnimationPlayerRecorder.openFileStream(this.m_fileKey + this.m_valuesFileNameSuffix, boolean1, (boolean1x)->{
			this.m_valuesFilePath = boolean1x;
		});
	}

	public void writeLine() {
		if (this.m_headerDirty || this.m_outHeader == null) {
			this.m_headerDirty = false;
			this.writeHeader();
		}

		this.writeData();
	}

	public void close() {
		if (this.m_outHeader != null) {
			this.m_outHeader.close();
			this.m_outHeader = null;
		}

		if (this.m_outValues != null) {
			this.m_outValues.close();
			this.m_outValues = null;
		}
	}

	public void closeAndDiscard() {
		this.close();
		ZomboidFileSystem.instance.tryDeleteFile(this.m_headerFilePath);
		this.m_headerFilePath = null;
		ZomboidFileSystem.instance.tryDeleteFile(this.m_valuesFilePath);
		this.m_valuesFilePath = null;
	}

	protected abstract void onColumnAdded();

	public abstract void reset();

	protected void writeHeader() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("frameNo");
		this.writeHeader(stringBuilder);
		this.openHeader(false);
		this.m_outHeader.println(stringBuilder);
	}

	protected void writeHeader(StringBuilder stringBuilder) {
		int int1 = 0;
		for (int int2 = this.getColumnCount(); int1 < int2; ++int1) {
			appendCell(stringBuilder, this.getNameAt(int1));
		}
	}

	protected void writeData() {
		if (this.m_outValues == null) {
			this.openValuesFile(false);
		}

		StringBuilder stringBuilder = this.m_lineBuffer;
		stringBuilder.setLength(0);
		this.writeData(stringBuilder);
		if (this.m_previousLine == null || !this.m_previousLine.contentEquals(stringBuilder)) {
			this.m_outValues.print(this.m_frameNumber);
			this.m_outValues.println(stringBuilder);
			this.m_previousLine = stringBuilder.toString();
			this.m_previousFrameNo = this.m_frameNumber;
		}
	}

	protected void writeData(StringBuilder stringBuilder) {
		int int1 = 0;
		for (int int2 = this.getColumnCount(); int1 < int2; ++int1) {
			appendCell(stringBuilder, this.getValueAt(int1));
		}
	}

	public static StringBuilder appendCell(StringBuilder stringBuilder) {
		return stringBuilder.append(",");
	}

	public static StringBuilder appendCell(StringBuilder stringBuilder, String string) {
		return stringBuilder.append(",").append(string);
	}

	public static StringBuilder appendCell(StringBuilder stringBuilder, float float1) {
		return stringBuilder.append(",").append(float1);
	}

	public static StringBuilder appendCell(StringBuilder stringBuilder, int int1) {
		return stringBuilder.append(",").append(int1);
	}

	public static StringBuilder appendCell(StringBuilder stringBuilder, long long1) {
		return stringBuilder.append(",").append(long1);
	}

	public static StringBuilder appendCellQuot(StringBuilder stringBuilder, String string) {
		return stringBuilder.append(",").append('\"').append(string).append('\"');
	}
}
