// Lấy thông tin user khi load trang
async function loadUserInfo() {
    const token = localStorage.getItem("accessToken"); 
    if (!token) {
        alert("Bạn chưa đăng nhập");
        window.location.href = "/login";
        return;
    }
    try {
        const res = await fetch("/api/user/me", {
            headers: {
                "Authorization": "Bearer " + token 
            }
        });

        if (!res.ok) {
            throw new Error("Không lấy được thông tin user");
        }

        const user = await res.json();

        // Gán dữ liệu vào form
        document.getElementById("fullName").value = user.fullName;
        document.getElementById("email").innerText = user.email;
        document.getElementById("phone").value = user.phone || "";

    } catch (err) {
        alert(err.message);
    }
}

// Cập nhật thông tin user
async function updateUser(event) {
    event.preventDefault();

    const fullName = document.getElementById("fullName").value;
    const phone = document.getElementById("phone").value;
    const token = localStorage.getItem("accessToken"); 

    try {
        const res = await fetch("/api/user/me", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token 
            },
            body: JSON.stringify({ fullname: fullName, phone: phone })
        });

        if (!res.ok) {
            const errorData = await res.json();
            throw new Error(errorData.message || "Update failed");
        }

        await res.json();
        alert("Cập nhật thành công!");
    } catch (err) {
        alert("Error: " + err.message);
    }
}
