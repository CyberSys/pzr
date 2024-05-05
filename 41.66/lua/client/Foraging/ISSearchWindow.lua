-------------------------------------------------
-------------------------------------------------
--
-- ISSearchWindow
--
-- eris
--
-------------------------------------------------
-------------------------------------------------
require "ISUI/ISCollapsableWindow";
require "Foraging/ISSearchManager";
require "Foraging/ISZoneDisplay";
-------------------------------------------------
-------------------------------------------------
ISSearchWindow = ISCollapsableWindow:derive("ISSearchWindow");
ISSearchWindow.players          = {};
ISSearchWindow.showDebug        = false;
-------------------------------------------------
-------------------------------------------------
function ISSearchWindow:update()
	if (not self:getIsVisible()) then return; end;
	if self.manager.isSearchMode then
		self.toggleSearchMode.title = getText("UI_disable_search_mode");
	else
		self.toggleSearchMode.title = getText("UI_enable_search_mode");
	end;
	if ISSearchWindow.showDebug and self.manager.isSearchMode then
		local currentZone = self.manager.currentZone;
		if currentZone and currentZone.name then
			local title = "DEBUG: " .. currentZone.name;
			local itemsLeft = " - ICONS: (" .. currentZone.itemsLeft .. " / " .. currentZone.itemsTotal .. ")";
			self:setTitle(title .. itemsLeft);
		else
			self:setTitle("No Zone Here");
		end;
	else
		self:setTitle(getText("UI_investigate_area_window_title"));
	end;
end
-------------------------------------------------
-------------------------------------------------
function ISSearchWindow:toggleForceVisionTooltip()
	if self.tooltipForced and self.tooltipForced == "Vision" then
		self.tooltipForced = nil;
	else
		self.tooltipForced = "Vision";
	end;
end

function ISSearchWindow:toggleForceAreaTooltip()
	if self.tooltipForced and self.tooltipForced == "Area" then
		self.tooltipForced = nil;
	else
		self.tooltipForced = "Area";
	end;
end
-------------------------------------------------
-------------------------------------------------
function ISSearchWindow:onToggleVisible()
	if self:getIsVisible() then
		self:addToUIManager();
	else
		self:removeFromUIManager();
	end;
end

function ISSearchWindow:close()
	ISCollapsableWindow.close(self);
	if JoypadState.players[self.player+1] then
		setJoypadFocus(self.player, nil);
	end;
end

function ISSearchWindow:onJoypadDirUp()
	self:setY(self:getY() - self.joypadMoveSpeed);
end

function ISSearchWindow:onJoypadDirDown()
	self:setY(self:getY() + self.joypadMoveSpeed);
end

function ISSearchWindow:onJoypadDirLeft()
	self:setX(self:getX() - self.joypadMoveSpeed);
end

function ISSearchWindow:onJoypadDirRight()
	self:setX(self:getX() + self.joypadMoveSpeed);
end

function ISSearchWindow:onLoseJoypadFocus()
	self.drawJoypadFocus = false;
end

function ISSearchWindow:onGainJoypadFocus()
	self.drawJoypadFocus = true;
end

function ISSearchWindow:onJoypadDown(_button)
	if _button == Joypad.AButton then
		self.manager:toggleSearchMode();
	elseif _button == Joypad.BButton then
		self:close();
	elseif _button == Joypad.YButton then
		self:toggleForceAreaTooltip();
	elseif _button == Joypad.XButton then
		self:toggleForceVisionTooltip();
	elseif _button == Joypad.LBumper then
		setJoypadFocus(self.player, nil);
	elseif _button == Joypad.RBumper then
		setJoypadFocus(self.player, nil);
	end;
end

function ISSearchWindow:getAPrompt()
	return getText("UI_optionscreen_binding_Toggle Search Mode");
end

function ISSearchWindow:getBPrompt()
	return getText("IGUI_RadioClose");
end

function ISSearchWindow:getXPrompt()
	return getText("UI_investigate_area_window_toggle_vision_tooltip");
end

function ISSearchWindow:getYPrompt()
	return getText("UI_investigate_area_window_toggle_area_tooltip");
end

function ISSearchWindow:getLBPrompt()
	return getText("IGUI_RadioReleaseFocus");
end

function ISSearchWindow:getRBPrompt()
	return getText("IGUI_RadioReleaseFocus");
end

function ISSearchWindow:isValidPrompt()
	return self:getIsVisible();
end

-------------------------------------------------
-------------------------------------------------
function ISSearchWindow:checkShowFirstTimeSearchTutorial()
	if getCore():isShowFirstTimeSearchTutorial() then
		if not (SurvivalGuideManager.instance and SurvivalGuideManager.instance.panel) then
			doSurvivalGuide();
		end;
		--
		if SurvivalGuideManager.instance and SurvivalGuideManager.instance.panel then
			SurvivalGuideManager.instance.panel:setVisible(true);
			SurvivalGuideManager.instance.panel:setPage(11);
			if JoypadState.players[self.player+1] then setJoypadFocus(self.player, SurvivalGuideManager.instance.panel); end;
			getCore():setShowFirstTimeSearchTutorial(false);
			getCore():saveOptions();
		end;
	end;
end
-------------------------------------------------
-------------------------------------------------
function ISSearchWindow:initialise()
	ISCollapsableWindow.initialise(self);
	--
	self.zoneDisplay = ISZoneDisplay:new(self);
	self:addChild(self.zoneDisplay);
	self.toggleSearchMode = ISButton:new(0, self.zoneDisplay:getBottom() + 2, 300, 20, getText("UI_enable_search_mode"), self.manager, ISSearchManager.toggleSearchMode);
	self:addChild(self.toggleSearchMode);
	--
	self:addToUIManager();
	self:setVisible(false);
	self:update();
	self:setHeight(self.toggleSearchMode:getBottom());
	self:bringToTop();
	--
	self:setInfo(getText("SurvivalGuide_entrie11moreinfo"));
	--
	ISLayoutManager.RegisterWindow('ISSearchWindow', ISSearchWindow, self);
end
-------------------------------------------------
-------------------------------------------------
function ISSearchWindow:new(_manager)
	local o = ISCollapsableWindow:new(0, 0, 300, 0);
	setmetatable(o, self);
	self.__index = self;
	--
	o.x                 = 120;
	o.y                 = 300;
	o.width             = 300;
	o.height            = 170;
	--
	o.joypadMoveSpeed   = 20;
	o.overrideBPrompt   = true;
	o.tooltipForced     = nil;
	--
	o.showBackground    = true;
	o.showBorder        = true;
	o.backgroundColor   = {r=0, g=0, b=0, a=1};
	o.borderColor       = {r=0.4, g=0.4, b=0.4, a=0};
	--
	o.manager           = _manager;
	o.character         = _manager.character;
	o.player            = _manager.player;
	o.gameTime          = getGameTime();
	o.climateManager    = getClimateManager();
	--
	o.title             = getText("UI_investigate_area_window_title");
	--
	o.visibleTarget		= o;
	o.visibleFunction	= ISSearchWindow.onToggleVisible;
	--
	o:setResizable(false);
	o:setDrawFrame(true);
	--
	o:initialise();
	--
	return o;
end
-------------------------------------------------
-------------------------------------------------
function ISSearchWindow.toggleWindow(_character)
	if not ISSearchWindow.players[_character] then ISSearchWindow.createUI(_character:getPlayerNum()); end;
	local searchWindow = ISSearchWindow.players[_character];
	if searchWindow then
		local isVisible = not searchWindow:getIsVisible();
		searchWindow:setVisible(isVisible);
		searchWindow.tooltipForced = nil;
		searchWindow:bringToTop();
		if isVisible then
			if JoypadState.players[searchWindow.player+1] then
				setJoypadFocus(searchWindow.player, searchWindow);
			end;
			searchWindow:checkShowFirstTimeSearchTutorial();
		end;
	end;
end

function ISSearchWindow.showWindow(_character)
	if not ISSearchWindow.players[_character] then ISSearchWindow.createUI(_character:getPlayerNum()); end;
	local searchWindow = ISSearchWindow.players[_character];
	if searchWindow then
		searchWindow:setVisible(true);
		searchWindow:bringToTop();
		searchWindow:checkShowFirstTimeSearchTutorial();
	end;
end
-------------------------------------------------
-------------------------------------------------
function ISSearchWindow.createUI(_player)
	local character = getSpecificPlayer(_player);
	if (not ISSearchWindow.players[character]) then
		ISSearchWindow.players[character] = ISSearchWindow:new(ISSearchManager.getManager(character));
		ISSearchWindow.players[character]:setVisible(false);
		print("[ISSearchWindow] created UI for player " .. _player);
	end;
end

function ISSearchWindow.destroyUI(_character)
	if ISSearchWindow.players[_character] then
		ISSearchWindow.players[_character]:setVisible(false);
		ISSearchWindow.players[_character]:removeFromUIManager();
		ISSearchWindow.players[_character] = nil;
		print("[ISSearchWindow] removed UI for player " .. _character:getPlayerNum());
	end;
end

Events.OnCreatePlayer.Add(ISSearchWindow.createUI);
Events.OnPlayerDeath.Add(ISSearchWindow.destroyUI);

function ISSearchWindow.OnFillWorldObjectContextMenu(_player, _context)
	local character = getSpecificPlayer(_player);
	local searchWindow = ISSearchWindow.players[character];
	if searchWindow then
		if (not searchWindow:getIsVisible()) then
			_context:addOption(getText("UI_investigate_area_window_show"), character, ISSearchWindow.toggleWindow);
		else
			_context:addOption(getText("UI_investigate_area_window_hide"), character, ISSearchWindow.toggleWindow);
		end;
	end;
end

Events.OnFillWorldObjectContextMenu.Add(ISSearchWindow.OnFillWorldObjectContextMenu);

function ISSearchWindow.onEnableSearchMode(_character, _isSearchMode)
	local searchWindow = ISSearchWindow.players[_character];
	if searchWindow then searchWindow:checkShowFirstTimeSearchTutorial(); end;
end

Events.onEnableSearchMode.Add(ISSearchWindow.onEnableSearchMode);
-------------------------------------------------
-------------------------------------------------