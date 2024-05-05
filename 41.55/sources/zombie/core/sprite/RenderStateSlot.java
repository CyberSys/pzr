package zombie.core.sprite;



public enum RenderStateSlot {

	Populating,
	Ready,
	Rendering,
	m_index;

	private RenderStateSlot(int int1) {
		this.m_index = int1;
	}
	public int index() {
		return this.m_index;
	}
	public int count() {
		return 3;
	}
	private static RenderStateSlot[] $values() {
		return new RenderStateSlot[]{Populating, Ready, Rendering};
	}
}
