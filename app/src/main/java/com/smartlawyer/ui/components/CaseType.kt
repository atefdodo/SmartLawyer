package com.smartlawyer.ui.components

enum class CaseType(
    val value: String,
    val displayName: String
) {
    CIVIL_PARTIAL("مدني جزئي", "مدني جزئي"),
    CIVIL_FULL("مدني كلي", "مدني كلي"),
    LABOR("عمالي", "عمالي"),
    LABOR_APPEAL("استئناف عمالي", "استئناف عمالي"),
    MISDEMEANOR("جنحة", "جنحة"),
    FELONY("جناية", "جناية"),
    ECONOMIC("اقتصادي", "اقتصادي"),
    ADMINISTRATIVE("محكمة إدارية", "محكمة إدارية"),
    HOUSING("إسكان", "إسكان"),
    CASSATION("نقض", "نقض");

    companion object {
        fun fromValue(value: String): CaseType {
            return entries.find { it.value == value } ?: CIVIL_PARTIAL
        }
    }
}