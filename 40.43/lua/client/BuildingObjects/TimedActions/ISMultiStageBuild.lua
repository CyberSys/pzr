--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 12/05/16
-- Time: 11:01
-- To change this template use File | Settings | File Templates.
--

require "TimedActions/ISBaseTimedAction"

ISMultiStageBuild = ISBaseTimedAction:derive("ISMultiStageBuild");

function ISMultiStageBuild:isValid()
    return true;
end

function ISMultiStageBuild:update()
end

function ISMultiStageBuild:start()
    if not ISBuildMenu.canDoStage(self.character, self.stage) then
        self:forceStop();
    end
    if self.stage:getCraftingSound() then
        self.sound = self.character:playSound(self.stage:getCraftingSound());
        -- FIXME: apply getHammerSoundMod() / getWeldingSoundMod()
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 1)
    end
end

function ISMultiStageBuild:stop()
    if self.sound then
        self.character:getEmitter():stopSound(self.sound)
        self.sound = nil
    end
    ISBaseTimedAction.stop(self);
end

function ISMultiStageBuild:perform()
    if self.sound then
        self.character:getEmitter():stopSound(self.sound)
        self.sound = nil
    end
    self.stage:doStage(self.character, self.item, not ISBuildMenu.cheat);
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
end

function ISMultiStageBuild:new(character, stage, item, time)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
    o.stage = stage;
    o.item = item;
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.maxTime = time;
    if ISBuildMenu.cheat then o.maxTime = 1; end
    o.caloriesModifier = 4;
    return o;
end


