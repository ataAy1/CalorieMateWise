import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.search.data.model.ParsedFood
import com.app.search.databinding.ItemFoodBinding
import coil.load
import com.app.search.R

class FoodAdapter : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private var foodList: List<ParsedFood> = emptyList()

    fun submitList(list: List<ParsedFood>) {
        foodList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.binding.foodLabel.text = food.label
        holder.binding.foodCalories.text = "${food.nutrients.ENERC_KCAL} kcal"

        // Load image using Coil
        holder.binding.foodImage.load(food.image) {
            //placeholder(R.drawable.placeholder_image) //
            //error(R.drawable.error_image) //
        }
    }

    override fun getItemCount(): Int = foodList.size

    class FoodViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)
}
