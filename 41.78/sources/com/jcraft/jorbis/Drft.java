package com.jcraft.jorbis;


class Drft {
	static int[] ntryh = new int[]{4, 2, 3, 5};
	static float tpi = 6.2831855F;
	static float hsqt2 = 0.70710677F;
	static float taui = 0.8660254F;
	static float taur = -0.5F;
	static float sqrt2 = 1.4142135F;
	int n;
	int[] splitcache;
	float[] trigcache;

	static void dradb2(int int1, int int2, float[] floatArray, float[] floatArray2, float[] floatArray3, int int3) {
		int int4 = int2 * int1;
		int int5 = 0;
		int int6 = 0;
		int int7 = (int1 << 1) - 1;
		int int8;
		for (int8 = 0; int8 < int2; ++int8) {
			floatArray2[int5] = floatArray[int6] + floatArray[int7 + int6];
			floatArray2[int5 + int4] = floatArray[int6] - floatArray[int7 + int6];
			int6 = (int5 += int1) << 1;
		}

		if (int1 >= 2) {
			if (int1 != 2) {
				int5 = 0;
				int6 = 0;
				for (int8 = 0; int8 < int2; ++int8) {
					int7 = int5;
					int int9 = int6;
					int int10 = int6 + (int1 << 1);
					int int11 = int4 + int5;
					for (int int12 = 2; int12 < int1; int12 += 2) {
						int7 += 2;
						int9 += 2;
						int10 -= 2;
						int11 += 2;
						floatArray2[int7 - 1] = floatArray[int9 - 1] + floatArray[int10 - 1];
						float float1 = floatArray[int9 - 1] - floatArray[int10 - 1];
						floatArray2[int7] = floatArray[int9] - floatArray[int10];
						float float2 = floatArray[int9] + floatArray[int10];
						floatArray2[int11 - 1] = floatArray3[int3 + int12 - 2] * float1 - floatArray3[int3 + int12 - 1] * float2;
						floatArray2[int11] = floatArray3[int3 + int12 - 2] * float2 + floatArray3[int3 + int12 - 1] * float1;
					}

					int6 = (int5 += int1) << 1;
				}

				if (int1 % 2 == 1) {
					return;
				}
			}

			int5 = int1 - 1;
			int6 = int1 - 1;
			for (int8 = 0; int8 < int2; ++int8) {
				floatArray2[int5] = floatArray[int6] + floatArray[int6];
				floatArray2[int5 + int4] = -(floatArray[int6 + 1] + floatArray[int6 + 1]);
				int5 += int1;
				int6 += int1 << 1;
			}
		}
	}

	static void dradb3(int int1, int int2, float[] floatArray, float[] floatArray2, float[] floatArray3, int int3, float[] floatArray4, int int4) {
		int int5 = int2 * int1;
		int int6 = 0;
		int int7 = int5 << 1;
		int int8 = int1 << 1;
		int int9 = int1 + (int1 << 1);
		int int10 = 0;
		int int11;
		float float1;
		float float2;
		float float3;
		for (int11 = 0; int11 < int2; ++int11) {
			float3 = floatArray[int8 - 1] + floatArray[int8 - 1];
			float2 = floatArray[int10] + taur * float3;
			floatArray2[int6] = floatArray[int10] + float3;
			float1 = taui * (floatArray[int8] + floatArray[int8]);
			floatArray2[int6 + int5] = float2 - float1;
			floatArray2[int6 + int7] = float2 + float1;
			int6 += int1;
			int8 += int9;
			int10 += int9;
		}

		if (int1 != 1) {
			int6 = 0;
			int8 = int1 << 1;
			for (int11 = 0; int11 < int2; ++int11) {
				int int12 = int6 + (int6 << 1);
				int int13 = int10 = int12 + int8;
				int int14 = int6;
				int int15;
				int int16 = (int15 = int6 + int5) + int5;
				for (int int17 = 2; int17 < int1; int17 += 2) {
					int10 += 2;
					int13 -= 2;
					int12 += 2;
					int14 += 2;
					int15 += 2;
					int16 += 2;
					float3 = floatArray[int10 - 1] + floatArray[int13 - 1];
					float2 = floatArray[int12 - 1] + taur * float3;
					floatArray2[int14 - 1] = floatArray[int12 - 1] + float3;
					float float4 = floatArray[int10] - floatArray[int13];
					float float5 = floatArray[int12] + taur * float4;
					floatArray2[int14] = floatArray[int12] + float4;
					float float6 = taui * (floatArray[int10 - 1] - floatArray[int13 - 1]);
					float1 = taui * (floatArray[int10] + floatArray[int13]);
					float float7 = float2 - float1;
					float float8 = float2 + float1;
					float float9 = float5 + float6;
					float float10 = float5 - float6;
					floatArray2[int15 - 1] = floatArray3[int3 + int17 - 2] * float7 - floatArray3[int3 + int17 - 1] * float9;
					floatArray2[int15] = floatArray3[int3 + int17 - 2] * float9 + floatArray3[int3 + int17 - 1] * float7;
					floatArray2[int16 - 1] = floatArray4[int4 + int17 - 2] * float8 - floatArray4[int4 + int17 - 1] * float10;
					floatArray2[int16] = floatArray4[int4 + int17 - 2] * float10 + floatArray4[int4 + int17 - 1] * float8;
				}

				int6 += int1;
			}
		}
	}

	static void dradb4(int int1, int int2, float[] floatArray, float[] floatArray2, float[] floatArray3, int int3, float[] floatArray4, int int4, float[] floatArray5, int int5) {
		int int6 = int2 * int1;
		int int7 = 0;
		int int8 = int1 << 2;
		int int9 = 0;
		int int10 = int1 << 1;
		int int11;
		int int12;
		int int13;
		float float1;
		float float2;
		float float3;
		float float4;
		for (int11 = 0; int11 < int2; ++int11) {
			int12 = int9 + int10;
			float3 = floatArray[int12 - 1] + floatArray[int12 - 1];
			float4 = floatArray[int12] + floatArray[int12];
			float1 = floatArray[int9] - floatArray[(int12 += int10) - 1];
			float2 = floatArray[int9] + floatArray[int12 - 1];
			floatArray2[int7] = float2 + float3;
			floatArray2[int13 = int7 + int6] = float1 - float4;
			floatArray2[int13 += int6] = float2 - float3;
			floatArray2[int13 + int6] = float1 + float4;
			int7 += int1;
			int9 += int8;
		}

		if (int1 >= 2) {
			float float5;
			float float6;
			if (int1 != 2) {
				int7 = 0;
				for (int11 = 0; int11 < int2; ++int11) {
					int13 = (int12 = int9 = (int8 = int7 << 2) + int10) + int10;
					int int14 = int7;
					for (int int15 = 2; int15 < int1; int15 += 2) {
						int8 += 2;
						int9 += 2;
						int12 -= 2;
						int13 -= 2;
						int14 += 2;
						float5 = floatArray[int8] + floatArray[int13];
						float6 = floatArray[int8] - floatArray[int13];
						float float7 = floatArray[int9] - floatArray[int12];
						float4 = floatArray[int9] + floatArray[int12];
						float1 = floatArray[int8 - 1] - floatArray[int13 - 1];
						float2 = floatArray[int8 - 1] + floatArray[int13 - 1];
						float float8 = floatArray[int9 - 1] - floatArray[int12 - 1];
						float3 = floatArray[int9 - 1] + floatArray[int12 - 1];
						floatArray2[int14 - 1] = float2 + float3;
						float float9 = float2 - float3;
						floatArray2[int14] = float6 + float7;
						float float10 = float6 - float7;
						float float11 = float1 - float4;
						float float12 = float1 + float4;
						float float13 = float5 + float8;
						float float14 = float5 - float8;
						int int16;
						floatArray2[(int16 = int14 + int6) - 1] = floatArray3[int3 + int15 - 2] * float11 - floatArray3[int3 + int15 - 1] * float13;
						floatArray2[int16] = floatArray3[int3 + int15 - 2] * float13 + floatArray3[int3 + int15 - 1] * float11;
						floatArray2[(int16 += int6) - 1] = floatArray4[int4 + int15 - 2] * float9 - floatArray4[int4 + int15 - 1] * float10;
						floatArray2[int16] = floatArray4[int4 + int15 - 2] * float10 + floatArray4[int4 + int15 - 1] * float9;
						floatArray2[(int16 += int6) - 1] = floatArray5[int5 + int15 - 2] * float12 - floatArray5[int5 + int15 - 1] * float14;
						floatArray2[int16] = floatArray5[int5 + int15 - 2] * float14 + floatArray5[int5 + int15 - 1] * float12;
					}

					int7 += int1;
				}

				if (int1 % 2 == 1) {
					return;
				}
			}

			int7 = int1;
			int8 = int1 << 2;
			int9 = int1 - 1;
			int12 = int1 + (int1 << 1);
			for (int11 = 0; int11 < int2; ++int11) {
				float5 = floatArray[int7] + floatArray[int12];
				float6 = floatArray[int12] - floatArray[int7];
				float1 = floatArray[int7 - 1] - floatArray[int12 - 1];
				float2 = floatArray[int7 - 1] + floatArray[int12 - 1];
				floatArray2[int9] = float2 + float2;
				floatArray2[int13 = int9 + int6] = sqrt2 * (float1 - float5);
				floatArray2[int13 += int6] = float6 + float6;
				floatArray2[int13 + int6] = -sqrt2 * (float1 + float5);
				int9 += int1;
				int7 += int8;
				int12 += int8;
			}
		}
	}

	static void dradbg(int int1, int int2, int int3, int int4, float[] floatArray, float[] floatArray2, float[] floatArray3, float[] floatArray4, float[] floatArray5, float[] floatArray6, int int5) {
		int int6 = 0;
		int int7 = 0;
		int int8 = 0;
		int int9 = 0;
		float float1 = 0.0F;
		float float2 = 0.0F;
		int int10 = 0;
		short short1 = 100;
		while (true) {
			int int11;
			int int12;
			int int13;
			int int14;
			int int15;
			int int16;
			int int17;
			int int18;
			int int19;
			int int20;
			int int21;
			int int22;
			int int23;
			int int24;
			int int25;
			int int26;
			int int27;
			switch (short1) {
			case 100: 
				int8 = int2 * int1;
				int7 = int3 * int1;
				float float3 = tpi / (float)int2;
				float1 = (float)Math.cos((double)float3);
				float2 = (float)Math.sin((double)float3);
				int9 = int1 - 1 >>> 1;
				int10 = int2;
				int6 = int2 + 1 >>> 1;
				if (int1 < int3) {
					short1 = 103;
					break;
				}

				int17 = 0;
				int18 = 0;
				for (int14 = 0; int14 < int3; ++int14) {
					int19 = int17;
					int20 = int18;
					for (int12 = 0; int12 < int1; ++int12) {
						floatArray4[int19] = floatArray[int20];
						++int19;
						++int20;
					}

					int17 += int1;
					int18 += int8;
				}

				short1 = 106;
				break;
			
			case 103: 
				int17 = 0;
				for (int12 = 0; int12 < int1; ++int12) {
					int18 = int17;
					int19 = int17;
					for (int14 = 0; int14 < int3; ++int14) {
						floatArray4[int18] = floatArray[int19];
						int18 += int1;
						int19 += int8;
					}

					++int17;
				}

			
			case 106: 
				int17 = 0;
				int18 = int10 * int7;
				int23 = int21 = int1 << 1;
				for (int13 = 1; int13 < int6; ++int13) {
					int17 += int7;
					int18 -= int7;
					int19 = int17;
					int20 = int18;
					int22 = int21;
					for (int14 = 0; int14 < int3; ++int14) {
						floatArray4[int19] = floatArray[int22 - 1] + floatArray[int22 - 1];
						floatArray4[int20] = floatArray[int22] + floatArray[int22];
						int19 += int1;
						int20 += int1;
						int22 += int8;
					}

					int21 += int23;
				}

				if (int1 == 1) {
					short1 = 116;
				} else {
					if (int9 < int3) {
						short1 = 112;
						break;
					}

					int17 = 0;
					int18 = int10 * int7;
					int23 = 0;
					for (int13 = 1; int13 < int6; ++int13) {
						int17 += int7;
						int18 -= int7;
						int19 = int17;
						int20 = int18;
						int23 += int1 << 1;
						int24 = int23;
						for (int14 = 0; int14 < int3; ++int14) {
							int21 = int19;
							int22 = int20;
							int25 = int24;
							int26 = int24;
							for (int12 = 2; int12 < int1; int12 += 2) {
								int21 += 2;
								int22 += 2;
								int25 += 2;
								int26 -= 2;
								floatArray4[int21 - 1] = floatArray[int25 - 1] + floatArray[int26 - 1];
								floatArray4[int22 - 1] = floatArray[int25 - 1] - floatArray[int26 - 1];
								floatArray4[int21] = floatArray[int25] - floatArray[int26];
								floatArray4[int22] = floatArray[int25] + floatArray[int26];
							}

							int19 += int1;
							int20 += int1;
							int24 += int8;
						}
					}

					short1 = 116;
				}

				break;
			
			case 112: 
				int17 = 0;
				int18 = int10 * int7;
				int23 = 0;
				for (int13 = 1; int13 < int6; ++int13) {
					int17 += int7;
					int18 -= int7;
					int19 = int17;
					int20 = int18;
					int23 += int1 << 1;
					int24 = int23;
					int25 = int23;
					for (int12 = 2; int12 < int1; int12 += 2) {
						int19 += 2;
						int20 += 2;
						int24 += 2;
						int25 -= 2;
						int21 = int19;
						int22 = int20;
						int26 = int24;
						int27 = int25;
						for (int14 = 0; int14 < int3; ++int14) {
							floatArray4[int21 - 1] = floatArray[int26 - 1] + floatArray[int27 - 1];
							floatArray4[int22 - 1] = floatArray[int26 - 1] - floatArray[int27 - 1];
							floatArray4[int21] = floatArray[int26] - floatArray[int27];
							floatArray4[int22] = floatArray[int26] + floatArray[int27];
							int21 += int1;
							int22 += int1;
							int26 += int8;
							int27 += int8;
						}
					}
				}

			
			case 116: 
				float float4 = 1.0F;
				float float5 = 0.0F;
				int17 = 0;
				int25 = int18 = int10 * int4;
				int19 = (int2 - 1) * int4;
				for (int int28 = 1; int28 < int6; ++int28) {
					int17 += int4;
					int18 -= int4;
					float float6 = float1 * float4 - float2 * float5;
					float5 = float1 * float5 + float2 * float4;
					float4 = float6;
					int20 = int17;
					int21 = int18;
					int22 = 0;
					int23 = int4;
					int24 = int19;
					for (int15 = 0; int15 < int4; ++int15) {
						floatArray3[int20++] = floatArray5[int22++] + float4 * floatArray5[int23++];
						floatArray3[int21++] = float5 * floatArray5[int24++];
					}

					float float7 = float4;
					float float8 = float5;
					float float9 = float4;
					float float10 = float5;
					int22 = int4;
					int23 = int25 - int4;
					for (int13 = 2; int13 < int6; ++int13) {
						int22 += int4;
						int23 -= int4;
						float float11 = float7 * float9 - float8 * float10;
						float10 = float7 * float10 + float8 * float9;
						float9 = float11;
						int20 = int17;
						int21 = int18;
						int26 = int22;
						int27 = int23;
						for (int15 = 0; int15 < int4; ++int15) {
							int int29 = int20++;
							floatArray3[int29] += float9 * floatArray5[int26++];
							int29 = int21++;
							floatArray3[int29] += float10 * floatArray5[int27++];
						}
					}
				}

				int17 = 0;
				for (int13 = 1; int13 < int6; ++int13) {
					int17 += int4;
					int18 = int17;
					for (int15 = 0; int15 < int4; ++int15) {
						floatArray5[int15] += floatArray5[int18++];
					}
				}

				int17 = 0;
				int18 = int10 * int7;
				for (int13 = 1; int13 < int6; ++int13) {
					int17 += int7;
					int18 -= int7;
					int19 = int17;
					int20 = int18;
					for (int14 = 0; int14 < int3; ++int14) {
						floatArray4[int19] = floatArray2[int19] - floatArray2[int20];
						floatArray4[int20] = floatArray2[int19] + floatArray2[int20];
						int19 += int1;
						int20 += int1;
					}
				}

				if (int1 == 1) {
					short1 = 132;
				} else {
					if (int9 < int3) {
						short1 = 128;
						break;
					}

					int17 = 0;
					int18 = int10 * int7;
					for (int13 = 1; int13 < int6; ++int13) {
						int17 += int7;
						int18 -= int7;
						int19 = int17;
						int20 = int18;
						for (int14 = 0; int14 < int3; ++int14) {
							int21 = int19;
							int22 = int20;
							for (int12 = 2; int12 < int1; int12 += 2) {
								int21 += 2;
								int22 += 2;
								floatArray4[int21 - 1] = floatArray2[int21 - 1] - floatArray2[int22];
								floatArray4[int22 - 1] = floatArray2[int21 - 1] + floatArray2[int22];
								floatArray4[int21] = floatArray2[int21] + floatArray2[int22 - 1];
								floatArray4[int22] = floatArray2[int21] - floatArray2[int22 - 1];
							}

							int19 += int1;
							int20 += int1;
						}
					}

					short1 = 132;
				}

				break;
			
			case 128: 
				int17 = 0;
				int18 = int10 * int7;
				for (int13 = 1; int13 < int6; ++int13) {
					int17 += int7;
					int18 -= int7;
					int19 = int17;
					int20 = int18;
					for (int12 = 2; int12 < int1; int12 += 2) {
						int19 += 2;
						int20 += 2;
						int21 = int19;
						int22 = int20;
						for (int14 = 0; int14 < int3; ++int14) {
							floatArray4[int21 - 1] = floatArray2[int21 - 1] - floatArray2[int22];
							floatArray4[int22 - 1] = floatArray2[int21 - 1] + floatArray2[int22];
							floatArray4[int21] = floatArray2[int21] + floatArray2[int22 - 1];
							floatArray4[int22] = floatArray2[int21] - floatArray2[int22 - 1];
							int21 += int1;
							int22 += int1;
						}
					}
				}

			
			case 132: 
				if (int1 == 1) {
					return;
				}

				for (int15 = 0; int15 < int4; ++int15) {
					floatArray3[int15] = floatArray5[int15];
				}

				int17 = 0;
				for (int13 = 1; int13 < int2; ++int13) {
					int18 = int17 += int7;
					for (int14 = 0; int14 < int3; ++int14) {
						floatArray2[int18] = floatArray4[int18];
						int18 += int1;
					}
				}

				if (int9 <= int3) {
					int16 = -int1 - 1;
					int17 = 0;
					for (int13 = 1; int13 < int2; ++int13) {
						int16 += int1;
						int17 += int7;
						int11 = int16;
						int18 = int17;
						for (int12 = 2; int12 < int1; int12 += 2) {
							int18 += 2;
							int11 += 2;
							int19 = int18;
							for (int14 = 0; int14 < int3; ++int14) {
								floatArray2[int19 - 1] = floatArray6[int5 + int11 - 1] * floatArray4[int19 - 1] - floatArray6[int5 + int11] * floatArray4[int19];
								floatArray2[int19] = floatArray6[int5 + int11 - 1] * floatArray4[int19] + floatArray6[int5 + int11] * floatArray4[int19 - 1];
								int19 += int1;
							}
						}
					}

					return;
				}

				short1 = 139;
				break;
			
			case 139: 
				int16 = -int1 - 1;
				int17 = 0;
				for (int13 = 1; int13 < int2; ++int13) {
					int16 += int1;
					int17 += int7;
					int18 = int17;
					for (int14 = 0; int14 < int3; ++int14) {
						int11 = int16;
						int19 = int18;
						for (int12 = 2; int12 < int1; int12 += 2) {
							int11 += 2;
							int19 += 2;
							floatArray2[int19 - 1] = floatArray6[int5 + int11 - 1] * floatArray4[int19 - 1] - floatArray6[int5 + int11] * floatArray4[int19];
							floatArray2[int19] = floatArray6[int5 + int11 - 1] * floatArray4[int19] + floatArray6[int5 + int11] * floatArray4[int19 - 1];
						}

						int18 += int1;
					}
				}

				return;
			
			}
		}
	}

	static void dradf2(int int1, int int2, float[] floatArray, float[] floatArray2, float[] floatArray3, int int3) {
		int int4 = 0;
		int int5;
		int int6 = int5 = int2 * int1;
		int int7 = int1 << 1;
		int int8;
		for (int8 = 0; int8 < int2; ++int8) {
			floatArray2[int4 << 1] = floatArray[int4] + floatArray[int5];
			floatArray2[(int4 << 1) + int7 - 1] = floatArray[int4] - floatArray[int5];
			int4 += int1;
			int5 += int1;
		}

		if (int1 >= 2) {
			if (int1 != 2) {
				int4 = 0;
				int5 = int6;
				for (int8 = 0; int8 < int2; ++int8) {
					int7 = int5;
					int int9 = (int4 << 1) + (int1 << 1);
					int int10 = int4;
					int int11 = int4 + int4;
					for (int int12 = 2; int12 < int1; int12 += 2) {
						int7 += 2;
						int9 -= 2;
						int10 += 2;
						int11 += 2;
						float float1 = floatArray3[int3 + int12 - 2] * floatArray[int7 - 1] + floatArray3[int3 + int12 - 1] * floatArray[int7];
						float float2 = floatArray3[int3 + int12 - 2] * floatArray[int7] - floatArray3[int3 + int12 - 1] * floatArray[int7 - 1];
						floatArray2[int11] = floatArray[int10] + float2;
						floatArray2[int9] = float2 - floatArray[int10];
						floatArray2[int11 - 1] = floatArray[int10 - 1] + float1;
						floatArray2[int9 - 1] = floatArray[int10 - 1] - float1;
					}

					int4 += int1;
					int5 += int1;
				}

				if (int1 % 2 == 1) {
					return;
				}
			}

			int4 = int1;
			int7 = int5 = int1 - 1;
			int5 += int6;
			for (int8 = 0; int8 < int2; ++int8) {
				floatArray2[int4] = -floatArray[int5];
				floatArray2[int4 - 1] = floatArray[int7];
				int4 += int1 << 1;
				int5 += int1;
				int7 += int1;
			}
		}
	}

	static void dradf4(int int1, int int2, float[] floatArray, float[] floatArray2, float[] floatArray3, int int3, float[] floatArray4, int int4, float[] floatArray5, int int5) {
		int int6 = int2 * int1;
		int int7 = int6;
		int int8 = int6 << 1;
		int int9 = int6 + (int6 << 1);
		int int10 = 0;
		int int11;
		int int12;
		float float1;
		float float2;
		for (int11 = 0; int11 < int2; ++int11) {
			float1 = floatArray[int7] + floatArray[int9];
			float2 = floatArray[int10] + floatArray[int8];
			floatArray2[int12 = int10 << 2] = float1 + float2;
			floatArray2[(int1 << 2) + int12 - 1] = float2 - float1;
			floatArray2[(int12 += int1 << 1) - 1] = floatArray[int10] - floatArray[int8];
			floatArray2[int12] = floatArray[int9] - floatArray[int7];
			int7 += int1;
			int9 += int1;
			int10 += int1;
			int8 += int1;
		}

		if (int1 >= 2) {
			int int13;
			float float3;
			if (int1 != 2) {
				int7 = 0;
				for (int11 = 0; int11 < int2; ++int11) {
					int9 = int7;
					int8 = int7 << 2;
					int12 = (int13 = int1 << 1) + int8;
					for (int int14 = 2; int14 < int1; int14 += 2) {
						int9 += 2;
						int8 += 2;
						int12 -= 2;
						int10 = int9 + int6;
						float float4 = floatArray3[int3 + int14 - 2] * floatArray[int10 - 1] + floatArray3[int3 + int14 - 1] * floatArray[int10];
						float float5 = floatArray3[int3 + int14 - 2] * floatArray[int10] - floatArray3[int3 + int14 - 1] * floatArray[int10 - 1];
						int10 += int6;
						float float6 = floatArray4[int4 + int14 - 2] * floatArray[int10 - 1] + floatArray4[int4 + int14 - 1] * floatArray[int10];
						float float7 = floatArray4[int4 + int14 - 2] * floatArray[int10] - floatArray4[int4 + int14 - 1] * floatArray[int10 - 1];
						int10 += int6;
						float float8 = floatArray5[int5 + int14 - 2] * floatArray[int10 - 1] + floatArray5[int5 + int14 - 1] * floatArray[int10];
						float float9 = floatArray5[int5 + int14 - 2] * floatArray[int10] - floatArray5[int5 + int14 - 1] * floatArray[int10 - 1];
						float1 = float4 + float8;
						float float10 = float8 - float4;
						float3 = float5 + float9;
						float float11 = float5 - float9;
						float float12 = floatArray[int9] + float7;
						float float13 = floatArray[int9] - float7;
						float2 = floatArray[int9 - 1] + float6;
						float float14 = floatArray[int9 - 1] - float6;
						floatArray2[int8 - 1] = float1 + float2;
						floatArray2[int8] = float3 + float12;
						floatArray2[int12 - 1] = float14 - float11;
						floatArray2[int12] = float10 - float13;
						floatArray2[int8 + int13 - 1] = float11 + float14;
						floatArray2[int8 + int13] = float10 + float13;
						floatArray2[int12 + int13 - 1] = float2 - float1;
						floatArray2[int12 + int13] = float3 - float12;
					}

					int7 += int1;
				}

				if ((int1 & 1) != 0) {
					return;
				}
			}

			int9 = (int7 = int6 + int1 - 1) + (int6 << 1);
			int10 = int1 << 2;
			int8 = int1;
			int12 = int1 << 1;
			int13 = int1;
			for (int11 = 0; int11 < int2; ++int11) {
				float3 = -hsqt2 * (floatArray[int7] + floatArray[int9]);
				float1 = hsqt2 * (floatArray[int7] - floatArray[int9]);
				floatArray2[int8 - 1] = float1 + floatArray[int13 - 1];
				floatArray2[int8 + int12 - 1] = floatArray[int13 - 1] - float1;
				floatArray2[int8] = float3 - floatArray[int7 + int6];
				floatArray2[int8 + int12] = float3 + floatArray[int7 + int6];
				int7 += int1;
				int9 += int1;
				int8 += int10;
				int13 += int1;
			}
		}
	}

	static void dradfg(int int1, int int2, int int3, int int4, float[] floatArray, float[] floatArray2, float[] floatArray3, float[] floatArray4, float[] floatArray5, float[] floatArray6, int int5) {
		int int6 = 0;
		float float1 = 0.0F;
		float float2 = 0.0F;
		float float3 = tpi / (float)int2;
		float1 = (float)Math.cos((double)float3);
		float2 = (float)Math.sin((double)float3);
		int int7 = int2 + 1 >> 1;
		int int8 = int2;
		int int9 = int1;
		int int10 = int1 - 1 >> 1;
		int int11 = int3 * int1;
		int int12 = int2 * int1;
		short short1 = 100;
		while (true) {
			int int13;
			int int14;
			int int15;
			int int16;
			int int17;
			int int18;
			int int19;
			int int20;
			int int21;
			int int22;
			int int23;
			int int24;
			switch (short1) {
			case 101: 
				if (int1 == 1) {
					short1 = 119;
					break;
				} else {
					for (int16 = 0; int16 < int4; ++int16) {
						floatArray5[int16] = floatArray3[int16];
					}

					int17 = 0;
					for (int14 = 1; int14 < int2; ++int14) {
						int17 += int11;
						int6 = int17;
						for (int15 = 0; int15 < int3; ++int15) {
							floatArray4[int6] = floatArray2[int6];
							int6 += int1;
						}
					}

					int int25 = -int1;
					int17 = 0;
					int int26;
					if (int10 > int3) {
						for (int14 = 1; int14 < int2; ++int14) {
							int17 += int11;
							int25 += int1;
							int6 = -int1 + int17;
							for (int15 = 0; int15 < int3; ++int15) {
								int26 = int25 - 1;
								int6 += int1;
								int18 = int6;
								for (int13 = 2; int13 < int1; int13 += 2) {
									int26 += 2;
									int18 += 2;
									floatArray4[int18 - 1] = floatArray6[int5 + int26 - 1] * floatArray2[int18 - 1] + floatArray6[int5 + int26] * floatArray2[int18];
									floatArray4[int18] = floatArray6[int5 + int26 - 1] * floatArray2[int18] - floatArray6[int5 + int26] * floatArray2[int18 - 1];
								}
							}
						}
					} else {
						for (int14 = 1; int14 < int2; ++int14) {
							int25 += int1;
							int26 = int25 - 1;
							int17 += int11;
							int6 = int17;
							for (int13 = 2; int13 < int1; int13 += 2) {
								int26 += 2;
								int6 += 2;
								int18 = int6;
								for (int15 = 0; int15 < int3; ++int15) {
									floatArray4[int18 - 1] = floatArray6[int5 + int26 - 1] * floatArray2[int18 - 1] + floatArray6[int5 + int26] * floatArray2[int18];
									floatArray4[int18] = floatArray6[int5 + int26 - 1] * floatArray2[int18] - floatArray6[int5 + int26] * floatArray2[int18 - 1];
									int18 += int1;
								}
							}
						}
					}

					int17 = 0;
					int6 = int8 * int11;
					if (int10 < int3) {
						for (int14 = 1; int14 < int7; ++int14) {
							int17 += int11;
							int6 -= int11;
							int18 = int17;
							int19 = int6;
							for (int13 = 2; int13 < int1; int13 += 2) {
								int18 += 2;
								int19 += 2;
								int20 = int18 - int1;
								int21 = int19 - int1;
								for (int15 = 0; int15 < int3; ++int15) {
									int20 += int1;
									int21 += int1;
									floatArray2[int20 - 1] = floatArray4[int20 - 1] + floatArray4[int21 - 1];
									floatArray2[int21 - 1] = floatArray4[int20] - floatArray4[int21];
									floatArray2[int20] = floatArray4[int20] + floatArray4[int21];
									floatArray2[int21] = floatArray4[int21 - 1] - floatArray4[int20 - 1];
								}
							}
						}
					} else {
						for (int14 = 1; int14 < int7; ++int14) {
							int17 += int11;
							int6 -= int11;
							int18 = int17;
							int19 = int6;
							for (int15 = 0; int15 < int3; ++int15) {
								int20 = int18;
								int21 = int19;
								for (int13 = 2; int13 < int1; int13 += 2) {
									int20 += 2;
									int21 += 2;
									floatArray2[int20 - 1] = floatArray4[int20 - 1] + floatArray4[int21 - 1];
									floatArray2[int21 - 1] = floatArray4[int20] - floatArray4[int21];
									floatArray2[int20] = floatArray4[int20] + floatArray4[int21];
									floatArray2[int21] = floatArray4[int21 - 1] - floatArray4[int20 - 1];
								}

								int18 += int1;
								int19 += int1;
							}
						}
					}
				}

			
			case 119: 
				for (int16 = 0; int16 < int4; ++int16) {
					floatArray3[int16] = floatArray5[int16];
				}

				int17 = 0;
				int6 = int8 * int4;
				for (int14 = 1; int14 < int7; ++int14) {
					int17 += int11;
					int6 -= int11;
					int18 = int17 - int1;
					int19 = int6 - int1;
					for (int15 = 0; int15 < int3; ++int15) {
						int18 += int1;
						int19 += int1;
						floatArray2[int18] = floatArray4[int18] + floatArray4[int19];
						floatArray2[int19] = floatArray4[int19] - floatArray4[int18];
					}
				}

				float float4 = 1.0F;
				float float5 = 0.0F;
				int17 = 0;
				int6 = int8 * int4;
				int18 = (int2 - 1) * int4;
				for (int int27 = 1; int27 < int7; ++int27) {
					int17 += int4;
					int6 -= int4;
					float float6 = float1 * float4 - float2 * float5;
					float5 = float1 * float5 + float2 * float4;
					float4 = float6;
					int19 = int17;
					int20 = int6;
					int21 = int18;
					int22 = int4;
					for (int16 = 0; int16 < int4; ++int16) {
						floatArray5[int19++] = floatArray3[int16] + float4 * floatArray3[int22++];
						floatArray5[int20++] = float5 * floatArray3[int21++];
					}

					float float7 = float4;
					float float8 = float5;
					float float9 = float4;
					float float10 = float5;
					int19 = int4;
					int20 = (int8 - 1) * int4;
					for (int14 = 2; int14 < int7; ++int14) {
						int19 += int4;
						int20 -= int4;
						float float11 = float7 * float9 - float8 * float10;
						float10 = float7 * float10 + float8 * float9;
						float9 = float11;
						int21 = int17;
						int22 = int6;
						int23 = int19;
						int24 = int20;
						for (int16 = 0; int16 < int4; ++int16) {
							int int28 = int21++;
							floatArray5[int28] += float9 * floatArray3[int23++];
							int28 = int22++;
							floatArray5[int28] += float10 * floatArray3[int24++];
						}
					}
				}

				int17 = 0;
				for (int14 = 1; int14 < int7; ++int14) {
					int17 += int4;
					int6 = int17;
					for (int16 = 0; int16 < int4; ++int16) {
						floatArray5[int16] += floatArray3[int6++];
					}
				}

				if (int1 < int3) {
					short1 = 132;
					break;
				}

				int17 = 0;
				int6 = 0;
				for (int15 = 0; int15 < int3; ++int15) {
					int18 = int17;
					int19 = int6;
					for (int13 = 0; int13 < int1; ++int13) {
						floatArray[int19++] = floatArray4[int18++];
					}

					int17 += int1;
					int6 += int12;
				}

				short1 = 135;
				break;
			
			case 132: 
				for (int13 = 0; int13 < int1; ++int13) {
					int17 = int13;
					int6 = int13;
					for (int15 = 0; int15 < int3; ++int15) {
						floatArray[int6] = floatArray4[int17];
						int17 += int1;
						int6 += int12;
					}
				}

			
			case 135: 
				int17 = 0;
				int6 = int1 << 1;
				int18 = 0;
				int19 = int8 * int11;
				for (int14 = 1; int14 < int7; ++int14) {
					int17 += int6;
					int18 += int11;
					int19 -= int11;
					int20 = int17;
					int21 = int18;
					int22 = int19;
					for (int15 = 0; int15 < int3; ++int15) {
						floatArray[int20 - 1] = floatArray4[int21];
						floatArray[int20] = floatArray4[int22];
						int20 += int12;
						int21 += int1;
						int22 += int1;
					}
				}

				if (int1 == 1) {
					return;
				}

				if (int10 >= int3) {
					int17 = -int1;
					int18 = 0;
					int19 = 0;
					int20 = int8 * int11;
					for (int14 = 1; int14 < int7; ++int14) {
						int17 += int6;
						int18 += int6;
						int19 += int11;
						int20 -= int11;
						int21 = int17;
						int22 = int18;
						int23 = int19;
						int24 = int20;
						for (int15 = 0; int15 < int3; ++int15) {
							for (int13 = 2; int13 < int1; int13 += 2) {
								int int29 = int9 - int13;
								floatArray[int13 + int22 - 1] = floatArray4[int13 + int23 - 1] + floatArray4[int13 + int24 - 1];
								floatArray[int29 + int21 - 1] = floatArray4[int13 + int23 - 1] - floatArray4[int13 + int24 - 1];
								floatArray[int13 + int22] = floatArray4[int13 + int23] + floatArray4[int13 + int24];
								floatArray[int29 + int21] = floatArray4[int13 + int24] - floatArray4[int13 + int23];
							}

							int21 += int12;
							int22 += int12;
							int23 += int1;
							int24 += int1;
						}
					}

					return;
				}

				short1 = 141;
				break;
			
			case 141: 
				int17 = -int1;
				int18 = 0;
				int19 = 0;
				int20 = int8 * int11;
				for (int14 = 1; int14 < int7; ++int14) {
					int17 += int6;
					int18 += int6;
					int19 += int11;
					int20 -= int11;
					for (int13 = 2; int13 < int1; int13 += 2) {
						int21 = int9 + int17 - int13;
						int22 = int13 + int18;
						int23 = int13 + int19;
						int24 = int13 + int20;
						for (int15 = 0; int15 < int3; ++int15) {
							floatArray[int22 - 1] = floatArray4[int23 - 1] + floatArray4[int24 - 1];
							floatArray[int21 - 1] = floatArray4[int23 - 1] - floatArray4[int24 - 1];
							floatArray[int22] = floatArray4[int23] + floatArray4[int24];
							floatArray[int21] = floatArray4[int24] - floatArray4[int23];
							int21 += int12;
							int22 += int12;
							int23 += int1;
							int24 += int1;
						}
					}
				}

				return;
			
			}
		}
	}

	static void drftb1(int int1, float[] floatArray, float[] floatArray2, float[] floatArray3, int int2, int[] intArray) {
		int int3 = 0;
		int int4 = 0;
		int int5 = 0;
		int int6 = 0;
		int int7 = intArray[1];
		int int8 = 0;
		int int9 = 1;
		int int10 = 1;
		for (int int11 = 0; int11 < int7; ++int11) {
			byte byte1 = 100;
			label71: while (true) {
				int int12;
				switch (byte1) {
				case 100: 
					int4 = intArray[int11 + 2];
					int3 = int4 * int9;
					int5 = int1 / int3;
					int6 = int5 * int9;
					if (int4 != 4) {
						byte1 = 103;
					} else {
						int12 = int10 + int5;
						int int13 = int12 + int5;
						if (int8 != 0) {
							dradb4(int5, int9, floatArray2, floatArray, floatArray3, int2 + int10 - 1, floatArray3, int2 + int12 - 1, floatArray3, int2 + int13 - 1);
						} else {
							dradb4(int5, int9, floatArray, floatArray2, floatArray3, int2 + int10 - 1, floatArray3, int2 + int12 - 1, floatArray3, int2 + int13 - 1);
						}

						int8 = 1 - int8;
						byte1 = 115;
					}

					break;
				
				case 103: 
					if (int4 != 2) {
						byte1 = 106;
					} else {
						if (int8 != 0) {
							dradb2(int5, int9, floatArray2, floatArray, floatArray3, int2 + int10 - 1);
						} else {
							dradb2(int5, int9, floatArray, floatArray2, floatArray3, int2 + int10 - 1);
						}

						int8 = 1 - int8;
						byte1 = 115;
					}

					break;
				
				case 106: 
					if (int4 != 3) {
						byte1 = 109;
					} else {
						int12 = int10 + int5;
						if (int8 != 0) {
							dradb3(int5, int9, floatArray2, floatArray, floatArray3, int2 + int10 - 1, floatArray3, int2 + int12 - 1);
						} else {
							dradb3(int5, int9, floatArray, floatArray2, floatArray3, int2 + int10 - 1, floatArray3, int2 + int12 - 1);
						}

						int8 = 1 - int8;
						byte1 = 115;
					}

					break;
				
				case 109: 
					if (int8 != 0) {
						dradbg(int5, int4, int9, int6, floatArray2, floatArray2, floatArray2, floatArray, floatArray, floatArray3, int2 + int10 - 1);
					} else {
						dradbg(int5, int4, int9, int6, floatArray, floatArray, floatArray, floatArray2, floatArray2, floatArray3, int2 + int10 - 1);
					}

					if (int5 == 1) {
						int8 = 1 - int8;
					}

				
				case 115: 
					break label71;
				
				}
			}

			int9 = int3;
			int10 += (int4 - 1) * int5;
		}

		if (int8 != 0) {
			for (int int14 = 0; int14 < int1; ++int14) {
				floatArray[int14] = floatArray2[int14];
			}
		}
	}

	static void drftf1(int int1, float[] floatArray, float[] floatArray2, float[] floatArray3, int[] intArray) {
		int int2 = intArray[1];
		int int3 = 1;
		int int4 = int1;
		int int5 = int1;
		for (int int6 = 0; int6 < int2; ++int6) {
			int int7 = int2 - int6;
			int int8 = intArray[int7 + 1];
			int int9 = int4 / int8;
			int int10 = int1 / int4;
			int int11 = int10 * int9;
			int5 -= (int8 - 1) * int10;
			int3 = 1 - int3;
			byte byte1 = 100;
			label62: while (true) {
				switch (byte1) {
				case 100: 
					if (int8 != 4) {
						byte1 = 102;
					} else {
						int int12 = int5 + int10;
						int int13 = int12 + int10;
						if (int3 != 0) {
							dradf4(int10, int9, floatArray2, floatArray, floatArray3, int5 - 1, floatArray3, int12 - 1, floatArray3, int13 - 1);
						} else {
							dradf4(int10, int9, floatArray, floatArray2, floatArray3, int5 - 1, floatArray3, int12 - 1, floatArray3, int13 - 1);
						}

						byte1 = 110;
					}

				
				case 101: 
				
				case 105: 
				
				case 106: 
				
				case 107: 
				
				case 108: 
				
				default: 
					break;
				
				case 102: 
					if (int8 != 2) {
						byte1 = 104;
					} else if (int3 != 0) {
						byte1 = 103;
					} else {
						dradf2(int10, int9, floatArray, floatArray2, floatArray3, int5 - 1);
						byte1 = 110;
					}

					break;
				
				case 103: 
					dradf2(int10, int9, floatArray2, floatArray, floatArray3, int5 - 1);
				
				case 104: 
					if (int10 == 1) {
						int3 = 1 - int3;
					}

					if (int3 != 0) {
						byte1 = 109;
					} else {
						dradfg(int10, int8, int9, int11, floatArray, floatArray, floatArray, floatArray2, floatArray2, floatArray3, int5 - 1);
						int3 = 1;
						byte1 = 110;
					}

					break;
				
				case 109: 
					dradfg(int10, int8, int9, int11, floatArray2, floatArray2, floatArray2, floatArray, floatArray, floatArray3, int5 - 1);
					int3 = 0;
				
				case 110: 
					break label62;
				
				}
			}

			int4 = int9;
		}

		if (int3 != 1) {
			for (int int14 = 0; int14 < int1; ++int14) {
				floatArray[int14] = floatArray2[int14];
			}
		}
	}

	static void drfti1(int int1, float[] floatArray, int int2, int[] intArray) {
		int int3 = 0;
		int int4 = -1;
		int int5 = int1;
		int int6 = 0;
		byte byte1 = 101;
		while (true) {
			while (true) {
				int int7;
				switch (byte1) {
				case 101: 
					++int4;
					if (int4 < 4) {
						int3 = ntryh[int4];
					} else {
						int3 += 2;
					}

				
				case 104: 
					int int8 = int5 / int3;
					int int9 = int5 - int3 * int8;
					if (int9 != 0) {
						byte1 = 101;
						break;
					} else {
						++int6;
						intArray[int6 + 1] = int3;
						int5 = int8;
						if (int3 != 2) {
							byte1 = 107;
							break;
						} else if (int6 == 1) {
							byte1 = 107;
							break;
						} else {
							for (int7 = 1; int7 < int6; ++int7) {
								int int10 = int6 - int7 + 1;
								intArray[int10 + 1] = intArray[int10];
							}

							intArray[2] = 2;
						}
					}

				
				case 107: 
					if (int5 == 1) {
						intArray[0] = int1;
						intArray[1] = int6;
						float float1 = tpi / (float)int1;
						int int11 = 0;
						int int12 = int6 - 1;
						int int13 = 1;
						if (int12 == 0) {
							return;
						}

						for (int int14 = 0; int14 < int12; ++int14) {
							int int15 = intArray[int14 + 2];
							int int16 = 0;
							int int17 = int13 * int15;
							int int18 = int1 / int17;
							int int19 = int15 - 1;
							for (int4 = 0; int4 < int19; ++int4) {
								int16 += int13;
								int7 = int11;
								float float2 = (float)int16 * float1;
								float float3 = 0.0F;
								for (int int20 = 2; int20 < int18; int20 += 2) {
									++float3;
									float float4 = float3 * float2;
									floatArray[int2 + int7++] = (float)Math.cos((double)float4);
									floatArray[int2 + int7++] = (float)Math.sin((double)float4);
								}

								int11 += int18;
							}

							int13 = int17;
						}

						return;
					}

					byte1 = 104;
				
				}
			}
		}
	}

	static void fdrffti(int int1, float[] floatArray, int[] intArray) {
		if (int1 != 1) {
			drfti1(int1, floatArray, int1, intArray);
		}
	}

	void backward(float[] floatArray) {
		if (this.n != 1) {
			drftb1(this.n, floatArray, this.trigcache, this.trigcache, this.n, this.splitcache);
		}
	}

	void clear() {
		if (this.trigcache != null) {
			this.trigcache = null;
		}

		if (this.splitcache != null) {
			this.splitcache = null;
		}
	}

	void init(int int1) {
		this.n = int1;
		this.trigcache = new float[3 * int1];
		this.splitcache = new int[32];
		fdrffti(int1, this.trigcache, this.splitcache);
	}
}
