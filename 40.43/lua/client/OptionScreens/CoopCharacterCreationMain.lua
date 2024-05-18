--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "OptionScreens/CharacterCreationMain"

CoopCharacterCreationMain = CharacterCreationMain:derive("CoopCharacterCreationMain")

function CoopCharacterCreationMain:onOptionMouseDown(button, x, y)
	if button.internal == "BACK" then
		self:setVisible(false)
		if CoopMapSpawnSelect.instance:hasChoices() then
			CoopMapSpawnSelect.instance:setVisible(true, self.joyfocus)
			return
		end
		if CoopUserName.instance:shouldShow() then
			CoopUserName.instance:setVisible(true, self.joyfocus)
			return
		end
		CoopCharacterCreation.instance:cancel()
	end
	if button.internal == "NEXT" then
		self:setVisible(false)
--		self:removeChild(CharacterCreationHeader.instance)
--		CharacterCreationProfession.instance:addChild(CharacterCreationHeader.instance)
		CharacterCreationProfession.instance:setVisible(true, self.joyfocus)
	end
	if button.internal == "RANDOM" then
		CharacterCreationHeader.instance:onOptionMouseDown(button, x, y)
	end
end

function CoopCharacterCreationMain:new(x, y, width, height)
	local o = CharacterCreationMain:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
	o:setUIName("CoopCharacterCreationMain")
	return o
end
