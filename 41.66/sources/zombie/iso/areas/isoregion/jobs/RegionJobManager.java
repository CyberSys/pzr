package zombie.iso.areas.isoregion.jobs;

import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;


public final class RegionJobManager {
	private static final ConcurrentLinkedQueue poolSquareUpdate = new ConcurrentLinkedQueue();
	private static final ConcurrentLinkedQueue poolChunkUpdate = new ConcurrentLinkedQueue();
	private static final ConcurrentLinkedQueue poolApplyChanges = new ConcurrentLinkedQueue();
	private static final ConcurrentLinkedQueue poolServerSendFullData = new ConcurrentLinkedQueue();
	private static final ConcurrentLinkedQueue poolDebugResetAllData = new ConcurrentLinkedQueue();

	public static JobSquareUpdate allocSquareUpdate(int int1, int int2, int int3, byte byte1) {
		JobSquareUpdate jobSquareUpdate = (JobSquareUpdate)poolSquareUpdate.poll();
		if (jobSquareUpdate == null) {
			jobSquareUpdate = new JobSquareUpdate();
		}

		jobSquareUpdate.worldSquareX = int1;
		jobSquareUpdate.worldSquareY = int2;
		jobSquareUpdate.worldSquareZ = int3;
		jobSquareUpdate.newSquareFlags = byte1;
		return jobSquareUpdate;
	}

	public static JobChunkUpdate allocChunkUpdate() {
		JobChunkUpdate jobChunkUpdate = (JobChunkUpdate)poolChunkUpdate.poll();
		if (jobChunkUpdate == null) {
			jobChunkUpdate = new JobChunkUpdate();
		}

		return jobChunkUpdate;
	}

	public static JobApplyChanges allocApplyChanges(boolean boolean1) {
		JobApplyChanges jobApplyChanges = (JobApplyChanges)poolApplyChanges.poll();
		if (jobApplyChanges == null) {
			jobApplyChanges = new JobApplyChanges();
		}

		jobApplyChanges.saveToDisk = boolean1;
		return jobApplyChanges;
	}

	public static JobServerSendFullData allocServerSendFullData(UdpConnection udpConnection) {
		JobServerSendFullData jobServerSendFullData = (JobServerSendFullData)poolServerSendFullData.poll();
		if (jobServerSendFullData == null) {
			jobServerSendFullData = new JobServerSendFullData();
		}

		jobServerSendFullData.targetConn = udpConnection;
		return jobServerSendFullData;
	}

	public static JobDebugResetAllData allocDebugResetAllData() {
		JobDebugResetAllData jobDebugResetAllData = (JobDebugResetAllData)poolDebugResetAllData.poll();
		if (jobDebugResetAllData == null) {
			jobDebugResetAllData = new JobDebugResetAllData();
		}

		return jobDebugResetAllData;
	}

	public static void release(RegionJob regionJob) {
		regionJob.reset();
		switch (regionJob.getJobType()) {
		case SquareUpdate: 
			poolSquareUpdate.add((JobSquareUpdate)regionJob);
			break;
		
		case ApplyChanges: 
			poolApplyChanges.add((JobApplyChanges)regionJob);
			break;
		
		case ChunkUpdate: 
			poolChunkUpdate.add((JobChunkUpdate)regionJob);
			break;
		
		case ServerSendFullData: 
			poolServerSendFullData.add((JobServerSendFullData)regionJob);
			break;
		
		case DebugResetAllData: 
			poolDebugResetAllData.add((JobDebugResetAllData)regionJob);
			break;
		
		default: 
			if (Core.bDebug) {
				throw new RuntimeException("No pooling for this job type?");
			}

		
		}
	}
}
