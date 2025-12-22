async function loadPitchDetail() {
    const container = document.getElementById("pitchDetailContainer");

    // Hiển thị trạng thái loading
    container.innerHTML = `<p>${i18n.loading}</p>`;

    try {
        // 1. Lấy thông tin sân
        const res = await fetch(`/api/pitches/${pitchId}`);
        if (!res.ok) {
            container.innerHTML = `<p>${i18n.error}</p>`;
            return;
        }
        const p = await res.json();

        // 2. Lấy ảnh cover
        const coverRes = await fetch(`/api/pitches/${pitchId}/images/cover`);
        let coverUrl = "/images/default-cover.jpg"; // fallback nếu không có cover
        if (coverRes.ok) {
            const cover = await coverRes.json();
            if (cover && cover.url) {
                coverUrl = cover.url;
            }
        }

        // 3. Render thông tin + nút booking
        container.innerHTML = `
            <div class="pitch-detail-card">
                <img src="${coverUrl}" alt="Pitch Cover" class="pitch-detail-img">

                <div class="pitch-detail-info">
                    <h3>${p.name}</h3>
                    <p><strong>${i18n.location}</strong> ${p.location}</p>
                    <p><strong>${i18n.description}</strong> ${p.description || '-'}</p>
                    <p><strong>${i18n.branch}</strong> ${p.branchName}</p>
                    <p><strong>${i18n.type}</strong> ${p.pitchTypeName}</p>
                    <p><strong>${i18n.status}</strong> ${p.active ? i18n.active : i18n.inactive}</p>
                    <a href="#" id="bookingBtn" class="booking-btn">${i18n.booking}</a>
                </div>
            </div>
        `;

        // 4. Gallery
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

            // Scroll với nút prev/next
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

        // 5. Average rating
        const avgRes = await fetch(`/api/pitches/${pitchId}/reviews/average`);
        if (avgRes.ok) {
            const avg = await avgRes.json();

            // render sao cho avg rating
            const stars = Array.from({ length: 5 }, (_, i) => {
                return `<span class="star ${i < Math.round(avg) ? "filled" : ""}">★</span>`;
            }).join("");

            document.getElementById("avgRating").innerHTML = `${avg} ${stars}`;
        }

        // 6. Reviews
        const reviewsRes = await fetch(`/api/pitches/${pitchId}/reviews`);
        if (reviewsRes.ok) {
            const reviews = await reviewsRes.json();
            const reviewContainer = document.getElementById("reviewsContainer");
            reviewContainer.innerHTML = "";
            if (reviews.length === 0) {
                reviewContainer.innerHTML = `<p>Chưa có đánh giá nào</p>`;
            } else {
                reviews.forEach(r => {
                    const card = document.createElement("div");
                    card.classList.add("review-card");

                    // render số sao đúng rating
                    const stars = Array.from({ length: 5 }, (_, i) => {
                        return `<span class="star ${i < r.rating ? "filled" : ""}">★</span>`;
                    }).join("");

                    card.innerHTML = `
                        <strong>${r.userFullName}</strong><br>
                        ${stars}
                        <p>${r.content || ""}</p>
                    `;
                    reviewContainer.appendChild(card);
                });
            }
        }

    } catch (err) {
        console.error("Error:", err);
        container.innerHTML = `<p>${i18n.fetchFailed}</p>`;
    }
}

// ✅ helper: đọc payload của JWT
function decodeJwt(token) {
  try { return JSON.parse(atob(token.split('.')[1])); } catch { return null; }
}

// ✅ check khi click nút booking
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

  // ✅ 👉 CHỈ redirect sang trang booking để Thymeleaf render pitchId
  window.location.href = `/user/booking/${pitchId}`;
});

// ✅ Gọi khi load trang
document.addEventListener("DOMContentLoaded", loadPitchDetail);
