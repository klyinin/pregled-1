package com.bobi.vendorium.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bobi.vendorium.R
import com.bobi.vendorium.adapters.ImageAdapter
import com.bobi.vendorium.models.Listing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class AddListingFragment : Fragment(R.layout.fragment_add_listing) {

    private lateinit var etListingName: EditText
    private lateinit var etListingPrice: EditText
    private lateinit var etListingDescription: EditText
    private lateinit var btnSaveData: Button
    private lateinit var spinnerCategory: Spinner

    private lateinit var dbRef: DatabaseReference

    private lateinit var btnChooseImage: Button
    private lateinit var rvSelectedImages: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private val selectedImageUris: MutableList<Uri> = mutableListOf()

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etListingName = view.findViewById(R.id.etListingName)
        etListingPrice = view.findViewById(R.id.etListingPrice)
        etListingDescription = view.findViewById(R.id.etListingDescription)
        btnSaveData = view.findViewById(R.id.btnSave)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)

        dbRef = FirebaseDatabase.getInstance().getReference("Listings")

        btnSaveData.setOnClickListener {
            saveListingData()
        }

        btnChooseImage = view.findViewById(R.id.btnChoose)
        rvSelectedImages = view.findViewById(R.id.rvSelectedImages)

        imageAdapter = ImageAdapter(selectedImageUris)
        rvSelectedImages.adapter = imageAdapter
        rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        btnChooseImage.setOnClickListener {
            selectImage()
        }

        setupCategorySpinner()
    }

    private fun setupCategorySpinner() {
        //val categories = listOf("Electronics", "Clothes", "House", "Furniture", "Books", "Sports", "Appliances") // Replace with your predefined category list

        val jezik = getResources().getConfiguration().getLocales().get(0).toString()
        Log.d("LOG jezik", jezik)

        val categories = when (jezik) {
            "sl" -> listOf("Elektronika", "Oblačila", "Dom", "Pohištvo", "Knjige", "Šport", "Aparati") // Slovenian categories
            else -> listOf("Electronics", "Clothes", "House", "Furniture", "Books", "Sports", "Appliances") // Default English categories
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private val MAX_IMAGE_LIMIT = 3 // Maximum number of images allowed

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                // Multiple images selected
                val count = data.clipData!!.itemCount
                val imageCount = selectedImageUris.size + count // Total number of selected images

                if (imageCount > MAX_IMAGE_LIMIT) {
                    val jezik = getResources().getConfiguration().getLocales().get(0).toString()
                    when (jezik) {
                        "sl" -> Toast.makeText(requireContext(), "Lahko izberete do $MAX_IMAGE_LIMIT slike", Toast.LENGTH_SHORT).show()
                        else ->  Toast.makeText(requireContext(), "You can select up to $MAX_IMAGE_LIMIT images", Toast.LENGTH_SHORT).show() // Default English categories
                    }

                    return
                }

                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImageUris.add(imageUri)
                }
            } else if (data?.data != null) {
                // Single image selected
                if (selectedImageUris.size >= MAX_IMAGE_LIMIT) {
                    Toast.makeText(requireContext(), "You can select up to $MAX_IMAGE_LIMIT images", Toast.LENGTH_SHORT).show()
                    return
                }

                val imageUri = data.data
                imageUri?.let { selectedImageUris.add(it) }
            }

            imageAdapter.notifyDataSetChanged()
        }
    }

    private fun saveListingData() {
        val listingName = etListingName.text.toString()
        val listingPrice = etListingPrice.text.toString().toDoubleOrNull()
        val listingDescription = etListingDescription.text.toString()
        val ownerId = FirebaseAuth.getInstance().currentUser?.uid
        val dateOfCreation = getCurrentTimestamp()
        val category = getCategorySelectionEnglish()

        if (listingName.isEmpty()) {
            etListingName.error = "Please enter name"
            return
        }
        if (listingPrice == null || listingPrice <= 0) {
            etListingPrice.error = "Please enter a valid price"
            return
        }

        if (selectedImageUris.isNotEmpty()) {
            uploadImages(listingName, listingPrice, listingDescription, ownerId, dateOfCreation, category)
        } else {
            saveListing(listingName, listingPrice, listingDescription, ownerId, dateOfCreation, category, emptyList())
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun getCategorySelection(): String {
        return spinnerCategory.selectedItem.toString()
    }

    private fun uploadImages(
        listingName: String,
        listingPrice: Double,
        listingDescription: String,
        ownerId: String?,
        dateOfCreation: String,
        category: String,
    ) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val imageUrls = mutableListOf<String>()
        var uploadCount = 0

        for (uri in selectedImageUris) {
            val ref = storageReference.child("images/" + UUID.randomUUID().toString())

            ref.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    progressDialog.setMessage("Uploaded ${++uploadCount} of ${selectedImageUris.size}")

                    // Retrieve the download URL of the uploaded image
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        imageUrls.add(downloadUri.toString())

                        if (uploadCount == selectedImageUris.size) {
                            progressDialog.dismiss()
                            saveListing(listingName, listingPrice, listingDescription, ownerId, dateOfCreation, category, imageUrls)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Failed " + e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveListing(
        listingName: String,
        listingPrice: Double,
        listingDescription: String,
        ownerId: String?,
        dateOfCreation: String,
        category: String,
        imageUrls: List<String>,
    ) {
        val listingId = dbRef.push().key

        if (listingId == null) {
            Toast.makeText(requireContext(), "Error generating listing ID", Toast.LENGTH_LONG).show()
            return
        }

        val listing = Listing(
            id = listingId,
            name = listingName,
            price = listingPrice,
            description = listingDescription,
            dateOfCreation = dateOfCreation,
            category = category,
            images = imageUrls,
            ownerID = ownerId ?: "",
        )

        dbRef.child(listingId).setValue(listing)
            .addOnCompleteListener {

                val jezik = getResources().getConfiguration().getLocales().get(0).toString()
                when (jezik) {
                    "sl" -> Toast.makeText(requireContext(), "Oglas naložen uspešno", Toast.LENGTH_LONG).show()
                    else ->  Toast.makeText(requireContext(), "Listing inserted successfully", Toast.LENGTH_LONG).show() // Default English categories
                }

                etListingName.text.clear()
                etListingPrice.text.clear()
                etListingDescription.text.clear()
                selectedImageUris.clear()
                imageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { err ->
                Toast.makeText(requireContext(), "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun getCategorySelectionEnglish(): String {
        val jezik = getResources().getConfiguration().getLocales().get(0).toString()
        return when (jezik) {
            "sl" -> {
                val categoriesSlovenian = listOf("Elektronika", "Oblačila", "Dom", "Pohištvo", "Knjige", "Šport", "Aparati")
                val categoriesEnglish = listOf("Electronics", "Clothes", "House", "Furniture", "Books", "Sports", "Appliances")
                val selectedCategorySlovenian = spinnerCategory.selectedItem.toString()
                val categoryIndex = categoriesSlovenian.indexOf(selectedCategorySlovenian)
                if (categoryIndex != -1) {
                    categoriesEnglish[categoryIndex]
                } else {
                    ""
                }
            }
            else -> spinnerCategory.selectedItem.toString()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 22
    }
}
