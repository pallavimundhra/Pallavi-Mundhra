package com.example.data

import android.content.Context
import androidx.room.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow

// --- Model Classes for Skin Report ---

data class SkinConcern(
    val name: String,         // Acne, Hyperpigmentation, Dark Spots, Redness, Large Pores, Dehydration, Uneven Texture
    val severity: String,     // Low, Medium, High
    val description: String,
    val percentage: Int       // e.g. 75
)

data class Product(
    val name: String,
    val brand: String,
    val keyIngredients: String,
    val price: String,
    val suitabilityScore: Int, // e.g. 92
    val buyLink: String,
    val marketplace: String    // Amazon, Nykaa, Sephora, Brand Website
)

data class ProductRecommendations(
    val budget: List<Product>,
    val standard: List<Product>,
    val premium: List<Product>
)

data class SkincareRoutineStep(
    val stepNumber: Int,
    val title: String,         // Cleanser, Serum, Moisturizer, Sunscreen
    val instruction: String,
    val productType: String,
    val frequency: String
)

data class SkincareRoutine(
    val morning: List<SkincareRoutineStep>,
    val evening: List<SkincareRoutineStep>,
    val weekly: List<SkincareRoutineStep>
)

data class IngredientInsight(
    val name: String,
    val role: String,          // Antioxidant, Hydrator, Exfoliant
    val suitability: String,   // Beneficial, Highly Recommended, Avoid
    val explanation: String
)

data class IngredientsSafety(
    val beneficialIngredients: List<IngredientInsight>,
    val harmfulIngredients: List<IngredientInsight>
)

data class HomeRemedy(
    val name: String,
    val ingredients: List<String>,
    val instructions: String,
    val expectedBenefits: String,
    val precautions: String
)

data class BudgetAlternative(
    val premiumProduct: String,
    val budgetProduct: String,
    val saving: String,
    val reason: String
)

data class SkinReport(
    val skinType: String,       // Oily, Dry, Combination, Sensitive, Normal
    val skinTypeScore: Int,     // e.g. 88
    val skinTypeDescription: String,
    val skinScore: Int,         // Overall score, e.g. 78 (higher is healthier)
    val concerns: List<SkinConcern>,
    val routines: SkincareRoutine,
    val recommendations: ProductRecommendations,
    val ingredientsSafety: IngredientsSafety,
    val homeRemedies: List<HomeRemedy>,
    val budgetAlternatives: List<BudgetAlternative>
)

// --- Room Entity ---

@Entity(tableName = "skin_analysis")
data class SkinAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val imageSourceUri: String, // Path or identifier of the image analyzed
    val report: SkinReport,
    val notes: String = ""      // User notes or journaling on progress
)

// --- Room Type Converters (Moshi JSON Serialization) ---

class SkinReportConverters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(SkinReport::class.java)

    @TypeConverter
    fun fromSkinReport(report: SkinReport?): String {
        return report?.let { adapter.toJson(it) } ?: ""
    }

    @TypeConverter
    fun toSkinReport(json: String?): SkinReport? {
        return if (json.isNullOrEmpty()) null else adapter.fromJson(json)
    }
}

// --- DAO ---

@Dao
interface SkinAnalysisDao {
    @Query("SELECT * FROM skin_analysis ORDER BY timestamp DESC")
    fun getAllAnalyses(): Flow<List<SkinAnalysisEntity>>

    @Query("SELECT * FROM skin_analysis WHERE id = :id LIMIT 1")
    suspend fun getAnalysisById(id: Long): SkinAnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: SkinAnalysisEntity): Long

    @Query("DELETE FROM skin_analysis WHERE id = :id")
    suspend fun deleteAnalysisById(id: Long)

    @Query("DELETE FROM skin_analysis")
    suspend fun clearAll()
}

// --- Database ---

@Database(entities = [SkinAnalysisEntity::class], version = 1, exportSchema = false)
@TypeConverters(SkinReportConverters::class)
abstract class SkinDatabase : RoomDatabase() {
    abstract fun dao(): SkinAnalysisDao

    companion object {
        @Volatile
        private var INSTANCE: SkinDatabase? = null

        fun getDatabase(context: Context): SkinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkinDatabase::class.java,
                    "auraskin_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Repository Pattern ---

class SkinAnalysisRepository(private val dao: SkinAnalysisDao) {
    val allAnalyses: Flow<List<SkinAnalysisEntity>> = dao.getAllAnalyses()

    suspend fun getAnalysisById(id: Long): SkinAnalysisEntity? {
        return dao.getAnalysisById(id)
    }

    suspend fun insertAnalysis(analysis: SkinAnalysisEntity): Long {
        return dao.insertAnalysis(analysis)
    }

    suspend fun deleteAnalysis(id: Long) {
        dao.deleteAnalysisById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
