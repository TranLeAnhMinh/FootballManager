document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("resetForm");
    if (!form) return; // an toàn nếu file bị import nhầm sang trang khác

    form.addEventListener("submit", async function(e) {
        e.preventDefault();
        const token = document.getElementById("token").value;
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (newPassword !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        try {
            const res = await fetch(`/api/auth/recover/confirm?token=${encodeURIComponent(token)}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ newPassword })
            });

            if (res.ok) {
                alert("Password reset successfully! Please login with your new password.");
                window.location.href = "/login";
            } else {
                alert("Token expired or invalid. Please request a new recovery link.");
            }
        } catch (err) {
            console.error(err);
            alert("Request failed, please try again.");
        }
    });
});
