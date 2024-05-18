
StaticRelationshipModifiers = {

    plotted_against_me =
    {
        modifier = -40
    },
    loyal_to_me =
    {
        modifier = 40
    },
    killer =
    {
        modifier = -50
    },
    suspected_killer =
    {
        modifier = -30
    },
    killed_friend =
    {
        modifier = -50
    },
    killed_family =
    {
        modifier = -100
    },
    suspect_killed_friend =
    {
        modifier = -30
    },
    suspect_killed_family =
    {
        modifier = -60
    },
    stole_leadership =
    {
        modifier = -30
    },
    insulted =
    {
        modifier = -20,
        timeout = 6 -- 6 days...
    },
    bonded =
    {
        modifier = 20,
        timeout = 6 -- 6 days...
    }

}

TraitRelationshipModifiers = {

    {
        A="Insurbordinate",
        Test=function(a,b) return b:isLeaderOf(a) end,
        modifier= -50,
    },
    {
        A="Aggressive",
        B="Coward",
        modifier= -60,
    },
    {
        A="Coward",
        B="Aggressive",
        modifier= -60,
    },
    {
        A="Aggressive",
        B="Aggressive",
        modifier= 30,
    },
    {
        A="Tough",
        B="Tough",
        modifier= 30,
    },
    {
        A="Aggressive",
        B="Nervous",
        modifier= -30,
    },
    {
        A="Nervous",
        B="Aggressive",
        modifier= -30,
    },
    {
        A="Quiet",
        B="Loud",
        modifier= -20,
    },
    {
        A="Loud",
        B="Quiet",
        modifier= -20,
    },
    {
        A="Loyal",
        B="Loyal",
        modifier= 30,
    },
    {
        B="Friendly",
        modifier= 30,
    },
    {
        B="Depressed",
        modifier= -20,
    },
    {
        B="Unstable",
        modifier= -20,
    },
    {
        B="Insane",
        modifier= -50,
    },
    {
        A="Trusting",
        modifier= 50,
    },
    {
        B="Shifty",
        modifier= -15,
    },
    {
        B="Brave",
        modifier= 25,
    },
    {
        B="Confident",
        modifier= 25,
    },
    {
        B="Nervous",
        modifier= -15,
    },
    {
        B="Kind-hearted",
        modifier= 20,
    },
    {
        B="Charasmatic",
        modifier= 50,
    },
    {
        A="Kind-hearted",
        B="Cruel",
        modifier= -100,
    },
    {
        B="Cruel",
        modifier= -40,
    },
}
