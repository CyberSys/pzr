require "TimedActions/ISBaseTimedAction"

ISClimbThroughWindow = ISBaseTimedAction:derive("ISClimbThroughWindow");

function ISClimbThroughWindow:isValid()
    if instanceof(self.item, 'IsoWindow') and not self.item:isDestroyed() and not self.item:IsOpen() then
        return false
    end
    return true;
end

function ISClimbThroughWindow:update()
end

function ISClimbThroughWindow:start()
end

function ISClimbThroughWindow:stop()
    ISBaseTimedAction.stop(self);
end

function ISClimbThroughWindow:perform()
    --self.item:ToggleWindow(self.character);
    if IsoWindowFrame.isWindowFrame(self.item) then
        self.character:climbThroughWindowFrame(self.item)
    else
        self.character:climbThroughWindow(self.item);
    end
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
end

function ISClimbThroughWindow:new(character, item, time)
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
