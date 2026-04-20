/* ============================================================
   KHỞI TẠO
============================================================ */
let currentPitch = null;

document.addEventListener("DOMContentLoaded", () => {
    if (pitchId) {
        loadPitchDetail();
    }

    const backBtn = document.getElementById("backBtn");
    if (backBtn) {
        backBtn.textContent = i18n.back;
    }

    bindImageUploadEvents();
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
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            }
        });

        if (!res.ok) {
            container.innerHTML = `<p class="error">${i18n.error}</p>`;
            return;
        }

        const p = await res.json();
        currentPitch = p;

        const cover =
            p.images?.find(i => i.cover)?.url ||
            p.images?.find(i => i.isCover)?.url ||
            p.images?.[0]?.url ||
            "/images/no-image.png";

        container.innerHTML = `
            <div class="pitch-detail-card">
                <img src="${cover}" class="pitch-detail-img">

                <div class="pitch-detail-info">
                    <h3>${p.name}</h3>

                    <p><strong>${i18n.location}</strong> ${p.location ?? ""}</p>
                    <p><strong>${i18n.description}</strong> ${p.description ?? ""}</p>
                    <p><strong>${i18n.branch}</strong> ${p.branchName ?? ""}</p>
                    <p><strong>${i18n.type}</strong> ${p.pitchTypeName ?? ""}</p>

                    <p>
                        <strong>${i18n.status}</strong>
                        <span style="color:${p.active ? "#10b981" : "#6b7280"}; font-weight:600;">
                            ${p.active ? i18n.active : i18n.inactive}
                        </span>
                    </p>

                    <div class="pitch-action-buttons">
                        <button class="booking-btn edit-btn" id="openEditBtn">
                            ${i18n.edit}
                        </button>

                        <button class="booking-btn edit-price-btn" id="openEditPriceBtn">
                            ${i18n.editPrice}
                        </button>
                    </div>
                </div>
            </div>
        `;

        const openEditBtn = document.getElementById("openEditBtn");
        if (openEditBtn) {
            openEditBtn.onclick = () => openEditModal();
        }

        const openEditPriceBtn = document.getElementById("openEditPriceBtn");
        if (openEditPriceBtn) {
            openEditPriceBtn.onclick = () => {
                window.location.href = `/adminsystem/pitches/${pitchId}/edit-price`;
            };
        }

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

    if (!images.length) {
        gallery.innerHTML = `<p class="gallery-empty">${i18n.imageNoFile}</p>`;
        return;
    }

    gallery.innerHTML = images.map(img => {
        const isCover = img.cover === true || img.isCover === true;

        return `
            <div class="gallery-item ${isCover ? "is-cover" : ""}">
                <img src="${img.url}" alt="pitch-image">

                <div class="gallery-overlay">
                    ${isCover ? `<span class="cover-badge">${i18n.imageCover}</span>` : ""}
                    <button
                        type="button"
                        class="gallery-delete-btn"
                        data-image-id="${img.id}"
                        title="${i18n.imageDelete}">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </div>
            </div>
        `;
    }).join("");

    const prev = document.getElementById("galleryPrev");
    const next = document.getElementById("galleryNext");

    if (prev) {
        prev.onclick = () => gallery.scrollBy({ left: -300, behavior: "smooth" });
    }

    if (next) {
        next.onclick = () => gallery.scrollBy({ left: 300, behavior: "smooth" });
    }

    gallery.querySelectorAll(".gallery-delete-btn").forEach(btn => {
        btn.addEventListener("click", async () => {
            const imageId = btn.dataset.imageId;
            await deletePitchImage(imageId);
        });
    });
}


/* ============================================================
   3. UPLOAD ẢNH
============================================================ */
function bindImageUploadEvents() {
    const input = document.getElementById("pitchImageInput");
    const fileName = document.getElementById("pitchImageFilename");
    const uploadBtn = document.getElementById("uploadPitchImagesBtn");

    if (input) {
        input.addEventListener("change", () => {
            const files = Array.from(input.files || []);
            if (!files.length) {
                fileName.textContent = i18n.imageNoFile;
                return;
            }

            fileName.textContent = files.map(f => f.name).join(", ");
        });
    }

    if (uploadBtn) {
        uploadBtn.addEventListener("click", uploadPitchImages);
    }
}

async function uploadPitchImages() {
    const input = document.getElementById("pitchImageInput");
    const files = Array.from(input?.files || []);

    if (!files.length) {
        alert(i18n.imageUploadEmpty);
        return;
    }

    const formData = new FormData();
    files.forEach(file => formData.append("files", file));

    try {
        const res = await fetch(`/api/adminsystem/pitches/${pitchId}/images`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            },
            body: formData
        });

        if (!res.ok) {
            const errorText = await res.text();
            alert(errorText || i18n.imageUploadFail);
            return;
        }

        alert(i18n.imageUploadSuccess);

        input.value = "";
        document.getElementById("pitchImageFilename").textContent = i18n.imageNoFile;

        await loadPitchDetail();

    } catch (err) {
        console.error(err);
        alert(i18n.imageUploadFail);
    }
}


/* ============================================================
   4. DELETE ẢNH
============================================================ */
async function deletePitchImage(imageId) {
    if (!imageId) return;

    const confirmed = confirm(i18n.imageDeleteConfirm);
    if (!confirmed) return;

    try {
        const res = await fetch(`/api/adminsystem/images/${imageId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            }
        });

        if (!res.ok) {
            const errorText = await res.text();
            alert(errorText || i18n.imageDeleteFail);
            return;
        }

        alert(i18n.imageDeleteSuccess);
        await loadPitchDetail();

    } catch (err) {
        console.error(err);
        alert(i18n.imageDeleteFail);
    }
}


/* ============================================================
   5. OPEN EDIT MODAL
============================================================ */
function openEditModal() {
    if (!currentPitch) return;

    document.getElementById("editName").value = currentPitch.name ?? "";
    document.getElementById("editLocation").value = currentPitch.location ?? "";
    document.getElementById("editDescription").value = currentPitch.description ?? "";
    document.getElementById("editPitchType").value = currentPitch.pitchTypeId ?? "";
    document.getElementById("editActive").value = currentPitch.active ? "true" : "false";

    new bootstrap.Modal(document.getElementById("editPitchModal")).show();
}


/* ============================================================
   6. SAVE UPDATE
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

        const modalEl = document.getElementById("editPitchModal");
        const modalInstance = bootstrap.Modal.getInstance(modalEl);
        if (modalInstance) {
            modalInstance.hide();
        }

        loadPitchDetail();

    } catch (err) {
        console.error(err);
        alert(i18n.error);
    }
});