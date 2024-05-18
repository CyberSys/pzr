require "ISUI/ISPanel"

MetaWorldRenderPanel = ISPanel:derive("MetaWorldRenderPanel");

--************************************************************************--
--** ISComboBox:initialise
--**
--************************************************************************--

function MetaWorldRenderPanel:initialise()
	ISPanel.initialise(self);
end


function MetaWorldRenderPanel:render()

	--getWorld():setDrawWorld(false);

	local buildings = SadisticAIDirector.instance.buildings;

	local zoom = self.zoom;
	for i=0, buildings:size() - 1 do
		local b = buildings:get(i);
		local rooms = b:getRooms();
		for k=0, rooms:size() - 1 do
			local r = rooms:get(k);

			local mm = r:getRects():size();
			for m=0, mm-1 do
				local rect = r:getRects():get(m);
				local x1 = rect:getX();
				local y1 = rect:getY();
				local x2 = rect:getX2();
				local y2 = rect:getY2();
				if (x2 < x1) then
					local temp = x2;
					x2 = x1;
					x1 = temp;
				end
				if (y2 < y1) then
					local temp = y2;
					y2 = y1;
					y1 = temp;
				end
				local w = x2 - x1;
				local h = y2 - y1;

				x1 = x1 * zoom;
				y1 = y1 * zoom;
				w = w * zoom;
				h = h * zoom;
				self:drawRect(x1, y1, w, h, 1, 0, 0, 1);

				--    local meta = r:getMetaObjects();
				--   for l=0, meta:size() - 1 do
				--     local m = meta:get(l);
				--   if m:getType() <= 4 then
				--  self:drawRect(x1+(m:getX()*zoom), y1+(m:getY()*zoom), zoom, zoom, 1, 1, 1, 1);
				-- end

				-- end
			end
		end
		--	self:drawText("remote: "..b:getTable().remoteness, x1 + w, y1, 0, 0.5, 1.0, 1)
	end

	local groups = SadisticAIDirector.instance.groups;

	for i=0, groups:size() - 1 do
		local g = groups:get(i);

		local x = g.x;
		local y = g.y;
		x = x * zoom;
		y = y * zoom;
		local col = {r=1, g=1, b=1};

		--local leader = g:getLeader();

        --col.b = 1.0 - leader:getMeta():getBaseChanceOfViolenceDelta();

		if col.b < 0 then col.b = 0; end
		if col.b > 1 then col.b = 1; end

		col.g = col.b;


		self:drawRect(x, y, 2, 2, col.r, col.g, col.b, 1);
	--	self:drawText(g:size().."", x+3, y, col.r, col.g, col.b, 1)

        for i = 0, g.members:size() -1 do
            local mem = g.members:get(i);
            if mem ~= nil and mem:getMeta() ~= nil then
                if mem:getMeta().x ~= nil then
                    self:drawRect(mem:getMeta().x* zoom, mem:getMeta().y* zoom, 2, 2, 1, 1, 1, 1);
                end
            end

        end
	end

	self:drawRect(getPlayer():getX()- (300*10), getPlayer():getY()- (300*6), 2, 2, 1, 1, 0, 1);

end

function MetaWorldRenderPanel:new (x, y, width, height)
	local o = {}
	--o.data = {}
	o = ISPanel:new(x, y, width, height);
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.backgroundColor = {r=0, g=0, b=0, a=1};
	o.borderColor = {r=1, g=1, b=1, a=0.7};

	o.zoom = 0.2;
	return o
end


