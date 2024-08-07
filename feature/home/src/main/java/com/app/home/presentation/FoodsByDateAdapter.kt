import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.core.data.model.FoodModel
import com.app.home.databinding.ItemFoodsBydateBinding
import com.app.home.presentation.FoodItemsDialogFragment

class FoodsByDateAdapter(
    private var foodsByDate: Map<String, List<FoodModel>> = emptyMap(),
    private val fragmentActivity: androidx.fragment.app.FragmentActivity
) : RecyclerView.Adapter<FoodsByDateAdapter.FoodViewHolder>() {

    fun updateData(newFoods: List<FoodModel>) {
        Log.d("FoodsByDateAdapter", "Updating data with newFoods: $newFoods")
        foodsByDate = newFoods.groupBy { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodsBydateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val date = foodsByDate.keys.elementAt(position)
        val foods = foodsByDate[date] ?: emptyList()
        Log.d("FoodsByDateAdapter", "Binding data at position $position with date: $date and foods: $foods")
        holder.bind(date, foods)
    }

    override fun getItemCount() = foodsByDate.size

    inner class FoodViewHolder(private val binding: ItemFoodsBydateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String, foods: List<FoodModel>) {
            val firstFoodItem = foods.firstOrNull()
            val dayOfMonth = firstFoodItem?.dayOfMonth ?: ""
            val dayName = firstFoodItem?.dayName ?: ""

            binding.buttonDateOfMeal.text = dayOfMonth

            binding.buttonDateOfMeal.setOnClickListener {
                showFoodsForDate("${firstFoodItem?.date} ${firstFoodItem?.dayName}", foods)
            }
        }

        private fun showFoodsForDate(formattedDate: String, foods: List<FoodModel>) {

            val totalCalories = foods.sumOf { it.calories }
            val totalFat = foods.sumOf { it.fat }
            val totalCarbohydrates = foods.sumOf { it.carbohydrates }


            val dialog = FoodItemsDialogFragment.newInstance(
                formattedDate,
                totalCalories,
                totalFat,
                totalCarbohydrates.toInt()
            )
            dialog.show(fragmentActivity.supportFragmentManager, "FoodItemsDialogFragment")
        }
    }
}
