package zombie.core.skinnedmodel.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.util.SharedStrings;


public final class ModelLoader {
	public static final ModelLoader instance = new ModelLoader();
	private final ThreadLocal sharedStrings = ThreadLocal.withInitial(SharedStrings::new);

	protected ModelTxt loadTxt(String string, boolean boolean1, boolean boolean2, SkinningData skinningData) throws IOException {
		ModelTxt modelTxt = new ModelTxt();
		modelTxt.bStatic = boolean1;
		modelTxt.bReverse = boolean2;
		VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(boolean1 ? 4 : 6);
		vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
		vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
		vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
		vertexFormat.setElement(3, VertexBufferObject.VertexType.TextureCoordArray, 8);
		if (!boolean1) {
			vertexFormat.setElement(4, VertexBufferObject.VertexType.BlendWeightArray, 16);
			vertexFormat.setElement(5, VertexBufferObject.VertexType.BlendIndexArray, 16);
		}

		vertexFormat.calculate();
		FileReader fileReader = new FileReader(string);
		try {
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			try {
				SharedStrings sharedStrings = (SharedStrings)this.sharedStrings.get();
				ModelLoader.LoadMode loadMode = ModelLoader.LoadMode.Version;
				String string2 = null;
				int int1 = 0;
				int int2 = 0;
				int int3 = 0;
				boolean boolean3 = false;
				int int4 = 0;
				boolean boolean4 = false;
				label223: while (true) {
					while (true) {
						int int5;
						int int6;
						int int7;
						int int8;
						do {
							if ((string2 = bufferedReader.readLine()) == null) {
								if (boolean1 || skinningData == null) {
									break label223;
								}

								try {
									int[] intArray = new int[modelTxt.boneIndices.size()];
									ArrayList arrayList = modelTxt.SkeletonHierarchy;
									HashMap hashMap = modelTxt.boneIndices;
									HashMap hashMap2 = new HashMap(skinningData.BoneIndices);
									ArrayList arrayList2 = new ArrayList(skinningData.SkeletonHierarchy);
									hashMap.forEach((skinningDatax,modelTxtx)->{
										int vertexFormat = (Integer)hashMap2.getOrDefault(skinningDatax, -1);
										if (vertexFormat == -1) {
											vertexFormat = hashMap2.size();
											hashMap2.put(skinningDatax, vertexFormat);
											int fileReader = (Integer)arrayList.get(modelTxtx);
											if (fileReader >= 0) {
												arrayList2.add(intArray[fileReader]);
											} else {
												boolean bufferedReader = true;
											}
										}

										intArray[modelTxtx] = vertexFormat;
									});

									modelTxt.boneIndices = hashMap2;
									modelTxt.SkeletonHierarchy = arrayList2;
									int int9;
									for (int7 = 0; int7 < modelTxt.vertices.m_numVertices; ++int7) {
										int8 = (int)modelTxt.vertices.getElementFloat(int7, 5, 0);
										int5 = (int)modelTxt.vertices.getElementFloat(int7, 5, 1);
										int6 = (int)modelTxt.vertices.getElementFloat(int7, 5, 2);
										int9 = (int)modelTxt.vertices.getElementFloat(int7, 5, 3);
										if (int8 >= 0) {
											int8 = intArray[int8];
										}

										if (int5 >= 0) {
											int5 = intArray[int5];
										}

										if (int6 >= 0) {
											int6 = intArray[int6];
										}

										if (int9 >= 0) {
											int9 = intArray[int9];
										}

										modelTxt.vertices.setElement(int7, 5, (float)int8, (float)int5, (float)int6, (float)int9);
									}

									Iterator iterator = modelTxt.clips.values().iterator();
									while (iterator.hasNext()) {
										AnimationClip animationClip = (AnimationClip)iterator.next();
										Keyframe[] keyframeArray = animationClip.getKeyframes();
										int6 = keyframeArray.length;
										for (int9 = 0; int9 < int6; ++int9) {
											Keyframe keyframe = keyframeArray[int9];
											keyframe.Bone = intArray[keyframe.Bone];
										}
									}

									modelTxt.skinOffsetMatrices = this.RemapMatrices(intArray, modelTxt.skinOffsetMatrices, modelTxt.boneIndices.size());
									modelTxt.bindPose = this.RemapMatrices(intArray, modelTxt.bindPose, modelTxt.boneIndices.size());
									modelTxt.invBindPose = this.RemapMatrices(intArray, modelTxt.invBindPose, modelTxt.boneIndices.size());
								} catch (Exception exception) {
									exception.toString();
								}

								break label223;
							}
						}				 while (string2.indexOf(35) == 0);

						if (string2.contains("Tangent")) {
							if (boolean1) {
								int1 += 2;
							}

							boolean4 = true;
						}

						if (int1 > 0) {
							--int1;
						} else {
							String string3;
							float float1;
							float float2;
							int int10;
							String string4;
							String[] stringArray;
							String string5;
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
								int2 = Integer.parseInt(string2);
								loadMode = ModelLoader.LoadMode.VertexBuffer;
								modelTxt.vertices = new VertexBufferObject.VertexArray(vertexFormat, int2);
								break;
							
							case VertexBuffer: 
								int10 = 0;
								for (; int10 < int2; ++int10) {
									stringArray = string2.split(",");
									float1 = Float.parseFloat(stringArray[0].trim());
									float float3 = Float.parseFloat(stringArray[1].trim());
									float float4 = Float.parseFloat(stringArray[2].trim());
									string2 = bufferedReader.readLine();
									stringArray = string2.split(",");
									float float5 = Float.parseFloat(stringArray[0].trim());
									float float6 = Float.parseFloat(stringArray[1].trim());
									float float7 = Float.parseFloat(stringArray[2].trim());
									float2 = 0.0F;
									float float8 = 0.0F;
									float float9 = 0.0F;
									if (boolean4) {
										string2 = bufferedReader.readLine();
										stringArray = string2.split(",");
										float2 = Float.parseFloat(stringArray[0].trim());
										float8 = Float.parseFloat(stringArray[1].trim());
										float9 = Float.parseFloat(stringArray[2].trim());
									}

									string2 = bufferedReader.readLine();
									stringArray = string2.split(",");
									float float10 = Float.parseFloat(stringArray[0].trim());
									float float11 = Float.parseFloat(stringArray[1].trim());
									float float12 = 0.0F;
									float float13 = 0.0F;
									float float14 = 0.0F;
									float float15 = 0.0F;
									int int11 = 0;
									int int12 = 0;
									int int13 = 0;
									int int14 = 0;
									if (!boolean1) {
										string2 = bufferedReader.readLine();
										stringArray = string2.split(",");
										float12 = Float.parseFloat(stringArray[0].trim());
										float13 = Float.parseFloat(stringArray[1].trim());
										float14 = Float.parseFloat(stringArray[2].trim());
										float15 = Float.parseFloat(stringArray[3].trim());
										string2 = bufferedReader.readLine();
										stringArray = string2.split(",");
										int11 = Integer.parseInt(stringArray[0].trim());
										int12 = Integer.parseInt(stringArray[1].trim());
										int13 = Integer.parseInt(stringArray[2].trim());
										int14 = Integer.parseInt(stringArray[3].trim());
									}

									string2 = bufferedReader.readLine();
									modelTxt.vertices.setElement(int10, 0, float1, float3, float4);
									modelTxt.vertices.setElement(int10, 1, float5, float6, float7);
									modelTxt.vertices.setElement(int10, 2, float2, float8, float9);
									modelTxt.vertices.setElement(int10, 3, float10, float11);
									if (!boolean1) {
										modelTxt.vertices.setElement(int10, 4, float12, float13, float14, float15);
										modelTxt.vertices.setElement(int10, 5, (float)int11, (float)int12, (float)int13, (float)int14);
									}
								}

								loadMode = ModelLoader.LoadMode.NumberOfFaces;
								break;
							
							case NumberOfFaces: 
								int3 = Integer.parseInt(string2);
								modelTxt.elements = new int[int3 * 3];
								loadMode = ModelLoader.LoadMode.FaceData;
								break;
							
							case FaceData: 
								for (int10 = 0; int10 < int3; ++int10) {
									stringArray = string2.split(",");
									int8 = Integer.parseInt(stringArray[0].trim());
									int5 = Integer.parseInt(stringArray[1].trim());
									int6 = Integer.parseInt(stringArray[2].trim());
									if (boolean2) {
										modelTxt.elements[int10 * 3 + 2] = int8;
										modelTxt.elements[int10 * 3 + 1] = int5;
										modelTxt.elements[int10 * 3 + 0] = int6;
									} else {
										modelTxt.elements[int10 * 3 + 0] = int8;
										modelTxt.elements[int10 * 3 + 1] = int5;
										modelTxt.elements[int10 * 3 + 2] = int6;
									}

									string2 = bufferedReader.readLine();
								}

								loadMode = ModelLoader.LoadMode.NumberOfBones;
								break;
							
							case NumberOfBones: 
								int4 = Integer.parseInt(string2);
								loadMode = ModelLoader.LoadMode.SkeletonHierarchy;
								break;
							
							case SkeletonHierarchy: 
								for (int10 = 0; int10 < int4; ++int10) {
									int7 = Integer.parseInt(string2);
									string2 = bufferedReader.readLine();
									int8 = Integer.parseInt(string2);
									string2 = bufferedReader.readLine();
									string5 = sharedStrings.get(string2);
									string2 = bufferedReader.readLine();
									modelTxt.SkeletonHierarchy.add(int8);
									modelTxt.boneIndices.put(string5, int7);
								}

								loadMode = ModelLoader.LoadMode.BindPose;
								break;
							
							case BindPose: 
								for (int10 = 0; int10 < int4; ++int10) {
									string2 = bufferedReader.readLine();
									string3 = bufferedReader.readLine();
									string4 = bufferedReader.readLine();
									string5 = bufferedReader.readLine();
									modelTxt.bindPose.add(int10, this.getMatrix(string2, string3, string4, string5));
									string2 = bufferedReader.readLine();
								}

								loadMode = ModelLoader.LoadMode.InvBindPose;
								break;
							
							case InvBindPose: 
								for (int10 = 0; int10 < int4; ++int10) {
									string2 = bufferedReader.readLine();
									string3 = bufferedReader.readLine();
									string4 = bufferedReader.readLine();
									string5 = bufferedReader.readLine();
									modelTxt.invBindPose.add(int10, this.getMatrix(string2, string3, string4, string5));
									string2 = bufferedReader.readLine();
								}

								loadMode = ModelLoader.LoadMode.SkinOffsetMatrices;
								break;
							
							case SkinOffsetMatrices: 
								for (int10 = 0; int10 < int4; ++int10) {
									string2 = bufferedReader.readLine();
									string3 = bufferedReader.readLine();
									string4 = bufferedReader.readLine();
									string5 = bufferedReader.readLine();
									modelTxt.skinOffsetMatrices.add(int10, this.getMatrix(string2, string3, string4, string5));
									string2 = bufferedReader.readLine();
								}

								loadMode = ModelLoader.LoadMode.NumberOfAnims;
								break;
							
							case NumberOfAnims: 
								int int15 = Integer.parseInt(string2);
								loadMode = ModelLoader.LoadMode.Anim;
								break;
							
							case Anim: 
								ArrayList arrayList3 = new ArrayList();
								string3 = string2;
								string2 = bufferedReader.readLine();
								float1 = Float.parseFloat(string2);
								string2 = bufferedReader.readLine();
								int5 = Integer.parseInt(string2);
								string2 = bufferedReader.readLine();
								for (int6 = 0; int6 < int5; ++int6) {
									Keyframe keyframe2 = new Keyframe();
									int int16 = Integer.parseInt(string2);
									string2 = bufferedReader.readLine();
									String string6 = sharedStrings.get(string2);
									string2 = bufferedReader.readLine();
									float2 = Float.parseFloat(string2);
									string2 = bufferedReader.readLine();
									String string7 = bufferedReader.readLine();
									Vector3f vector3f = this.getVector(string2);
									Quaternion quaternion = this.getQuaternion(string7);
									if (int6 < int5 - 1) {
										string2 = bufferedReader.readLine();
									}

									keyframe2.Bone = int16;
									keyframe2.BoneName = string6;
									keyframe2.Time = float2;
									keyframe2.Rotation = quaternion;
									keyframe2.Position = new Vector3f(vector3f);
									arrayList3.add(keyframe2);
								}

								AnimationClip animationClip2 = new AnimationClip(float1, arrayList3, string3, false);
								arrayList3.clear();
								if (ModelManager.instance.bCreateSoftwareMeshes) {
									animationClip2.staticClip = new StaticAnimation(animationClip2);
								}

								modelTxt.clips.put(string3, animationClip2);
							
							}
						}
					}
				}
			} catch (Throwable throwable) {
				try {
					bufferedReader.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			bufferedReader.close();
		} catch (Throwable throwable3) {
			try {
				fileReader.close();
			} catch (Throwable throwable4) {
				throwable3.addSuppressed(throwable4);
			}

			throw throwable3;
		}

		fileReader.close();
		return modelTxt;
	}

	protected void applyToMesh(ModelTxt modelTxt, ModelMesh modelMesh, SkinningData skinningData) {
		if (modelTxt.bStatic) {
			if (!ModelManager.NoOpenGL) {
				RenderThread.queueInvokeOnRenderContext(()->{
					modelMesh.SetVertexBuffer(new VertexBufferObject(modelTxt.vertices, modelTxt.elements));
					if (ModelManager.instance.bCreateSoftwareMeshes) {
						modelMesh.softwareMesh.vb = modelMesh.vb;
					}
				});
			}
		} else {
			modelMesh.skinningData = new SkinningData(modelTxt.clips, modelTxt.bindPose, modelTxt.invBindPose, modelTxt.skinOffsetMatrices, modelTxt.SkeletonHierarchy, modelTxt.boneIndices);
			if (!ModelManager.NoOpenGL) {
				RenderThread.queueInvokeOnRenderContext(()->{
					modelMesh.SetVertexBuffer(new VertexBufferObject(modelTxt.vertices, modelTxt.elements, modelTxt.bReverse));
					if (ModelManager.instance.bCreateSoftwareMeshes) {
					}
				});
			}
		}

		if (skinningData != null) {
			modelMesh.skinningData.AnimationClips = skinningData.AnimationClips;
		}
	}

	protected void applyToAnimation(ModelTxt modelTxt, AnimationAsset animationAsset) {
		animationAsset.AnimationClips = modelTxt.clips;
		animationAsset.assetParams.animationsMesh.skinningData.AnimationClips.putAll(modelTxt.clips);
	}

	private ArrayList RemapMatrices(int[] intArray, ArrayList arrayList, int int1) {
		ArrayList arrayList2 = new ArrayList(int1);
		Matrix4f matrix4f = new Matrix4f();
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			arrayList2.add(matrix4f);
		}

		for (int2 = 0; int2 < intArray.length; ++int2) {
			arrayList2.set(intArray[int2], (Matrix4f)arrayList.get(int2));
		}

		return arrayList2;
	}

	private Vector3f getVector(String string) {
		Vector3f vector3f = new Vector3f();
		String[] stringArray = string.split(",");
		vector3f.x = Float.parseFloat(stringArray[0]);
		vector3f.y = Float.parseFloat(stringArray[1]);
		vector3f.z = Float.parseFloat(stringArray[2]);
		return vector3f;
	}

	private Quaternion getQuaternion(String string) {
		Quaternion quaternion = new Quaternion();
		String[] stringArray = string.split(",");
		quaternion.x = Float.parseFloat(stringArray[0]);
		quaternion.y = Float.parseFloat(stringArray[1]);
		quaternion.z = Float.parseFloat(stringArray[2]);
		quaternion.w = Float.parseFloat(stringArray[3]);
		return quaternion;
	}

	private Matrix4f getMatrix(String string, String string2, String string3, String string4) {
		Matrix4f matrix4f = new Matrix4f();
		boolean boolean1 = false;
		String[] stringArray = string.split(",");
		matrix4f.m00 = Float.parseFloat(stringArray[0]);
		matrix4f.m01 = Float.parseFloat(stringArray[1]);
		matrix4f.m02 = Float.parseFloat(stringArray[2]);
		matrix4f.m03 = Float.parseFloat(stringArray[3]);
		stringArray = string2.split(",");
		matrix4f.m10 = Float.parseFloat(stringArray[0]);
		matrix4f.m11 = Float.parseFloat(stringArray[1]);
		matrix4f.m12 = Float.parseFloat(stringArray[2]);
		matrix4f.m13 = Float.parseFloat(stringArray[3]);
		stringArray = string3.split(",");
		matrix4f.m20 = Float.parseFloat(stringArray[0]);
		matrix4f.m21 = Float.parseFloat(stringArray[1]);
		matrix4f.m22 = Float.parseFloat(stringArray[2]);
		matrix4f.m23 = Float.parseFloat(stringArray[3]);
		stringArray = string4.split(",");
		matrix4f.m30 = Float.parseFloat(stringArray[0]);
		matrix4f.m31 = Float.parseFloat(stringArray[1]);
		matrix4f.m32 = Float.parseFloat(stringArray[2]);
		matrix4f.m33 = Float.parseFloat(stringArray[3]);
		return matrix4f;
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

		private static ModelLoader.LoadMode[] $values() {
			return new ModelLoader.LoadMode[]{Version, ModelName, VertexStrideElementCount, VertexStrideSize, VertexStrideData, VertexCount, VertexBuffer, NumberOfFaces, FaceData, NumberOfBones, SkeletonHierarchy, BindPose, InvBindPose, SkinOffsetMatrices, NumberOfAnims, Anim};
		}
	}
}
