--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

StoryNewOrLoad = ISPanel:derive("StoryNewOrLoad");

function StoryNewOrLoad:initialise()
	ISPanel.initialise(self);
end


--************************************************************************--
--** StoryNewOrLoad:instantiate
--**
--************************************************************************--
function StoryNewOrLoad:instantiate()
	self.javaObject = UIElement.new(self);
	self.javaObject:setX(self.x);
	self.javaObject:setY(self.y);
	self.javaObject:setHeight(self.height);
	self.javaObject:setWidth(self.width);
	self.javaObject:setAnchorLeft(self.anchorLeft);
	self.javaObject:setAnchorRight(self.anchorRight);
	self.javaObject:setAnchorTop(self.anchorTop);
	self.javaObject:setAnchorBottom(self.anchorBottom);
end

function StoryNewOrLoad:create()
	self.newButton = ISButton:new(16, self.height - 30, 100, 25, "NEW", self, StoryNewOrLoad.onOptionMouseDown);
	self.newButton.internal = "NEW";
	self.newButton:initialise();
	self.newButton:instantiate();
	self.newButton:setAnchorLeft(false);
	self.newButton:setAnchorRight(true);
	self.newButton:setAnchorTop(false);
	self.newButton:setAnchorBottom(true);
	self.newButton.borderColor = {r=1, g=1, b=1, a=0.1};
	self.newButton:setFont(UIFont.Small);
	self.newButton:ignoreWidthChange();
	self.newButton:ignoreHeightChange();
	self:addChild(self.newButton);

	self.loadButton = ISButton:new(self.width - 126, self.height - 30, 100, 25, "LOAD", self, StoryNewOrLoad.onOptionMouseDown);
	self.loadButton.internal = "LOAD";
	self.loadButton:initialise();
	self.loadButton:instantiate();
	self.loadButton:setAnchorLeft(false);
	self.loadButton:setAnchorRight(true);
	self.loadButton:setAnchorTop(false);
	self.loadButton:setAnchorBottom(true);
	self.loadButton.borderColor = {r=1, g=1, b=1, a=0.1};
	self.loadButton:setFont(UIFont.Small);
	self.loadButton:ignoreWidthChange();
	self.loadButton:ignoreHeightChange();
	self:addChild(self.loadButton);
end

function StoryNewOrLoad:drawMap(y, item, alt)
	self:drawRect(0, y, self:getWidth(), 30, 0.3, 0.7, 0.35, 0.15);
	return y;
end

function StoryNewOrLoad:prerender()
	ISPanel.prerender(self);
	self:drawTextCentre("NEW OR LOAD STORY", self.width / 2, 10, 1, 1, 1, 1, UIFont.Large);
end

function StoryNewOrLoad:onOptionMouseDown(button, x, y)
	 if button.internal == "BACK" then
		self:setVisible(false);
		MainScreen.instance.storyNewOrLoadScreen:setVisible(false);
		MainScreen.instance.bottomPanel:setVisible(true);
	 elseif button.internal == "LOAD" then
		self:setVisible(false);
		StoryScreen:populateListBox(MainScreen.instance.savedStory);
		MainScreen.instance.storyNewOrLoadScreen:setVisible(false);
		MainScreen.instance.storyScreen:setVisible(true);
		MainScreen.instance.storyScreen.mode = "LOAD";
	 elseif button.internal == "NEW" then
		self:setVisible(false);
		StoryScreen:populateListBox(getStoryDirectoryTable());
		MainScreen.instance.storyNewOrLoadScreen:setVisible(false);
		MainScreen.instance.storyScreen:setVisible(true);
		MainScreen.instance.storyScreen.mode = "NEW";
	 end
end


function StoryNewOrLoad:new (x, y, width, height)
	local o = {}
	--o.data = {}
	o = ISPanel:new(x, y, width, height);
	StoryNewOrLoad.instance = o;
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.backgroundColor = {r=0, g=0, b=0, a=0.3};
	o.borderColor = {r=1, g=1, b=1, a=0.2};
	o.width = width;
	o.height = height;
	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	o.itemheightoverride = {}
	o.selected = 1;
	return o
end
