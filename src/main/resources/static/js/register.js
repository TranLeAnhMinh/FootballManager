document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registerForm");
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const body = {
            fullName: document.getElementById("fullName").value,
            email: document.getElementById("email").value,
            phone: document.getElementById("phone").value,
            role: document.getElementById("role").value
        };

        const res = await fetch("/api/auth/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(body)
        });

        if (res.ok) {
            alert("Success!");
            window.location.href = "/login";
        } else {
            const err = await res.json();
            alert(err.message);
        }
    });
});
