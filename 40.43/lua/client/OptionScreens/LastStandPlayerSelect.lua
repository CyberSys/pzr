require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

LastStandPlayerSelect = ISPanelJoypad:derive("LastStandPlayerSelect");
LastStandPlayerSelect.playerSelected = nil;

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_MEDIUM = getTextManager():getFontHeight(UIFont.Medium)
local FONT_HGT_LARGE = getTextManager():getFontHeight(UIFont.Large)

function LastStandPlayerSelect:initialise()
	ISPanelJoypad.initialise(self);
end


	--************************************************************************--
	--** ISPanel:instantiate
	--**
	--************************************************************************--
function LastStandPlayerSelect:instantiate()

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

function LastStandPlayerSelect:getAllSavedPlayers()
	local ret = {}
	local playerList = getAllSavedPlayers();
	if playerList then
		for i=0,playerList:size()-1 do
			local playerFile = playerList:get(i);
			local newPlayer = nil;
			while true do
				line = playerFile:readLine();
				if line == nil then
					if newPlayer then -- save the last player found
						table.insert(ret, newPlayer)
					end
					playerFile:close();
					break;
				end
				if luautils.stringStarts(line, "Player") then
					if newPlayer then -- if we previously loaded a player, we add it to our list box
						table.insert(ret, newPlayer)
					end
					newPlayer = {}; -- prepare a new player
					newPlayer.traits = {};
					newPlayer.skills = {};
					newPlayer.playedTime = 1;
				end
				line = luautils.trim(line);
				if luautils.stringStarts(line, "Surname") then
					newPlayer.surname = string.split(line, "=")[2] or "";
				end
				if luautils.stringStarts(line, "Forename") then
					newPlayer.forename = string.split(line, "=")[2] or "";
				end
				if luautils.stringStarts(line, "Toppal") then
					newPlayer.toppal = string.split(string.split(line, "=")[2], ";")[1];
					newPlayer.toppalRGB = LastStandPlayerSelect:getColor(string.split(string.split(line, "=")[2], ";")[2]);
				end
				if luautils.stringStarts(line, "Bottomspal") then
					newPlayer.bottomspal = string.split(string.split(line, "=")[2], ";")[1];
					newPlayer.bottomRGB = LastStandPlayerSelect:getColor(string.split(string.split(line, "=")[2], ";")[2]);
				end
				if luautils.stringStarts(line, "Torso") then
					newPlayer.torso = string.split(line, "=")[2];
				end
				if luautils.stringStarts(line, "Hair") then
					newPlayer.hair = string.split(string.split(line, "=")[2], ";")[1];
					newPlayer.hairRGB = LastStandPlayerSelect:getColor(string.split(string.split(line, "=")[2], ";")[2]);
				end
				if luautils.stringStarts(line, "Female") then
					newPlayer.female = true;
				end
				if luautils.stringStarts(line, "Male") then
					newPlayer.female = false;
				end
				if luautils.stringStarts(line, "addTrait") then
					table.insert(newPlayer.traits,string.split(line, "=")[2]);
				end
				if luautils.stringStarts(line, "addSkills") then
					for i=0,tonumber(string.split(string.split(line, "=")[2], ",")[2]) -1 do
						table.insert(newPlayer.skills,string.split(string.split(line, "=")[2], ",")[1]);
					end
				end
				if luautils.stringStarts(line, "Profession") then
					newPlayer.profession = string.split(line, "=")[2];
				end
				if luautils.stringStarts(line, "GlobalXp") then
					newPlayer.globalXp = string.split(line, "=")[2];
				end
				if luautils.stringStarts(line, "Level") then
					newPlayer.level = string.split(line, "=")[2];
				end
				if luautils.stringStarts(line, "PlayedTime") then
					newPlayer.playedTime = string.split(line, "=")[2];
				end
				if luautils.stringStarts(line, "addGoldBoostBonus") then
					newPlayer.boostGoldLevel = string.split(line, "=")[2];
				end
				if luautils.stringStarts(line, "addXpBoostBonus") then
					newPlayer.boostXpLevel = string.split(line, "=")[2];
				end
				if luautils.stringStarts(line, "addStartingGoldBonus") then
					newPlayer.startingGoldLevel = string.split(line, "=")[2];
				end
			end
		end
	end
	return ret
end

function LastStandPlayerSelect:createSurvivorDescFromData(newPlayer)
	local newDesc = SurvivorFactory.CreateSurvivor();
	newDesc:setForename(newPlayer.forename);
	newDesc:setSurname(newPlayer.surname);
	newDesc:setToppal(newPlayer.toppal);
	newDesc:setBottomspal(newPlayer.bottomspal);
	newDesc:setTorso(newPlayer.torso);
	newDesc:setHair(newPlayer.hair);
	newDesc:setProfession(newPlayer.profession);
	newDesc:setHairColor(ColorInfo.new(newPlayer.hairRGB.r,newPlayer.hairRGB.g,newPlayer.hairRGB.b,1):toColor());
	newDesc:setTrouserColor(ColorInfo.new(newPlayer.bottomRGB.r,newPlayer.bottomRGB.g,newPlayer.bottomRGB.b,1):toColor());
	newDesc:setTopColor(ColorInfo.new(newPlayer.toppalRGB.r,newPlayer.toppalRGB.g,newPlayer.toppalRGB.b,1):toColor());
	newDesc:setFemale(newPlayer.female);
	if newPlayer.female then newDesc:getExtras():clear() end -- remove the beard
	return newDesc
end

function LastStandPlayerSelect:createPlayerList()
	local players = self:getAllSavedPlayers()
	for _,newPlayer in ipairs(players) do
		local newDesc = self:createSurvivorDescFromData(newPlayer)
		local avatar = IsoSurvivor.new(newDesc, nil, 0, 0, 0)
		avatar:PlayAnimWithSpeed("Idle", 0.3)
		avatar:setDir(IsoDirections.SE)
		avatar:reloadSpritePart()
		self.playersDesc["Player" .. newPlayer.forename .. newPlayer.surname] = avatar
		self.listbox:addItem(newPlayer.forename .. newPlayer.surname, newPlayer)
	end
end

function LastStandPlayerSelect:getColor(line)
	local result = {};
	result.r = tonumber(string.split(line, ",")[1]);
	result.g = tonumber(string.split(line, ",")[2]);
	result.b = tonumber(string.split(line, ",")[3]);
	return result;
end

function LastStandPlayerSelect:create()
	local btnWid = 100
	local btnHgt = math.max(25, FONT_HGT_SMALL + 3 * 2)
	local padBottom = 10
	local btnPadX = 16

	local listY = 10 + FONT_HGT_LARGE + 10
	self.listbox = ISScrollingListBox:new(16, listY, self.width-32, self.height - padBottom - btnHgt - padBottom - listY);
	self.listbox:initialise();
	self.listbox:instantiate();
	self.listbox:setAnchorLeft(true);
	self.listbox:setAnchorRight(true);
	self.listbox:setAnchorTop(true);
	self.listbox:setAnchorBottom(true);
	self.listbox.itemheight = 160;
	self.listbox.drawBorder = true
	self.listbox.doDrawItem = LastStandPlayerSelect.drawMap;
	self.listbox:setOnMouseDoubleClick(self, LastStandPlayerSelect.onDblClickPlayer);

	self:createPlayerList();

--~ 	for i, k in ipairs(LastStandChallenge) do
--~ 		self.listbox:addItem(k.name, k);
--~ 	end
	self:addChild(self.listbox);

	local btnY = self.height - padBottom - btnHgt
	
	self.backButton = ISButton:new(16, btnY, btnWid, btnHgt, getText("UI_btn_back"), self, LastStandPlayerSelect.onOptionMouseDown);
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

	self.deleteButton = ISButton:new(self.width - 16 - btnWid, btnY, btnWid, btnHgt, getText("UI_btn_delete"), self, LastStandPlayerSelect.onOptionMouseDown);
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

	self.playButton = ISButton:new(self.deleteButton.x - btnPadX - btnWid, btnY, btnWid, btnHgt, getText("UI_btn_play"), self, LastStandPlayerSelect.onOptionMouseDown);
	self.playButton.internal = "PLAY";
	self.playButton:initialise();
	self.playButton:instantiate();
	self.playButton:setAnchorLeft(false);
	self.playButton:setAnchorRight(true);
	self.playButton:setAnchorTop(false);
	self.playButton:setAnchorBottom(true);
	self.playButton.borderColor = {r=1, g=1, b=1, a=0.1};
	self:addChild(self.playButton);

	self.newButton = ISButton:new(self.playButton.x - btnPadX - btnWid, btnY, btnWid, btnHgt, getText("UI_btn_new"), self, LastStandPlayerSelect.onOptionMouseDown);
	self.newButton.internal = "NEW";
	self.newButton:initialise();
	self.newButton:instantiate();
	self.newButton:setAnchorLeft(false);
	self.newButton:setAnchorRight(true);
	self.newButton:setAnchorTop(false);
	self.newButton:setAnchorBottom(true);
	self.newButton.borderColor = {r=1, g=1, b=1, a=0.1};
	self:addChild(self.newButton);

	self:setVisible(false);

--~ 	avatar = IsoSurvivor.new(SurvivorFactory.CreateSurvivor(), nil, 0, 0, 0);
--~ 	avatar:PlayAnimWithSpeed("Run", 0.3);
--~ 	avatar:reloadSpritePart();
end

function LastStandPlayerSelect:render()
--~ 	avatar:drawAt(200,200);
end

function LastStandPlayerSelect:drawMap(y, item, alt)
	local isMouseOver = self.mouseoverselected == item.index and not self:isMouseOverScrollBar()
	if self.selected == item.index then
		self:drawRect(0, (y), self:getWidth(), self.itemheight-1, 0.3, 0.7, 0.35, 0.15);
	elseif isMouseOver then
		self:drawRect(1, y + 1, self:getWidth() - 2, item.height - 2, 0.95, 0.05, 0.05, 0.05);
	end
	local x = 0;

	self:drawRect( 16, y+16, 128, 128, 1, 0, 0, 0);
	local avatar = LastStandPlayerSelect.instance.playersDesc["Player" .. item.item.forename .. item.item.surname];
	self:drawRectBorder( 16, y+16, 128, 128, 0.3, 1, 1, 1);
	self:drawRectBorder(0, (y), self:getWidth(), self.itemheight-1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);

	self:drawText(item.item.forename .. " " .. item.item.surname, 160, (y)+15, 0.9, 0.9, 0.9, 0.9, UIFont.Large);

	x = 160;
	local y1 = y + 15 + FONT_HGT_LARGE + 4
	for i,v in ipairs(item.item.traits) do
		local trait = TraitFactory.getTrait(v);
		if trait and trait:getTexture() then
			self:drawTexture(trait:getTexture(), x, y1, 1,1,1,1);
			x = x + trait:getTexture():getWidth() + 2
		end
	end
	y1 = y1 + 16 + 4

	self:drawText("Played time : " .. item.item.playedTime, 160, y1, 0.7, 0.7, 0.7, 0.7, UIFont.Small);
	y1 = y1 + FONT_HGT_SMALL
	self:drawText("Xp : " .. item.item.globalXp, 160, y1, 0.7, 0.7, 0.7, 0.7, UIFont.Small);
	y1 = y1 + FONT_HGT_SMALL

	local prof = ProfessionFactory.getProfession(item.item.profession);
	if prof and prof:getTexture() then
		self:drawTexture(prof:getTexture(), self.width-80, y + 15, 1,1,1,1);
	end


--~ 	self:drawTextRight(item.item.lastPlayed, self:getWidth() - 10, (y)+self.itemheight-20, 0.7, 0.7, 0.7, 0.7, UIFont.Small);
	--self:drawTexture(self.worldimage, 16, y+16, 1, 1, 1, 1);

	self.itemheightoverride[item.text] = self.itemheight;

	avatar:drawAt(self:getAbsoluteX() + 16 + 128 / 2, self:getAbsoluteY() + y + 16 + 128 / 2 + 117 + self:getYScroll());

	y = y + self.itemheightoverride[item.text];

	return y;
end

function LastStandPlayerSelect:prerender()
	LastStandPlayerSelect.instance = self
	self.listbox.doDrawItem = self.drawMap
	
	ISPanelJoypad.prerender(self);
	self:drawTextCentre(getText("UI_challengeplayer_title"), self.width / 2, 10, 1, 1, 1, 1, UIFont.Large);
end

function LastStandPlayerSelect:onOptionMouseDown(button, x, y)
	local sel = self.listbox.items[self.listbox.selected];
	 if button.internal == "BACK" then
		MainScreen.instance.soloScreen:setVisible(true, self.joyfocus);
		MainScreen.instance.lastStandPlayerSelect:setVisible(false);
	 end
	 if button.internal == "PLAY" then
		LastStandPlayerSelect.clickPlay();
	 end
	 if button.internal == "NEW" then
		MainScreen.instance.charCreationProfession.doSpawnPoint = false;
		MainScreen.instance.charCreationMain.previousScreen = "LastStandPlayerSelect"
		MainScreen.instance.charCreationMain:setVisible(true, self.joyfocus);
		MainScreen.instance.lastStandPlayerSelect:setVisible(false);
	 end
	 if button.internal == "DELETE" then
		local modal = ISModalDialog:new((getCore():getScreenWidth() / 2) - 130, (getCore():getScreenHeight() / 2) - 60, 260, 120, getText("UI_challengeplayer_delete"), true, self, LastStandPlayerSelect.onDeleteModalClick);
		modal:initialise();
		modal:addToUIManager();
		if self.joyfocus then
			self.joyfocus.focus = modal;
--			updateJoypadFocus(self.joyfocus);
		end
	 end
end

function LastStandPlayerSelect:onDeleteModalClick(button)
	if LastStandPlayerSelect.instance.joyfocus then
		LastStandPlayerSelect.instance.joyfocus.focus = LastStandPlayerSelect.instance.listbox
	end
	if button.internal == "YES" then
		local sel = LastStandPlayerSelect.instance.listbox.items[LastStandPlayerSelect.instance.listbox.selected];
		table.remove(LastStandPlayerSelect.instance.listbox.items, LastStandPlayerSelect.instance.listbox.selected);
		deletePlayerSave(sel.text);
		if #LastStandPlayerSelect.instance.listbox.items == 0 then -- no more player saved, we redirect to the character creation screen
			MainScreen.instance.charCreationProfession.doSpawnPoint = false;
			MainScreen.instance.charCreationMain.previousScreen = "LastStandPlayerSelect"
			MainScreen.instance.charCreationMain:setVisible(true, self.joyfocus);
			MainScreen.instance.lastStandPlayerSelect:setVisible(false);
		end
	end
end

LastStandPlayerSelect.initWorld = function()
	if LastStandPlayerSelect.playerSelected then
		getWorld():setLuaPlayerDesc(LastStandPlayerSelect.instance.playersDesc["Player" .. LastStandPlayerSelect.playerSelected.forename .. LastStandPlayerSelect.playerSelected.surname]:getDescriptor());
		getWorld():getLuaTraits():clear()
		for i,v in ipairs(LastStandPlayerSelect.playerSelected.traits) do
			getWorld():addLuaTrait(v);
		end
	end
end

-- add the different saved skills level
LastStandPlayerSelect.newGame = function(player, square)
	if LastStandPlayerSelect.playerSelected then
		if globalChallenge.id ~= "Challenge2" then return end -- FIXME
		local skillMap = { Blunt=Perks.Blunt, Blade=Perks.Axe, Carpentry=Perks.Woodwork }
		for i,v in ipairs(LastStandPlayerSelect.playerSelected.skills) do
			local perk = skillMap[v]
			if perk then
				player:LevelPerk(perk);
				luautils.updatePerksXp(perk, player);
			end
		end
		player:getModData()["challenge2BoostGoldLevel"] = LastStandPlayerSelect.playerSelected.boostGoldLevel or 1;
		player:getModData()["challenge2StartingGoldLevel"] = LastStandPlayerSelect.playerSelected.startingGoldLevel or 0;
		player:getModData()["challenge2BoostXpLevel"] = LastStandPlayerSelect.playerSelected.boostXpLevel or 1;
	end
end

function LastStandPlayerSelect:onDblClickPlayer()
	LastStandPlayerSelect.clickPlay();
end

LastStandPlayerSelect.clickPlay = function()
	local sel = LastStandPlayerSelect.instance.listbox.items[LastStandPlayerSelect.instance.listbox.selected];
	MainScreen.instance.charCreationProfession.doSpawnPoint = false;
	MainScreen.instance.desc = LastStandPlayerSelect.instance.playersDesc["Player" .. sel.item.forename .. sel.item.surname]:getDescriptor();
	LastStandPlayerSelect.playerSelected = sel.item;
	LastStandPlayerSelect.playerSelected.playedTime = LastStandPlayerSelect.playerSelected.playedTime + 1;
	MainScreen.instance.lastStandPlayerSelect:setVisible(false);
	-- menu activated via joypad, we disable the joypads and will re-set them automatically when the game is started
	if LastStandPlayerSelect.instance.joyfocus then
		local joypadData = LastStandPlayerSelect.instance.joyfocus
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

function LastStandPlayerSelect:onGainJoypadFocus(joypadData)
    ISPanelJoypad.onGainJoypadFocus(self, joypadData);
    joypadData.focus = self.listbox;
    updateJoypadFocus(joypadData);
    self.listbox:setISButtonForA(self.playButton);
    self.listbox:setISButtonForB(self.backButton);
    self.listbox:setISButtonForX(self.newButton);
    self.listbox:setISButtonForY(self.deleteButton);
end

function LastStandPlayerSelect:new (x, y, width, height)
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
	o.playersDesc = {};
	LastStandPlayerSelect.instance = o;
	return o
end

Events.OnInitWorld.Add(LastStandPlayerSelect.initWorld);
Events.OnNewGame.Add(LastStandPlayerSelect.newGame);
