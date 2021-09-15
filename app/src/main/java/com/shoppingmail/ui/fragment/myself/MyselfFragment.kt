package com.shoppingmail.ui.fragment.myself

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.EnvironmentCompat
import androidx.fragment.app.FragmentActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide.init
import com.shoppingmail.*
import com.shoppingmail.logic.`class`.FileUtils
import com.shoppingmail.ui.fragment.cart.CartViewModel
import kotlinx.android.synthetic.main.dialog_custom_layout.*
import kotlinx.android.synthetic.main.dialog_custom_layout.view.*
import kotlinx.android.synthetic.main.fragment_myself.view.*
import java.io.File

class MyselfFragment : Fragment() {

    //需要上传的图片的Uri
    private var imageUri: Uri? = null
    //需要上传的图片的文件
    private var file: File? = null
    private var image_head: ImageView? = null
//    private val isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private var dialog:Dialog? = null

    //这一步待修改
//    companion object {
//        fun newInstance() = MyselfFragment()
//    }
//    private lateinit var myselfViewModel: MyselfViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        myselfViewModel =
//            ViewModelProvider(this).get(MyselfViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_myself, container, false)
//        val textView : TextView = root.findViewById(R.id.text_myself)
//        myselfViewModel.text.observe(viewLifecycleOwner, {
//            textView.text = it
//        })

        //加载当前用户名
        val prefs: SharedPreferences? = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)
        root.text_name.text = prefs?.getString("present_user_nickname", "")

        //如果没有登录就隐藏按钮
        if (root.text_name.text.toString() != ""){
            root.myself_operator.visibility = root.visibility
        }

        //  加载头像
//        val image_head : ImageView = root.findViewById(R.id.image_head)
        image_head = root.findViewById(R.id.image_head)


        //用户已经登录
        if (prefs?.getString("present_user_nickname", "") != ""){
            image_head?.load(R.drawable.take_photo) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }

        //点击头像按钮
        root.image_head.setOnClickListener {
            if (prefs?.getString("present_user_nickname", "") == ""){
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }else{
                //这一步实现上传头像的功能，有相册与拍照两个功能
                activity?.let { it1 -> showBottomDialog(it1) }
            }
        }

        //地址按钮
        root.myself_address.setOnClickListener {
            val intent = Intent(activity, AddressActivity::class.java)
            startActivity(intent)
        }

        //待收货按钮
        root.myself_wait_receive_goods.setOnClickListener {
            val intent = Intent(activity, OrderActivity::class.java)
            startActivity(intent)
        }

        //切换账号
        root.myself_switch_account_or_exit.setOnClickListener {
            val editor = prefs?.edit()
            editor?.clear()
            editor?.apply()
            val intent = Intent(activity, RecommendActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
//            Process.killProcess(Process.myPid())
//            activity?.finish()
        }


        root.text_name.setOnClickListener {
            if (prefs?.getString("present_user_nickname", "") == ""){
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        return root
//        return inflater.inflate(R.layout.fragment_myself, container, false)
    }

    //弹出框
    private fun showBottomDialog(activity: Activity) {

        //1、使用Dialog、设置style
//        val dialog = Dialog(activity, R.style.DialogTheme)
        dialog = Dialog(activity, R.style.DialogTheme)
        //2、设置布局
        val view: View = View.inflate(activity, R.layout.dialog_custom_layout, null)
        dialog?.setContentView(view)
//        val window: Window = dialog.getWindow()
        val window = dialog?.window
        //设置弹出位置
        window?.setGravity(Gravity.BOTTOM)
        //设置弹出动画
//        window?.setWindowAnimations(android.R.style.main_menu_animStyle)
        window?.setWindowAnimations(R.style.main_menu_animStyle)
        //设置对话框大小
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.show()
        init(activity)


//        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(object : OnClickListener() {
//            fun onClick(view: View?) {
//                dialog.dismiss()
//            }
//        })

        //跳转到相机拍照
//        dialog?.tv_take_photo?.setOnClickListener(this)
//        dialog?.tv_take_pic?.setOnClickListener(this)
//        dialog?.tv_cancel?.setOnClickListener(this)

//        //获取相册的图片
//        dialog.tv_take_pic.setOnClickListener {
//
//        }
    }

    private fun init(activity: Activity){
        dialog?.tv_take_photo?.setOnClickListener(View.OnClickListener {
            //相机
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)) {
                //权限都齐的情况下，跳转相机
                openCamera(activity)
            } else {
                if (activity != null) {
                    //请求权限
                    ActivityCompat.requestPermissions(activity, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), PHOTO_REQUEST_CAMERA)
                }
            }
        })
        dialog?.tv_take_pic?.setOnClickListener(View.OnClickListener {
            //相册
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)) {
                //权限都齐的情况下，跳转相册
                openAlbum(activity)
            } else {
                if (activity != null) {
                    //请求权限
                    ActivityCompat.requestPermissions(activity, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), PHOTO_REQUEST_ALBUM)
                }
            }
        })

        dialog?.tv_cancel?.setOnClickListener {
            dialog?.dismiss()
        }
    }

    /**
     * 跳转相机
     */
    private fun openCamera(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //判断是否有相机
        if (activity != null && activity != null && intent.resolveActivity(activity.packageManager) != null) {
            val file: File?
            var uri: Uri? = null
//            if (isAndroidQ) {
//                //适配Android10
//                uri = createImageUri(context)
//            } else {
            //Android10以下
            file = createImageFile(activity)
            if (file != null) {
                //Android10以下
                uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //适配Android7.0文件权限
                    FileProvider.getUriForFile(activity, "com.shoppingmail", file)
                } else {
                    Uri.fromFile(file)
                }
//                }
            }
            imageUri = uri
            Log.e(TAG, "相机保存的图片Uri：$imageUri")
            if (uri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }
    }

    /**
     * Android10以下创建图片file，用来保存拍照后的照片
     * @return file
     */
    //这一步待修改
    private fun createImageFile(activity: Activity): File? {
        val file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (file != null && !file.exists()) {
            if (file.mkdir()) {
                Log.e(TAG, "文件夹创建成功")
            } else {
                Log.e(TAG, "file为空或者文件夹创建失败")
            }
        }
        val tempFile = File(file, SAVE_AVATAR_NAME)
        Log.e(TAG, "临时文件路径：" + tempFile.absolutePath)
        return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(tempFile)) {
            null
        } else tempFile
    }


    /**
     * 跳转相册
     */
    private fun openAlbum(activity: Activity) {
        //这里需要找相册中的url
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, ALBUM_REQUEST_CODE)
    }

    /**
     * 跳转裁剪
     */
    private fun openCrop(uri: Uri?) {
//        println("可以跳到openCrop这一行")
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED && activity != null) {
            file = File(activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/0"), SAVE_AVATAR_NAME)
            Log.e(TAG, "裁剪图片存放路径：" + file!!.absolutePath)
        }
        val intent = Intent("com.android.camera.action.CROP")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        intent.setDataAndType(uri, "image/*")
        // 设置裁剪
        intent.putExtra("crop", "true")

        // aspectX aspectY 是宽高的比例
        var valx = 3
        var valy = 4
        intent.putExtra("aspectX", valx)
        intent.putExtra("aspectY", valy)
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 80)
        intent.putExtra("outputY", 80)
        //适配Android10，存放图片路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        // 图片格式
        intent.putExtra("outputFormat", "PNG")
        intent.putExtra("noFaceDetection", true) // 取消人脸识别
        intent.putExtra("return-data", true) // true:不返回uri，false：返回uri
        startActivityForResult(intent, TAILOR_REQUEST_CODE)
    }



    //权限的申请，这里调用到相机与相册的权限
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PHOTO_REQUEST_CAMERA ->                 //相机权限请求回调
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        //跳转相机
                        activity?.let { openCamera(it) }
                    } else {
                        //无权限提示
                        Toast.makeText(context, "权限未通过", Toast.LENGTH_SHORT).show()
                    }
                }
            PHOTO_REQUEST_ALBUM -> if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //跳转相册
                    activity?.let { openAlbum(it) }
                } else {
                    //无权限提示
                    Toast.makeText(context, "权限未通过", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //相册剪辑
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == -1) {
            //回调成功
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    //相机回调
                    Log.e(TAG, "相机回调")

                    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                        //照片裁剪
                        openCrop(imageUri)
                    } else {
                        Toast.makeText(activity, "未找到存储卡", Toast.LENGTH_SHORT).show()
                    }
                }
                ALBUM_REQUEST_CODE -> {
                    //相册回调
                    var uri:Uri? = null
                    Log.e(TAG, "相册回调")
                    if (data != null && data.data != null) {
                        image_head!!.setImageURI(data.data)
                        //如果需要上传操作的可以使用这个方法
                        val act: FragmentActivity? = activity
                        val file: File? = FileUtils.uriToFile(data.data, act!!)
                        if (file != null) {
                            //Android10以下
                            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            println("这里可以执行")
                            //适配Android7.0文件权限
                            FileProvider.getUriForFile(act, "com.shoppingmail", file)
                            } else {
                                Uri.fromFile(file)
                            }
                        }
                        //这里的file就是需要上传的图片了
                        if (uri != null){
                            openCrop(uri)
                        }
                    }
                }
                TAILOR_REQUEST_CODE -> {
                    //图片剪裁回调
                    Log.e(TAG, "图片剪裁回调")
                    //                    Glide.with(context).load(file).into(image);
                    val uri = Uri.fromFile(file)
                    print("400行中和uri" + uri.toString() + "\n" + file.toString())
                    image_head!!.setImageURI(uri)
                    //如果需要上传全局的这个file就是需要上传的图片了
//                    val file = file
                }
            }
        } else {
            Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PHOTO_REQUEST_CAMERA = 10 //相机权限请求
        private const val PHOTO_REQUEST_ALBUM = 20 //相册权限请求
        private const val CAMERA_REQUEST_CODE = 100 //相机跳转code
        private const val ALBUM_REQUEST_CODE = 200 //相册跳转code
        private const val TAILOR_REQUEST_CODE = 300 //图片剪裁code
        private const val SAVE_AVATAR_NAME = "head.png" //需要上传的图片的文件名
        private const val CLOSE_DIALOG = 400
    }
}