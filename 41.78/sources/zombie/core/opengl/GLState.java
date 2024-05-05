package zombie.core.opengl;

import zombie.core.SpriteRenderer;
import zombie.util.Type;


public final class GLState {
	public static final GLState.CAlphaFunc AlphaFunc = new GLState.CAlphaFunc();
	public static final GLState.CAlphaTest AlphaTest = new GLState.CAlphaTest();
	public static final GLState.CBlendFunc BlendFunc = new GLState.CBlendFunc();
	public static final GLState.CBlendFuncSeparate BlendFuncSeparate = new GLState.CBlendFuncSeparate();
	public static final GLState.CColorMask ColorMask = new GLState.CColorMask();
	public static final GLState.CStencilFunc StencilFunc = new GLState.CStencilFunc();
	public static final GLState.CStencilMask StencilMask = new GLState.CStencilMask();
	public static final GLState.CStencilOp StencilOp = new GLState.CStencilOp();
	public static final GLState.CStencilTest StencilTest = new GLState.CStencilTest();

	public static void startFrame() {
		AlphaFunc.setDirty();
		AlphaTest.setDirty();
		BlendFunc.setDirty();
		BlendFuncSeparate.setDirty();
		ColorMask.setDirty();
		StencilFunc.setDirty();
		StencilMask.setDirty();
		StencilOp.setDirty();
		StencilTest.setDirty();
	}

	public static final class CAlphaFunc extends GLState.BaseIntFloat {

		void Set(GLState.CIntFloatValue cIntFloatValue) {
			SpriteRenderer.instance.glAlphaFunc(cIntFloatValue.a, cIntFloatValue.b);
		}
	}

	public static final class CAlphaTest extends GLState.BaseBoolean {

		void Set(GLState.CBooleanValue cBooleanValue) {
			if (cBooleanValue.value) {
				SpriteRenderer.instance.glEnable(3008);
			} else {
				SpriteRenderer.instance.glDisable(3008);
			}
		}
	}

	public static final class CBlendFunc extends GLState.Base2Ints {

		void Set(GLState.C2IntsValue c2IntsValue) {
			SpriteRenderer.instance.glBlendFunc(c2IntsValue.a, c2IntsValue.b);
		}
	}

	public static final class CBlendFuncSeparate extends GLState.Base4Ints {

		void Set(GLState.C4IntsValue c4IntsValue) {
			SpriteRenderer.instance.glBlendFuncSeparate(c4IntsValue.a, c4IntsValue.b, c4IntsValue.c, c4IntsValue.d);
		}
	}

	public static final class CColorMask extends GLState.Base4Booleans {

		void Set(GLState.C4BooleansValue c4BooleansValue) {
			SpriteRenderer.instance.glColorMask(c4BooleansValue.a ? 1 : 0, c4BooleansValue.b ? 1 : 0, c4BooleansValue.c ? 1 : 0, c4BooleansValue.d ? 1 : 0);
		}
	}

	public static final class CStencilFunc extends GLState.Base3Ints {

		void Set(GLState.C3IntsValue c3IntsValue) {
			SpriteRenderer.instance.glStencilFunc(c3IntsValue.a, c3IntsValue.b, c3IntsValue.c);
		}
	}

	public static final class CStencilMask extends GLState.BaseInt {

		void Set(GLState.CIntValue cIntValue) {
			SpriteRenderer.instance.glStencilMask(cIntValue.value);
		}
	}

	public static final class CStencilOp extends GLState.Base3Ints {

		void Set(GLState.C3IntsValue c3IntsValue) {
			SpriteRenderer.instance.glStencilOp(c3IntsValue.a, c3IntsValue.b, c3IntsValue.c);
		}
	}

	public static final class CStencilTest extends GLState.BaseBoolean {

		void Set(GLState.CBooleanValue cBooleanValue) {
			if (cBooleanValue.value) {
				SpriteRenderer.instance.glEnable(2960);
			} else {
				SpriteRenderer.instance.glDisable(2960);
			}
		}
	}

	public abstract static class Base4Ints extends IOpenGLState {

		GLState.C4IntsValue defaultValue() {
			return new GLState.C4IntsValue();
		}
	}

	public abstract static class Base3Ints extends IOpenGLState {

		GLState.C3IntsValue defaultValue() {
			return new GLState.C3IntsValue();
		}
	}

	public abstract static class Base2Ints extends IOpenGLState {

		GLState.C2IntsValue defaultValue() {
			return new GLState.C2IntsValue();
		}
	}

	public abstract static class BaseInt extends IOpenGLState {

		GLState.CIntValue defaultValue() {
			return new GLState.CIntValue();
		}
	}

	public abstract static class BaseIntFloat extends IOpenGLState {

		GLState.CIntFloatValue defaultValue() {
			return new GLState.CIntFloatValue();
		}
	}

	public abstract static class Base4Booleans extends IOpenGLState {

		GLState.C4BooleansValue defaultValue() {
			return new GLState.C4BooleansValue();
		}
	}

	public abstract static class BaseBoolean extends IOpenGLState {

		GLState.CBooleanValue defaultValue() {
			return new GLState.CBooleanValue(true);
		}
	}

	public static final class CIntFloatValue implements IOpenGLState.Value {
		int a;
		float b;

		public GLState.CIntFloatValue set(int int1, float float1) {
			this.a = int1;
			this.b = float1;
			return this;
		}

		public boolean equals(Object object) {
			GLState.CIntFloatValue cIntFloatValue = (GLState.CIntFloatValue)Type.tryCastTo(object, GLState.CIntFloatValue.class);
			return cIntFloatValue != null && cIntFloatValue.a == this.a && cIntFloatValue.b == this.b;
		}

		public IOpenGLState.Value set(IOpenGLState.Value value) {
			GLState.CIntFloatValue cIntFloatValue = (GLState.CIntFloatValue)value;
			this.a = cIntFloatValue.a;
			this.b = cIntFloatValue.b;
			return this;
		}
	}

	public static final class C4IntsValue implements IOpenGLState.Value {
		int a;
		int b;
		int c;
		int d;

		public GLState.C4IntsValue set(int int1, int int2, int int3, int int4) {
			this.a = int1;
			this.b = int2;
			this.c = int3;
			this.d = int4;
			return this;
		}

		public boolean equals(Object object) {
			GLState.C4IntsValue c4IntsValue = (GLState.C4IntsValue)Type.tryCastTo(object, GLState.C4IntsValue.class);
			return c4IntsValue != null && c4IntsValue.a == this.a && c4IntsValue.b == this.b && c4IntsValue.c == this.c && c4IntsValue.d == this.d;
		}

		public IOpenGLState.Value set(IOpenGLState.Value value) {
			GLState.C4IntsValue c4IntsValue = (GLState.C4IntsValue)value;
			this.a = c4IntsValue.a;
			this.b = c4IntsValue.b;
			this.c = c4IntsValue.c;
			this.d = c4IntsValue.d;
			return this;
		}
	}

	public static final class C3IntsValue implements IOpenGLState.Value {
		int a;
		int b;
		int c;

		public GLState.C3IntsValue set(int int1, int int2, int int3) {
			this.a = int1;
			this.b = int2;
			this.c = int3;
			return this;
		}

		public boolean equals(Object object) {
			GLState.C3IntsValue c3IntsValue = (GLState.C3IntsValue)Type.tryCastTo(object, GLState.C3IntsValue.class);
			return c3IntsValue != null && c3IntsValue.a == this.a && c3IntsValue.b == this.b && c3IntsValue.c == this.c;
		}

		public IOpenGLState.Value set(IOpenGLState.Value value) {
			GLState.C3IntsValue c3IntsValue = (GLState.C3IntsValue)value;
			this.a = c3IntsValue.a;
			this.b = c3IntsValue.b;
			this.c = c3IntsValue.c;
			return this;
		}
	}

	public static final class C2IntsValue implements IOpenGLState.Value {
		int a;
		int b;

		public GLState.C2IntsValue set(int int1, int int2) {
			this.a = int1;
			this.b = int2;
			return this;
		}

		public boolean equals(Object object) {
			GLState.C2IntsValue c2IntsValue = (GLState.C2IntsValue)Type.tryCastTo(object, GLState.C2IntsValue.class);
			return c2IntsValue != null && c2IntsValue.a == this.a && c2IntsValue.b == this.b;
		}

		public IOpenGLState.Value set(IOpenGLState.Value value) {
			GLState.C2IntsValue c2IntsValue = (GLState.C2IntsValue)value;
			this.a = c2IntsValue.a;
			this.b = c2IntsValue.b;
			return this;
		}
	}

	public static class CIntValue implements IOpenGLState.Value {
		int value;

		public GLState.CIntValue set(int int1) {
			this.value = int1;
			return this;
		}

		public boolean equals(Object object) {
			return object instanceof GLState.CIntValue && ((GLState.CIntValue)object).value == this.value;
		}

		public IOpenGLState.Value set(IOpenGLState.Value value) {
			this.value = ((GLState.CIntValue)value).value;
			return this;
		}
	}

	public static final class C4BooleansValue implements IOpenGLState.Value {
		boolean a;
		boolean b;
		boolean c;
		boolean d;

		public GLState.C4BooleansValue set(boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
			this.a = boolean1;
			this.b = boolean2;
			this.c = boolean3;
			this.d = boolean4;
			return this;
		}

		public boolean equals(Object object) {
			GLState.C4BooleansValue c4BooleansValue = (GLState.C4BooleansValue)Type.tryCastTo(object, GLState.C4BooleansValue.class);
			return c4BooleansValue != null && c4BooleansValue.a == this.a && c4BooleansValue.b == this.b && c4BooleansValue.c == this.c;
		}

		public IOpenGLState.Value set(IOpenGLState.Value value) {
			GLState.C4BooleansValue c4BooleansValue = (GLState.C4BooleansValue)value;
			this.a = c4BooleansValue.a;
			this.b = c4BooleansValue.b;
			this.c = c4BooleansValue.c;
			this.d = c4BooleansValue.d;
			return this;
		}
	}

	public static class CBooleanValue implements IOpenGLState.Value {
		public static final GLState.CBooleanValue TRUE = new GLState.CBooleanValue(true);
		public static final GLState.CBooleanValue FALSE = new GLState.CBooleanValue(false);
		boolean value;

		CBooleanValue(boolean boolean1) {
			this.value = boolean1;
		}

		public boolean equals(Object object) {
			return object instanceof GLState.CBooleanValue && ((GLState.CBooleanValue)object).value == this.value;
		}

		public IOpenGLState.Value set(IOpenGLState.Value value) {
			this.value = ((GLState.CBooleanValue)value).value;
			return this;
		}
	}
}
