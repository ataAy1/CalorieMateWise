import com.app.core.data.model.FoodModel

data class HomeUIState(
    val todayFoods: List<FoodModel> = emptyList(),
    val foodsByDate: List<FoodModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
