--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPlantainCataplasm = ISBaseTimedAction:derive("ISPlantainCataplasm");

function ISPlantainCataplasm:isValid()
	if ISHealthPanel.DidPatientMove(self.character, self.otherPlayer, self.bandagedPlayerX, self.bandagedPlayerY) then
		return false
	end
    return self.character:getInventory():contains(self.item)
end

function ISPlantainCataplasm:update()
    self.item:setJobDelta(self:getJobDelta());
    ISHealthPanel.setBodyPartActionForPlayer(self.otherPlayer, self.bodyPart, self, "Apply Cataplasm", { plantain = true })
end

function ISPlantainCataplasm:start()
    self.item:setJobType(getText("IGUI_JobType_ApplyCataplasm"));
    self.item:setJobDelta(0.0);
end

function ISPlantainCataplasm:stop()
    ISHealthPanel.setBodyPartActionForPlayer(self.otherPlayer, self.bodyPart, nil, nil, nil)
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);
end

function ISPlantainCataplasm:perform()
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
    self.item:setJobDelta(0.0);

    if self.character:HasTrait("Hemophobic") and self.bodyPart:getBleedingTime() > 0 then
        self.character:getStats():setPanic(self.character:getStats():getPanic() + 50);
    end

    local cataplasmPower = ZombRandFloat((self.doctorLevel + 1) * 0.5, (self.doctorLevel + 1) * 1.0) + 10;
    self.bodyPart:setPlantainFactor(cataplasmPower);
    self.character:getInventory():Remove(self.item);
    if isClient() then
        sendCataplasm(self.otherPlayer:getOnlineID(), self.bodyPart:getIndex(), cataplasmPower, 0, 0);
    end
    ISHealthPanel.setBodyPartActionForPlayer(self.otherPlayer, self.bodyPart, nil, nil, nil)
end

function ISPlantainCataplasm:new(doctor, otherPlayer, item, bodyPart)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = doctor;
    o.otherPlayer = otherPlayer;
    o.doctorLevel = doctor:getPerkLevel(Perks.Doctor);
	o.item = item;
	o.bodyPart = bodyPart;
	o.stopOnWalk = true;
	o.stopOnRun = true;
    o.bandagedPlayerX = otherPlayer:getX();
    o.bandagedPlayerY = otherPlayer:getY();
    o.maxTime = 120 - (o.doctorLevel * 4);
    if doctor:getAccessLevel() ~= "None" then
        o.maxTime = 1;
        o.doctorLevel = 10;
    end
	return o;
end
