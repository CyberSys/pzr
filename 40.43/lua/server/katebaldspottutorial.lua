require "SurvivalGuide/ISTutorialPanel"

-- Tutorial panel pages for the kate / baldspot story. Used KateBaldspotTutorial table to avoid naming conflicts with other lua scripts.
KateBaldspotTutorial = {}

KateBaldspotTutorial.TutorialWardrobe = {x=44, y=22, z=1 }
KateBaldspotTutorial.TutorialPills = {x=46, y=22, z=1 }

KateBaldspotTutorial.prepareMap = function ()

	-- get the various containers we need...

	TutorialHelperFunctions.replaceInContainer(KateBaldspotTutorial.TutorialWardrobe.x,
		KateBaldspotTutorial.TutorialWardrobe.y,
		KateBaldspotTutorial.TutorialWardrobe.z,
		"wardrobe",
		{"Base.Pillow", "Base.Sheet"})

	TutorialHelperFunctions.replaceInContainer(KateBaldspotTutorial.TutorialPills.x,
		KateBaldspotTutorial.TutorialPills.y,
		KateBaldspotTutorial.TutorialPills.z,
		"medicine",
		{"Base.Pills"})



end

-- checks if the hammer is equipped in the character's primary hand.
KateBaldspotTutorial.TestEquipHammer = function ()

	local player = getPlayer();

	if(player == nil) then
		return false;
	end

	if( player:isPrimaryEquipped("Hammer") ) then
		return true;
	end

	return false;
end

-- Actual method that defines tutorial page content and next page conditions.
KateBaldspotTutorial.Create = function (tutPanel)
	tutPanel.tutorialSetInfo = ISTutorialSetInfo:new();

	-- Add barricading tutorial bits.
	tutPanel.tutorialSetInfo:addPage("Barricading",
		"To barricade a door you need to equip the hammer from your inventory. To do this simply click on the hammer: <IMAGE:media/Item_Hammer.png> in your inventory and then click it on your primary hand slot: <IMAGE:media/ui/HandMain_Off.png> to equip it.", KateBaldspotTutorial.TestEquipHammer);
	tutPanel.tutorialSetInfo:addPage("Barricading",
		"Once the hammer is equipped, click on the planks of wood: <IMAGE:media/Item_Plank.png> in your inventory and click them on the door or window you want to barricade.", nil);
	tutPanel.tutorialSetInfo:applyPageToRichTextPanel(tutPanel.richtext);
end

-- Event method to check game mode and create K&B tutorial panel if necessary.
KateBaldspotTutorial.TestCreateTutorial = function ()

	local mode = getCore():getGameMode();
	if mode ~= "KateBaldspot" then
		return;
	end
	KateBaldspotTutorial.prepareMap();
	local panel2 = ISTutorialPanel:new(0, 0, 300, 250);
	panel2:initialise();
	panel2:addToUIManager();
	KateBaldspotTutorial.Create(panel2);

end

Events.OnCreateUI.Add(KateBaldspotTutorial.TestCreateTutorial);
