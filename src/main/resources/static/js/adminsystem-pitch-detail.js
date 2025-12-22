/* ============================================================
   KHỞI TẠO
============================================================ */
let currentPitch = null;

document.addEventListener("DOMContentLoaded", () => {
    if (pitchId) {
        loadPitchDetail();
    }
});


/* ============================================================
   1. LOAD CHI TIẾT SÂN
============================================================ */
async function loadPitchDetail() {
    const container = document.getElementById("pitchDetailContainer");
    if (!container) return;

    container.innerHTML = `<p>${i18n.loading}</p>`;

    try {
        const res = await fetch(`/api/adminsystem/pitches/${pitchId}`, {
            headers: { "Authorization": `Bearer ${localStorage.getItem("accessToken")}` }
        });

        if (!res.ok) {
            container.innerHTML = `<p class="error">${i18n.error}</p>`;
            return;
        }

        const p = await res.json();
        currentPitch = p; // LƯU LẠI PITCH CHO MODAL

        const cover =
            p.images?.find(i => i.cover)?.url ||
            p.images?.[0]?.url ||
            "/images/no-image.png";

        container.innerHTML = `
            <div class="pitch-detail-card">
                <img src="${cover}" class="pitch-detail-img">

                <div class="pitch-detail-info">
                    <h3>${p.name}</h3>

                    <p><strong>${i18n.location}</strong> ${p.location}</p>
                    <p><strong>${i18n.description}</strong> ${p.description}</p>
                    <p><strong>${i18n.branch}</strong> ${p.branchName}</p>
                    <p><strong>${i18n.type}</strong> ${p.pitchTypeName}</p>

                    <p><strong>${i18n.status}</strong>
                        <span style="color:${p.active ? "#10b981" : "#6b7280"}; font-weight:600;">
                            ${p.active ? i18n.active : i18n.inactive}
                        </span>
                    </p>

                    <button class="booking-btn edit-btn" id="openEditBtn">
                        ${i18n.edit}
                    </button>
                </div>
            </div>
        `;

        document.getElementById("openEditBtn").onclick = () => openEditModal();

        loadGallery(p.images || []);

    } catch (err) {
        console.error(err);
        container.innerHTML = `<p class="error">${i18n.error}</p>`;
    }
}


/* ============================================================
   2. GALLERY
============================================================ */
function loadGallery(images) {
    const gallery = document.getElementById("galleryImages");
    if (!gallery) return;

    gallery.innerHTML = images.map(img => `<img src="${img.url}" alt="">`).join("");

    const prev = document.getElementById("galleryPrev");
    const next = document.getElementById("galleryNext");

    if (prev) prev.onclick = () => gallery.scrollBy({ left: -300, behavior: "smooth" });
    if (next) next.onclick = () => gallery.scrollBy({ left: 300, behavior: "smooth" });
}


/* ============================================================
   3. OPEN EDIT MODAL
============================================================ */
function openEditModal() {
    if (!currentPitch) return;

    document.getElementById("editName").value = currentPitch.name;
    document.getElementById("editLocation").value = currentPitch.location;
    document.getElementById("editDescription").value = currentPitch.description;

    // Gán loại sân 5/7/11 AUTO MATCH
    document.getElementById("editPitchType").value = currentPitch.pitchTypeId;

    // Gán trạng thái
    document.getElementById("editActive").value = currentPitch.active ? "true" : "false";

    new bootstrap.Modal(document.getElementById("editPitchModal")).show();
}


/* ============================================================
   4. SAVE UPDATE
============================================================ */
document.getElementById("savePitchBtn")?.addEventListener("click", async () => {
    const payload = {
        name: document.getElementById("editName").value,
        location: document.getElementById("editLocation").value,
        description: document.getElementById("editDescription").value,
        pitchTypeId: document.getElementById("editPitchType").value,
        active: document.getElementById("editActive").value === "true"
    };

    try {
        const res = await fetch(`/api/adminsystem/pitches/${pitchId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            alert(i18n.error);
            return;
        }

        bootstrap.Modal.getInstance(document.getElementById("editPitchModal")).hide();
        loadPitchDetail();  // refresh UI

    } catch (err) {
        console.error(err);
        alert(i18n.error);
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const backBtn = document.getElementById("backBtn");
    if (backBtn) backBtn.textContent = i18n.back;
});