package se.umu.sifl0010.googlemapsapplication.data

import se.umu.sifl0010.googlemapsapplication.model.Category

object CategoryDataProvider {
    fun getCategoryTree(): List<Category> {
        return listOf(
            Category("Food & Drink", listOf(
                Category("Restaurants", listOf(
                    Category("Asian", listOf(
                        Category("Chinese"),
                        Category("Japanese"),
                        Category("Thai"),
                        Category("Korean"),
                        Category("Vietnamese"),
                        Category("Indian"),
                        Category("Other Asian Cuisines")
                    )),
                    Category("European", listOf(
                        Category("Italian"),
                        Category("French"),
                        Category("Spanish / Tapas"),
                        Category("Greek"),
                        Category("German"),
                        Category("Other European Cuisines")
                    )),
                    Category("Middle Eastern", listOf(
                        Category("Turkish"),
                        Category("Lebanese"),
                        Category("Persian")
                    )),
                    Category("African", listOf(
                        Category("Moroccan"),
                        Category("Ethiopian"),
                        Category("Other African Cuisines")
                    )),
                    Category("North American", listOf(
                        Category("American (Traditional)"),
                        Category("Barbecue"),
                        Category("Cajun & Creole"),
                        Category("Canadian"),
                        Category("Mexican")
                    )),
                    Category("South American", listOf(
                        Category("Brazilian"),
                        Category("Peruvian"),
                        Category("Argentine")
                    )),
                    Category("Fast Food"),
                    Category("Pizza"),
                    Category("Seafood"),
                    Category("Vegetarian / Vegan"),
                    Category("Steakhouses"),
                    Category("Buffets"),
                    Category("Fine Dining / Gourmet")
                )),
                Category("Bars & Pubs", listOf(
                    Category("Sports Bar"),
                    Category("Karaoke Bar"),
                    Category("Wine Bar"),
                    Category("Craft Beer Bar"),
                    Category("Cocktail Lounge"),
                    Category("Pub / Tavern")
                )),
                Category("Cafés & Coffee Shops", listOf(
                    Category("Specialty Coffee"),
                    Category("Tea Rooms"),
                    Category("Coffee Roasters")
                )),
                Category("Bakeries & Desserts", listOf(
                    Category("Bakery"),
                    Category("Pâtisserie"),
                    Category("Donut Shop"),
                    Category("Ice Cream & Frozen Yogurt"),
                    Category("Chocolatier / Candy Shop")
                )),
                Category("Supermarkets & Grocery", listOf(
                    Category("General Supermarket"),
                    Category("Organic / Health Food Store"),
                    Category("Asian Market"),
                    Category("Latin Market"),
                    Category("Specialty Grocery")
                )),
                Category("Food Stands & Street Food", listOf(
                    Category("Food Trucks"),
                    Category("Street Vendors"),
                    Category("Farmers Markets")
                )),
                Category("Other Food Services", listOf(
                    Category("Delicatessens"),
                    Category("Caterers")
                ))
            )),
            // Continue with the remaining highest-level categories…
            Category("Nightlife & Entertainment", listOf(
                Category("Night Clubs", listOf(
                    Category("Dance Clubs"),
                    Category("Latin Clubs"),
                    Category("EDM Clubs")
                )),
                Category("Live Music Venues", listOf(
                    Category("Concert Halls"),
                    Category("Music Bars"),
                    Category("Jazz & Blues Clubs")
                )),
                Category("Karaoke"),
                Category("Comedy Clubs"),
                Category("Pubs & Bars"),
                Category("Hookah / Shisha Lounges")
            )),
            Category("Sports & Recreation", listOf(
                Category("Indoor Recreation", listOf(
                    Category("Bowling Alley"),
                    Category("Pool / Billiards Hall"),
                    Category("Darts Hall"),
                    Category("Table Tennis / Ping Pong Center"),
                    Category("Arcade / Gaming Center")
                )),
                Category("Outdoor Sports Fields & Courts", listOf(
                    Category("Soccer Field"),
                    Category("Basketball Court"),
                    Category("Tennis Court"),
                    Category("Baseball / Softball Field"),
                    Category("Volleyball Court"),
                    Category("Golf Course"),
                    Category("Mini Golf")
                )),
                Category("Recreation Centers", listOf(
                    Category("Sports Center / Gym"),
                    Category("Fitness Studios (Yoga, Pilates, Dance)")
                )),
                Category("Swimming", listOf(
                    Category("Indoor Pools"),
                    Category("Outdoor Pools"),
                    Category("Water Parks")
                )),
                Category("Adventure & Extreme", listOf(
                    Category("Climbing / Bouldering Gym"),
                    Category("Paintball / Laser Tag"),
                    Category("Skate Parks"),
                    Category("Go-Kart Tracks")
                )),
                Category("Other Sports", listOf(
                    Category("Boul / Boules / Bocce"),
                    Category("Driving Range"),
                    Category("Equestrian Centers"),
                    Category("Martial Arts Dojos")
                ))
            )),
            Category("Attractions & Leisure", listOf(
                Category("Parks & Gardens", listOf(
                    Category("Urban Parks"),
                    Category("Botanical Gardens"),
                    Category("Dog Parks")
                )),
                Category("Nature & Outdoors", listOf(
                    Category("Beaches"),
                    Category("Lakes / Reservoirs"),
                    Category("Hiking Trails"),
                    Category("Campgrounds"),
                    Category("National / State Parks")
                )),
                Category("Family Activities", listOf(
                    Category("Zoos"),
                    Category("Aquariums"),
                    Category("Theme Parks / Amusement Parks"),
                    Category("Petting Zoos"),
                    Category("Children’s Play Centers")
                )),
                Category("Sightseeing & Landmarks", listOf(
                    Category("Monuments"),
                    Category("Historical Landmarks"),
                    Category("Scenic Lookouts / Viewpoints")
                ))
            )),
            Category("Arts & Culture", listOf(
                Category("Museums", listOf(
                    Category("Art Museums"),
                    Category("History Museums"),
                    Category("Science Museums"),
                    Category("Children’s Museums"),
                    Category("Specialized / Themed Museums")
                )),
                Category("Galleries", listOf(
                    Category("Art Galleries"),
                    Category("Photography Galleries")
                )),
                Category("Performing Arts", listOf(
                    Category("Theaters"),
                    Category("Opera Houses"),
                    Category("Ballet & Dance Halls"),
                    Category("Concert Venues")
                )),
                Category("Cultural Centers", listOf(
                    Category("Community Centers"),
                    Category("Cultural Institutes")
                )),
                Category("Libraries & Archives"),
                Category("Historic Sites", listOf(
                    Category("Castles / Fortresses"),
                    Category("Archaeological Sites"),
                    Category("Heritage Buildings")
                ))
            )),
            Category("Shopping", listOf(
                Category("Shopping Malls & Complexes", listOf(
                    Category("Outlet Malls"),
                    Category("Indoor Shopping Centers"),
                    Category("Open‑Air Shopping Plazas")
                )),
                Category("Department Stores"),
                Category("Specialty & Boutique Shops", listOf(
                    Category("Clothing Boutiques"),
                    Category("Shoe Stores"),
                    Category("Jewelry Stores"),
                    Category("Cosmetics & Beauty"),
                    Category("Accessories & Bags")
                )),
                Category("Electronics & Technology", listOf(
                    Category("Computer / IT Shops"),
                    Category("Mobile Phone Stores"),
                    Category("Electronics Superstores")
                )),
                Category("Books & Media", listOf(
                    Category("Bookstores"),
                    Category("Comic / Manga Shops"),
                    Category("Music Stores")
                )),
                Category("Home & Garden", listOf(
                    Category("Furniture Stores"),
                    Category("Home Decor"),
                    Category("Garden Centers")
                )),
                Category("Thrift & Secondhand", listOf(
                    Category("Thrift Stores"),
                    Category("Vintage Shops")
                ))
            )),
            Category("Health & Wellness", listOf(
                Category("Gyms & Fitness", listOf(
                    Category("Weight Training Gyms"),
                    Category("CrossFit Boxes"),
                    Category("Yoga Studios"),
                    Category("Pilates Studios"),
                    Category("Spin / Cycling Studios")
                )),
                Category("Spas & Beauty", listOf(
                    Category("Day Spas"),
                    Category("Massage Therapy"),
                    Category("Nail Salons"),
                    Category("Hair Salons / Barbershops"),
                    Category("Aesthetics / Skin Care Clinics")
                )),
                Category("Medical Facilities", listOf(
                    Category("Hospitals"),
                    Category("Clinics / Urgent Care"),
                    Category("Pharmacies")
                )),
                Category("Holistic & Alternative", listOf(
                    Category("Acupuncture"),
                    Category("Chiropractors"),
                    Category("Homeopathy")
                ))
            )),
            Category("Lodging & Travel", listOf(
                Category("Hotels", listOf(
                    Category("Luxury Hotels"),
                    Category("Boutique Hotels"),
                    Category("Business Hotels")
                )),
                Category("Budget & Alternative Stays", listOf(
                    Category("Motels"),
                    Category("Hostels"),
                    Category("Guest Houses"),
                    Category("Bed & Breakfasts"),
                    Category("Vacation Rentals")
                )),
                Category("Transportation", listOf(
                    Category("Airports"),
                    Category("Train Stations"),
                    Category("Bus Stations"),
                    Category("Car Rentals"),
                    Category("Taxi Stands"),
                    Category("Rideshare Pickup Points")
                )),
                Category("Travel Agencies & Tour Operators")
            )),
            Category("Services & Utilities", listOf(
                Category("Financial Services", listOf(
                    Category("Banks"),
                    Category("ATMs"),
                    Category("Currency Exchange")
                )),
                Category("Automotive", listOf(
                    Category("Gas Stations"),
                    Category("Car Repair / Mechanics"),
                    Category("Car Washes")
                )),
                Category("Professional Services", listOf(
                    Category("Legal Services / Law Offices"),
                    Category("Accounting / Tax Services"),
                    Category("Printing / Copy Centers")
                )),
                Category("Postal & Shipping", listOf(
                    Category("Post Offices"),
                    Category("Courier Services")
                )),
                Category("Home Services", listOf(
                    Category("Plumbers"),
                    Category("Electricians"),
                    Category("HVAC Services")
                )),
                Category("Pet Services", listOf(
                    Category("Pet Grooming"),
                    Category("Veterinary Clinics"),
                    Category("Pet Boarding")
                )),
                Category("Tailors / Dry Cleaning")
            )),
            Category("Education & Community", listOf(
                Category("Schools & Universities", listOf(
                    Category("Elementary Schools"),
                    Category("High Schools"),
                    Category("Colleges / Universities"),
                    Category("Trade / Vocational Schools")
                )),
                Category("Language Schools"),
                Category("Community Centers"),
                Category("Places of Worship", listOf(
                    Category("Churches"),
                    Category("Temples"),
                    Category("Mosques"),
                    Category("Synagogues")
                ))
            ))
        )
    }
}
