package com.meet.project.oneclickshop.ui.fragments

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.meet.project.oneclickshop.R
import com.meet.project.oneclickshop.databinding.ComposeFragmentBinding
import com.meet.project.oneclickshop.utils.BaseFragment
import com.meet.project.oneclickshop.utils.getInjector
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale


class AddProductFragment : BaseFragment() {

    private val injector by lazy { getInjector(requireContext()) }
    private lateinit var binding: ComposeFragmentBinding
    private val navController: NavController by lazy { findNavController() }
    private val viewModel: ProductViewModel by activityViewModels { injector.provideProductViewModel() }

    private lateinit var pickImageTask: ActivityResultLauncher<String?>
    private val localActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                viewModel.showImage = true
            } else {
                viewModel.showImage = false
                Toast.makeText(context, "Please enable storage permission to upload and Image.", Toast.LENGTH_SHORT).show()
            }
        }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { permission -> viewModel.showImage = permission == true }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.compose_fragment, container, false)
        initData()
        binding.composeView.setContent {
            AddProductScreen(
                viewModel = viewModel,
                onAddProductClick = ::onAddProductClick,
                addImageClick = ::addImageClick
            )
        }
        initImageTasks()
        return binding.root
    }

    private fun initData() {
        if (!allPermissionsGranted()) {
            localActivityResultLauncher.launch(REQUIRED_PERMISSIONS)
        }

        viewModel.errorMessageForAddProduct = ""
        viewModel.addProductLoading = false
        viewModel.errorScreenForAddProduct = false
        viewModel.productAdded = false
        viewModel.imageAdded = false
        viewModel.productName = ""
        viewModel.price = ""
        viewModel.taxes = "0"
        viewModel.addImageLoading = false
        viewModel.expanded = false
        viewModel.image = null
        viewModel.showImage = true
        if (viewModel.productTypeList.isNotEmpty()) {
            viewModel.selectedType = viewModel.productTypeList[0]
        } else {
            viewModel.selectedType = ""
        }
    }

    private fun onAddProductClick() {
        if (viewModel.productName.isEmpty() || viewModel.selectedType.isEmpty() || viewModel.price.isEmpty() || viewModel.taxes.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.productAdded = false
            viewModel.addProductLoading = true
            viewModel.addProduct(
                onSuccess = {
                    viewModel.addProductResponse = it
                    viewModel.productAdded = true
                    viewModel.errorScreenForAddProduct = false
                    viewModel.addProductLoading = false
                },
                onFailure = {
                    viewModel.errorScreenForAddProduct = true
                    viewModel.errorMessageForAddProduct = it
                    viewModel.addProductLoading = false
                }
            )
        }
    }

    private fun addImageClick() {
        pickImageTask.launch("image/*")
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initImageTasks() {
        pickImageTask = getPickContentTask(onContentPicked = { uri ->
            context?.contentResolver?.openInputStream(uri)?.let { inputStream ->
                viewModel.addImageLoading = true
                val targetFile = createFile(
                    baseFolder = getOutputDirectory(requireContext()),
                    extension = uri.path?.getFileExtension() ?: IMAGE_EXTENSION
                )
                copyInputStreamToFile(file = targetFile, inputStream = inputStream)
                viewModel.imageAdded = true
                viewModel.image = targetFile
                viewModel.addImageLoading = false
            } ?: run {
                Log.e("initVideoTasks", "Something Went Wrong")
            }
        })
    }

    private fun copyInputStreamToFile(file: File, inputStream: InputStream) {
        try {
            if (checkManifestPermission()) {
                file.outputStream().use { fileOut ->
                    val bytesCopied = inputStream.copyTo(fileOut)
                    Log.d("Filee", "bytesCopied - $bytesCopied")
                }
            } else {
                requestManifestPermission(onPermissionGranted = {
                    file.outputStream().use { fileOut ->
                        val bytesCopied = inputStream.copyTo(fileOut)
                        Log.d("Filee", "bytesCopied - $bytesCopied")
                    }
                }, onPermissionRejected = {
                    Log.e("copyInputStreamToFile", "Storage permission is required")
                })
            }
        } catch (e: Exception) {
            Log.e("initVideoTasks", e.message.toString())
        }
    }

    private fun checkManifestPermission(perm: String = WRITE_EXTERNAL_STORAGE): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(), perm
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestManifestPermission(
        onPermissionGranted: () -> Unit,
        onPermissionRejected: () -> Unit,
        perm: String = WRITE_EXTERNAL_STORAGE
    ) {
        activityResultLauncher.launch(perm)
    }

    private fun getOutputDirectory(context: Context, dirName: String = "one_click_shop_new"): File {
        val appContext = context.applicationContext
        return appContext.cacheDir
    }

    private fun createFile(
        baseFolder: File,
        extension: String
    ) = File(
        baseFolder, SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis() + 1) + extension
    )

    private fun getPickContentTask(onContentPicked: (Uri) -> Unit): ActivityResultLauncher<String?> {
        return registerForActivityResult(CustomPickImage()) {
            it?.let {
                onContentPicked(it)
            } ?: run {
                Toast.makeText(context, "Image not picked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun String.getFileExtension(): String? {
        return if (this.lastIndexOf('.') < 0) {
            null
        } else {
            this.substring(this.lastIndexOf('.'))
        }
    }

    class CustomPickImage : ActivityResultContracts.GetContent() {
        override fun createIntent(context: Context, input: String): Intent {
            super.createIntent(context, input)
            return if (Build.VERSION.SDK_INT <= 24) {
                val i = Intent()
                i.type = "image/*"
                i.action = Intent.ACTION_GET_CONTENT
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.putExtra("crop", "true")
                i.putExtra("aspectX", 1)
                i.putExtra("aspectY", 1)
                i.putExtra("outputX", 96)
                i.putExtra("outputY", 96)
                i.putExtra("noFaceDetection", true)
                i.putExtra("return-data", true)
            } else {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    .putExtra("crop", "true")
                    .putExtra("aspectX", 1)
                    .putExtra("aspectY", 1)
                    .putExtra("outputX", 96)
                    .putExtra("outputY", 96)
                    .putExtra("noFaceDetection", true)
                    .putExtra("return-data", true)
                    .apply { type = "image/*" }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        navController.navigateUp()
        return true
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy_MM_dd_HH_mm_ss"
        private const val IMAGE_EXTENSION = ".jpg"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.INTERNET,
            ).apply {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    add(WRITE_EXTERNAL_STORAGE)
                    add(READ_EXTERNAL_STORAGE)
                } else {
                    add(READ_MEDIA_IMAGES)
                }
            }.toTypedArray()
    }
}