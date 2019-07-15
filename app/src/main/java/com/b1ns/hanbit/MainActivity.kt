package com.b1ns.hanbit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.ValueCallback




class MainActivity : AppCompatActivity() {

    val TAG1 = "hellow web view"
    val webURL : String = "https://www.hanlight.kr"
    val PICKFILE_REQUEST_CODE = 100

    private var mUploadMessage: ValueCallback<Uri>? = null
    private val FILECHOOSER_RESULTCODE = 1


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = true
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true

        val settings = webView.settings
        settings.domStorageEnabled = true

        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webView.loadUrl(webURL)
        }



        webView.webChromeClient = object:WebChromeClient() {
            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams:FileChooserParams):Boolean {
                var mFilePathCallback = filePathCallback
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                startActivityForResult(intent, PICKFILE_REQUEST_CODE)
                return true
            }
        }


        fun onActivityResult(requestCode: Int, resultCode: Int,
                             intent: Intent,
                             mFilePathCallback: Any): Boolean {
            var PICKFILE_REQUEST_CODE = null
            if (requestCode == PICKFILE_REQUEST_CODE)
            {
                val result = if (resultCode != RESULT_OK)
                    null
                else
                    intent.data
                val resultsArray = arrayOfNulls<Uri>(1)
                resultsArray[0] = result
                mFilePathCallback.onReceiveValue(resultsArray)

            }
            return true
        }


        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val request = DownloadManager.Request(Uri.parse(url))
            request.allowScanningByMediaScanner()

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mimetype)
            val webview = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            webview.enqueue(request)
            Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()
        }


        class webviewclient : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                webView.loadUrl(webURL)
                return true
            }
        }
        webviewclient()



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            val result = if (intent == null || resultCode != Activity.RESULT_OK)
                null
            else
                intent.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
        Log.i(TAG1, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
        Log.i(TAG1, "onRestoreInstanceState")
    }

    private fun Any.onReceiveValue(resultsArray: Array<Uri?>) {}

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }



}
