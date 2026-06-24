package com.example.network

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

// --- Gemini API Request Structures (Moshi-compatible) ---

data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

data class InlineData(
    val mimeType: String,
    val data: String
)

data class Content(
    val parts: List<Part>
)

data class ResponseFormatText(
    val mimeType: String
)

data class ResponseFormat(
    val text: ResponseFormatText? = null
)

data class GenerationConfig(
    val responseFormat: ResponseFormat? = null,
    val temperature: Float? = null,
    val topP: Float? = null
)

data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

data class PartResponse(
    val text: String? = null
)

data class ContentResponse(
    val parts: List<PartResponse>
)

data class Candidate(
    val content: ContentResponse
)

data class GenerateContentResponse(
    val candidates: List<Candidate>
)

// --- Retrofit Service ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

// --- Gemini Hair & Scalp Analysis Engine ---

object SkinAnalysisEngine {
    private const val TAG = "SkinAnalysisEngine"
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val reportAdapter = moshi.adapter(SkinReport::class.java)

    // Helper to convert Bitmap to Base64
    fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Call live Gemini API to analyze the uploaded hair/scalp image.
     */
    suspend fun analyzeSkin(bitmap: Bitmap?, faceTypeLabel: String): SkinReport = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "API Key is empty or placeholder. Falling back to high-quality simulated hair analysis.")
            return@withContext getSimulatedReport(faceTypeLabel)
        }

        val prompt = """
            You are a senior Trichologist (Hair & Scalp Doctor), Beauty-Tech Expert, and Skincare/Haircare Chemist.
            Analyze this scalp or hair image (it represents a user scan with $faceTypeLabel condition).
            
            Perform an exhaustive scalp and hair health diagnostic audit and generate a complete report matching the JSON schema below.
            Do not include any explanation or formatting outside of the valid JSON block.
            
            Return the response in this exact JSON structure:
            {
              "skinType": "Oily Scalp / Curly 3B" | "Dry Scalp / Wavy 2A" | "Dandruff Flakes / Straight 1A" | "Thinning Crown / Coily 4C" | "Healthy Normal / Balanced",
              "skinTypeScore": 0 to 100, // Root Strength Index (higher is stronger hair roots)
              "skinTypeDescription": "Detailed analysis of hair texture, curl pattern, scalp sebum levels, follicular viability, and overall crown health...",
              "skinScore": 0 to 100, // Mane Health Rating (higher is healthier, glossy hair)
              "concerns": [
                {
                  "name": "Dandruff" | "Frizz" | "Dry Scalp" | "Oily Scalp" | "Split Ends" | "Hair Thinning" | "Heat Damage",
                  "severity": "Low" | "Medium" | "High",
                  "description": "Short diagnostic explanation of the issue observed on their scalp or hair strands...",
                  "percentage": 0 to 100 // severity percentage
                }
              ],
              "routines": {
                "morning": [
                  { "stepNumber": 1, "title": "Scalp Tonic", "instruction": "Apply 3 drops of copper peptides to damp roots. Massage in circles.", "productType": "Anti-thinning tonic", "frequency": "Daily morning" }
                ],
                "evening": [
                  { "stepNumber": 1, "title": "Overnight Oil", "instruction": "Apply cold-pressed rosemary oil to thinning edges.", "productType": "Rosemary Scalp Serum", "frequency": "3x per week night" }
                ],
                "weekly": [
                  { "stepNumber": 1, "title": "Exfoliating Scrub", "instruction": "Part hair and massage salicylic acid scalp scrub to wash away flakes.", "productType": "Salicylic acid scrub", "frequency": "1x per week" }
                ]
              },
              "recommendations": {
                "budget": [
                  { "name": "Tea Tree Oil Shampoo", "brand": "Simple Care", "keyIngredients": "Tea Tree, Salicylic Acid", "price": "${'$'}8.99", "suitabilityScore": 95, "buyLink": "https://www.amazon.com", "marketplace": "Amazon" }
                ],
                "standard": [
                  { "name": "Scalp Revival Charcoal Scrub", "brand": "Briogeo", "keyIngredients": "Binchotan Charcoal, Tea Tree, Coconut Oil", "price": "${'$'}42.00", "suitabilityScore": 91, "buyLink": "https://www.sephora.com", "marketplace": "Sephora" }
                ],
                "premium": [
                  { "name": "Multi-Peptide Hair Density Serum", "brand": "The Ordinary", "keyIngredients": "Redensyl, Procapil, Caffeine", "price": "${'$'}22.50", "suitabilityScore": 93, "buyLink": "https://www.sephora.com", "marketplace": "Sephora" }
                ]
              },
              "ingredientsSafety": {
                "beneficialIngredients": [
                  { "name": "Rosemary Oil", "role": "Follicle Stimulant", "suitability": "Highly Recommended", "explanation": "Clinically proven to mimic 2% minoxidil in boosting scalp blood flow and microcirculation." }
                ],
                "harmfulIngredients": [
                  { "name": "Sodium Lauryl Sulfate (SLS)", "role": "Aggressive Surfactant", "suitability": "Avoid", "explanation": "Strips protective natural lipids from the scalp, triggering reactive sebum overflow." }
                ]
              },
              "homeRemedies": [
                {
                  "name": "Soothe-Flakes Tea Tree Scalp Mask",
                  "ingredients": ["3 tbsp fresh aloe vera gel", "3 drops pure tea tree essential oil"],
                  "instructions": "Whisk aloe gel with tea tree oil. Part your hair into sections and paint directly onto your scalp. Leave for 20 minutes, then rinse with cold water.",
                  "expectedBenefits": "Cools vascular redness, hydrates itchy dry skin, and contains natural anti-fungal properties to combat dandruff fungi.",
                  "precautions": "Do not exceed 3 drops of tea tree oil to prevent sensory tingling or contact allergy."
                }
              ],
              "budgetAlternatives": [
                {
                  "premiumProduct": "Kérastase Specifique Potentialiste Serum (${'$'}58)",
                  "budgetProduct": "L'Oreal Elvive Hyaluron Plump Scalp Serum (${'$'}9)",
                  "saving": "${'$'}49.00",
                  "reason": "Both formulations are enriched with active hyaluronic networks to plumps parched hair cuticles without silicone buildup."
                }
              ]
            }
            
            Ensure you fill out multiple items in concerns, routines, homeRemedies, and budgetAlternatives.
            The values should represent actual trichological findings and highly relatable hair diagnostics. Keep the style quirky, engaging, and clear!
        """.trimIndent()

        try {
            val parts = mutableListOf<Part>()
            parts.add(Part(text = prompt))
            
            if (bitmap != null) {
                val base64Image = bitmap.toBase64()
                parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image)))
            } else {
                parts.add(Part(text = "Please analyze based on the profile: $faceTypeLabel"))
            }

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = parts)),
                generationConfig = GenerationConfig(
                    responseFormat = ResponseFormat(text = ResponseFormatText(mimeType = "application/json")),
                    temperature = 0.2f
                ),
                systemInstruction = Content(parts = listOf(Part(text = "You are a professional Trichologist and Hair Health AI assistant. Always output valid JSON.")))
            )

            val rawResponse = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = rawResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("Empty response from Gemini")

            Log.d(TAG, "Gemini response: $jsonText")
            return@withContext reportAdapter.fromJson(jsonText)
                ?: throw Exception("Failed to parse hair report JSON")

        } catch (e: Exception) {
            Log.e(TAG, "Error calling live Gemini, running simulated fallback.", e)
            return@withContext getSimulatedReport(faceTypeLabel)
        }
    }

    /**
     * A highly polished on-device hair & scalp simulation report matching different hair profiles perfectly.
     */
    fun getSimulatedReport(faceTypeLabel: String): SkinReport {
        return when (faceTypeLabel) {
            "Oily Scalp & Dandruff Active" -> SkinReport(
                skinType = "Oily Scalp & Straight 1A",
                skinTypeScore = 78,
                skinTypeDescription = "Hyperactive sebaceous glands concentrated around the crown area. Excessive sebum build-up acts as a food source for Malassezia globosa (dandruff yeast), causing greasy flakes, vascular redness, and root itchiness.",
                skinScore = 65,
                concerns = listOf(
                    SkinConcern("Dandruff", "High", "Heavy white flakes and scaling concentrated around the crown. Caused by Malassezia yeast feeding on excess sebum.", 85),
                    SkinConcern("Oily Scalp", "High", "Roots become greasy within 12 hours of washing due to high sebum production from overactive follicles.", 80),
                    SkinConcern("Dry Scalp", "Low", "Slight surface tightness after using harsh clarifying shampoos, showing minor scalp irritation.", 30)
                ),
                routines = SkincareRoutine(
                    morning = listOf(
                        SkincareRoutineStep(1, "Clarifying Scalp Wash", "Massage specifically into your wet roots for 60 seconds. Let the active ingredients work.", "Salicylic Acid & Zinc Pyrithione Shampoo", "3x per week morning"),
                        SkincareRoutineStep(2, "Mid-Length Conditioner", "Apply ONLY from the ears down to prevent flattening your roots. Rinse with cool water.", "Lightweight Aloe Vera Hydrating Conditioner", "Every wash morning"),
                        SkincareRoutineStep(3, "Anti-Seeding Scalp Serum", "Mist onto clean damp hair roots to balance microflora and regulate oil production.", "Niacinamide 5% & Zinc PCA Scalp Mist", "Daily morning")
                    ),
                    evening = listOf(
                        SkincareRoutineStep(1, "Soothe-Itch Massager", "Use a soft silicone scalp brush in gentle circular patterns to lift cellular debris and boost blood flow.", "Silicone Scalp Massager Brush", "Every Evening"),
                        SkincareRoutineStep(2, "Peppermint Infusion", "Apply 4 drops to itchy sections to immediately cool vascular redness and neutralize scent.", "Water-Based Peppermint & Rosemary Water Spray", "Every Evening")
                    ),
                    weekly = listOf(
                        SkincareRoutineStep(1, "Salicylic Acid Scalp Detox", "Apply directly to dry scalp, massage, leave for 10 minutes, then wash to dissolve stubborn oil plugs.", "Salicylic Acid 2% Scalp Scrub Pre-Wash", "1x per week (Sunday)"),
                        SkincareRoutineStep(2, "Clarifying ACV Rinse", "Mix 1 tbsp raw apple cider vinegar with 1 cup water. Pour over scalp, leave for 2 minutes, rinse.", "DIY Apple Cider Vinegar Scalp Rinse", "1x per week (Wednesday)")
                    )
                ),
                recommendations = ProductRecommendations(
                    budget = listOf(
                        Product("Anti-Dandruff Active Shampoo", "Selsun Blue", "Selenium Sulfide 1%", "₹350", 94, "https://www.amazon.in", "Amazon"),
                        Product("Peppermint Hair & Scalp Spray", "Mamaearth", "Peppermint Oil, Tea Tree, Witch Hazel", "₹299", 91, "https://www.nykaa.com", "Nykaa")
                    ),
                    standard = listOf(
                        Product("Salicylic Acid 2% Exfoliating Scrub", "Minimalist", "Salicylic Acid, Panthenol, Betaine", "₹599", 93, "https://www.nykaa.com", "Nykaa"),
                        Product("Scalp Sync Anti-Dandruff", "Biolage", "Pyrithione Zinc, Mint Leaf", "₹1,200", 88, "https://www.amazon.in", "Amazon")
                    ),
                    premium = listOf(
                        Product("Specifique Potentialiste Scalp Serum", "Kérastase", "Bifidus Prebiotics, Vitamin C", "₹5,200", 91, "https://www.sephora.com", "Sephora"),
                        Product("Flaky/Itchy Scalp Toner", "Philip Kingsley", "Piroctone Olamine, Camphor", "₹2,800", 92, "https://www.amazon.in", "Brand Website")
                    )
                ),
                ingredientsSafety = IngredientsSafety(
                    beneficialIngredients = listOf(
                        IngredientInsight("Zinc Pyrithione / Piroctone Olamine", "Anti-fungal", "Highly Recommended", "Directly targets the Malassezia dandruff yeast to stop scaling, flaking, and severe crown itching."),
                        IngredientInsight("Salicylic Acid", "Beta Hydroxy Acid (BHA)", "Highly Recommended", "Oil-soluble exfoliator that breaks down stubborn oil plugs, flakes, and scalp buildup.")
                    ),
                    harmfulIngredients = listOf(
                        IngredientInsight("Dimethicone / heavy silicones", "Emollient Build-up", "Avoid", "Forms a water-resistant layer on the scalp that traps excess sebum and feeds yeast, exacerbating dandruff."),
                        IngredientInsight("Heavy Oils (e.g. Olive, Mustard)", "Yeast Superfood", "Avoid", "Yeast thrives on the lipid carbon chains in vegetable oils, turning oil massage into an itch trigger.")
                    )
                ),
                homeRemedies = listOf(
                    HomeRemedy(
                        name = "DIY Tea Tree & Aloe Scalp Gel",
                        ingredients = listOf("3 tbsp organic cold-pressed aloe vera gel", "3 drops pure tea tree essential oil"),
                        instructions = "Mix the ingredients together until smooth. Part your hair and paint directly onto flaky zones. Leave on for 20 minutes before a standard shampoo wash.",
                        expectedBenefits = "Tea tree is a potent anti-fungal agent, while aloe vera reduces vascular redness and cools scalp heat instantly.",
                        precautions = "Keep the tea tree oil below 3 drops to avoid a tingling skin sensation."
                    ),
                    HomeRemedy(
                        name = "Anti-Flake Green Tea & Lemon Spray",
                        ingredients = listOf("1 brewed green tea bag (cooled)", "1 tsp fresh organic lemon juice"),
                        instructions = "Whisk the lemon juice into the cooled green tea. Spray onto your scalp 15 minutes before your wash cycle, massage lightly, and rinse out.",
                        expectedBenefits = "Green tea reduces excess oil synthesis, while the acidity of lemon helps balance scalp pH.",
                        precautions = "Do not apply if your scalp has active scratch marks or broken skin."
                    )
                ),
                budgetAlternatives = listOf(
                    BudgetAlternative(
                        premiumProduct = "Kérastase Anti-Pelliculaire Shampoo (₹2,600)",
                        budgetProduct = "L'Oreal Clay Rebalancing Shampoo (₹399)",
                        saving = "₹2,201",
                        reason = "Both use clarifying clays and zinc actives to safely absorb heavy grease without stripping fragile strands."
                    )
                )
            )

            "Dry Scalp & Hair Thinning Risk" -> SkinReport(
                skinType = "Dry Scalp / Coily 4C",
                skinTypeScore = 62,
                skinTypeDescription = "Compromised scalp barrier lacking moisture. Fine hair follicles show signs of vascular slowdown around the crown and hairline, indicating early-stage temporary hair shedding (Telogen Effluvium).",
                skinScore = 58,
                concerns = listOf(
                    SkinConcern("Hair Thinning", "High", "Early signs of hair thinning around the temples and crown. Follicles are undernourished and shedding strands prematurely.", 82),
                    SkinConcern("Dry Scalp", "High", "Scalp lacks protective oil barrier, leading to tight, dry, itchy skin and dry dust-like flakes.", 78),
                    SkinConcern("Frizz", "Medium", "Coily 4C cuticles are parched and open, easily absorbing environmental humidity and expanding into frizz.", 60)
                ),
                routines = SkincareRoutine(
                    morning = listOf(
                        SkincareRoutineStep(1, "Nourishing Co-Wash", "Wash with a cleansing cream instead of suds. Cleanses without stripping delicate oils.", "Sulfate-Free Peptide Cleansing Conditioner", "2x per week morning"),
                        SkincareRoutineStep(2, "Density Booster", "Apply 4 drops to dry temples and massage. Lightweight, leaves zero grease.", "Redensyl & Procapil Hair Density Serum", "Daily morning"),
                        SkincareRoutineStep(3, "Leave-In Butter", "Seal curly cuticles with moisture.", "Shea Butter & Argan Leave-in Cream", "Daily morning")
                    ),
                    evening = listOf(
                        SkincareRoutineStep(1, "Rosemary Oil Protocol", "Apply 5 drops of cold-pressed rosemary oil mixed with pumpkin seed oil to active thinning patches.", "Rosemary & Pumpkin Seed Scalp Serum", "Every Evening"),
                        SkincareRoutineStep(2, "Follicle Stimulation Massage", "Invert your head slightly and massage with fingers for 4 minutes to trigger microcirculation.", "Inversion Scalp Massage Method", "Every Evening"),
                        SkincareRoutineStep(3, "Satin Wrap Protection", "Wrap hair in a silk bonnet or use a satin pillowcase to stop friction and split ends.", "Satin Hair Wrap Sleeve", "Every Evening")
                    ),
                    weekly = listOf(
                        SkincareRoutineStep(1, "Hot Oil Hair Cocoon", "Warm 2 tbsp jojoba oil, apply to scalp and length, wrap in a warm towel for 30 minutes, then wash.", "Jojoba & Rosemary Hot Oil Mask", "1x per week (Friday)"),
                        SkincareRoutineStep(2, "Scalp Hydration Mask", "Apply a deep conditioning mask to soothe scalp dryness.", "Aloe Vera & Ceramide Hair Mask", "1x per week (Sunday)")
                    )
                ),
                recommendations = ProductRecommendations(
                    budget = listOf(
                        Product("Rosemary & Biotin Anti-Hairfall", "Pilgrim", "Rosemary Oil, Biotin, Redensyl", "₹450", 93, "https://www.nykaa.com", "Nykaa"),
                        Product("Sulfate-Free Coconut Cleanser", "Wow Skin Science", "Coconut Milk, Argan Oil", "₹349", 90, "https://www.amazon.in", "Amazon")
                    ),
                    standard = listOf(
                        Product("Multi-Peptide Hair Density Serum", "The Ordinary", "Redensyl, Procapil, Capixyl, Caffeine", "₹2,150", 94, "https://www.nykaa.com", "Nykaa"),
                        Product("Scalp Relief Treatment Gel", "CeraVe", "Ceramides, Niacinamide", "₹1,250", 89, "https://www.amazon.in", "Amazon")
                    ),
                    premium = listOf(
                        Product("Serene Scalp Thickening Treatment", "Oribe", "Capixyl, Pea Extract, Rosemary", "₹6,500", 92, "https://www.sephora.com", "Sephora"),
                        Product("Superpower Hair & Scalp Serum", "Better Not Younger", "Centella, Ginger, Niacinamide", "₹4,200", 88, "https://www.sephora.com", "Sephora")
                    )
                ),
                ingredientsSafety = IngredientsSafety(
                    beneficialIngredients = listOf(
                        IngredientInsight("Redensyl & Procapil", "Follicle Activator", "Highly Recommended", "Synergistic plant extracts that target hair follicle stem cells to boost density and reduce shedding."),
                        IngredientInsight("Rosemary Oil", "Natural Vasodilator", "Highly Recommended", "Increases cellular metabolism in follicles to sustain a longer growth (Anagen) phase.")
                    ),
                    harmfulIngredients = listOf(
                        IngredientInsight("Sulfate Surfactants (SLS/SLES)", "Aggressive Cleanser", "Avoid", "Severely strips lipids and water, leaving dry scalps prone to itch, micro-inflammation, and thinning."),
                        IngredientInsight("Denatured Alcohol", "Drying Solvent", "Avoid", "Commonly used in hair sprays to dry instantly, but severely dehydrates curly strands and damages follicles.")
                    )
                ),
                homeRemedies = listOf(
                    HomeRemedy(
                        name = "Snoop Density Peppermint & Rosemary Water",
                        ingredients = listOf("2 sprigs fresh organic rosemary", "5 fresh mint leaves", "1.5 cups filtered water"),
                        instructions = "Boil the rosemary and mint in the water for 15 minutes until the liquid turns amber. Cool, strain into a spray mist bottle, and store in the fridge. Mist onto your scalp daily.",
                        expectedBenefits = "Peppermint and rosemary stimulate scalp circulation, nourishing weak hair roots directly.",
                        precautions = "Discard and brew a fresh batch every 7 days."
                    ),
                    HomeRemedy(
                        name = "DIY Hydrating Pumpkin Seed Oil Compress",
                        ingredients = listOf("1 tbsp organic pumpkin seed oil", "2 drops lavender essential oil"),
                        instructions = "Warm the oils slightly. Massage into your scalp, focusing on thinning edges. Wrap in a warm damp towel for 30 minutes, then double-shampoo.",
                        expectedBenefits = "Pumpkin seed oil is a natural DHT-blocker block that feeds the scalp healthy fatty acids.",
                        precautions = "Ensure the oil is comfortably warm, not hot, to protect delicate follicles."
                    )
                ),
                budgetAlternatives = listOf(
                    BudgetAlternative(
                        premiumProduct = "Aveda Invati Advanced Scalp Revitalizer (₹5,800)",
                        budgetProduct = "The Ordinary Multi-Peptide Hair Density Serum (₹2,150)",
                        saving = "₹3,650",
                        reason = "Both utilize clinically studied botanical peptide compounds (Procapil & Redensyl) to activate hair stems."
                    )
                )
            )

            "Curly Hair & Severe Frizz/Damage" -> SkinReport(
                skinType = "Curly Hair / Curly 3B",
                skinTypeScore = 82,
                skinTypeDescription = "Highly porous hair shaft with raised, damaged cuticles. Lack of moisture distribution from roots to tips due to the curly spiral shape, causing severe frizz, split ends, and mechanical breakage.",
                skinScore = 60,
                concerns = listOf(
                    SkinConcern("Frizz", "High", "Raised hair cuticles easily absorb humidity, making curly strands dry, puffy, and tangled.", 88),
                    SkinConcern("Heat Damage", "High", "High porosity and weak keratin bonds due to frequent heat styling. Noticeable dryness and elasticity loss.", 80),
                    SkinConcern("Split Ends", "Medium", "Frayed ends and physical breakage along the hair shaft from rough towel drying.", 68)
                ),
                routines = SkincareRoutine(
                    morning = listOf(
                        SkincareRoutineStep(1, "Low-Poo Hydrating Wash", "Use a sulfate-free shampoo with moisturizing lipids. Cleanses without stripping curl hydration.", "Sulfate-free Argan Oil Shampoo", "2x per week morning"),
                        SkincareRoutineStep(2, "Slippery Conditioner", "Apply generous amounts and detangle gently with a wide-tooth comb while wet.", "Shea Butter Slip Conditioner", "Every wash morning"),
                        SkincareRoutineStep(3, "Curly Definition Styling", "Scrunch into soaking wet hair using the 'praying hands' technique, then air-dry or diffuse.", "Argan Oil Leave-In Cream & Flaxseed Styling Gel", "Every wash morning")
                    ),
                    evening = listOf(
                        SkincareRoutineStep(1, "Pineapple Hair Tie", "Gather curls loosely at the very top of your head to prevent flattening during sleep.", "Silk Scrunchie Loop", "Every Evening"),
                        SkincareRoutineStep(2, "Cuticle Sealing Elixir", "Warm 3 drops between hands and scrunch onto dry ends to lock in hydration.", "Pure Argan Oil or Marula Oil", "Every Evening")
                    ),
                    weekly = listOf(
                        SkincareRoutineStep(1, "Deep Keratin Repair Mask", "Apply to damp hair from roots to ends, leave for 20 minutes under a shower cap, then rinse.", "Hydrolyzed Keratin & Honey Deep Conditioner", "1x per week (Saturday)"),
                        SkincareRoutineStep(2, "Scalp Moisture Balancing Scrub", "Gently clear hair styling product buildup from the scalp.", "Coconut Oil & Sugarcane Scalp Exfoliator", "1x per week (Wednesday)")
                    )
                ),
                recommendations = ProductRecommendations(
                    budget = listOf(
                        Product("Cantu Shea Butter Leave-In", "Cantu", "Pure Shea Butter, Canola Oil", "₹750", 92, "https://www.nykaa.com", "Nykaa"),
                        Product("Sulfate-Free Moisture shampoo", "Love Beauty & Planet", "Coconut Oil, Ylang Ylang", "₹450", 89, "https://www.amazon.in", "Amazon")
                    ),
                    standard = listOf(
                        Product("Curl Definition Gel", "Ashba Botanics", "Flaxseed Extract, Amino Acids", "₹1,099", 94, "https://www.nykaa.com", "Nykaa"),
                        Product("No. 3 Hair Perfector", "Olaplex", "Bis-Aminopropyl Diglycol Dimaleate", "₹2,950", 91, "https://www.sephora.com", "Sephora")
                    ),
                    premium = listOf(
                        Product("Don't Despair, Repair! Deep Conditioning", "Briogeo", "Rosehip Oil, Algae Extract, Biotin", "₹3,800", 93, "https://www.sephora.com", "Sephora"),
                        Product("Curl Charisma leave-in", "Briogeo", "Rice Amino Acids, Avocado Oil", "₹2,600", 90, "https://www.sephora.com", "Sephora")
                    )
                ),
                ingredientsSafety = IngredientsSafety(
                    beneficialIngredients = listOf(
                        IngredientInsight("Hydrolyzed Keratin / Wheat Protein", "Bond Builder", "Highly Recommended", "Fills in the microscopic gaps along damaged, porous hair shafts, restoring elasticity."),
                        IngredientInsight("Argan Oil & Shea Butter", "Hydrophobic Emollient", "Highly Recommended", "Coats curly cuticles to block out frizz-inducing humidity and retain internal water.")
                    ),
                    harmfulIngredients = listOf(
                        IngredientInsight("Isopropyl Alcohol", "Evaporative Agent", "Avoid", "Commonly found in aerosols. Dries out the natural moisture of curls, causing rough texture and breakage."),
                        IngredientInsight("Insoluble Silicones (e.g. Dimethicone)", "Build-up Coat", "Avoid", "Accumulates on curls, preventing fresh water from entering the hair shaft during washes.")
                    )
                ),
                homeRemedies = listOf(
                    HomeRemedy(
                        name = "The Snoop Honey-Banana Mask",
                        ingredients = listOf("1 overripe banana (blended smooth)", "1 tbsp organic honey", "1 tbsp pure coconut oil"),
                        instructions = "Blend the banana completely until it is a baby-food puree with ZERO chunks. Whisk in honey and oil. Apply to damp curls, leave under a shower cap for 30 minutes, and rinse thoroughly.",
                        expectedBenefits = "Bananas are rich in potassium and natural oils that soften hair cuticles. Honey acts as a humectant to hold curl moisture.",
                        precautions = "The banana must be completely liquefied; otherwise, tiny bits can stick to your curls."
                    ),
                    HomeRemedy(
                        name = "Flaxseed Defining Gel DIY",
                        ingredients = listOf("2 tbsp whole flaxseeds", "1 cup water"),
                        instructions = "Boil seeds in water for 7-10 minutes, stirring occasionally, until a thin egg-white gel forms. Strain immediately through a stocking. Let cool, then scrunch into wet curls.",
                        expectedBenefits = "Flaxseeds provide immediate natural hold, omega-3 nourishment, and intense shine without flakes or crunch.",
                        precautions = "Keep refrigerated and discard after 10 days."
                    )
                ),
                budgetAlternatives = listOf(
                    BudgetAlternative(
                        premiumProduct = "Olaplex No. 3 Bond Perfector (₹2,950)",
                        budgetProduct = "Minimalist Maleic Bond Repair Complex (₹499)",
                        saving = "₹2,451",
                        reason = "Both use molecular bond-building technology to repair sulfur-bridge links broken by heat styling."
                    )
                )
            )

            else -> SkinReport( // Normal Scalp & Balanced Hair / Fallback
                skinType = "Balanced Scalp & Mane / Wavy 2B",
                skinTypeScore = 95,
                skinTypeDescription = "Healthy sebum-to-moisture ratio. Follicles are strong and highly nourished. Hair shafts are strong with aligned cuticles and brilliant natural luster.",
                skinScore = 90,
                concerns = listOf(
                    SkinConcern("Frizz", "Low", "Slight humidity-induced fluffiness around the top layers, easily resolved with light oils.", 20)
                ),
                routines = SkincareRoutine(
                    morning = listOf(
                        SkincareRoutineStep(1, "Gentle Botanical Wash", "Wash with a pH-balanced organic shampoo to keep roots clean.", "Gentle Aloe & Green Tea Shampoo", "Every 2 days morning"),
                        SkincareRoutineStep(2, "Silky Detangling Conditioner", "Apply from mid-lengths to ends, let sit for 60 seconds, and rinse.", "Hydrolyzed Silk Conditioner", "Every wash morning"),
                        SkincareRoutineStep(3, "Radiance Protection Spray", "Lightweight shield to protect from UV and pollution.", "Rosewater & Vitamin E Protective Spray", "Daily morning")
                    ),
                    evening = listOf(
                        SkincareRoutineStep(1, "Bamboo Root Comb", "Detangle gently with a wooden wide-tooth comb to spread natural oils.", "Bamboo Wide-Tooth Comb", "Every Evening"),
                        SkincareRoutineStep(2, "Sleep Bonnet protective", "Protect natural waves from cotton sheet friction.", "Satin Pillowcase protection", "Every Evening")
                    ),
                    weekly = listOf(
                        SkincareRoutineStep(1, "Lustre-Boost Hair Spa", "Apply a lightweight oil mask to amplify gloss and shine.", "Argan & Rosehip Hair Oil Spa", "1x per week")
                    )
                ),
                recommendations = ProductRecommendations(
                    budget = listOf(
                        Product("Gentle Aloe & Green Tea Shampoo", "Simple", "Pro-Vitamin B5, Aloe Vera", "₹249", 96, "https://www.nykaa.com", "Nykaa"),
                        Product("Smoothing Hair Serum", "L'Oreal", "Argan Oil, Silk Proteins", "₹399", 93, "https://www.nykaa.com", "Nykaa")
                    ),
                    standard = listOf(
                        Product("Argan Hair Mask Spa", "Mamaearth", "Argan Oil, Avocado Oil, Milk Proteins", "₹599", 94, "https://www.nykaa.com", "Nykaa")
                    ),
                    premium = listOf(
                        Product("The Ritual Hair Oil", "Moroccanoil", "Pure Argan Oil, Linseed Extract", "₹3,800", 91, "https://www.sephora.com", "Sephora")
                    )
                ),
                ingredientsSafety = IngredientsSafety(
                    beneficialIngredients = listOf(
                        IngredientInsight("Squalane & Argan Oil", "Shine Amplifiers", "Highly Recommended", "Provides a breathable barrier that reflects light beautifully without greasy weight."),
                        IngredientInsight("Panthenol (Vitamin B5)", "Moisture Binder", "Highly Recommended", "Penetrates the hair cortex to swell the hair shaft, adding structural volume and luster.")
                    ),
                    harmfulIngredients = listOf(
                        IngredientInsight("Aggressive Sulfates (Ammonium Lauryl Sulfate)", "Harsh Strip", "Avoid", "Unnecessarily harsh cleanser that can dull natural hair shine over time.")
                    )
                ),
                homeRemedies = listOf(
                    HomeRemedy(
                        name = "Shine Boost Honey Water Spray",
                        ingredients = listOf("1 cup warm distilled water", "1/2 tsp organic raw honey", "2 drops sweet orange oil"),
                        instructions = "Dissolve honey in warm water, add orange oil, shake well, and spray lightly onto damp hair before styling. Do not rinse out.",
                        expectedBenefits = "Honey is a natural humectant that adds brilliant glass-like shine to straight and wavy strands.",
                        precautions = "Do not exceed the honey measurement to avoid sticky hair texture."
                    )
                ),
                budgetAlternatives = listOf(
                    BudgetAlternative(
                        premiumProduct = "Moroccanoil Treatment Original (₹3,800)",
                        budgetProduct = "Minimalist Cold-Pressed Argan Oil (₹599)",
                        saving = "₹3,201",
                        reason = "Both utilize the protective power of premium Moroccan Argan Oil to deliver shiny, split-end resistant hair."
                    )
                )
            )
        }
    }
}
