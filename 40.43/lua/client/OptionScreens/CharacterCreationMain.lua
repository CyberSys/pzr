--***********************************************************
--**                   ROBERT JOHNSON                      **
--***********************************************************

require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

CharacterCreationMain = ISPanelJoypad:derive("CharacterCreationMain");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
local FONT_HGT_MEDIUM = getTextManager():getFontHeight(UIFont.Medium)

function CharacterCreationMain:initialise()
    ISPanelJoypad.initialise(self);
end
--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function CharacterCreationMain:instantiate()

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
	self:createChildren();
end

function CharacterCreationMain:create()
	self.maletex = getTexture("media/ui/maleicon.png");
	self.femaletex = getTexture("media/ui/femaleicon.png");
	local w = self.width * 0.5;
	if(w < 768) then
		w = 768;
	end
	local tw = self.width;
	local mainPanelY = 48
	local mainPanelPadBottom = 80
	if getCore():getScreenHeight() <= 600 then
		mainPanelPadBottom = 16
	end
	self.mainPanel = ISPanel:new((tw-w)/2, 48, w, self.height - mainPanelPadBottom - mainPanelY);
	self.mainPanel.backgroundColor = {r=0, g=0, b=0, a=0.8};
	self.mainPanel.borderColor = {r=1, g=1, b=1, a=0.5};

	self.mainPanel:initialise();
	self.mainPanel:setAnchorRight(true);
	self.mainPanel:setAnchorLeft(true);
	self.mainPanel:setAnchorBottom(true);
	self.mainPanel:setAnchorTop(true);
	self:addChild(self.mainPanel);

	MainScreen.instance.desc = SurvivorFactory.CreateSurvivor();

	-- we add the header (contain forename/surname/avatar/...)
	MainScreen.instance.charCreationHeader:setX(self.mainPanel:getX());
	MainScreen.instance.charCreationHeader:setY(self.mainPanel:getY());
	MainScreen.instance.charCreationHeader:setAnchorRight(true);
	MainScreen.instance.charCreationHeader:setAnchorLeft(true);
	MainScreen.instance.charCreationHeader:setAnchorBottom(false);
	MainScreen.instance.charCreationHeader:setAnchorTop(true);

	self:addChild(MainScreen.instance.charCreationHeader);

	-- HAIR TYPE SELECTION
	self.yOffset = 64+96+6+40;
	self:createChestTypeBtn();
	self:createHairTypeBtn();
	self:createBeardTypeBtn();

	-- CLOTHING
	self.yOffset = 64+96+6+40;
	self:createClothingBtn();

	local buttonHgt = FONT_HGT_SMALL + 3 * 2

	-- BOTTOM BUTTON
	self.backButton = ISButton:new(16, self.mainPanel.height - 5 - buttonHgt, 100, buttonHgt, getText("UI_btn_back"), self, self.onOptionMouseDown);
	self.backButton.internal = "BACK";
	self.backButton:initialise();
	self.backButton:instantiate();
	self.backButton:setAnchorLeft(true);
	self.backButton:setAnchorTop(false);
	self.backButton:setAnchorBottom(true);
	self.backButton.borderColor = {r=1, g=1, b=1, a=0.1};
	self.backButton.setJoypadFocused = self.setJoypadFocusedBButton
	self.mainPanel:addChild(self.backButton);

	self.playButton = ISButton:new(self.mainPanel.width - 116, self.mainPanel.height - 5 - buttonHgt, 100, buttonHgt, getText("UI_btn_next"), self, self.onOptionMouseDown);
	self.playButton.internal = "NEXT";
	self.playButton:initialise();
	self.playButton:instantiate();
	self.playButton:setAnchorLeft(false);
	self.playButton:setAnchorRight(true);
	self.playButton:setAnchorTop(false);
	self.playButton:setAnchorBottom(true);
	self.playButton:setEnable(true); -- sets the hard-coded border color
	self.playButton.setJoypadFocused = self.setJoypadFocusedAButton
	self.mainPanel:addChild(self.playButton);

	local textWid = getTextManager():MeasureStringX(UIFont.Small, getText("UI_characreation_random"))
	local buttonWid = math.max(100, textWid + 8 * 2)
    self.randomButton = ISButton:new(self.playButton:getX() - 10 - buttonWid, self.playButton:getY(), buttonWid, buttonHgt, getText("UI_characreation_random"), self, self.onOptionMouseDown);
	self.randomButton.internal = "RANDOM";
    self.randomButton:initialise();
    self.randomButton:instantiate();
    self.randomButton:setAnchorLeft(false);
    self.randomButton:setAnchorRight(true);
    self.randomButton:setAnchorTop(false);
    self.randomButton:setAnchorBottom(true);
    self.randomButton.borderColor = { r = 1, g = 1, b = 1, a = 0.1 };
	self.backButton.setJoypadFocused = self.setJoypadFocusedYButton
    self.mainPanel:addChild(self.randomButton);

	-- DISABLE BUTTON
	self:disableBtn();
end

function CharacterCreationMain:createChestTypeBtn()
	local comboHgt = FONT_HGT_SMALL + 3 * 2
	
	local lbl = ISLabel:new(64, self.yOffset, FONT_HGT_MEDIUM, getText("UI_characreation_body"), 1, 1, 1, 1, UIFont.Medium, true);
	lbl:initialise();
	lbl:instantiate();
	self.mainPanel:addChild(lbl);

	local rect = ISRect:new(64, self.yOffset + FONT_HGT_MEDIUM + 5, self.mainPanel:getWidth() - 110, 1, 1, 0.3, 0.3, 0.3);
	rect:initialise();
	rect:instantiate();
	self.mainPanel:addChild(rect);

	self.yOffset = self.yOffset + FONT_HGT_MEDIUM + 15;

	-------------
	-- SKIN COLOR 
	-------------
	self.skinColorLbl = ISLabel:new(64+70, self.yOffset, FONT_HGT_SMALL, getText("UI_SkinColor"), 1, 1, 1, 1, UIFont.Small);
	self.skinColorLbl:initialise();
	self.skinColorLbl:instantiate();
	self.mainPanel:addChild(self.skinColorLbl);

	local xColor = 90;
	local skinColors = { ColorInfo.new(0.8313725590705872,0.6705882549285889,0.2705882489681244,1),
						 ColorInfo.new(0.4156862795352936,0.18039216101169586,0.07058823853731155,1) }
	self.skinColorButtons = {}
	self.skinColor = 1
	for n,color in ipairs(skinColors) do
		local skinColorBtn = ISButton:new(64+xColor, self.yOffset + (FONT_HGT_SMALL - 15) / 2, 15, 15, "", self, CharacterCreationMain.onSkinColorSelected);
		skinColorBtn.internal = n;
		skinColorBtn:initialise();
		skinColorBtn:instantiate();
		color:desaturate(0.5);
		skinColorBtn.backgroundColor = {r = color:getR(), g = color:getG(), b = color:getB(), a = 1};
		self.mainPanel:addChild(skinColorBtn);
		table.insert(self.skinColorButtons, skinColorBtn);
		xColor = xColor + 20;
	end
	self.yOffset = self.yOffset + FONT_HGT_SMALL + 5 + 4;

	-------------
	-- CHEST HAIR
	-------------
	self.chestHairLbl = ISLabel:new(64+70, self.yOffset, comboHgt, getText("UI_ChestHair"), 1, 1, 1, 1, UIFont.Small);
	self.chestHairLbl:initialise();
	self.chestHairLbl:instantiate();
	self.mainPanel:addChild(self.chestHairLbl);

	self.chestHairCombo = ISComboBox:new(64+90, self.yOffset, 100, comboHgt, self, CharacterCreationMain.onChestHairSelected);
	self.chestHairCombo:initialise();
--	self.chestHairCombo:instantiate();
	self.chestHairCombo:addOption(getText("UI_Yes"))
	self.chestHairCombo:addOption(getText("UI_No"))
	self.mainPanel:addChild(self.chestHairCombo)
	self.yOffset = self.yOffset + comboHgt + 4;

	----------------------
	-- HEAD/FACIAL STUBBLE
	----------------------
	self.shavedHairLbl = ISLabel:new(64+70, self.yOffset, comboHgt, getText("UI_Stubble"), 1, 1, 1, 1, UIFont.Small);
	self.shavedHairLbl:initialise();
	self.shavedHairLbl:instantiate();
	self.mainPanel:addChild(self.shavedHairLbl);

	self.shavedHairCombo = ISComboBox:new(64+90, self.yOffset, 100, comboHgt, self, CharacterCreationMain.onShavedHairSelected);
	self.shavedHairCombo:initialise();
--	self.shavedHairCombo:instantiate();
	self.shavedHairCombo:addOption(getText("UI_Yes"))
	self.shavedHairCombo:addOption(getText("UI_No"))
	self.mainPanel:addChild(self.shavedHairCombo)
	self.yOffset = self.yOffset + comboHgt + 10;
end


function CharacterCreationMain:createHairTypeBtn()
	local comboHgt = FONT_HGT_SMALL + 3 * 2

	local lbl = ISLabel:new(64, self.yOffset, FONT_HGT_MEDIUM, getText("UI_characreation_hair"), 1, 1, 1, 1, UIFont.Medium, true);
	lbl:initialise();
	lbl:instantiate();
	self.mainPanel:addChild(lbl);

	local rect = ISRect:new(64, self.yOffset + FONT_HGT_MEDIUM + 5, self.mainPanel:getWidth() - 110, 1, 1, 0.3, 0.3, 0.3);
	rect:setAnchorRight(true);
	rect:initialise();
	rect:instantiate();
	self.mainPanel:addChild(rect);

	self.yOffset = self.yOffset + FONT_HGT_MEDIUM + 15;

	self.hairTypeLbl = ISLabel:new(64+70, self.yOffset, comboHgt, getText("UI_characreation_hairtype"), 1, 1, 1, 1, UIFont.Small);
	self.hairTypeLbl:initialise();
	self.hairTypeLbl:instantiate();

	self.mainPanel:addChild(self.hairTypeLbl);

	self.hairTypeCombo = ISComboBox:new(64+90, self.yOffset, 100, comboHgt, self, CharacterCreationMain.onHairTypeSelected);
	self.hairTypeCombo:initialise();
--	self.hairTypeCombo:instantiate();
	self.hairTypeCombo:addOption(getText("UI_HairBaldspot"))
	self.hairTypeCombo:addOption(getText("UI_HairPicard"))
	self.hairTypeCombo:addOption(getText("UI_HairRecede"))
	self.hairTypeCombo:addOption(getText("UI_HairShort"))
	self.hairTypeCombo:addOption(getText("UI_HairMessy"))
	self.hairTypeCombo:addOption(getText("UI_HairNone"))
	self.mainPanel:addChild(self.hairTypeCombo)

	self.hairType = 0

	self.yOffset = self.yOffset + comboHgt + 4;

	self.hairColorLbl = ISLabel:new(64+70, self.yOffset, FONT_HGT_SMALL, getText("UI_characreation_color"), 1, 1, 1, 1, UIFont.Small);
	self.hairColorLbl:initialise();
	self.hairColorLbl:instantiate();

	self.mainPanel:addChild(self.hairColorLbl);

	local xColor = 90;
	local fontHgt = getTextManager():getFontHeight(self.hairColorLbl.font)

	local hairColors = MainScreen.instance.desc:getCommonHairColor();
	for i=0,hairColors:size() - 1 do
		local color = hairColors:get(i);
		local hairColorBtn = ISButton:new(64+xColor, self.yOffset + (fontHgt - 15) / 2, 15, 15, "", self, CharacterCreationMain.onHairColorMouseDown);
		hairColorBtn.internal = color;
		hairColorBtn:initialise();
		hairColorBtn:instantiate();
		-- we create a new info color to desaturate it (like in the game)
		local info = ColorInfo.new(color:getRedFloat(),color:getGreenFloat(),color:getBlueFloat(), 1);
		info:desaturate(0.5);
		hairColorBtn.backgroundColor = {r=info:getR(), g=info:getG(), b=info:getB(), a=1};
		self.mainPanel:addChild(hairColorBtn);
		table.insert(self.colorPanel, hairColorBtn);
		xColor = xColor + 20;
        table.insert(self.hairColorButtons, hairColorBtn);
	end

	self.yOffset = self.yOffset + comboHgt + 10;
end

function CharacterCreationMain:createBeardTypeBtn()
	local comboHgt = FONT_HGT_SMALL + 3 * 2

	self.beardLbl = ISLabel:new(64, self.yOffset, FONT_HGT_MEDIUM, getText("UI_characreation_beard"), 1, 1, 1, 1, UIFont.Medium, true);
	self.beardLbl:initialise();
	self.beardLbl:instantiate();
	self.mainPanel:addChild(self.beardLbl);

	self.beardRect = ISRect:new(64, self.yOffset + FONT_HGT_MEDIUM + 5, self.mainPanel:getWidth() - 110, 1, 1, 0.3, 0.3, 0.3);
	self.beardRect:setAnchorRight(true);
	self.beardRect:initialise();
	self.beardRect:instantiate();
	self.mainPanel:addChild(self.beardRect);

	self.yOffset = self.yOffset + FONT_HGT_MEDIUM + 15;

	self.beardTypeLbl = ISLabel:new(64+ 70, self.yOffset, comboHgt, getText("UI_characreation_beardtype"), 1, 1, 1, 1, UIFont.Small);
	self.beardTypeLbl:initialise();
	self.beardTypeLbl:instantiate();

	self.mainPanel:addChild(self.beardTypeLbl);

	self.beardTypeCombo = ISComboBox:new(64+90, self.yOffset, 100, comboHgt, self, CharacterCreationMain.onBeardTypeSelected);
	self.beardTypeCombo:initialise();
--	self.beardTypeCombo:instantiate();
	self.mainPanel:addChild(self.beardTypeCombo)

	self.yOffset = self.yOffset + comboHgt + 10;
end

function CharacterCreationMain:createClothingBtn()
	local comboHgt = FONT_HGT_SMALL + 3 * 2

	local x = self.mainPanel:getWidth() / 2
	
	self.clothingLbl = ISLabel:new(x, self.yOffset, FONT_HGT_MEDIUM, getText("UI_characreation_clothing"), 1, 1, 1, 1, UIFont.Medium, true);
	self.clothingLbl:initialise();
	self.mainPanel:addChild(self.clothingLbl);

	self.yOffset = self.yOffset + FONT_HGT_MEDIUM + 15;

	self.clothingTopLbl = ISLabel:new(x + 70, self.yOffset, comboHgt, getText("UI_characreation_clothing_top"), 1, 1, 1, 1, UIFont.Small);
	self.clothingTopLbl:initialise();
	self.mainPanel:addChild(self.clothingTopLbl);

	self.clothingTopCombo = ISComboBox:new(x + 90, self.yOffset, 100, comboHgt, self, CharacterCreationMain.onClothingTopSelected);
	self.clothingTopCombo:initialise();
	self.mainPanel:addChild(self.clothingTopCombo)

	local fontHgt = getTextManager():getFontHeight(self.skinColorLbl.font)
	local button = ISButton:new(x + 90 + 100 + 20, self.yOffset + (fontHgt - 15) / 2, 15, 15, "", self, CharacterCreationMain.onClothingTopColorClicked);
	button.internal = color;
	button:initialise();
	button.backgroundColor = {r = 1, g = 1, b = 1, a = 1};
	self.mainPanel:addChild(button);
	self.clothingTopColorBtn = button

	self.yOffset = self.yOffset + comboHgt + 4;

	self.clothingBottomLbl = ISLabel:new(x + 70, self.yOffset, comboHgt, getText("UI_characreation_clothing_bottom"), 1, 1, 1, 1, UIFont.Small);
	self.clothingBottomLbl:initialise();
	self.mainPanel:addChild(self.clothingBottomLbl);

	self.clothingBottomCombo = ISComboBox:new(x + 90, self.yOffset, 100, comboHgt, self, CharacterCreationMain.onClothingBottomSelected);
	self.clothingBottomCombo:initialise();
	self.mainPanel:addChild(self.clothingBottomCombo)

	button = ISButton:new(x + 90 + 100 + 20, self.yOffset + (fontHgt - 15) / 2, 15, 15, "", self, CharacterCreationMain.onClothingBottomColorClicked);
	button.internal = color;
	button:initialise();
	button.backgroundColor = {r = 1, g = 1, b = 1, a = 1};
	self.mainPanel:addChild(button);
	self.clothingBottomColorBtn = button

	self.yOffset = self.yOffset + comboHgt + 4;

	self.clothingFeetLbl = ISLabel:new(x + 70, self.yOffset, comboHgt, getText("UI_characreation_clothing_footwear"), 1, 1, 1, 1, UIFont.Small);
	self.clothingFeetLbl:initialise();
	self.mainPanel:addChild(self.clothingFeetLbl);

	self.clothingFeetCombo = ISComboBox:new(x + 90, self.yOffset, 100, comboHgt, self, CharacterCreationMain.onClothingFootwearSelected);
	self.clothingFeetCombo:initialise();
	self.clothingFeetCombo:addOption(getText("UI_characreation_clothing_none"))
	self.clothingFeetCombo:addOption(getItemText("Shoes"))
	self.mainPanel:addChild(self.clothingFeetCombo);

	self.colorPicker = ISColorPicker:new(0, 0)
	self.colorPicker:initialise()
	self.colorPicker.pickedTarget = self
	self.colorPicker.resetFocusTo = self
--	self.mainPanel:addChild(colorPicker)
end

function CharacterCreationMain:disableBtn()
	-- hair color btn disable
	for i,v in pairs(self.colorPanel) do
		if v.internal == self.hairColor then
			v:setEnable(false);
		else
			v:setEnable(true);
		end
	end

	local desc = MainScreen.instance.desc
	local visible = not desc:isFemale()
	self.chestHairLbl:setVisible(visible)
	self.chestHairCombo:setVisible(visible)
	self.shavedHairLbl:setVisible(visible)
	self.shavedHairCombo:setVisible(visible)
	self.beardTypeLbl:setVisible(visible)
	self.beardTypeCombo:setVisible(visible)
	self.beardRect:setVisible(visible)
	self.beardLbl:setVisible(visible)

	-- Changing male <-> female, update combobox choices.
	if self.female ~= desc:isFemale() then
		self.female = desc:isFemale()
		
		self.hairTypeCombo.options = {}
		if desc:isFemale() then
			self.hairTypeCombo:addOption(getText("UI_HairShort") .. " 1")
			self.hairTypeCombo:addOption(getText("UI_HairLong") .. " 1")
			self.hairTypeCombo:addOption(getText("UI_HairLong") .. " 2")
			self.hairTypeCombo:addOption(getText("UI_HairShort") .. " 2")
			self.hairTypeCombo:addOption(getText("UI_HairLong") .. " 3")
		else
			self.hairTypeCombo:addOption(getText("UI_HairBaldspot"))
			self.hairTypeCombo:addOption(getText("UI_HairPicard"))
			self.hairTypeCombo:addOption(getText("UI_HairRecede"))
			self.hairTypeCombo:addOption(getText("UI_HairShort"))
			self.hairTypeCombo:addOption(getText("UI_HairMessy"))
			self.hairTypeCombo:addOption(getText("UI_HairNone"))
		end

		self.beardTypeCombo.options = {}
		if desc:isFemale() then
			-- no bearded ladies
		else
			self.beardTypeCombo:addOption(getText("UI_BeardFull"))
			self.beardTypeCombo:addOption(getText("UI_BeardChops"))
			self.beardTypeCombo:addOption(getText("UI_BeardNoMoustache"))
			self.beardTypeCombo:addOption(getText("UI_BeardGoatee"))
			self.beardTypeCombo:addOption(getText("UI_HairNone"))
		end

		self.clothingTopCombo.options = {}
		self.clothingTopCombo:addOption(getText("UI_characreation_clothing_none"))
		if desc:isFemale() then
			self.clothingTopCombo:addOption(getItemText("Blouse"))
			self.clothingTopCombo:addOption(getItemText("Vest"))
		else
			self.clothingTopCombo:addOption(getItemText("Sweater"))
			self.clothingTopCombo:addOption(getItemText("Vest"))
		end

		self.clothingBottomCombo.options = {}
		self.clothingBottomCombo:addOption(getText("UI_characreation_clothing_none"))
		if desc:isFemale() then
			self.clothingBottomCombo:addOption(getItemText("Skirt"))
			self.clothingBottomCombo:addOption(getItemText("Pants"))
		else
			self.clothingBottomCombo:addOption(getItemText("Pants"))
		end
	end

	self:syncUIWithTorso()
	self.hairTypeCombo.selected = self.hairType + 1

	local beard = desc:getBeardNoColor()
	if beard and beard ~= "" then
		self.beardTypeCombo.selected = desc:getBeardNumber() + 1
	else
		self.beardTypeCombo.selected = 5
	end

	for i,v in ipairs(self.skinColorButtons) do
		v:setEnable(i ~= self.skinColor)
	end

	if not desc:getTop() or desc:getTop() == "" then
		self.clothingTopCombo.selected = 1
	elseif desc:getTop() == "Blouse" or desc:getTop() == "Shirt" then
		self.clothingTopCombo.selected = 2
	elseif desc:getTop() == "Vest" then
		self.clothingTopCombo.selected = 3
	end
	local col = desc:getTopColor()
--	col:desaturate(0.5)
	self.clothingTopColorBtn.backgroundColor = { r = col:getR(), g = col:getG(), b = col:getB(), a = 1 }

	if not desc:getBottoms() or desc:getBottoms() == "" then
		self.clothingBottomCombo.selected = 1
	elseif desc:getBottoms() == "Skirt" then
		self.clothingBottomCombo.selected = 2
	elseif desc:getBottoms() == "Trousers" then
		self.clothingBottomCombo.selected = desc:isFemale() and 3 or 2
	end
	local col = desc:getTrouserColor()
--	col:desaturate(0.5)
	self.clothingBottomColorBtn.backgroundColor = { r = col:getR(), g = col:getG(), b = col:getB(), a = 1 }

	if not desc:getShoes() or desc:getShoes() == "" then
		self.clothingFeetCombo.selected = 1
	else
		self.clothingFeetCombo.selected = 2
	end

	if MainScreen.instance.avatar then
		MainScreen.instance.avatar:PlayAnimWithSpeed("Run", 0.3);
	end
end

function CharacterCreationMain:getBeard()
	if MainScreen.instance.desc:getExtras() then
		for i=0,MainScreen.instance.desc:getExtras():size() - 1 do
			local extra = MainScreen.instance.desc:getExtras():get(i);
			if luautils.stringStarts(extra, "Beard") then
				return extra;
			end
		end
	end
	return nil;
end

function CharacterCreationMain:onHairColorMouseDown(button, x, y)
	self.hairColor = button.internal;
	MainScreen.instance.desc:setHairColor(button.internal);
	MainScreen.instance.avatar:reloadSpritePart();
	self:disableBtn();
end

function CharacterCreationMain:syncTorsoWithUI()
	local torsoNum = self.skinColor - 1
	if not MainScreen.instance.desc:isFemale() then
		-- white white+chest white+stubble white+chest+stubble
		-- black black+chest black+stubble black+chest+stubble
		torsoNum = torsoNum * 4
		if self.shavedHairCombo.selected == 1 then
			torsoNum = torsoNum + 2
		end
		if self.chestHairCombo.selected == 1 then
			torsoNum = torsoNum + 1
		end
	end
	MainScreen.instance.desc:setTorsoNumber(torsoNum)
	SurvivorFactory.setTorso(MainScreen.instance.desc)
	MainScreen.instance.avatar:reloadSpritePart()
	self:disableBtn()
end

function CharacterCreationMain:syncUIWithTorso()
	local torsoNum = MainScreen.instance.desc:getTorsoNumber()
	if MainScreen.instance.desc:isFemale() then
		self.skinColor = torsoNum + 1
	else
		if torsoNum < 4 then self.skinColor = 1 else self.skinColor = 2 end
		if torsoNum == 1 or torsoNum == 3 or torsoNum == 5 or torsoNum == 7 then
			self.chestHairCombo.selected = 1 -- Yes
		else
			self.chestHairCombo.selected = 2 -- No
		end
		if torsoNum == 2 or torsoNum == 3 or torsoNum == 6 or torsoNum == 7 then
			self.shavedHairCombo.selected = 1 -- Yes
		else
			self.shavedHairCombo.selected = 2 -- No
		end
	end
end

function CharacterCreationMain:onChestHairSelected(combo)
	self:syncTorsoWithUI()
end

function CharacterCreationMain:onShavedHairSelected(combo)
	self:syncTorsoWithUI()
end

function CharacterCreationMain:onSkinColorSelected(button, x, y)
	self.skinColor = button.internal
	self:syncTorsoWithUI()
end

function CharacterCreationMain:onHairTypeSelected(combo)
	self.hairType = combo.selected - 1
	MainScreen.instance.desc:setHairNumber(self.hairType)
	SurvivorFactory.setHairNoColor(MainScreen.instance.desc)
	local hair = MainScreen.instance.desc:getHairNoColor()
	if hair ~= "none" then
		hair = hair .. "White"
	end
	MainScreen.instance.desc:setHair(hair)
	MainScreen.instance.avatar:reloadSpritePart()
	self:disableBtn()
end

function CharacterCreationMain:onBeardTypeSelected(combo)
	MainScreen.instance.desc:setBeardNumber(combo.selected - 1)
	SurvivorFactory.setBeardNoColor(MainScreen.instance.desc)
	MainScreen.instance.desc:getExtras():clear()
	local beard = MainScreen.instance.desc:getBeardNoColor()
	if beard and beard ~= "" then
		beard = beard .. "White"
		MainScreen.instance.desc:getExtras():add(beard)
	end
	MainScreen.instance.avatar:reloadSpritePart()
	self:disableBtn()
end

function CharacterCreationMain:onClothingTopSelected(combo)
	local desc = MainScreen.instance.desc
	local choices = {}
	if desc:isFemale() then
		choices = { "", "Blouse", "Vest" }
	else
		choices = { "", "Shirt", "Vest" }
	end
	local top = choices[combo.selected]
	local topPal = nil
	if top == "" then
		top = nil
	else
		topPal = top .. "_White"
	end
	desc:setTop(top)
	desc:setToppal(topPal)

	local avatar = MainScreen.instance.avatar
	avatar:reloadSpritePart()
	avatar:setDir(CharacterCreationHeader.instance.direction)
	avatar:PlayAnimWithSpeed("Run", 0.3)
end

function CharacterCreationMain:onClothingBottomSelected(combo)
	local desc = MainScreen.instance.desc
	local choices = {}
	if desc:isFemale() then
		choices = { "", "Skirt", "Trousers" }
	else
		choices = { "", "Trousers" }
	end
	local bottom = choices[combo.selected]
	local bottomPal = nil
	if bottom == "" then
		bottom = nil
	else
		bottomPal = bottom .. "_White"
	end
	desc:setBottoms(bottom)
	desc:setBottomspal(bottomPal)

	local avatar = MainScreen.instance.avatar
	avatar:reloadSpritePart()
	avatar:setDir(CharacterCreationHeader.instance.direction)
	avatar:PlayAnimWithSpeed("Run", 0.3)
end

function CharacterCreationMain:onClothingFootwearSelected(combo)
	local desc = MainScreen.instance.desc
	if combo.selected == 1 then
		desc:setShoes(nil)
	else
		desc:setShoes("Shoes")
	end

	local avatar = MainScreen.instance.avatar
	avatar:reloadSpritePart()
	avatar:setDir(CharacterCreationHeader.instance.direction)
	avatar:PlayAnimWithSpeed("Run", 0.3)
end

function CharacterCreationMain:onClothingTopColorClicked(button)
	self.colorPicker:setX(button:getX() - self.colorPicker:getWidth())
	self.colorPicker:setY(button:getY() + button:getHeight())
	self.colorPicker.pickedFunc = CharacterCreationMain.onClothingTopColorPicked
	self.colorPicker:setInitialColor(MainScreen.instance.desc:getTopColor())
	self.mainPanel:addChild(self.colorPicker)
	if self.joyfocus then
		self.joyfocus.focus = self.colorPicker
	end
end

function CharacterCreationMain:onClothingBottomColorClicked(button)
	self.colorPicker:setX(button:getX() - self.colorPicker:getWidth())
	self.colorPicker:setY(button:getY() + button:getHeight())
	self.colorPicker.pickedFunc = CharacterCreationMain.onClothingBottomColorPicked
	self.colorPicker:setInitialColor(MainScreen.instance.desc:getTrouserColor())
	self.mainPanel:addChild(self.colorPicker)
	if self.joyfocus then
		self.joyfocus.focus = self.colorPicker
	end
end

function CharacterCreationMain:onClothingTopColorPicked(color, mouseUp)
	self.clothingTopColorBtn.backgroundColor = { r=color.r, g=color.g, b=color.b, a = 1 }

	MainScreen.instance.desc:setTopColor(ColorInfo.new(color.r, color.g, color.b, 1):toColor())

	local avatar = MainScreen.instance.avatar
	avatar:reloadSpriteColors()
end

function CharacterCreationMain:onClothingBottomColorPicked(color, mouseUp)
	self.clothingBottomColorBtn.backgroundColor = { r=color.r, g=color.g, b=color.b, a = 1 }

	MainScreen.instance.desc:setTrouserColor(ColorInfo.new(color.r, color.g, color.b, 1):toColor())

	local avatar = MainScreen.instance.avatar
	avatar:reloadSpriteColors()
end

function CharacterCreationMain:onOptionMouseDown(button, x, y)
	if button.internal == "BACK" then
		self:setVisible(false)
        if self.previousScreen == "NewGameScreen" then
            self.previousScreen = nil
            NewGameScreen.instance:setVisible(true, self.joyfocus)
            return
        end
        if self.previousScreen == "LoadGameScreen" then
            self.previousScreen = nil
            LoadGameScreen.instance:setSaveGamesList()
            LoadGameScreen.instance:setVisible(true, self.joyfocus)
            return
        end
		if self.previousScreen == "MapSpawnSelect" then
			self.previousScreen = nil
			MapSpawnSelect.instance:setVisible(true, self.joyfocus)
			return
		end
		if self.previousScreen == "WorldSelect" then
			self.previousScreen = nil
			WorldSelect.instance:setVisible(true, self.joyfocus)
			return
		end
		if self.previousScreen == "LastStandPlayerSelect" then
			self.previousScreen = nil
			LastStandPlayerSelect.instance:setVisible(true, self.joyfocus)
			return
		end
		if self.previousScreen == "SandboxOptionsScreen" then
			self.previousScreen = nil
			SandboxOptionsScreen.instance:setVisible(true, self.joyfocus)
			return
		end
		if getWorld():getGameMode() == "Multiplayer" then
			backToSinglePlayer()
			getCore():ResetLua(true, "exitJoinServer")
			return
		end
    end
	if button.internal == "RANDOM" then
		CharacterCreationHeader.instance:onOptionMouseDown(button, x, y)
	end
	if button.internal == "NEXT" then
		MainScreen.instance.charCreationMain:setVisible(false);
--		MainScreen.instance.charCreationMain:removeChild(MainScreen.instance.charCreationHeader);
--		MainScreen.instance.charCreationProfession:addChild(MainScreen.instance.charCreationHeader);
		MainScreen.instance.charCreationProfession:setVisible(true, self.joyfocus);
	end
	self:disableBtn();
end

-- draw the avatar of the player
function CharacterCreationMain:drawAvatar()
	if MainScreen.instance.avatar == nil then
		return;
	end
	local x = self:getAbsoluteX();
	local y = self:getAbsoluteY();
	x = x + 96/2;
	y = y + 165;

	MainScreen.instance.avatar:drawAt(x,y);
end

function CharacterCreationMain:prerender()
	ISPanel.prerender(self);
	self:drawTextCentre(getText("UI_characreation_title"), self.width / 2, 10, 1, 1, 1, 1, UIFont.Large);
	local avatar = MainScreen.instance.avatar
	if avatar ~= nil then
		avatar:getSprite():update(avatar:getSpriteDef())
	end
end

function CharacterCreationMain:onGainJoypadFocus(joypadData)
    print("character creation main gain focus");
    ISPanelJoypad.onGainJoypadFocus(self, joypadData);
    self.playButton:setJoypadButton(getTexture("media/ui/abutton.png"))
    self:setISButtonForB(self.backButton);
    self:setISButtonForY(self.randomButton);
    -- init all the button for the controller
    self:loadJoypadButtons();
    self:clearJoypadFocus(JoypadState[1])
    self.joypadIndex = 1;
    self.joypadButtons = self.joypadButtonsY[#self.joypadButtonsY];
    self.joypadIndexY = #self.joypadButtonsY;
    self.playButton:setJoypadFocused(true);
end

function CharacterCreationMain:onLoseJoypadFocus(joypadData)
	self.playButton.isJoypad = false
	self.ISButtonB = nil
	self.backButton.isJoypad = false
	self.ISButtonY = nil
	self.randomButton.isJoypad = false
	ISPanelJoypad.onLoseJoypadFocus(joypadData)
end

function CharacterCreationMain:setJoypadFocusedAButton(focused)
	ISButton.setJoypadFocused(self, focused)
	CharacterCreationMain.instance.ISButtonA = focused and self or nil
	self.isJoypad = focused
end

function CharacterCreationMain:setJoypadFocusedBButton(focused)
	ISButton.setJoypadFocused(self, focused)
	CharacterCreationMain.instance.ISButtonB = focused and self or nil
	self.isJoypad = focused
end

function CharacterCreationMain:setJoypadFocusedYButton(focused)
	ISButton.setJoypadFocused(self, focused)
	CharacterCreationMain.instance.ISButtonY = focused and self or nil
	self.isJoypad = focused
end

function CharacterCreationMain:loadJoypadButtons()
    self:clearJoypadFocus(JoypadState[1])
    self.joypadButtonsY = {};
    local sexButton = self:insertNewLineOfButtons(MainScreen.instance.charCreationHeader.femaleButton, MainScreen.instance.charCreationHeader.maleButton);
--    self:insertNewLineOfButtons(MainScreen.instance.charCreationHeader.randomButton);
    local buttons = {}
    for _,button in ipairs(self.skinColorButtons) do table.insert(buttons, button) end
    table.insert(buttons, self.clothingTopCombo)
    table.insert(buttons, self.clothingTopColorBtn)
    self:insertNewListOfButtons(buttons)
    if MainScreen.instance.desc:isFemale() then
        self:insertNewLineOfButtons(self.clothingBottomCombo, self.clothingBottomColorBtn)
        self:insertNewLineOfButtons(self.clothingFeetCombo)
    else
        self:insertNewLineOfButtons(self.chestHairCombo, self.clothingBottomCombo, self.clothingBottomColorBtn)
        self:insertNewLineOfButtons(self.shavedHairCombo, self.clothingFeetCombo)
    end
    self:insertNewLineOfButtons(self.hairTypeCombo);
    self:insertNewListOfButtons(self.hairColorButtons);
    if not MainScreen.instance.desc:isFemale() then
        self:insertNewLineOfButtons(self.beardTypeCombo);
    end
    self:insertNewLineOfButtons(self.playButton);
    self.joypadButtons = sexButton;
    self.joypadIndex = (MainScreen.instance.desc:isFemale() and 2) or 1
    self.joypadIndexY = 1
    self.joypadButtons[self.joypadIndex]:setJoypadFocused(true, JoypadState[1])
end

function CharacterCreationMain:onResolutionChange(oldw, oldh, neww, newh)
	local w = self.width * 0.5;
	if (w < 768) then
		w = 768;
	end
	local tw = self.width;
	local mainPanelY = 48
	local mainPanelPadBottom = 80
	if getCore():getScreenHeight() <= 600 then
		mainPanelPadBottom = 16
	end
	self.mainPanel:setWidth(w)
	self.mainPanel:setHeight(self.height - mainPanelPadBottom - mainPanelY)
	self.mainPanel:setX((tw - w) / 2)
	self.mainPanel:setY(48)
	self.mainPanel:recalcSize()

	MainScreen.instance.charCreationHeader:setX(self.mainPanel:getX());
	MainScreen.instance.charCreationHeader:setY(self.mainPanel:getY());
	MainScreen.instance.charCreationHeader:setWidth(self.mainPanel:getWidth());
end

function CharacterCreationMain:new (x, y, width, height)
	local o = {};
	o = ISPanelJoypad:new(x, y, width, height);
	setmetatable(o, self);
	self.__index = self;
	o.x = 0;
	o.y = 0;
	o.backgroundColor = {r=0, g=0, b=0, a=0.0};
	o.borderColor = {r=1, g=1, b=1, a=0.0};
	o.itemheightoverride = {};
	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	o.colorPanel = {};
    o.rArrow = getTexture("media/ui/ArrowRight.png");
    o.disabledRArrow = getTexture("media/ui/ArrowRight_Disabled.png");
    o.lArrow = getTexture("media/ui/ArrowLeft.png");
    o.disabledLArrow = getTexture("media/ui/ArrowLeft_Disabled.png");
    o.hairColorButtons = {};
	CharacterCreationMain.instance = o;
	return o;
end
