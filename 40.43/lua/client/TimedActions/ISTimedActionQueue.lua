require "ISBaseObject"

ISTimedActionQueue = ISBaseObject:derive("ISTimedActionQueue");

ISTimedActionQueue.IDMax = 1;

ISTimedActionQueue.queues = {}

function ISTimedActionQueue:addToQueue (action)

	if  isKeyDown(Keyboard.KEY_ESCAPE) then
		self.queue = {}
	end

    local count = 0;
    for i,v in ipairs(self.queue) do
        count = count + 1;
    end
    table.insert(self.queue, action );
    table.insert(self.queue, nil );
    --self.queue[ISTimedActionQueue.IDMax] = {id = ISTimedActionQueue.IDMax, character = character, action = action};
    --print("action inserted. found "..count.." items on queue.");

    -- none in queue, so go!
    if count == 0 then
        action:begin();
        self.current = action;
        --print("action started.");
        --	ISTimedActionQueue.IDMax = ISTimedActionQueue.IDMax + 1;
    end

end

function ISTimedActionQueue:clearQueue ()
    self.queue[0] = nil
    self.queue[1] = nil
end

function ISTimedActionQueue:onCompleted(action)
--~ 	print "removing completed action.";
	for i,v in ipairs(self.queue) do
		if v == action then
--~ 			print ("removing entry: "..i);
			table.remove(self.queue, i);
			break;
		end
	end

	self.current = nil;
	for i,v in ipairs(self.queue) do
--~ 		print ("starting next queued action: "..i);
		v:begin();
		self.current = v;
		return;
	end

end


function ISTimedActionQueue:resetQueue()
    --print("Clearing action queue.");
	self.queue = {}
	self.current = nil;
end

--************************************************************************--
--** ISTimedActionQueue:new
--**
--************************************************************************--
function ISTimedActionQueue:new (character)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.queue = {}
	ISTimedActionQueue.queues[character] = o;
	return o
end

ISTimedActionQueue.getTimedActionQueue = function(character)
	local queue = ISTimedActionQueue.queues[character];
	if queue == nil then
		queue = ISTimedActionQueue:new(character);
	end

	return queue;
end

ISTimedActionQueue.add = function(action)
	if instanceof(action.character, "IsoGameCharacter") and action.character:isAsleep() then
		return;
	end
    local queue = ISTimedActionQueue.queues[action.character];
    if queue == nil then
        queue = ISTimedActionQueue:new(action.character);
    end

    queue:addToQueue(action);

    return queue;
end

ISTimedActionQueue.addAfter = function(previousAction, action)
    if instanceof(action.character, "IsoGameCharacter") and action.character:isAsleep() then
        return;
    end
    local queue = ISTimedActionQueue.getTimedActionQueue(action.character);
    for i,v in ipairs(queue.queue) do
        if v == previousAction then
            table.insert(queue.queue, i + 1, action);
            break
        end
    end
    return queue;
end

ISTimedActionQueue.clear = function(character)
    --print("Stopping current action.")
    character:StopAllActionQueue();
    --print("Clearing queue.")
    local queue = ISTimedActionQueue.queues[character];
    if queue == nil then
        queue = ISTimedActionQueue:new(character);
    end

    queue:clearQueue();

    return queue;
end

ISTimedActionQueue.hasAction = function(action)
    if action == nil then return false end
    local queue = ISTimedActionQueue.queues[action.character]
    if queue == nil then return false end
    for k,v in ipairs(queue.queue) do
        if v == action then return true end
    end
    return false
end

-- master function for stalled queues
ISTimedActionQueue.onTick = function()

   for i, k in pairs(ISTimedActionQueue.queues) do

       local v2 = k.queue[1];
      -- for i2,v2 in ipairs(k.queue) do
            if v2 and v2.action and not k.character:getCharacterActions():contains(v2.action) then
                print('bugged action, cleared queue')
                k:resetQueue()
                return
            end
             if v2 ~= nil and (v2.action == nil or v2.action:hasStalled()) then
                 k:onCompleted(v2);
                 return;
             elseif v2 == nil then
                 k:clearQueue();
             end
     --  end
     end
end

Events.OnTick.Add(ISTimedActionQueue.onTick);
