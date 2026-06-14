package com.windslash.itriplanery.data

data class ItineraryStep(
    val time: String,
    val text: String,
    val meta: String,
    val cost: Int,
    val type: String, // "food", "transit", "visit", "shopping", "other"
    val details: String? = null,
    val mapQuery: String? = null,
    val id: String = "" // stable id when loaded from the database; used as the checklist key
)

data class PriorityObjective(
    val name: String,
    val type: String,
    val budget: Int,
    val query: String,
    val time: String? = null
)

data class ContingencyPlan(
    val name: String,
    val budget: Int,
    val desc: String,
    val query: String,
    val match: String? = null
)

data class MapMarker(
    val lat: Double,
    val lng: Double,
    val type: String, // "station", "food", "logistics", "visit"
    val seq: Int,
    val title: String,
    val query: String,
    val meta: String? = null
)

data class ItineraryDay(
    val date: String,
    val day: String,
    val title: String,
    val location: String,
    val steps: Int,
    val morning: List<ItineraryStep>,
    val afternoon: List<ItineraryStep>,
    val evening: List<ItineraryStep>,
    val food: PriorityObjective? = null,
    val snack: PriorityObjective? = null,
    val alts: List<ContingencyPlan> = emptyList(),
    val customAlts: List<ItineraryStep> = emptyList(),
    val markers: List<MapMarker> = emptyList()
)

object ItineraryData {
    val days = listOf(
        // Day 1
        ItineraryDay(
            date = "Oct 9 (Fri)",
            day = "Day 1",
            title = "Old Tokyo & The Market Rush",
            location = "Ueno/Yanaka/Ameyoko",
            steps = 16000,
            morning = listOf(
                ItineraryStep("07:30", "LAND at Narita (Royal Brunei).", "Arrival", 0, "logistics", "Clear immigration. Buy a bottle of green tea immediately for hydration.", "Narita Airport"),
                ItineraryStep("09:00", "TRAIN: Keisei Skyliner -> Keisei-Ueno.", "Train", 2580, "transit", "Relax and enjoy the countryside view. The fastest way into the city.", null),
                ItineraryStep("10:00", "ARRIVE Ueno. Drop Bags at Hotel.", "Logistics", 0, "logistics", "Leave bags at front desk (Check-in is 15:00). Keep valuables and passport with you.", "Keisei Ueno Station"),
                ItineraryStep("10:15", "WALK: Ueno Park (The 'Adrenaline' Walk).", "Stroll", 0, "sightseeing", "Walk through the main park avenue. Stop by <b>Toshogu Shrine</b> (Gold Shrine) if you have energy. Fresh air to fight jet lag.", "Ueno Park"),
                ItineraryStep("11:00", "WALK: To Nezu via Cemetery.", "Walk", 0, "sightseeing", "Exit the back of the park. Quiet, historic neighborhood path.", "Yanaka Cemetery"),
                ItineraryStep("11:30", "Lunch: <b>Kamachiku</b> (Nezu).", "Tier S", 2000, "food", "<b>The Hidden Gem:</b> Udon served in a 1910 stone warehouse with a garden view. Shoes off, tatami mats. Perfect peace after the flight.<br><b>Order:</b> 'Kama-age' (Hot dipping) or 'Zaru' (Cold).", "Kamachiku Nezu")
            ),
            afternoon = listOf(
                ItineraryStep("13:00", "EXPLORE: Yanaka Ginza", "Old Town", 0, "sightseeing", "Old Tokyo vibes. Eat street food snacks and look for cat-themed shops.", "Yanaka Ginza"),
                ItineraryStep("15:00", "CHECK-IN at Hotel", "Rest", 0, "logistics", "Freshen up and maybe take a quick 30m power nap. Wash face.", null),
                ItineraryStep("16:30", "EXPLORE: Ameyoko Market", "Shopping", 1000, "shopping", "Vibrant, crowded market streets under the train tracks. Get the famous Menchi Katsu.", "Ameyoko Shopping District")
            ),
            evening = listOf(
                ItineraryStep("19:00", "Dinner: Kamo to Negi", "Tier A", 1200, "food", "Rich duck-based broth with charred green onions. Try to sit at the tatami bar.<br><b>Note:</b> Expect to wait in line. Buy ticket from machine first.", "Ramen Kamo to Negi Ueno"),
                ItineraryStep("21:00", "Rest & Convenience Store", "Conbini Run", 500, "other", "Grab some water, Famichiki, and a nice dessert for the hotel.", "FamilyMart Ueno")
            ),
            food = PriorityObjective("Kamachiku & Kamo to Negi", "Udon/Ramen", 3200, "Kamachiku Nezu", "11:30"),
            snack = PriorityObjective("Ameyoko Street Food", "Snacks", 1000, "Ameyoko Shopping District", "16:30"),
            markers = listOf(
                MapMarker(35.7719, 140.3928, "transit", 1, "Narita Airport", "Narita Airport Terminal 1", "Arrival"),
                MapMarker(35.7138, 139.7773, "station", 2, "Keisei-Ueno", "Keisei Ueno Station", "Logistics"),
                MapMarker(35.7140, 139.7740, "visit", 3, "Ueno Park Walk", "Ueno Park", "Sightseeing"),
                MapMarker(35.7196, 139.7658, "food", 4, "Kamachiku Udon", "Kamachiku Nezu", "Lunch"),
                MapMarker(35.7103, 139.7745, "visit", 5, "Ameyoko Market", "Ameyoko Shopping District", "Sightseeing"),
                MapMarker(35.7095, 139.7748, "food", 6, "Kamo to Negi", "Ramen Kamo to Negi Ueno", "Dinner")
            )
        ),

        // Day 2
        ItineraryDay(
            date = "Oct 10",
            day = "Day 2",
            title = "TeamLab, Nakano & Gyukatsu",
            location = "Toyosu & Nakano",
            steps = 18000,
            morning = listOf(
                ItineraryStep("09:00", "TeamLab Planets TOKYO", "Exhibition", 3800, "visit", "Ensure tickets are booked 30 days prior. Barefoot immersive experience.", "teamLab Planets TOKYO")
            ),
            afternoon = listOf(
                ItineraryStep("12:30", "Nakano Broadway Shopping", "Anime", 0, "shopping", "Pop-culture paradise. Excellent place for high-quality figures and retro retro goods.", "Nakano Broadway"),
                ItineraryStep("14:00", "8-Layer Soft Serve & Ramen", "Snack", 800, "sweets", "Try the legendary towering ice cream at Daily Chi-co in Broadway basement.", "Daily Chi-co Nakano"),
                ItineraryStep("15:00", "Tonkatsu Maruyama Nakano", "Late Lunch", 1800, "food", "Crispy custom pork loin cutlet. Eat with salt instead of sauce to enjoy the sweet meat.", "Tonkatsu Maruyama Nakano")
            ),
            evening = listOf(
                ItineraryStep("18:30", "Gyukatsu Aona rare beef", "Dinner", 2200, "food", "Crispy deep-fried rare Wagyu beef cutlets served with wasabi and soy sauce.", "Gyukatsu Aona Okachimachi"),
                ItineraryStep("20:30", "Shinobazu Pond Stroll", "Night walk", 0, "visit", "Historic pond next to Ueno Park. Beautiful views of skyscrapers reflecting on water.", "Shinobazu Pond")
            ),
            food = PriorityObjective("Gyukatsu Aona", "Beef Cutlet", 2200, "Gyukatsu Aona Ueno", "18:30"),
            snack = PriorityObjective("Daily Chi-co", "8-Layer Ice Cream", 800, "Daily Chi-co Broadway", "14:00"),
            alts = listOf(
                ContingencyPlan("Tonkatsu Maruyama", 1800, "Famed crispy Nakano Pork Loin", "Tonkatsu Maruyama Nakano")
            ),
            markers = listOf(
                MapMarker(35.6436, 139.7891, "visit", 1, "TeamLab Planets", "teamLab Planets TOKYO", "Barefoot digital art"),
                MapMarker(35.7088, 139.6657, "shopping", 2, "Nakano Broadway", "Nakano Broadway", "Pop culture shopping"),
                MapMarker(35.7086, 139.7735, "food", 3, "Gyukatsu Aona", "Gyukatsu Aona Okachimachi", "Rare Beef Cutlet")
            )
        ),

        // Day 3
        ItineraryDay(
            date = "Oct 11",
            day = "Day 3",
            title = "Seafood, Shiba Sky Sunset",
            location = "Toyosu, Odaiba & Shibuya",
            steps = 22000,
            morning = listOf(
                ItineraryStep("08:00", "Toyosu Seafood Market", "Seafood", 0, "visit", "Check out the huge tuna auction viewing screens and historic displays.", "Toyosu Market"),
                ItineraryStep("09:00", "Iso Sushi Omakase Set", "Breakfast", 5000, "food", "Avoid massive queue of Sushi Dai. Iso Sushi has incredibly fresh catches directly from market.", "Iso Sushi Toyosu")
            ),
            afternoon = listOf(
                ItineraryStep("12:00", "Odaiba Seaside Exploration", "Sightseeing", 0, "visit", "Take the driverless Yurikamome Line. Standard front seat seat is highly recommended.", "Odaiba Marine Park"),
                ItineraryStep("14:00", "Tamagoyaki Stick at Tsukiji", "Snack", 300, "street", "Freshly baked hot, sweet omelet on a stick.", "Marutake Tsukiji"),
                ItineraryStep("16:20", "Shibuya Sky Sunset View", "Skydeck", 2200, "visit", "Book exact 16:20 sunset slot 30 days out. Incredible 360 views.", "Shibuya Sky")
            ),
            evening = listOf(
                ItineraryStep("18:30", "Nikuya no Daidokoro Wagyu", "Dinner", 6500, "food", "Celebration Wagyu AYCE premium plate. Mention Anniversary for special meat cake.", "Nikuya no Daidokoro Shibuya"),
                ItineraryStep("21:00", "Explore Shibuya crossing", "Nightlife", 0, "visit", "Walk the world's busiest pedestrian crossing.", "Shibuya Crossing")
            ),
            food = PriorityObjective("Nikuya no Daidokoro", "Wagyu AYCE", 6500, "Nikuya no Daidokoro Shibuya", "18:30"),
            snack = PriorityObjective("Iso Sushi", "Omakase", 5000, "Iso Sushi Toyosu", "09:00"),
            alts = listOf(
                ContingencyPlan("Shibuya Yushoku", 1500, "Sweet Japanese curry or hamburger steak.", "Zuicho Shibuya")
            ),
            markers = listOf(
                MapMarker(35.6445, 139.7821, "visit", 1, "Toyosu Market", "Toyosu Market", "Tuna auctions & seafood"),
                MapMarker(35.6598, 139.7023, "visit", 2, "Shibuya Sky", "Shibuya Sky", "Sunset observatory deck"),
                MapMarker(35.6601, 139.7011, "food", 3, "Nikuya no Daidokoro", "Nikuya no Daidokoro Shibuya", "Celebrating Wagyu Feast")
            )
        ),

        // Day 4
        ItineraryDay(
            date = "Oct 12",
            day = "Day 4",
            title = "Shibuya Crossing & Fluffy Pancakes",
            location = "Shibuya & Harajuku",
            steps = 15000,
            morning = listOf(
                ItineraryStep("10:00", "Flipper's Souffle Pancakes", "Brunch", 1600, "sweets", "Aesthetic fluffy pancakes that wobble as you move. Get fresh fruit options.", "Flipper's Shibuya")
            ),
            afternoon = listOf(
                ItineraryStep("12:00", "Meiji Jingu Shrine & Harajuku", "Walk", 0, "visit", "Lush forest shrine in center of Tokyo. exit leads into trendy Takeshita market.", "Meiji Jingu"),
                ItineraryStep("13:30", "Kiwamiya Hamburg Steak", "Lunch", 1800, "food", "Customize the cook of your raw beef mince on hot searing stones. Super flavorful.", "Kiwamiya Hamburg Shibuya Parco"),
                ItineraryStep("15:30", "Cremia Premium Soft Serve", "Sweets", 600, "sweets", "Richest Hokkaido milk ice cream inside a sweet butter cookie cone.", "CREMIA Shibuya")
            ),
            evening = listOf(
                ItineraryStep("18:30", "Chiikawa Ramen Theme Night", "Dinner", 1500, "food", "予約制 theme cafe ramen serving decorated animal character bowls.", "Chiikawa Ramen Shibuya"),
                ItineraryStep("20:30", "Miyashita Park hanging out", "Recreation", 0, "visit", "Modern rooftop community park. Grab cheap convenience snacks and relax.", "Miyashita Park")
            ),
            food = PriorityObjective("Kiwamiya Hamburg", "Stone Sear Beef", 1800, "Kiwamiya Shibuya Parco", "13:30"),
            snack = PriorityObjective("Flipper's", "Souffle Pancake", 1600, "Flipper's Shibuya", "10:00"),
            alts = listOf(
                ContingencyPlan("Zuicho Katsudon", 1500, "Sweet egg layered crispy pork cutlet on rice.", "Zuicho Shibuya")
            ),
            markers = listOf(
                MapMarker(35.6701, 139.7027, "visit", 1, "Meiji Jingu", "Meiji Jingu", "Forest Shrine walkthrough"),
                MapMarker(35.6618, 139.6985, "food", 2, "Kiwamiya Hamburg", "Kiwamiya Hamburg Shibuya Parco", "Hot searing stone plates"),
                MapMarker(35.6620, 139.7022, "visit", 3, "Miyashita Park", "Miyashita Park", "Rooftop leisure zone")
            )
        ),

        // Day 5
        ItineraryDay(
            date = "Oct 13",
            day = "Day 5",
            title = "Kamakura Coastal Escape",
            location = "Kamakura, Enoshima",
            steps = 20000,
            morning = listOf(
                ItineraryStep("08:00", "Odakyu Romancecar to Kamakura", "Transit", 1000, "transit", "Coastal retro train. Enjoy ocean views out of Enoshima windows.", "Shinjuku Station"),
                ItineraryStep("09:30", "Giraffa Cheese Curry Bread", "Snack", 450, "street", "Incredible hot curry bread with massive mozzarella cheese pull.", "Giraffa Kamakura")
            ),
            afternoon = listOf(
                ItineraryStep("11:30", "Unana Butter Eel Onigiri", "Lunch", 800, "food", "Fresh buttery grilled unagi skewered on charcoal onigiri. Very unique.", "Unana Kamakura"),
                ItineraryStep("13:00", "Kotoku-in Great Buddha", "Shrine", 300, "visit", "Famous giant bronze Amida outdoor Buddha statue.", "Kotoku-in"),
                ItineraryStep("15:00", "Wasai Yakura Shirasu Bowl", "Lunch", 1800, "food", "Enjoy Kamakura's local whitebait specialty served raw or boiled.", "Wasai Yakura")
            ),
            evening = listOf(
                ItineraryStep("18:30", "Isomaru Suisan Shellfish BBQ", "Dinner", 3000, "food", "Grill your own fresh crabs, shells, scallops, and squid directly on desk stoves.", "Isomaru Suisan Ueno"),
                ItineraryStep("21:00", "Starlight Izakaya strolls", "Drinks", 1000, "other", "Relax near alleyway bars in Ueno.", "Ueno")
            ),
            food = PriorityObjective("Wasai Yakura", "Whitebait Special", 1800, "Wasai Yakura", "15:00"),
            snack = PriorityObjective("Giraffa", "Cheese Curry Bread", 450, "Giraffa Kamakura", "09:30"),
            alts = listOf(
                ContingencyPlan("Caraway Curry", 1100, "Spicy, velvety thick classic Japanese beef curry.", "Caraway Kamakura")
            ),
            markers = listOf(
                MapMarker(35.3191, 139.5505, "visit", 1, "Kamakura Station", "Kamakura Station", "Start of coastal day"),
                MapMarker(35.3168, 139.5361, "visit", 2, "Giant Buddha Kotoku-in", "Kotoku-in", "Outdoor bronze statue"),
                MapMarker(35.3218, 139.5528, "food", 3, "Unana Eel", "Unana Kamakura", "Charcoal skewered onigiri")
            )
        ),

        // Day 6
        ItineraryDay(
            date = "Oct 14",
            day = "Day 6",
            title = "Ginza Luxury & Monja",
            location = "Ginza & Tsukishima",
            steps = 14000,
            morning = listOf(
                ItineraryStep("09:30", "Ginza Showroom Stroll", "Shopping", 0, "shopping", "Walk down Ginza high streets. Check out multi-floor global flagship stores.", "Ginza Six"),
                ItineraryStep("10:30", "Hanayama Udon flat noodles", "Lunch", 1800, "food", "Extremely wide Himokawa udon served in custom badger clay bowls. Pull tickets early.", "Hanayama Udon Ginza")
            ),
            afternoon = listOf(
                ItineraryStep("13:30", "age.3 Fried Whipped Sandwich", "Dessert", 600, "sweets", "Crispy deep-fried sweet bread sandwich loaded with thick cream and strawberry toppings.", "age.3 Ginza"),
                ItineraryStep("15:00", "Rengatei historic Lunch", "Yoshoku", 2200, "food", "The birth place of westernized Japanese dishes. Get the signature fluffy Omurice.", "Rengatei")
            ),
            evening = listOf(
                ItineraryStep("18:00", "Monja Moheji grill dining", "Dinner", 2500, "food", "Try savoury liquid shredded cabbage pan pancake cooked right infront of you on iron.", "Monja Moheji Tsukishima"),
                ItineraryStep("20:30", "Odaiba Rainbow Bridge views", "Scenic", 0, "visit", "Enjoy gorgeous city skylines shining over harbor waters.", "Odaiba Marine Park")
            ),
            food = PriorityObjective("Monja Moheji", "Mentaiko Monja", 2500, "Monja Moheji Tsukishima", "18:00"),
            snack = PriorityObjective("Hanayama Udon", "Super Wide Udon", 1800, "Hanayama Udon Ginza", "10:30"),
            alts = listOf(
                ContingencyPlan("Rengatei Cafe", 2200, "Yoshoku omurice history", "Rengatei Ginza")
            ),
            markers = listOf(
                MapMarker(35.6696, 139.7661, "food", 1, "Hanayama Udon Ginza", "Hanayama Udon Ginza", "Famed flat rustic udon"),
                MapMarker(35.6708, 139.7645, "sweets", 2, "age.3 Ginza", "age.3 Ginza", "Crispy whipped dessert"),
                MapMarker(35.6644, 139.7831, "food", 3, "Monja Moheji", "Monja Moheji Tsukishima", "Self-grilled tabletop monja")
            )
        ),

        // Day 7
        ItineraryDay(
            date = "Oct 15",
            day = "Day 7",
            title = "Mt. Fuji Scenic Expedition",
            location = "Lake Kawaguchiko",
            steps = 15000,
            morning = listOf(
                ItineraryStep("07:30", "Fuji Excursion express train", "Express", 4100, "transit", "Express directly from Shinjuku into Kawaguchiko. Reserve seats early.", "Shinjuku Station"),
                ItineraryStep("10:00", "Rent Electric Cycles at Station", "Logistics", 1500, "transit", "Best way to explore around the lake without relying on traffic buses.", "Kawaguchiko Station")
            ),
            afternoon = listOf(
                ItineraryStep("11:30", "Tempura Idaten cheap bowls", "Lunch", 1200, "food", "Crispy light tempura bowls made fresh. Quick and extremely affordable.", "Tempura Idaten"),
                ItineraryStep("13:00", "Oishi Park lake stroll", "Fuji View", 0, "visit", "Panoramic view of the volcano perfectly aligned across blue purple flower gardens.", "Oishi Park"),
                ItineraryStep("14:30", "Blueberry Soft Serve treat", "Sweets", 500, "sweets", "Freshly picked tangy blueberry flavored ice cream reflecting snow caps.", "Oishi Park")
            ),
            evening = listOf(
                ItineraryStep("17:30", "Sanrokuen Fire Pit roasting", "Dinner", 4000, "food", "Incredibly rustic 150-year-old minshuku. Roast thick skewers over open charcoal hearth.", "Sanrokuen"),
                ItineraryStep("20:00", "Return Train Shinjuku base", "Transit", 4100, "transit", "Relaxing trip back to Tokyo core.", "Kawaguchiko Station")
            ),
            food = PriorityObjective("Sanrokuen", "Robatayaki Pit", 4000, "Sanrokuen", "17:30"),
            snack = PriorityObjective("Blueberry Soft Serve", "Tangy Blueberry", 500, "Oishi Park", "14:30"),
            alts = listOf(
                ContingencyPlan("Hoto Fudou", 1500, "Traditional flat pumpkin stew in cast iron pot.", "Hoto Fudou Kawaguchiko")
            ),
            markers = listOf(
                MapMarker(35.4981, 138.7684, "station", 1, "Kawaguchiko Station", "Kawaguchiko Station", "Fuji rental bike point"),
                MapMarker(35.5229, 138.7538, "visit", 2, "Oishi Park", "Oishi Park", "Scenic lake park overlooking Mt. Fuji"),
                MapMarker(35.4921, 138.7611, "food", 3, "Sanrokuen Skewers", "Sanrokuen", "charcoal hearth grilling")
            )
        ),

        // Day 8
        ItineraryDay(
            date = "Oct 16",
            day = "Day 8",
            title = "Otaku Paradise & Shinjuku Alleys",
            location = "Akihabara & Shinjuku",
            steps = 22000,
            morning = listOf(
                ItineraryStep("09:30", "Akihabara Figurine Hunting", "Shopping", 0, "shopping", "Check out Mandarake, Radio Kaikan, and AmiAmi. Compare prices before buying.", "Akihabara Station"),
                ItineraryStep("11:00", "Naruto Taiyaki crispy cake", "Snack", 300, "street", "Crispy fish-shaped waffle pastry filled with sweet red bean or purple potato.", "Naruto Taiyaki Akihabara")
            ),
            afternoon = listOf(
                ItineraryStep("13:00", "Sunshine City Ikebukuro", "Acg", 0, "shopping", "Explore Pokemon Center Mega Tokyo, One Piece Store, and Ghibli shop.", "Sunshine City"),
                ItineraryStep("14:30", "Uchitateya thick Udon bowl", "Late Lunch", 1400, "food", "Insanely thick, chewy Musashino style noodles served in boiling claypots.", "Uchitateya Ikebukuro")
            ),
            evening = listOf(
                ItineraryStep("18:00", "Omoide Yokocho Yakitori", "Yakitori", 2500, "food", "Memory Lane. Atmospheric tiny smokey lanes. Sit down for skewers and beer.", "Omoide Yokocho Shinjuku"),
                ItineraryStep("20:30", "Kameya Ten-Tama Standing Soba", "Supper", 600, "food", "Famed 24hr stall. Get cheap hot buckwheat soup topped with runny egg and tempura.", "Kameya Shinjuku")
            ),
            food = PriorityObjective("Omoide Yokocho", "Smokey Yakitori", 2500, "Omoide Yokocho", "18:00"),
            snack = PriorityObjective("Naruto Taiyaki", "Sweet Potato Fish", 300, "Naruto Taiyaki Akihabara", "11:00"),
            alts = listOf(
                ContingencyPlan("Uchitateya Udon", 1400, "Ultra rustic musashino udon", "Uchitateya")
            ),
            markers = listOf(
                MapMarker(35.6983, 139.7731, "shopping", 1, "Akihabara Town", "Akihabara Station", "Anime & electronics"),
                MapMarker(35.7289, 139.7191, "food", 2, "Uchitateya", "Uchitateya", "super rustic boiling claypot"),
                MapMarker(35.6929, 139.6994, "food", 3, "Omoide Yokocho", "Omoide Yokocho", "Yakitori smoky alleyways")
            )
        ),

        // Day 9
        ItineraryDay(
            date = "Oct 17",
            day = "Day 9",
            title = "Historic Asakusa & Skytree",
            location = "Asakusa & Skytree",
            steps = 16000,
            morning = listOf(
                ItineraryStep("08:30", "Senso-ji Temple Stroll", "Temple", 0, "visit", "Tokyo's oldest and most iconic buddhist temple. Shop at Nakamise-dori street.", "Senso-ji"),
                ItineraryStep("10:00", "Kagetsudo Jumbo Melonpan", "Snack", 400, "sweets", "Freshly baked warm sweet bread that is light, crispy, and fluffy like a melon.", "Kagetsudo Asakusa")
            ),
            afternoon = listOf(
                ItineraryStep("12:00", "Suzukien Intense Matcha Lvl7", "Matcha", 800, "sweets", "Try the absolute strongest, deepest matcha gelato available in the world.", "Suzukien Asakusa"),
                ItineraryStep("13:30", "Tokyo Skytree Solamachi", "Mall", 0, "shopping", "Famed shopping mall underneath Skytree. Great gift shopping.", "Tokyo Solamachi")
            ),
            evening = listOf(
                ItineraryStep("17:30", "Toriton Conveyor Belt Sushi", "Dinner", 3500, "food", "Famed Hokkaido brand serving huge premium slices. Pull queue tickets at 6F.", "Toriton Solamachi"),
                ItineraryStep("19:30", "Okonomiyaki Sometaro grill", "Dinner 2", 2000, "food", "Charming traditional teppan house. Try pork okonomiyaki cooked on table plate.", "Okonomiyaki Sometaro")
            ),
            food = PriorityObjective("Toriton Sushi", "Conveyor Belt", 3500, "Toriton Solamachi", "17:30"),
            snack = PriorityObjective("Suzukien", "Intense Matcha", 800, "Suzukien", "12:00"),
            alts = listOf(
                ContingencyPlan("Okonomiyaki Sometaro", 2000, "Vintage tableside iron okonomiyaki", "Okonomiyaki Sometaro")
            ),
            markers = listOf(
                MapMarker(35.7148, 139.7967, "visit", 1, "Senso-ji Temple", "Senso-ji", "Oldest red gate temple"),
                MapMarker(35.7118, 139.7924, "food", 2, "Sometaro Okonomiyaki", "Okonomiyaki Sometaro", "Vintage tableside iron"),
                MapMarker(35.7101, 139.8107, "food", 3, "Toriton Skytree", "Toriton Solamachi", "Huge premium raw fish slices")
            )
        ),

        // Day 10
        ItineraryDay(
            date = "Oct 18",
            day = "Day 10",
            title = "Kawagoe Festival Day Trip",
            location = "Kawagoe & Ikebukuro",
            steps = 19000,
            morning = listOf(
                ItineraryStep("08:30", "Tobu Train to Kawagoe", "Transit", 600, "transit", "retro Edo-styled town on northwestern outskirts of Tokyo town.", "Ikebukuro Station"),
                ItineraryStep("10:00", "Ogakiku Unagi Traditional", "Lunch", 4500, "food", "Super iconic 200 year old restaurant grilling ultra sweet soft eel over charcoal.", "Ogakiku Kawagoe")
            ),
            afternoon = listOf(
                ItineraryStep("13:00", "Walk Clay Warehouse Streets", "Edo Town", 0, "visit", "Historic fireproof storehouses, candy alley, and old wooden bells.", "Kurazukuri Zone"),
                ItineraryStep("15:00", "Sweet Potato Takoyaki stick", "Snack", 500, "street", "Fresh local sweet potato pastries, cakes, and festival street bites.", "Candy Alley Kawagoe")
            ),
            evening = listOf(
                ItineraryStep("18:30", "Mutekiya Tonkotsu Ramen", "Dinner", 1500, "food", "Rich, thick fatty cream pork broth. Famed thick melt-in-mouth pork belly slices.", "Mutekiya Ikebukuro"),
                ItineraryStep("21:00", "Late Night Arcade center", "Fun", 1000, "other", "Enjoy classic crane games and rhythm games inside retro arcades.", "Ikebukuro")
            ),
            food = PriorityObjective("Mutekiya", "Rich Tonkotsu", 1500, "Mutekiya Ikebukuro", "18:30"),
            snack = PriorityObjective("Ogakiku Eel", "Sweet Glazed Eel", 4500, "Ogakiku Kawagoe", "10:00"),
            alts = listOf(
                ContingencyPlan("Kawagoe Street Eats", 1000, "Crispy fried sweet potato skewers", "Candy Alley Kawagoe")
            ),
            markers = listOf(
                MapMarker(35.9189, 139.4831, "visit", 1, "Hon-Kawagoe Station", "Hon-Kawagoe Station", "Start of Edo heritage walk"),
                MapMarker(35.9172, 139.4828, "food", 2, "Ogakiku Unagi", "Ogakiku Kawagoe", "200-year-old eel charcoal glazed"),
                MapMarker(35.7265, 139.7118, "food", 3, "Mutekiya", "Mutekiya Ikebukuro", "Ultra thick creamy pork belly ramen")
            )
        ),

        // Day 11
        ItineraryDay(
            date = "Oct 19",
            day = "Day 11",
            title = "Yanaka Ginza & Departure",
            location = "Sendagi & Narita",
            steps = 10000,
            morning = listOf(
                ItineraryStep("09:00", "Risaku Handmade Onigiri", "Breakfast", 1000, "food", "Try beautiful morning sets of handmade warm rice triangles with miso soup.", "Risaku Sendagi")
            ),
            afternoon = listOf(
                ItineraryStep("11:00", "Yanaka Ginza retro walk", "Explore", 0, "visit", "Charming retro neighborhood that survived WWII bombing. Famous cat statues.", "Yanaka Ginza"),
                ItineraryStep("13:30", "Narita Express to Airport", "Transit", 3200, "transit", "Swift comfortable express train returning to the skies.", "Nippori Station")
            ),
            evening = listOf(
                ItineraryStep("17:00", "Board Flight Departure", "Skyward", 0, "transit", "Sayonara! End of Japan tactical mission itinerary.", "Narita Airport")
            ),
            food = PriorityObjective("Risaku Onigiri", "Warm Rice Triangles", 1000, "Risaku Sendagi", "09:00"),
            snack = null,
            alts = emptyList(),
            markers = listOf(
                MapMarker(35.7258, 139.7611, "food", 1, "Risaku Onigiri", "Risaku Sendagi", "Sendagi cozy rice breakfast"),
                MapMarker(35.7268, 139.7668, "visit", 2, "Yanaka Ginza", "Yanaka Ginza", "Charming retro town lane"),
                MapMarker(35.7725, 140.3928, "station", 3, "Narita Terminal", "Narita Airport", "Departure gate")
            )
        )
    )
}
