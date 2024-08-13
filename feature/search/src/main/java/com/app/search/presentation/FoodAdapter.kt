// FoodAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.data.dto.ParsedFood
import com.app.search.databinding.ItemFoodSearchBinding
import com.app.search.presentation.SearchFragmentDirections

class FoodAdapter(private val navController: NavController) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private var foodList: List<ParsedFood> = emptyList()

    fun submitList(list: List<ParsedFood>) {
        foodList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]

        holder.itemView.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToNavigationDetail(food)
            navController.navigate(action)
        }

        holder.binding.foodLabel.text = food.label.toString()
        //holder.binding.foodCalories.text = "${food.nutrients.ENERC_KCAL} kcal"
        holder.binding.foodImage.load(food.image)
    }

    override fun getItemCount(): Int = foodList.size

    class FoodViewHolder(val binding: ItemFoodSearchBinding) : RecyclerView.ViewHolder(binding.root)
}
