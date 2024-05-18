--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISBuryCorpse = ISBaseTimedAction:derive("ISBuryCorpse");

function ISBuryCorpse:isValid()
	return true;
end

function ISBuryCorpse:update()
	self.character:faceThisObject(self.graves)
end

function ISBuryCorpse:start()
end

function ISBuryCorpse:stop()
    ISBaseTimedAction.stop(self);
end

function ISBuryCorpse:perform()
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);

	self.graves:getModData()["corpses"] = self.graves:getModData()["corpses"] + 1;
	self.graves:transmitModData()

	self.character:setPrimaryHandItem(nil);
	self.character:setSecondaryHandItem(nil);
	self.character:getInventory():RemoveOneOf("CorpseMale", false);
	
	local sq1 = self.graves:getSquare();
	local sq2 = nil;
	local sq3 = nil;
	if self.graves:getNorth() then
		if self.graves:getModData()["spriteType"] == "sprite1" then
			sq2 = getCell():getGridSquare(sq1:getX(), sq1:getY() - 1, sq1:getZ());
		elseif self.graves:getModData()["spriteType"] == "sprite2" then
			sq2 = getCell():getGridSquare(sq1:getX(), sq1:getY() + 1, sq1:getZ());
		end
	else
		if self.graves:getModData()["spriteType"] == "sprite1" then
			sq2 = getCell():getGridSquare(sq1:getX() - 1, sq1:getY(), sq1:getZ());
		elseif self.graves:getModData()["spriteType"] == "sprite2" then
			sq2 = getCell():getGridSquare(sq1:getX() + 1, sq1:getY(), sq1:getZ());
		end
	end
	
	self:increaseCorpse(sq2);

	if self.graves:getModData()["corpses"] >= 5 then
		self:changeSprite(sq1);
		self:changeSprite(sq2);
	end
end

function ISBuryCorpse:increaseCorpse(square)
	for i=0,square:getSpecialObjects():size()-1 do
		local grave = square:getSpecialObjects():get(i);
		if grave:getName() == "EmptyGraves" then
			grave:getModData()["corpses"] = grave:getModData()["corpses"] + 1;
			grave:transmitModData()
		end
	end
end


function ISBuryCorpse:changeSprite(square)
	for i=0,square:getSpecialObjects():size()-1 do
		local grave = square:getSpecialObjects():get(i);
		if grave:getName() == "EmptyGraves" then
			local split = luautils.split(grave:getSprite():getName(), "_");
			local spriteName = "location_community_cemetary_01_" .. (split[5] + 8);
			grave:setSpriteFromName(spriteName);
			grave:transmitUpdatedSpriteToServer();
		end
	end
	
end

function ISBuryCorpse:new(character, graves, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = getSpecificPlayer(character);
	o.graves = graves;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    o.caloriesModifier = 5;
	return o;
end