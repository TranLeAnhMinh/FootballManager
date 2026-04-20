async function loadAdminBranches() {
    const container = document.getElementById("branchContainer");
    container.innerHTML = `<p class="loading-text">${i18n.loading}</p>`;

    const token = localStorage.getItem("accessToken");
    if (!token) {
        container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
        return;
    }

    try {
        const res = await fetch("/api/adminsystem/branches", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!res.ok) {
            container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
            return;
        }

        const branches = await res.json();

        if (branches.length === 0) {
            container.innerHTML = `<p class="empty-text">${i18n.empty}</p>`;
            return;
        }

        let html = `
        <div class="admin-branch-list">
        ${branches.map(b => `
            <div class="admin-branch-card">

                <div class="branch-header-box">
                    <div class="branch-top-row">
                        <div class="branch-info">
                            <h3 class="branch-title">${escapeHtml(b.name)}</h3>
                            <p class="branch-location">
                                <i class="fa-solid fa-location-dot"></i> ${escapeHtml(b.location || "")}
                            </p>
                        </div>

                        <!-- THÊM MỚI: nút edit branch -->
                        <button
                            class="btn-edit-branch"
                            data-branch-id="${escapeHtml(b.id)}"
                            data-branch-name="${escapeHtmlAttr(b.name || "")}"
                            data-branch-location="${escapeHtmlAttr(b.location || "")}"
                            data-branch-description="${escapeHtmlAttr(b.description || "")}"
                            onclick="openEditBranchModal(this)"
                            type="button"
                        >
                            ${i18n.editBranch}
                        </button>
                    </div>

                    <button class="btn-add-pitch" onclick="openPitchModal('${b.id}', '${escapeHtml(b.name)}')">
                        ${`+ ${i18n.addPitch}`}
                    </button>
                </div>

                <p class="branch-description">${escapeHtml(b.description || "")}</p>

                <div class="admin-pitch-list">
                    ${(b.pitches || []).map(p => `
                        <div class="admin-pitch-card" onclick="goToPitch('${p.id}')">
                            <div class="pitch-content">

                                <div class="pitch-title-row">
                                    <h4>${escapeHtml(p.name)}</h4>

                                    ${p.priceConfigComplete
                                        ? ``
                                        : `
                                            <span class="price-warning-badge" title="${i18n.priceConfigMissing}">
                                                <i class="fa-solid fa-triangle-exclamation"></i>
                                            </span>
                                        `}
                                </div>

                                <p><i class="fa-solid fa-map"></i> ${escapeHtml(p.location || "")}</p>
                                <p>${escapeHtml(p.description || "")}</p>

                                <div class="pitch-bottom-row">
                                    <p>
                                        ${p.active
                                            ? `<span class="status-tag active">${i18n.statusActive}</span>`
                                            : `<span class="status-tag inactive">${i18n.statusInactive}</span>`}
                                    </p>

                                    ${p.priceConfigComplete
                                        ? ``
                                        : `<span class="price-missing-text">${i18n.priceConfigMissingShort}</span>`}
                                </div>
                            </div>
                        </div>
                    `).join("")}
                </div>

            </div>
        `).join("")}
        </div>`;

        container.innerHTML = html;

    } catch (err) {
        console.error(err);
        container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
    }
}

function goToPitch(id) {
    window.location.href = `/adminsystem/pitches/${id}`;
}

function openPitchModal(branchId, branchName) {
    document.getElementById("pitchBranchId").value = branchId;
    document.getElementById("pitchBranchName").innerText = branchName;
    new bootstrap.Modal(document.getElementById("createPitchModal")).show();
}

/* THÊM MỚI: mở modal edit an toàn bằng data-* */
function openEditBranchModal(button) {
    const branchId = button.dataset.branchId;
    const name = button.dataset.branchName;
    const location = button.dataset.branchLocation;
    const description = button.dataset.branchDescription;

    document.getElementById("editBranchId").value = branchId || "";
    document.getElementById("editBranchName").value = name || "";
    document.getElementById("editBranchLocation").value = location || "";
    document.getElementById("editBranchDescription").value = description || "";

    new bootstrap.Modal(document.getElementById("editBranchModal")).show();
}

/* THÊM MỚI: submit form edit branch */
const editBranchForm = document.getElementById("editBranchForm");
if (editBranchForm) {
    editBranchForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const branchId = document.getElementById("editBranchId").value;

        const payload = {
            name: document.getElementById("editBranchName").value.trim(),
            location: document.getElementById("editBranchLocation").value.trim(),
            description: document.getElementById("editBranchDescription").value.trim()
        };

        try {
            const res = await fetch(`/api/adminsystem/branches/${branchId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errData = await res.json().catch(() => null);
                alert(errData?.message || i18n.error);
                return;
            }

            bootstrap.Modal.getInstance(document.getElementById("editBranchModal")).hide();
            await loadAdminBranches();

        } catch (err) {
            console.error(err);
            alert(i18n.error);
        }
    });
}

document.getElementById("createPitchForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const form = e.target;
    const fd = new FormData();

    const pitchJson = {
        branchId: form.branchId.value,
        name: form.name.value,
        location: form.location.value,
        description: form.description.value,
        pitchTypeId: form.pitchTypeId.value
    };

    fd.append("pitch", JSON.stringify(pitchJson));

    const files = form.file.files;
    for (let f of files) {
        fd.append("file", f);
    }

    const res = await fetch("/api/adminsystem/pitches/upload-and-create", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
        },
        body: fd
    });

    if (!res.ok) {
        alert(i18n.error);
        return;
    }

    loadAdminBranches();
    bootstrap.Modal.getInstance(document.getElementById("createPitchModal")).hide();
});

/* SỬA: dùng i18n mới cho image */
document.getElementById("pitchFiles").addEventListener("change", function () {
    const label = document.getElementById("fileText");
    const info = document.getElementById("fileInfo");

    if (this.files.length === 0) {
        info.innerText = i18n.noFile;
    } else if (this.files.length === 1) {
        info.innerText = this.files[0].name;
    } else {
        info.innerText = `${this.files.length} files`;
    }

    label.innerText = i18n.chooseFile;
});

function escapeHtml(str) {
    if (!str) return "";
    return str
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

/* THÊM MỚI: escape cho data-* attribute */
function escapeHtmlAttr(str) {
    if (!str) return "";
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}