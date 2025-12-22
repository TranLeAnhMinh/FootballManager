// Hàm lấy token từ localStorage
function getAccessToken() {
    return localStorage.getItem("accessToken");
}

// Hàm logout client (clear localStorage + redirect)
function logoutClient() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userId");
    window.location.href = "/login";
}

// Middleware fetch API (tự động gắn token)
async function authFetch(url, options = {}) {
    const token = getAccessToken();
    if (!token) {
        logoutClient();
        return;
    }

    options.headers = {
        ...(options.headers || {}),
        "Authorization": "Bearer " + token
    };

    try {
        const res = await fetch(url, options);
        if (res.status === 401 || res.status === 403) {
            logoutClient();
        }
        return res;
    } catch (err) {
        console.error("Request failed:", err);
        logoutClient();
    }
}

// Hàm logout gọi API rồi clear token
async function logout() {
    try {
        const token = getAccessToken();
        if (token) {
            await fetch("/api/auth/logout", {
                method: "POST",
                headers: { "Authorization": "Bearer " + token }
            });
        }
    } catch (err) {
        console.error("Logout error:", err);
    } finally {
        logoutClient();
    }
}

// Kiểm tra token khi load trang
(function checkAuthOnLoad() {
    const token = getAccessToken();
    if (!token) {
        logoutClient();
    }
})();


