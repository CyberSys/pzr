--***********************************************************
--**                   ROBERT JOHNSON                      **
--***********************************************************

require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

CharacterCreationHeader = ISPanel:derive("CharacterCreationHeader");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_MEDIUM = getTextManager():getFontHeight(UIFont.Medium)

function CharacterCreationHeader:initialise()
	ISPanel.initialise(self);
end
--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function CharacterCreationHeader:instantiate()
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

function CharacterCreationHeader:create()
	self.maletex = getTexture("media/ui/maleicon.png");
	self.femaletex = getTexture("media/ui/femaleicon.png");
	self.avatarBackgroundTexture = getTexture("media/ui/avatarBackground.png");

	self.avatarPanel = ISPanel:new(64, 64, 96, 96);
	self.avatarPanel:initialise();

	self:addChild(self.avatarPanel);
	self.avatarPanel.backgroundColor = {r=0, g=0, b=0, a=0.8};
	self.avatarPanel.borderColor = {r=1, g=1, b=1, a=0.2};

	self.turnLeftButton = ISButton:new(64, 64+96-15, 15, 15, "", self, CharacterCreationHeader.onTurnChar);
	self.turnLeftButton.internal = "TURNCHARACTERLEFT";
	self.turnLeftButton:initialise();
	self.turnLeftButton:instantiate();
	self.turnLeftButton:setImage(getTexture("media/ui/ArrowLeft.png"));

	self:addChild(self.turnLeftButton);

	self.turnRightButton = ISButton:new(64+96-15, 64+96-15, 15, 15, "", self, CharacterCreationHeader.onTurnChar);
	self.turnRightButton.internal = "TURNCHARACTERRIGHT";
	self.turnRightButton:initialise();
	self.turnRightButton:instantiate();
	self.turnRightButton:setImage(getTexture("media/ui/ArrowRight.png"));

	self:addChild(self.turnRightButton);

	local entryHgt = math.max(FONT_HGT_SMALL + 2 * 2, FONT_HGT_MEDIUM)
	local labelMaxWid = 0
	labelMaxWid = math.max(labelMaxWid, getTextManager():MeasureStringX(UIFont.Medium, getText("UI_characreation_forename")))
	labelMaxWid = math.max(labelMaxWid, getTextManager():MeasureStringX(UIFont.Medium, getText("UI_characreation_surname")))
	labelMaxWid = math.max(labelMaxWid, getTextManager():MeasureStringX(UIFont.Medium, getText("UI_characreation_gender")))
	local entryX = 64 + 96 + 16 + labelMaxWid + 6

	-- name/surname/sex btn
	self.forenameEntry = ISTextEntryBox:new(MainScreen.instance.desc:getForename(), entryX, 64, 200, entryHgt);
	self.forenameEntry:initialise();
	self.forenameEntry:instantiate();

--	self.forenameEntry:setAnchorRight(true);
	self:addChild(self.forenameEntry);

	self.surnameEntry = ISTextEntryBox:new(MainScreen.instance.desc:getSurname(), entryX, self.forenameEntry:getBottom() + 8, 200, entryHgt);
	self.surnameEntry:initialise();
	self.surnameEntry:instantiate();

--	self.surnameEntry:setAnchorRight(true);
	self:addChild(self.surnameEntry);

	self.femaleButton = ISButton:new(entryX, self.surnameEntry:getBottom() + 8 + (entryHgt - 18) / 2, 18, 18, "", self, CharacterCreationHeader.onOptionMouseDown);
	self.femaleButton.internal = "FEMALE";
	self.femaleButton:initialise();
	self.femaleButton:instantiate();

	self.femaleButton.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
	self:addChild(self.femaleButton);
	self.femaleButton.image = self.femaletex;

	self.maleButton = ISButton:new(entryX + 22, self.femaleButton:getY(), 18, 18, "", self, CharacterCreationHeader.onOptionMouseDown);
	self.maleButton.internal = "MALE";
	self.maleButton:initialise();
	self.maleButton:instantiate();

	self.maleButton.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
	self:addChild(self.maleButton);
	self.maleButton.image = self.maletex;

	self.direction = 5;

	if MainScreen.instance.avatar == nil then
		self:createAvatar();
		CharacterCreationProfession.instance:changeClothes();
	end

	self:disableBtn();
end

function CharacterCreationHeader:onTurnChar(button, x, y)
	if button.internal == "TURNCHARACTERLEFT" then
		self.direction = self.direction - 1;
		if self.direction < 0 then
			self.direction = 7;
		end
		MainScreen.instance.avatar:setDir(IsoDirections.fromIndex(self.direction));
	elseif button.internal == "TURNCHARACTERRIGHT" then
		self.direction = self.direction + 1;
		if self.direction > 7 then
			self.direction = 0;
		end
		MainScreen.instance.avatar:setDir(IsoDirections.fromIndex(self.direction));
	elseif button.internal == "CHARRUN" then
		MainScreen.instance.avatar:PlayAnimWithSpeed("Run", 0.1);
	end
end

function CharacterCreationHeader:onOptionMouseDown(button, x, y)
	-- remove the beard
	MainScreen.instance.desc:getExtras():clear()
	-- sex/random button handler
	if button.internal == "FEMALE" then
		MainScreen.instance.avatar:setFemale(true);
		MainScreen.instance.desc:setFemale(true);
		MainScreen.instance.desc:setTorso("Kate");
		MainScreen.instance.desc:setTorsoNumber(0);
		if CharacterCreationMain.instance.hairType == 5 then
			CharacterCreationMain.instance.hairType = 4;
		end
		SurvivorFactory.setHairNoColor(MainScreen.instance.desc);
		local hair = MainScreen.instance.desc:getHairNoColor()
		if hair ~= "none" then
			hair = hair .. "White"
		end
		MainScreen.instance.desc:setHair(hair);
		MainScreen.instance.avatar:reloadSpritePart();
		CharacterCreationProfession.instance:changeClothes();
	elseif button.internal == "MALE" then
		MainScreen.instance.avatar:setFemale(false);
		MainScreen.instance.desc:setFemale(false);
		MainScreen.instance.desc:setTorso("Male");
		MainScreen.instance.desc:setTorsoNumber(0);
		SurvivorFactory.setHairNoColor(MainScreen.instance.desc);
		local hair = MainScreen.instance.desc:getHairNoColor()
		if hair ~= "none" then
			hair = hair .. "White"
		end
		MainScreen.instance.desc:setHair(hair);
		local beard = MainScreen.instance.desc:getBeardNoColor()
		if beard and beard ~= "" then
			beard = beard .. "White";
			MainScreen.instance.desc:getExtras():add(beard);
		end
		MainScreen.instance.avatar:reloadSpritePart();
		CharacterCreationProfession.instance:changeClothes();
	elseif button.internal == "RANDOM" then
		MainScreen.instance.desc = SurvivorFactory.CreateSurvivor();
		self:createAvatar();
		CharacterCreationProfession.instance:changeClothes();
	end

	-- we random the name
	SurvivorFactory.randomName(MainScreen.instance.desc);

	self.forenameEntry:setText(MainScreen.instance.desc:getForename());
	self.surnameEntry:setText(MainScreen.instance.desc:getSurname());

    CharacterCreationMain.instance:loadJoypadButtons();

	self:disableBtn();
end

function CharacterCreationHeader:disableBtn()
    self.femaleButton:setEnable(true);
    self.maleButton:setEnable(true);
    -- sex btn disable
	if MainScreen.instance.desc:isFemale() then
        self.femaleButton:setEnable(false);
        --[[
        self.femaleButton.textureColor.r = 1;
        self.femaleButton.textureColor.g = 1;
        self.femaleButton.textureColor.b = 1;
        self.maleButton.textureColor.r = 0.3;
        self.maleButton.textureColor.g = 0.3;
        self.maleButton.textureColor.b = 0.3;
        self.femaleButton.borderColor.a = 0.7;
        self.femaleButton.borderColor.r = 0.7;
        self.femaleButton.borderColor.g = 0.1;
        self.femaleButton.borderColor.b = 0.1;
        self.maleButton.borderColor.a = 1;
        self.maleButton.borderColor.r = 1;
        self.maleButton.borderColor.g = 1;
        self.maleButton.borderColor.b = 1;
        ]]--
	else
        self.maleButton:setEnable(false);
        --[[
        self.maleButton.textureColor.r = 1;
        self.maleButton.textureColor.g = 1;
        self.maleButton.textureColor.b = 1;
        self.femaleButton.textureColor.r = 0.3;
        self.femaleButton.textureColor.g = 0.3;
        self.femaleButton.textureColor.b = 0.3;
        self.maleButton.borderColor.a = 0.7;
        self.maleButton.borderColor.r = 0.7;
        self.maleButton.borderColor.g = 0.1;
        self.maleButton.borderColor.b = 0.1;
        self.femaleButton.borderColor.a = 1;
        self.femaleButton.borderColor.r = 1;
        self.femaleButton.borderColor.g = 1;
        self.femaleButton.borderColor.b = 1;
        ]]--
	end
	CharacterCreationMain.instance:disableBtn();
end

function CharacterCreationHeader:createAvatar()
	if not MainScreen.instance.desc then
		MainScreen.instance.desc = SurvivorFactory.CreateSurvivor();
	end
	MainScreen.instance.avatar = IsoSurvivor.new(MainScreen.instance.desc, nil, 0, 0, 0);

	MainScreen.instance.avatar:setDir(self.direction);
	MainScreen.instance.avatar:PlayAnimWithSpeed("Run", 0.3);

	CharacterCreationMain.instance.hairType = MainScreen.instance.desc:getHairNumber();
	CharacterCreationMain.instance.hairColor = MainScreen.instance.desc:getHairColor();

	CharacterCreationProfession.instance.defaultToppal = MainScreen.instance.desc:getToppal();
	CharacterCreationProfession.instance.defaultBottomspal = MainScreen.instance.desc:getBottomspal();

	CharacterCreationProfession.instance.defaultTop = MainScreen.instance.desc:getTop();
	CharacterCreationProfession.instance.defaultBottoms = MainScreen.instance.desc:getBottoms();

	CharacterCreationProfession.instance.defaultToppalColor = MainScreen.instance.desc:getTopColor();
	CharacterCreationProfession.instance.defaultBottomspalColor = MainScreen.instance.desc:getTrouserColor();

	SurvivorFactory.setHairNoColor(MainScreen.instance.desc);
	local hair = MainScreen.instance.desc:getHairNoColor()
	if hair ~= "none" then
		hair = hair .. "White"
	end
	MainScreen.instance.desc:setHair(hair);
	MainScreen.instance.avatar:reloadSpritePart();

	MainScreen.instance.avatar:getSprite():setAnimateWhenPaused(true)
end

function CharacterCreationHeader:initPlayer()
	MainScreen.instance.desc:setForename(self.forenameEntry:getText());
	MainScreen.instance.desc:setSurname(self.surnameEntry:getText());
end

function CharacterCreationHeader:drawAvatar()
	if MainScreen.instance.avatar == nil then
		return;
	end

	local x = self.avatarPanel:getAbsoluteX();
	local y = self.avatarPanel:getAbsoluteY();
	x = x + 96/2;
	y = y + 165;

	MainScreen.instance.avatar:drawAt(x,y);
end

function CharacterCreationHeader:prerender()
	ISPanel.prerender(self);
end

function CharacterCreationHeader:render()
	-- rect over the avatar display
	self:drawRectBorder(62, 62, 100, 100, 1, 0.3, 0.3, 0.3);

	local textX = self.forenameEntry:getX() - 6
	self:drawTextRight(getText("UI_characreation_forename"), textX, self.forenameEntry:getY(), 1, 1, 1, 1, UIFont.Medium);
	self:drawTextRight(getText("UI_characreation_surname"), textX, self.surnameEntry:getY(), 1, 1, 1, 1, UIFont.Medium);
	self:drawTextRight(getText("UI_characreation_gender"), textX, self.surnameEntry:getBottom() + 8, 1, 1, 1, 1, UIFont.Medium);

	self:drawTexture(self.avatarBackgroundTexture, 64, 64, 1, 1, 1, 1);

	self:drawAvatar();

	self.turnLeftButton:prerender();
	self.turnLeftButton:render();
	self.turnRightButton:prerender();
	self.turnRightButton:render();
end

function CharacterCreationHeader:new (x, y, width, height)
	local o = {};
	o = ISPanel:new(x, y, width, height);
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.backgroundColor = {r=0, g=0, b=0, a=0.0};
	o.borderColor = {r=1, g=1, b=1, a=0.0};
	o.itemheightoverride = {};
	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	CharacterCreationHeader.instance = o;
	return o
end
