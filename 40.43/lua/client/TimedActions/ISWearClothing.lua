--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISWearClothing = ISBaseTimedAction:derive("ISWearClothing");

function ISWearClothing:isValid()
	return self.character:getInventory():contains(self.item);
end

function ISWearClothing:update()
	self.item:setJobDelta(self:getJobDelta());
end

function ISWearClothing:start()
	self.item:setJobType(getText("ContextMenu_Wear") .. ' ' .. self.item:getName());
	self.item:setJobDelta(0.0);
end

function ISWearClothing:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);
end

function ISWearClothing:perform()
    self.item:getContainer():setDrawDirty(true);
    self.item:setJobDelta(0.0);
	if instanceof(self.item, "InventoryContainer") and self.item:canBeEquipped() == "Back" then
		self.character:setClothingItem_Back(self.item);
		getPlayerData(self.character:getPlayerNum()).playerInventory:refreshBackpacks();
	else
		if self.item:getBodyLocation() == ClothingBodyLocation.Top then
			self.character:setClothingItem_Torso(self.item);
		elseif self.item:getBodyLocation() == ClothingBodyLocation.Shoes then
			self.character:setClothingItem_Feet(self.item);
		elseif self.item:getBodyLocation() == ClothingBodyLocation.Bottoms then
			self.character:setClothingItem_Legs(self.item);
		end
	end
	self.character:initSpritePartsEmpty();
	triggerEvent("OnClothingUpdated", self.character)
--~ 	self.character:SetClothing(self.item:getBodyLocation(), self.item:getSpriteName(), self.item:getPalette());
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISWearClothing:new(character, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	return o;
end
