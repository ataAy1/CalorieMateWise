import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.home.databinding.ItemTodayFoodsBinding
import com.app.core.data.model.FoodModel

class TodayFoodsAdapter(
    private var foods: List<FoodModel>
) : RecyclerView.Adapter<TodayFoodsAdapter.FoodViewHolder>() {

    fun updateData(newFoods: List<FoodModel>) {
        Log.d("TodayFoodsAdapter", "Updating data with newFoods: $newFoods")
        foods = newFoods
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding =
            ItemTodayFoodsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        Log.d("TodayFoodsAdapter", "Binding data at position $position with food: $food")
        holder.bind(food)
    }

    override fun getItemCount() = foods.size

    inner class FoodViewHolder(private val binding: ItemTodayFoodsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodModel) {
            binding.textFoodLabel.text = food.label
            binding.textFoodCalories.text = food.calories.toString()
            binding.imageViewFood.load(food.image)
        }
    }
}
