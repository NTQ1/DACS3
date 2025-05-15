package com.yourname.dacs.model

data class GiaoDich(
    val id: String = "",
    val soTien: Double = 0.0,
    val danhMucId: String = "",
    val accountId: String = "",
    val thoiGian: String = "",
    val loai: String = "", // "thu" hoặc "chi"
    val tenDanhMuc: String = "",
    val iconDanhMuc: String = "",
    val mauSacDanhMuc: String = ""
)

