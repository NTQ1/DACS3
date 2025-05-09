package com.yourname.dacs.model

data class GiaoDich(
    val id: String = "",
    val soTien: Double = 0.0,
    val ghiChu: String = "",
    val danhMucId: String = "",
    val accountId: String = "",
    val thoiGian: String = "",
    val loai: String = "" // "thu" hoặc "chi"
)
