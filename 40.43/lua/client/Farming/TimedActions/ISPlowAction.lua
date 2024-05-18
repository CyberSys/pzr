--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPlowAction = ISBaseTimedAction:derive("ISPlowAction");

function ISPlowAction:isValid()
	return true;
end

function ISPlowAction:update()
	self.character:faceLocation(self.gridSquare:getX(), self.gridSquare:getY())
    if self.item then
	    self.item:setJobDelta(self:getJobDelta());
    end
end

function ISPlowAction:start()
    if self.item then
        self.item:setJobType(getText("ContextMenu_Dig"));
        self.item:setJobDelta(0.0);
    end
    self.sound = getSoundManager():PlayWorldSound("Shoveling", self.gridSquare, 0, 10, 1, true);
    --    self.gridSquare:playSound("shoveling", true);
    addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 10, 1)
end

function ISPlowAction:stop()
    if self.sound and self.sound:isPlaying() then
        self.sound:stop();
    end
    ISBaseTimedAction.stop(self);
    if self.item then
        self.item:setJobDelta(0.0);
    end
end

function ISPlowAction:perform()
    if self.item then
        self.item:getContainer():setDrawDirty(true);
        self.item:setJobDelta(0.0);
    elseif ZombRand(10) == 0 then -- chance of getting hurt
        if ZombRand(2) == 0 then
            self.character:getBodyDamage():getBodyPart(BodyPartType.Hand_L):SetScratchedWeapon(true);
        else
            self.character:getBodyDamage():getBodyPart(BodyPartType.Hand_R):SetScratchedWeapon(true);
        end
    end
    if self.sound and self.sound:isPlaying() then
        self.sound:stop();
    end

	local sq = self.gridSquare
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ() }
	CFarmingSystem.instance:sendCommand(self.character, 'plow', args)

	CFarmingSystem.instance:changePlayer(self.character)
    -- maybe give worm ?
    if ZombRand(5) == 0 then
        self.character:getInventory():AddItem("Base.Worm");
    end
	ISBaseTimedAction.perform(self);
end

function ISPlowAction:new (character, square, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	-- custom fields
    if not item then
        o.maxTime = time * 2;
    end
	o.item = item;
	o.gridSquare = square
    o.caloriesModifier = 5;
	return o
end
