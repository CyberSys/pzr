--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

ISGameLoadingUI = ISPanelJoypad:derive("ISGameLoadingUI")

function ISGameLoadingUI:createChildren()
	local buttonWid = 250
	local buttonHgt = 40

	local button = ISButton:new((self.width - buttonWid) / 2, self.height - 40 - buttonHgt,
		buttonWid, buttonHgt, getText("IGUI_PostDeath_Quit"), self, self.onExit)
	button:setAnchorTop(false)
	button:setAnchorLeft(false)
	button:setAnchorBottom(true)
	self:addChild(button)
	self.buttonExit = button
end

function ISGameLoadingUI:onExit()
	getCore():quit()
end

function ISGameLoadingUI:onGainJoypadFocus(joypadData)
	self:setISButtonForA(self.buttonExit)
end

function ISGameLoadingUI:onJoypadDown(button)
	ISPanelJoypad.onJoypadDown(self, button)
end

function ISGameLoadingUI:new(status)
	local x,y,w,h = 0,0,getCore():getScreenWidth(),getCore():getScreenHeight()
	local o = ISPanelJoypad:new(x, y, w, h)
	setmetatable(o, self)
	self.__index = self
	o.backgroundColor.a = 0.0
	o.status = status
	ISGameLoadingUI.instance = o
	return o
end

function ISGameLoadingUI.OnJoypadActivateUI(id)
	if JoypadState.joypads[id] then return end
	local joypadData = {}
	joypadData.id = id
	joypadData.pressed = {}
	joypadData.wasPressed = {}
	for n = 1,getButtonCount(id) do
		joypadData.pressed[n-1] = isJoypadPressed(id, n-1)
	end
	joypadData.timepressdown = 0
	joypadData.timepressup = 0
	JoypadState.joypads[id] = joypadData
	JoypadState.count = JoypadState.count + 1
	JoypadState[JoypadState.count] = joypadData
	
	joypadData.focus = ISGameLoadingUI.instance
end

function ISGameLoadingUI_OnGameLoadingUI(status)
--	MainScreen.instance:removeFromUIManager()
	local ui = ISGameLoadingUI:new(status)
	ui:initialise()
	ui:instantiate()
	ui:addToUIManager()
	GameWindow.doRenderEvent(true)

	-- All events are cleared in GameLoadingState to avoid surprises.
	-- Register only the events needed by this UI.
	LuaEventManager.AddEvent("OnJoypadActivateUI")
	LuaEventManager.AddEvent("OnRenderTick")
	Events.OnRenderTick.Add(onJoypadRenderTick)

	if JoypadState.forceActivate then
		-- Controller was activated in the main menu.
		ISGameLoadingUI.OnJoypadActivateUI(JoypadState.forceActivate)
		JoypadState.forceActivate = nil
		updateJoypadFocus(JoypadState[1])
	else
		-- Controller wasn't activated yet.
		Events.OnJoypadActivateUI.Add(ISGameLoadingUI.OnJoypadActivateUI)
	end
end

