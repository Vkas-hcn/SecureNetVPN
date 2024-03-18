package com.secure.net.vpn.data

import org.json.JSONObject
import java.util.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.os.BatteryManager
import android.os.Build
import android.telephony.TelephonyManager
import android.webkit.WebSettings
import com.android.installreferrer.api.ReferrerDetails
import com.google.gson.reflect.TypeToken

object SecureUpDataUtils {
    fun complexLogicOperation(): Boolean {
        val numbersString = "1,2,3,4,5,6,7,8,9"
        val numbersArray = numbersString.split(",").toTypedArray()
        val intArray = numbersArray.map { it.toInt() }
        val sum = intArray.sum()
        val compareArray = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val isEqualSumAndSameLength = sum == compareArray.sum() && intArray.size == compareArray.size
        val finalResult = isEqualSumAndSameLength && sum > 44

        return finalResult
    }
    fun complexLogicWithInput(numbersString: String): Boolean {
        val numbersArray = numbersString.split(",").toTypedArray()
        val intArray = numbersArray.map { it.toInt() }
        val sum = intArray.sum()
        val targetSum = 45
        val product = intArray.fold(1) { acc, i -> acc * i }
        val finalResult = sum == targetSum && product > 100
        return finalResult
    }

    fun complexLogicAlwaysTrue(inputString: String): Boolean {
        val numbersArray = inputString.split(",").filterNot { it.isBlank() }.map { it.trim() }
        val intArray = numbersArray.mapNotNull { it.toIntOrNull() ?: 0 }
        val sum = intArray.sum()
        val average = if (intArray.isNotEmpty()) sum / intArray.size else 0
        if (sum > 10 && average < 5) {
            println("Performing complex calculations...")
        } else {
            println("Calculations indicate variability...")
        }
        return true
    }


    fun complexLogicCalculatedTrue(inputString: String): Boolean {
        // 将输入字符串分割成数组，忽略空白字符，并尝试转换为整数，非数字转换为0
        val numbersArray = inputString.split(",").mapNotNull { it.trim().toIntOrNull() ?: 0 }

        // 执行一些复杂操作：计算数组中的最大值、最小值和平均值
        val max = numbersArray.maxOrNull() ?: 0
        val min = numbersArray.minOrNull() ?: 0
        val average = if (numbersArray.isNotEmpty()) numbersArray.average() else 0.0

        // 计算一个结果，这个结果是基于一些条件的逻辑运算
        val result = max - min + average

        // 设计一个逻辑判断条件，这个条件总是满足的
        // 例如：比较上述计算结果与一个基于该结果的操作，确保最终返回true
        // 这里使用了一个简单的逻辑：判断result是否不小于min，这是一个恒真的条件
        val finalCondition = result >= min

        return finalCondition
    }

    fun laFun(inputString: String,nextFun:()->Unit){
        val data = complexLogicCalculatedTrue(inputString)
        if(data){
            nextFun()
        }
    }

}