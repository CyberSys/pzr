package zombie.core.skinnedmodel.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.skinnedmodel.Vector4;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.iso.Vector2;
import zombie.util.SharedStrings;


public class ModelLoader {
	public static ModelLoader instance = new ModelLoader();
	private SharedStrings sharedStrings = new SharedStrings();

	public Model Load(String string, String string2, String string3, boolean boolean1) throws IOException {
		Model model = new Model(boolean1);
		RenderThread.borrowContext();
		ModelLoader.LoadMode loadMode = ModelLoader.LoadMode.Version;
		VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkinArray = null;
		VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextureArray = null;
		int[] intArray = null;
		HashMap hashMap = new HashMap();
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		ArrayList arrayList3 = new ArrayList();
		ArrayList arrayList4 = new ArrayList();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(string));
		String string4 = null;
		int int1 = 0;
		int int2 = 0;
		int int3 = 0;
		boolean boolean2 = false;
		int int4 = 0;
		HashMap hashMap2 = new HashMap();
		boolean boolean3 = false;
		while ((string4 = bufferedReader.readLine()) != null) {
			try {
				if (string4.indexOf(35) != 0) {
					if (string4.contains("Tangent")) {
						if (boolean1) {
							int1 += 2;
						}

						boolean3 = true;
					}

					if (int1 > 0) {
						--int1;
					} else {
						String string5;
						float float1;
						int int5;
						int int6;
						float float2;
						int int7;
						String string6;
						String[] stringArray;
						int int8;
						String string7;
						switch (loadMode) {
						case Version: 
							loadMode = ModelLoader.LoadMode.ModelName;
							break;
						
						case ModelName: 
							loadMode = ModelLoader.LoadMode.VertexStrideElementCount;
							break;
						
						case VertexStrideElementCount: 
							loadMode = ModelLoader.LoadMode.VertexCount;
							if (boolean1) {
								int1 = 7;
							} else {
								int1 = 13;
							}

							break;
						
						case VertexCount: 
							int2 = Integer.parseInt(string4);
							loadMode = ModelLoader.LoadMode.VertexBuffer;
							vertexPositionNormalTangentTextureSkinArray = new VertexPositionNormalTangentTextureSkin[int2];
							vertexPositionNormalTangentTextureArray = new VertexPositionNormalTangentTexture[int2];
							break;
						
						case VertexBuffer: 
							int7 = 0;
							for (; int7 < int2; ++int7) {
								stringArray = string4.split(",");
								float1 = Float.parseFloat(stringArray[0].trim());
								float float3 = Float.parseFloat(stringArray[1].trim());
								float float4 = Float.parseFloat(stringArray[2].trim());
								string4 = bufferedReader.readLine();
								stringArray = string4.split(",");
								float float5 = Float.parseFloat(stringArray[0].trim());
								float float6 = Float.parseFloat(stringArray[1].trim());
								float float7 = Float.parseFloat(stringArray[2].trim());
								float2 = 0.0F;
								float float8 = 0.0F;
								float float9 = 0.0F;
								if (boolean3) {
									string4 = bufferedReader.readLine();
									stringArray = string4.split(",");
									float2 = Float.parseFloat(stringArray[0].trim());
									float8 = Float.parseFloat(stringArray[1].trim());
									float9 = Float.parseFloat(stringArray[2].trim());
								}

								string4 = bufferedReader.readLine();
								stringArray = string4.split(",");
								float float10 = Float.parseFloat(stringArray[0].trim());
								float float11 = Float.parseFloat(stringArray[1].trim());
								float float12 = 0.0F;
								float float13 = 0.0F;
								float float14 = 0.0F;
								float float15 = 0.0F;
								int int9 = 0;
								int int10 = 0;
								int int11 = 0;
								int int12 = 0;
								if (!boolean1) {
									string4 = bufferedReader.readLine();
									stringArray = string4.split(",");
									float12 = Float.parseFloat(stringArray[0].trim());
									float13 = Float.parseFloat(stringArray[1].trim());
									float14 = Float.parseFloat(stringArray[2].trim());
									float15 = Float.parseFloat(stringArray[3].trim());
									string4 = bufferedReader.readLine();
									stringArray = string4.split(",");
									int9 = Integer.parseInt(stringArray[0].trim());
									int10 = Integer.parseInt(stringArray[1].trim());
									int11 = Integer.parseInt(stringArray[2].trim());
									int12 = Integer.parseInt(stringArray[3].trim());
								}

								string4 = bufferedReader.readLine();
								if (!boolean1) {
									vertexPositionNormalTangentTextureSkinArray[int7] = new VertexPositionNormalTangentTextureSkin(new Vector3(float1, float3, float4), new Vector3(float5, float6, float7), new Vector3(float2, float8, float9), new Vector2(float10, float11), new Vector4(float12, float13, float14, float15), new UInt4(int9, int10, int11, int12));
								} else {
									vertexPositionNormalTangentTextureArray[int7] = new VertexPositionNormalTangentTexture(new Vector3(float1, float3, float4), new Vector3(float5, float6, float7), new Vector3(float2, float8, float9), new Vector2(float10, float11));
								}
							}

							loadMode = ModelLoader.LoadMode.NumberOfFaces;
							break;
						
						case NumberOfFaces: 
							int3 = Integer.parseInt(string4);
							intArray = new int[int3 * 3];
							loadMode = ModelLoader.LoadMode.FaceData;
							break;
						
						case FaceData: 
							for (int7 = 0; int7 < int3; ++int7) {
								stringArray = string4.split(",");
								int8 = Integer.parseInt(stringArray[0].trim());
								int5 = Integer.parseInt(stringArray[1].trim());
								int6 = Integer.parseInt(stringArray[2].trim());
								intArray[int7 * 3] = int8;
								intArray[int7 * 3 + 1] = int5;
								intArray[int7 * 3 + 2] = int6;
								string4 = bufferedReader.readLine();
							}

							loadMode = ModelLoader.LoadMode.NumberOfBones;
							break;
						
						case NumberOfBones: 
							int4 = Integer.parseInt(string4);
							loadMode = ModelLoader.LoadMode.SkeletonHierarchy;
							break;
						
						case SkeletonHierarchy: 
							for (int7 = 0; int7 < int4; ++int7) {
								int int13 = Integer.parseInt(string4);
								string4 = bufferedReader.readLine();
								int8 = Integer.parseInt(string4);
								string4 = bufferedReader.readLine();
								string7 = this.sharedStrings.get(string4);
								string4 = bufferedReader.readLine();
								arrayList.add(int8);
								hashMap.put(string7, int13);
							}

							loadMode = ModelLoader.LoadMode.BindPose;
							break;
						
						case BindPose: 
							for (int7 = 0; int7 < int4; ++int7) {
								string4 = bufferedReader.readLine();
								string5 = bufferedReader.readLine();
								string6 = bufferedReader.readLine();
								string7 = bufferedReader.readLine();
								arrayList2.add(int7, this.getMatrix(string4, string5, string6, string7));
								string4 = bufferedReader.readLine();
							}

							loadMode = ModelLoader.LoadMode.InvBindPose;
							break;
						
						case InvBindPose: 
							for (int7 = 0; int7 < int4; ++int7) {
								string4 = bufferedReader.readLine();
								string5 = bufferedReader.readLine();
								string6 = bufferedReader.readLine();
								string7 = bufferedReader.readLine();
								arrayList4.add(int7, this.getMatrix(string4, string5, string6, string7));
								string4 = bufferedReader.readLine();
							}

							loadMode = ModelLoader.LoadMode.SkinOffsetMatrices;
							break;
						
						case SkinOffsetMatrices: 
							for (int7 = 0; int7 < int4; ++int7) {
								string4 = bufferedReader.readLine();
								string5 = bufferedReader.readLine();
								string6 = bufferedReader.readLine();
								string7 = bufferedReader.readLine();
								arrayList3.add(int7, this.getMatrix(string4, string5, string6, string7));
								string4 = bufferedReader.readLine();
							}

							loadMode = ModelLoader.LoadMode.NumberOfAnims;
							break;
						
						case NumberOfAnims: 
							int int14 = Integer.parseInt(string4);
							loadMode = ModelLoader.LoadMode.Anim;
							break;
						
						case Anim: 
							ArrayList arrayList5 = new ArrayList();
							string5 = string4;
							string4 = bufferedReader.readLine();
							float1 = Float.parseFloat(string4);
							string4 = bufferedReader.readLine();
							int5 = Integer.parseInt(string4);
							string4 = bufferedReader.readLine();
							for (int6 = 0; int6 < int5; ++int6) {
								Keyframe keyframe = new Keyframe();
								int int15 = Integer.parseInt(string4);
								string4 = bufferedReader.readLine();
								String string8 = this.sharedStrings.get(string4);
								string4 = bufferedReader.readLine();
								float2 = Float.parseFloat(string4);
								string4 = bufferedReader.readLine();
								String string9 = bufferedReader.readLine();
								Vector3f vector3f = this.getVector(string4);
								Quaternionf quaternionf = this.getQuaternion(string9);
								if (int6 < int5 - 1) {
									string4 = bufferedReader.readLine();
								}

								keyframe.Bone = int15;
								keyframe.BoneName = string8;
								keyframe.Time = float2;
								keyframe.Rotation = quaternionf;
								keyframe.Position = vector3f;
								arrayList5.add(keyframe);
							}

							AnimationClip animationClip = new AnimationClip(float1, arrayList5, string5);
							hashMap2.put(string5, animationClip);
						
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		bufferedReader.close();
		if (!boolean1) {
			SkinningData skinningData = new SkinningData(hashMap2, arrayList2, arrayList4, arrayList3, arrayList, hashMap);
			model.Tag = skinningData;
			model.Mesh.SetVertexBuffer(new VertexBufferObject(vertexPositionNormalTangentTextureSkinArray, intArray));
		} else {
			model.Mesh.SetVertexBuffer(new VertexBufferObject(vertexPositionNormalTangentTextureArray, intArray));
		}

		model.CreateShader(string3);
		RenderThread.returnContext();
		TextureID.bUseCompression = false;
		model.tex = Texture.getSharedTexture("media/textures/" + string2 + ".png");
		TextureID.bUseCompression = TextureID.bUseCompressionOption;
		return model;
	}

	private Vector3f getVector(String string) {
		Vector3f vector3f = new Vector3f();
		String[] stringArray = string.split(",");
		vector3f.x = Float.parseFloat(stringArray[0]);
		vector3f.y = Float.parseFloat(stringArray[1]);
		vector3f.z = Float.parseFloat(stringArray[2]);
		return vector3f;
	}

	private Quaternionf getQuaternion(String string) {
		Quaternionf quaternionf = new Quaternionf();
		String[] stringArray = string.split(",");
		quaternionf.x = Float.parseFloat(stringArray[0]);
		quaternionf.y = Float.parseFloat(stringArray[1]);
		quaternionf.z = Float.parseFloat(stringArray[2]);
		quaternionf.w = Float.parseFloat(stringArray[3]);
		return quaternionf;
	}

	private Matrix4f getMatrix(String string, String string2, String string3, String string4) {
		boolean boolean1 = false;
		String[] stringArray = string.split(",");
		float float1 = Float.parseFloat(stringArray[0]);
		float float2 = Float.parseFloat(stringArray[1]);
		float float3 = Float.parseFloat(stringArray[2]);
		float float4 = Float.parseFloat(stringArray[3]);
		stringArray = string2.split(",");
		float float5 = Float.parseFloat(stringArray[0]);
		float float6 = Float.parseFloat(stringArray[1]);
		float float7 = Float.parseFloat(stringArray[2]);
		float float8 = Float.parseFloat(stringArray[3]);
		stringArray = string3.split(",");
		float float9 = Float.parseFloat(stringArray[0]);
		float float10 = Float.parseFloat(stringArray[1]);
		float float11 = Float.parseFloat(stringArray[2]);
		float float12 = Float.parseFloat(stringArray[3]);
		stringArray = string4.split(",");
		float float13 = Float.parseFloat(stringArray[0]);
		float float14 = Float.parseFloat(stringArray[1]);
		float float15 = Float.parseFloat(stringArray[2]);
		float float16 = Float.parseFloat(stringArray[3]);
		return new Matrix4f(float1, float2, float3, float4, float5, float6, float7, float8, float9, float10, float11, float12, float13, float14, float15, float16);
	}
	public static enum LoadMode {

		Version,
		ModelName,
		VertexStrideElementCount,
		VertexStrideSize,
		VertexStrideData,
		VertexCount,
		VertexBuffer,
		NumberOfFaces,
		FaceData,
		NumberOfBones,
		SkeletonHierarchy,
		BindPose,
		InvBindPose,
		SkinOffsetMatrices,
		NumberOfAnims,
		Anim;
	}
}
