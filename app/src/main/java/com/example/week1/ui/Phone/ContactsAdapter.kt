import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.databinding.ViewContactsListBinding
import com.example.week1.ui.Phone.ContactPair
import com.example.week1.ui.Phone.ContactsData
import java.util.Locale

class ContactsAdapter(
    private val contactPairs: List<ContactPair>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(contact: ContactsData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewContactsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

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
            binding.column1.setOnClickListener { listener.onItemClick(contact1) }

            if (contact2 != null) {
                binding.txtName2.text = contact2.name
                binding.txtPhoneNumber2.text = PhoneNumberUtils.formatNumber(contact2.number, Locale.getDefault().country)
                binding.column2.visibility = View.VISIBLE
                binding.column2.setOnClickListener { listener.onItemClick(contact2) }
            } else {
                binding.column2.visibility = View.INVISIBLE
            }
        }
    }
}
