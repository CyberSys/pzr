package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import java.util.List;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.FileTask_ParseXML;
import zombie.fileSystem.FileSystem;
import zombie.util.list.PZArrayUtil;


public class ClothingItemAssetManager extends AssetManager {
	public static final ClothingItemAssetManager instance = new ClothingItemAssetManager();

	protected void startLoading(Asset asset) {
		FileSystem fileSystem = asset.getAssetManager().getOwner().getFileSystem();
		FileTask_ParseXML fileTask_ParseXML = new FileTask_ParseXML(ClothingItemXML.class, asset.getPath().getPath(), (fileSystemx)->{
    this.onFileTaskFinished((ClothingItem)asset, fileSystemx);
}, fileSystem);
		AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_ParseXML, asset);
		this.setTask(asset, assetTask_RunFileTask);
		assetTask_RunFileTask.execute();
	}

	private void onFileTaskFinished(ClothingItem clothingItem, Object object) {
		if (object instanceof ClothingItemXML) {
			ClothingItemXML clothingItemXML = (ClothingItemXML)object;
			clothingItem.m_MaleModel = this.fixPath(clothingItemXML.m_MaleModel);
			clothingItem.m_FemaleModel = this.fixPath(clothingItemXML.m_FemaleModel);
			clothingItem.m_Static = clothingItemXML.m_Static;
			PZArrayUtil.arrayCopy((List)clothingItem.m_BaseTextures, (List)this.fixPaths(clothingItemXML.m_BaseTextures));
			clothingItem.m_AttachBone = clothingItemXML.m_AttachBone;
			PZArrayUtil.arrayCopy((List)clothingItem.m_Masks, (List)clothingItemXML.m_Masks);
			clothingItem.m_MasksFolder = this.fixPath(clothingItemXML.m_MasksFolder);
			clothingItem.m_UnderlayMasksFolder = this.fixPath(clothingItemXML.m_UnderlayMasksFolder);
			PZArrayUtil.arrayCopy((List)clothingItem.textureChoices, (List)this.fixPaths(clothingItemXML.textureChoices));
			clothingItem.m_AllowRandomHue = clothingItemXML.m_AllowRandomHue;
			clothingItem.m_AllowRandomTint = clothingItemXML.m_AllowRandomTint;
			clothingItem.m_DecalGroup = clothingItemXML.m_DecalGroup;
			clothingItem.m_Shader = clothingItemXML.m_Shader;
			clothingItem.m_HatCategory = clothingItemXML.m_HatCategory;
			this.onLoadingSucceeded(clothingItem);
		} else {
			this.onLoadingFailed(clothingItem);
		}
	}

	private String fixPath(String string) {
		return string == null ? null : string.replaceAll("\\\\", "/");
	}

	private ArrayList fixPaths(ArrayList arrayList) {
		if (arrayList == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				arrayList.set(int1, this.fixPath((String)arrayList.get(int1)));
			}

			return arrayList;
		}
	}

	public void onStateChanged(Asset.State state, Asset.State state2, Asset asset) {
		super.onStateChanged(state, state2, asset);
		if (state2 == Asset.State.READY) {
			OutfitManager.instance.onClothingItemStateChanged((ClothingItem)asset);
		}
	}

	protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
		return new ClothingItem(assetPath, this);
	}

	protected void destroyAsset(Asset asset) {
	}
}
