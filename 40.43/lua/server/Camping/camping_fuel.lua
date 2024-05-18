-- This is a list of all the item types and categories that can be burned in a fire.
-- The value is the number of hours of fuel added to a fire.
campingFuelType = {
    Log = 4.0,
    PercedWood = 2.0,
    Plank = 2.0,
    RippedSheets = 5/60.0,
    RippedSheetsDirty = 5/60.0,
    Sheet = 15/60.0,
    SheetPaper = 5/60.0,
    Socks = 5/60.0,
    Socks2 = 5/60.0,
    WoodenStick = 0.25,
    TreeBranch = 1.0,
    UnusableWood = 1.4,
    Twigs = 0.25,
}
campingFuelCategory = {
    Clothing = 15/60.0,
    Literature = 15/60.0
}

-- Types of items that can be used with a lighter/matches to start a fire.
campingLightFireType = {
    RippedSheets = 5/60.0,
    RippedSheetsDirty = 5/60.0,
    Sheet = 15/60.0,
    SheetPaper = 5/60.0,
    Socks = 5/60.0,
    Socks2 = 5/60.0,
    Twigs = 15/60.0,
    Shoes = -1, -- Shoes (Clothing category) cannot be used
}
campingLightFireCategory = {
    Clothing = 15/60.0,
    Literature = 15/60.0,
}

