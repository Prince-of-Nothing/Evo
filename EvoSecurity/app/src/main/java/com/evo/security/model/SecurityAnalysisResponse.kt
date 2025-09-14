package com.evo.security.model

data class SecurityAnalysisResponse(
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
    val harmless: Int,
    val timeout: Int,
    val confirmed_timeout: Int,
    val failure: Int,
    val type_unsupported: Int
) {
    fun isSafe(): Boolean {
        return malicious == 0 && suspicious == 0
    }

    fun getThreatLevel(): ThreatLevel {
        return when {
            malicious > 0 -> ThreatLevel.HIGH
            suspicious > 0 -> ThreatLevel.MEDIUM
            failure > 0 || timeout > 0 -> ThreatLevel.UNKNOWN
            else -> ThreatLevel.SAFE
        }
    }
}

enum class ThreatLevel {
    SAFE, MEDIUM, HIGH, UNKNOWN
}