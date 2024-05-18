if isDemo() then return end

require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

LoadGameScreen = ISPanelJoypad:derive("LoadGameScreen");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_LARGE = getTextManager():getFontHeight(UIFont.Large)

-- -- -- -- --
-- -- -- -- --
-- -- -- -- --

SaveInfoPanel = ISPanelJoypad:derive("SaveInfoPanel")

function SaveInfoPanel:createChildren()
    self.richText = ISRichTextPanel:new(0, 10, 200, 200)
    self.richText:initialise()
    self.richText:instantiate()
    self.richText.background = false
    self.richText.marginLeft = 10
    self.richText:setAnchorBottom(true)
    self.richText:setAnchorRight(true)
    self.richText:setVisible(false)
    self.richText:setScrollWithParent(true)
    self:addChild(self.richText)
end

function SaveInfoPanel:prerender()
	self:setStencilRect(0, 0, self.width, self.height)

	local selectedItem = self.parent.listbox.items[self.parent.listbox.selected]

	local thumbHeight = 256
	if selectedItem.thumb == nil and selectedItem.thumbMissing ~= true then
		-- FIXME: should unload these after choosing a savegame
		selectedItem.thumb = getTextureFromSaveDir("thumb.png", selectedItem.text)
		if selectedItem.thumb == nil then
			selectedItem.thumbMissing = true
		end
	end

	if selectedItem.thumb then
		thumbHeight = selectedItem.thumb:getHeight()
		local BreakPoint = (self.width - thumbHeight) / 2
		self:drawTexture(selectedItem.thumb, BreakPoint, 10, 1, 1, 1, 1)
	end
	local descRectWidth = self.width
	local descRectHeight = self.height

	-- ISRichTextPanel.drawMargins = true

	self.richText:setX(0)
	self.richText:setY(thumbHeight + 10 + self:getYScroll())
	self.richText:setWidth(self.width - 17)
	self.richText:setVisible(true)

	local text = " <H1> " .. selectedItem.item.saveName .. " <LINE> <LINE> <H2> "
	local mode = getTextOrNull("IGUI_Gametime_" .. selectedItem.item.gameMode)
	if not mode then mode = selectedItem.item.gameMode end
	text = text .. getText("IGUI_Gametime_GameMode", mode) .. " <LINE> "
	text = text .. selectedItem.item.lastPlayed .. " <LINE> "
	local mapName = selectedItem.item.mapName
	if string.len(mapName) > 128 then
		mapName = string.sub(mapName, 1, 128) .. "..."
	end
	text = text .. getText("UI_Map") .. mapName .. " <LINE> "
	text = text .. getText("UI_WorldVersion") ..selectedItem.item.worldVersion .. " <LINE> "
	if not selectedItem.item.mapInfo then
		text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_MapNotFound") .. " <RGB:1,1,1> <H2> <LINE> "
	else
		if tonumber(selectedItem.item.worldVersion) then
			if selectedItem.item.worldVersion == 0 then
				text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_SavefileCorrupt") .. " <RGB:1,1,1> <H2> <LINE> "
			elseif selectedItem.item.worldVersion <= 23 then
				text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_SavefileOld") .. " <RGB:1,1,1> <H2> <LINE> "
			elseif selectedItem.item.worldVersion <= 115 then
				text = text .. " <LINE> <CENTER> <H1> <RED> " .. getText("UI_worldscreen_SaveCannotBeLoaded") .. " <LINE> <TEXT> <RED> " .. getText("UI_worldscreen_SavefileVehicle",selectedItem.item.worldVersion, IsoWorld.getWorldVersion()) .. " <RGB:1,1,1> <H2> <LINE> "
			elseif selectedItem.item.worldVersion > IsoWorld.getWorldVersion() then
				text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_SavefileNewerThanGame") .. " <RGB:1,1,1> <H2> <LINE> "
			end
		end
	end
	self.richText.text = text
	self.richText:paginate()

	
	self:setScrollHeight(thumbHeight + 10 + self.richText:getHeight())
end

function SaveInfoPanel:render()
	self:clearStencilRect()

	if self.joyfocus then
		self:drawRectBorder(0, -self:getYScroll(), self:getWidth(), self:getHeight(), 0.4, 0.2, 1.0, 1.0);
		self:drawRectBorder(1, 1-self:getYScroll(), self:getWidth()-2, self:getHeight()-2, 0.4, 0.2, 1.0, 1.0);
	else
		self:drawRectBorderStatic(0, 0, self.width, self.height, 0.3, 1, 1, 1)
	end
end

function SaveInfoPanel:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del * 18))
	return true
end

function SaveInfoPanel:onJoypadDirUp(joypadData)
	self:setYScroll(self:getYScroll() + 48)
end

function SaveInfoPanel:onJoypadDirDown(joypadData)
	self:setYScroll(self:getYScroll() - 48)
end

-- -- -- -- --
-- -- -- -- --
-- -- -- -- --

function LoadGameScreen:initialise()
	ISPanel.initialise(self);
end


	--************************************************************************--
	--** ISPanel:instantiate
	--**
	--************************************************************************--
function LoadGameScreen:instantiate()

	--self:initialise();
	self.javaObject = UIElement.new(self);
	self.javaObject:setX(self.x);
	self.javaObject:setY(self.y);
	self.javaObject:setHeight(self.height);
	self.javaObject:setWidth(self.width);
	self.javaObject:setAnchorLeft(self.anchorLeft);
	self.javaObject:setAnchorRight(self.anchorRight);
	self.javaObject:setAnchorTop(self.anchorTop);
	self.javaObject:setAnchorBottom(self.anchorBottom);



end

function LoadGameScreen:setSaveGamesList()
	self.listbox:clear();
	local dirs = getFullSaveDirectoryTable();
	for i, k in ipairs(dirs) do
		local newSave = {};
		-- we create an item with somes info, like the last time we played that save
		local info = getSaveInfo(k)
		newSave.lastPlayed = getLastPlayedDate(k);
		newSave.worldVersion = info.worldVersion or 'unknown'
		newSave.mapName = info.mapName or 'Muldraugh, KY'
		newSave.mapInfo = MainScreen.checkMapsAvailable(newSave.mapName)
        newSave.saveName = info.saveName;
        newSave.gameMode = info.gameMode;
        newSave.playerAlive = info.playerAlive
		self.listbox:addItem(k, newSave);--{age = self:getWorldAge(k)});
	end
end

function LoadGameScreen:hasChoices()
	return #self.listbox.items > 0
end

function LoadGameScreen:create()
	local buttonHgt = math.max(25, FONT_HGT_SMALL + 3 * 2)

	self.listbox = ISScrollingListBox:new(16, self.startY, self.width / 2, self.height-120-6);
	self.listbox:initialise();
	self.listbox:instantiate();
	self.listbox:setAnchorLeft(true);
--	self.listbox:setAnchorRight(true);
	self.listbox:setAnchorTop(true);
	self.listbox:setAnchorBottom(true);
	self.listbox.itemheight = FONT_HGT_SMALL + FONT_HGT_LARGE + 10;
	self.listbox.doDrawItem = LoadGameScreen.drawMap;
	self.listbox:setOnMouseDoubleClick(self, LoadGameScreen.onDblClickWorld);
    self.listbox:setOnMouseDownFunction(self, LoadGameScreen.onClickWorld);
	self.listbox.onGainJoypadFocus = self.onGainJoypadFocus_child
	self.listbox.onLoseJoypadFocus = self.onLoseJoypadFocus_child
	self.listbox.onJoypadDirRight = self.onJoypadDirRight_child
	self:addChild(self.listbox);

	local x = self.listbox:getRight() + 16
	self.infoPanel = SaveInfoPanel:new(x, self.startY, self.width - 16 - x, self.height-120-6)
	self.infoPanel.onGainJoypadFocus = self.onGainJoypadFocus_child
	self.infoPanel.onJoypadDirLeft = self.onJoypadDirLeft_child
	self.infoPanel:initialise()
	self.infoPanel:instantiate()
	self.infoPanel:setAnchorLeft(false)
	self.infoPanel:setAnchorRight(false)
	self.infoPanel:setAnchorTop(true)
	self.infoPanel:setAnchorBottom(true)
	self.infoPanel:addScrollBars()
	self:addChild(self.infoPanel)

	self.backButton = ISButton:new(16, self.height - buttonHgt - 5, 100, buttonHgt, getText("UI_btn_back"), self, LoadGameScreen.onOptionMouseDown);
	self.backButton.internal = "BACK";
	self.backButton:initialise();
	self.backButton:instantiate();
	self.backButton:setAnchorLeft(true);
	self.backButton:setAnchorTop(false);
	self.backButton:setAnchorBottom(true);
	self.backButton.borderColor = {r=1, g=1, b=1, a=0.1};

	self.backButton:setFont(UIFont.Small);
	self.backButton:ignoreWidthChange();
	self.backButton:ignoreHeightChange();
	self:addChild(self.backButton);

	self.deleteButton = ISButton:new(self.width - 116, self.height - buttonHgt - 5, 100, buttonHgt, getText("UI_btn_delete"), self, LoadGameScreen.onOptionMouseDown);
	self.deleteButton.internal = "DELETE";
	self.deleteButton:initialise();
	self.deleteButton:instantiate();
	self.deleteButton:setAnchorLeft(false);
	self.deleteButton:setAnchorRight(true);
	self.deleteButton:setAnchorTop(false);
	self.deleteButton:setAnchorBottom(true);
	self.deleteButton.borderColor = {r=1, g=1, b=1, a=0.1};

	self.deleteButton:setFont(UIFont.Small);
	self.deleteButton:ignoreWidthChange();
	self.deleteButton:ignoreHeightChange();
	self:addChild(self.deleteButton);

	self.playButton = ISButton:new(self.deleteButton.x - 110, self.height - buttonHgt - 5, 100, buttonHgt, getText("UI_btn_play"), self, LoadGameScreen.onOptionMouseDown);
	self.playButton.internal = "PLAY";
	self.playButton:initialise();
	self.playButton:instantiate();
	self.playButton:setAnchorLeft(false);
	self.playButton:setAnchorRight(true);
	self.playButton:setAnchorTop(false);
	self.playButton:setAnchorBottom(true);
	self.playButton.borderColor = {r=1, g=1, b=1, a=0.1};
	self:addChild(self.playButton);

    self.richText = ISRichTextPanel:new(16, 10, 500,200);
    self.richText:initialise();
    self.richText.background = false;
    self.richText:setAnchorBottom(true);
    self.richText:setAnchorRight(true);
    self.richText:setVisible(false);
    self:addChild(self.richText);

	self:setVisible(false);
end

function LoadGameScreen:drawMap(y, item, alt)
	-- self == listbox item
	--if self.worldimage == nil then
	--	self.worldimage = getTexture("media/maps/"..item.text.."/thumb.png");
	--end
	local isMouseOver = self.mouseoverselected == item.index and not self:isMouseOverScrollBar()
	if self.selected == item.index then
		self:drawRect(0, (y), self:getWidth(), self.itemheight-1, 0.3, 0.7, 0.35, 0.15);
	elseif isMouseOver then
		self:drawRect(1, y + 1, self:getWidth() - 2, item.height - 2, 0.95, 0.05, 0.05, 0.05);
    end

	local x = 0;
--	if item.thumb == nil and item.thumbMissing ~= true then
--		-- FIXME: should unload these after choosing a savegame
--		item.thumb = getTextureFromSaveDir("thumb.png", item.text);
--		if item.thumb == nil then
--			item.thumbMissing = true;
--		end
--	end
--	if(item.thumb ~= nil) then
--		self:drawTextureScaled(item.thumb, 16, y+16, 128, 128, 1, 1, 1, 1);
--		self:drawRectBorder( 16, y+16, 128, 128, 0.3, 1, 1, 1);
--		self:drawRectBorder(0, (y), self:getWidth(), self.itemheight-1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);
--		x = 16 + 128 + (160-128);
--	else
--		self:drawRect( 16, y+16, 128, 128, 1, 0, 0, 0);
--		self:drawRectBorder( 16, y+16, 128, 128, 0.3, 1, 1, 1);
--		self:drawRectBorder(0, (y), self:getWidth(), self.itemheight-1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);
--
--	end

    local mode = getTextOrNull("IGUI_Gametime_" .. item.item.gameMode)
    if not mode then mode = item.item.gameMode end
    self:drawText(mode, 20, (y)+5, 0.9, 0.9, 0.9, 0.9, UIFont.NewSmall);
	self:drawText(item.item.saveName, 20, (y)+5+FONT_HGT_SMALL, 0.9, 0.9, 0.9, 0.9, UIFont.NewLarge);
    self:drawRectBorder(0, (y), self:getWidth(), self.itemheight-1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);

--	local heightLarge = getTextManager():getFontFromEnum(UIFont.Large):getLineHeight()
--	self:drawText(getText("UI_WorldVersion") ..item.item.worldVersion, 160, (y)+15+heightLarge+2, 0.7, 0.7, 0.7, 0.7, UIFont.Small);
--	local heightSmall = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
--	self:drawText(getText("UI_Map")..item.item.mapName, 160, (y)+15+heightLarge+2+heightSmall, 0.7, 0.7, 0.7, 0.7, UIFont.Small);
--	if not item.item.mapInfo then
--		self:drawText("This map can't be found.  Check that the needed mod is loaded.", 160, (y)+15+heightLarge+2+heightSmall+heightSmall, 0.7, 0.0, 0.0, 1.0, UIFont.Small);
--	else
--		if tonumber(item.item.worldVersion) then
--			if item.item.worldVersion == 0 then
--				self:drawText("Savefile appears to be corrupt. World Version is zero.", 160, (y)+15+heightLarge+2+heightSmall+heightSmall, 0.7, 0.0, 0.0, 1.0, UIFont.Small);
--			elseif item.item.worldVersion <= 23 then
--				self:drawText("This savefile is too old to be loaded.", 160, (y)+15+heightLarge+2+heightSmall+heightSmall, 0.7, 0.0, 0.0, 1.0, UIFont.Small);
--			end
--		end
--	end
--
--	local scrollbarWidth = 17
--	self:drawTextRight(item.item.lastPlayed, self:getWidth() - 10 - scrollbarWidth, (y)+self.itemheight-20, 0.7, 0.7, 0.7, 0.7, UIFont.Small);
	--self:drawTexture(self.worldimage, 16, y+16, 1, 1, 1, 1);

	if not item.item.playerAlive and self.parent.deadTexture then
		local tex = self.parent.deadTexture
		local dy = (self.itemheight - tex:getHeightOrig()) / 2
		self:drawTexture(tex, self:getWidth() - 24 - tex:getWidthOrig(), y + dy, 1, 1, 1, 1)
	end

	self.itemheightoverride[item.text] = self.itemheight;

	y = y + self.itemheightoverride[item.text];

	return y;
end

function LoadGameScreen:render()
	self.deleteButton:setVisible(false);
    self.playButton:setVisible(false);
    if self.listbox.items[self.listbox.selected] then
        self.playButton:setVisible(true);
        self.deleteButton:setVisible(true);
    end
    --[[
    
--    self.listbox:setHeight(#self.listbox.items * self.listbox.itemheight)
    local selectedItem = self.listbox.items[self.listbox.selected];
--    local BreakPoint = ((self.width/4)*3) - selectedItem.spiffo:getHeight()/2;

    local thumbHeight = 256;
    if selectedItem.thumb == nil and selectedItem.thumbMissing ~= true then
        -- FIXME: should unload these after choosing a savegame
        selectedItem.thumb = getTextureFromSaveDir("thumb.png", selectedItem.text);
        if selectedItem.thumb == nil then
            selectedItem.thumbMissing = true;
        end
    end

    if selectedItem.thumb then
        thumbHeight = selectedItem.thumb:getHeight();
        local BreakPoint = ((self.width/4)*3) - selectedItem.thumb:getHeight()/2;
        self:drawTexture(selectedItem.thumb, BreakPoint, self.startY, 1, 1, 1, 1);
--        self:drawRectBorder( BreakPoint, self.startY, selectedItem.thumb:getWidth(), selectedItem.thumb:getHeight(), 0.3, 1, 1, 1);
    end
    local descRectWidth = self.width - 37 - (self.width/2 + 30)
    local descRectHeight = self.height - 200 - (self.startY + thumbHeight + 10)

    self.richText:setX(self.width/2 + 30)
    self.richText:setY(self.startY + 256 + 10)
    self.richText:setWidth(descRectWidth)
    self.richText:setHeight(descRectHeight)
    self.richText:setVisible(true);
    local text = " <H1> " .. selectedItem.item.saveName .. " <LINE> <LINE> <H2> ";
    local mode = getTextOrNull("IGUI_Gametime_" .. selectedItem.item.gameMode)
    if not mode then mode = selectedItem.item.gameMode end
    text = text .. getText("IGUI_Gametime_GameMode", mode) .. " <LINE> ";
    text = text .. selectedItem.item.lastPlayed .. " <LINE> ";
    local mapName = selectedItem.item.mapName
    if string.len(mapName) > 128 then
        mapName = string.sub(mapName, 1, 128) .. "..."
    end
    text = text .. getText("UI_Map") .. mapName .. " <LINE> ";
    text = text .. getText("UI_WorldVersion") ..selectedItem.item.worldVersion .. " <LINE> ";
    if not selectedItem.item.mapInfo then
        text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_MapNotFound") .. " <RGB:1,1,1> <H2> <LINE> ";
    else
        if tonumber(selectedItem.item.worldVersion) then
            if selectedItem.item.worldVersion == 0 then
                text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_SavefileCorrupt") .. " <RGB:1,1,1> <H2> <LINE> ";
            elseif selectedItem.item.worldVersion <= 23 then
                text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_SavefileOld") .. " <RGB:1,1,1> <H2> <LINE> ";
			elseif selectedItem.item.worldVersion <= 115 then
				text = text .. " <LINE> <CENTER> <H1> <RED> " .. getText("UI_worldscreen_SaveCannotBeLoaded") .. " <LINE> <TEXT> <RED> " .. getText("UI_worldscreen_SavefileVehicle",selectedItem.item.worldVersion, IsoWorld.getWorldVersion()) .. " <RGB:1,1,1> <H2> <LINE> ";
            elseif selectedItem.item.worldVersion > IsoWorld.getWorldVersion() then
                text = text .. " <TEXT> <RED> " .. getText("UI_worldscreen_SavefileNewerThanGame") .. " <RGB:1,1,1> <H2> <LINE> ";
			end
        end
    end
    self.richText.text = text;
    self.richText:paginate();
    local moreHeight = self.richText:getHeight();
    if moreHeight < 200 then
        moreHeight = 200;
    end
    local rectHeight = self.deleteButton:getY() - 10 - self.startY;
    local rectWidth = (self.deleteButton:getX() + self.deleteButton:getWidth()) - (self.width/2 + 30);
    self:drawRectBorder( self.width/2 + 30, self.startY, rectWidth, rectHeight, 0.3, 1, 1, 1);
    ]]--
end

function LoadGameScreen:prerender()
LoadGameScreen.instance = self
	ISPanel.prerender(self);

	self:drawTextCentre(getText("UI_LoadGameScreen_title"), self.width / 2, 10, 1, 1, 1, 1, UIFont.Cred2);

	self:disableBtn()
end

LoadGameScreen.onClickWorld = function()
    if MainScreen.instance.loadScreen.modal then
        MainScreen.instance.loadScreen.modal:setVisible(false);
        MainScreen.instance.loadScreen.modal:removeFromUIManager();
    end
end

function LoadGameScreen:onOptionMouseDown(button, x, y)
     if self.modal then
         self.modal:setVisible(false);
         self.modal = nil;
     end
	 if button.internal == "BACK" then
         self:setVisible(false);
         MainScreen.instance.bottomPanel:setVisible(true);
         if self.joyfocus then
             self.joyfocus.focus = MainScreen.instance;
             updateJoypadFocus(self.joyfocus);
         end
	 end
	 if button.internal == "PLAY" then
		self:clickPlay();
  	 end
	 if button.internal == "DELETE" then
		self.modal = ISModalDialog:new((getCore():getScreenWidth() / 2) - 130, (getCore():getScreenHeight() / 2) - 60, 260, 120, getText("UI_worldscreen_deletesave"), true, self, LoadGameScreen.onDeleteModalClick);
        self.modal:initialise();
        self.modal:addToUIManager();
        if self.joyfocus then
            self.joyfocus.focus = self.modal;
            updateJoypadFocus(self.joyfocus);
        end
	 end
end

function LoadGameScreen:onDblClickWorld()
	self:clickPlay();
end

function LoadGameScreen:clickPlay()
	local sel = self.listbox.items[self.listbox.selected];

	-- The map may not exist if a needed mod isn't loaded.
	if not sel or not sel.item.mapInfo then
		return
	end

	-- Old saves require build23 branch
	if tonumber(sel.item.worldVersion) and sel.item.worldVersion <= 23 then
		return
	end
	
	-- Vehicle saves require vehicle build
	if tonumber(sel.item.worldVersion) and sel.item.worldVersion <= 115 then
		local worldVersion = tonumber(sel.item.worldVersion)
		local errorMsg = nil
--		local lastPlayed = getLastPlayedDate(getWorld():getWorld())
		if not worldVersion or not sel.item.mapName then
			worldVersion = worldVersion or '???'
			errorMsg = getText("UI_mainscreen_SavefileNotFound")
		elseif not MainScreen.checkMapsAvailable(sel.item.mapName) then
			errorMsg = getText("UI_worldscreen_MapNotFound")
		elseif worldVersion == 0 then
			errorMsg = getText("UI_worldscreen_SavefileCorrupt")
		elseif worldVersion <= 23 then
			errorMsg = getText("UI_worldscreen_SavefileOld")
		elseif worldVersion <= 115 then
			errorMsg = "<LINE> <CENTER> <H1> <RED> " .. getText("UI_worldscreen_SaveCannotBeLoaded") .. " <LINE> <TEXT> <RED> " .. getText("UI_worldscreen_SavefileVehicle",worldVersion, IsoWorld.getWorldVersion());
		elseif worldVersion > IsoWorld.getWorldVersion() then
			errorMsg = getText("UI_worldscreen_SavefileNewerThanGame")
		end
		if errorMsg then
			local text = " <H1> " .. getText("UI_mainscreen_ErrorLoadingSavefile") .. " <H2> <LINE> <LINE> "
			text = text .. getText("UI_mainscreen_SavefileName", sel.item.saveName) .. " <LINE> <H2> "
--			text = text .. sel.item.saveName .. " <LINE> "
			text = text .. getText("UI_Map") .. sel.item.mapName .. " <LINE> "
			text = text .. getText("UI_WorldVersion") .. worldVersion .. " <LINE> "
			text = text .. " <TEXT> <RED> " .. errorMsg .. " <RGB:1,1,1> <LINE> "
			local modal = ISModalRichText:new(getCore():getScreenWidth() / 2 - 360 / 2,
				getCore():getScreenHeight() / 2 - 60, 360, 120, text, false,
				self, self.onErrorLoadingClick)
			modal:initialise()
			modal:addToUIManager()
			modal:setAlwaysOnTop(true)
			if JoypadState[1] then
				JoypadState[1].focus = modal
				updateJoypadFocus(JoypadState[1])
			end
		end
--
		return
	end

	if tonumber(sel.item.worldVersion) and sel.item.worldVersion > IsoWorld.getWorldVersion() then
		return
	end

	getWorld():setWorld(sel.item.saveName);
    getWorld():setGameMode(sel.item.gameMode);
	if not checkSaveFileExists("map_p.bin") then
		self:setVisible(false);
		if getCore():isChallenge() then
			MainScreen.instance.createWorld = false
			CharacterCreationProfession.instance.doSpawnPoint = false
			MainScreen.instance.charCreationMain.previousScreen = "LoadGameScreen"
			MainScreen.instance.charCreationMain:setVisible(true, self.joyfocus)
			return
		end
        MainScreen.instance.createWorld = false;
		local map = getSaveInfo(sel.text).mapName or "DEFAULT"
		getWorld():setMap(map) -- needed since we aren't showing WorldSelect
		if getWorld():getGameMode() == "Sandbox" then
			getSandboxOptions():loadCurrentGameBinFile()
		end
		if MapSpawnSelect.instance:hasChoices() then
			MapSpawnSelect.instance:fillList()
			MapSpawnSelect.instance.previousScreen = "LoadGameScreen"
			MapSpawnSelect.instance:setVisible(true, self.joyfocus)
		else
			MapSpawnSelect.instance:useDefaultSpawnRegion()
			MainScreen.instance.charCreationMain.previousScreen = "LoadGameScreen"
			MainScreen.instance.charCreationMain:setVisible(true, self.joyfocus)
		end
    else
        local saveinfo = getSaveInfo(sel.text);
		MainScreen.instance.loadScreen:setVisible(false);
        -- menu activated via joypad, we disable the joypads and will re-set them automatically when the game is started
        if self.joyfocus then
            local joypadData = self.joyfocus
            joypadData.focus = nil;
            updateJoypadFocus(joypadData);
            JoypadState.count = 0
            JoypadState.players = {};
            JoypadState.joypads = {};
            JoypadState.forceActivate = joypadData.id;
        end
        GameWindow.doRenderEvent(false);
		forceChangeState(GameLoadingState.new());
    end
end

function LoadGameScreen:onDeleteModalClick(button)
	if LoadGameScreen.instance.joyfocus then
		LoadGameScreen.instance.joyfocus.focus = LoadGameScreen.instance.listbox
		updateJoypadFocus(LoadGameScreen.instance.joyfocus)
	end
	if button.internal == "YES" then
		local sel = LoadGameScreen.instance.listbox.items[LoadGameScreen.instance.listbox.selected];
		LoadGameScreen.instance.listbox:removeItemByIndex(LoadGameScreen.instance.listbox.selected)
		deleteSave(sel.text);
        LoadGameScreen.instance.listbox.joypadListIndex = 1;
        LoadGameScreen.instance.listbox.selected = 1;
		if #LoadGameScreen.instance.listbox.items == 0 then
			LoadGameScreen.instance:setVisible(false);
			MainScreen.instance.loadOption:setVisible(false)
			MainScreen.instance.latestSaveOption:setVisible(false)
			MainScreen.instance.soloScreen:setVisible(true, self.joyfocus)
        else
            LoadGameScreen.instance:setVisible(true, self.joyfocus);
        end
    end
end

function LoadGameScreen:onErrorLoadingClick(button)
	if LoadGameScreen.instance.joyfocus then
		LoadGameScreen.instance.joyfocus.focus = LoadGameScreen.instance.listbox
		updateJoypadFocus(LoadGameScreen.instance.joyfocus)
	end
end

function LoadGameScreen:onGainJoypadFocus(joypadData)
    ISPanelJoypad.onGainJoypadFocus(self, joypadData);
    joypadData.focus = self.listbox;
    self.listbox.joypadFocused = true
    updateJoypadFocus(joypadData);
end

function LoadGameScreen:onGainJoypadFocus_child(joypadData)
	ISPanelJoypad.onGainJoypadFocus(self, joypadData)
	self:setISButtonForA(self.parent.playButton)
	self:setISButtonForB(self.parent.backButton)
	self:setISButtonForY(self.parent.deleteButton)
	if self == self.parent.listbox then
		self.joypadFocused = true
	end
end

function LoadGameScreen:onLoseJoypadFocus_child(joypadData)
	ISPanelJoypad.onLoseJoypadFocus(self, joypadData)
	if self == self.parent.listbox then
		self.joypadFocused = false
	end
end

function LoadGameScreen:onJoypadDirLeft_child(joypadData)
	joypadData.focus = self.parent.listbox
	updateJoypadFocus(joypadData)
end

function LoadGameScreen:onJoypadDirRight_child(joypadData)
	self.parent.listbox.joypadFocused = false
	joypadData.focus = self.parent.infoPanel
	updateJoypadFocus(joypadData)
end

function LoadGameScreen:disableBtn()
	local sel = LoadGameScreen.instance.listbox.items[LoadGameScreen.instance.listbox.selected]
	if sel and sel.item and sel.item.mapInfo then
		if tonumber(sel.item.worldVersion) and sel.item.worldVersion > IsoWorld.getWorldVersion() then
			self.playButton:setEnable(false)
		elseif not tonumber(sel.item.worldVersion) or sel.item.worldVersion > 23 then
			self.playButton:setEnable(true)
		elseif not tonumber(sel.item.worldVersion) or sel.item.worldVersion > 115 then
			self.playButton:setEnable(true)
		else
			self.playButton:setEnable(false)
		end
	else
		self.playButton:setEnable(false)
	end
end

function LoadGameScreen:onResolutionChange(oldw, oldh, neww, newh)
	self.listbox:setWidth(self.width / 2)
	local x = self.listbox:getRight() + 16
	self.infoPanel:setX(x)
	self.infoPanel:setWidth(self.width - 16 - x)
end

function LoadGameScreen:new (x, y, width, height)
	local o = {}
	--o.data = {}
	o = ISPanelJoypad:new(x, y, width, height);
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.backgroundColor = {r=0, g=0, b=0, a=0.3};
	o.borderColor = {r=1, g=1, b=1, a=0.2};
	o.width = width;
	o.height = height;
	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	o.itemheightoverride = {}
	o.selected = 1;
    o.startY = 80;
    o.deadTexture = getTexture("media/ui/Moodles/Moodle_Icon_Dead.png")
	LoadGameScreen.instance = o;
	return o
end

function LoadGameScreen.OnKeyPressed(key)
	if LoadGameScreen.instance and LoadGameScreen.instance:isVisible() then
		if (key == Keyboard.KEY_DELETE) then
			LoadGameScreen.instance.deleteButton:forceClick()
		end

		local listbox = LoadGameScreen.instance.listbox

		if (key == Keyboard.KEY_UP) then
			listbox.selected = listbox.selected - 1
			if listbox.selected <= 0 then
				listbox.selected = listbox:size()
			end
			listbox:ensureVisible(listbox.selected)
		end

		if key == Keyboard.KEY_DOWN then
			listbox.selected = listbox.selected + 1
			if listbox.selected > listbox:size() then
				listbox.selected = 1
			end
			listbox:ensureVisible(listbox.selected)
		end
	end
end

Events.OnKeyPressed.Add(LoadGameScreen.OnKeyPressed)
