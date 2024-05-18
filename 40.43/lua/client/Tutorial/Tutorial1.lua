Tutorial1 = {}

Tutorial1.Add = function()

end

Tutorial1.PreloadInit = function()
--  SandboxVars.ZombieLore.Speed = 1;
end

Tutorial1.AddPlayer = function(p)

    if getCore():isDedicated() then return end

    p:setDir(IsoDirections.SE);
    --local pl = getPlayer();

--  local torch = pl:getInventory():AddItem("Base.Torch");

--local pistol = pl:getInventory():AddItem("Base.Schoolbag");
--  pl:getInventory():AddItems("Base.ShotgunShells", 5);
--   torch:setActivated(true);
--    torch:setLightStrength(torch:getLightStrength() / 1.5);
--  pl:setSecondaryHandItem(torch);
-- pl:setPrimaryHandItem(pistol);
end


Tutorial1.Init = function()

    Tutorial1.steps = LuaList:new();

    Tutorial1.steps:add(WelcomeStep:new())
    Tutorial1.steps:add(WalkToAdjacent:new())
    Tutorial1.steps:add(InventoryLootingStep:new());
    Tutorial1.steps:add(InventoryUseStep:new());
    Tutorial1.steps:add(FightStep:new());

    Tutorial1.steps:get(0):begin();

    Events.OnTick.Add(Tutorial1.Tick)
    Tutorial1.FillContainers();
end

Tutorial1.cratePositions = {{"lootingStuff", "counter", 157, 152, 0}, {"empty", "counter", 157, 156, 0}, {"empty", "counter", 157, 155, 0}, {"empty", "counter", 157, 154, 0}, {"empty", "counter", 157, 153, 0}, {"empty", "counter", 158, 152, 0}};
Tutorial1.FillContainers = function()
    for k, v in ipairs(Tutorial1.cratePositions) do
        local type = v[1];
        local container = v[2];
        local x = v[3];
        local y = v[4];
        local z = v[5];
        local sq = getCell():getGridSquare(x, y, z);
        if sq ~= nil then
            local objs = sq:getObjects();
            for i = 0, objs:size()-1 do
                local o = objs:get(i);
                local c = o:getContainer();
                if c ~= nil and c:getType() == container then
                    c:emptyIt();
                    if type == "lootingStuff" then
                        local apple = c:AddItem("Base.DeadMouse");
                        apple:setAge(17);
                        Tutorial1.DeadMouse = apple
                        c:AddItem("Base.WaterBottleEmpty");
                    end
                    c:setExplored(true);
                end
            end
        end
    end
end

Tutorial1.SpawnZombies = function(count)

end

Tutorial1.Render = function()

end

Tutorial1.Tick = function()
    if getPlayer() == nil then return end;

    if Tutorial1.steps == nil or Tutorial1.steps:isEmpty() then return; end
    if Tutorial1.steps:get(0):isComplete() then
        Tutorial1.steps:get(0):finish();
        Tutorial1.steps:removeAt(0);

        if not Tutorial1.steps:isEmpty() then
            Tutorial1.steps:get(0):begin();
        end
    end
end

Tutorial1.name = "Tutorial1";
--Tutorial1.description = "Prepare for a short, sharp dose of Zomboid. Deadheads are approaching from every angle.\nYou're going to check out, and soon, but just how long can you resist the horde?\nReady that shotgun...";
Tutorial1.image = "media/lua/LastStand/Challenge1.png";
Tutorial1.world = "challengemaps/Challenge1";
Tutorial1.xcell = 0;
Tutorial1.ycell = 0;
Tutorial1.x = 158.5;
Tutorial1.y = 157.5;
Tutorial1.hourOfDay = 20;
--Tutorial1.cratePositions = { {"weapons3", "crate", 151, 152, 0},{"weapons2", "crate", 142, 148, 0}, {"weapons1", "crate", 147+3, 151+3, 1}, {"medicine", "crate", 156+3, 144+3, 1}, {"carpentry", "crate", 135, 179, 0}, {"carpentry", "crate", 157, 151, 0}, {"carpentry", "crate", 158, 151, 0}}
--Events.OnChallengeQuery.Add(Tutorial1.Add)
