require "NPCs/Behaviour/BaseBehaviour"

GuardBehaviour = BaseBehaviour:derive("GuardBehaviour");

function GuardBehaviour:process()

    if self.bored <= 0 then
        self.bored = self.boredmax;
        self.timed = nil;
        self.target = nil;
        self.standSquare = nil;
    end
    if self.count <= 0 then
        self.count = self.countmax;
        self.timed = nil;
    end

    if self.target == nil and self.character:getRoom() ~= nil then
      --  print("room found");
        local building = self.character:getRoom():getBuilding();
        if building ~= nil then
        --    print("building found");
            local room = building:getRandomRoom();

            if room ~= nil then
  --              print("random room found");
                local windows = room:getWindows();

                if not windows:isEmpty() then
--                    print("choosing window")
                    local w = windows:get(ZombRand(windows:size()))
                    self.target = w;
                    self.room = room;
                end
            end
        end
    end

    if self.standSquare == nil then
        if instanceof(self.target, "IsoWindow") then
    --        print("found window")
           -- go stand next to it...
            local sq = self.target:getIndoorSquare();
            if sq ~= nil then
      --          print("found indoor square")
                if not sq:isFree(true) then
        --            print("square not free")
                    self.target = nil;
                else
                    self.standSquare = sq;
                end
            end
        end
    end

    self.count = self.count - getGameTime():getMultiplier();
    self.bored = self.bored - getGameTime():getMultiplier();

    if self.timed == nil and self.standSquare ~= nil and self.standSquare ~= self.character:getInstance():getCurrentSquare() then

        self.timed = ISWalkToTimedAction:new(self.character:getInstance(), self.standSquare);
        ISTimedActionQueue.add(self.timed);
    end
end

function GuardBehaviour:new (character)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
    o.countmax = 5.0 * 60;
    o.count = o.countmax;
    o.boredmax = 30.0 * 60;
    o.bored = o.boredmax;
    return o
end

