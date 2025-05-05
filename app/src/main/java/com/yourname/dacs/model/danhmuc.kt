package com.yourname.dacs.model

data class DanhMuc(
    var id: String = "",
    var ten: String = "",
    var loai: String = "",
    var icon: String = "",
    var mauSac: String = "",
    var userId: String = "" // ⚠️ Quan trọng: thêm userId
)
