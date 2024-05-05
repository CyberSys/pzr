package zombie.iso.areas.isoregion.jobs;


public abstract class RegionJob {
	private final RegionJobType type;

	protected RegionJob(RegionJobType regionJobType) {
		this.type = regionJobType;
	}

	protected void reset() {
	}

	public RegionJobType getJobType() {
		return this.type;
	}
}
