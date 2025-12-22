package com.example.footballmanagement.entity.enums;

public enum UserRole {
    USER,
    ADMIN_SYSTEM,
    ADMIN_BRANCH,
    PENDING_ADMIN_BRANCH,
}

//Note: Sau này sửa phần login. Khi chọn ROLE là admin_branch thì khi bấm register sẽ lưu dưới database là PENDING_ADMIN_BRANCH.
//Sau đó thằng admin_system sẽ sửa cái role là admin_branch mới được xài. 