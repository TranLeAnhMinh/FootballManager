document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    if (!form) return;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try {
            const res = await fetch("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });

            if (res.ok) {
                const data = await res.json();

                // Lưu token vào localStorage
                localStorage.setItem("accessToken", data.accessToken);
                localStorage.setItem("refreshToken", data.refreshToken);
                localStorage.setItem("userRole", data.role);

                // Redirect theo role
                switch (data.role) {
                    case "USER":
                        window.location.href = "/user/home";
                        break;
                    case "ADMIN_BRANCH":
                        window.location.href = "/admin/home";
                        break;
                    case "ADMIN_SYSTEM":
                        window.location.href = "/adminsystem/home";
                        break;
                    default:
                        alert("Unauthorized role");
                }
            } else {
                alert("Invalid email or password");
            }
        } catch (err) {
            console.error(err);
            alert("Login failed, please try again");
        }
    });
});


