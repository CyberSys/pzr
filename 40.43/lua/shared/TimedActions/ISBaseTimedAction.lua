require "ISBaseObject"

ISBaseTimedAction = ISBaseObject:derive("ISBaseTimedAction");

ISBaseTimedAction.IDMax = 1;



function ISBaseTimedAction:isValid()

end

function ISBaseTimedAction:update()

end

function ISBaseTimedAction:forceComplete()
    self.action:forceComplete();
end

function ISBaseTimedAction:forceStop()
    self.action:forceStop();
end

function ISBaseTimedAction:getJobDelta()
	return self.action:getJobDelta();
end

function ISBaseTimedAction:start()

end

function ISBaseTimedAction:stop()
    ISTimedActionQueue.getTimedActionQueue(self.character):resetQueue();
end

function ISBaseTimedAction:perform()

	ISTimedActionQueue.getTimedActionQueue(self.character):onCompleted(self);

end

function ISBaseTimedAction:create()
    -- add a slight maxtime if the character is unhappy
	if self.maxTime ~= -1 then
		self.maxTime = self.maxTime + ((self.character:getMoodles():getMoodleLevel(MoodleType.Unhappy)) * 10)
        -- add more time if the character have his hands wounded
        if not self.ignoreHandsWounds then
            for i=BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.ForeArm_R) do
                local part = self.character:getBodyDamage():getBodyPart(BodyPartType.FromIndex(i));
                self.maxTime = self.maxTime + part:getPain();
            end
        end
    end
    self.action = LuaTimedActionNew.new(self, self.character);
end

function ISBaseTimedAction:begin()
	self:create();
--	print("action created.");
	self.character:StartAction(self.action);
--	print("action called.");
end

function ISBaseTimedAction:setTime(time)
	self.maxTime = time;
end

function ISBaseTimedAction:new (character)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.stopOnWalk = false;
	o.stopOnRun = false;
    o.caloriesModifier = 1;
	o.maxTime = -1;
	return o
end
