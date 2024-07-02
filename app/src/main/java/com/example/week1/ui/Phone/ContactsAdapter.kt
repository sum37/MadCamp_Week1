import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.example.week1.databinding.ViewContactsListBinding
import com.example.week1.ui.Phone.ContactPair
import com.example.week1.ui.Phone.ContactsData
import java.io.InputStream
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
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contactPairs[position])
    }

    override fun getItemCount() = contactPairs.size

    inner class ViewHolder(private val binding: ViewContactsListBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contactPair: ContactPair) {
            val contact1 = contactPair.contact1
            val contact2 = contactPair.contact2

            binding.txtName1.text = contact1.name
            binding.txtPhoneNumber1.text = PhoneNumberUtils.formatNumber(contact1.number, Locale.getDefault().country)
            binding.column1.setOnClickListener { listener.onItemClick(contact1) }
            loadContactPhoto(contact1.id, binding.imgContactPhoto1)

            if (contact2 != null) {
                binding.txtName2.text = contact2.name
                binding.txtPhoneNumber2.text = PhoneNumberUtils.formatNumber(contact2.number, Locale.getDefault().country)
                binding.column2.visibility = View.VISIBLE
                binding.column2.setOnClickListener { listener.onItemClick(contact2) }
                loadContactPhoto(contact2.id, binding.imgContactPhoto2)
            } else {
                binding.column2.visibility = View.INVISIBLE
            }
        }

        private fun loadContactPhoto(contactId: Int, imageView: ImageView) {
            val photoUri = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_URI, contactId.toString()
            )
            val inputStream: InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(
                context.contentResolver, photoUri
            )
            inputStream?.let {
                val bitmap = BitmapFactory.decodeStream(it)
                imageView.setImageBitmap(bitmap)
                it.close()
            } ?: run {
                imageView.setImageResource(R.drawable.default_image) // 기본 이미지 설정
            }
        }
    }
}
