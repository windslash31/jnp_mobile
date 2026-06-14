package com.windslash.itriplanery.data

data class BookingTarget(
    val title: String,
    val date: String,
    val bookDate: String,
    val link: String,
    val note: String,
    val icon: String, // fontawesome equivalent or material equivalent
    val color: String, // tailwind representation for styled containers
    val strategy: String, // HTML content strategy details
    val isCritical: Boolean = false,
    val isRecommended: Boolean = false,
    val isPhoneOnly: Boolean = false
)

data class LanguagePhrase(
    val english: String,
    val formal: String,
    val native: String,
    val note: String? = null
)

data class LanguageGroup(
    val title: String,
    val iconName: String,
    val colorHex: String,
    val phrases: List<LanguagePhrase>
)

data class TipHackSection(
    val category: String,
    val iconName: String,
    val colorHex: String,
    val items: List<String>
)

object TipsData {
    val criticalBookings = listOf(
        BookingTarget(
            title = "Ghibli Museum",
            date = "For Oct 9 (Fri)",
            bookDate = "Sept 10 @ 10:00 JST",
            link = "https://l-tike.com/st1/ghibli-en/sitetop",
            note = "Lawson Ticket. Queue online 30 mins early. Sells out instantly.",
            icon = "gavel",
            color = "#EF4444",
            strategy = "<b>Difficulty: Extreme.</b><br/><br/>1. Log in at 09:30 AM JST on Sept 10th.<br/>2. Open multiple browser tabs; do NOT refresh during high load queue allocation.<br/>3. Ready your passport details exactly. The name on the ticket MUST match your passport perfectly.<br/>4. Payment requires a 3D-secure enabled card.",
            isCritical = true
        ),
        BookingTarget(
            title = "Cafe Capyba",
            date = "For Oct 17 (Sat)",
            bookDate = "Oct 3 @ 12:00 JST",
            link = "https://capyba.jp/",
            note = "Strict 2-week window. Slots vanish in seconds.",
            icon = "pets",
            color = "#F59E0B",
            strategy = "<b>Difficulty: Very High.</b><br/><br/>1. Go to the Hikifune location booking page.<br/>2. Select '2 People' and aim for a afternoon slot (14:30 or 15:00).<br/>3. Have credit card pre-saved in browser autofill.",
            isCritical = true
        ),
        BookingTarget(
            title = "Fuji Excursion Train",
            date = "For Oct 16 (Fri)",
            bookDate = "Sept 16 @ 10:00 JST",
            link = "https://www.eki-net.com/en/jreast-train-reservation/Top/Index",
            note = "Route: Shinjuku → Kawaguchiko. Essential for morning views.",
            icon = "train",
            color = "#4F46E5",
            strategy = "<b>Difficulty: Medium.</b><br/><br/>1. Create a JR-East reservation account prior.<br/>2. Purchase a 'Fare Ticket + Seat Reservation' bundle combined.<br/>3. Print physically at Shinjuku Green Ticket Machines using the QR code sent.",
            isCritical = true
        ),
        BookingTarget(
            title = "Shibuya Sky Sunset",
            date = "For Oct 11 (Sun)",
            bookDate = "~Sept 27",
            link = "https://www.shibuya-scramble-square.com/sky/ticket/en/",
            note = "Select 'Sunset' slot (~16:20). Golden hour slots sell out fast.",
            icon = "wb_sunny",
            color = "#2563EB",
            strategy = "<b>Difficulty: High.</b><br/><br/>1. Sunset occurs around 17:15 in autumn.<br/>2. Secure the 16:20-16:40 slot to capture blue hour shift.<br/>3. Download cellular offline dynamic QR codes before ascending.",
            isCritical = true
        )
    )

    val recommendedBookings = listOf(
        BookingTarget(
            title = "Nikuya no Daidokoro",
            date = "Oct 11",
            bookDate = "Variable",
            link = "https://www.tablecheck.com/en/shops/nikuyanodaidokoro-shibuya/reserve",
            note = "Mention 'Celebrating birthday/anniversary - Meat Cake requested.' in notes.",
            icon = "cake",
            color = "#EC4899",
            strategy = "",
            isRecommended = true
        ),
        BookingTarget(
            title = "Flipper's Pancakes",
            date = "Oct 11",
            bookDate = "3 weeks out",
            link = "https://www.tablecheck.com/en/shops/flippers-shibuya/reserve",
            note = "Secures table skips the 1-hour public waiting line.",
            icon = "cookie",
            color = "#10B981",
            strategy = "",
            isRecommended = true
        ),
        BookingTarget(
            title = "Okonomiyaki Sometaro",
            date = "Oct 17",
            bookDate = "1 month out",
            link = "https://maps.app.goo.gl/gMDWq8YqF2WvJ9Q78",
            note = "Strictly Phone Booking. Use Concierge Script to ask hotel reception.",
            icon = "restaurant",
            color = "#6366F1",
            strategy = "",
            isRecommended = true,
            isPhoneOnly = true
        )
    )

    val conciergeScript = """
Dear Concierge Team,

I am a guest checking in on October 8th. Could you please assist me in making the following restaurant reservations?

1. Asakusa Okonomiyaki Sometaro (Day 9)
- Date: Saturday, October 17, 2026
- Time: 18:00 (6:00 PM)
- People: 2 Adults
- Note: We do not speak Japanese.

2. Kamachiku Udon (Day 1)
- Date: Friday, October 9, 2026
- Time: 11:30 (Lunch)
- People: 2 Adults
- Note: Please confirm if they accept lunch reservations.

Thank you very much.
    """.trimIndent()

    val languageGroups = listOf(
        LanguageGroup(
            title = "Essentials & Politeness",
            iconName = "chat",
            colorHex = "#4F46E5",
            phrases = listOf(
                LanguagePhrase("Thank you", "Arigatou Gozaimasu", "Domo / Arigatou"),
                LanguagePhrase("Excuse me / Sorry", "Sumimasen", "Suimasen (Fast)"),
                LanguagePhrase("Yes", "Hai", "Hai / Un"),
                LanguagePhrase("No / It's fine", "Iie", "Daijoubu (Polite refusal)"),
                LanguagePhrase("Do you understand?", "Wakarimasen", "Wakaranai")
            )
        ),
        LanguageGroup(
            title = "Dining Combat",
            iconName = "restaurant_menu",
            colorHex = "#EA580C",
            phrases = listOf(
                LanguagePhrase("Order please!", "Sumimasen!", "Sumimasen!"),
                LanguagePhrase("Rice size: L / M / S", "Oomori / Namimori / Sukuname", "Oomori / Futsuu / Sukuname"),
                LanguagePhrase("Refill (Second round)", "Okawari kudasai", "Okawari!"),
                LanguagePhrase("Water please", "Omizu wo kudasai", "Omizu kudasai"),
                LanguagePhrase("Check please", "Okaikei onegaishimasu", "Okaikei / Check"),
                LanguagePhrase("Delicious", "Oishii desu", "Umai / Oishii"),
                LanguagePhrase("Thanks for the meal", "Gochisousama deshita", "Gochisousama")
            )
        ),
        LanguageGroup(
            title = "Dietary Phrases (Critical)",
            iconName = "warning",
            colorHex = "#D97706",
            phrases = listOf(
                LanguagePhrase(
                    english = "No bean sprouts, no onion, no green onion",
                    formal = "Moyashi to, Negi to, Tamanegi wa, ZENBU NUKI de onegaishimasu.",
                    native = "Moyashi, Negi, Tamanegi wa, ZENBU NUKI de",
                    note = "Removes all standard garnish bulb accents."
                ),
                LanguagePhrase(
                    english = "Garnishes ON THE SIDE please",
                    formal = "Yakumi wa betsu-zara de onegaishimasu",
                    native = "Yakumi wa BETSU-ZARA de",
                    note = "The Master Key. 'Yakumi' covers onions, ginger, wasabi. Keeps soup clean."
                ),
                LanguagePhrase(
                    english = "NO bean sprouts (Moyashi)",
                    formal = "Moyashi wa nuki de onegaishimasu",
                    native = "Moyashi NUKI de",
                    note = "Essential for Ramen shops where sprouts are piled high."
                ),
                LanguagePhrase(
                    english = "I love garlic (extra please)",
                    formal = "Ninniku wa daisuki desu. Oome ni dekimasu ka?",
                    native = "Ninniku MASHI / OOME",
                    note = "Reminds chef that you STILL enjoy garlic mince."
                )
            )
        ),
        LanguageGroup(
            title = "Shopping & Tax Free",
            iconName = "local_mall",
            colorHex = "#DB2777",
            phrases = listOf(
                LanguagePhrase("Is this Tax Free?", "Menzei dekimasu ka?", "Tax Free OK?"),
                LanguagePhrase("I have my passport", "Pasupoto wo motte imasu", "Pasupoto aru"),
                LanguagePhrase("New / Unopened box", "Atarashii no wo kudasai", "Atarashii no", "Buying pristine figures"),
                LanguagePhrase("Can I pay with card?", "Kaado wa tsukaemasu ka?", "Kaado OK?")
            )
        )
    )

    val tipSections = listOf(
        TipHackSection(
            category = "Money Hacks",
            iconName = "attach_money",
            colorHex = "#D97706",
            items = listOf(
                "Buying drinks? Drug Stores (such as Matsumoto Kiyoshi) are 30-40% cheaper than Konbini.",
                "Need a high-quality Bento? Supermarkets (Life, Summit) slash ready-to-eat sets' prices by 20-50% after 19:30 during 'Depachika' clearance hours.",
                "Avoid tourist umbrellas. Most hotels loan sturdy plastic ones at the front desk for free.",
                "Daiso and Seria (100 Yen shops) are absolute lifesavers for forgotten chargers, cables, or basic souvenirs.",
                "Avoid credit card dynamic conversion DCC. When paying, always choose JPY (Yen) over your home currency to avoid 5-10% hidden fees."
            )
        ),
        TipHackSection(
            category = "Dining & Protocol",
            iconName = "restaurant",
            colorHex = "#EA580C",
            items = listOf(
                "Never tip. It causes mass panic and servers will chase you down the street to return your coin.",
                "No eating while walking (Tabearuki). Stand near the shop front or vending machine until finished, then dispose of waste there.",
                "Wet towels (Oshibori) are strictly for your hands, not your face. Dragging it across your brow is seen as unpolished.",
                "Slurping noodles is not rude; it cools the broth, aerates the noodles, and indicates enjoyment to the chef."
            )
        ),
        TipHackSection(
            category = "Transport & Nav",
            iconName = "directions_subway",
            colorHex = "#2563EB",
            items = listOf(
                "Escalator Etiquette: Stand LEFT in Tokyo regions. Stand RIGHT in Kansai (Osaka).",
                "Trains are silent zones. Conversations must be whispered, and phone calls are strictly banned on trains.",
                "Google Maps 'Exit Identifier' is savior. Exit A4 vs exit C1 can save you a 15-minute walk around massive stations."
            )
        )
    )
}
