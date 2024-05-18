--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "OptionScreens/CharacterCreationProfession"

CoopCharacterCreationProfession = CharacterCreationProfession:derive("CoopCharacterCreationProfession")

local _onOptionMouseDown = CharacterCreationProfession.onOptionMouseDown
function CoopCharacterCreationProfession:onOptionMouseDown(button, x, y)
	if button.internal == "BACK" then
		self:setVisible(false)
--		self:removeChild(CharacterCreationHeader.instance)
--		CharacterCreationMain.instance:addChild(CharacterCreationHeader.instance)
		CharacterCreationMain.instance:setVisible(true, self.joyfocus)
	end
	if button.internal == "NEXT" then
		if CoopMapSpawnSelect.instance.selectedRegion then
			local spawnRegion = CoopMapSpawnSelect.instance.selectedRegion
			print('using spawn region '..tostring(spawnRegion.name))
			local spawn = spawnRegion.points[MainScreen.instance.desc:getProfession()]
			if not spawn then
				spawn = spawnRegion.points["unemployed"]
			end
			if not spawn then
				print("ERROR: there is no spawn point table for the player's profession, don't know where to spawn the player")
				return
			end
			print(#spawn..' possible spawn points')
			local randSpawnPoint = spawn[(ZombRand(#spawn) + 1)]
			getWorld():setLuaSpawnCellX(randSpawnPoint.worldX)
			getWorld():setLuaSpawnCellY(randSpawnPoint.worldY)
			getWorld():setLuaPosX(randSpawnPoint.posX)
			getWorld():setLuaPosY(randSpawnPoint.posY)
			getWorld():setLuaPosZ(randSpawnPoint.posZ or 0)
		end
		if Core.isLastStand() then
			getWorld():setLuaSpawnCellX(globalChallenge.xcell);
			getWorld():setLuaSpawnCellY(globalChallenge.ycell);
			getWorld():setLuaPosX(globalChallenge.x);
			getWorld():setLuaPosY(globalChallenge.y);
			getWorld():setLuaPosZ(globalChallenge.z);
		end
		self:initPlayer()
		getWorld():setLuaPlayerDesc(MainScreen.instance.desc)
		getWorld():getLuaTraits():clear()
		for i,v in pairs(self.listboxTraitSelected.items) do
			getWorld():addLuaTrait(v.item:getType())
		end
		MainScreen.instance.avatar = nil
		CoopCharacterCreation.instance:accept()
	end
	if button.internal ~= "BACK" and button.internal ~= "NEXT" then
		CharacterCreationProfession.onOptionMouseDown(self, button, x, y)
	end
end

function CoopCharacterCreationProfession:new(x, y, width, height)
	local o = CharacterCreationProfession:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
	o:setUIName("CoopCharacterCreationProfession")
	return o
end

