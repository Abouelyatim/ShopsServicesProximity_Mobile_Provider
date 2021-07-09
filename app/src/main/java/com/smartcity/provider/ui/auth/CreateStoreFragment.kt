package com.smartcity.provider.ui.auth

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.smartcity.provider.R
import com.smartcity.provider.di.auth.AuthScope
import com.smartcity.provider.models.Store
import com.smartcity.provider.models.StoreAddress
import com.smartcity.provider.ui.*
import com.smartcity.provider.ui.auth.state.AuthStateEvent
import com.smartcity.provider.ui.auth.state.CategoryStore
import com.smartcity.provider.ui.auth.state.StoreFields
import com.smartcity.provider.util.Constants
import com.smartcity.provider.util.ErrorHandling
import com.smartcity.provider.util.SuccessHandling
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_store.*
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


@AuthScope
class CreateStoreFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAuthFragment(R.layout.fragment_create_store){
      lateinit var buttonGroup:ThemedToggleButtonGroup

    val viewModel: AuthViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCategoryStore()
        subscribeObservers()


        store_image.setOnClickListener {
               if(stateChangeListener.isStoragePermissionGranted()){
                   pickFromGallery()
               }
           }

           update_textview_store.setOnClickListener {
               if(stateChangeListener.isStoragePermissionGranted()){
                   pickFromGallery()
               }
           }


        val applicationInfo = activity!!.packageManager.getApplicationInfo( activity!!.packageName, PackageManager.GET_META_DATA)
        input_address.setOnClickListener {
            val gpsTracker = GpsTracker(context!!)
            if(stateChangeListener.isFineLocationPermissionGranted()){
                gpsTracker.getCurrentLocation()
                val latitude: Double = gpsTracker.getLatitude()
                val longitude: Double = gpsTracker.getLongitude()

                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(latitude, longitude)
                    .showLatLong(true)
                    .setMapZoom(16.0f)
                    .setMapRawResourceStyle(R.raw.map_style)
                    .setMapType(MapType.NORMAL)
                    .setPlaceSearchBar(false, applicationInfo.metaData.getString("com.google.android.geo.API_KEY"))
                    .setMarkerImageImageColor(R.color.bleu)
                    .setFabColor(R.color.bleu)
                    .setPrimaryTextColor(R.color.black)
                    .setSecondaryTextColor(R.color.dark)
                    .build(activity!!)
                startActivityForResult(intent, com.sucho.placepicker.Constants.PLACE_PICKER_REQUEST)
            }else{
                stateChangeListener.isFineLocationPermissionGranted()
            }
        }
           next_button.setOnClickListener {
               val callback: AreYouSureCallback = object: AreYouSureCallback {

                   override fun proceed() {
                       create()
                   }

                   override fun cancel() {
                       // ignore
                   }

               }
               uiCommunicationListener.onUIMessageReceived(
                   UIMessage(
                       getString(R.string.are_you_sure_publish),
                       UIMessageType.AreYouSureDialog(callback)
                   )
               )
           }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                activity!!,
                permissionsToRequest.toTypedArray(),
                122);
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE)
    }

    private fun getCategoryStore() {
       /* viewModel.setStateEvent(
            AuthStateEvent.GetCategoryStore()
        )*/
    }




    var beug=true
    override fun onPause() {
        super.onPause()
        beug=false
    }

    fun initButtonGroup(listCategory:List<String>){

        if (beug){
             buttonGroup = ThemedToggleButtonGroup(context!!)
            buttonGroup.justifyContent = JustifyContent.CENTER // this is optional
            rootView.addView(buttonGroup)

            listCategory.map {

                val btn1 = ThemedButton(buttonGroup.context)

                btn1.text = it
                buttonGroup.addView(btn1, ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
            }

        }


           /* buttonGroup = activity!!.findViewById<ThemedToggleButtonGroup>(R.id.rootView)
            buttonGroup?.let {
                buttonGroup!!.justifyContent=JustifyContent.FLEX_START
                val height=120


                buttonGroup!!.requiredAmount=2
                buttonGroup!!.selectableAmount=listCategory.size

                listCategory.distinct().map { list ->
                    val btn = ThemedButton(buttonGroup!!.context)
                    btn.text = list
                    buttonGroup!!.addView(btn, ViewGroup.LayoutParams(WRAP_CONTENT, height)) }

                //val selectedButtons = buttonGroup.selectedButtons


        }*/

    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    private fun create() {
        var multipartBody: MultipartBody.Part? = null

        viewModel.viewState.value?.storeFields?.newImageUri?.let{ imageUri ->
            imageUri.path?.let{filePath ->
                val imageFile = File(filePath)
                Log.d(TAG, "CreateBlogFragment, imageFile: file: ${imageFile}")
                val requestBody =
                    RequestBody.create(
                        MediaType.parse("image/*"),
                        imageFile
                    )
                // name = field name in serializer
                // filename = name of the image file
                // requestBody = file with file type information
                multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )
            }
        }
        multipartBody?.let {
            val store=Store(
                input_name.text.toString(),
                input_description.text.toString(),
                viewModel.viewState.value!!.storeFields.store_address!!,
                -1
            )


            viewModel.setStateEvent(
                AuthStateEvent.CreateStoreAttemptEvent(
                    store,
                    it
                )
            )
        }

    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)


            if(dataState != null){
                dataState.data?.let { data ->
                    data.data?.peekContent()?.let{
                        viewModel.setCategoryStore(
                            it.categoryStore
                        )
                    }
                }
            }

            if(dataState != null){
                dataState.data?.let { data ->

                    data.response?.peekContent()?.let{ response ->
                        if(response.message.equals(SuccessHandling.STORE_CREATION_DONE)){
                            findNavController().navigate(R.id.action_createStoreFragment_to_launcherFragment)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.categoryStore.listCategoryStore?.let {
               // initButtonGroup(it)
            }

        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{
            it.storeFields?.let{storeFields ->
                setStoreProperties(
                    storeFields.store_name,
                    storeFields.store_description,
                    storeFields.store_address,
                    storeFields.newImageUri
                )
            }
        })
    }

    fun setStoreProperties(name: String? ,
                           description: String?,
                           address: StoreAddress?,
                           newImageUri: Uri?){
        if(newImageUri != null){
            requestManager
                .load(newImageUri)
                .into(store_image)
        }
        else{
            requestManager
                .load(R.drawable.default_image)
                .into(store_image)
        }
        input_name.setText(name)
        input_description.setText(description)
        address?.let {
            input_address.setText(it.addressLine())
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "CROP: RESULT OK")
            when (requestCode) {

                Constants.GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let{
                            launchImageCrop(uri)
                        }
                    }?: showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri: ${resultUri}")
                    viewModel.setNewStoreFields(
                        name = null,
                        description = null,
                        address = null,
                        category = null,
                        newImageUri = resultUri
                    )
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.d(TAG, "CROP: ERROR")
                    showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                com.sucho.placepicker.Constants.PLACE_PICKER_REQUEST ->{
                    val addressData = data?.getParcelableExtra<AddressData>(com.sucho.placepicker.Constants.ADDRESS_INTENT)
                    var address:Address?=null
                    addressData?.let {
                        it.addressList?.let {
                            address=it.first()
                        }
                    }

                    address?.let {

                        val storeAddress=StoreAddress(
                            it.featureName,
                            it.adminArea,
                            it.subAdminArea,
                            it.locality,
                            it.thoroughfare,
                            it.postalCode,
                            it.countryCode,
                            it.countryName,
                            addressData!!.latitude,
                            addressData!!.longitude,
                            it.getAddressLine(0)
                        )

                        viewModel.setNewStoreFields(
                            name = null,
                            description = null,
                            address = storeAddress,
                            category = null,
                            newImageUri = null
                        )

                        Log.d("ii",storeAddress.toString())
                    }





                }
            }
        }
    }
    private fun launchImageCrop(uri: Uri){
        context?.let{
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    fun showErrorDialog(errorMessage: String){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setCategoryStore(CategoryStore())
        viewModel.setStoreFields(
            StoreFields(
                input_name.text.toString(),
                input_description.text.toString(),
                null
            )
        )
    }

}