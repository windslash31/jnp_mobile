package com.windslash.itriplanery.data

data class FoodItem(
    val id: String,
    val name: String,
    val dish: String,
    val area: String,
    val note: String,
    val mustTry: Boolean,
    val exclusive: Boolean = false
)

data class FoodCategory(
    val id: String,
    val name: String,
    val icon: String,
    val bgColor: String,
    val textColor: String,
    val tagline: String,
    val items: List<FoodItem>
)

object FoodData {
    val categories = listOf(
        FoodCategory(
            id = "ramen",
            name = "Ramen & Noodles",
            icon = "🍜",
            bgColor = "#FFFBEB", // amber-50
            textColor = "#D97706", // amber-600
            tagline = "Slurp Protocol",
            items = listOf(
                FoodItem("f_kamo", "Kamo to Negi", "Duck Ramen", "Ueno (Day 1)", "Tier S. Queue ~30 mins.", true),
                FoodItem("f_hanayama", "Hanayama Udon", "Himokawa Udon", "Ginza (Day 6)", "Get ticket at 10:00.", true),
                FoodItem("f_tatsunoya", "Tatsunoya", "Rich Tsukemen", "Shinjuku (Day 7)", "Post-Fuji meal.", false),
                FoodItem("f_uchitateya", "Uchitateya", "Musashino Udon", "Ikebukuro (Day 8)", "Thick & rustic.", true),
                FoodItem("f_chiikawa", "Chiikawa Ramen", "Theme Ramen", "Shibuya (Day 4)", "Reservation required.", false, true),
                FoodItem("f_mutekiya", "Mutekiya", "Tonkotsu Ramen", "Ikebukuro (Day 10)", "Finale Ramen.", false),
                FoodItem("f_kamachiku", "Kamachiku", "Garden Udon", "Nezu (Day 1)", "Lunch. Shoes off.", false),
                FoodItem("f_hoto", "Hoto Fudou", "Pumpkin Hoto", "Fuji (Day 7)", "Local specialty.", false),
                FoodItem("f_kameya", "Kameya", "Ten-Tama Soba", "Shinjuku (Day 8)", "Standing stall.", false)
            )
        ),
        FoodCategory(
            id = "sushi",
            name = "Sushi & Teppan",
            icon = "🍣",
            bgColor = "#EFF6FF", // blue-50
            textColor = "#2563EB", // blue-600
            tagline = "Ocean & Iron",
            items = listOf(
                FoodItem("f_iso", "Iso Sushi", "Omakase Set", "Toyosu (Day 3)", "Skip Sushi Dai line.", true),
                FoodItem("f_toriton", "Toriton", "Conveyor Belt", "Skytree (Day 9)", "Pull ticket 10:30.", true),
                FoodItem("f_isomaru", "Isomaru Suisan", "Shellfish BBQ", "Ueno (Day 5)", "Grill at table.", false),
                FoodItem("f_monja", "Monja Moheji", "Mentaiko Monja", "Tsukishima (Day 6)", "Staff cooks it.", true, true),
                FoodItem("f_sometaro", "Okonomiyaki Sometaro", "Okonomiyaki", "Asakusa (Day 9)", "Historic house.", true),
                FoodItem("f_wasai", "Wasai Yakura", "Shirasu Bowl", "Kamakura (Day 5)", "Whitebait specialty.", false),
                FoodItem("f_uokin", "Uokin", "Izakaya Sashimi", "Shimbashi (Day 3)", "Huge portions.", false)
            )
        ),
        FoodCategory(
            id = "meat",
            name = "Meat & Fried",
            icon = "🥩",
            bgColor = "#FFF1F2", // rose-50
            textColor = "#E11D48", // rose-600
            tagline = "Carnivore Mode",
            items = listOf(
                FoodItem("f_rikimaru", "Yakiniku Rikimaru", "Wagyu AYCE", "Ikebukuro (Day 4)", "Osaka style value.", true),
                FoodItem("f_gyukatsu", "Gyukatsu Aona", "Beef Cutlet", "Ueno (Day 2)", "Rare lean beef.", true),
                FoodItem("f_maruyama", "Tonkatsu Maruyama", "Pork Loin", "Nakano (Day 2)", "Salt > Sauce.", false),
                FoodItem("f_hibiki", "Hibiki", "Premium Izakaya", "Odaiba (Day 3)", "Rainbow Bridge view.", false),
                FoodItem("f_idaten", "Tempura Idaten", "Fresh Tempura", "Fuji (Day 7)", "Affordable.", false),
                FoodItem("f_kiwamiya", "Kiwamiya", "Hamburg Steak", "Shibuya (Day 4)", "Grill on stone.", true),
                FoodItem("f_sanrokuen", "Sanrokuen", "Robatayaki", "Fuji (Day 7)", "Fire pit grilling.", true)
            )
        ),
        FoodCategory(
            id = "rice",
            name = "Rice & Traditional",
            icon = "🍚",
            bgColor = "#F0FDF4", // green-50
            textColor = "#16A34A", // green-600
            tagline = "Bowls & History",
            items = listOf(
                FoodItem("f_ogakiku", "Ogakiku", "Unagi (Eel)", "Kawagoe (Day 10)", "Historic shop.", true),
                FoodItem("f_unana", "Unana", "Unagi Onigiri", "Kamakura (Day 5)", "Butter grilled.", true),
                FoodItem("f_risaku", "Risaku", "Handmade Onigiri", "Sendagi (Day 11)", "Last meal.", false),
                FoodItem("f_zuicho", "Zuicho", "Katsudon", "Shibuya (Day 4 Alt)", "Sweet soy egg.", false),
                FoodItem("f_caraway", "Caraway", "Beef Curry", "Kamakura (Day 5 Alt)", "Local legend.", false),
                FoodItem("f_rengatei", "Rengatei", "Omurice/Yoshoku", "Ginza (Day 6 Alt)", "The Birthplace.", false, true)
            )
        ),
        FoodCategory(
            id = "street",
            name = "Street & Snacks",
            icon = "🍢",
            bgColor = "#FFF7ED", // orange-50
            textColor = "#EA580C", // orange-600
            tagline = "Vibes & Sticks",
            items = listOf(
                FoodItem("f_omoide", "Omoide Yokocho", "Yakitori Alley", "Shinjuku (Day 8)", "Ucchan & Kameya.", true),
                FoodItem("f_giraffa", "Giraffa", "Curry Bread", "Kamakura (Day 5)", "Cheese pull.", true),
                FoodItem("f_naruto", "Naruto Taiyaki", "Fish Cake", "Akiba (Day 8)", "Hot & crispy.", true),
                FoodItem("f_menchi", "Ameyoko Street Food", "Menchi Katsu", "Ueno (Day 1)", "Meat cutlet.", false),
                FoodItem("f_marutake", "Marutake", "Tamagoyaki Stick", "Toyosu (Day 3)", "Sweet egg.", true),
                FoodItem("f_takoyaki", "Festival Stall", "Takoyaki", "Kawagoe (Day 10)", "Street vibe.", false)
            )
        ),
        FoodCategory(
            id = "sweets",
            name = "Sweets & Cafe",
            icon = "🍰",
            bgColor = "#FDF2F8", // pink-50
            textColor = "#DB2777", // pink-600
            tagline = "Sugar Rush",
            items = listOf(
                FoodItem("f_suzukien", "Suzukien", "Lvl 7 Matcha", "Asakusa (Day 9)", "Strongest matcha.", true),
                FoodItem("f_chico", "Daily Chi-co", "8-Layer Soft Serve", "Nakano (Day 2)", "Huge portion.", false),
                FoodItem("f_cremia", "CREMIA", "Premium Soft Serve", "Shibuya (Day 4)", "Langue de chat.", false),
                FoodItem("f_age3", "age.3", "Fried Sandwich", "Ginza (Day 6)", "Crispy cream.", true),
                FoodItem("f_flipper", "Flipper's", "Souffle Pancake", "Shibuya (Day 4)", "Fluffy.", false),
                FoodItem("f_melon", "Kagetsudo", "Jumbo Melonpan", "Asakusa (Day 9)", "Fresh baked.", true),
                FoodItem("f_oishi", "Blueberry Soft Serve", "Blueberry Soft", "Fuji (Day 7)", "Fuji View.", false)
            )
        )
    )
}
