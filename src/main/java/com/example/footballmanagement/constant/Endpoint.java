package com.example.footballmanagement.constant;

public class Endpoint {
    // ================= BASE =================
    public static final String BASE = "http://localhost:8080";

    // ================= AUTH =================
    public static final String AUTH_LOGIN = "/api/auth/login";
    public static final String AUTH_REGISTER = "/api/auth/register";
    public static final String AUTH_REFRESH  = "/api/auth/refresh";
    public static final String AUTH_LOGOUT  = "/api/auth/logout";
    public static final String AUTH_RECOVERY  = "/api/auth/recover/**";
    public static final String AUTH_RECOVERY_CONFIRM = "/api/auth/recover/confirm";
    
    // ================= MVC PAGES =================
    public static final String LOGIN_PAGE = "/login";
    public static final String REGISTER_PAGE = "/register";
    public static final String FORGOTPASSWORD_PAGE = "/forgotpassword";
    public static final String RESETPASSWORD_PAGE = "/resetpassword";
    public static final String PITCH_DETAIL_PAGE = "/user/pitch/{id}";

    // ================= USERS =================
    public static final String USER_API_ENDPOINT = "/api/user/**"; // security matcher
    public static final String USER_API_BASE     = "/api/user";    // controller base
    // ================= USER MGMT (ADMIN SYSTEM) =================
    public static final String USER_ADMIN_SYSTEM_API_BASE = "/api/adminsystem/users";
    public static final String USER_ADMIN_SYSTEM_ENDPOINT = "/api/adminsystem/users/**";
    // ================= USER PAGES =================
    public static final String BOOKING_HISTORY_PAGE = "/user/booking-history";
    
    // ================= USER / ADMIN PAGES =================
    public static final String USER_ENDPOINT          = "/user/**";
    public static final String ADMIN_ENDPOINT         = "/admin/**";              
    public static final String ADMIN_SYSTEM_ENDPOINT  = "/adminsystem/**"; 

    // ================= PITCH =================
    public static final String PITCH_ENDPOINT       = "/api/pitches/**";          
    public static final String PITCH_API_BASE       = "/api/pitches";         
    // ================= PITCH (ADMIN BRANCH) =================
    public static final String PITCH_ADMIN_API_BASE = "/api/admin/pitches";     // controller base
    public static final String PITCH_ADMIN_ENDPOINT = "/api/admin/pitches/**";   
    // ================= PITCH (ADMIN SYSTEM) =================
    public static final String PITCH_ADMIN_SYSTEM_API_BASE = "/api/adminsystem/pitches"; // controller base
    public static final String PITCH_ADMIN_SYSTEM_ENDPOINT = "/api/adminsystem/pitches/**";

    // ================= BRANCH (ADMIN SYSTEM) =================
    public static final String BRANCH_ADMIN_SYSTEM_API_BASE = "/api/adminsystem/branches";
    public static final String BRANCH_ADMIN_SYSTEM_ENDPOINT = "/api/adminsystem/branches/**";


    // ================= MAINTENANCE =================
    public static final String MAINTENANCE_WINDOW_ENDPOINT = "/api/pitches/*/maintenance-windows/**";

    // ================= BOOKING =================
    public static final String BOOKING_SLOT_ENDPOINT  = "/api/pitches/*/booking-slots/**";
    public static final String BOOKING_ENDPOINT       = "/api/bookings/**";    // security matcher
    public static final String BOOKING_API_BASE       = "/api/bookings";       // controller base
    public static final String BOOKING_ADMIN_ENDPOINT = "/api/admin/bookings/**"; 
    public static final String BOOKING_BRANCH_ENDPOINT = "/api/bookings/branch/**";

    
    // ================= VOUCHERS =================
    public static final String VOUCHER_ENDPOINT       = "/api/vouchers/**";            
    public static final String VOUCHER_API_BASE       = "/api/vouchers";               
    public static final String VOUCHER_ADMIN_ENDPOINT = "/api/admin/vouchers/**"; 
    public static final String VOUCHER_ADMIN_SYSTEM_API_BASE = "/api/adminsystem/vouchers";
    public static final String VOUCHER_ADMIN_SYSTEM_ENDPOINT = "/api/adminsystem/vouchers/**";

    // ================= PAYMENT =================
    public static final String PAYMENT_ENDPOINT = "/api/payment/**";
    public static final String PAYMENT_RETURN   = "/api/payment/vnpay-return";

    // ================= REVENUE =================
    public static final String REVENUE_BRANCH_ENDPOINT = "/api/revenue/branch/**";  // API cho admin chi nhánh xem doanh thu
    public static final String REVENUE_ADMIN_SYSTEM_ENDPOINT = "/api/adminsystem/revenue/**";

}
