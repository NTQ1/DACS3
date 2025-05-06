package com.yourname.dacs.model

data class DanhMuc(
    var id: String = "",
    var ten: String = "",
    var loai: String = "",
    var icon: String = "",
    var mauSac: String = "",
    val accountId: String = "" // <-- thêm dòng này
)
