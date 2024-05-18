if debugScenarios == nil then
    debugScenarios = {}
end


debugScenarios.DebugScenario = {
    name = "Basic Debug Scenario",
--    forceLaunch = true, -- use this to force the launch of THIS scenario right after main menu was loaded, save more clicks! Don't do multiple scenarii with this options
--      startLoc = {x=13538, y=5759, z=0 }, -- Mall
--    startLoc = {x=10145, y=12763, z=0 }, -- lighting test
--    startLoc = {x=3645, y=8564, z=0 }, -- Roadtrip start
--          startLoc = {x=10645, y=10437, z=0 }, -- Police station
--      startLoc = {x=13926, y=5877, z=0 }, -- Mall bookstore
--    startLoc = {x=10862, y=10290, z=0 }, -- Mul
        startLoc = {x=10580,y=11193,z=0}, -- car crash
--    startLoc = {x=11515, y=8830, z=0 }, -- DIxie gas station
--    startLoc = {x=10657, y=10625, z=0 }, -- Muldraugh gas station
--    startLoc = {x=5796, y=5384, z=0 }, -- Junkyard
--    startLoc = {x=10835, y=10144, z=0 }, -- middle of muldraugh
    setSandbox = function()
        SandboxVars.VehicleEasyUse = true;
--        SandboxVars.ChanceHasGas = 1;*
--        SandboxVars.InitialGas = 2;
--        SandboxVars.Zombies = 1;
--        SandboxVars.Distribution = 1;
--        SandboxVars.DayLength = 3;
--        SandboxVars.StartMonth = 12;
        --        SandboxVars.WaterShutModifier = -1;
--        SandboxVars.ElecShutModifier = -1;
--        SandboxVars.StartTime = 2;
--    SandboxVars.TimeSinceApo = 7;
--        SandboxVars.CarSpawnRate = 2;
--        SandboxVars.LockedCar = 1;
--    SandboxVars.CarAlarm = 1;
--    SandboxVars.ChanceHasGas = 1;
--    SandboxVars.InitialGas = 2;
--    SandboxVars.CarGeneralCondition = 1;
--    SandboxVars.RecentlySurvivorVehicles = 1;
--        SandboxVars.Zombies = 1;
--        SandboxVars.AllowExteriorGenerator = true;
--        SandboxVars.FoodLoot = 1;
--        SandboxVars.WeaponLoot = 1;
--        SandboxVars.OtherLoot = 1;
--        SandboxVars.Temperature = 3;
--        SandboxVars.Rain = 3;
--        --    SandboxVars.erosion = 12
--        SandboxVars.ErosionSpeed = 1
--        SandboxVars.XpMultiplier = "1.0";
--        SandboxVars.Farming = 3;z
--        SandboxVars.NatureAbundance = 5;
--        SandboxVars.PlantResilience = 3;
--        SandboxVars.PlantAbundance = 3;
--        SandboxVars.Alarm = 3;
--        SandboxVars.LockedHouses = 3;
--        SandboxVars.FoodRotSpeed = 3;
--        SandboxVars.FridgeFactor = 3;
--        SandboxVars.ZombiesRespawn = 2;
--        SandboxVars.LootRespawn = 1;
--        SandboxVars.StatsDecrease = 3;
--        SandboxVars.StarterKit = false;
--        SandboxVars.TimeSinceApo = 13;
--
--
    end,
    onStart = function()
        getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");getPlayer():getInventory():AddItem("Base.WhiskeyFull");
    end
}
