package zombie.core.skinnedmodel.advancedanimation;


public class BlendInfo {
	public String name;
	public BlendType Type;
	public String mulDec;
	public String mulInc;
	public float dec;
	public float inc;

	public static class BlendInstance {
		public float current = -1.0F;
		public float target;
		BlendInfo info;

		public String GetDebug() {
			String string = "Blend: " + this.info.name;
			switch (this.info.Type) {
			case Linear: 
				string = string + ", Linear ";
				break;
			
			case InverseExponential: 
				string = string + ", InverseExponential ";
			
			}
			string = string + ", Current " + this.current;
			return string;
		}

		public BlendInstance(BlendInfo blendInfo) {
			this.info = blendInfo;
		}

		public void set(float float1) {
			this.target = float1;
			if (this.current == -1.0F) {
				this.current = this.target;
			}
		}

		public void update() {
			float float1 = 0.0F;
			float float2;
			if (this.current < this.target) {
				float2 = 1.0F;
				switch (this.info.Type) {
				case InverseExponential: 
					float2 = this.current / 1.0F;
					float2 = 1.0F - float2;
					if (float2 < 0.1F) {
						float2 = 0.1F;
					}

				
				case Linear: 
				
				default: 
					float1 = this.info.inc * float2;
					this.current += float1;
					if (this.current > this.target) {
						this.current = this.target;
					}

				
				}
			} else if (this.current > this.target) {
				float2 = 1.0F;
				switch (this.info.Type) {
				case InverseExponential: 
					float2 = this.current / 1.0F;
					float2 = 1.0F - float2;
					if (float2 < 0.1F) {
						float2 = 0.1F;
					}

				
				case Linear: 
				
				default: 
					float1 = -this.info.dec * float2;
					this.current += float1;
					if (this.current < this.target) {
						this.current = this.target;
					}

				
				}
			}
		}
	}
}
