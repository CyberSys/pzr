require "ISUI/ISPanel"

if debugScenarios == nil then
    debugScenarios = {}
end

DebugScenarios = ISPanel:derive("DebugScenarios");

selectedDebugScenario = nil;

function DebugScenarios:createChildren()
    self.listbox = ISScrollingListBox:new(16, 16, self:getWidth()-32, self:getHeight()-32);
    self.listbox:initialise();
    self.listbox:instantiate();

    self.listbox.itemheight = 24;
    self.listbox.drawBorder = true;
    self.listbox.doDrawItem = DebugScenarios.drawItem;
    self.listbox:setOnMouseDoubleClick(self, DebugScenarios.onDblClickOption);
    for k,v in pairs(debugScenarios) do
        self.listbox:addItem(v.name, k);
    end

    self:addChild(self.listbox);
end

function DebugScenarios:drawItem(y, item, alt)

    if self.selected == item.index then
        self:drawRect(0, (y), self:getWidth(), item.height-1, 1, 0,0,0);
    end
    self:drawRectBorder(0, y, self:getWidth(), item.height-1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    -- the name of the story
    self:drawText(item.text, 16, y, 0.6, 0.7, 0.9, 1.0, UIFont.Medium);

    return y+item.height;
end

function DebugScenarios:new (x, y, width, height)
    local o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.y = y;
    o.backgroundColor = {r=0.0, g=0.05, b=0.1, a=1.0};
    o.borderColor = {r=1, g=1, b=1, a=0.7};

    return o
end

function DebugScenarios:onDblClickOption(option)

    local scenario = debugScenarios[option];

    self:launchScenario(scenario);
end

function DebugScenarios:launchScenario(scenario)
    MainScreen.instance:setBeginnerPreset();
    
    if(scenario ~= nil) then
        selectedDebugScenario = scenario;
    end
    
    if selectedDebugScenario.setSandbox ~= nil then
        selectedDebugScenario.setSandbox();
    end
    local worldName = ZombRand(100000)..ZombRand(100000)..ZombRand(100000)..ZombRand(100000);
    getWorld():setWorld(worldName);
    createWorld(worldName);
    GameWindow.doRenderEvent(false);
    forceChangeState(GameLoadingState.new());
    DebugScenarios.instance:setVisible(false);
end

function doDebugScenarios()
    local x = getCore():getScreenWidth() / 2;
    local y = getCore():getScreenHeight() / 2;
    x = x - 250;
    y = y - 250;
    local debug = DebugScenarios:new(x, y, 500, 500);
    debug:addToUIManager();
    DebugScenarios.instance = debug;

    -- check if any scenarios have the forceLaunch option, in this case we launch it directly, save more clicks!
    for i,v in pairs(debugScenarios) do
        if v.forceLaunch then
            DebugScenarios.instance:launchScenario(v);
        end
    end
end


function DebugScenarios.ongamestart()
    if selectedDebugScenario and selectedDebugScenario.onStart then
        selectedDebugScenario.onStart();
    end
end

Events.OnNewGame.Add(DebugScenarios.ongamestart);