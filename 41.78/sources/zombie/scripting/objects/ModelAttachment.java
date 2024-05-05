package zombie.scripting.objects;

import java.util.ArrayList;
import org.joml.Vector3f;
import zombie.util.StringUtils;


public final class ModelAttachment {
	private String id;
	private final Vector3f offset = new Vector3f();
	private final Vector3f rotate = new Vector3f();
	private String bone;
	private ArrayList canAttach;
	private float zoffset;
	private boolean updateConstraint = true;

	public ModelAttachment(String string) {
		this.setId(string);
	}

	public String getId() {
		return this.id;
	}

	public void setId(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			throw new IllegalArgumentException("ModelAttachment id is null or empty");
		} else {
			this.id = string;
		}
	}

	public Vector3f getOffset() {
		return this.offset;
	}

	public Vector3f getRotate() {
		return this.rotate;
	}

	public String getBone() {
		return this.bone;
	}

	public void setBone(String string) {
		string = string.trim();
		this.bone = string.isEmpty() ? null : string;
	}

	public ArrayList getCanAttach() {
		return this.canAttach;
	}

	public void setCanAttach(ArrayList arrayList) {
		this.canAttach = arrayList;
	}

	public float getZOffset() {
		return this.zoffset;
	}

	public void setZOffset(float float1) {
		this.zoffset = float1;
	}

	public boolean isUpdateConstraint() {
		return this.updateConstraint;
	}

	public void setUpdateConstraint(boolean boolean1) {
		this.updateConstraint = boolean1;
	}
}
