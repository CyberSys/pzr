--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "Map/CGlobalObject"

CTrapGlobalObject = CGlobalObject:derive("CTrapGlobalObject")

function CTrapGlobalObject:new(luaSystem, isoObject)
	local o = CGlobalObject.new(self, luaSystem, isoObject)
	return o
end

function CTrapGlobalObject:fromModData(modData)
	for k,v in pairs(modData) do
		self[k] = v
	end

	if self.trapBait == "" then
		self.bait = nil
	else
		self.bait = self.trapBait
	end

	self.animal = {}
	local animalType = modData["animal"]
	for i,v in ipairs(Animals) do
		if v.type == animalType then
			self.animal = v
		end
	end
end

