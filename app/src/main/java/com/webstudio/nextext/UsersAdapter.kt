import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.webstudio.nextext.R
import com.webstudio.nextext.User

class UsersAdapter(
    private val users: List<User>,
    private val onMessageClick: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.userName)
        val mobile = view.findViewById<TextView>(R.id.userMobile)
        val profile = view.findViewById<ImageView>(R.id.profileImage)
        val messageButton = view.findViewById<Button>(R.id.messageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.mobile.text = user.mobile


        Glide.with(holder.itemView.context)
            .load(user.profileUrl)
            .placeholder(R.drawable.default_profile)
            .circleCrop()
            .into(holder.profile)

        holder.messageButton.setOnClickListener {
            onMessageClick(user)
        }
    }
}
