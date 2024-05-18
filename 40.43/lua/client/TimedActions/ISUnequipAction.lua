--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISUnequipAction = ISBaseTimedAction:derive("ISUnequipAction");

function ISUnequipAction:isValid()
	return true;
end

function ISUnequipAction:update()
	self.item:setJobDelta(self:getJobDelta());
end

function ISUnequipAction:start()
	self.item:setJobType(getText("ContextMenu_Unequip") .. " " .. self.item:getName());
	self.item:setJobDelta(0.0);
end

function ISUnequipAction:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);
end

function ISUnequipAction:perform()
    self.item:getContainer():setDrawDirty(true);
    self.item:setJobDelta(0.0);
	if instanceof(self.item, "InventoryContainer") and self.item:canBeEquipped() == "Back" and self.character:getClothingItem_Back() == self.item then
		self.character:setClothingItem_Back(nil);
	elseif self.item:getCategory() == "Clothing" then
		if self.item:getBodyLocation() == ClothingBodyLocation.Top and self.item == self.character:getClothingItem_Torso() then
			self.character:setClothingItem_Torso(nil);
		elseif self.item:getBodyLocation() == ClothingBodyLocation.Shoes and self.item == self.character:getClothingItem_Feet() then
			self.character:setClothingItem_Feet(nil);
		elseif self.item:getBodyLocation() == ClothingBodyLocation.Bottoms and self.item == self.character:getClothingItem_Legs() then
			self.character:setClothingItem_Legs(nil);
		elseif self.item == self.character:getPrimaryHandItem() then
			self.character:setPrimaryHandItem(nil);
		elseif self.item == self.character:getSecondaryHandItem() then
			self.character:setSecondaryHandItem(nil);
		end
		triggerEvent("OnClothingUpdated", self.character)
    end
    if self.item == self.character:getPrimaryHandItem() then
        if (self.item:isTwoHandWeapon() or self.item:isRequiresEquippedBothHands()) and self.item == self.character:getSecondaryHandItem() then
            self.character:setSecondaryHandItem(nil);
        end
		self.character:setPrimaryHandItem(nil);
    end
    if self.item == self.character:getSecondaryHandItem() then
        if (self.item:isTwoHandWeapon() or self.item:isRequiresEquippedBothHands()) and self.item == self.character:getPrimaryHandItem() then
            self.character:setPrimaryHandItem(nil);
        end
		self.character:setSecondaryHandItem(nil);
    end
    if self.item:getType() == "Generator" or self.item:getType() == "CorpseMale" or self.item:getType() == "CorpseFemale" then
       self.character:getInventory():Remove(self.item);
       self.character:getCurrentSquare():AddWorldInventoryItem(self.item,0,0,0);
    end
	getPlayerData(self.character:getPlayerNum()).playerInventory:refreshBackpacks();
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISUnequipAction:new(character, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = false;
	o.stopOnRun = true;
	o.maxTime = time;
	return o;
end
