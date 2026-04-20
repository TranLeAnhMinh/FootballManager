async function loadPitchDetail() {
    const container = document.getElementById("pitchDetailContainer");

    container.innerHTML = `<p>${i18n.loading}</p>`;

    try {
        const res = await fetch(`/api/pitches/${pitchId}`);
        if (!res.ok) {
            container.innerHTML = `<p>${i18n.error}</p>`;
            return;
        }
        const p = await res.json();

        const coverRes = await fetch(`/api/pitches/${pitchId}/images/cover`);
        let coverUrl = "/images/default-cover.jpg";
        if (coverRes.ok) {
            const cover = await coverRes.json();
            if (cover && cover.url) {
                coverUrl = cover.url;
            }
        }

        container.innerHTML = `
            <div class="pitch-detail-card">
                <img src="${coverUrl}" alt="Pitch Cover" class="pitch-detail-img">

                <div class="pitch-detail-info">
                    <h3>${p.name}</h3>
                    <p><strong>${i18n.location}</strong> ${p.location}</p>
                    <p><strong>${i18n.description}</strong> ${p.description || "-"}</p>
                    <p><strong>${i18n.branch}</strong> ${p.branchName}</p>
                    <p><strong>${i18n.type}</strong> ${p.pitchTypeName}</p>
                    <p><strong>${i18n.status}</strong> ${p.active ? i18n.active : i18n.inactive}</p>
                    <a href="#" id="bookingBtn" class="booking-btn">${i18n.booking}</a>
                </div>
            </div>
        `;

        const galleryRes = await fetch(`/api/pitches/${pitchId}/images/gallery`);
        if (galleryRes.ok) {
            const gallery = await galleryRes.json();
            const galleryContainer = document.getElementById("galleryImages");
            galleryContainer.innerHTML = "";

            gallery.forEach(img => {
                const el = document.createElement("img");
                el.src = img.url;
                galleryContainer.appendChild(el);
            });

            const prevBtn = document.getElementById("galleryPrev");
            const nextBtn = document.getElementById("galleryNext");
            const scrollAmount = 200;

            prevBtn.addEventListener("click", () => {
                galleryContainer.scrollBy({ left: -scrollAmount, behavior: "smooth" });
            });

            nextBtn.addEventListener("click", () => {
                galleryContainer.scrollBy({ left: scrollAmount, behavior: "smooth" });
            });
        }

        await loadAverageRating();
        await loadReviews();
        await setupReviewForm();

    } catch (err) {
        console.error("Error:", err);
        container.innerHTML = `<p>${i18n.fetchFailed}</p>`;
    }
}

async function loadAverageRating() {
    const avgRes = await fetch(`/api/pitches/${pitchId}/reviews/average`);
    if (!avgRes.ok) return;

    const avg = await avgRes.json();

    const stars = Array.from({ length: 5 }, (_, i) => {
        return `<span class="star ${i < Math.round(avg) ? "filled" : ""}">★</span>`;
    }).join("");

    document.getElementById("avgRating").innerHTML = `${avg} ${stars}`;
}

async function loadReviews() {
    const reviewsRes = await fetch(`/api/pitches/${pitchId}/reviews`);
    if (!reviewsRes.ok) return;

    const reviews = await reviewsRes.json();
    const reviewContainer = document.getElementById("reviewsContainer");
    reviewContainer.innerHTML = "";

    if (reviews.length === 0) {
        reviewContainer.innerHTML = `<p>${i18n.reviewNone}</p>`;
        return;
    }

    reviews.forEach(r => {
        const card = document.createElement("div");
        card.classList.add("review-card");

        const stars = Array.from({ length: 5 }, (_, i) => {
            return `<span class="star ${i < r.rating ? "filled" : ""}">★</span>`;
        }).join("");

        card.innerHTML = `
            <strong>${r.userFullName}</strong>
            <div class="review-stars">${stars}</div>
            <p>${r.content || ""}</p>
        `;

        reviewContainer.appendChild(card);
    });
}

function renderRatingStars(current = 5) {
    const container = document.getElementById("ratingStars");
    const ratingInput = document.getElementById("ratingValue");

    if (!container || !ratingInput) return;

    container.innerHTML = "";

    for (let i = 1; i <= 5; i++) {
        const star = document.createElement("span");
        star.classList.add("star");
        if (i <= current) {
            star.classList.add("filled");
        }
        star.textContent = "★";

        star.addEventListener("mouseenter", () => {
            paintRatingStars(i);
        });

        star.addEventListener("click", () => {
            ratingInput.value = i;
            paintRatingStars(i);
        });

        container.appendChild(star);
    }

    container.onmouseleave = () => {
        paintRatingStars(Number(ratingInput.value));
    };
}

function paintRatingStars(value) {
    const stars = document.querySelectorAll("#ratingStars .star");
    stars.forEach((star, index) => {
        if (index < value) {
            star.classList.add("filled");
        } else {
            star.classList.remove("filled");
        }
    });
}

async function setupReviewForm() {
    const reviewForm = document.getElementById("reviewForm");
    const reviewAuthMessage = document.getElementById("reviewAuthMessage");
    const ratingValue = document.getElementById("ratingValue");
    const reviewContent = document.getElementById("reviewContent");

    const token = localStorage.getItem("accessToken");
    if (!token) {
        reviewForm.style.display = "none";
        reviewAuthMessage.innerHTML = `<a href="/login">${i18n.reviewLoginRequired}</a>`;
        return;
    }

    const payload = decodeJwt(token);
    if (!payload || payload.role !== "USER") {
        reviewForm.style.display = "none";
        reviewAuthMessage.textContent = i18n.reviewUserOnly;
        return;
    }

    reviewForm.style.display = "block";
    reviewAuthMessage.innerHTML = "";

    ratingValue.value = 5;
    renderRatingStars(5);

    try {
        const reviewsRes = await fetch(`/api/pitches/${pitchId}/reviews`);
        if (reviewsRes.ok) {
            const reviews = await reviewsRes.json();
            const currentUserId = payload.userId || payload.id || payload.sub;

            const myReview = reviews.find(r => r.userId === currentUserId);
            if (myReview) {
                ratingValue.value = myReview.rating;
                reviewContent.value = myReview.content || "";
                renderRatingStars(myReview.rating);
            }
        }
    } catch (err) {
        console.error("Load my review error:", err);
    }

    reviewForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const rating = Number(ratingValue.value);
        const content = reviewContent.value.trim();

        try {
            const res = await fetch(`/api/pitches/${pitchId}/reviews`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    rating,
                    content
                })
            });

            if (!res.ok) {
                const errorText = await res.text();
                alert(errorText || i18n.reviewSubmitFailed);
                return;
            }

            await loadAverageRating();
            await loadReviews();
        } catch (err) {
            console.error("Submit review error:", err);
            alert(i18n.reviewSubmitError);
        }
    }, { once: true });
}

function decodeJwt(token) {
    try {
        return JSON.parse(atob(token.split(".")[1]));
    } catch {
        return null;
    }
}

document.addEventListener("click", async (e) => {
    const btn = e.target.closest("#bookingBtn");
    if (!btn) return;

    e.preventDefault();
    const token = localStorage.getItem("accessToken");
    if (!token) {
        alert("Bạn cần đăng nhập để đặt lịch");
        window.location.href = "/login";
        return;
    }

    const payload = decodeJwt(token);
    if (!payload || payload.role !== "USER") {
        alert("Bạn cần đăng nhập để đặt lịch");
        window.location.href = "/login";
        return;
    }

    window.location.href = `/user/booking/${pitchId}`;
});

document.addEventListener("DOMContentLoaded", loadPitchDetail);