package zombie.characterTextures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.inventory.types.Clothing;
import zombie.scripting.objects.Item;
import zombie.util.Type;


public enum BloodClothingType {

	Jacket,
	LongJacket,
	Trousers,
	ShortsShort,
	Shirt,
	ShirtLongSleeves,
	ShirtNoSleeves,
	Jumper,
	JumperNoSleeves,
	Shoes,
	FullHelmet,
	Apron,
	Bag,
	Hands,
	Head,
	Neck,
	UpperBody,
	LowerBody,
	LowerLegs,
	UpperLegs,
	LowerArms,
	UpperArms,
	Groin,
	coveredParts,
	bodyParts;

	public static BloodClothingType fromString(String string) {
		if (Jacket.toString().equals(string)) {
			return Jacket;
		} else if (LongJacket.toString().equals(string)) {
			return LongJacket;
		} else if (Trousers.toString().equals(string)) {
			return Trousers;
		} else if (ShortsShort.toString().equals(string)) {
			return ShortsShort;
		} else if (Shirt.toString().equals(string)) {
			return Shirt;
		} else if (ShirtLongSleeves.toString().equals(string)) {
			return ShirtLongSleeves;
		} else if (ShirtNoSleeves.toString().equals(string)) {
			return ShirtNoSleeves;
		} else if (Jumper.toString().equals(string)) {
			return Jumper;
		} else if (JumperNoSleeves.toString().equals(string)) {
			return JumperNoSleeves;
		} else if (Shoes.toString().equals(string)) {
			return Shoes;
		} else if (FullHelmet.toString().equals(string)) {
			return FullHelmet;
		} else if (Bag.toString().equals(string)) {
			return Bag;
		} else if (Hands.toString().equals(string)) {
			return Hands;
		} else if (Head.toString().equals(string)) {
			return Head;
		} else if (Neck.toString().equals(string)) {
			return Neck;
		} else if (Apron.toString().equals(string)) {
			return Apron;
		} else if (Bag.toString().equals(string)) {
			return Bag;
		} else if (Hands.toString().equals(string)) {
			return Hands;
		} else if (Head.toString().equals(string)) {
			return Head;
		} else if (Neck.toString().equals(string)) {
			return Neck;
		} else if (UpperBody.toString().equals(string)) {
			return UpperBody;
		} else if (LowerBody.toString().equals(string)) {
			return LowerBody;
		} else if (LowerLegs.toString().equals(string)) {
			return LowerLegs;
		} else if (UpperLegs.toString().equals(string)) {
			return UpperLegs;
		} else if (LowerArms.toString().equals(string)) {
			return LowerArms;
		} else if (UpperArms.toString().equals(string)) {
			return UpperArms;
		} else {
			return Groin.toString().equals(string) ? Groin : null;
		}
	}
	private static void init() {
		if (coveredParts == null) {
			coveredParts = new HashMap();
			ArrayList arrayList = new ArrayList();
			arrayList.add(BloodBodyPartType.Torso_Upper);
			arrayList.add(BloodBodyPartType.Torso_Lower);
			arrayList.add(BloodBodyPartType.UpperLeg_L);
			arrayList.add(BloodBodyPartType.UpperLeg_R);
			coveredParts.put(Apron, arrayList);
			ArrayList arrayList2 = new ArrayList();
			arrayList2.add(BloodBodyPartType.Torso_Upper);
			arrayList2.add(BloodBodyPartType.Torso_Lower);
			arrayList2.add(BloodBodyPartType.Back);
			coveredParts.put(ShirtNoSleeves, arrayList2);
			coveredParts.put(JumperNoSleeves, arrayList2);
			ArrayList arrayList3 = new ArrayList();
			arrayList3.addAll(arrayList2);
			arrayList3.add(BloodBodyPartType.UpperArm_L);
			arrayList3.add(BloodBodyPartType.UpperArm_R);
			coveredParts.put(Shirt, arrayList3);
			ArrayList arrayList4 = new ArrayList();
			arrayList4.addAll(arrayList3);
			arrayList4.add(BloodBodyPartType.ForeArm_L);
			arrayList4.add(BloodBodyPartType.ForeArm_R);
			coveredParts.put(ShirtLongSleeves, arrayList4);
			coveredParts.put(Jumper, arrayList4);
			ArrayList arrayList5 = new ArrayList();
			arrayList5.addAll(arrayList4);
			arrayList5.add(BloodBodyPartType.Neck);
			coveredParts.put(Jacket, arrayList5);
			ArrayList arrayList6 = new ArrayList();
			arrayList6.addAll(arrayList4);
			arrayList6.add(BloodBodyPartType.Neck);
			arrayList6.add(BloodBodyPartType.Groin);
			arrayList6.add(BloodBodyPartType.UpperLeg_L);
			arrayList6.add(BloodBodyPartType.UpperLeg_R);
			coveredParts.put(LongJacket, arrayList6);
			ArrayList arrayList7 = new ArrayList();
			arrayList7.add(BloodBodyPartType.Groin);
			arrayList7.add(BloodBodyPartType.UpperLeg_L);
			arrayList7.add(BloodBodyPartType.UpperLeg_R);
			coveredParts.put(ShortsShort, arrayList7);
			ArrayList arrayList8 = new ArrayList();
			arrayList8.addAll(arrayList7);
			arrayList8.add(BloodBodyPartType.LowerLeg_L);
			arrayList8.add(BloodBodyPartType.LowerLeg_R);
			coveredParts.put(Trousers, arrayList8);
			ArrayList arrayList9 = new ArrayList();
			arrayList9.add(BloodBodyPartType.Foot_L);
			arrayList9.add(BloodBodyPartType.Foot_R);
			coveredParts.put(Shoes, arrayList9);
			ArrayList arrayList10 = new ArrayList();
			arrayList10.add(BloodBodyPartType.Head);
			coveredParts.put(FullHelmet, arrayList10);
			ArrayList arrayList11 = new ArrayList();
			arrayList11.add(BloodBodyPartType.Back);
			coveredParts.put(Bag, arrayList11);
			ArrayList arrayList12 = new ArrayList();
			arrayList12.add(BloodBodyPartType.Hand_L);
			arrayList12.add(BloodBodyPartType.Hand_R);
			coveredParts.put(Hands, arrayList12);
			ArrayList arrayList13 = new ArrayList();
			arrayList13.add(BloodBodyPartType.Head);
			coveredParts.put(Head, arrayList13);
			ArrayList arrayList14 = new ArrayList();
			arrayList14.add(BloodBodyPartType.Neck);
			coveredParts.put(Neck, arrayList14);
			ArrayList arrayList15 = new ArrayList();
			arrayList15.add(BloodBodyPartType.Groin);
			coveredParts.put(Groin, arrayList15);
			ArrayList arrayList16 = new ArrayList();
			arrayList16.add(BloodBodyPartType.Torso_Upper);
			coveredParts.put(UpperBody, arrayList16);
			ArrayList arrayList17 = new ArrayList();
			arrayList17.add(BloodBodyPartType.Torso_Lower);
			coveredParts.put(LowerBody, arrayList17);
			ArrayList arrayList18 = new ArrayList();
			arrayList18.add(BloodBodyPartType.LowerLeg_L);
			arrayList18.add(BloodBodyPartType.LowerLeg_R);
			coveredParts.put(LowerLegs, arrayList18);
			ArrayList arrayList19 = new ArrayList();
			arrayList19.add(BloodBodyPartType.UpperLeg_L);
			arrayList19.add(BloodBodyPartType.UpperLeg_R);
			coveredParts.put(UpperLegs, arrayList19);
			ArrayList arrayList20 = new ArrayList();
			arrayList20.add(BloodBodyPartType.UpperArm_L);
			arrayList20.add(BloodBodyPartType.UpperArm_R);
			coveredParts.put(UpperArms, arrayList20);
			ArrayList arrayList21 = new ArrayList();
			arrayList21.add(BloodBodyPartType.ForeArm_L);
			arrayList21.add(BloodBodyPartType.ForeArm_R);
			coveredParts.put(LowerArms, arrayList21);
		}
	}
	public static ArrayList getCoveredParts(ArrayList arrayList) {
		return getCoveredParts(arrayList, new ArrayList());
	}
	public static ArrayList getCoveredParts(ArrayList arrayList, ArrayList arrayList2) {
		if (arrayList == null) {
			return arrayList2;
		} else {
			init();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				BloodClothingType bloodClothingType = (BloodClothingType)arrayList.get(int1);
				arrayList2.addAll((Collection)coveredParts.get(bloodClothingType));
			}

			return arrayList2;
		}
	}
	public static int getCoveredPartCount(ArrayList arrayList) {
		if (arrayList == null) {
			return 0;
		} else {
			init();
			int int1 = 0;
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				BloodClothingType bloodClothingType = (BloodClothingType)arrayList.get(int2);
				int1 += ((ArrayList)coveredParts.get(bloodClothingType)).size();
			}

			return int1;
		}
	}
	public static void addBlood(int int1, HumanVisual humanVisual, ArrayList arrayList, boolean boolean1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(Rand.Next(0, BloodBodyPartType.MAX.index()));
			addBlood(bloodBodyPartType, humanVisual, arrayList, boolean1);
		}
	}
	public static void addBlood(BloodBodyPartType bloodBodyPartType, HumanVisual humanVisual, ArrayList arrayList, boolean boolean1) {
		init();
		float float1 = 0.0F;
		if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
			float float2 = 0.01F;
			float float3 = 0.05F;
			if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
				float2 = 0.001F;
				float3 = 0.01F;
			}

			if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
				float2 = 0.05F;
				float3 = 0.1F;
			}

			float1 = OutfitRNG.Next(float2, float3);
		}

		addBlood(bloodBodyPartType, float1, humanVisual, arrayList, boolean1);
	}
	public static void addDirt(BloodBodyPartType bloodBodyPartType, HumanVisual humanVisual, ArrayList arrayList, boolean boolean1) {
		init();
		float float1 = 0.0F;
		if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
			float float2 = 0.01F;
			float float3 = 0.05F;
			if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
				float2 = 0.001F;
				float3 = 0.01F;
			}

			if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
				float2 = 0.05F;
				float3 = 0.1F;
			}

			float1 = OutfitRNG.Next(float2, float3);
		}

		addDirt(bloodBodyPartType, float1, humanVisual, arrayList, boolean1);
	}
	public static void addHole(BloodBodyPartType bloodBodyPartType, HumanVisual humanVisual, ArrayList arrayList) {
		addHole(bloodBodyPartType, humanVisual, arrayList, false);
	}
	public static boolean addHole(BloodBodyPartType bloodBodyPartType, HumanVisual humanVisual, ArrayList arrayList, boolean boolean1) {
		init();
		ItemVisual itemVisual = null;
		boolean boolean2 = false;
		for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
			ItemVisual itemVisual2 = (ItemVisual)arrayList.get(int1);
			Item item = itemVisual2.getScriptItem();
			if (item != null && (itemVisual2.getInventoryItem() == null || !itemVisual2.getInventoryItem().isBroken())) {
				ArrayList arrayList2 = item.getBloodClothingType();
				if (arrayList2 != null) {
					for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
						BloodClothingType bloodClothingType = (BloodClothingType)item.getBloodClothingType().get(int2);
						if (((ArrayList)coveredParts.get(bloodClothingType)).contains(bloodBodyPartType) && item.canHaveHoles && itemVisual2.getHole(bloodBodyPartType) == 0.0F) {
							itemVisual = itemVisual2;
							break;
						}
					}

					if (itemVisual != null) {
						itemVisual.setHole(bloodBodyPartType);
						Clothing clothing = (Clothing)Type.tryCastTo(itemVisual.getInventoryItem(), Clothing.class);
						if (clothing != null) {
							clothing.removePatch(bloodBodyPartType);
							clothing.setCondition(clothing.getCondition() - clothing.getCondLossPerHole());
						}

						boolean2 = true;
						if (!boolean1) {
							break;
						}

						itemVisual = null;
					}
				}
			}
		}

		if (itemVisual == null || boolean1) {
			humanVisual.setHole(bloodBodyPartType);
		}

		return boolean2;
	}
	public static void addBasicPatch(BloodBodyPartType bloodBodyPartType, HumanVisual humanVisual, ArrayList arrayList) {
		init();
		ItemVisual itemVisual = null;
		for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
			ItemVisual itemVisual2 = (ItemVisual)arrayList.get(int1);
			Item item = itemVisual2.getScriptItem();
			if (item != null) {
				ArrayList arrayList2 = item.getBloodClothingType();
				if (arrayList2 != null) {
					for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
						BloodClothingType bloodClothingType = (BloodClothingType)arrayList2.get(int2);
						if (((ArrayList)coveredParts.get(bloodClothingType)).contains(bloodBodyPartType) && itemVisual2.getBasicPatch(bloodBodyPartType) == 0.0F) {
							itemVisual = itemVisual2;
							break;
						}
					}

					if (itemVisual != null) {
						break;
					}
				}
			}
		}

		if (itemVisual != null) {
			itemVisual.removeHole(BloodBodyPartType.ToIndex(bloodBodyPartType));
			itemVisual.setBasicPatch(bloodBodyPartType);
		}
	}
	public static void addDirt(BloodBodyPartType bloodBodyPartType, float float1, HumanVisual humanVisual, ArrayList arrayList, boolean boolean1) {
		init();
		ItemVisual itemVisual = null;
		float float2;
		if (!boolean1) {
			for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
				ItemVisual itemVisual2 = (ItemVisual)arrayList.get(int1);
				Item item = itemVisual2.getScriptItem();
				if (item != null) {
					ArrayList arrayList2 = item.getBloodClothingType();
					if (arrayList2 != null) {
						for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
							BloodClothingType bloodClothingType = (BloodClothingType)arrayList2.get(int2);
							if (((ArrayList)coveredParts.get(bloodClothingType)).contains(bloodBodyPartType) && itemVisual2.getHole(bloodBodyPartType) == 0.0F) {
								itemVisual = itemVisual2;
								break;
							}
						}

						if (itemVisual != null) {
							break;
						}
					}
				}
			}

			if (itemVisual != null) {
				if (float1 > 0.0F) {
					itemVisual.setDirt(bloodBodyPartType, itemVisual.getDirt(bloodBodyPartType) + float1);
					if (itemVisual.getInventoryItem() instanceof Clothing) {
						calcTotalDirtLevel((Clothing)itemVisual.getInventoryItem());
					}
				}
			} else {
				float2 = humanVisual.getDirt(bloodBodyPartType);
				humanVisual.setDirt(bloodBodyPartType, float2 + 0.05F);
			}
		} else {
			float2 = humanVisual.getDirt(bloodBodyPartType);
			humanVisual.setDirt(bloodBodyPartType, float2 + 0.05F);
			float float3 = humanVisual.getDirt(bloodBodyPartType);
			if (Rand.NextBool(Math.abs((new Float(float3 * 100.0F)).intValue() - 100))) {
				return;
			}

			for (int int3 = 0; int3 < arrayList.size(); ++int3) {
				itemVisual = null;
				ItemVisual itemVisual3 = (ItemVisual)arrayList.get(int3);
				Item item2 = itemVisual3.getScriptItem();
				if (item2 != null) {
					ArrayList arrayList3 = item2.getBloodClothingType();
					if (arrayList3 != null) {
						for (int int4 = 0; int4 < arrayList3.size(); ++int4) {
							BloodClothingType bloodClothingType2 = (BloodClothingType)arrayList3.get(int4);
							if (((ArrayList)coveredParts.get(bloodClothingType2)).contains(bloodBodyPartType) && itemVisual3.getHole(bloodBodyPartType) == 0.0F) {
								itemVisual = itemVisual3;
								break;
							}
						}

						if (itemVisual != null) {
							if (float1 > 0.0F) {
								itemVisual.setDirt(bloodBodyPartType, itemVisual.getDirt(bloodBodyPartType) + float1);
								if (itemVisual.getInventoryItem() instanceof Clothing) {
									calcTotalDirtLevel((Clothing)itemVisual.getInventoryItem());
								}

								float3 = itemVisual.getDirt(bloodBodyPartType);
							}

							if (Rand.NextBool(Math.abs((new Float(float3 * 100.0F)).intValue() - 100))) {
								break;
							}
						}
					}
				}
			}
		}
	}
	public static void addBlood(BloodBodyPartType bloodBodyPartType, float float1, HumanVisual humanVisual, ArrayList arrayList, boolean boolean1) {
		init();
		ItemVisual itemVisual = null;
		float float2;
		if (!boolean1) {
			for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
				ItemVisual itemVisual2 = (ItemVisual)arrayList.get(int1);
				Item item = itemVisual2.getScriptItem();
				if (item != null) {
					ArrayList arrayList2 = item.getBloodClothingType();
					if (arrayList2 != null) {
						for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
							BloodClothingType bloodClothingType = (BloodClothingType)arrayList2.get(int2);
							if (((ArrayList)coveredParts.get(bloodClothingType)).contains(bloodBodyPartType) && itemVisual2.getHole(bloodBodyPartType) == 0.0F) {
								itemVisual = itemVisual2;
								break;
							}
						}

						if (itemVisual != null) {
							break;
						}
					}
				}
			}

			if (itemVisual != null) {
				if (float1 > 0.0F) {
					itemVisual.setBlood(bloodBodyPartType, itemVisual.getBlood(bloodBodyPartType) + float1);
					if (itemVisual.getInventoryItem() instanceof Clothing) {
						calcTotalBloodLevel((Clothing)itemVisual.getInventoryItem());
					}
				}
			} else {
				float2 = humanVisual.getBlood(bloodBodyPartType);
				humanVisual.setBlood(bloodBodyPartType, float2 + 0.05F);
			}
		} else {
			float2 = humanVisual.getBlood(bloodBodyPartType);
			humanVisual.setBlood(bloodBodyPartType, float2 + 0.05F);
			float float3 = humanVisual.getBlood(bloodBodyPartType);
			if (OutfitRNG.NextBool(Math.abs((new Float(float3 * 100.0F)).intValue() - 100))) {
				return;
			}

			for (int int3 = 0; int3 < arrayList.size(); ++int3) {
				itemVisual = null;
				ItemVisual itemVisual3 = (ItemVisual)arrayList.get(int3);
				Item item2 = itemVisual3.getScriptItem();
				if (item2 != null) {
					ArrayList arrayList3 = item2.getBloodClothingType();
					if (arrayList3 != null) {
						for (int int4 = 0; int4 < arrayList3.size(); ++int4) {
							BloodClothingType bloodClothingType2 = (BloodClothingType)arrayList3.get(int4);
							if (((ArrayList)coveredParts.get(bloodClothingType2)).contains(bloodBodyPartType) && itemVisual3.getHole(bloodBodyPartType) == 0.0F) {
								itemVisual = itemVisual3;
								break;
							}
						}

						if (itemVisual != null) {
							if (float1 > 0.0F) {
								itemVisual.setBlood(bloodBodyPartType, itemVisual.getBlood(bloodBodyPartType) + float1);
								if (itemVisual.getInventoryItem() instanceof Clothing) {
									calcTotalBloodLevel((Clothing)itemVisual.getInventoryItem());
								}

								float3 = itemVisual.getBlood(bloodBodyPartType);
							}

							if (OutfitRNG.NextBool(Math.abs((new Float(float3 * 100.0F)).intValue() - 100))) {
								break;
							}
						}
					}
				}
			}
		}
	}
	public static synchronized void calcTotalBloodLevel(Clothing clothing) {
		ItemVisual itemVisual = clothing.getVisual();
		if (itemVisual == null) {
			clothing.setBloodLevel(0.0F);
		} else {
			ArrayList arrayList = clothing.getBloodClothingType();
			if (arrayList == null) {
				clothing.setBloodLevel(0.0F);
			} else {
				bodyParts.clear();
				getCoveredParts(arrayList, bodyParts);
				if (bodyParts.isEmpty()) {
					clothing.setBloodLevel(0.0F);
				} else {
					float float1 = 0.0F;
					for (int int1 = 0; int1 < bodyParts.size(); ++int1) {
						float1 += itemVisual.getBlood((BloodBodyPartType)bodyParts.get(int1)) * 100.0F;
					}

					clothing.setBloodLevel(float1 / (float)bodyParts.size());
				}
			}
		}
	}
	public static synchronized void calcTotalDirtLevel(Clothing clothing) {
		ItemVisual itemVisual = clothing.getVisual();
		if (itemVisual == null) {
			clothing.setDirtyness(0.0F);
		} else {
			ArrayList arrayList = clothing.getBloodClothingType();
			if (arrayList == null) {
				clothing.setDirtyness(0.0F);
			} else {
				bodyParts.clear();
				getCoveredParts(arrayList, bodyParts);
				if (bodyParts.isEmpty()) {
					clothing.setDirtyness(0.0F);
				} else {
					float float1 = 0.0F;
					for (int int1 = 0; int1 < bodyParts.size(); ++int1) {
						float1 += itemVisual.getDirt((BloodBodyPartType)bodyParts.get(int1)) * 100.0F;
					}

					clothing.setDirtyness(float1 / (float)bodyParts.size());
				}
			}
		}
	}
	private static BloodClothingType[] $values() {
		return new BloodClothingType[]{Jacket, LongJacket, Trousers, ShortsShort, Shirt, ShirtLongSleeves, ShirtNoSleeves, Jumper, JumperNoSleeves, Shoes, FullHelmet, Apron, Bag, Hands, Head, Neck, UpperBody, LowerBody, LowerLegs, UpperLegs, LowerArms, UpperArms, Groin};
	}
}
