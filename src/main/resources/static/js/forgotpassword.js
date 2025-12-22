document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("forgotForm");
    if (!form) return; // an toàn nếu trang khác cũng import file này

    form.addEventListener("submit", async function(e) {
        e.preventDefault();
        const email = document.getElementById("email").value;

        try {
            const res = await fetch(`/api/auth/recover?email=${encodeURIComponent(email)}`, {
                method: "POST"
            });

            if (res.ok) {
                alert("Recovery email sent! Please check your inbox.");
                window.location.href = "/login";
            } else {
                alert("Email not found or error occurred.");
            }
        } catch (err) {
            console.error(err);
            alert("Request failed, please try again.");
        }
    });
});
