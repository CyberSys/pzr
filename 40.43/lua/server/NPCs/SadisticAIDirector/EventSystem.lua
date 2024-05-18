require "ISBaseObject"
require "Util/LuaList"

EventSystem = ISBaseObject:derive("EventSystem");

--************************************************************************--
--** EventSystem:initialise
--**
--************************************************************************--
function EventSystem:initialise()

end

function EventSystem:registerEvent(event)
    if self.events == nil then
        self.events = LuaList:new();
        self.eventMap = {}
    end
    self.events:add(event);
    self.eventMap[event.name] = event;
end

function EventSystem:registerCharacterEvent(event)
    if self.cevents == nil then
        self.cevents = LuaList:new();
        self.ceventMap = {}
    end
    self.cevents:add(event);
    self.ceventMap[event.name] = event;
end

function EventSystem:tick()

   -- print(self.processEvents.debug);
  --  print (self.processEvents:size());
 	for i=0, self.processEvents:size() - 1 do
		local inst = self.processEvents:get(i);

		if inst.event.isFinished(inst, inst.group, inst.character) then
			print("is finished");
			if inst.event.finally ~= nil then
				inst.event.finally(inst, inst.group, inst.character);
			end
			self.processEvents:remove(inst);
			i = i-1;
        else
            if inst.event.processMeta ~= nil then
                inst.event.processMeta(inst, inst.group, inst.character);
            end
            if inst.group:getLeader() ~= nil and inst.group:getLeader():getInstance() ~= nil then
                if inst.event.processReal ~= nil then
                        inst.event.processReal(inst, inst.group, inst.character);
                end
            end
		end
	end
end

function EventSystem:clearGroupEvents(group)

    if nil == nil then return; end
    print("shouldn't ever get here");
    for i=0, self.processEvents:size() - 1 do
        local inst = self.processEvents:get(i);

        if inst.group == group then
            self.processEvents:remove(inst);
            i = i - 1;
        end

    end


    for i=0, self.cprocessEvents:size() - 1 do
        local inst = self.cprocessEvents:get(i);

        if inst.group == group then
            self.cprocessEvents:remove(inst);
            i = i - 1;
        end

    end

    group.eventqueuelist:clear();
end

function EventSystem:queue(event, group, inst, c)
    if instanceof(group, "SurvivorGroup") then
        group = group:getLuaGroup();
    end

    if group == nil then print "group nil. Bad"; end
    if inst == nil then print "inst nil. Bad"; end
    if event == nil then print "event nil. Bad"; end

    group.inst = inst;
    inst.event = event;
    inst.group = group;
    inst.character = c;
    --print("fired "..event.name);

    if event == nil then
        --print("Event null.");
        return;
    end
    if group == nil then
        --print("group null.");
        return;
    end
    if inst == nil then
        --print("inst null.");
        return;
    end

    group.eventqueuelist:add({inst=inst, event=event, char=c});
end

function EventSystem:ctrigger(event, group, inst, c)

    -- we have the java object, so get the actual lua group.
    if instanceof(group, "SurvivorGroup") then
        group = group:getLuaGroup();
    end

    if group == nil then print "group nil. Bad"; end
    if inst == nil then print "inst nil. Bad"; end
    if event == nil then print "event nil. Bad"; end
    print("triggering "..event.name)

    group.inst = inst;
    inst.event = event;
    inst.group = group;
    inst.character = c;
    --print("fired "..event.name);

    if event == nil then
        --print("Event null.");
        return;
    end
    if group == nil then
        --print("group null.");
        return;
    end
    if inst == nil then
        --print("inst null.");
        return;
    end
    local e = event;
    --print("calling meta");
    if e.meta ~= nil then
        e.meta(inst, group, c);
    end

    --print("test real");
    if group:getLeader() ~= nil and group:getLeader():getInstance() ~= nil then
        if e.real ~= nil then
            --print("is real");
            e.real(inst, group, c);
        end
    else
        if e.metaOnly ~= nil then
            --print("meta only");
            e.metaOnly(inst, group, c);
        end
    end

    --print("test is finished");
    if inst.event.isFinished(inst, group, c) then
        --print("is finished");
        if event.finally ~= nil then
            --print("finally");
            event.finally(inst, group, c);
        end
    else
        self.cprocessEvents:add(inst);
    end

    --self:trigger(event);
end

function EventSystem:trigger(event, group, inst)

    -- we have the java object, so get the actual lua group.
    if instanceof(group, "SurvivorGroup") then
        group = group:getLuaGroup();
    end
    if group == nil then print "group nil. Bad"; end
    if inst == nil then print "inst nil. Bad"; end
    if event == nil then print "event nil. Bad"; end
    print("triggering "..event.name)

    group.inst = inst;
    inst.event = event;
    inst.group = group;
    --print("fired "..event.name);

    if event == nil then
        --print("Event null.");
        return;
    end
    if group == nil then
        --print("group null.");
        return;
    end
    if inst == nil then
        --print("inst null.");
        return;
    end
    local e = event;
    --print("calling meta");
    if e.meta ~= nil then
        e.meta(inst, group);
    end

    --print("test real");
    if group:getLeader() ~= nil and group:getLeader():getInstance() ~= nil then
        if e.real ~= nil then
            --print("is real");
            e.real(inst, group);
        end
    else
        if e.metaOnly ~= nil then
            --print("meta only");
            e.metaOnly(inst, group);
        end
    end

    --print("test is finished");
    if inst.event.isFinished(inst, group) then
        print("is finished");
        if event.finally ~= nil then
            --print("finally");
            event.finally(inst, group);
        end
    else
        if self.processEvents == nil then
            self.processEvents = LuaList:new();
        end
        print("adding process event");
        self.processEvents:add(inst);
        print("size of process events list: "..self.processEvents:size());
    end

    --self:trigger(event);
end

function EventSystem:updateSurvivorGroup(group, days)

    for i=0, self.events:size() - 1 do
        local v = self.events:get(i);
        local inst = {}
        if v.isValid(inst, group) and v.triggerEveryXDaysOnAverage ~= nil then

            local outcome = nil;
            local m = (v.triggerEveryXDaysOnAverage(group) * v.modifiers(inst, group));
            if m < days then m = days; end
            local chanceDelta = days / m;
            local chance = 0;
            local mul = 1;
            while chance < 1 do
                mul = mul * 10;
                chance = chanceDelta * mul;

            end
            --            --print(mul .. " " .. chance);
            if ZombRand(mul) < chance then
                self:trigger(v, group, inst);
            end
        else
            ----print("event not valid");
        end
    end

    -- do character events
    for j=0, group.members:size() - 1 do
        local c = group.members:get(j);
        for i=0, self.cevents:size() - 1 do
            local v = self.cevents:get(i);
            local inst = {}
            if v.isValid(inst, group, c) and v.triggerEveryXDaysOnAverage ~= nil then

                local outcome = nil;
                local m = (v.triggerEveryXDaysOnAverage(group, c) * v.modifiers(inst, group, c));
                if m < days then m = days; end
                local chanceDelta = days / m;
                local chance = 0;
                local mul = 1;
                while chance < 1 do
                    mul = mul * 10;
                    chance = chanceDelta * mul;

                end
                --            --print(mul .. " " .. chance);
                if ZombRand(mul) < chance then
                    self:ctrigger(v, group, inst, c);
                end
            else
                ----print("event not valid");
            end
        end
    end
end

function EventSystem:new ()
	local o = {}
	setmetatable(o, self)
	self.__index = self
    o.events = LuaList:new();
    o.eventMap = {}
    o.cevents = LuaList:new();
    o.ceventMap = {}
    o.processEvents = LuaList:new();
    --o.processEvents.debug = true;
    o.cprocessEvents = LuaList:new();
    return o
end

EventSystem.instance = EventSystem:new();
