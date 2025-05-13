package com.yourname.dacs.model

data class TietKiem(
    val idGiaoDich: String = "",         // ID duy nhất cho giao dịch
    val ngayGio: String = "",            // Thời gian dạng chuỗi, ví dụ: "2025-05-12 14:30"
    val loaiGiaoDich: String = "",       // "thu" hoặc "chi"
    val ghiChu: String = "",             // Ghi chú mô tả giao dịch
    val soTien: Double = 0.0,            // Số tiền của giao dịch
    val idNguoiDung: String = "",        // ID của người thực hiện giao dịch
    val idHu: String = ""                // ID của hũ tiết kiệm
)