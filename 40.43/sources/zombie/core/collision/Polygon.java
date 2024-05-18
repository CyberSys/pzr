package zombie.core.collision;

import java.util.ArrayList;
import zombie.iso.Vector2;


public class Polygon {
	public ArrayList points = new ArrayList(4);
	public ArrayList edges = new ArrayList(4);
	float x = 0.0F;
	float y = 0.0F;
	float x2 = 0.0F;
	float y2 = 0.0F;
	Vector2[] vecs = new Vector2[4];
	Vector2[] eds = new Vector2[4];
	static Vector2 temp = new Vector2();

	public void Set(float float1, float float2, float float3, float float4) {
		this.x = float1;
		this.y = float2;
		this.x2 = float3;
		this.y2 = float4;
		this.points.clear();
		if (this.vecs[0] == null) {
			for (int int1 = 0; int1 < 4; ++int1) {
				this.vecs[int1] = new Vector2();
				this.eds[int1] = new Vector2();
			}
		}

		this.vecs[0].x = float1;
		this.vecs[0].y = float2;
		this.vecs[1].x = float3;
		this.vecs[1].y = float2;
		this.vecs[2].x = float3;
		this.vecs[2].y = float4;
		this.vecs[3].x = float1;
		this.vecs[3].y = float4;
		this.points.add(this.vecs[0]);
		this.points.add(this.vecs[1]);
		this.points.add(this.vecs[2]);
		this.points.add(this.vecs[3]);
		this.BuildEdges();
	}

	public Vector2 Center() {
		temp.x = this.x + (this.x2 - this.x) / 2.0F;
		temp.y = this.y + (this.y2 - this.y) / 2.0F;
		return temp;
	}

	public void BuildEdges() {
		this.edges.clear();
		for (int int1 = 0; int1 < this.points.size(); ++int1) {
			Vector2 vector2 = (Vector2)this.points.get(int1);
			Vector2 vector22;
			if (int1 + 1 >= this.points.size()) {
				vector22 = (Vector2)this.points.get(0);
			} else {
				vector22 = (Vector2)this.points.get(int1 + 1);
			}

			this.eds[int1].x = vector22.x - vector2.x;
			this.eds[int1].y = vector22.y - vector2.y;
			this.edges.add(this.eds[int1]);
		}
	}

	public void Offset(float float1, float float2) {
		for (int int1 = 0; int1 < this.points.size(); ++int1) {
			Vector2 vector2 = (Vector2)this.points.get(int1);
			vector2.x += float1;
			vector2.y += float2;
		}
	}

	public void Offset(Vector2 vector2) {
		this.Offset(vector2.x, vector2.y);
	}
}
