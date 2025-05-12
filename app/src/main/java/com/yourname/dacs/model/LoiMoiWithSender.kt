package com.yourname.dacs.model

data class LoiMoiWithSender(
    val loimoi: Loimoi,
    val tenNguoiGui: String? = null
    // hoặc có thể là tên thuộc tính khác như:
    // val nguoiGui: NguoiDung? = null
    // val senderInfo: String? = null
)
