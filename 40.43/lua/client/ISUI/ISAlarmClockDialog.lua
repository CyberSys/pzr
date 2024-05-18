require "ISUI/ISPanelJoypad"

ISAlarmClockDialog = ISPanelJoypad:derive("ISAlarmClockDialog");


--************************************************************************--
--** ISAlarmClockDialog:initialise
--**
--************************************************************************--

function ISAlarmClockDialog:initialise()
    ISPanelJoypad.initialise(self);
    local midY = 70
    
    self.button1p = ISButton:new((self:getWidth() / 2) - 28, midY - 30, 25, 16, "^", self, ISAlarmClockDialog.onClick);
    self.button1p.internal = "HOURSPLUS";
    self.button1p:initialise();
    self.button1p:instantiate();
    self.button1p.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.button1p);

    self.hours = ISTextEntryBox:new("0", self:getWidth() / 2 - 28, midY -10, 25, 18);
    self.hours:initialise();
    self.hours:instantiate();
    self:addChild(self.hours);

    self.button1m = ISButton:new(self:getWidth() / 2 - 28, midY + 11,25, 16, "v", self, ISAlarmClockDialog.onClick);
    self.button1m.internal = "HOURSMINUS";
    self.button1m:initialise();
    self.button1m:instantiate();
    self.button1m.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.button1m);

    --
    self.button2p = ISButton:new(self:getWidth() / 2 -1, midY - 30, 25, 16, "^", self, ISAlarmClockDialog.onClick);
    self.button2p.internal = "MINPLUS";
    self.button2p:initialise();
    self.button2p:instantiate();
    self.button2p.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.button2p);

    self.mins = ISTextEntryBox:new("0", self:getWidth() / 2 -1, midY -10, 25, 18);
    self.mins:initialise();
    self.mins:instantiate();
    self:addChild(self.mins);

    self.button2m = ISButton:new(self:getWidth() / 2 -1, midY + 11, 25, 16,"v", self, ISAlarmClockDialog.onClick);
    self.button2m.internal = "MINMINUS";
    self.button2m:initialise();
    self.button2m:instantiate();
    self.button2m.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.button2m);

    local fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
    local alarmHgt = fontHgt + 4
    local okHgt = alarmHgt
    self.setAlarm = ISLabel:new((self:getWidth() / 2) - 32, self:getHeight() - 5 - okHgt - 5 - alarmHgt, alarmHgt, getText("UI_Alarm"), 1,1,1,1,UIFont.Small,true);
    self.setAlarm:initialise();
    self.setAlarm:instantiate();
    self.setAlarm.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.setAlarm);

    local textWid = getTextManager():MeasureStringX(UIFont.Small, getText("UI_Alarm"))
    self.setAlarmButton = ISButton:new(self.setAlarm.x + 5 + textWid, self:getHeight() - 5 - okHgt - 5 - alarmHgt, 26, alarmHgt, getText("UI_On"), self, ISAlarmClockDialog.onClick);
    self.setAlarmButton.internal = "SETALARM";
    self.setAlarmButton.alarmOn = true;
    self.setAlarmButton:initialise();
    self.setAlarmButton:instantiate();
    self.setAlarmButton.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.setAlarmButton);


    --
    self.ok = ISButton:new((self:getWidth() - 100) / 2, self:getHeight() - 5 - okHgt, 100, okHgt, getText("UI_Ok"), self, ISAlarmClockDialog.onClick);
    self.ok.internal = "OK";
    self.ok:initialise();
    self.ok:instantiate();
    self.ok.borderColor = {r=1, g=1, b=1, a=0.1};
    self:addChild(self.ok);

    self.hours:setText(self.alarm:getHour() .. "");
    if self.alarm:getMinute() == 0 then
        self.mins:setText(self.alarm:getMinute() .. "0");
    else
        self.mins:setText(self.alarm:getMinute() .. "");
    end
    if not self.alarm:isAlarmSet() then
        self.setAlarmButton.alarmOn = false;
        self.setAlarmButton.title = getText("UI_Off");
    end


    self:insertNewLineOfButtons(self.button1p, self.button2p)
    self:insertNewLineOfButtons(self.button1m, self.button2m)
	self:insertNewLineOfButtons(self.setAlarmButton)
    self:insertNewLineOfButtons(self.ok)
end

function ISAlarmClockDialog:destroy()
    UIManager.setShowPausedMessage(true);
    self:setVisible(false);
    self:removeFromUIManager();
    if UIManager.getSpeedControls() then
        UIManager.getSpeedControls():SetCurrentGameSpeed(1);
    end
end

function ISAlarmClockDialog:onClick(button)
    if button.internal == "SETALARM" then
        self.setAlarmButton.alarmOn = not self.setAlarmButton.alarmOn;
        if self.setAlarmButton.alarmOn then
            self.setAlarmButton.alarmOn = true;
            self.setAlarmButton.title = getText("UI_On");
        else
            self.setAlarmButton.alarmOn = false;
            self.setAlarmButton.title = getText("UI_Off");
        end
    end
    if button.internal == "OK" then
        self.alarm:setAlarmSet(self.setAlarmButton.alarmOn);
        self.alarm:setHour(tonumber(self.hours:getText()))
        self.alarm:setMinute(tonumber(self.mins:getText()))
		self.alarm:syncAlarmClock()
        self:destroy();
        if JoypadState.players[self.player+1] then
            setJoypadFocus(self.player, self.prevFocus)
        end
    end
    if button.internal == "HOURSPLUS" then
        self:incrementHour(self.hours);
    end
    if button.internal == "HOURSMINUS" then
        self:decrementHour(self.hours);
    end
    if button.internal == "MINPLUS" then
        self:incrementMin(self.mins);
    end
    if button.internal == "MINMINUS" then
        self:decrementMin(self.mins);
    end
end

function ISAlarmClockDialog:incrementHour(number)
    local newNumber = tonumber(number:getText()) + 1;
    if newNumber > 23 then
        newNumber = 0;
    end
    number:setText(newNumber .. "");
end

function ISAlarmClockDialog:decrementHour(number)
    local newNumber = tonumber(number:getText()) - 1;
    if newNumber < 0 then
        newNumber = 23;
    end
    number:setText(newNumber .. "");
end

function ISAlarmClockDialog:incrementMin(number)
    local newNumber = tonumber(number:getText()) + 10;
    if newNumber > 50 then
        newNumber = 0;
    end
    if newNumber == 0 then
        number:setText(newNumber .. "0");
    else
        number:setText(newNumber .. "");
    end
end

function ISAlarmClockDialog:decrementMin(number)
    local newNumber = tonumber(number:getText()) - 10;
    if newNumber < 0 then
        newNumber = 50;
    end
    if newNumber == 0 then
        number:setText(newNumber .. "0");
    else
        number:setText(newNumber .. "");
    end
end

function ISAlarmClockDialog:prerender()
    self.backgroundColor.a = UIManager.isFBOActive() and 0.8 or 0.8
    self:drawRect(0, 0, self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
    self:drawRectBorder(0, 0, self.width, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawTextCentre(getText("IGUI_SetAlarm"), self:getWidth()/2, 10, 1, 1, 1, 1, UIFont.Small);
end

--************************************************************************--
--** ISAlarmClockDialog:render
--**
--************************************************************************--
function ISAlarmClockDialog:render()

end

function ISAlarmClockDialog:update()
    ISPanelJoypad.update(self)
    if self.ok.joypadFocused then
        self.ok:setJoypadButton(getTexture("media/ui/abutton.png"))
    else
        self.ok.joypadTexture = nil
        self.ok.isJoypad = false
    end
    if self.character:getX() ~= self.playerX or self.character:getY() ~= self.playerY then
        self:destroy()
    end
end

function ISAlarmClockDialog:onGainJoypadFocus(joypadData)
    ISPanelJoypad.onGainJoypadFocus(self, joypadData)
    self.joypadIndexY = 1
    self.joypadIndex = 1
    self.joypadButtons = self.joypadButtonsY[self.joypadIndexY]
    self.joypadButtons[self.joypadIndex]:setJoypadFocused(true)
end

function ISAlarmClockDialog:onJoypadDown(button)
    ISPanelJoypad.onJoypadDown(self, button)
    if button == Joypad.BButton then
        self:onClick(self.ok)
    end
end

function ISAlarmClockDialog:getCode()
    local n1 = tonumber(self.number1:getText()) * 100
    local n2 = tonumber(self.number2:getText()) * 10
    local n3 = tonumber(self.number3:getText())
    return n1 + n2 + n3
end

--************************************************************************--
--** ISAlarmClockDialog:new
--**
--************************************************************************--
function ISAlarmClockDialog:new(x, y, width, height, player, alarm)
    local o = {}
    o = ISPanelJoypad:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    local playerObj = player and getSpecificPlayer(player) or nil
    o.character = playerObj;
    o.name = nil;
    o.backgroundColor = {r=0, g=0, b=0, a=0.5};
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    if y == 0 then
        o.y = getPlayerScreenTop(player) + (getPlayerScreenHeight(player) - height) / 2
        o:setY(o.y)
    end
    if x == 0 then
        o.x = getPlayerScreenLeft(player) + (getPlayerScreenWidth(player) - width) / 2
        o:setX(o.x)
    end
    o.width = width;
    o.height = height;
    o.anchorLeft = true;
    o.anchorRight = true;
    o.anchorTop = true;
    o.anchorBottom = true;
    o.player = player;
    o.playerX = playerObj:getX()
    o.playerY = playerObj:getY()
    o.alarm = alarm;
    return o;
end

