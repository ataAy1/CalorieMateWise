import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.core.data.model.FoodModel
import com.app.home.databinding.ItemFoodsBydateBinding

class FoodsByDateAdapter(
    private var foods: List<FoodModel>
) : RecyclerView.Adapter<FoodsByDateAdapter.FoodViewHolder>() {

    fun updateData(newFoods: List<FoodModel>) {
        Log.d("FoodsByDateAdapter", "Updating data with newFoods: $newFoods")
        foods = newFoods
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodsBydateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        Log.d("FoodsByDateAdapter", "Binding data at position $position with food: $food")
        holder.bind(food)
    }

    override fun getItemCount() = foods.size

    inner class FoodViewHolder(private val binding: ItemFoodsBydateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodModel) {
            binding.buttonDateOfMeal.text = food.dayOfMonth
        }
    }
}
