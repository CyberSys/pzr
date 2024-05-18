require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

ChatOnscreen = ISPanel:derive("ChatOnscreen");

function ChatOnscreen:initialise()
    ISPanel.initialise(self);
end

--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function ChatOnscreen:createChildren()


    self.textEntry = ISTextEntryBox:new("",self.x, self.y + (18*5), 1000, 18);
    self.textEntry:initialise();
    self.textEntry:instantiate();
    self.textEntry.backgroundColor = {r=0, g=0, b=0, a=0.0};
    self.textEntry.borderColor = {r=1, g=1, b=1, a=0.0};
    self.textEntry:setVisible(false);
    self.textEntry:setAnchorTop(false);
    self.textEntry:setAnchorBottom(true);
    self.textEntry.onCommandEntered = ChatOnscreen.onCommandEntered;
    self.textEntry:addToUIManager();
end

function ChatOnscreen:onCommandEntered()
    local command = ChatOnscreen.instance.textEntry:getText();
    ChatOnscreen.instance.textEntry:clear();
    getPlayer():Say(command);
    ChatOnscreen.instance.textEntry:unfocus();
    ChatOnscreen.instance.textEntry:setVisible(false);
    ChatOnscreen.instance.textEntry:setText("");
end


function ChatOnscreen:render()

end


function ChatOnscreen:new (x, y, width, height)
    local o = {}
    --o.data = {}
    o = ISPanel:new(x, y, width, height);

    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.y = y;
    o.backgroundColor = {r=0, g=0, b=0, a=0.0};
    o.borderColor = {r=1, g=1, b=1, a=0.0};
    o.width = width;
    o.height = height;
    o.anchorLeft = true;
    o.anchorRight = false;
    o.anchorTop = true;
    o.anchorBottom = false;
    o.addY = 0;
    return o
end


function LoadChatIngamePanel()

    local h = getCore():getScreenHeight();

    ChatOnscreen.instance = ChatOnscreen:new(32, h - 130, 1, 1);
    ChatOnscreen.instance:initialise();
    ChatOnscreen.instance:setAnchorTop(false);
    ChatOnscreen.instance:setAnchorBottom(true);
    ChatOnscreen.instance:addToUIManager();

end


function onToggleChatBox(key)
    -- T
    if ChatOnscreen.instance==nil then return;end
    if key == 20 then
        ChatOnscreen.instance.textEntry:setVisible(true);

        ChatOnscreen.instance.textEntry:focus();
        ChatOnscreen.instance.textEntry:ignoreFirstInput();
    end
end


function closeBox(x, y)
    if ChatOnscreen.instance==nil then return;end

    ChatOnscreen.instance.textEntry:unfocus();
    ChatOnscreen.instance.textEntry:setVisible(false);
    ChatOnscreen.instance.textEntry:setText("");
end

--Events.OnKeyPressed.Add(onToggleChatBox);
--Events.OnGameStart.Add(LoadChatIngamePanel);
--
--Events.OnMouseDown.Add(closeBox);

