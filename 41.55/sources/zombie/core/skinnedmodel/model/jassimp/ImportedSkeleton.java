package zombie.core.skinnedmodel.model.jassimp;

import gnu.trove.list.array.TFloatArrayList;
import jassimp.AiAnimation;
import jassimp.AiBone;
import jassimp.AiBuiltInWrapperProvider;
import jassimp.AiMatrix4f;
import jassimp.AiMesh;
import jassimp.AiNode;
import jassimp.AiNodeAnim;
import jassimp.AiQuaternion;
import jassimp.AiScene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;


public final class ImportedSkeleton {
	final HashMap boneIndices = new HashMap();
	final ArrayList SkeletonHierarchy = new ArrayList();
	final ArrayList bindPose = new ArrayList();
	final ArrayList invBindPose = new ArrayList();
	final ArrayList skinOffsetMatrices = new ArrayList();
	AiNode rootBoneNode = null;
	final HashMap clips = new HashMap();
	final AiBuiltInWrapperProvider wrapper = new AiBuiltInWrapperProvider();
	final Quaternion end = new Quaternion();

	private ImportedSkeleton() {
	}

	public static ImportedSkeleton process(ImportedSkeletonParams importedSkeletonParams) {
		ImportedSkeleton importedSkeleton = new ImportedSkeleton();
		importedSkeleton.processAiScene(importedSkeletonParams);
		return importedSkeleton;
	}

	private void processAiScene(ImportedSkeletonParams importedSkeletonParams) {
		AiScene aiScene = importedSkeletonParams.scene;
		JAssImpImporter.LoadMode loadMode = importedSkeletonParams.mode;
		SkinningData skinningData = importedSkeletonParams.skinnedTo;
		float float1 = importedSkeletonParams.animBonesScaleModifier;
		Quaternion quaternion = importedSkeletonParams.animBonesRotateModifier;
		AiMesh aiMesh = importedSkeletonParams.mesh;
		AiNode aiNode = (AiNode)aiScene.getSceneRoot(this.wrapper);
		this.rootBoneNode = JAssImpImporter.FindNode("Dummy01", aiNode);
		boolean boolean1;
		if (this.rootBoneNode == null) {
			this.rootBoneNode = JAssImpImporter.FindNode("VehicleSkeleton", aiNode);
			boolean1 = true;
		} else {
			boolean1 = false;
		}

		while (this.rootBoneNode != null && this.rootBoneNode.getParent() != null && this.rootBoneNode.getParent() != aiNode) {
			this.rootBoneNode = this.rootBoneNode.getParent();
		}

		if (this.rootBoneNode == null) {
			this.rootBoneNode = aiNode;
		}

		ArrayList arrayList = new ArrayList();
		JAssImpImporter.CollectBoneNodes(arrayList, this.rootBoneNode);
		AiNode aiNode2 = JAssImpImporter.FindNode("Translation_Data", aiNode);
		if (aiNode2 != null) {
			arrayList.add(aiNode2);
		}

		if (skinningData != null) {
			this.boneIndices.putAll(skinningData.BoneIndices);
			this.SkeletonHierarchy.addAll(skinningData.SkeletonHierarchy);
		}

		int int1;
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			AiNode aiNode3 = (AiNode)arrayList.get(int2);
			String string = aiNode3.getName();
			if (!this.boneIndices.containsKey(string)) {
				int1 = this.boneIndices.size();
				this.boneIndices.put(string, int1);
				if (aiNode3 == this.rootBoneNode) {
					this.SkeletonHierarchy.add(-1);
				} else {
					AiNode aiNode4;
					for (aiNode4 = aiNode3.getParent(); aiNode4 != null && !this.boneIndices.containsKey(aiNode4.getName()); aiNode4 = aiNode4.getParent()) {
					}

					if (aiNode4 != null) {
						this.SkeletonHierarchy.add((Integer)this.boneIndices.get(aiNode4.getName()));
					} else {
						this.SkeletonHierarchy.add(0);
					}
				}
			}
		}

		Matrix4f matrix4f = new Matrix4f();
		for (int int3 = 0; int3 < this.boneIndices.size(); ++int3) {
			this.bindPose.add(matrix4f);
			this.skinOffsetMatrices.add(matrix4f);
		}

		List list = aiMesh.getBones();
		int int4;
		for (int4 = 0; int4 < arrayList.size(); ++int4) {
			AiNode aiNode5 = (AiNode)arrayList.get(int4);
			String string2 = aiNode5.getName();
			AiBone aiBone = JAssImpImporter.FindAiBone(string2, list);
			if (aiBone != null) {
				AiMatrix4f aiMatrix4f = (AiMatrix4f)aiBone.getOffsetMatrix(this.wrapper);
				if (aiMatrix4f != null) {
					Matrix4f matrix4f2 = JAssImpImporter.getMatrixFromAiMatrix(aiMatrix4f);
					Matrix4f matrix4f3 = new Matrix4f(matrix4f2);
					matrix4f3.invert();
					Matrix4f matrix4f4 = new Matrix4f();
					matrix4f4.setIdentity();
					String string3 = aiNode5.getParent().getName();
					AiBone aiBone2 = JAssImpImporter.FindAiBone(string3, list);
					if (aiBone2 != null) {
						AiMatrix4f aiMatrix4f2 = (AiMatrix4f)aiBone2.getOffsetMatrix(this.wrapper);
						if (aiMatrix4f2 != null) {
							JAssImpImporter.getMatrixFromAiMatrix(aiMatrix4f2, matrix4f4);
						}
					}

					Matrix4f matrix4f5 = new Matrix4f(matrix4f4);
					matrix4f5.invert();
					Matrix4f matrix4f6 = new Matrix4f();
					Matrix4f.mul(matrix4f3, matrix4f5, matrix4f6);
					matrix4f6.invert();
					int int5 = (Integer)this.boneIndices.get(string2);
					this.bindPose.set(int5, matrix4f6);
					this.skinOffsetMatrices.set(int5, matrix4f2);
				}
			}
		}

		int4 = this.bindPose.size();
		for (int1 = 0; int1 < int4; ++int1) {
			Matrix4f matrix4f7 = new Matrix4f((Matrix4f)this.bindPose.get(int1));
			matrix4f7.invert();
			this.invBindPose.add(int1, matrix4f7);
		}

		if (loadMode == JAssImpImporter.LoadMode.AnimationOnly || skinningData == null) {
			int1 = aiScene.getNumAnimations();
			if (int1 > 0) {
				List list2 = aiScene.getAnimations();
				for (int int6 = 0; int6 < int1; ++int6) {
					AiAnimation aiAnimation = (AiAnimation)list2.get(int6);
					if (boolean1) {
						this.processAnimation(aiAnimation, boolean1, 1.0F, (Quaternion)null);
					} else {
						this.processAnimation(aiAnimation, boolean1, float1, quaternion);
					}
				}
			}
		}
	}

	@Deprecated
	void processAnimationOld(AiAnimation aiAnimation, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		float float1 = (float)aiAnimation.getDuration();
		float float2 = float1 / (float)aiAnimation.getTicksPerSecond();
		ArrayList arrayList2 = new ArrayList();
		List list = aiAnimation.getChannels();
		int int1;
		for (int1 = 0; int1 < list.size(); ++int1) {
			AiNodeAnim aiNodeAnim = (AiNodeAnim)list.get(int1);
			int int2;
			float float3;
			for (int2 = 0; int2 < aiNodeAnim.getNumPosKeys(); ++int2) {
				float3 = (float)aiNodeAnim.getPosKeyTime(int2);
				if (!arrayList2.contains(float3)) {
					arrayList2.add(float3);
				}
			}

			for (int2 = 0; int2 < aiNodeAnim.getNumRotKeys(); ++int2) {
				float3 = (float)aiNodeAnim.getRotKeyTime(int2);
				if (!arrayList2.contains(float3)) {
					arrayList2.add(float3);
				}
			}

			for (int2 = 0; int2 < aiNodeAnim.getNumScaleKeys(); ++int2) {
				float3 = (float)aiNodeAnim.getScaleKeyTime(int2);
				if (!arrayList2.contains(float3)) {
					arrayList2.add(float3);
				}
			}
		}

		Collections.sort(arrayList2);
		int int3;
		for (int1 = 0; int1 < arrayList2.size(); ++int1) {
			for (int3 = 0; int3 < list.size(); ++int3) {
				AiNodeAnim aiNodeAnim2 = (AiNodeAnim)list.get(int3);
				Keyframe keyframe = new Keyframe();
				keyframe.clear();
				keyframe.BoneName = aiNodeAnim2.getNodeName();
				Integer integer = (Integer)this.boneIndices.get(keyframe.BoneName);
				if (integer == null) {
					DebugLog.General.error("Could not find bone index for node name: \"%s\"", keyframe.BoneName);
				} else {
					keyframe.Bone = integer;
					keyframe.Time = (Float)arrayList2.get(int1) / (float)aiAnimation.getTicksPerSecond();
					if (!boolean1) {
						keyframe.Position = JAssImpImporter.GetKeyFramePosition(aiNodeAnim2, (Float)arrayList2.get(int1));
						keyframe.Rotation = JAssImpImporter.GetKeyFrameRotation(aiNodeAnim2, (Float)arrayList2.get(int1));
						keyframe.Scale = JAssImpImporter.GetKeyFrameScale(aiNodeAnim2, (Float)arrayList2.get(int1));
					} else {
						keyframe.Position = this.GetKeyFramePosition(aiNodeAnim2, (Float)arrayList2.get(int1), aiAnimation.getDuration());
						keyframe.Rotation = this.GetKeyFrameRotation(aiNodeAnim2, (Float)arrayList2.get(int1), aiAnimation.getDuration());
						keyframe.Scale = this.GetKeyFrameScale(aiNodeAnim2, (Float)arrayList2.get(int1), aiAnimation.getDuration());
					}

					if (keyframe.Bone >= 0) {
						arrayList.add(keyframe);
					}
				}
			}
		}

		String string = aiAnimation.getName();
		int3 = string.indexOf(124);
		if (int3 > 0) {
			string = string.substring(int3 + 1);
		}

		AnimationClip animationClip = new AnimationClip(float2, arrayList, string, true);
		arrayList.clear();
		if (ModelManager.instance.bCreateSoftwareMeshes) {
			animationClip.staticClip = new StaticAnimation(animationClip);
		}

		this.clips.put(string, animationClip);
	}

	private void processAnimation(AiAnimation aiAnimation, boolean boolean1, float float1, Quaternion quaternion) {
		ArrayList arrayList = new ArrayList();
		float float2 = (float)aiAnimation.getDuration();
		float float3 = float2 / (float)aiAnimation.getTicksPerSecond();
		TFloatArrayList[] tFloatArrayListArray = new TFloatArrayList[this.boneIndices.size()];
		Arrays.fill(tFloatArrayListArray, (Object)null);
		ArrayList arrayList2 = new ArrayList(this.boneIndices.size());
		for (int int1 = 0; int1 < this.boneIndices.size(); ++int1) {
			arrayList2.add((Object)null);
		}

		this.collectBoneFrames(aiAnimation, tFloatArrayListArray, arrayList2);
		Quaternion quaternion2 = null;
		boolean boolean2 = quaternion != null;
		if (boolean2) {
			quaternion2 = new Quaternion();
			Quaternion.mulInverse(quaternion2, quaternion, quaternion2);
		}

		for (int int2 = 0; int2 < this.boneIndices.size(); ++int2) {
			ArrayList arrayList3 = (ArrayList)arrayList2.get(int2);
			if (arrayList3 == null) {
				if (int2 == 0 && quaternion != null) {
					Quaternion quaternion3 = new Quaternion();
					quaternion3.set(quaternion);
					this.addDefaultAnimTrack("RootNode", int2, quaternion3, new Vector3f(0.0F, 0.0F, 0.0F), arrayList, float3);
				}
			} else {
				TFloatArrayList tFloatArrayList = tFloatArrayListArray[int2];
				if (tFloatArrayList != null) {
					tFloatArrayList.sort();
					int int3 = this.getParentBoneIdx(int2);
					boolean boolean3 = boolean2 && (int3 == 0 || this.doesParentBoneHaveAnimFrames(tFloatArrayListArray, arrayList2, int2));
					for (int int4 = 0; int4 < tFloatArrayList.size(); ++int4) {
						float float4 = tFloatArrayList.get(int4);
						float float5 = float4 / (float)aiAnimation.getTicksPerSecond();
						for (int int5 = 0; int5 < arrayList3.size(); ++int5) {
							AiNodeAnim aiNodeAnim = (AiNodeAnim)arrayList3.get(int5);
							Keyframe keyframe = new Keyframe();
							keyframe.clear();
							keyframe.BoneName = aiNodeAnim.getNodeName();
							keyframe.Bone = int2;
							keyframe.Time = float5;
							if (!boolean1) {
								keyframe.Position = JAssImpImporter.GetKeyFramePosition(aiNodeAnim, float4);
								keyframe.Rotation = JAssImpImporter.GetKeyFrameRotation(aiNodeAnim, float4);
								keyframe.Scale = JAssImpImporter.GetKeyFrameScale(aiNodeAnim, float4);
							} else {
								keyframe.Position = this.GetKeyFramePosition(aiNodeAnim, float4, (double)float2);
								keyframe.Rotation = this.GetKeyFrameRotation(aiNodeAnim, float4, (double)float2);
								keyframe.Scale = this.GetKeyFrameScale(aiNodeAnim, float4, (double)float2);
							}

							Vector3f vector3f = keyframe.Position;
							vector3f.x *= float1;
							vector3f = keyframe.Position;
							vector3f.y *= float1;
							vector3f = keyframe.Position;
							vector3f.z *= float1;
							if (boolean2) {
								if (boolean3) {
									Quaternion.mul(quaternion2, keyframe.Rotation, keyframe.Rotation);
									boolean boolean4 = keyframe.BoneName.equalsIgnoreCase("Translation_Data");
									if (!boolean4) {
										HelperFunctions.transform(quaternion2, keyframe.Position, keyframe.Position);
									}
								}

								Quaternion.mul(keyframe.Rotation, quaternion, keyframe.Rotation);
							}

							arrayList.add(keyframe);
						}
					}
				}
			}
		}

		String string = aiAnimation.getName();
		int int6 = string.indexOf(124);
		if (int6 > 0) {
			string = string.substring(int6 + 1);
		}

		string = string.trim();
		AnimationClip animationClip = new AnimationClip(float3, arrayList, string, true);
		arrayList.clear();
		if (ModelManager.instance.bCreateSoftwareMeshes) {
			animationClip.staticClip = new StaticAnimation(animationClip);
		}

		this.clips.put(string, animationClip);
	}

	private void addDefaultAnimTrack(String string, int int1, Quaternion quaternion, Vector3f vector3f, ArrayList arrayList, float float1) {
		Vector3f vector3f2 = new Vector3f(1.0F, 1.0F, 1.0F);
		Keyframe keyframe = new Keyframe();
		keyframe.clear();
		keyframe.BoneName = string;
		keyframe.Bone = int1;
		keyframe.Time = 0.0F;
		keyframe.Position = vector3f;
		keyframe.Rotation = quaternion;
		keyframe.Scale = vector3f2;
		arrayList.add(keyframe);
		Keyframe keyframe2 = new Keyframe();
		keyframe2.clear();
		keyframe2.BoneName = string;
		keyframe2.Bone = int1;
		keyframe2.Time = float1;
		keyframe2.Position = vector3f;
		keyframe2.Rotation = quaternion;
		keyframe2.Scale = vector3f2;
		arrayList.add(keyframe2);
	}

	private boolean doesParentBoneHaveAnimFrames(TFloatArrayList[] tFloatArrayListArray, ArrayList arrayList, int int1) {
		int int2 = this.getParentBoneIdx(int1);
		return int2 < 0 ? false : this.doesBoneHaveAnimFrames(tFloatArrayListArray, arrayList, int2);
	}

	private boolean doesBoneHaveAnimFrames(TFloatArrayList[] tFloatArrayListArray, ArrayList arrayList, int int1) {
		TFloatArrayList tFloatArrayList = tFloatArrayListArray[int1];
		if (tFloatArrayList != null && tFloatArrayList.size() > 0) {
			ArrayList arrayList2 = (ArrayList)arrayList.get(int1);
			return arrayList2.size() > 0;
		} else {
			return false;
		}
	}

	private void collectBoneFrames(AiAnimation aiAnimation, TFloatArrayList[] tFloatArrayListArray, ArrayList arrayList) {
		List list = aiAnimation.getChannels();
		for (int int1 = 0; int1 < list.size(); ++int1) {
			AiNodeAnim aiNodeAnim = (AiNodeAnim)list.get(int1);
			String string = aiNodeAnim.getNodeName();
			Integer integer = (Integer)this.boneIndices.get(string);
			if (integer == null) {
				DebugLog.General.error("Could not find bone index for node name: \"%s\"", string);
			} else {
				ArrayList arrayList2 = (ArrayList)arrayList.get(integer);
				if (arrayList2 == null) {
					arrayList2 = new ArrayList();
					arrayList.set(integer, arrayList2);
				}

				arrayList2.add(aiNodeAnim);
				TFloatArrayList tFloatArrayList = tFloatArrayListArray[integer];
				if (tFloatArrayList == null) {
					tFloatArrayList = new TFloatArrayList();
					tFloatArrayListArray[integer] = tFloatArrayList;
				}

				int int2;
				float float1;
				for (int2 = 0; int2 < aiNodeAnim.getNumPosKeys(); ++int2) {
					float1 = (float)aiNodeAnim.getPosKeyTime(int2);
					if (!tFloatArrayList.contains(float1)) {
						tFloatArrayList.add(float1);
					}
				}

				for (int2 = 0; int2 < aiNodeAnim.getNumRotKeys(); ++int2) {
					float1 = (float)aiNodeAnim.getRotKeyTime(int2);
					if (!tFloatArrayList.contains(float1)) {
						tFloatArrayList.add(float1);
					}
				}

				for (int2 = 0; int2 < aiNodeAnim.getNumScaleKeys(); ++int2) {
					float1 = (float)aiNodeAnim.getScaleKeyTime(int2);
					if (!tFloatArrayList.contains(float1)) {
						tFloatArrayList.add(float1);
					}
				}
			}
		}
	}

	private int getParentBoneIdx(int int1) {
		return int1 > -1 ? (Integer)this.SkeletonHierarchy.get(int1) : -1;
	}

	public int getNumBoneAncestors(int int1) {
		int int2 = 0;
		for (int int3 = this.getParentBoneIdx(int1); int3 > -1; int3 = this.getParentBoneIdx(int3)) {
			++int2;
		}

		return int2;
	}

	private Vector3f GetKeyFramePosition(AiNodeAnim aiNodeAnim, float float1, double double1) {
		Vector3f vector3f = new Vector3f();
		if (aiNodeAnim.getNumPosKeys() == 0) {
			return vector3f;
		} else {
			int int1;
			for (int1 = 0; int1 < aiNodeAnim.getNumPosKeys() - 1 && !((double)float1 < aiNodeAnim.getPosKeyTime(int1 + 1)); ++int1) {
			}

			int int2 = (int1 + 1) % aiNodeAnim.getNumPosKeys();
			float float2 = (float)aiNodeAnim.getPosKeyTime(int1);
			float float3 = (float)aiNodeAnim.getPosKeyTime(int2);
			float float4 = float3 - float2;
			if (float4 < 0.0F) {
				float4 = (float)((double)float4 + double1);
			}

			if (float4 > 0.0F) {
				float float5 = float3 - float2;
				float float6 = float1 - float2;
				float6 /= float5;
				float float7 = aiNodeAnim.getPosKeyX(int1);
				float float8 = aiNodeAnim.getPosKeyX(int2);
				float float9 = float7 + float6 * (float8 - float7);
				float float10 = aiNodeAnim.getPosKeyY(int1);
				float float11 = aiNodeAnim.getPosKeyY(int2);
				float float12 = float10 + float6 * (float11 - float10);
				float float13 = aiNodeAnim.getPosKeyZ(int1);
				float float14 = aiNodeAnim.getPosKeyZ(int2);
				float float15 = float13 + float6 * (float14 - float13);
				vector3f.set(float9, float12, float15);
			} else {
				vector3f.set(aiNodeAnim.getPosKeyX(int1), aiNodeAnim.getPosKeyY(int1), aiNodeAnim.getPosKeyZ(int1));
			}

			return vector3f;
		}
	}

	private Quaternion GetKeyFrameRotation(AiNodeAnim aiNodeAnim, float float1, double double1) {
		Quaternion quaternion = new Quaternion();
		if (aiNodeAnim.getNumRotKeys() == 0) {
			return quaternion;
		} else {
			int int1;
			for (int1 = 0; int1 < aiNodeAnim.getNumRotKeys() - 1 && !((double)float1 < aiNodeAnim.getRotKeyTime(int1 + 1)); ++int1) {
			}

			int int2 = (int1 + 1) % aiNodeAnim.getNumRotKeys();
			float float2 = (float)aiNodeAnim.getRotKeyTime(int1);
			float float3 = (float)aiNodeAnim.getRotKeyTime(int2);
			float float4 = float3 - float2;
			if (float4 < 0.0F) {
				float4 = (float)((double)float4 + double1);
			}

			float float5;
			if (float4 > 0.0F) {
				float5 = (float1 - float2) / float4;
				AiQuaternion aiQuaternion = (AiQuaternion)aiNodeAnim.getRotKeyQuaternion(int1, this.wrapper);
				AiQuaternion aiQuaternion2 = (AiQuaternion)aiNodeAnim.getRotKeyQuaternion(int2, this.wrapper);
				double double2 = (double)(aiQuaternion.getX() * aiQuaternion2.getX() + aiQuaternion.getY() * aiQuaternion2.getY() + aiQuaternion.getZ() * aiQuaternion2.getZ() + aiQuaternion.getW() * aiQuaternion2.getW());
				this.end.set(aiQuaternion2.getX(), aiQuaternion2.getY(), aiQuaternion2.getZ(), aiQuaternion2.getW());
				if (double2 < 0.0) {
					double2 *= -1.0;
					this.end.setX(-this.end.getX());
					this.end.setY(-this.end.getY());
					this.end.setZ(-this.end.getZ());
					this.end.setW(-this.end.getW());
				}

				double double3;
				double double4;
				if (1.0 - double2 > 1.0E-4) {
					double double5 = Math.acos(double2);
					double double6 = Math.sin(double5);
					double3 = Math.sin((1.0 - (double)float5) * double5) / double6;
					double4 = Math.sin((double)float5 * double5) / double6;
				} else {
					double3 = 1.0 - (double)float5;
					double4 = (double)float5;
				}

				quaternion.set((float)(double3 * (double)aiQuaternion.getX() + double4 * (double)this.end.getX()), (float)(double3 * (double)aiQuaternion.getY() + double4 * (double)this.end.getY()), (float)(double3 * (double)aiQuaternion.getZ() + double4 * (double)this.end.getZ()), (float)(double3 * (double)aiQuaternion.getW() + double4 * (double)this.end.getW()));
			} else {
				float5 = aiNodeAnim.getRotKeyX(int1);
				float float6 = aiNodeAnim.getRotKeyY(int1);
				float float7 = aiNodeAnim.getRotKeyZ(int1);
				float float8 = aiNodeAnim.getRotKeyW(int1);
				quaternion.set(float5, float6, float7, float8);
			}

			return quaternion;
		}
	}

	private Vector3f GetKeyFrameScale(AiNodeAnim aiNodeAnim, float float1, double double1) {
		Vector3f vector3f = new Vector3f(1.0F, 1.0F, 1.0F);
		if (aiNodeAnim.getNumScaleKeys() == 0) {
			return vector3f;
		} else {
			int int1;
			for (int1 = 0; int1 < aiNodeAnim.getNumScaleKeys() - 1 && !((double)float1 < aiNodeAnim.getScaleKeyTime(int1 + 1)); ++int1) {
			}

			vector3f.set(aiNodeAnim.getScaleKeyX(int1), aiNodeAnim.getScaleKeyY(int1), aiNodeAnim.getScaleKeyZ(int1));
			return vector3f;
		}
	}
}
