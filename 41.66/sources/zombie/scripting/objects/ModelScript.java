package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.joml.Vector3f;
import zombie.ZomboidFileSystem;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.core.skinnedmodel.model.Model;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class ModelScript extends BaseScriptObject {
	public static final String DEFAULT_SHADER_NAME = "basicEffect";
	public String fileName;
	public String name;
	public String meshName;
	public String textureName;
	public String shaderName;
	public boolean bStatic = true;
	public float scale = 1.0F;
	public final ArrayList m_attachments = new ArrayList();
	public boolean invertX = false;
	public Model loadedModel;
	public final ArrayList boneWeights = new ArrayList();
	private static final HashSet reported = new HashSet();

	public void Load(String string, String string2) {
		ScriptManager scriptManager = ScriptManager.instance;
		this.fileName = scriptManager.currentFileName;
		this.name = string;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.children.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
			if ("attachment".equals(block2.type)) {
				this.LoadAttachment(block2);
			}
		}

		iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String[] stringArray = value.string.split("=");
			String string3 = stringArray[0].trim();
			String string4 = stringArray[1].trim();
			if ("mesh".equalsIgnoreCase(string3)) {
				this.meshName = string4;
			} else if ("scale".equalsIgnoreCase(string3)) {
				this.scale = Float.parseFloat(string4);
			} else if ("shader".equalsIgnoreCase(string3)) {
				this.shaderName = string4;
			} else if ("static".equalsIgnoreCase(string3)) {
				this.bStatic = Boolean.parseBoolean(string4);
			} else if ("texture".equalsIgnoreCase(string3)) {
				this.textureName = string4;
			} else if ("invertX".equalsIgnoreCase(string3)) {
				this.invertX = Boolean.parseBoolean(string4);
			} else if ("boneWeight".equalsIgnoreCase(string3)) {
				String[] stringArray2 = string4.split("\\s+");
				if (stringArray2.length == 2) {
					AnimBoneWeight animBoneWeight = new AnimBoneWeight(stringArray2[0], PZMath.tryParseFloat(stringArray2[1], 1.0F));
					animBoneWeight.includeDescendants = false;
					this.boneWeights.add(animBoneWeight);
				}
			}
		}
	}

	private ModelAttachment LoadAttachment(ScriptParser.Block block) {
		ModelAttachment modelAttachment = this.getAttachmentById(block.id);
		if (modelAttachment == null) {
			modelAttachment = new ModelAttachment(block.id);
			this.m_attachments.add(modelAttachment);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("bone".equals(string)) {
				modelAttachment.setBone(string2);
			} else if ("offset".equals(string)) {
				this.LoadVector3f(string2, modelAttachment.getOffset());
			} else if ("rotate".equals(string)) {
				this.LoadVector3f(string2, modelAttachment.getRotate());
			}
		}

		return modelAttachment;
	}

	private void LoadVector3f(String string, Vector3f vector3f) {
		String[] stringArray = string.split(" ");
		vector3f.set(Float.parseFloat(stringArray[0]), Float.parseFloat(stringArray[1]), Float.parseFloat(stringArray[2]));
	}

	public String getName() {
		return this.name;
	}

	public String getFullType() {
		return this.module.name + "." + this.name;
	}

	public String getMeshName() {
		return this.meshName;
	}

	public String getTextureName() {
		return StringUtils.isNullOrWhitespace(this.textureName) ? this.meshName : this.textureName;
	}

	public String getTextureName(boolean boolean1) {
		return StringUtils.isNullOrWhitespace(this.textureName) && !boolean1 ? this.meshName : this.textureName;
	}

	public String getShaderName() {
		return StringUtils.isNullOrWhitespace(this.shaderName) ? "basicEffect" : this.shaderName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public int getAttachmentCount() {
		return this.m_attachments.size();
	}

	public ModelAttachment getAttachment(int int1) {
		return (ModelAttachment)this.m_attachments.get(int1);
	}

	public ModelAttachment getAttachmentById(String string) {
		for (int int1 = 0; int1 < this.m_attachments.size(); ++int1) {
			ModelAttachment modelAttachment = (ModelAttachment)this.m_attachments.get(int1);
			if (modelAttachment.getId().equals(string)) {
				return modelAttachment;
			}
		}

		return null;
	}

	public ModelAttachment addAttachment(ModelAttachment modelAttachment) {
		this.m_attachments.add(modelAttachment);
		return modelAttachment;
	}

	public ModelAttachment removeAttachment(ModelAttachment modelAttachment) {
		this.m_attachments.remove(modelAttachment);
		return modelAttachment;
	}

	public ModelAttachment addAttachmentAt(int int1, ModelAttachment modelAttachment) {
		this.m_attachments.add(int1, modelAttachment);
		return modelAttachment;
	}

	public ModelAttachment removeAttachment(int int1) {
		return (ModelAttachment)this.m_attachments.remove(int1);
	}

	public void reset() {
		this.invertX = false;
		this.name = null;
		this.meshName = null;
		this.textureName = null;
		this.shaderName = null;
		this.bStatic = true;
		this.scale = 1.0F;
		this.boneWeights.clear();
	}

	private static void checkMesh(String string, String string2) {
		if (!StringUtils.isNullOrWhitespace(string2)) {
			String string3 = string2.toLowerCase(Locale.ENGLISH);
			if (!ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/models_x/" + string3 + ".fbx") && !ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/models_x/" + string3 + ".x") && !ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/models/" + string3 + ".txt")) {
				reported.add(string2);
				DebugLog.Script.warn("no such mesh \"" + string2 + "\" for " + string);
			}
		}
	}

	private static void checkTexture(String string, String string2) {
		if (!GameServer.bServer) {
			if (!StringUtils.isNullOrWhitespace(string2)) {
				String string3 = string2.toLowerCase(Locale.ENGLISH);
				if (!ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/textures/" + string3 + ".png")) {
					reported.add(string2);
					DebugLog.Script.warn("no such texture \"" + string2 + "\" for " + string);
				}
			}
		}
	}

	private static void check(String string, String string2) {
		if (!StringUtils.isNullOrWhitespace(string2)) {
			if (!reported.contains(string2)) {
				ModelScript modelScript = ScriptManager.instance.getModelScript(string2);
				if (modelScript == null) {
					reported.add(string2);
					DebugLog.Script.warn("no such model \"" + string2 + "\" for " + string);
				} else {
					checkMesh(modelScript.getFullType(), modelScript.getMeshName());
					checkTexture(modelScript.getFullType(), modelScript.getTextureName());
				}
			}
		}
	}

	public static void ScriptsLoaded() {
		reported.clear();
		ArrayList arrayList = ScriptManager.instance.getAllItems();
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			check(item.getFullName(), item.getStaticModel());
			check(item.getFullName(), item.getWeaponSprite());
		}

		ArrayList arrayList2 = ScriptManager.instance.getAllRecipes();
		Iterator iterator2 = arrayList2.iterator();
		while (iterator2.hasNext()) {
			Recipe recipe = (Recipe)iterator2.next();
			if (recipe.getProp1() != null && !recipe.getProp1().startsWith("Source=")) {
				check(recipe.getFullType(), recipe.getProp1());
			}

			if (recipe.getProp2() != null && !recipe.getProp2().startsWith("Source=")) {
				check(recipe.getFullType(), recipe.getProp2());
			}
		}
	}
}
