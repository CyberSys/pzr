--***********************************************************
--**                    THE INDIE STONE                    **
--**				  Author: turbotutone				   **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISMoveablesAction = ISBaseTimedAction:derive("ISMoveablesAction")

function ISMoveablesAction:isValid()
    return true;
end

function ISMoveablesAction:update()
    if self.mode and self.mode=="scrap" and self.moveProps and self.moveProps.object then
        self.character:faceThisObject(self.moveProps.object);
    else
        self.character:faceLocation(self.square:getX(), self.square:getY());
    end
    if self.sound and not self.sound:isPlaying() then
        self:setActionSound();
    end
end

function ISMoveablesAction:setActionSound()
    if self.mode == "scrap" then
        self.sound = self.moveProps:getScrapSound( self.character );
    else
        self.sound = self.moveProps:getSoundFromTool( self.square, self.character, self.mode );
    end
end

function ISMoveablesAction:start()
    self:setActionSound();
    if self.sound then
        --self.sound = sound;
        self.sound:stop();
    end
end

function ISMoveablesAction:stop()
    if self.sound and self.sound:isPlaying() then
        self.sound:stop();
    end
    ISBaseTimedAction.stop(self)
end

--[[
-- The moveprops of the new facing (where applies) are always used to perform the actions, the origSpriteName is passed to retrieve the original object from tile or inventory.
 ]]
function ISMoveablesAction:perform()
    if self.sound and self.sound:isPlaying() then
        self.sound:stop();
    end

    if self.moveProps and self.moveProps.isMoveable and self.mode and self.mode ~= "scrap" then
        if self.mode == "pickup" then
            self.moveProps:pickUpMoveableViaCursor( self.character, self.square, self.origSpriteName, self.moveCursor ); --OrigSpriteName currently not used in this one.
        elseif self.mode == "place" then
            self.moveProps:placeMoveableViaCursor( self.character, self.square, self.origSpriteName, self.moveCursor );
        elseif self.mode == "rotate" then
            self.moveProps:rotateMoveableViaCursor( self.character, self.square, self.origSpriteName, self.moveCursor );
        end
    elseif self.mode and self.mode=="scrap" then
        self.moveProps:scrapObjectViaCursor( self.character, self.square, self.origSpriteName, self.moveCursor );
    end

    ISBaseTimedAction.perform(self)
end

function ISMoveablesAction:new(character, _sq, _moveProps, _mode, _origSpriteName, _moveCursor )
    local o             = {};
    setmetatable(o, self);
    self.__index        = self;
    o.character         = character;
    o.square            = _sq;
    o.origSpriteName    = _origSpriteName;
    o.stopOnWalk        = true;
    o.stopOnRun         = true;
    o.maxTime           = 50;
    o.spriteFrame       = 0;
    o.mode              = _mode;
    o.moveProps         = _moveProps;
    o.moveCursor        = _moveCursor;

    if ISMoveableDefinitions.cheat then
        o.maxTime = 10;
    else
        if o.moveProps and o.moveProps.isMoveable and _mode and _mode~="rotate" and _mode~= "scrap" then
            o.maxTime = o.moveProps:getActionTime( character, _mode );
        elseif o.moveProps and _mode == "scrap" then
            o.maxTime = o.moveProps:getScrapActionTime( character );
        end
    end
    return o;
end
