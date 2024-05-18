require "ISBaseObject"

SadisticMusicDirector = ISBaseObject:derive("SadisticMusicDirector");

SadisticMusicDirector.triggerDelay = ZombRand(200,500);

function SadisticMusicDirector:tick()
    if isServer() then return end

    -- FIXME? self.gameStarted is never set to true
    if not self.gameStarted then
       self:trigger();
    end
     local stats = getPlayer():getStats();
    local numZombies = stats:getNumVisibleZombies();

    local numChasing = stats:getNumChasingZombies() / 2;

    numZombies = numZombies + (numChasing / 2);
    if(self.lastNumZombie < numZombies) then
         self:seenZombies(numZombies);
    end

    self.lastNumZombie = numZombies;
    --if not getSoundManager():isPlayingMusic() then
        self.triggerCount = self.triggerCount + 1;
    --end

    if stats:getNumVisibleZombies() == 0 and stats:getNumChasingZombies() == 0 then
        self.lastSeenZombie = self.lastSeenZombie + 1;
    else
        self.lastSeenZombie = 0;
    end

    if(self.triggerCount > SadisticMusicDirector.triggerDelay) then
--    if(self.triggerCount > SadisticMusicDirector.triggerDelay) and not getSoundManager():isPlayingMusic()  then
        self:trigger();
        self.lastChangedTrack = 0;
        self.triggerCount = 0;
    end
    self.lastChangedTrack = self.lastChangedTrack + 1;
end

function SadisticMusicDirector:seenZombies(num)

    if getSpecificPlayer(0) == nil then return end;
    if num == 0 then self.drama = 0; return; end
    if num > 10 then
       self.drama = 10;
    else
        self.drama = num + 3;
    end

--    print("seen zombies, drama" .. self.drama)

    local outside = getSpecificPlayer(0):isOutside();
     if(self.triggerCount > SadisticMusicDirector.triggerDelay * 2)  then
        self:trigger();
    end

    if outside and not self.lastOutside then
       self:trigger();
    end
    if(self.drama > self.lastTriggerDrama) then
        self:trigger();
    end
    if(self.triggerCount > SadisticMusicDirector.triggerDelay)  then
        self.lastOutside = outside;
    end

end

function SadisticMusicDirector:trigger()
    -- check if we gonna play creepy ambient, it's at night and need to have seen no zombies for some time, then we stop the music and next time he see a zombie the creepy ambient kicks in
    if getGameTime():getHour() > 19 or getGameTime():getHour() < 8 then
--        print("ambient time! ", self.drama, self.lastTriggerDrama)
        if(self.drama > 0 and self.drama ~= self.lastTriggerDrama) then
--            print("music drama : " .. self.drama .. " not seen zombie for " .. self.lastSeenZombie);
            -- maximum 3 creepy ambient at a time
            if not getSoundManager():isPlayingMusic() and getSoundManager():getAmbientPieces():size() <= 3 then
--                print("already playing ambient: " .. getSoundManager():getAmbientPieces():size())
                local choice = MusicChoices.getAmbient(self.drama);

--                print("choosed night ambient: " .. choice, self.drama);

                getSoundManager():playNightAmbient(choice);
                self.lastTriggerDrama = self.drama;
            end
        end
    end
    if(self.triggerCount > SadisticMusicDirector.triggerDelay ) then
--   if(self.triggerCount > SadisticMusicDirector.triggerDelay or self.drama > self.lastTriggerDrama) then
--       print(self.triggerCount, SadisticMusicDirector.triggerDelay, self.drama, self.lastTriggerDrama, getSoundManager():isPlayingMusic());
      if not getSoundManager():isPlayingMusic() or (self.lastTriggerDrama <= 5 and (self.drama > self.lastTriggerDrama + 4 and self.lastChangedTrack > SadisticMusicDirector.triggerDelay) or (self.drama > self.lastTriggerDrama + 6 and self.lastChangedTrack > SadisticMusicDirector.triggerDelay /2 )) then
          -- we stop the music if it's night and he hasn't see zombies for a while
          if (getGameTime():getHour() > 19 and getGameTime():getHour() < 8 and self.lastSeenZombie >= 250) or not getSoundManager():getAmbientPieces():isEmpty() then
             return;
          end
          local mod = 0;
          if getPlayer():isOutside() then
              mod = ZombRand(3);
          end
          if getCore():getGameMode()=="LastStand" then
              if self.drama < 6 then self.drama = 6; end
          end
          local choice = MusicChoices.get(self.drama + mod);
--          print("play music ", getGameTime():getHour(), self.lastSeenZombie, choice)
          self.lastTriggerDrama = self.drama + mod;
          self.lastChangedTrack = 0;
          self.triggerCount = 0;
          -- reboot the next time we'll play music
          SadisticMusicDirector.triggerDelay = ZombRand(200,500);

          getSoundManager():playMusic(choice);
      end
   end
end

function SadisticMusicDirector:new ()
    local o = {}
    setmetatable(o, self)
    self.__index = self

    o.lastTimeSinceZombie = 0;
    o.lastNumZombie = 0;
    o.lastTriggerDrama = 0;
    o.triggerCount = 100000;
    o.drama = 0;
    o.lastOutside = false;
    o.gameStarted = false;
    o.lastChangedTrack = 11110;
    o.lastSeenZombie = 0;
    return o
end

SadisticMusicDirector.instance = SadisticMusicDirector:new();

function SadisticMusicDirectorTick()
    -- SadisticAIDirector:tick() used to call this, now it's called by IngameState.
    SadisticMusicDirector.instance:tick()
end

