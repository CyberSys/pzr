--***********************************************************
--**                    ROBERT JOHNSON                     **
--**              Panel wich display all our skills        **
--***********************************************************

require "ISUI/ISPanelJoypad"

ISCharacterScreen = ISPanelJoypad:derive("ISCharacterScreen");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_MEDIUM = getTextManager():getFontHeight(UIFont.Medium)

--************************************************************************--
--** ISPanel:initialise
--**
--************************************************************************--

function ISCharacterScreen:initialise()
	ISPanelJoypad.initialise(self);
	self:create();
end


function ISCharacterScreen:setVisible(visible)
--    self.parent:setVisible(visible);
    self:loadTraits();
    self:loadProfession();
    self.javaObject:setVisible(visible);
end

function ISCharacterScreen:render()

--	if self.Strength ~= self.char:getPerkLevel(Perks.Strength) or
--			self.Fitness ~= self.char:getPerkLevel(Perks.Fitness) then
	if self:traitsChanged() then
		self:loadTraits();
    end
    self:loadProfession();
--	end
	
--~ 	ISCharacterScreen.loadTraits(self);

	ISCharacterScreen.loadFavouriteWeapon(self);

	self:drawRectBorder(25, 25, 96, 96, 0.6,1,1,1);
	self:drawRect(25, 25, 96, 96, 0.8,0,0,0);

	local z = 25;

	local nameText = self.char:getDescriptor():getForename().." "..self.char:getDescriptor():getSurname()
	local nameWid = getTextManager():MeasureStringX(UIFont.Medium, nameText)
	self:drawText(nameText, 150, z, 1,1,1,1, UIFont.Medium);

	local professionWid = self.profImage:getWidth()
    if not self.professionTexture then
		professionWid = math.max(professionWid, getTextManager():MeasureStringX(UIFont.Small, self.profession))
        self:drawText(self.profession, self.width - 20 - professionWid, z, 1,1,1,1,UIFont.Small);
        self.profImage:setVisible(false);
    else
        self.profImage:setVisible(true);
        self.profImage:setMouseOverText(self.profession);
        self.profImage.texture = self.professionTexture;
    end

	local panelWidth = 150 + nameWid + 20 + professionWid + 20
	panelWidth = math.max(panelWidth, 148 + 160 + 20 + self.profImage.width + 20)
	self:setWidthAndParentWidth(panelWidth)

	self.profImage:setX(panelWidth - 20 - self.profImage.width)

	z = z + FONT_HGT_MEDIUM;
	self:drawRect(148, z, 160, 1, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

	z = z + 10;

	local smallFontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()

	self:drawTextRight(getText("IGUI_char_Age"), self.xOffset, z, 1,1,1,1, UIFont.Small);
	self:drawText("27", self.xOffset + 10, z, 1,1,1,0.5, UIFont.Small);

	z = z + smallFontHgt;
	self:drawTextRight(getText("IGUI_char_Sex"), self.xOffset, z, 1,1,1,1, UIFont.Small);
	self:drawText(self.sexText, self.xOffset + 10, z, 1,1,1,0.5, UIFont.Small);

    z = z + smallFontHgt;
--    self:drawTextRight("Cal", self.xOffset, z, 1,1,1,1, UIFont.Small);
--    self:drawText(round(self.char:getNutrition():getCalories(), 2) .. "", self.xOffset + 10, z, 1,1,1,0.5, UIFont.Small);
    self:drawTextRight(getText("IGUI_char_Weight"), self.xOffset, z, 1,1,1,1, UIFont.Small);
    local weightStr = tostring(round(self.char:getNutrition():getWeight(), 0))
    self:drawText(weightStr, self.xOffset + 10, z, 1,1,1,0.5, UIFont.Small);
	if self.char:getNutrition():isIncWeight() or self.char:getNutrition():isIncWeightLot() or self.char:getNutrition():isDecWeight() then
		local nutritionWidth = getTextManager():MeasureStringX(UIFont.Small, weightStr) + 13;
		if self.char:getNutrition():isIncWeight() and not self.char:getNutrition():isIncWeightLot() then
			self:drawTexture(self.weightIncTexture, self.xOffset + nutritionWidth, z + 3, 1, 0.8, 0.8, 0.8)
		end
		if self.char:getNutrition():isIncWeightLot() then
			self:drawTexture(self.weightIncLotTexture, self.xOffset + nutritionWidth, z, 1, 0.8, 0.8, 0.8)
		end
		if self.char:getNutrition():isDecWeight() then
			self:drawTexture(self.weightDecTexture, self.xOffset + nutritionWidth, z + 3, 1, 0.8, 0.8, 0.8)
		end
	end
--    z = z + 14;
--    self:drawTextRight("Carb/Prot/Lip", self.xOffset, z, 1,1,1,1, UIFont.Small);
--    self:drawText(round(self.char:getNutrition():getCarbohydrates(),2) .. "," .. round(self.char:getNutrition():getProteins(),2) .. "," .. round(self.char:getNutrition():getLipids(),2), self.xOffset + 10, z, 1,1,1,0.5, UIFont.Small);
--    z = z + 14;

	z = z + smallFontHgt;
	local traitBottom = z
	if #self.traits > 0 then
		self:drawTextRight(getText("IGUI_char_Traits"), self.xOffset, z, 1,1,1,1, UIFont.Small);
		x = self.xOffset + 10;
		local y = z + (math.max(FONT_HGT_SMALL, 18) - 18) / 2 + 2
		for i,v in ipairs(self.traits) do
			v:setY(y);
			v:setX(x);
            v:setVisible(true);
			traitBottom = y + v:getTexture():getHeightOrig() + 2
			x = x + v:getTexture():getWidthOrig() + 6;
			if x + v:getTexture():getWidthOrig() > self:getWidth() - 20 then
				x = self.xOffset + 10
				y = y + v:getTexture():getHeightOrig() + 2
			end
		end
	end
	z = math.max(z + 45, traitBottom);
	local textWid1 = getTextManager():MeasureStringX(UIFont.Small, getText("IGUI_char_Favourite_Weapon"))
	local textWid2 = getTextManager():MeasureStringX(UIFont.Small, getText("IGUI_char_Zombies_Killed"))
	local textWid3 = getTextManager():MeasureStringX(UIFont.Small, getText("IGUI_char_Survived_For"))
	local x = 20 + math.max(textWid1, math.max(textWid2, textWid3))
	
	if self.favouriteWeapon then
		self:drawTextRight(getText("IGUI_char_Favourite_Weapon"), x, z, 1,1,1,1, UIFont.Small);
		self:drawText(self.favouriteWeapon, x + 10, z, 1,1,1,0.5, UIFont.Small);
		z = z + smallFontHgt;
	end
	self:drawTextRight(getText("IGUI_char_Zombies_Killed"), x, z, 1,1,1,1, UIFont.Small);
	self:drawText(self.char:getZombieKills() .. "", x + 10, z, 1,1,1,0.5, UIFont.Small);
	z = z + smallFontHgt;
	--[[
	self:drawTextRight(getText("IGUI_char_Survivor_Killed"), x, z, 1,1,1,1, UIFont.Small);
	self:drawText("0", x + 10, z, 1,1,1,0.5, UIFont.Small);
	z = z + smallFontHgt + 6;
	self:drawRect(30, z, self.width - 60, 1, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
	z = z + 6;
	]]--
	if instanceof(self.char, 'IsoPlayer') and self.char:getInventory():contains("DigitalWatch2") then
		self:drawTextRight(getText("IGUI_char_Survived_For"), x, z, 1,1,1,1, UIFont.Small);
		self:drawText(self.char:getTimeSurvived(), x + 10, z, 1,1,1,0.5, UIFont.Small);
	end
	
	z = z + smallFontHgt + 10

	self:drawAvatar();

	self:setHeightAndParentHeight(z)
end

function ISCharacterScreen:create()
--~ 	if self.avatar == nil then
--~ 		self:createAvatar();
--~ 	end

--~ 	self.avatarPanel = ISPanel:new(30, 80, 96, 96);
--~ 	self.avatarPanel:initialise();

--~ 	self:addChild(self.avatarPanel);
--~ 	self.avatarPanel.backgroundColor = {r=0, g=0, b=0, a=0.8};
--~ 	self.avatarPanel.borderColor = {r=1, g=1, b=1, a=0.2};
--~ 	self.avatarPanel.render = ISCharacterScreen.drawAvatar;

	self.sexText = getText("IGUI_char_Male");
	if self.char:getDescriptor():isFemale() then
		self.sexText = getText("IGUI_char_Female");
	end

	ISCharacterScreen.loadTraits(self);

	ISCharacterScreen.loadProfession(self);

	local texSize = 64
	self.profImage = ISImage:new(self:getWidth() - texSize - 20, 25, texSize, texSize, nil);
	self.profImage:initialise();
	self:addChild(self.profImage);

    self.xOffset = 180;
    if 130 + getTextManager():MeasureStringX(UIFont.Small,getText("IGUI_char_Age")) > self.xOffset then
        self.xOffset = 130 + getTextManager():MeasureStringX(UIFont.Small,getText("IGUI_char_Age"));
    end
    if 130 + getTextManager():MeasureStringX(UIFont.Small,getText("IGUI_char_Sex")) > self.xOffset then
        self.xOffset = 130 + getTextManager():MeasureStringX(UIFont.Small,getText("IGUI_char_Sex"));
    end
    if 130 + getTextManager():MeasureStringX(UIFont.Small,getText("IGUI_char_Traits")) > self.xOffset then
        self.xOffset = 130 + getTextManager():MeasureStringX(UIFont.Small,getText("IGUI_char_Traits"));
    end
end

ISCharacterScreen.traitsChanged = function(self)
	local traits = self.char:getTraits()
	if traits:size() ~= #self.traits then
		return true
	end
	for i=1,traits:size() do
		local trait = TraitFactory.getTrait(traits:get(i-1))
		if trait:getTexture() ~= self.traits[i].texture then
			return true
		end
	end
	return false
end

ISCharacterScreen.loadTraits = function(self)
	for _,image in ipairs(self.traits) do
		self:removeChild(image)
	end
	self.traits = {};
	for i=0, self.char:getTraits():size() - 1 do
		local trait = TraitFactory.getTrait(self.char:getTraits():get(i));
		if trait and trait:getTexture() then
			local textImage = ISImage:new(0, 0, trait:getTexture():getWidthOrig(), trait:getTexture():getHeightOrig(), trait:getTexture());
			textImage:initialise();
			textImage:setMouseOverText(trait:getLabel());
            textImage:setVisible(false);
			self:addChild(textImage);
			table.insert(self.traits, textImage);
		end
	end
	self.Strength = self.char:getPerkLevel(Perks.Strength)
	self.Fitness = self.char:getPerkLevel(Perks.Fitness)
end

ISCharacterScreen.loadProfession = function(self)
	self.professionTexture = nil;
	self.profession = nil;
	if self.char:getDescriptor() and self.char:getDescriptor():getProfession() then
		local prof = ProfessionFactory.getProfession(self.char:getDescriptor():getProfession());
		if prof then
			self.profession = prof:getName();
			self.professionTexture = prof:getTexture();
		end
	end
end

ISCharacterScreen.loadFavouriteWeapon = function(self)
	self.favouriteWeapon = nil;
	local swing = 0;
	for iPData,vPData in pairs(self.char:getModData()) do
		for index in string.gmatch(iPData, "^Fav:(.+)") do
			if vPData > swing then
				self.favouriteWeapon = index;
				swing = vPData;
			end
		end
	end
end

function ISCharacterScreen:drawAvatar()
	local x = self:getAbsoluteX();
	local y = self:getAbsoluteY();
	x = x + 96/2 + 22;
	y = y + 190;


	-- we recreate the survivor display every tick, so if you changed clothes, you'll see it
	if not self.avatar then
		self.avatar = IsoSurvivor.new(self.char:getDescriptor(), nil, 0, 0, 0);
		self.avatar:setDir(IsoDirections.SE);
		self.avatar:PlayAnimWithSpeed("Idle", 0.1);
	end

	self.avatar:drawAt(x,y);
end

function ISCharacterScreen:onJoypadDown(button)
	if button == Joypad.BButton then
		getPlayerInfoPanel(self.playerNum):toggleView(xpSystemText.info)
		setJoypadFocus(self.playerNum, nil)
	end
    if button == Joypad.LBumper then
        getPlayerInfoPanel(self.playerNum):onJoypadDown(button)
    end
    if button == Joypad.RBumper then
        getPlayerInfoPanel(self.playerNum):onJoypadDown(button)
    end
end

function ISCharacterScreen:new(x, y, width, height, playerNum)
	local o = {};
	o = ISPanelJoypad:new(x, y, width, height);
	o:noBackground();
	setmetatable(o, self);
    self.__index = self;
    o.playerNum = playerNum
	o.char = getSpecificPlayer(playerNum);
	o.avatar = IsoSurvivor.new(o.char:getDescriptor(), nil, 0, 0, 0);
	o.avatar:setDir(IsoDirections.SE);
	o.avatar:PlayAnimWithSpeed("Idle", 0.1);
	o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
	o.backgroundColor = {r=0, g=0, b=0, a=0.8};
	o.weightIncTexture = getTexture("media/ui/chevron_up.png")
	o.weightIncLotTexture = getTexture("media/ui/chevron_double.png")
	o.weightDecTexture = getTexture("media/ui/chevron_down.png")
	o.traits = {}
   return o;
end
