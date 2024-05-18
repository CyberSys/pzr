-- =============================================================================
-- Profession Clothing
-- by RoboMat
-- 
-- Created: 23.09.13 - 15:57
-- =============================================================================

-- This table contains the clothing variables of the starting
-- professions. If no custom values are found base values will
-- be used. Each profession MUST consist of the following tables:
--
-- profession ={
--          male={
--				topCol={},
--				bottomCol{},
--          },
--          female={
--				topCol={},
--				bottomCol{},
--          },
-- }
--
-- The police officer profession is an example of how to use every
-- value.
-- by RoboMat
ProfessionClothing = {
	-- Police Officer
	-- Male: Blue Shirt, Dark Grey Trousers
	-- Female: Blue Blouse, Grey Skirt
	policeofficer = {
		male = {
			topPal = "Shirt_White",
			top = "Shirt",
			bottomPal = "Trousers_White",
			bottom = "Trousers",
			topCol = {
				r = 0.1725,
				g = 0.2745,
				b = 0.5411,
			},
			bottomCol = {
				r = 0.3600,
				g = 0.3600,
				b = 0.3600,
			},
		},
		female = {
			topPal = "Blouse_White",
			top = "Blouse",
			bottomPal = "Skirt_White",
			bottom = "Skirt",
			topCol = {
				r = 0.1725,
				g = 0.2745,
				b = 0.5411,
			},
			bottomCol = {
				r = 0.4705,
				g = 0.4705,
				b = 0.4705,
			},
		},
	},

	-- Construction Worker
	-- Male: Shirtless, Blue Trousers
	-- Female: Blue Vest, Blue Trousers
	constructionworker = {
		male = {
			topPal = "",
			top = "",
			topCol = {},
			bottomCol = {
				r = 0.1725,
				g = 0.2745,
				b = 0.5411,
			},
		},
		female = {
			topCol = {
				r = 0.1725,
				g = 0.2745,
				b = 0.5411,
			},
			bottomCol = {
				r = 0.1725,
				g = 0.2745,
				b = 0.5411,
			},
		},
	},

	-- Fire Officer
	-- Male: Black Shirt, White Trousers
	-- Female: Black Blouse, White Trousers
	fireofficer = {
		male = {
			topPal = "Shirt_White",
			top = "Shirt",
			topCol = {
				r = 0.1000,
				g = 0.1000,
				b = 0.1000,
			},
			bottomCol = {
				r = 1.0000,
				g = 1.0000,
				b = 1.0000,
			},
		},
		female = {
			topPal = "Blouse_White",
			top = "Blouse",
			topCol = {
				r = 0.1000,
				g = 0.1000,
				b = 0.1000,
			},
			bottomCol = {
				r = 1.0000,
				g = 1.0000,
				b = 1.0000,
			},
		},
	},

	-- Park Ranger
	-- Male: Sand Shirt, Brown Trousers
	-- Female: Sand Vest, Brown Trousers
	parkranger = {
		male = {
			topPal = "Shirt_White",
			top = "Shirt",
			topCol = {
				r = 0.5058,
				g = 0.3333,
				b = 0.1686,
			},
			bottomCol = {
				r = 0.3019,
				g = 0.1725,
				b = 0.0470,
			},
		},
		female = {
			topCol = {
				r = 0.5058,
				g = 0.3333,
				b = 0.1686,
			},
			bottomCol = {
				r = 0.3019,
				g = 0.1725,
				b = 0.0470,
			},
		},
	},

	-- Security Guard
	-- Male: Black Shirt, Blue Trousers
	-- Female: Blue Blouse, Black Trousers
	securityguard = {
		male = {
			topPal = "Shirt_White",
			top = "Shirt",
			topCol = {
				r = 0.1960,
				g = 0.1960,
				b = 0.1960,
			},
			bottomCol = {
				r = 0.1725,
				g = 0.2745,
				b = 0.5411,
			},
		},
		female = {
			topPal = "Blouse_White",
			top = "Blouse",
			topCol = {
				r = 0.1725,
				g = 0.2745,
				b = 0.5411,
			},
			bottomCol = {
				r = 0.1960,
				g = 0.1960,
				b = 0.1960,
			},
		},
	}
}
