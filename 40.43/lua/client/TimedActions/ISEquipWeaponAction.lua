--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISEquipWeaponAction = ISBaseTimedAction:derive("ISEquipWeaponAction");

function ISEquipWeaponAction:isValid()
	return self.character:getInventory():contains(self.item);
end

function ISEquipWeaponAction:update()
	self.item:setJobDelta(self:getJobDelta());
end

function ISEquipWeaponAction:start()
    if self.primary then
	    self.item:setJobType(getText("ContextMenu_Equip_Primary") .. " " .. self.item:getName());
    else
        self.item:setJobType(getText("ContextMenu_Equip_Secondary") .. " " .. self.item:getName());
    end
    if self.twoHands then
        self.item:setJobType(getText("ContextMenu_Equip_Two_Hands") .. " " .. self.item:getName());
    end
	self.item:setJobDelta(0.0);
end

function ISEquipWeaponAction:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);

end

function forceDropHeavyItems(character)
    if not character or not character:getCurrentSquare() then return end
    local primary = character:getPrimaryHandItem()
    local secondary = character:getSecondaryHandItem()
    if primary and (primary:getType() == "Generator" or primary:getType() == "CorpseMale" or primary:getType() == "CorpseFemale") then
        character:getInventory():Remove(primary)
        character:getCurrentSquare():AddWorldInventoryItem(primary, 0, 0, 0)
        character:setPrimaryHandItem(nil)
        if primary == secondary then
            character:setSecondaryHandItem(nil)
            secondary = nil
        end
    end
    if secondary and (secondary:getType() == "Generator" or secondary:getType() == "CorpseMale" or secondary:getType() == "CorpseFemale") then
        character:getInventory():Remove(secondary)
        character:getCurrentSquare():AddWorldInventoryItem(secondary, 0, 0, 0)
        character:setSecondaryHandItem(nil)
    end
end

function ISEquipWeaponAction:perform()
    self.item:getContainer():setDrawDirty(true);
    self.item:setJobDelta(0.0);
    forceDropHeavyItems(self.character)
	if not self.twoHands then
		-- equip primary weapon
		if(self.primary) then
            -- if the previous weapon need to be equipped in both hands, we then remove it
            if self.character:getSecondaryHandItem() and self.character:getSecondaryHandItem():isRequiresEquippedBothHands() then
                self.character:setSecondaryHandItem(nil);
            end
			-- if this weapon is already equiped in the 2nd hand, we remove it
			if(self.character:getSecondaryHandItem() == self.item or self.character:getSecondaryHandItem() == self.character:getPrimaryHandItem()) then
                self.character:setSecondaryHandItem(nil);
            end
            if not self.character:getPrimaryHandItem() or self.character:getPrimaryHandItem() ~= self.item then
			    self.character:setPrimaryHandItem(nil);
			    self.character:setPrimaryHandItem(self.item);
            end
		else -- second hand weapon
            -- if the previous weapon need to be equipped in both hands, we then remove it
            if self.character:getPrimaryHandItem() and self.character:getPrimaryHandItem():isRequiresEquippedBothHands() then
                self.character:setPrimaryHandItem(nil);
            end
			-- if this weapon is already equiped in the 1st hand, we remove it
			if(self.character:getPrimaryHandItem() == self.item or self.character:getSecondaryHandItem() == self.character:getPrimaryHandItem()) then
                self.character:setPrimaryHandItem(nil);
            end
            if not self.character:getSecondaryHandItem() or self.character:getSecondaryHandItem() ~= self.item then
                self.character:setSecondaryHandItem(nil);
			    self.character:setSecondaryHandItem(self.item);
            end
		end
    else
        self.character:setPrimaryHandItem(nil);
        self.character:setSecondaryHandItem(nil);

		self.character:setPrimaryHandItem(self.item);
		self.character:setSecondaryHandItem(self.item);
	end

	--if self.item:canBeActivated() and ((instanceof("Drainable", self.item) and self.item:getUsedDelta() > 0) or not instanceof("Drainable", self.item)) then
	if self.item:canBeActivated() then
		self.item:setActivated(true);
	end
	getPlayerData(self.character:getPlayerNum()).playerInventory:refreshBackpacks();

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISEquipWeaponAction:new (character, item, time, primary, twoHands)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = false;
	o.stopOnRun = true;
	o.maxTime = time;
	o.primary = primary;
	o.twoHands = twoHands;
    if item:isRequiresEquippedBothHands() then
        o.twoHands = true;
    end
    if character:getSecondaryHandItem() and character:getSecondaryHandItem() == item then
        o.maxTime = 0;
    end
    if character:getPrimaryHandItem() and character:getPrimaryHandItem() == item then
        o.maxTime = 0;
    end
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
	if o.twoHands then
		o.jobType = getText("ContextMenu_Equip_Two_Hands") .. " " .. item:getName()
	elseif o.primary then
		o.jobType = getText("ContextMenu_Equip_Primary") .. " " .. item:getName()
	else
		o.jobType = getText("ContextMenu_Equip_Secondary") .. " " .. item:getName()
	end
	return o
end
