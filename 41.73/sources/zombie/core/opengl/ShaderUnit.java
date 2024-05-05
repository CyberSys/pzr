package zombie.core.opengl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.IndieFileLoader;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.DebugType;
import zombie.util.StringUtils;


public final class ShaderUnit {
	private final ShaderProgram m_parentProgram;
	private final String m_fileName;
	private final ShaderUnit.Type m_unitType;
	private int m_glID;
	private boolean m_isAttached;

	public ShaderUnit(ShaderProgram shaderProgram, String string, ShaderUnit.Type type) {
		this.m_parentProgram = shaderProgram;
		this.m_fileName = string;
		this.m_unitType = type;
		this.m_glID = 0;
		this.m_isAttached = false;
	}

	public String getFileName() {
		return this.m_fileName;
	}

	public boolean isCompiled() {
		return this.m_glID != 0;
	}

	public boolean compile() {
		if (DebugLog.isEnabled(DebugType.Shader)) {
			DebugLog.Shader.debugln(this.getFileName());
		}

		int int1 = getGlType(this.m_unitType);
		ArrayList arrayList = new ArrayList();
		String string = this.loadShaderFile(this.m_fileName, arrayList);
		if (string == null) {
			return false;
		} else {
			Iterator iterator = arrayList.iterator();
			DebugLogStream debugLogStream;
			String string2;
			while (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				if (this.m_parentProgram == null) {
					debugLogStream = DebugLog.Shader;
					string2 = this.getFileName();
					debugLogStream.error(string2 + "> Cannot include additional shader file. Parent program is null. " + string3);
					break;
				}

				String string4 = string3 + ".glsl";
				if (DebugLog.isEnabled(DebugType.Shader)) {
					debugLogStream = DebugLog.Shader;
					string2 = this.getFileName();
					debugLogStream.debugln(string2 + "> Loading additional shader unit: " + string4);
				}

				ShaderUnit shaderUnit = this.m_parentProgram.addShader(string4, this.m_unitType);
				if (!shaderUnit.isCompiled() && !shaderUnit.compile()) {
					debugLogStream = DebugLog.Shader;
					string2 = this.getFileName();
					debugLogStream.error(string2 + "> Included shader unit failed to compile: " + string4);
					return false;
				}
			}

			int int2 = ARBShaderObjects.glCreateShaderObjectARB(int1);
			if (int2 == 0) {
				debugLogStream = DebugLog.Shader;
				string2 = this.getFileName();
				debugLogStream.error(string2 + "> Failed to generate shaderID. Shader code:\n" + string);
				return false;
			} else {
				ARBShaderObjects.glShaderSourceARB(int2, string);
				ARBShaderObjects.glCompileShaderARB(int2);
				ShaderProgram.printLogInfo(int2);
				this.m_glID = int2;
				return true;
			}
		}
	}

	public boolean attach() {
		if (DebugLog.isEnabled(DebugType.Shader)) {
			DebugLog.Shader.debugln(this.getFileName());
		}

		if (this.getParentShaderProgramGLID() == 0) {
			DebugLog.Shader.error("Parent program does not exist.");
			return false;
		} else {
			if (!this.isCompiled()) {
				this.compile();
			}

			if (!this.isCompiled()) {
				return false;
			} else {
				ARBShaderObjects.glAttachObjectARB(this.getParentShaderProgramGLID(), this.getGLID());
				if (!PZGLUtil.checkGLError(false)) {
					this.destroy();
					return false;
				} else {
					this.m_isAttached = true;
					return true;
				}
			}
		}
	}

	public void destroy() {
		if (this.m_glID == 0) {
			this.m_isAttached = false;
		} else {
			DebugLog.Shader.debugln(this.getFileName());
			try {
				if (this.m_isAttached && this.getParentShaderProgramGLID() != 0) {
					ARBShaderObjects.glDetachObjectARB(this.getParentShaderProgramGLID(), this.m_glID);
					if (!PZGLUtil.checkGLError(false)) {
						DebugLog.Shader.error("ShaderUnit failed to detach: " + this.getFileName());
						return;
					}
				}

				ARBShaderObjects.glDeleteObjectARB(this.m_glID);
				PZGLUtil.checkGLError(false);
			} finally {
				this.m_glID = 0;
				this.m_isAttached = false;
			}
		}
	}

	public int getGLID() {
		return this.m_glID;
	}

	public int getParentShaderProgramGLID() {
		return this.m_parentProgram != null ? this.m_parentProgram.getShaderID() : 0;
	}

	private static int getGlType(ShaderUnit.Type type) {
		return type == ShaderUnit.Type.Vert ? '謱' : '謰';
	}

	private String loadShaderFile(String string, ArrayList arrayList) {
		arrayList.clear();
		String string2 = this.preProcessShaderFile(string, arrayList);
		if (string2 == null) {
			return null;
		} else {
			int int1 = string2.indexOf("#");
			if (int1 > 0) {
				string2 = string2.substring(int1);
			}

			return string2;
		}
	}

	private String preProcessShaderFile(String string, ArrayList arrayList) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string, false);
			try {
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				try {
					String string2 = System.getProperty("line.separator");
					for (String string3 = bufferedReader.readLine(); string3 != null; string3 = bufferedReader.readLine()) {
						String string4 = string3.trim();
						if (!string4.startsWith("#include ") || !this.processIncludeLine(string, stringBuilder, string4, string2, arrayList)) {
							stringBuilder.append(string4).append(string2);
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
				if (inputStreamReader != null) {
					try {
						inputStreamReader.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}
				}

				throw throwable3;
			}

			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
		} catch (Exception exception) {
			DebugLog.Shader.error("Failed reading shader code. fileName:" + string);
			exception.printStackTrace(DebugLog.Shader);
			return null;
		}

		return stringBuilder.toString();
	}

	private boolean processIncludeLine(String string, StringBuilder stringBuilder, String string2, String string3, ArrayList arrayList) {
		String string4 = string2.substring("#include ".length());
		if (string4.startsWith("\"") && string4.endsWith("\"")) {
			String string5 = this.getParentFolder(string);
			String string6 = string4.substring(1, string4.length() - 1);
			string6 = string6.trim();
			string6 = string6.replace('\\', '/');
			string6 = string6.toLowerCase();
			if (string6.contains(":")) {
				DebugLog.Shader.error(string + "> include cannot have \':\' characters. " + string4);
				return false;
			} else if (string6.startsWith("/")) {
				DebugLog.Shader.error(string + "> include cannot start with \'/\' or \'\\\' characters. " + string4);
				return false;
			} else {
				String string7 = string5 + "/" + string6;
				ArrayList arrayList2 = new ArrayList();
				String[] stringArray = string7.split("/");
				int int1 = stringArray.length;
				String string8;
				for (int int2 = 0; int2 < int1; ++int2) {
					string8 = stringArray[int2];
					if (!string8.equals(".") && !string8.isEmpty()) {
						if (StringUtils.isNullOrWhitespace(string8)) {
							DebugLog.Shader.error(string + "> include path cannot have whitespace-only folders. " + string4);
							return false;
						}

						if (string8.equals("..")) {
							if (arrayList2.isEmpty()) {
								DebugLog.Shader.error(string + "> include cannot go out of bounds with \'..\' parameters. " + string4);
								return false;
							}

							arrayList2.remove(arrayList2.size() - 1);
						} else {
							arrayList2.add(string8);
						}
					}
				}

				StringBuilder stringBuilder2 = new StringBuilder(string7.length());
				String string9;
				for (Iterator iterator = arrayList2.iterator(); iterator.hasNext(); stringBuilder2.append(string9)) {
					string9 = (String)iterator.next();
					if (stringBuilder2.length() > 0) {
						stringBuilder2.append('/');
					}
				}

				String string10 = stringBuilder2.toString();
				if (arrayList.contains(string10)) {
					stringBuilder.append("// Duplicate Include, skipped. ").append(string2).append(string3);
					return true;
				} else {
					arrayList.add(string10);
					string9 = string10 + ".h";
					string8 = this.preProcessShaderFile(string9, arrayList);
					stringBuilder.append(string3);
					stringBuilder.append("// Include begin ").append(string2).append(string3);
					stringBuilder.append(string8).append(string3);
					stringBuilder.append("// Include end   ").append(string2).append(string3);
					stringBuilder.append(string3);
					return true;
				}
			}
		} else {
			DebugLog.Shader.error(string + "> include needs to be in quotes: " + string4);
			return false;
		}
	}

	private String getParentFolder(String string) {
		int int1 = string.lastIndexOf("/");
		if (int1 > -1) {
			return string.substring(0, int1);
		} else {
			int1 = string.lastIndexOf("\\");
			return int1 > -1 ? string.substring(0, int1) : "";
		}
	}

	public static enum Type {

		Vert,
		Frag;

		private static ShaderUnit.Type[] $values() {
			return new ShaderUnit.Type[]{Vert, Frag};
		}
	}
}
