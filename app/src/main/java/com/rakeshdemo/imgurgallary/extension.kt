package com.rakeshdemo.imgurgallary

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.kaopiz.kprogresshud.KProgressHUD
import java.util.*


fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

// Date convert to Human Read format
fun Date.convertString(dateFormat: String): String? {
    val format = java.text.SimpleDateFormat(dateFormat, Locale.getDefault())
    return format.format(this)
}
// showToast
fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

//Progress Dialog
fun Context.initLoading(loadMsg: String = "Please wait"): KProgressHUD {
    return KProgressHUD.create(this)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel("Please wait")
        .setDetailsLabel("Downloading data")
        .setCancellable(true)
        .setAnimationSpeed(2)
        .setDimAmount(0.5f)
        .show();
}
//Check Internet Connection
fun isOnline(con: Context): Boolean {
    var result = false // Returns connection type. 0: none; 1: mobile data; 2: wifi
    val cm = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork )?.run {
                if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = true
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = true
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    result = true
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    result = true
                }
            }
        }
    }
    return result
}