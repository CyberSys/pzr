package org.luaj.kahluafork.compiler;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.Prototype;
import zombie.core.Core;


public class LexState {
	public int nCcalls;
	protected static final String RESERVED_LOCAL_VAR_FOR_CONTROL = "(for control)";
	protected static final String RESERVED_LOCAL_VAR_FOR_STATE = "(for state)";
	protected static final String RESERVED_LOCAL_VAR_FOR_GENERATOR = "(for generator)";
	protected static final String RESERVED_LOCAL_VAR_FOR_STEP = "(for step)";
	protected static final String RESERVED_LOCAL_VAR_FOR_LIMIT = "(for limit)";
	protected static final String RESERVED_LOCAL_VAR_FOR_INDEX = "(for index)";
	protected static final String[] RESERVED_LOCAL_VAR_KEYWORDS = new String[]{"(for control)", "(for generator)", "(for index)", "(for limit)", "(for state)", "(for step)"};
	private static final Hashtable RESERVED_LOCAL_VAR_KEYWORDS_TABLE = new Hashtable();
	private static final int EOZ = -1;
	private static final int MAXSRC = 80;
	private static final int MAX_INT = 2147483645;
	private static final int UCHAR_MAX = 255;
	private static final int LUAI_MAXCCALLS = 200;
	static final int NO_JUMP = -1;
	static final int OPR_ADD = 0;
	static final int OPR_SUB = 1;
	static final int OPR_MUL = 2;
	static final int OPR_DIV = 3;
	static final int OPR_MOD = 4;
	static final int OPR_POW = 5;
	static final int OPR_CONCAT = 6;
	static final int OPR_NE = 7;
	static final int OPR_EQ = 8;
	static final int OPR_LT = 9;
	static final int OPR_LE = 10;
	static final int OPR_GT = 11;
	static final int OPR_GE = 12;
	static final int OPR_AND = 13;
	static final int OPR_OR = 14;
	static final int OPR_NOBINOPR = 15;
	static final int OPR_MINUS = 0;
	static final int OPR_NOT = 1;
	static final int OPR_LEN = 2;
	static final int OPR_NOUNOPR = 3;
	static final int VVOID = 0;
	static final int VNIL = 1;
	static final int VTRUE = 2;
	static final int VFALSE = 3;
	static final int VK = 4;
	static final int VKNUM = 5;
	static final int VLOCAL = 6;
	static final int VUPVAL = 7;
	static final int VGLOBAL = 8;
	static final int VINDEXED = 9;
	static final int VJMP = 10;
	static final int VRELOCABLE = 11;
	static final int VNONRELOC = 12;
	static final int VCALL = 13;
	static final int VVARARG = 14;
	int current;
	int linenumber;
	int lastline;
	final Token t = new Token();
	final Token lookahead = new Token();
	FuncState fs;
	Reader z;
	byte[] buff;
	int nbuff;
	String source;
	static final String[] luaX_tokens;
	static final int TK_AND = 257;
	static final int TK_BREAK = 258;
	static final int TK_DO = 259;
	static final int TK_ELSE = 260;
	static final int TK_ELSEIF = 261;
	static final int TK_END = 262;
	static final int TK_FALSE = 263;
	static final int TK_FOR = 264;
	static final int TK_FUNCTION = 265;
	static final int TK_IF = 266;
	static final int TK_IN = 267;
	static final int TK_LOCAL = 268;
	static final int TK_NIL = 269;
	static final int TK_NOT = 270;
	static final int TK_OR = 271;
	static final int TK_REPEAT = 272;
	static final int TK_RETURN = 273;
	static final int TK_THEN = 274;
	static final int TK_TRUE = 275;
	static final int TK_UNTIL = 276;
	static final int TK_WHILE = 277;
	static final int TK_CONCAT = 278;
	static final int TK_DOTS = 279;
	static final int TK_EQ = 280;
	static final int TK_GE = 281;
	static final int TK_LE = 282;
	static final int TK_NE = 283;
	static final int TK_NUMBER = 284;
	static final int TK_NAME = 285;
	static final int TK_STRING = 286;
	static final int TK_EOS = 287;
	static final int FIRST_RESERVED = 257;
	static final int NUM_RESERVED = 21;
	static final Hashtable RESERVED;
	static final int[] priorityLeft;
	static final int[] priorityRight;
	static final int UNARY_PRIORITY = 8;

	private static final String LUA_QS(String string) {
		return "\'" + string + "\'";
	}

	private static final String LUA_QL(Object object) {
		return LUA_QS(String.valueOf(object));
	}

	public static boolean isReservedKeyword(String string) {
		return RESERVED_LOCAL_VAR_KEYWORDS_TABLE.containsKey(string);
	}

	private boolean isalnum(int int1) {
		return int1 >= 48 && int1 <= 57 || int1 >= 97 && int1 <= 122 || int1 >= 65 && int1 <= 90 || int1 == 95;
	}

	private boolean isalpha(int int1) {
		return int1 >= 97 && int1 <= 122 || int1 >= 65 && int1 <= 90;
	}

	private boolean isdigit(int int1) {
		return int1 >= 48 && int1 <= 57;
	}

	private boolean isspace(int int1) {
		return int1 <= 32;
	}

	public static Prototype compile(int int1, Reader reader, String string, String string2) {
		if (string != null) {
			string2 = string;
		} else {
			string = "stdin";
			string2 = "[string \"" + trim(string2, 80) + "\"]";
		}

		LexState lexState = new LexState(reader, int1, string2);
		FuncState funcState = new FuncState(lexState);
		funcState.isVararg = 2;
		funcState.f.name = string;
		lexState.next();
		lexState.chunk();
		lexState.check(287);
		lexState.close_func();
		FuncState._assert(funcState.prev == null);
		FuncState._assert(funcState.f.numUpvalues == 0);
		FuncState._assert(lexState.fs == null);
		return funcState.f;
	}

	public LexState(Reader reader, int int1, String string) {
		this.z = reader;
		this.buff = new byte[32];
		this.lookahead.token = 287;
		this.fs = null;
		this.linenumber = 1;
		this.lastline = 1;
		this.source = string;
		this.nbuff = 0;
		this.current = int1;
		this.skipShebang();
	}

	void nextChar() {
		try {
			this.current = this.z.read();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			this.current = -1;
		}
	}

	boolean currIsNewline() {
		return this.current == 10 || this.current == 13;
	}

	void save_and_next() {
		this.save(this.current);
		this.nextChar();
	}

	void save(int int1) {
		if (this.buff == null || this.nbuff + 1 > this.buff.length) {
			this.buff = FuncState.realloc(this.buff, this.nbuff * 2 + 1);
		}

		this.buff[this.nbuff++] = (byte)int1;
	}

	String token2str(int int1) {
		if (int1 < 257) {
			return iscntrl(int1) ? "char(" + int1 + ")" : String.valueOf((char)int1);
		} else {
			return luaX_tokens[int1 - 257];
		}
	}

	private static boolean iscntrl(int int1) {
		return int1 < 32;
	}

	String txtToken(int int1) {
		switch (int1) {
		case 284: 
		
		case 285: 
		
		case 286: 
			return new String(this.buff, 0, this.nbuff);
		
		default: 
			return this.token2str(int1);
		
		}
	}

	void lexerror(String string, int int1) {
		String string2 = this.source;
		String string3;
		if (int1 != 0) {
			string3 = string2 + ":" + this.linenumber + ": " + string + " near `" + this.txtToken(int1) + "`";
		} else {
			string3 = string2 + ":" + this.linenumber + ": " + string;
		}

		throw new KahluaException(string3);
	}

	private static String trim(String string, int int1) {
		if (string.length() > int1) {
			String string2 = string.substring(0, int1 - 3);
			return string2 + "...";
		} else {
			return string;
		}
	}

	void syntaxerror(String string) {
		this.lexerror(string, this.t.token);
	}

	String newstring(byte[] byteArray, int int1, int int2) {
		try {
			String string = new String(byteArray, int1, int2, "UTF-8");
			return string;
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			return null;
		}
	}

	void inclinenumber() {
		int int1 = this.current;
		FuncState._assert(this.currIsNewline());
		this.nextChar();
		if (this.currIsNewline() && this.current != int1) {
			this.nextChar();
		}

		if (++this.linenumber >= 2147483645) {
			this.syntaxerror("chunk has too many lines");
		}
	}

	private void skipShebang() {
		if (this.current == 35) {
			while (!this.currIsNewline() && this.current != -1) {
				this.nextChar();
			}
		}
	}

	boolean check_next(String string) {
		if (string.indexOf(this.current) < 0) {
			return false;
		} else {
			this.save_and_next();
			return true;
		}
	}

	void str2d(String string, Token token) {
		try {
			double double1;
			if (string.startsWith("0x")) {
				double1 = (double)Long.parseLong(string.substring(2), 16);
			} else {
				double1 = Double.parseDouble(string);
			}

			token.r = double1;
		} catch (NumberFormatException numberFormatException) {
			this.lexerror("malformed number", 284);
		}
	}

	void read_numeral(Token token) {
		FuncState._assert(this.isdigit(this.current));
		do {
			do {
				this.save_and_next();
			}	 while (this.isdigit(this.current));
		} while (this.current == 46);

		if (this.check_next("Ee")) {
			this.check_next("+-");
		}

		while (this.isalnum(this.current) || this.current == 95) {
			this.save_and_next();
		}

		String string = new String(this.buff, 0, this.nbuff);
		this.str2d(string, token);
	}

	int skip_sep() {
		int int1 = 0;
		int int2 = this.current;
		FuncState._assert(int2 == 91 || int2 == 93);
		this.save_and_next();
		while (this.current == 61) {
			this.save_and_next();
			++int1;
		}

		return this.current == int2 ? int1 : -int1 - 1;
	}

	void read_long_string(Token token, int int1) {
		int int2 = 0;
		this.save_and_next();
		if (this.currIsNewline()) {
			this.inclinenumber();
		}

		boolean boolean1 = false;
		while (!boolean1) {
			switch (this.current) {
			case -1: 
				this.lexerror(token != null ? "unfinished long string" : "unfinished long comment", 287);
				break;
			
			case 10: 
			
			case 13: 
				this.save(10);
				this.inclinenumber();
				if (token == null) {
					this.nbuff = 0;
				}

				break;
			
			case 91: 
				if (this.skip_sep() == int1) {
					this.save_and_next();
					++int2;
				}

				break;
			
			case 93: 
				if (this.skip_sep() == int1) {
					this.save_and_next();
					boolean1 = true;
				}

				break;
			
			default: 
				if (token != null) {
					this.save_and_next();
				} else {
					this.nextChar();
				}

			
			}
		}

		if (token != null) {
			token.ts = this.newstring(this.buff, 2 + int1, this.nbuff - 2 * (2 + int1));
		}
	}

	void read_string(int int1, Token token) {
		this.save_and_next();
		while (true) {
			byte byte1;
			label53: while (true) {
				while (this.current != int1) {
					switch (this.current) {
					case -1: 
						this.lexerror("unfinished string", 287);
						break;
					
					case 10: 
					
					case 13: 
						this.lexerror("unfinished string", 286);
						break;
					
					case 92: 
						this.nextChar();
						int int2;
						int int3;
						switch (this.current) {
						case -1: 
							continue;
						
						case 10: 
						
						case 13: 
							this.save(10);
							this.inclinenumber();
							continue;
						
						case 97: 
							byte1 = 7;
							break label53;
						
						case 98: 
							byte1 = 8;
							break label53;
						
						case 102: 
							byte1 = 12;
							break label53;
						
						case 110: 
							byte1 = 10;
							break label53;
						
						case 114: 
							byte1 = 13;
							break label53;
						
						case 116: 
							byte1 = 9;
							break label53;
						
						case 118: 
							byte1 = 11;
							break label53;
						
						default: 
							if (!this.isdigit(this.current)) {
								this.save_and_next();
								continue;
							}

							int2 = 0;
							int3 = 0;
						
						}

						do {
							int3 = 10 * int3 + (this.current - 48);
							this.nextChar();
							++int2;
						}				 while (int2 < 3 && this.isdigit(this.current));

						if (int3 > 255) {
							this.lexerror("escape sequence too large", 286);
						}

						this.save(int3);
						break;
					
					default: 
						this.save_and_next();
					
					}
				}

				this.save_and_next();
				token.ts = this.newstring(this.buff, 1, this.nbuff - 2);
				return;
			}

			this.save(byte1);
			this.nextChar();
		}
	}

	int llex(Token token) {
		this.nbuff = 0;
		label103: while (true) {
			int int1;
			switch (this.current) {
			case -1: 
				return 287;
			
			case 10: 
			
			case 13: 
				this.inclinenumber();
				break;
			
			case 34: 
			
			case 39: 
				this.read_string(this.current, token);
				return 286;
			
			case 45: 
				this.nextChar();
				if (this.current != 45) {
					return 45;
				}

				this.nextChar();
				if (this.current == 91) {
					int1 = this.skip_sep();
					this.nbuff = 0;
					if (int1 >= 0) {
						this.read_long_string((Token)null, int1);
						this.nbuff = 0;
						break;
					}
				}

				while (true) {
					if (this.currIsNewline() || this.current == -1) {
						continue label103;
					}

					this.nextChar();
				}

			
			case 46: 
				this.save_and_next();
				if (this.check_next(".")) {
					if (this.check_next(".")) {
						return 279;
					}

					return 278;
				}

				if (!this.isdigit(this.current)) {
					return 46;
				}

				this.read_numeral(token);
				return 284;
			
			case 60: 
				this.nextChar();
				if (this.current != 61) {
					return 60;
				}

				this.nextChar();
				return 282;
			
			case 62: 
				this.nextChar();
				if (this.current != 61) {
					return 62;
				}

				this.nextChar();
				return 281;
			
			case 91: 
				int1 = this.skip_sep();
				if (int1 >= 0) {
					this.read_long_string(token, int1);
					return 286;
				}

				if (int1 == -1) {
					return 91;
				}

				this.lexerror("invalid long string delimiter", 286);
			
			case 61: 
				this.nextChar();
				if (this.current != 61) {
					return 61;
				}

				this.nextChar();
				return 280;
			
			case 126: 
				this.nextChar();
				if (this.current != 61) {
					return 126;
				}

				this.nextChar();
				return 283;
			
			default: 
				if (!this.isspace(this.current)) {
					if (this.isdigit(this.current)) {
						this.read_numeral(token);
						return 284;
					}

					if (!this.isalpha(this.current) && this.current != 95) {
						int1 = this.current;
						this.nextChar();
						return int1;
					}

					do {
						do {
							this.save_and_next();
						}				 while (this.isalnum(this.current));
					}			 while (this.current == 95);

					String string = this.newstring(this.buff, 0, this.nbuff);
					if (RESERVED.containsKey(string)) {
						return (Integer)RESERVED.get(string);
					}

					token.ts = string;
					return 285;
				}

				FuncState._assert(!this.currIsNewline());
				this.nextChar();
			
			}
		}
	}

	void next() {
		this.lastline = this.linenumber;
		if (this.lookahead.token != 287) {
			this.t.set(this.lookahead);
			this.lookahead.token = 287;
		} else {
			this.t.token = this.llex(this.t);
		}
	}

	void lookahead() {
		FuncState._assert(this.lookahead.token == 287);
		this.lookahead.token = this.llex(this.lookahead);
	}

	boolean hasmultret(int int1) {
		return int1 == 13 || int1 == 14;
	}

	void error_expected(int int1) {
		String string = this.token2str(int1);
		this.syntaxerror(LUA_QS(string) + " expected");
	}

	boolean testnext(int int1) {
		if (this.t.token == int1) {
			this.next();
			return true;
		} else {
			return false;
		}
	}

	void check(int int1) {
		if (this.t.token != int1) {
			this.error_expected(int1);
		}
	}

	void checknext(int int1) {
		this.check(int1);
		this.next();
	}

	void check_condition(boolean boolean1, String string) {
		if (!boolean1) {
			this.syntaxerror(string);
		}
	}

	void check_match(int int1, int int2, int int3) {
		if (!this.testnext(int1)) {
			if (int3 == this.linenumber) {
				this.error_expected(int1);
			} else {
				String string = LUA_QS(this.token2str(int1));
				this.syntaxerror(string + " expected (to close " + LUA_QS(this.token2str(int2)) + " at line " + int3 + ")");
			}
		}
	}

	String str_checkname() {
		this.check(285);
		String string = this.t.ts;
		this.next();
		return string;
	}

	void codestring(ExpDesc expDesc, String string) {
		expDesc.init(4, this.fs.stringK(string));
	}

	void checkname(ExpDesc expDesc) {
		this.codestring(expDesc, this.str_checkname());
	}

	int registerlocalvar(String string) {
		FuncState funcState = this.fs;
		if (funcState.locvars == null || funcState.nlocvars + 1 > funcState.locvars.length) {
			funcState.locvars = FuncState.realloc(funcState.locvars, funcState.nlocvars * 2 + 1);
		}

		funcState.locvars[funcState.nlocvars] = string;
		return funcState.nlocvars++;
	}

	void new_localvarliteral(String string, int int1) {
		this.new_localvar(string, int1);
	}

	void new_localvar(String string, int int1, int int2) {
		FuncState funcState = this.fs;
		funcState.checklimit(funcState.nactvar + int1 + 1, 200, "local variables");
		funcState.actvar[funcState.nactvar + int1] = (short)this.registerlocalvar(string);
		if (Core.bDebug) {
			funcState.actvarline[funcState.actvar[funcState.nactvar + int1]] = this.linenumber;
		}
	}

	void new_localvar(String string, int int1) {
		FuncState funcState = this.fs;
		funcState.checklimit(funcState.nactvar + int1 + 1, 200, "local variables");
		funcState.actvar[funcState.nactvar + int1] = (short)this.registerlocalvar(string);
		if (Core.bDebug) {
			funcState.actvarline[funcState.actvar[funcState.nactvar + int1]] = this.linenumber;
		}
	}

	void adjustlocalvars(int int1) {
		FuncState funcState = this.fs;
		funcState.nactvar += int1;
	}

	void removevars(int int1) {
		FuncState funcState = this.fs;
		funcState.nactvar = int1;
	}

	void singlevar(ExpDesc expDesc) {
		String string = this.str_checkname();
		FuncState funcState = this.fs;
		if (funcState.singlevaraux(string, expDesc, 1) == 8) {
			expDesc.info = funcState.stringK(string);
		}
	}

	void adjust_assign(int int1, int int2, ExpDesc expDesc) {
		FuncState funcState = this.fs;
		int int3 = int1 - int2;
		if (this.hasmultret(expDesc.k)) {
			++int3;
			if (int3 < 0) {
				int3 = 0;
			}

			funcState.setreturns(expDesc, int3);
			if (int3 > 1) {
				funcState.reserveregs(int3 - 1);
			}
		} else {
			if (expDesc.k != 0) {
				funcState.exp2nextreg(expDesc);
			}

			if (int3 > 0) {
				int int4 = funcState.freereg;
				funcState.reserveregs(int3);
				funcState.nil(int4, int3);
			}
		}
	}

	void enterlevel() {
		if (++this.nCcalls > 200) {
			this.lexerror("chunk has too many syntax levels", 0);
		}
	}

	void leavelevel() {
		--this.nCcalls;
	}

	void pushclosure(FuncState funcState, ExpDesc expDesc) {
		FuncState funcState2 = this.fs;
		Prototype prototype = funcState2.f;
		if (prototype.prototypes == null || funcState2.np + 1 > prototype.prototypes.length) {
			prototype.prototypes = FuncState.realloc(prototype.prototypes, funcState2.np * 2 + 1);
		}

		prototype.prototypes[funcState2.np++] = funcState.f;
		expDesc.init(11, funcState2.codeABx(36, 0, funcState2.np - 1));
		for (int int1 = 0; int1 < funcState.f.numUpvalues; ++int1) {
			int int2 = funcState.upvalues_k[int1] == 6 ? 0 : 4;
			funcState2.codeABC(int2, 0, funcState.upvalues_info[int1], 0);
		}
	}

	void close_func() {
		FuncState funcState = this.fs;
		Prototype prototype = funcState.f;
		prototype.isVararg = funcState.isVararg != 0;
		this.removevars(0);
		funcState.ret(0, 0);
		prototype.code = FuncState.realloc(prototype.code, funcState.pc);
		prototype.lines = FuncState.realloc(prototype.lines, funcState.pc);
		prototype.constants = FuncState.realloc(prototype.constants, funcState.nk);
		prototype.prototypes = FuncState.realloc(prototype.prototypes, funcState.np);
		funcState.locvars = FuncState.realloc(funcState.locvars, funcState.nlocvars);
		if (Core.bDebug) {
			prototype.locvars = funcState.locvars;
			prototype.locvarlines = funcState.actvarline;
		}

		funcState.upvalues = FuncState.realloc(funcState.upvalues, prototype.numUpvalues);
		FuncState._assert(funcState.bl == null);
		this.fs = funcState.prev;
	}

	void field(ExpDesc expDesc) {
		FuncState funcState = this.fs;
		ExpDesc expDesc2 = new ExpDesc();
		funcState.exp2anyreg(expDesc);
		this.next();
		this.checkname(expDesc2);
		funcState.indexed(expDesc, expDesc2);
	}

	void yindex(ExpDesc expDesc) {
		this.next();
		this.expr(expDesc);
		this.fs.exp2val(expDesc);
		this.checknext(93);
	}

	void recfield(ConsControl consControl) {
		FuncState funcState = this.fs;
		int int1 = this.fs.freereg;
		ExpDesc expDesc = new ExpDesc();
		ExpDesc expDesc2 = new ExpDesc();
		if (this.t.token == 285) {
			funcState.checklimit(consControl.nh, 2147483645, "items in a constructor");
			this.checkname(expDesc);
		} else {
			this.yindex(expDesc);
		}

		++consControl.nh;
		this.checknext(61);
		int int2 = funcState.exp2RK(expDesc);
		this.expr(expDesc2);
		funcState.codeABC(9, consControl.t.info, int2, funcState.exp2RK(expDesc2));
		funcState.freereg = int1;
	}

	void listfield(ConsControl consControl) {
		this.expr(consControl.v);
		this.fs.checklimit(consControl.na, 2147483645, "items in a constructor");
		++consControl.na;
		++consControl.tostore;
	}

	void constructor(ExpDesc expDesc) {
		FuncState funcState = this.fs;
		int int1 = this.linenumber;
		int int2 = funcState.codeABC(10, 0, 0, 0);
		ConsControl consControl = new ConsControl();
		consControl.na = consControl.nh = consControl.tostore = 0;
		consControl.t = expDesc;
		expDesc.init(11, int2);
		consControl.v.init(0, 0);
		funcState.exp2nextreg(expDesc);
		this.checknext(123);
		do {
			FuncState._assert(consControl.v.k == 0 || consControl.tostore > 0);
			if (this.t.token == 125) {
				break;
			}

			funcState.closelistfield(consControl);
			switch (this.t.token) {
			case 91: 
				this.recfield(consControl);
				break;
			
			case 285: 
				this.lookahead();
				if (this.lookahead.token != 61) {
					this.listfield(consControl);
				} else {
					this.recfield(consControl);
				}

				break;
			
			default: 
				this.listfield(consControl);
			
			}
		} while (this.testnext(44) || this.testnext(59));

		this.check_match(125, 123, int1);
		funcState.lastlistfield(consControl);
		InstructionPtr instructionPtr = new InstructionPtr(funcState.f.code, int2);
		FuncState.SETARG_B(instructionPtr, luaO_int2fb(consControl.na));
		FuncState.SETARG_C(instructionPtr, luaO_int2fb(consControl.nh));
	}

	static int luaO_int2fb(int int1) {
		int int2;
		for (int2 = 0; int1 >= 16; ++int2) {
			int1 = int1 + 1 >> 1;
		}

		return int1 < 8 ? int1 : int2 + 1 << 3 | int1 - 8;
	}

	void parlist() {
		FuncState funcState = this.fs;
		Prototype prototype = funcState.f;
		int int1 = 0;
		funcState.isVararg = 0;
		if (this.t.token != 41) {
			do {
				switch (this.t.token) {
				case 279: 
					this.next();
					funcState.isVararg |= 2;
					break;
				
				case 285: 
					this.new_localvar(this.str_checkname(), int1++);
					break;
				
				default: 
					this.syntaxerror("<name> or " + LUA_QL("...") + " expected");
				
				}
			}	 while (funcState.isVararg == 0 && this.testnext(44));
		}

		this.adjustlocalvars(int1);
		prototype.numParams = funcState.nactvar - (funcState.isVararg & 1);
		funcState.reserveregs(funcState.nactvar);
	}

	void body(ExpDesc expDesc, boolean boolean1, int int1) {
		FuncState funcState = new FuncState(this, this.t.ts);
		funcState.linedefined = int1;
		this.checknext(40);
		if (boolean1) {
			this.new_localvarliteral("self", 0);
			this.adjustlocalvars(1);
		}

		this.parlist();
		this.checknext(41);
		this.chunk();
		funcState.lastlinedefined = this.linenumber;
		this.check_match(262, 265, int1);
		this.close_func();
		this.pushclosure(funcState, expDesc);
	}

	int explist1(ExpDesc expDesc) {
		int int1 = 1;
		this.expr(expDesc);
		while (this.testnext(44)) {
			this.fs.exp2nextreg(expDesc);
			this.expr(expDesc);
			++int1;
		}

		return int1;
	}

	void funcargs(ExpDesc expDesc) {
		FuncState funcState = this.fs;
		ExpDesc expDesc2 = new ExpDesc();
		int int1 = this.linenumber;
		switch (this.t.token) {
		case 40: 
			if (int1 != this.lastline) {
				this.syntaxerror("ambiguous syntax (function call x new statement)");
			}

			this.next();
			if (this.t.token == 41) {
				expDesc2.k = 0;
			} else {
				this.explist1(expDesc2);
				funcState.setmultret(expDesc2);
			}

			this.check_match(41, 40, int1);
			break;
		
		case 123: 
			this.constructor(expDesc2);
			break;
		
		case 286: 
			this.codestring(expDesc2, this.t.ts);
			this.next();
			break;
		
		default: 
			this.syntaxerror("function arguments expected");
			return;
		
		}
		FuncState._assert(expDesc.k == 12);
		int int2 = expDesc.info;
		int int3;
		if (this.hasmultret(expDesc2.k)) {
			int3 = -1;
		} else {
			if (expDesc2.k != 0) {
				funcState.exp2nextreg(expDesc2);
			}

			int3 = funcState.freereg - (int2 + 1);
		}

		expDesc.init(13, funcState.codeABC(28, int2, int3 + 1, 2));
		funcState.fixline(int1);
		funcState.freereg = int2 + 1;
	}

	void prefixexp(ExpDesc expDesc) {
		switch (this.t.token) {
		case 40: 
			int int1 = this.linenumber;
			this.next();
			this.expr(expDesc);
			this.check_match(41, 40, int1);
			this.fs.dischargevars(expDesc);
			return;
		
		case 285: 
			this.singlevar(expDesc);
			return;
		
		default: 
			this.syntaxerror("unexpected symbol");
		
		}
	}

	void primaryexp(ExpDesc expDesc) {
		FuncState funcState = this.fs;
		this.prefixexp(expDesc);
		while (true) {
			ExpDesc expDesc2;
			switch (this.t.token) {
			case 40: 
			
			case 123: 
			
			case 286: 
				funcState.exp2nextreg(expDesc);
				this.funcargs(expDesc);
				break;
			
			case 46: 
				this.field(expDesc);
				break;
			
			case 58: 
				expDesc2 = new ExpDesc();
				this.next();
				this.checkname(expDesc2);
				funcState.self(expDesc, expDesc2);
				this.funcargs(expDesc);
				break;
			
			case 91: 
				expDesc2 = new ExpDesc();
				funcState.exp2anyreg(expDesc);
				this.yindex(expDesc2);
				funcState.indexed(expDesc, expDesc2);
				break;
			
			default: 
				return;
			
			}
		}
	}

	void simpleexp(ExpDesc expDesc) {
		switch (this.t.token) {
		case 123: 
			this.constructor(expDesc);
			return;
		
		case 263: 
			expDesc.init(3, 0);
			break;
		
		case 265: 
			this.next();
			this.body(expDesc, false, this.linenumber);
			return;
		
		case 269: 
			expDesc.init(1, 0);
			break;
		
		case 275: 
			expDesc.init(2, 0);
			break;
		
		case 279: 
			FuncState funcState = this.fs;
			this.check_condition(funcState.isVararg != 0, "cannot use " + LUA_QL("...") + " outside a vararg function");
			funcState.isVararg &= -5;
			expDesc.init(14, funcState.codeABC(37, 0, 1, 0));
			break;
		
		case 284: 
			expDesc.init(5, 0);
			expDesc.setNval(this.t.r);
			break;
		
		case 286: 
			this.codestring(expDesc, this.t.ts);
			break;
		
		default: 
			this.primaryexp(expDesc);
			return;
		
		}
		this.next();
	}

	int getunopr(int int1) {
		switch (int1) {
		case 35: 
			return 2;
		
		case 45: 
			return 0;
		
		case 270: 
			return 1;
		
		default: 
			return 3;
		
		}
	}

	int getbinopr(int int1) {
		switch (int1) {
		case 37: 
			return 4;
		
		case 42: 
			return 2;
		
		case 43: 
			return 0;
		
		case 45: 
			return 1;
		
		case 47: 
			return 3;
		
		case 60: 
			return 9;
		
		case 62: 
			return 11;
		
		case 94: 
			return 5;
		
		case 257: 
			return 13;
		
		case 271: 
			return 14;
		
		case 278: 
			return 6;
		
		case 280: 
			return 8;
		
		case 281: 
			return 12;
		
		case 282: 
			return 10;
		
		case 283: 
			return 7;
		
		default: 
			return 15;
		
		}
	}

	int subexpr(ExpDesc expDesc, int int1) {
		this.enterlevel();
		int int2 = this.getunopr(this.t.token);
		if (int2 != 3) {
			this.next();
			this.subexpr(expDesc, 8);
			this.fs.prefix(int2, expDesc);
		} else {
			this.simpleexp(expDesc);
		}

		int int3;
		int int4;
		for (int3 = this.getbinopr(this.t.token); int3 != 15 && priorityLeft[int3] > int1; int3 = int4) {
			ExpDesc expDesc2 = new ExpDesc();
			this.next();
			this.fs.infix(int3, expDesc);
			int4 = this.subexpr(expDesc2, priorityRight[int3]);
			this.fs.posfix(int3, expDesc, expDesc2);
		}

		this.leavelevel();
		return int3;
	}

	void expr(ExpDesc expDesc) {
		this.subexpr(expDesc, 0);
	}

	boolean block_follow(int int1) {
		switch (int1) {
		case 260: 
		
		case 261: 
		
		case 262: 
		
		case 276: 
		
		case 287: 
			return true;
		
		default: 
			return false;
		
		}
	}

	void block() {
		FuncState funcState = this.fs;
		BlockCnt blockCnt = new BlockCnt();
		funcState.enterblock(blockCnt, false);
		this.chunk();
		FuncState._assert(blockCnt.breaklist == -1);
		funcState.leaveblock();
	}

	void check_conflict(LHS_assign lHS_assign, ExpDesc expDesc) {
		FuncState funcState = this.fs;
		int int1 = funcState.freereg;
		boolean boolean1;
		for (boolean1 = false; lHS_assign != null; lHS_assign = lHS_assign.prev) {
			if (lHS_assign.v.k == 9) {
				if (lHS_assign.v.info == expDesc.info) {
					boolean1 = true;
					lHS_assign.v.info = int1;
				}

				if (lHS_assign.v.aux == expDesc.info) {
					boolean1 = true;
					lHS_assign.v.aux = int1;
				}
			}
		}

		if (boolean1) {
			funcState.codeABC(0, funcState.freereg, expDesc.info, 0);
			funcState.reserveregs(1);
		}
	}

	void assignment(LHS_assign lHS_assign, int int1) {
		ExpDesc expDesc = new ExpDesc();
		this.check_condition(6 <= lHS_assign.v.k && lHS_assign.v.k <= 9, "syntax error");
		if (this.testnext(44)) {
			LHS_assign lHS_assign2 = new LHS_assign();
			lHS_assign2.prev = lHS_assign;
			this.primaryexp(lHS_assign2.v);
			if (lHS_assign2.v.k == 6) {
				this.check_conflict(lHS_assign, lHS_assign2.v);
			}

			this.assignment(lHS_assign2, int1 + 1);
		} else {
			this.checknext(61);
			int int2 = this.explist1(expDesc);
			if (int2 == int1) {
				this.fs.setoneret(expDesc);
				this.fs.storevar(lHS_assign.v, expDesc);
				return;
			}

			this.adjust_assign(int1, int2, expDesc);
			if (int2 > int1) {
				FuncState funcState = this.fs;
				funcState.freereg -= int2 - int1;
			}
		}

		expDesc.init(12, this.fs.freereg - 1);
		this.fs.storevar(lHS_assign.v, expDesc);
	}

	int cond() {
		ExpDesc expDesc = new ExpDesc();
		this.expr(expDesc);
		if (expDesc.k == 1) {
			expDesc.k = 3;
		}

		this.fs.goiftrue(expDesc);
		return expDesc.f;
	}

	void breakstat() {
		FuncState funcState = this.fs;
		BlockCnt blockCnt = funcState.bl;
		boolean boolean1;
		for (boolean1 = false; blockCnt != null && !blockCnt.isbreakable; blockCnt = blockCnt.previous) {
			boolean1 |= blockCnt.upval;
		}

		if (blockCnt == null) {
			this.syntaxerror("no loop to break");
		}

		if (boolean1) {
			funcState.codeABC(35, blockCnt.nactvar, 0, 0);
		}

		blockCnt.breaklist = funcState.concat(blockCnt.breaklist, funcState.jump());
	}

	void whilestat(int int1) {
		FuncState funcState = this.fs;
		BlockCnt blockCnt = new BlockCnt();
		this.next();
		int int2 = funcState.getlabel();
		int int3 = this.cond();
		funcState.enterblock(blockCnt, true);
		this.checknext(259);
		this.block();
		funcState.patchlist(funcState.jump(), int2);
		this.check_match(262, 277, int1);
		funcState.leaveblock();
		funcState.patchtohere(int3);
	}

	void repeatstat(int int1) {
		FuncState funcState = this.fs;
		int int2 = funcState.getlabel();
		BlockCnt blockCnt = new BlockCnt();
		BlockCnt blockCnt2 = new BlockCnt();
		funcState.enterblock(blockCnt, true);
		funcState.enterblock(blockCnt2, false);
		this.next();
		this.chunk();
		this.check_match(276, 272, int1);
		int int3 = this.cond();
		if (!blockCnt2.upval) {
			funcState.leaveblock();
			funcState.patchlist(int3, int2);
		} else {
			this.breakstat();
			funcState.patchtohere(int3);
			funcState.leaveblock();
			funcState.patchlist(funcState.jump(), int2);
		}

		funcState.leaveblock();
	}

	int exp1() {
		ExpDesc expDesc = new ExpDesc();
		this.expr(expDesc);
		int int1 = expDesc.k;
		this.fs.exp2nextreg(expDesc);
		return int1;
	}

	void forbody(int int1, int int2, int int3, boolean boolean1) {
		BlockCnt blockCnt = new BlockCnt();
		FuncState funcState = this.fs;
		this.adjustlocalvars(3);
		this.checknext(259);
		int int4 = boolean1 ? funcState.codeAsBx(32, int1, -1) : funcState.jump();
		funcState.enterblock(blockCnt, false);
		this.adjustlocalvars(int3);
		funcState.reserveregs(int3);
		this.block();
		funcState.leaveblock();
		funcState.patchtohere(int4);
		int int5 = boolean1 ? funcState.codeAsBx(31, int1, -1) : funcState.codeABC(33, int1, 0, int3);
		funcState.fixline(int2);
		funcState.patchlist(boolean1 ? int5 : funcState.jump(), int4 + 1);
	}

	void fornum(String string, int int1) {
		FuncState funcState = this.fs;
		int int2 = funcState.freereg;
		this.new_localvarliteral("(for index)", 0);
		this.new_localvarliteral("(for limit)", 1);
		this.new_localvarliteral("(for step)", 2);
		this.new_localvar(string, 3);
		this.checknext(61);
		this.exp1();
		this.checknext(44);
		this.exp1();
		if (this.testnext(44)) {
			this.exp1();
		} else {
			funcState.codeABx(1, funcState.freereg, funcState.numberK(1.0));
			funcState.reserveregs(1);
		}

		this.forbody(int2, int1, 1, true);
	}

	void forlist(String string) {
		FuncState funcState = this.fs;
		ExpDesc expDesc = new ExpDesc();
		byte byte1 = 0;
		int int1 = funcState.freereg;
		int int2 = byte1 + 1;
		this.new_localvarliteral("(for generator)", byte1);
		this.new_localvarliteral("(for state)", int2++);
		this.new_localvarliteral("(for control)", int2++);
		this.new_localvar(string, int2++);
		while (this.testnext(44)) {
			this.new_localvar(this.str_checkname(), int2++);
		}

		this.checknext(267);
		int int3 = this.linenumber;
		this.adjust_assign(3, this.explist1(expDesc), expDesc);
		funcState.checkstack(3);
		this.forbody(int1, int3, int2 - 3, false);
	}

	void forstat(int int1) {
		FuncState funcState = this.fs;
		BlockCnt blockCnt = new BlockCnt();
		funcState.enterblock(blockCnt, true);
		this.next();
		String string = this.str_checkname();
		switch (this.t.token) {
		case 44: 
		
		case 267: 
			this.forlist(string);
			break;
		
		case 61: 
			this.fornum(string, int1);
			break;
		
		default: 
			String string2 = LUA_QL("=");
			this.syntaxerror(string2 + " or " + LUA_QL("in") + " expected");
		
		}
		this.check_match(262, 264, int1);
		funcState.leaveblock();
	}

	int test_then_block() {
		this.next();
		int int1 = this.cond();
		this.checknext(274);
		this.block();
		return int1;
	}

	void ifstat(int int1) {
		FuncState funcState = this.fs;
		int int2 = -1;
		int int3;
		for (int3 = this.test_then_block(); this.t.token == 261; int3 = this.test_then_block()) {
			int2 = funcState.concat(int2, funcState.jump());
			funcState.patchtohere(int3);
		}

		if (this.t.token == 260) {
			int2 = funcState.concat(int2, funcState.jump());
			funcState.patchtohere(int3);
			this.next();
			this.block();
		} else {
			int2 = funcState.concat(int2, int3);
		}

		funcState.patchtohere(int2);
		this.check_match(262, 266, int1);
	}

	void localfunc() {
		ExpDesc expDesc = new ExpDesc();
		ExpDesc expDesc2 = new ExpDesc();
		FuncState funcState = this.fs;
		this.new_localvar(this.str_checkname(), 0);
		expDesc.init(6, funcState.freereg);
		funcState.reserveregs(1);
		this.adjustlocalvars(1);
		this.body(expDesc2, false, this.linenumber);
		funcState.storevar(expDesc, expDesc2);
	}

	void localstat(int int1) {
		int int2 = 0;
		ExpDesc expDesc = new ExpDesc();
		do {
			this.new_localvar(this.str_checkname(), int2++, int1);
		} while (this.testnext(44));

		int int3;
		if (this.testnext(61)) {
			int3 = this.explist1(expDesc);
		} else {
			expDesc.k = 0;
			int3 = 0;
		}

		this.adjust_assign(int2, int3, expDesc);
		this.adjustlocalvars(int2);
	}

	boolean funcname(ExpDesc expDesc) {
		boolean boolean1 = false;
		this.singlevar(expDesc);
		while (this.t.token == 46) {
			this.field(expDesc);
		}

		if (this.t.token == 58) {
			boolean1 = true;
			this.field(expDesc);
		}

		return boolean1;
	}

	void funcstat(int int1) {
		ExpDesc expDesc = new ExpDesc();
		ExpDesc expDesc2 = new ExpDesc();
		this.next();
		boolean boolean1 = this.funcname(expDesc);
		this.body(expDesc2, boolean1, int1);
		this.fs.storevar(expDesc, expDesc2);
		this.fs.fixline(int1);
	}

	void exprstat() {
		FuncState funcState = this.fs;
		LHS_assign lHS_assign = new LHS_assign();
		this.primaryexp(lHS_assign.v);
		if (lHS_assign.v.k == 13) {
			FuncState.SETARG_C(funcState.getcodePtr(lHS_assign.v), 1);
		} else {
			lHS_assign.prev = null;
			this.assignment(lHS_assign, 1);
		}
	}

	void retstat() {
		FuncState funcState = this.fs;
		ExpDesc expDesc = new ExpDesc();
		this.next();
		int int1;
		int int2;
		if (!this.block_follow(this.t.token) && this.t.token != 59) {
			int2 = this.explist1(expDesc);
			if (this.hasmultret(expDesc.k)) {
				funcState.setmultret(expDesc);
				if (expDesc.k == 13 && int2 == 1) {
					FuncState.SET_OPCODE(funcState.getcodePtr(expDesc), 29);
					FuncState._assert(FuncState.GETARG_A(funcState.getcode(expDesc)) == funcState.nactvar);
				}

				int1 = funcState.nactvar;
				int2 = -1;
			} else if (int2 == 1) {
				int1 = funcState.exp2anyreg(expDesc);
			} else {
				funcState.exp2nextreg(expDesc);
				int1 = funcState.nactvar;
				FuncState._assert(int2 == funcState.freereg - int1);
			}
		} else {
			int2 = 0;
			int1 = 0;
		}

		funcState.ret(int1, int2);
	}

	boolean statement() {
		int int1 = this.linenumber;
		switch (this.t.token) {
		case 258: 
			this.next();
			this.breakstat();
			return true;
		
		case 259: 
			this.next();
			this.block();
			this.check_match(262, 259, int1);
			return false;
		
		case 260: 
		
		case 261: 
		
		case 262: 
		
		case 263: 
		
		case 267: 
		
		case 269: 
		
		case 270: 
		
		case 271: 
		
		case 274: 
		
		case 275: 
		
		case 276: 
		
		default: 
			this.exprstat();
			return false;
		
		case 264: 
			this.forstat(int1);
			return false;
		
		case 265: 
			this.funcstat(int1);
			return false;
		
		case 266: 
			this.ifstat(int1);
			return false;
		
		case 268: 
			this.next();
			if (this.testnext(265)) {
				this.localfunc();
			} else {
				this.localstat(int1);
			}

			return false;
		
		case 272: 
			this.repeatstat(int1);
			return false;
		
		case 273: 
			this.retstat();
			return true;
		
		case 277: 
			this.whilestat(int1);
			return false;
		
		}
	}

	void chunk() {
		boolean boolean1 = false;
		this.enterlevel();
		while (!boolean1 && !this.block_follow(this.t.token)) {
			boolean1 = this.statement();
			this.testnext(59);
			FuncState._assert(this.fs.f.maxStacksize >= this.fs.freereg && this.fs.freereg >= this.fs.nactvar);
			this.fs.freereg = this.fs.nactvar;
		}

		this.leavelevel();
	}

	static  {
	int var0;
	for (var0 = 0; var0 < RESERVED_LOCAL_VAR_KEYWORDS.length; ++var0) {
		RESERVED_LOCAL_VAR_KEYWORDS_TABLE.put(RESERVED_LOCAL_VAR_KEYWORDS[var0], Boolean.TRUE);
	}

		luaX_tokens = new String[]{"and", "break", "do", "else", "elseif", "end", "false", "for", "function", "if", "in", "local", "nil", "not", "or", "repeat", "return", "then", "true", "until", "while", "..", "...", "==", ">=", "<=", "~=", "<number>", "<name>", "<string>", "<eof>"};
		RESERVED = new Hashtable();
	for (var0 = 0; var0 < 21; ++var0) {
		String var1 = luaX_tokens[var0];
		RESERVED.put(var1, new Integer(257 + var0));
	}

		priorityLeft = new int[]{6, 6, 7, 7, 7, 10, 5, 3, 3, 3, 3, 3, 3, 2, 1};
		priorityRight = new int[]{6, 6, 7, 7, 7, 9, 4, 3, 3, 3, 3, 3, 3, 2, 1};
	}
}
