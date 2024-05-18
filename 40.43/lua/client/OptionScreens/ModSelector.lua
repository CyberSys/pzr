--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

ModSelector = ISPanelJoypad:derive("ModSelector");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_LARGE = getTextManager():getFontHeight(UIFont.Large)

function ModSelector:initialise()
    ISPanelJoypad.initialise(self);
end

function ModSelector:onDblClickMap(item)
	if not self.modorderui or not self.modorderui:isVisible() then
		if self.listbox:isMouseOverScrollBar() then return end
		if self.listbox.mouseOverButton then
			item = self.listbox.mouseOverButton.item
		end
		self:forceActivateMods(item.modInfo, not isModActive(item.modInfo));
	end
end

function ModSelector:forceActivateMods(loc, activate)
	if loc:isAvailable() then
		toggleModActive(loc, activate);
		-- we also activate the required mod if needed
		if isModActive(loc) and loc:getRequire() then
			for l=0,loc:getRequire():size() - 1 do
				for i,k in ipairs(self.listbox.items) do
					local modInfo2 = k.item.modInfo
					if modInfo2:getId() and modInfo2:getId():trim() == loc:getRequire():get(l):trim() then
						self:forceActivateMods(modInfo2, isModActive(loc));
					end
				end
			end
		end
	end
	-- check the "parents" mod to disable
	if not isModActive(loc) then
		for i,activatedMod in ipairs(self.listbox.items) do
			local modInfo2 = activatedMod.item.modInfo
			if isModActive(modInfo2) and modInfo2:getRequire() then
				for l=0,modInfo2:getRequire():size() - 1 do
					if loc:getId() == modInfo2:getRequire():get(l):trim() then
						self:forceActivateMods(modInfo2, false);
					end
				end
			end
		end
	end
	self.mapGroups:createGroups(false)
	self.mapConflicts = self.mapGroups:checkMapConflicts()
end

function ModSelector:onModsEnabledTick(option, selected)
	getCore():setOptionModsEnabled(selected)
end

--************************************************************************--
--** ModSelector:instantiate
--**
--************************************************************************--
function ModSelector:instantiate()
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

function ModSelector:populateListBox(list)
    ModSelector.instance.listbox:clear();
    self.ModStatus = {}
    for i, k in ipairs(list) do
		local item = {}
		item.modInfo = getModInfo(k)
		if item.modInfo:getDescription() then
			item.richText = ISRichTextLayout:new(self:getWidth()-17)
			item.richText:initialise()
			item.richText:setText(item.modInfo:getDescription())
			item.richText:paginate()
		end
        ModSelector.instance.listbox:addItem(k, item)
        self.ModStatus[k] = isModActive(item.modInfo)
    end
	-- check the require mods
	for i,k in ipairs(ModSelector.instance.listbox.items) do
		local modInfo = k.item.modInfo
		if modInfo:getRequire() then
			-- we check the existing mod to see if we have the required ones
			modInfo:setAvailable(ModSelector:checkRequiredMods(modInfo:getRequire()));
		end
	end
	self.ModsEnabled = getCore():getOptionModsEnabled()
	table.sort(self.listbox.items, function(a,b)
			return not string.sort(a.item.modInfo:getName(), b.item.modInfo:getName())
		end)
	self.mapGroups:createGroups(false)
	self.mapConflicts = self.mapGroups:checkMapConflicts()
end

function ModSelector:checkRequiredMods(list)
	for l=0,list:size() - 1 do
		local find = false;
		for i,k in ipairs(ModSelector.instance.listbox.items) do
			local modInfo = k.item.modInfo
			if modInfo:getId() and modInfo:getId():trim() == list:get(l):trim() then
				find = true;
				if modInfo:getRequire() then
					find = self:checkRequiredMods(modInfo:getRequire());
				end
				break;
			end
		end
		if not find then
			return false;
		end
	end
	return true;
end

function ModSelector:create()
    self.mode = "NEW";

    local listY = 10 + FONT_HGT_LARGE + 10
    local labelHgt = FONT_HGT_SMALL
     self.smallFontHgt = labelHgt
    local buttonHgt = math.max(25, FONT_HGT_SMALL + 3 * 2)
   
    self.listbox = ISScrollingListBox:new(16, listY, self.width/2-16, self.height-30-8-labelHgt-8-listY);
    self.listbox:initialise();
    self.listbox:instantiate();
    self.listbox:setAnchorLeft(true);
    self.listbox:setAnchorRight(true);
    self.listbox:setAnchorTop(true);
    self.listbox:setAnchorBottom(true);
    self.listbox.itemheight = 128;
    self.listbox.drawBorder = true
    self.listbox.doDrawItem = ModSelector.drawMap;
    self.listbox.onMouseDown = self.onMouseDown_listbox;
    self.listbox:setOnMouseDoubleClick(self, ModSelector.onDblClickMap);
    self:addChild(self.listbox);

    self.playButton = ISButton:new(16, self.height-buttonHgt - 5, 100, buttonHgt, getText("UI_btn_back"), self, ModSelector.onOptionMouseDown);
    self.playButton.internal = "DONE";
    self.playButton:initialise();
    self.playButton:instantiate();
    self.playButton:setAnchorLeft(true);
    self.playButton:setAnchorRight(false);
    self.playButton:setAnchorTop(false);
    self.playButton:setAnchorBottom(true);
    self.playButton.borderColor = {r=1, g=1, b=1, a=0.1};
    self.playButton:setFont(UIFont.Small);
    self.playButton:ignoreWidthChange();
    self.playButton:ignoreHeightChange();
    self:addChild(self.playButton);

    local x = getTextManager():MeasureStringX(UIFont.Small, getText("UI_mods_Explaination") .. Core.getMyDocumentFolder() .. getFileSeparator() .. "mods" .. getFileSeparator());
    local size = getTextManager():MeasureStringX(UIFont.Small, getText("UI_mods_GetModsHere"));
    self.getModButton = ISButton:new(self.width - size - 30, self.height - buttonHgt - 5, 100, buttonHgt, getText("UI_mods_GetModsHere"), self, ModSelector.onOptionMouseDown);
    self.getModButton.internal = "GETMOD";
    self.getModButton:initialise();
    self.getModButton:instantiate();
    self.getModButton:setAnchorLeft(false);
    self.getModButton:setAnchorRight(true);
    self.getModButton:setAnchorTop(false);
    self.getModButton:setAnchorBottom(true);
    self.getModButton.borderColor = {r=1, g=1, b=1, a=0.1};
    self.getModButton:setFont(UIFont.Small);
    self.getModButton:ignoreWidthChange();
    self.getModButton:ignoreHeightChange();
	if not getSteamModeActive() then
		self.getModButton.tooltip = getText("UI_mods_WorkshopRequiresSteam")
	end
    self:addChild(self.getModButton);

	self.modOrderbtn = ISButton:new(self.getModButton.x - getTextManager():MeasureStringX(UIFont.Small, getText("UI_mods_ModsOrder")) - 15, self.height - buttonHgt - 5, 100, buttonHgt, getText("UI_mods_ModsOrder"), self, ModSelector.onOptionMouseDown);
	self.modOrderbtn.internal = "MODSORDER";
	self.modOrderbtn:initialise();
	self.modOrderbtn:instantiate();
	self.modOrderbtn:setAnchorLeft(false);
	self.modOrderbtn:setAnchorRight(true);
	self.modOrderbtn:setAnchorTop(false);
	self.modOrderbtn:setAnchorBottom(true);
	self.modOrderbtn.borderColor = {r=1, g=1, b=1, a=0.1};
	self.modOrderbtn:setFont(UIFont.Small);
	self.modOrderbtn:ignoreWidthChange();
	self.modOrderbtn:ignoreHeightChange();
	self:addChild(self.modOrderbtn);

	local urlX = self:getWidth() / 2 + 16
	self.urlButton = ISButton:new(urlX, self.height - 5 - buttonHgt - 5 - buttonHgt, self:getWidth() - 16 - urlX, buttonHgt, getText("UI_mods_OpenWebBrowser"), self, ModSelector.onOptionMouseDown);
    self.urlButton.internal = "URL";
	self.urlButton.url = "";
    self.urlButton:initialise();
    self.urlButton:instantiate();
    self.urlButton:setAnchorLeft(false);
    self.urlButton:setAnchorRight(true);
    self.urlButton:setAnchorTop(false);
    self.urlButton:setAnchorBottom(true);
    self.urlButton.borderColor = {r=1, g=1, b=1, a=0.1};
    self.urlButton:setFont(UIFont.Small);
    self.urlButton:setEnable(false)
    self:addChild(self.urlButton);

--[[
	local checkBox = ISTickBox:new(16, self.height - 30, 200, 30, "Enable mods", self, ModSelector.onModsEnabledTick)
	checkBox:initialise()
	checkBox:setAnchorTop(false)
	checkBox:setAnchorBottom(true)
	checkBox:addOption("Enable mods", nil)
	checkBox.leftMargin = 0
	checkBox.selected[1] = getCore():getOptionModsEnabled()
	self:addChild(checkBox)
	--]]
end

function ModSelector:onMouseDown_listbox(x, y)
	-- stop you from changing mods while in mod order UI
	if not self.parent.modorderui or not self.parent.modorderui:isVisible() then
		if #self.items == 0 then return end
		local row = self:rowAt(x, y)
		if row > #self.items then
			row = #self.items
		end
		if row < 1 then
			row = 1
		end
		if self.mouseOverButton then
			self.parent:forceActivateMods(self.mouseOverButton.item.modInfo, not isModActive(self.mouseOverButton.item.modInfo));
		else
			self.selected = row
		end
	end
end

function ModSelector:drawMap(y, item, alt)
    local modInfo = item.item.modInfo
    local tex = nil;
    local isMouseOver = self.mouseoverselected == item.index
    -- if we've selected something, we gonna display the poster on the right
    if self.selected == item.index then
        self:drawRect(0, (y), self:getWidth(), item.height-1, 0.3, 0.7, 0.35, 0.15);
		-- if we have an url, we add a button to link it
		if modInfo:getWorkshopID() and isSteamOverlayEnabled() then
			ModSelector.instance.urlButton:setEnable(true);
			ModSelector.instance.urlButton.workshopID = modInfo:getWorkshopID()
			ModSelector.instance.urlButton:setTitle(getText("UI_WorkshopSubmit_OverlayButton"))
		elseif modInfo:getUrl() and modInfo:getUrl() ~= "" then
			ModSelector.instance.urlButton:setVisible(true);
			ModSelector.instance.urlButton.workshopID = nil
			ModSelector.instance.urlButton.url = modInfo:getUrl();
--			ModSelector.instance.urlButton:setTitle("URL : " .. modInfo:getUrl());
			ModSelector.instance.urlButton:setEnable(true);
			ModSelector.instance.urlButton:setTitle(getText("UI_mods_OpenWebBrowser"))
		else
			ModSelector.instance.urlButton:setTitle(getText("UI_mods_OpenWebBrowser"))
			ModSelector.instance.urlButton:setEnable(false);
		end
	elseif isMouseOver and not self:isMouseOverScrollBar() then
		self:drawRect(1, y + 1, self:getWidth() - 2, item.height - 2, 0.95, 0.05, 0.05, 0.05);
    end

    -- border over text and description
    self:drawRectBorder(0, (y), self:getWidth(), item.height-1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    -- the name of the story
    self:drawText(modInfo:getName(), 16, (y)+15, 0.9, 0.9, 0.9, 0.9, UIFont.Large);

	local largeFontHgt = getTextManager():getFontFromEnum(UIFont.Large):getLineHeight()
	local itemHgt = 15 + largeFontHgt

	-- the required mods to make this one works
	local smallFontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
	if modInfo:getRequire() and modInfo:isAvailable() then
		self:drawText(getText("UI_mods_require") .. string.gsub(string.gsub(modInfo:getRequire():toString(), "%[", ""), "%]", ""), 36, (y)+itemHgt, 0.9, 0.9, 0.9, 0.9, UIFont.Small);
		itemHgt = itemHgt + smallFontHgt
	elseif modInfo:getRequire() and not modInfo:isAvailable() then
		self:drawText(getText("UI_mods_require") .. string.gsub(string.gsub(modInfo:getRequire():toString(), "%[", ""), "%]", ""), 36, (y)+itemHgt, 0.9, 0.1, 0.1, 0.9, UIFont.Small);
		itemHgt = itemHgt + smallFontHgt
	end

    -- the description of our story
	if modInfo:getDescription() then
		local richText = item.item.richText
		if richText:getWidth() ~= self:getWidth() - 32 then
			richText:setWidth(self:getWidth() - 32)
			richText:paginate()
		end
		richText:render(16, y + itemHgt, self)
		itemHgt = itemHgt + richText:getHeight() + 8
	end

	local textDisabled = getText("UI_mods_ModDisabled")
	local textEnabled = getText("UI_mods_ModEnabled")
	local textDisabledWid = getTextManager():MeasureStringX(UIFont.Small, textDisabled)
	local textEnabledWid = getTextManager():MeasureStringX(UIFont.Small, textEnabled)
	local buttonWid = 8 + math.max(textEnabledWid, textDisabledWid) + 8
	local buttonHgt = largeFontHgt
	local scrollBarWid = self:isVScrollBarVisible() and 13 or 0
	local buttonX = self.width - 16 - scrollBarWid - buttonWid
	local buttonY = y + 15
	local isMouseOverButton = isMouseOver and ((self:getMouseX() > buttonX - 16) and (self:getMouseX() < self.width - scrollBarWid) and (self:getMouseY() < buttonY + buttonHgt + 16))
	local isJoypadSelected = self.parent.hasJoypadFocus and self.selected == item.index
	
    if(isModActive(modInfo)) then
--        self:drawTexture(ModSelector.instance.tickTexture, 10, (y)+20+34, 1, 1, 1, 1);
		if isMouseOverButton then
			self:drawRect(buttonX, buttonY, buttonWid, buttonHgt, 1, 0, 0.85, 0)
			self.mouseOverButton = item
		else
			self:drawRect(buttonX, buttonY, buttonWid, buttonHgt, 1, 0, 0.70, 0)
		end
		self:drawTextCentre(textEnabled, buttonX +  buttonWid / 2, y + 15 + (buttonHgt - smallFontHgt) / 2 , 0, 0, 0, 1)
	elseif (isMouseOver and not self:isMouseOverScrollBar() or isJoypadSelected) and modInfo:isAvailable() then
		local rgb = (isMouseOverButton or isJoypadSelected) and 0.5 or 0.2
		self:drawRect(buttonX, y + 15, buttonWid, buttonHgt, 1, rgb, rgb, rgb)
		self:drawTextCentre(textDisabled, buttonX + buttonWid / 2, y + 15 + (buttonHgt - smallFontHgt) / 2 , 0, 0, 0, 1)
		self.mouseOverButton = isMouseOverButton and item or nil
    end

	if not modInfo:isAvailable() then
		self:drawTexture(ModSelector.instance.cantTexture, 23, (y)+20+34, 1, 1, 1, 1);
	end

    y = y + itemHgt;

    return y;
end

function ModSelector:prerender()
	ModSelector.instance = self
	self:updateButtons();
    self.listbox.mouseOverButton = nil
    ISPanelJoypad.prerender(self);
	if self.listbox.items and self.listbox.items[self.listbox.selected] then
		local tex = self.listbox.items[self.listbox.selected].item.modInfo:getTexture()
		local left = self.urlButton:getX()
		local top = self.listbox:getY()
		local alpha = 1
		if tex == getTexture('white') then alpha = 0.1 end
		self:drawTextureScaledAspect(tex, left, top, self:getWidth() - 16 - left, self.urlButton:getY() - 8 - top, alpha, 1, 1, 1);
	end
    self:drawTextCentre(getText("UI_mods_SelectMods"), self.width / 2, 10, 1, 1, 1, 1, UIFont.Large);
    local labelY = self.playButton:getY() - 2 - self.smallFontHgt
    if self.hasJoypadFocus then
        local fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
        self:drawTextureScaled(self.abutton, 16, labelY, fontHgt, fontHgt, 1,1,1,1)
        self:drawText(getText("UI_mods_ExplanationJoypad") .. Core.getMyDocumentFolder() .. getFileSeparator() .. "mods" .. getFileSeparator(), 16 + fontHgt + 2, labelY, 1, 1, 1, 1, UIFont.Small);
    else
        self:drawText(getText("UI_mods_Explaination") .. Core.getMyDocumentFolder() .. getFileSeparator() .. "mods" .. getFileSeparator(), 16, labelY, 1, 1, 1, 1, UIFont.Small);
	end
end

function ModSelector:updateButtons()
	self.modOrderbtn.enable = self.mapConflicts
	if self.modorderui and self.modorderui:isReallyVisible() then
		self.modOrderbtn.blinkBG = false
		self.modOrderbtn.tooltip = nil
	else
		self.modOrderbtn.blinkBG = self.mapConflicts
		self.modOrderbtn.tooltip = self.mapConflicts and getText("UI_mods_ConflictDetected") or nil
	end
end

function ModSelector:onOptionMouseDown(button, x, y)
    if button.internal == "DONE" then
        self:setVisible(false);
        MainScreen.instance.modSelect:setVisible(false);
        MainScreen.instance.bottomPanel:setVisible(true);
        saveModsFile();
        local reset = self.ModsEnabled ~= getCore():getOptionModsEnabled()
        for k,v in pairs(self.ModStatus) do
			if self.ModStatus[k] ~= isModActive(getModInfo(k)) then
				reset = true
				break
			end
		end
		if reset then
			getCore():ResetLua(true, "modsChanged") -- Boom!
		else
			if self.joyfocus then
				self.joyfocus.focus = MainScreen.instance;
				updateJoypadFocus(self.joyfocus);
			end
		end
		if self.modorderui then
			self.modorderui:removeFromUIManager();
		end
    elseif button.internal == "URL" then
		if button.workshopID then
			activateSteamOverlayToWorkshopItem(button.workshopID)
		else
			openUrl(button.url);
		end
    elseif button.internal == "GETMOD" then
		if getSteamModeActive() then
			if isSteamOverlayEnabled() then
				activateSteamOverlayToWorkshop()
			else
				openUrl("steam://url/SteamWorkshopPage/108600")
			end
		else
		openUrl("http://theindiestone.com/forums/index.php/forum/58-mods/");
		end
    elseif button.internal == "MODSORDER" then
		self.modorderui = ModOrderUI:new(0, 0, 700, 400);
		self.modorderui:initialise();
		self.modorderui:addToUIManager();
	end
end

function ModSelector:onGainJoypadFocus(joypadData)
    ISPanelJoypad.onGainJoypadFocus(self, joypadData);
    self.listbox:setISButtonForB(self.playButton);
--    self.listbox:setJoypadFocused(true, joypadData);
	self.hasJoypadFocus = true
    joypadData.focus = self.listbox;
end

function ModSelector:onResolutionChange(oldw, oldh, neww, newh)
	self.listbox:setWidth(self:getWidth() / 2 - self.listbox:getX())
	self.listbox:recalcSize()
	self.listbox.vscroll:setX(self.listbox:getWidth() - 16)
	local urlX = self:getWidth() / 2 + 16
	self.urlButton:setWidth(self:getWidth() - 16 - urlX)
	self.urlButton:setX(urlX)
end

function ModSelector:new (x, y, width, height)
    local o = {}
    --o.data = {}
    o = ISPanelJoypad:new(x, y, width, height);
    ModSelector.instance = o;
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
    o.tickTexture = getTexture("Quest_Succeed");
	o.cantTexture = getTexture("Quest_Failed");
	o.abutton =  getTexture("media/ui/abutton.png");
    o.selected = 1;
    o.mapGroups = MapGroups.new()
    return o
end
