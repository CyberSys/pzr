--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISStopAlarmClockAction = ISBaseTimedAction:derive("ISStopAlarmClockAction");

function ISStopAlarmClockAction:isValid()
    return true;
end

function ISStopAlarmClockAction:update()
end

function ISStopAlarmClockAction:start()
end

function ISStopAlarmClockAction:stop()
    ISBaseTimedAction.stop(self);
end

function ISStopAlarmClockAction:perform()
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
    self.alarm:stopRinging()
    self.alarm:syncStopRinging()
end

function ISStopAlarmClockAction:new(character, alarm, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.alarm = alarm;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	return o;
end
