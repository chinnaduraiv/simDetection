package com.example.sim_detection

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterFragmentActivity

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** SimDetectionPlugin */
class SimDetectionPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "sime_detection_plugin")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "activeSubscriptionInfoList" -> {
                val results = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    getSIMModules()
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP_MR1")
                } // Whatever single sim / double sim
                result.success(results)
            }
            "SMS" -> {
                call.argument<String>("selectedSimSlotName")?.let { name ->
                    call.argument<String>("selectedSimSlotNumber")?.let { selectedSimSlotNumber ->
                        call.argument<String>("toMobileNumber")?.let { toMobileNumber ->
                            call.argument<String>("descriptionText")?.let { descriptionText ->
                                sendSMS(
                                    "+919585313659",
                                    name,
                                    selectedSimSlotNumber.toInt(),
                                    toMobileNumber,
                                    descriptionText
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("MissingPermission")
    private fun getSIMModules(): ArrayList<HashMap<String, String>> {
        val localSubscriptionManager =
            context.getSystemService(FlutterFragmentActivity.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val mapValues = ArrayList<HashMap<String, String>>()
        val subscriptionInfo: List<SubscriptionInfo> =
            localSubscriptionManager.activeSubscriptionInfoList
        for (element in subscriptionInfo) {
            val meMap = HashMap<String, String>()
            meMap["displayName"] = element.displayName.toString()
            meMap["simSlotIndex"] = element.simSlotIndex.toString()
            meMap["carrierName"] = element.carrierName.toString()
            meMap["iccId"] = element.iccId.toString()
            meMap["subscriptionId"] = element.subscriptionId.toString()
            if ("No service" != element.carrierName.toString())
                mapValues.add(meMap)
        }
        return mapValues
    }

    @SuppressLint("MissingPermission", "NewApi", "WrongConstant", "UnspecifiedImmutableFlag")
    fun sendSMS(
        phoneNumber: String,
        message: String,
        simSlot: Int,
        toMobileNumber: String,
        descriptionText: String
    ) {
        val sent = "SMS_SENT"
        val delivered = "SMS_DELIVERED"
        val sendPendingIntent: PendingIntent
        val deliveryPendingIntent: PendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sendPendingIntent =
                PendingIntent.getBroadcast(context, 1, Intent(sent), PendingIntent.FLAG_IMMUTABLE)
            deliveryPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    2,
                    Intent(delivered),
                    PendingIntent.FLAG_IMMUTABLE
                )
        } else {
            sendPendingIntent =
                PendingIntent.getBroadcast(context, 1, Intent(sent), PendingIntent.FLAG_ONE_SHOT)
            deliveryPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    2,
                    Intent(delivered),
                    PendingIntent.FLAG_ONE_SHOT
                )
        }


        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        context, "SMS sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                        context, "Generic failure",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                        context, "No service",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                        context, "Null PDU",
                        Toast.LENGTH_SHORT
                    ).show()
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                        context, "Radio off",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(sent))

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        context, "SMS delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                    Activity.RESULT_CANCELED -> Toast.makeText(
                        context, "SMS not delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, IntentFilter(delivered))
        val subscriptionManager = context.getSystemService(SubscriptionManager::class.java)
        val subscriptionInfo =
            subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simSlot)
        SmsManager.getSmsManagerForSubscriptionId(subscriptionInfo.subscriptionId)
            .sendTextMessage(
                toMobileNumber,
                null,
                descriptionText,
                sendPendingIntent,
                deliveryPendingIntent
            )
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
