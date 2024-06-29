import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.databinding.ViewContactsListBinding
import com.example.week1.ui.Phone.ContactPair
import java.util.Locale

class ContactsAdapter(private val contactPairs: List<ContactPair>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ViewContactsListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contactPairs[position])
    }

    override fun getItemCount() = contactPairs.size

    inner class ViewHolder(private val binding: ViewContactsListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contactPair: ContactPair) {
            val contact1 = contactPair.contact1
            val contact2 = contactPair.contact2

            binding.txtName1.text = contact1.name
            binding.txtPhoneNumber1.text = PhoneNumberUtils.formatNumber(contact1.number, Locale.getDefault().country)

            if (contact2 != null) {
                binding.txtName2.text = contact2.name
                binding.txtPhoneNumber2.text = PhoneNumberUtils.formatNumber(contact2.number, Locale.getDefault().country)
                binding.txtName2.visibility = View.VISIBLE
                binding.txtPhoneNumber2.visibility = View.VISIBLE
            } else {
                binding.txtName2.visibility = View.INVISIBLE
                binding.txtPhoneNumber2.visibility = View.INVISIBLE
            }
        }
    }
}
