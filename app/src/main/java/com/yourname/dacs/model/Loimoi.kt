package com.yourname.dacs.model

data class Loimoi(
    val idLoiMoi: String? = null,
    val idNguoiGui: String = "",
    val idNguoiNhan: String = "",
    val idHuChung: String = "",
    val tenHu: String = "", // 👈 Thêm tên hũ ở đây
    val trangThai: String = "cho" // "cho", "dongy", "tuchoi"
)
