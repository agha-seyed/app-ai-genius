package com.example.presentation.util

import java.io.IOException
import retrofit2.HttpException

fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is IOException -> "عدم اتصال به اینترنت. لطفاً اتصال شبکه خود را بررسی کنید."
        is HttpException -> {
            when (this.code()) {
                401, 403 -> "خطای احراز هویت. لطفاً کلید API را بررسی کنید."
                404 -> "سرویس مورد نظر یافت نشد."
                408 -> "پایان مهلت زمانی اتصال (Timeout)."
                429 -> "تعداد درخواست‌ها بیش از حد مجاز است. لطفاً کمی صبر کنید."
                in 500..599 -> "خطای سرور. لطفاً بعداً دوباره تلاش کنید."
                else -> "خطای ارتباط با سرور (کد: ${this.code()})"
            }
        }
        // Specific Generative AI errors can be mapped here if known (e.g. from google generative ai SDK)
        else -> this.localizedMessage ?: "خطای ناشناخته رخ داده است. لطفاً دوباره تلاش کنید."
    }
}
