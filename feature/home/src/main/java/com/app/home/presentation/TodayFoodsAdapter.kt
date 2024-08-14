import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.core.data.model.FoodModel
import com.app.home.databinding.DialogFoodDetailsBinding
import com.app.home.databinding.ItemTodayFoodsBinding

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

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val food = foods[position]
                    showFoodDetailsDialog(food)
                }
            }
        }

        fun bind(food: FoodModel) {
            binding.textFoodLabel.text = food.label
            binding.textFoodCalories.text = "${food.calories.toInt()} kalori"
            binding.imageViewFood.load(food.image)
        }

        private fun showFoodDetailsDialog(food: FoodModel) {
            val dialogBinding = DialogFoodDetailsBinding.inflate(LayoutInflater.from(binding.root.context))

            val dialog = android.app.AlertDialog.Builder(binding.root.context)
                .setView(dialogBinding.root)
                .setPositiveButton(android.R.string.ok, null)
                .create()

            dialogBinding.dialogTextFoodLabel.text = food.label
            dialogBinding.dialogTextFoodCalories.text = "Kalori: ${food.calories}"
            dialogBinding.dialogTextFoodCarbs.text = "Karbonhidrat: ${food.carbohydrates}"
            dialogBinding.dialogTextFoodProtein.text = "Protein: ${food.protein}"
            dialogBinding.dialogTextFoodFat.text = "Yağ: ${food.fat}"
            dialogBinding.dialogTextGram.text = "Gram: ${food.weightofFood}"
            dialogBinding.dialogImageViewFood.load(food.image)


            dialog.show()
        }

    }

}
