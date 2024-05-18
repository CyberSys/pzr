package org.luaj.kahluafork.compiler;

import java.util.Hashtable;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.Prototype;
import zombie.iso.IsoGridSquare;


public class FuncState {
	private static final Object NULL_OBJECT = new Object();
	public String[] locvars;
	public String[] upvalues;
	public int linedefined;
	public int lastlinedefined;
	public int isVararg;
	Prototype f;
	Hashtable htable;
	FuncState prev;
	LexState ls;
	BlockCnt bl;
	int pc;
	int lasttarget;
	int jpc;
	int freereg;
	int nk;
	int np;
	int nlocvars;
	int nactvar;
	int[] upvalues_k = new int[60];
	int[] upvalues_info = new int[60];
	short[] actvar = new short[200];
	int[] actvarline = new int[200];
	public static String currentFile;
	public static String currentfullFile;
	public static final int MAXSTACK = 250;
	static final int LUAI_MAXUPVALUES = 60;
	static final int LUAI_MAXVARS = 200;
	static final int OpArgN = 0;
	static final int OpArgU = 1;
	static final int OpArgR = 2;
	static final int OpArgK = 3;
	public static final int LUA_MULTRET = -1;
	public static final int VARARG_HASARG = 1;
	public static final int VARARG_ISVARARG = 2;
	public static final int VARARG_NEEDSARG = 4;
	public static final int iABC = 0;
	public static final int iABx = 1;
	public static final int iAsBx = 2;
	public static final int SIZE_C = 9;
	public static final int SIZE_B = 9;
	public static final int SIZE_Bx = 18;
	public static final int SIZE_A = 8;
	public static final int SIZE_OP = 6;
	public static final int POS_OP = 0;
	public static final int POS_A = 6;
	public static final int POS_C = 14;
	public static final int POS_B = 23;
	public static final int POS_Bx = 14;
	public static final int MAX_OP = 63;
	public static final int MAXARG_A = 255;
	public static final int MAXARG_B = 511;
	public static final int MAXARG_C = 511;
	public static final int MAXARG_Bx = 262143;
	public static final int MAXARG_sBx = 131071;
	public static final int MASK_OP = 63;
	public static final int MASK_A = 16320;
	public static final int MASK_B = -8388608;
	public static final int MASK_C = 8372224;
	public static final int MASK_Bx = -16384;
	public static final int MASK_NOT_OP = -64;
	public static final int MASK_NOT_A = -16321;
	public static final int MASK_NOT_B = 8388607;
	public static final int MASK_NOT_C = -8372225;
	public static final int MASK_NOT_Bx = 16383;
	public static final int BITRK = 256;
	public static final int MAXINDEXRK = 255;
	public static final int NO_REG = 255;
	public static final int OP_MOVE = 0;
	public static final int OP_LOADK = 1;
	public static final int OP_LOADBOOL = 2;
	public static final int OP_LOADNIL = 3;
	public static final int OP_GETUPVAL = 4;
	public static final int OP_GETGLOBAL = 5;
	public static final int OP_GETTABLE = 6;
	public static final int OP_SETGLOBAL = 7;
	public static final int OP_SETUPVAL = 8;
	public static final int OP_SETTABLE = 9;
	public static final int OP_NEWTABLE = 10;
	public static final int OP_SELF = 11;
	public static final int OP_ADD = 12;
	public static final int OP_SUB = 13;
	public static final int OP_MUL = 14;
	public static final int OP_DIV = 15;
	public static final int OP_MOD = 16;
	public static final int OP_POW = 17;
	public static final int OP_UNM = 18;
	public static final int OP_NOT = 19;
	public static final int OP_LEN = 20;
	public static final int OP_CONCAT = 21;
	public static final int OP_JMP = 22;
	public static final int OP_EQ = 23;
	public static final int OP_LT = 24;
	public static final int OP_LE = 25;
	public static final int OP_TEST = 26;
	public static final int OP_TESTSET = 27;
	public static final int OP_CALL = 28;
	public static final int OP_TAILCALL = 29;
	public static final int OP_RETURN = 30;
	public static final int OP_FORLOOP = 31;
	public static final int OP_FORPREP = 32;
	public static final int OP_TFORLOOP = 33;
	public static final int OP_SETLIST = 34;
	public static final int OP_CLOSE = 35;
	public static final int OP_CLOSURE = 36;
	public static final int OP_VARARG = 37;
	public static final int NUM_OPCODES = 38;
	public static final int[] luaP_opmodes = new int[]{96, 113, 84, 96, 80, 113, 108, 49, 16, 60, 84, 108, 124, 124, 124, 124, 124, 124, 96, 96, 96, 104, 34, 188, 188, 188, 228, 228, 84, 84, 16, 98, 98, 132, 20, 0, 81, 80};
	public static final int LFIELDS_PER_FLUSH = 50;

	FuncState(LexState lexState) {
		Prototype prototype = new Prototype();
		if (lexState.fs != null) {
			prototype.name = lexState.fs.f.name;
			IsoGridSquare.Checksum *= (long)prototype.name.hashCode();
		}

		this.f = prototype;
		this.prev = lexState.fs;
		this.ls = lexState;
		lexState.fs = this;
		this.pc = 0;
		this.lasttarget = -1;
		this.jpc = -1;
		this.freereg = 0;
		this.nk = 0;
		this.np = 0;
		this.nlocvars = 0;
		this.nactvar = 0;
		this.bl = null;
		prototype.maxStacksize = 2;
		this.htable = new Hashtable();
	}

	FuncState(LexState lexState, String string) {
		Prototype prototype = new Prototype();
		prototype.name = string;
		this.f = prototype;
		this.prev = lexState.fs;
		this.ls = lexState;
		lexState.fs = this;
		this.pc = 0;
		this.lasttarget = -1;
		this.jpc = -1;
		this.freereg = 0;
		this.nk = 0;
		this.np = 0;
		this.nlocvars = 0;
		this.nactvar = 0;
		this.bl = null;
		prototype.maxStacksize = 2;
		this.htable = new Hashtable();
	}

	InstructionPtr getcodePtr(ExpDesc expDesc) {
		return new InstructionPtr(this.f.code, expDesc.info);
	}

	int getcode(ExpDesc expDesc) {
		return this.f.code[expDesc.info];
	}

	int codeAsBx(int int1, int int2, int int3) {
		return this.codeABx(int1, int2, int3 + 131071);
	}

	void setmultret(ExpDesc expDesc) {
		this.setreturns(expDesc, -1);
	}

	String getlocvar(int int1) {
		return this.locvars[this.actvar[int1]];
	}

	void checklimit(int int1, int int2, String string) {
		if (int1 > int2) {
			this.errorlimit(int2, string);
		}
	}

	void errorlimit(int int1, String string) {
		String string2 = this.linedefined == 0 ? "main function has more than " + int1 + " " + string : "function at line " + this.linedefined + " has more than " + int1 + " " + string;
		this.ls.lexerror(string2, 0);
	}

	int indexupvalue(String string, ExpDesc expDesc) {
		for (int int1 = 0; int1 < this.f.numUpvalues; ++int1) {
			if (this.upvalues_k[int1] == expDesc.k && this.upvalues_info[int1] == expDesc.info) {
				_assert(this.upvalues[int1].equals(string));
				return int1;
			}
		}

		this.checklimit(this.f.numUpvalues + 1, 60, "upvalues");
		if (this.upvalues == null || this.f.numUpvalues + 1 > this.upvalues.length) {
			this.upvalues = realloc(this.upvalues, this.f.numUpvalues * 2 + 1);
		}

		_assert(expDesc.k == 6 || expDesc.k == 7);
		int int2 = this.f.numUpvalues++;
		this.upvalues[int2] = string;
		this.upvalues_k[int2] = expDesc.k;
		this.upvalues_info[int2] = expDesc.info;
		return int2;
	}

	int searchvar(String string) {
		for (int int1 = this.nactvar - 1; int1 >= 0; --int1) {
			if (string.equals(this.getlocvar(int1))) {
				return int1;
			}
		}

		return -1;
	}

	void markupval(int int1) {
		BlockCnt blockCnt;
		for (blockCnt = this.bl; blockCnt != null && blockCnt.nactvar > int1; blockCnt = blockCnt.previous) {
		}

		if (blockCnt != null) {
			blockCnt.upval = true;
		}
	}

	int singlevaraux(String string, ExpDesc expDesc, int int1) {
		int int2 = this.searchvar(string);
		if (int2 >= 0) {
			expDesc.init(6, int2);
			if (int1 == 0) {
				this.markupval(int2);
			}

			return 6;
		} else if (this.prev == null) {
			expDesc.init(8, 255);
			return 8;
		} else if (this.prev.singlevaraux(string, expDesc, 0) == 8) {
			return 8;
		} else {
			expDesc.info = this.indexupvalue(string, expDesc);
			expDesc.k = 7;
			return 7;
		}
	}

	void enterblock(BlockCnt blockCnt, boolean boolean1) {
		blockCnt.breaklist = -1;
		blockCnt.isbreakable = boolean1;
		blockCnt.nactvar = this.nactvar;
		blockCnt.upval = false;
		blockCnt.previous = this.bl;
		this.bl = blockCnt;
		_assert(this.freereg == this.nactvar);
	}

	void leaveblock() {
		BlockCnt blockCnt = this.bl;
		this.bl = blockCnt.previous;
		this.ls.removevars(blockCnt.nactvar);
		if (blockCnt.upval) {
			this.codeABC(35, blockCnt.nactvar, 0, 0);
		}

		_assert(!blockCnt.isbreakable || !blockCnt.upval);
		_assert(blockCnt.nactvar == this.nactvar);
		this.freereg = this.nactvar;
		this.patchtohere(blockCnt.breaklist);
	}

	void closelistfield(ConsControl consControl) {
		if (consControl.v.k != 0) {
			this.exp2nextreg(consControl.v);
			consControl.v.k = 0;
			if (consControl.tostore == 50) {
				this.setlist(consControl.t.info, consControl.na, consControl.tostore);
				consControl.tostore = 0;
			}
		}
	}

	boolean hasmultret(int int1) {
		return int1 == 13 || int1 == 14;
	}

	void lastlistfield(ConsControl consControl) {
		if (consControl.tostore != 0) {
			if (this.hasmultret(consControl.v.k)) {
				this.setmultret(consControl.v);
				this.setlist(consControl.t.info, consControl.na, -1);
				--consControl.na;
			} else {
				if (consControl.v.k != 0) {
					this.exp2nextreg(consControl.v);
				}

				this.setlist(consControl.t.info, consControl.na, consControl.tostore);
			}
		}
	}

	void nil(int int1, int int2) {
		if (this.pc > this.lasttarget) {
			if (this.pc == 0) {
				if (int1 >= this.nactvar) {
					return;
				}
			} else {
				InstructionPtr instructionPtr = new InstructionPtr(this.f.code, this.pc - 1);
				if (GET_OPCODE(instructionPtr.get()) == 3) {
					int int3 = GETARG_A(instructionPtr.get());
					int int4 = GETARG_B(instructionPtr.get());
					if (int3 <= int1 && int1 <= int4 + 1) {
						if (int1 + int2 - 1 > int4) {
							SETARG_B(instructionPtr, int1 + int2 - 1);
						}

						return;
					}
				}
			}
		}

		this.codeABC(3, int1, int1 + int2 - 1, 0);
	}

	int jump() {
		int int1 = this.jpc;
		this.jpc = -1;
		int int2 = this.codeAsBx(22, 0, -1);
		int2 = this.concat(int2, int1);
		IsoGridSquare.Checksum += (long)int2;
		return int2;
	}

	void ret(int int1, int int2) {
		this.codeABC(30, int1, int2 + 1, 0);
	}

	int condjump(int int1, int int2, int int3, int int4) {
		this.codeABC(int1, int2, int3, int4);
		IsoGridSquare.Checksum += (long)int1;
		IsoGridSquare.Checksum -= (long)int2;
		IsoGridSquare.Checksum += (long)int4;
		return this.jump();
	}

	void fixjump(int int1, int int2) {
		IsoGridSquare.Checksum += (long)int1;
		IsoGridSquare.Checksum += (long)int2;
		InstructionPtr instructionPtr = new InstructionPtr(this.f.code, int1);
		int int3 = int2 - (int1 + 1);
		_assert(int2 != -1);
		if (Math.abs(int3) > 131071) {
			this.ls.syntaxerror("control structure too long");
		}

		SETARG_sBx(instructionPtr, int3);
	}

	int getlabel() {
		this.lasttarget = this.pc;
		return this.pc;
	}

	int getjump(int int1) {
		int int2 = GETARG_sBx(this.f.code[int1]);
		return int2 == -1 ? -1 : int1 + 1 + int2;
	}

	InstructionPtr getjumpcontrol(int int1) {
		InstructionPtr instructionPtr = new InstructionPtr(this.f.code, int1);
		return int1 >= 1 && testTMode(GET_OPCODE(instructionPtr.code[instructionPtr.idx - 1])) ? new InstructionPtr(instructionPtr.code, instructionPtr.idx - 1) : instructionPtr;
	}

	boolean need_value(int int1) {
		while (int1 != -1) {
			int int2 = this.getjumpcontrol(int1).get();
			if (GET_OPCODE(int2) != 27) {
				return true;
			}

			int1 = this.getjump(int1);
		}

		return false;
	}

	boolean patchtestreg(int int1, int int2) {
		InstructionPtr instructionPtr = this.getjumpcontrol(int1);
		if (GET_OPCODE(instructionPtr.get()) != 27) {
			return false;
		} else {
			if (int2 != 255 && int2 != GETARG_B(instructionPtr.get())) {
				SETARG_A(instructionPtr, int2);
			} else {
				instructionPtr.set(CREATE_ABC(26, GETARG_B(instructionPtr.get()), 0, GETARG_C(instructionPtr.get())));
			}

			return true;
		}
	}

	void removevalues(int int1) {
		while (int1 != -1) {
			this.patchtestreg(int1, 255);
			int1 = this.getjump(int1);
		}
	}

	void patchlistaux(int int1, int int2, int int3, int int4) {
		int int5;
		for (; int1 != -1; int1 = int5) {
			int5 = this.getjump(int1);
			if (this.patchtestreg(int1, int3)) {
				this.fixjump(int1, int2);
			} else {
				this.fixjump(int1, int4);
			}
		}
	}

	void dischargejpc() {
		this.patchlistaux(this.jpc, this.pc, 255, this.pc);
		this.jpc = -1;
	}

	void patchlist(int int1, int int2) {
		if (int2 == this.pc) {
			this.patchtohere(int1);
		} else {
			_assert(int2 < this.pc);
			this.patchlistaux(int1, int2, 255, int2);
		}
	}

	void patchtohere(int int1) {
		this.getlabel();
		this.jpc = this.concat(this.jpc, int1);
	}

	int concat(int int1, int int2) {
		if (int2 == -1) {
			return int1;
		} else {
			if (int1 == -1) {
				int1 = int2;
			} else {
				int int3;
				int int4;
				for (int3 = int1; (int4 = this.getjump(int3)) != -1; int3 = int4) {
				}

				this.fixjump(int3, int2);
			}

			return int1;
		}
	}

	void checkstack(int int1) {
		int int2 = this.freereg + int1;
		if (int2 > this.f.maxStacksize) {
			if (int2 >= 250) {
				this.ls.syntaxerror("function or expression too complex");
			}

			this.f.maxStacksize = int2;
		}
	}

	void reserveregs(int int1) {
		this.checkstack(int1);
		this.freereg += int1;
	}

	void freereg(int int1) {
		if (!ISK(int1) && int1 >= this.nactvar) {
			--this.freereg;
			_assert(int1 == this.freereg);
		}
	}

	void freeexp(ExpDesc expDesc) {
		if (expDesc.k == 12) {
			this.freereg(expDesc.info);
		}
	}

	int addk(Object object) {
		int int1;
		if (this.htable.containsKey(object)) {
			int1 = (Integer)this.htable.get(object);
		} else {
			int1 = this.nk;
			this.htable.put(object, new Integer(int1));
			Prototype prototype = this.f;
			if (prototype.constants == null || this.nk + 1 >= prototype.constants.length) {
				prototype.constants = realloc(prototype.constants, this.nk * 2 + 1);
			}

			if (object == NULL_OBJECT) {
				object = null;
			}

			prototype.constants[this.nk++] = object;
		}

		return int1;
	}

	int stringK(String string) {
		return this.addk(string);
	}

	int numberK(double double1) {
		return this.addk(new Double(double1));
	}

	int boolK(boolean boolean1) {
		return this.addk(boolean1 ? Boolean.TRUE : Boolean.FALSE);
	}

	int nilK() {
		return this.addk(NULL_OBJECT);
	}

	void setreturns(ExpDesc expDesc, int int1) {
		if (expDesc.k == 13) {
			SETARG_C(this.getcodePtr(expDesc), int1 + 1);
		} else if (expDesc.k == 14) {
			SETARG_B(this.getcodePtr(expDesc), int1 + 1);
			SETARG_A(this.getcodePtr(expDesc), this.freereg);
			this.reserveregs(1);
		}
	}

	void setoneret(ExpDesc expDesc) {
		if (expDesc.k == 13) {
			expDesc.k = 12;
			expDesc.info = GETARG_A(this.getcode(expDesc));
		} else if (expDesc.k == 14) {
			SETARG_B(this.getcodePtr(expDesc), 2);
			expDesc.k = 11;
		}
	}

	void dischargevars(ExpDesc expDesc) {
		switch (expDesc.k) {
		case 6: 
			expDesc.k = 12;
			break;
		
		case 7: 
			expDesc.info = this.codeABC(4, 0, expDesc.info, 0);
			expDesc.k = 11;
			break;
		
		case 8: 
			expDesc.info = this.codeABx(5, 0, expDesc.info);
			expDesc.k = 11;
			break;
		
		case 9: 
			this.freereg(expDesc.aux);
			this.freereg(expDesc.info);
			expDesc.info = this.codeABC(6, 0, expDesc.info, expDesc.aux);
			expDesc.k = 11;
		
		case 10: 
		
		case 11: 
		
		case 12: 
		
		default: 
			break;
		
		case 13: 
		
		case 14: 
			this.setoneret(expDesc);
		
		}
	}

	int code_label(int int1, int int2, int int3) {
		this.getlabel();
		return this.codeABC(2, int1, int2, int3);
	}

	void discharge2reg(ExpDesc expDesc, int int1) {
		this.dischargevars(expDesc);
		switch (expDesc.k) {
		case 1: 
			this.nil(int1, 1);
			break;
		
		case 2: 
		
		case 3: 
			this.codeABC(2, int1, expDesc.k == 2 ? 1 : 0, 0);
			break;
		
		case 4: 
			this.codeABx(1, int1, expDesc.info);
			break;
		
		case 5: 
			this.codeABx(1, int1, this.numberK(expDesc.nval()));
			break;
		
		case 6: 
		
		case 7: 
		
		case 8: 
		
		case 9: 
		
		case 10: 
		
		default: 
			_assert(expDesc.k == 0 || expDesc.k == 10);
			return;
		
		case 11: 
			InstructionPtr instructionPtr = this.getcodePtr(expDesc);
			SETARG_A(instructionPtr, int1);
			break;
		
		case 12: 
			if (int1 != expDesc.info) {
				this.codeABC(0, int1, expDesc.info, 0);
			}

		
		}
		expDesc.info = int1;
		expDesc.k = 12;
	}

	void discharge2anyreg(ExpDesc expDesc) {
		if (expDesc.k != 12) {
			this.reserveregs(1);
			this.discharge2reg(expDesc, this.freereg - 1);
		}
	}

	void exp2reg(ExpDesc expDesc, int int1) {
		this.discharge2reg(expDesc, int1);
		if (expDesc.k == 10) {
			expDesc.t = this.concat(expDesc.t, expDesc.info);
		}

		if (expDesc.hasjumps()) {
			int int2 = -1;
			int int3 = -1;
			if (this.need_value(expDesc.t) || this.need_value(expDesc.f)) {
				int int4 = expDesc.k == 10 ? -1 : this.jump();
				int2 = this.code_label(int1, 0, 1);
				int3 = this.code_label(int1, 1, 0);
				this.patchtohere(int4);
			}

			int int5 = this.getlabel();
			this.patchlistaux(expDesc.f, int5, int1, int2);
			this.patchlistaux(expDesc.t, int5, int1, int3);
		}

		expDesc.f = expDesc.t = -1;
		expDesc.info = int1;
		expDesc.k = 12;
	}

	void exp2nextreg(ExpDesc expDesc) {
		this.dischargevars(expDesc);
		this.freeexp(expDesc);
		this.reserveregs(1);
		this.exp2reg(expDesc, this.freereg - 1);
	}

	int exp2anyreg(ExpDesc expDesc) {
		this.dischargevars(expDesc);
		if (expDesc.k == 12) {
			if (!expDesc.hasjumps()) {
				return expDesc.info;
			}

			if (expDesc.info >= this.nactvar) {
				this.exp2reg(expDesc, expDesc.info);
				return expDesc.info;
			}
		}

		this.exp2nextreg(expDesc);
		return expDesc.info;
	}

	void exp2val(ExpDesc expDesc) {
		if (expDesc.hasjumps()) {
			this.exp2anyreg(expDesc);
		} else {
			this.dischargevars(expDesc);
		}
	}

	int exp2RK(ExpDesc expDesc) {
		this.exp2val(expDesc);
		switch (expDesc.k) {
		case 1: 
		
		case 2: 
		
		case 3: 
		
		case 5: 
			if (this.nk <= 255) {
				expDesc.info = expDesc.k == 1 ? this.nilK() : (expDesc.k == 5 ? this.numberK(expDesc.nval()) : this.boolK(expDesc.k == 2));
				expDesc.k = 4;
				return RKASK(expDesc.info);
			}

			break;
		
		case 4: 
			if (expDesc.info <= 255) {
				return RKASK(expDesc.info);
			}

		
		}
		return this.exp2anyreg(expDesc);
	}

	void storevar(ExpDesc expDesc, ExpDesc expDesc2) {
		int int1;
		switch (expDesc.k) {
		case 6: 
			this.freeexp(expDesc2);
			this.exp2reg(expDesc2, expDesc.info);
			return;
		
		case 7: 
			int1 = this.exp2anyreg(expDesc2);
			this.codeABC(8, int1, expDesc.info, 0);
			break;
		
		case 8: 
			int1 = this.exp2anyreg(expDesc2);
			this.codeABx(7, int1, expDesc.info);
			break;
		
		case 9: 
			int1 = this.exp2RK(expDesc2);
			this.codeABC(9, expDesc.info, expDesc.aux, int1);
			break;
		
		default: 
			_assert(false);
		
		}
		this.freeexp(expDesc2);
	}

	void self(ExpDesc expDesc, ExpDesc expDesc2) {
		this.exp2anyreg(expDesc);
		this.freeexp(expDesc);
		int int1 = this.freereg;
		this.reserveregs(2);
		this.codeABC(11, int1, expDesc.info, this.exp2RK(expDesc2));
		this.freeexp(expDesc2);
		expDesc.info = int1;
		expDesc.k = 12;
	}

	void invertjump(ExpDesc expDesc) {
		InstructionPtr instructionPtr = this.getjumpcontrol(expDesc.info);
		_assert(testTMode(GET_OPCODE(instructionPtr.get())) && GET_OPCODE(instructionPtr.get()) != 27 && GET_OPCODE(instructionPtr.get()) != 26);
		int int1 = GETARG_A(instructionPtr.get());
		int int2 = int1 != 0 ? 0 : 1;
		SETARG_A(instructionPtr, int2);
	}

	int jumponcond(ExpDesc expDesc, int int1) {
		if (expDesc.k == 11) {
			int int2 = this.getcode(expDesc);
			if (GET_OPCODE(int2) == 19) {
				--this.pc;
				return this.condjump(26, GETARG_B(int2), 0, int1 != 0 ? 0 : 1);
			}
		}

		this.discharge2anyreg(expDesc);
		this.freeexp(expDesc);
		return this.condjump(27, 255, expDesc.info, int1);
	}

	void goiftrue(ExpDesc expDesc) {
		this.dischargevars(expDesc);
		int int1;
		switch (expDesc.k) {
		case 2: 
		
		case 4: 
		
		case 5: 
			int1 = -1;
			break;
		
		case 3: 
			int1 = this.jump();
			break;
		
		case 6: 
		
		case 7: 
		
		case 8: 
		
		case 9: 
		
		default: 
			int1 = this.jumponcond(expDesc, 0);
			break;
		
		case 10: 
			this.invertjump(expDesc);
			int1 = expDesc.info;
		
		}
		expDesc.f = this.concat(expDesc.f, int1);
		this.patchtohere(expDesc.t);
		expDesc.t = -1;
	}

	void goiffalse(ExpDesc expDesc) {
		this.dischargevars(expDesc);
		int int1;
		switch (expDesc.k) {
		case 1: 
		
		case 3: 
			int1 = -1;
			break;
		
		case 2: 
			int1 = this.jump();
			break;
		
		case 4: 
		
		case 5: 
		
		case 6: 
		
		case 7: 
		
		case 8: 
		
		case 9: 
		
		default: 
			int1 = this.jumponcond(expDesc, 1);
			break;
		
		case 10: 
			int1 = expDesc.info;
		
		}
		expDesc.t = this.concat(expDesc.t, int1);
		this.patchtohere(expDesc.f);
		expDesc.f = -1;
	}

	void codenot(ExpDesc expDesc) {
		this.dischargevars(expDesc);
		switch (expDesc.k) {
		case 1: 
		
		case 3: 
			expDesc.k = 2;
			break;
		
		case 2: 
		
		case 4: 
		
		case 5: 
			expDesc.k = 3;
			break;
		
		case 6: 
		
		case 7: 
		
		case 8: 
		
		case 9: 
		
		default: 
			_assert(false);
			break;
		
		case 10: 
			this.invertjump(expDesc);
			break;
		
		case 11: 
		
		case 12: 
			this.discharge2anyreg(expDesc);
			this.freeexp(expDesc);
			expDesc.info = this.codeABC(19, 0, expDesc.info, 0);
			expDesc.k = 11;
		
		}
		int int1 = expDesc.f;
		expDesc.f = expDesc.t;
		expDesc.t = int1;
		this.removevalues(expDesc.f);
		this.removevalues(expDesc.t);
	}

	void indexed(ExpDesc expDesc, ExpDesc expDesc2) {
		expDesc.aux = this.exp2RK(expDesc2);
		expDesc.k = 9;
	}

	boolean constfolding(int int1, ExpDesc expDesc, ExpDesc expDesc2) {
		if (expDesc.isnumeral() && expDesc2.isnumeral()) {
			double double1 = expDesc.nval();
			double double2 = expDesc2.nval();
			double double3;
			switch (int1) {
			case 12: 
				double3 = double1 + double2;
				break;
			
			case 13: 
				double3 = double1 - double2;
				break;
			
			case 14: 
				double3 = double1 * double2;
				break;
			
			case 15: 
				double3 = double1 / double2;
				break;
			
			case 16: 
				double3 = double1 % double2;
				break;
			
			case 17: 
				return false;
			
			case 18: 
				double3 = -double1;
				break;
			
			case 19: 
			
			default: 
				_assert(false);
				return false;
			
			case 20: 
				return false;
			
			}

			if (!Double.isNaN(double3) && !Double.isInfinite(double3)) {
				expDesc.setNval(double3);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	void codearith(int int1, ExpDesc expDesc, ExpDesc expDesc2) {
		if (!this.constfolding(int1, expDesc, expDesc2)) {
			int int2 = int1 != 18 && int1 != 20 ? this.exp2RK(expDesc2) : 0;
			int int3 = this.exp2RK(expDesc);
			if (int3 > int2) {
				this.freeexp(expDesc);
				this.freeexp(expDesc2);
			} else {
				this.freeexp(expDesc2);
				this.freeexp(expDesc);
			}

			expDesc.info = this.codeABC(int1, 0, int3, int2);
			expDesc.k = 11;
		}
	}

	void codecomp(int int1, int int2, ExpDesc expDesc, ExpDesc expDesc2) {
		int int3 = this.exp2RK(expDesc);
		int int4 = this.exp2RK(expDesc2);
		this.freeexp(expDesc2);
		this.freeexp(expDesc);
		if (int2 == 0 && int1 != 23) {
			int int5 = int3;
			int3 = int4;
			int4 = int5;
			int2 = 1;
		}

		expDesc.info = this.condjump(int1, int2, int3, int4);
		expDesc.k = 10;
	}

	void prefix(int int1, ExpDesc expDesc) {
		ExpDesc expDesc2 = new ExpDesc();
		expDesc2.init(5, 0);
		switch (int1) {
		case 0: 
			if (expDesc.k == 4) {
				this.exp2anyreg(expDesc);
			}

			this.codearith(18, expDesc, expDesc2);
			break;
		
		case 1: 
			this.codenot(expDesc);
			break;
		
		case 2: 
			this.exp2anyreg(expDesc);
			this.codearith(20, expDesc, expDesc2);
			break;
		
		default: 
			_assert(false);
		
		}
	}

	void infix(int int1, ExpDesc expDesc) {
		switch (int1) {
		case 0: 
		
		case 1: 
		
		case 2: 
		
		case 3: 
		
		case 4: 
		
		case 5: 
			if (!expDesc.isnumeral()) {
				this.exp2RK(expDesc);
			}

			break;
		
		case 6: 
			this.exp2nextreg(expDesc);
			break;
		
		case 7: 
		
		case 8: 
		
		case 9: 
		
		case 10: 
		
		case 11: 
		
		case 12: 
		
		default: 
			this.exp2RK(expDesc);
			break;
		
		case 13: 
			this.goiftrue(expDesc);
			break;
		
		case 14: 
			this.goiffalse(expDesc);
		
		}
	}

	void posfix(int int1, ExpDesc expDesc, ExpDesc expDesc2) {
		switch (int1) {
		case 0: 
			this.codearith(12, expDesc, expDesc2);
			break;
		
		case 1: 
			this.codearith(13, expDesc, expDesc2);
			break;
		
		case 2: 
			this.codearith(14, expDesc, expDesc2);
			break;
		
		case 3: 
			this.codearith(15, expDesc, expDesc2);
			break;
		
		case 4: 
			this.codearith(16, expDesc, expDesc2);
			break;
		
		case 5: 
			this.codearith(17, expDesc, expDesc2);
			break;
		
		case 6: 
			this.exp2val(expDesc2);
			if (expDesc2.k == 11 && GET_OPCODE(this.getcode(expDesc2)) == 21) {
				_assert(expDesc.info == GETARG_B(this.getcode(expDesc2)) - 1);
				this.freeexp(expDesc);
				SETARG_B(this.getcodePtr(expDesc2), expDesc.info);
				expDesc.k = 11;
				expDesc.info = expDesc2.info;
			} else {
				this.exp2nextreg(expDesc2);
				this.codearith(21, expDesc, expDesc2);
			}

			break;
		
		case 7: 
			this.codecomp(23, 0, expDesc, expDesc2);
			break;
		
		case 8: 
			this.codecomp(23, 1, expDesc, expDesc2);
			break;
		
		case 9: 
			this.codecomp(24, 1, expDesc, expDesc2);
			break;
		
		case 10: 
			this.codecomp(25, 1, expDesc, expDesc2);
			break;
		
		case 11: 
			this.codecomp(24, 0, expDesc, expDesc2);
			break;
		
		case 12: 
			this.codecomp(25, 0, expDesc, expDesc2);
			break;
		
		case 13: 
			_assert(expDesc.t == -1);
			this.dischargevars(expDesc2);
			expDesc2.f = this.concat(expDesc2.f, expDesc.f);
			expDesc.setvalue(expDesc2);
			break;
		
		case 14: 
			_assert(expDesc.f == -1);
			this.dischargevars(expDesc2);
			expDesc2.t = this.concat(expDesc2.t, expDesc.t);
			expDesc.setvalue(expDesc2);
			break;
		
		default: 
			_assert(false);
		
		}
	}

	void fixline(int int1) {
		this.f.lines[this.pc - 1] = int1;
	}

	int code(int int1, int int2) {
		Prototype prototype = this.f;
		this.dischargejpc();
		if (prototype.code == null || this.pc + 1 > prototype.code.length) {
			prototype.code = realloc(prototype.code, this.pc * 2 + 1);
		}

		prototype.code[this.pc] = int1;
		if (prototype.lines == null || this.pc + 1 > prototype.lines.length) {
			prototype.lines = realloc(prototype.lines, this.pc * 2 + 1);
		}

		prototype.lines[this.pc] = int2;
		prototype.file = currentFile;
		prototype.filename = currentfullFile;
		return this.pc++;
	}

	int codeABC(int int1, int int2, int int3, int int4) {
		_assert(getOpMode(int1) == 0);
		_assert(getBMode(int1) != 0 || int3 == 0);
		_assert(getCMode(int1) != 0 || int4 == 0);
		return this.code(CREATE_ABC(int1, int2, int3, int4), this.ls.lastline);
	}

	int codeABx(int int1, int int2, int int3) {
		_assert(getOpMode(int1) == 1 || getOpMode(int1) == 2);
		_assert(getCMode(int1) == 0);
		return this.code(CREATE_ABx(int1, int2, int3), this.ls.lastline);
	}

	void setlist(int int1, int int2, int int3) {
		int int4 = (int2 - 1) / 50 + 1;
		int int5 = int3 == -1 ? 0 : int3;
		_assert(int3 != 0);
		if (int4 <= 511) {
			this.codeABC(34, int1, int5, int4);
		} else {
			this.codeABC(34, int1, int5, 0);
			this.code(int4, this.ls.lastline);
		}

		this.freereg = int1 + 1;
	}

	protected static void _assert(boolean boolean1) {
		if (!boolean1) {
			throw new KahluaException("compiler assert failed");
		}
	}

	static void SET_OPCODE(InstructionPtr instructionPtr, int int1) {
		instructionPtr.set(instructionPtr.get() & -64 | int1 << 0 & 63);
	}

	static void SETARG_A(InstructionPtr instructionPtr, int int1) {
		instructionPtr.set(instructionPtr.get() & -16321 | int1 << 6 & 16320);
	}

	static void SETARG_B(InstructionPtr instructionPtr, int int1) {
		instructionPtr.set(instructionPtr.get() & 8388607 | int1 << 23 & -8388608);
	}

	static void SETARG_C(InstructionPtr instructionPtr, int int1) {
		instructionPtr.set(instructionPtr.get() & -8372225 | int1 << 14 & 8372224);
	}

	static void SETARG_Bx(InstructionPtr instructionPtr, int int1) {
		instructionPtr.set(instructionPtr.get() & 16383 | int1 << 14 & -16384);
	}

	static void SETARG_sBx(InstructionPtr instructionPtr, int int1) {
		SETARG_Bx(instructionPtr, int1 + 131071);
	}

	static int CREATE_ABC(int int1, int int2, int int3, int int4) {
		return int1 << 0 & 63 | int2 << 6 & 16320 | int3 << 23 & -8388608 | int4 << 14 & 8372224;
	}

	static int CREATE_ABx(int int1, int int2, int int3) {
		return int1 << 0 & 63 | int2 << 6 & 16320 | int3 << 14 & -16384;
	}

	static Object[] realloc(Object[] objectArray, int int1) {
		Object[] objectArray2 = new Object[int1];
		if (objectArray != null) {
			System.arraycopy(objectArray, 0, objectArray2, 0, Math.min(objectArray.length, int1));
		}

		return objectArray2;
	}

	static String[] realloc(String[] stringArray, int int1) {
		String[] stringArray2 = new String[int1];
		if (stringArray != null) {
			System.arraycopy(stringArray, 0, stringArray2, 0, Math.min(stringArray.length, int1));
		}

		return stringArray2;
	}

	static Prototype[] realloc(Prototype[] prototypeArray, int int1) {
		Prototype[] prototypeArray2 = new Prototype[int1];
		if (prototypeArray != null) {
			System.arraycopy(prototypeArray, 0, prototypeArray2, 0, Math.min(prototypeArray.length, int1));
		}

		return prototypeArray2;
	}

	static int[] realloc(int[] intArray, int int1) {
		int[] intArray2 = new int[int1];
		if (intArray != null) {
			System.arraycopy(intArray, 0, intArray2, 0, Math.min(intArray.length, int1));
		}

		return intArray2;
	}

	static byte[] realloc(byte[] byteArray, int int1) {
		byte[] byteArray2 = new byte[int1];
		if (byteArray != null) {
			System.arraycopy(byteArray, 0, byteArray2, 0, Math.min(byteArray.length, int1));
		}

		return byteArray2;
	}

	public static int GET_OPCODE(int int1) {
		return int1 >> 0 & 63;
	}

	public static int GETARG_A(int int1) {
		return int1 >> 6 & 255;
	}

	public static int GETARG_B(int int1) {
		return int1 >> 23 & 511;
	}

	public static int GETARG_C(int int1) {
		return int1 >> 14 & 511;
	}

	public static int GETARG_Bx(int int1) {
		return int1 >> 14 & 262143;
	}

	public static int GETARG_sBx(int int1) {
		return (int1 >> 14 & 262143) - 131071;
	}

	public static boolean ISK(int int1) {
		return 0 != (int1 & 256);
	}

	public static int INDEXK(int int1) {
		return int1 & -257;
	}

	public static int RKASK(int int1) {
		return int1 | 256;
	}

	public static int getOpMode(int int1) {
		return luaP_opmodes[int1] & 3;
	}

	public static int getBMode(int int1) {
		return luaP_opmodes[int1] >> 4 & 3;
	}

	public static int getCMode(int int1) {
		return luaP_opmodes[int1] >> 2 & 3;
	}

	public static boolean testTMode(int int1) {
		return 0 != (luaP_opmodes[int1] & 128);
	}
}
