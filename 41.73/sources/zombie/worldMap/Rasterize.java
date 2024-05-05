package zombie.worldMap;

import java.util.function.BiConsumer;


public final class Rasterize {
	final Rasterize.Edge edge1 = new Rasterize.Edge();
	final Rasterize.Edge edge2 = new Rasterize.Edge();
	final Rasterize.Edge edge3 = new Rasterize.Edge();

	void scanLine(int int1, int int2, int int3, BiConsumer biConsumer) {
		for (int int4 = int1; int4 < int2; ++int4) {
			biConsumer.accept(int4, int3);
		}
	}

	void scanSpan(Rasterize.Edge edge, Rasterize.Edge edge2, int int1, int int2, BiConsumer biConsumer) {
		int int3 = (int)Math.max((double)int1, Math.floor((double)edge2.y0));
		int int4 = (int)Math.min((double)int2, Math.ceil((double)edge2.y1));
		Rasterize.Edge edge3;
		if (edge.x0 == edge2.x0 && edge.y0 == edge2.y0) {
			if (edge.x0 + edge2.dy / edge.dy * edge.dx < edge2.x1) {
				edge3 = edge;
				edge = edge2;
				edge2 = edge3;
			}
		} else if (edge.x1 - edge2.dy / edge.dy * edge.dx < edge2.x0) {
			edge3 = edge;
			edge = edge2;
			edge2 = edge3;
		}

		double double1 = (double)(edge.dx / edge.dy);
		double double2 = (double)(edge2.dx / edge2.dy);
		double double3 = edge.dx > 0.0F ? 1.0 : 0.0;
		double double4 = edge2.dx < 0.0F ? 1.0 : 0.0;
		for (int int5 = int3; int5 < int4; ++int5) {
			double double5 = double1 * Math.max(0.0, Math.min((double)edge.dy, (double)int5 + double3 - (double)edge.y0)) + (double)edge.x0;
			double double6 = double2 * Math.max(0.0, Math.min((double)edge2.dy, (double)int5 + double4 - (double)edge2.y0)) + (double)edge2.x0;
			this.scanLine((int)Math.floor(double6), (int)Math.ceil(double5), int5, biConsumer);
		}
	}

	void scanTriangle(float float1, float float2, float float3, float float4, float float5, float float6, int int1, int int2, BiConsumer biConsumer) {
		Rasterize.Edge edge = this.edge1.init(float1, float2, float3, float4);
		Rasterize.Edge edge2 = this.edge2.init(float3, float4, float5, float6);
		Rasterize.Edge edge3 = this.edge3.init(float5, float6, float1, float2);
		Rasterize.Edge edge4;
		if (edge.dy > edge3.dy) {
			edge4 = edge;
			edge = edge3;
			edge3 = edge4;
		}

		if (edge2.dy > edge3.dy) {
			edge4 = edge2;
			edge2 = edge3;
			edge3 = edge4;
		}

		if (edge.dy > 0.0F) {
			this.scanSpan(edge3, edge, int1, int2, biConsumer);
		}

		if (edge2.dy > 0.0F) {
			this.scanSpan(edge3, edge2, int1, int2, biConsumer);
		}
	}

	private static final class Edge {
		float x0;
		float y0;
		float x1;
		float y1;
		float dx;
		float dy;

		Rasterize.Edge init(float float1, float float2, float float3, float float4) {
			if (float2 > float4) {
				this.x0 = float3;
				this.y0 = float4;
				this.x1 = float1;
				this.y1 = float2;
			} else {
				this.x0 = float1;
				this.y0 = float2;
				this.x1 = float3;
				this.y1 = float4;
			}

			this.dx = this.x1 - this.x0;
			this.dy = this.y1 - this.y0;
			return this;
		}
	}
}
