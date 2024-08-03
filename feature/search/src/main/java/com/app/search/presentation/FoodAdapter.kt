import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.search.data.model.ParsedFood
import com.app.search.databinding.ItemFoodBinding
import coil.load
import com.app.search.R

class FoodAdapter(private val navController: NavController) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

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

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("getFoodDetail", "ataa")
            }

            navController.navigate(com.app.detail.R.id.navigation_detail, bundle)
        }

        holder.binding.foodLabel.text = food.label
        holder.binding.foodCalories.text = "${food.nutrients.ENERC_KCAL} kcal"

        holder.binding.foodImage.load(food.image) {
            // placeholder(R.drawable.placeholder_image)
            // error(R.drawable.error_image)
        }
    }

    override fun getItemCount(): Int = foodList.size

    class FoodViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)
}
