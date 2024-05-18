require "ISUI/ISPanel"
require "ISUI/ISRichTextPanel"
require "ISUI/ISButton"

TutorialMessage = ISPanel:derive("TutorialMessage");


--************************************************************************--
--** ISDemoPopup:initialise
--**
--************************************************************************--

function TutorialMessage:initialise()
    ISPanel.initialise(self);
end

--************************************************************************--
--** ISDemoPopup:instantiate
--**
--************************************************************************--
function TutorialMessage:createChildren()

    -- CREATE TUTORIAL PANEL
    local panel = ISRichTextPanel:new(15, 10, self.width-30, self.height-32-10);
    panel:initialise();

    self:addChild(panel);
    --panel:paginate();
    self.richtext = panel;
    self.richtext.text = self.message;
    self.richtext:paginate();
    self.richtext.backgroundColor.a = 0;

end


function TutorialMessage:setInfo(item)

end

function TutorialMessage:onMouseWheel(del)
    return false;
end

--************************************************************************--
--** ISInfoWindow:update
--**
--************************************************************************--
function TutorialMessage:update()
    if self.test ~= nil then
        if(self.test()) then
            TutorialMessage.instance = nil;
            self:removeFromUIManager();
            self.test = nil;
            self.target:onClose(self);
        end

    end
end
function TutorialMessage:render()
    self:setWidth(self.richtext:getWidth() + 40);
    self:setHeight(self.richtext:getHeight() + 35);

    self:drawTextureScaled(TutorialMessage.spiffo, self.width - 43, -60, 256/2, 364/2, 1, 1, 1, 1);

end

--function TutorialMessage:onMouseDown(x, y)
--    if self.clickToSkip then
--        TutorialMessage.instance = nil;
--        self:removeFromUIManager();
--        self.target:onClose(self);
--    end
--
--end
--function TutorialMessage:onMouseDownOutside(x, y)
--    if self.clickToSkip then
--        TutorialMessage.instance = nil;
--        self:removeFromUIManager();
--        self.target:onClose(self);
--    end
--end

TutorialMessage.onKeyPressed = function(key)
    if TutorialMessage.instance and key == Keyboard.KEY_SPACE and TutorialMessage.instance.clickToSkip then
        local instance = TutorialMessage.instance;
        TutorialMessage.instance = nil;
        instance:removeFromUIManager();
        instance.target:onClose(instance);
        getPlayer():setAuthorizeMeleeAction(true);
    end
end

TutorialMessage.getInstance = function(x, y, w, h, message, clickToSkip, target, test)
    if TutorialMessage.instance ~= nil then
        return TutorialMessage.instance;
    end;
    x = x - (w / 2);
    y = y - (h/2);
    if TutorialMessage.instance ~= nil then
        TutorialMessage.instance:removeFromUIManager();
        TutorialMessage.instance:setX(x);
        TutorialMessage.instance:setY(y);
        TutorialMessage.instance:setWidth(w);
        TutorialMessage.instance:setHeight(h);
        TutorialMessage.instance.message = message;
        TutorialMessage.instance.clickToSkip = clickToSkip;
        TutorialMessage.instance:alwaysOnTop();
        TutorialMessage.instance:setAlwaysOnTop(true);
    else
        TutorialMessage.instance = TutorialMessage:new(x, y, w, h, clickToSkip, message);
        TutorialMessage.instance:initialise();
        TutorialMessage.instance:addToUIManager();
        TutorialMessage.instance:setAlwaysOnTop(true);
    end
    getPlayer():setAuthorizeMeleeAction(false);
    TutorialMessage.instance.target = target;
    TutorialMessage.instance.test = test;
    -- SurvivalGuideManager.instance.panel:setVisible(false);
    return TutorialMessage.instance;
end

--************************************************************************--
--** ISInfoWindow:new
--**
--************************************************************************--
function TutorialMessage:new (x, y, width, height, clickToSkip, message)
    local o = {}
    --o.data = {}
    o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.message = message;
    o.clickToSkip = clickToSkip;
    TutorialMessage.spiffo = getTexture("media/ui/survivorspiffo.png");
    o.y = y;
    o.borderColor = {r=1, g=1, b=1, a=0.7};
    o.backgroundColor = {r=0, g=0, b=0, a=0.8};
    o.width = width;
    o.height = height;
    o.anchorLeft = true;
    o.anchorRight = false;
    o.anchorTop = true;
    o.anchorBottom = false;
    o.timer = 0;
    o.clicktoSkip = false;
    return o
end

Events.OnKeyPressed.Add(TutorialMessage.onKeyPressed);
