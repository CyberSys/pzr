package zombie.core.skinnedmodel.model;

import zombie.core.Color;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.Vector3;
import zombie.iso.Vector2;


public class VertexDefinitions {

	class VertexPositionNormalTangentTexture {
		public Vector3 Position;
		public Vector3 Normal;
		public Vector3 Tangent;
		public Vector2 TextureCoordinates;

		public VertexPositionNormalTangentTexture(Vector3 vector3, Vector3 vector32, Vector3 vector33, Vector2 vector2) {
			this.Position = vector3;
			this.Normal = vector32;
			this.Tangent = vector33;
			this.TextureCoordinates = vector2;
		}

		public VertexPositionNormalTangentTexture(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11) {
			this.Position = new Vector3(float1, float2, float3);
			this.Normal = new Vector3(float4, float5, float6);
			this.Tangent = new Vector3(float7, float8, float9);
			this.TextureCoordinates = new Vector2(float10, float11);
		}
	}

	class VertexPositionNormalTexture {
		public Vector3 Position;
		public Vector3 Normal;
		public Vector2 TextureCoordinates;

		public VertexPositionNormalTexture(Vector3 vector3, Vector3 vector32, Vector2 vector2) {
			this.Position = vector3;
			this.Normal = vector32;
			this.TextureCoordinates = vector2;
		}

		public VertexPositionNormalTexture(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
			this.Position = new Vector3(float1, float2, float3);
			this.Normal = new Vector3(float4, float5, float6);
			this.TextureCoordinates = new Vector2(float7, float8);
		}
	}

	class VertexPositionNormal {
		public Vector3 Position;
		public Vector3 Normal;

		public VertexPositionNormal(Vector3 vector3, Vector3 vector32, Vector2 vector2) {
			this.Position = vector3;
			this.Normal = vector32;
		}

		public VertexPositionNormal(float float1, float float2, float float3, float float4, float float5, float float6) {
			this.Position = new Vector3(float1, float2, float3);
			this.Normal = new Vector3(float4, float5, float6);
		}
	}

	class VertexPositionColour {
		public Vector3 Position;
		public int Colour;

		public VertexPositionColour(Vector3 vector3, Color color) {
			this.Position = vector3;
			this.Colour = HelperFunctions.ToRgba(color);
		}

		public VertexPositionColour(float float1, float float2, float float3, Color color) {
			this.Position = new Vector3(float1, float2, float3);
			this.Colour = HelperFunctions.ToRgba(color);
		}
	}
}
