--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "Map/CGlobalObject"

CPlantGlobalObject = CGlobalObject:derive("CPlantGlobalObject")

function CPlantGlobalObject:new(luaSystem, isoObject)
	local o = CGlobalObject.new(self, luaSystem, isoObject)
	return o
end

function CPlantGlobalObject:getObject()
	return self:getIsoObject()
end

function CPlantGlobalObject:isAlive()
	return self.state ~= "destroy" and self.state ~= "dry" and self.state ~= "rotten"
end

function CPlantGlobalObject:canHarvest()
	return self:isAlive() and self.hasVegetable
end

