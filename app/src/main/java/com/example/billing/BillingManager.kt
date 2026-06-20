package com.example.billing

import android.app.Activity
import android.content.Context
import com.example.data.billing.BillingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    private val billingRepository: BillingRepository
) {
    fun initiatePurchaseFlow(activity: Activity) {
        // Mock purchase flow
        // In a real app, integrate Google Play Billing Library here
        billingRepository.upgradeToPremium()
    }
}
