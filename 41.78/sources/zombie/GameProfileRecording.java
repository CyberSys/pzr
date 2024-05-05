package zombie;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.animation.debug.GenericNameValueRecordingFrame;
import zombie.util.IPooledObject;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.list.PZArrayUtil;


public final class GameProfileRecording extends GenericNameValueRecordingFrame {
	private long m_startTime;
	private final GameProfileRecording.Row m_rootRow = new GameProfileRecording.Row();
	private final HashMap m_keyValueTable = new HashMap();
	protected PrintStream m_outSegment = null;
	private long m_firstFrameNo = -1L;
	private final List m_segmentFilePaths = new ArrayList();
	private int m_numFramesPerFile = 60;
	private int m_currentSegmentFrameCount = 0;

	public GameProfileRecording(String string) {
		super(string, "_times");
		this.addColumnInternal("StartTime");
		this.addColumnInternal("EndTime");
		this.addColumnInternal("SegmentNo");
		this.addColumnInternal("Spans");
		this.addColumnInternal("key");
		this.addColumnInternal("Depth");
		this.addColumnInternal("StartTime");
		this.addColumnInternal("EndTime");
		this.addColumnInternal("Time Format");
		this.addColumnInternal("x * 100ns");
	}

	public void setNumFramesPerSegment(int int1) {
		this.m_numFramesPerFile = int1;
	}

	public void setStartTime(long long1) {
		this.m_startTime = long1;
	}

	public void logTimeSpan(GameProfiler.ProfileArea profileArea) {
		if (this.m_firstFrameNo == -1L) {
			this.m_firstFrameNo = (long)this.m_frameNumber;
		}

		GameProfileRecording.Span span = this.allocSpan(profileArea);
		GameProfileRecording.Row row = this.m_rootRow;
		if (row.Spans.isEmpty()) {
			row.StartTime = span.StartTime;
		}

		row.EndTime = span.EndTime;
		row.Spans.add(span);
	}

	protected GameProfileRecording.Span allocSpan(GameProfiler.ProfileArea profileArea) {
		int int1 = this.getOrCreateKey(profileArea.Key);
		long long1 = profileArea.StartTime - this.m_startTime;
		long long2 = profileArea.EndTime - this.m_startTime;
		GameProfileRecording.Span span = GameProfileRecording.Span.alloc();
		span.key = int1;
		span.Depth = profileArea.Depth;
		span.StartTime = long1;
		span.EndTime = long2;
		int int2 = 0;
		for (int int3 = profileArea.Children.size(); int2 < int3; ++int2) {
			GameProfiler.ProfileArea profileArea2 = (GameProfiler.ProfileArea)profileArea.Children.get(int2);
			GameProfileRecording.Span span2 = this.allocSpan(profileArea2);
			span.Children.add(span2);
		}

		return span;
	}

	private int getOrCreateKey(String string) {
		Integer integer = (Integer)this.m_keyValueTable.get(string);
		if (integer == null) {
			integer = this.m_keyValueTable.size();
			this.m_keyValueTable.put(string, integer);
			this.m_headerDirty = true;
		}

		return integer;
	}

	public String getValueAt(int int1) {
		throw new RuntimeException("Not implemented. Use getValueAt(row, col)");
	}

	protected void onColumnAdded() {
	}

	public void reset() {
		this.m_rootRow.reset();
	}

	protected void openSegmentFile(boolean boolean1) {
		if (this.m_outSegment != null) {
			this.m_outSegment.flush();
			this.m_outSegment.close();
		}

		String string = String.format("%s%s_%04d", this.m_fileKey, this.m_valuesFileNameSuffix, this.m_segmentFilePaths.size());
		List list = this.m_segmentFilePaths;
		Objects.requireNonNull(list);
		this.m_outSegment = AnimationPlayerRecorder.openFileStream(string, boolean1, list::add);
		this.m_currentSegmentFrameCount = 0;
		this.m_headerDirty = true;
	}

	public void close() {
		if (this.m_outSegment != null) {
			this.m_outSegment.close();
			this.m_outSegment = null;
		}
	}

	public void closeAndDiscard() {
		super.closeAndDiscard();
		List list = this.m_segmentFilePaths;
		ZomboidFileSystem zomboidFileSystem = ZomboidFileSystem.instance;
		Objects.requireNonNull(zomboidFileSystem);
		PZArrayUtil.forEach(list, zomboidFileSystem::tryDeleteFile);
		this.m_segmentFilePaths.clear();
	}

	protected void writeData() {
		if (this.m_outValues == null) {
			this.openValuesFile(false);
		}

		StringBuilder stringBuilder = this.m_lineBuffer;
		stringBuilder.setLength(0);
		++this.m_currentSegmentFrameCount;
		if (this.m_outSegment == null || this.m_currentSegmentFrameCount >= this.m_numFramesPerFile) {
			this.openSegmentFile(false);
		}

		this.writeDataRow(stringBuilder, this.m_rootRow);
		this.m_outSegment.print(this.m_frameNumber);
		this.m_outSegment.println(stringBuilder);
		stringBuilder = this.m_lineBuffer;
		stringBuilder.setLength(0);
		this.writeFrameTimeRow(stringBuilder, this.m_rootRow, this.m_segmentFilePaths.size() - 1);
		this.m_outValues.print(this.m_frameNumber);
		this.m_outValues.println(stringBuilder);
	}

	private void writeDataRow(StringBuilder stringBuilder, GameProfileRecording.Row row) {
		int int1 = 0;
		for (int int2 = row.Spans.size(); int1 < int2; ++int1) {
			GameProfileRecording.Span span = (GameProfileRecording.Span)row.Spans.get(int1);
			this.writeSpan(stringBuilder, row, span);
		}
	}

	private void writeFrameTimeRow(StringBuilder stringBuilder, GameProfileRecording.Row row, int int1) {
		appendCell(stringBuilder, row.StartTime / 100L);
		appendCell(stringBuilder, row.EndTime / 100L);
		appendCell(stringBuilder, int1);
	}

	private void writeSpan(StringBuilder stringBuilder, GameProfileRecording.Row row, GameProfileRecording.Span span) {
		long long1 = (span.StartTime - row.StartTime) / 100L;
		long long2 = (span.EndTime - span.StartTime) / 100L;
		appendCell(stringBuilder, span.key);
		appendCell(stringBuilder, span.Depth);
		appendCell(stringBuilder, long1);
		appendCell(stringBuilder, long2);
		int int1 = 0;
		for (int int2 = span.Children.size(); int1 < int2; ++int1) {
			GameProfileRecording.Span span2 = (GameProfileRecording.Span)span.Children.get(int1);
			this.writeSpan(stringBuilder, row, span2);
		}
	}

	protected void writeHeader() {
		super.writeHeader();
		this.m_outHeader.println();
		this.m_outHeader.println("Segmentation Info");
		this.m_outHeader.println("FirstFrame," + this.m_firstFrameNo);
		this.m_outHeader.println("NumFramesPerFile," + this.m_numFramesPerFile);
		this.m_outHeader.println("NumFiles," + this.m_segmentFilePaths.size());
		this.m_outHeader.println();
		this.m_outHeader.println("KeyNamesTable");
		this.m_outHeader.println("Index,Name");
		StringBuilder stringBuilder = new StringBuilder();
		Iterator iterator = this.m_keyValueTable.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			stringBuilder.setLength(0);
			stringBuilder.append(entry.getValue());
			stringBuilder.append(",");
			stringBuilder.append((String)entry.getKey());
			this.m_outHeader.println(stringBuilder);
		}
	}

	public static class Row {
		long StartTime;
		long EndTime;
		final List Spans = new ArrayList();

		public void reset() {
			IPooledObject.release(this.Spans);
		}
	}

	public static class Span extends PooledObject {
		int key;
		int Depth;
		long StartTime;
		long EndTime;
		final List Children = new ArrayList();
		private static final Pool s_pool = new Pool(GameProfileRecording.Span::new);

		public void onReleased() {
			super.onReleased();
			IPooledObject.release(this.Children);
		}

		public static GameProfileRecording.Span alloc() {
			return (GameProfileRecording.Span)s_pool.alloc();
		}
	}
}
