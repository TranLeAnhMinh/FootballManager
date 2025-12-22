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
                    <div>
                        <h3 class="branch-title">${b.name}</h3>
                        <p class="branch-location">
                            <i class="fa-solid fa-location-dot"></i> ${b.location}
                        </p>
                    </div>

                    <button class="btn-add-pitch" onclick="openPitchModal('${b.id}', '${b.name}')">
                         ${`+ ${i18n.addPitch}`}
                    </button>
                </div>

                <p class="branch-description">${b.description || ""}</p>

                <div class="admin-pitch-list">
                    ${b.pitches.map(p => `
                        <div class="admin-pitch-card" onclick="goToPitch('${p.id}')">
                            <div class="pitch-content">
                                <h4>${p.name}</h4>
                                <p><i class="fa-solid fa-map"></i> ${p.location}</p>
                                <p>${p.description}</p>

                                <p>
                                    ${p.active
                                        ? `<span class="status-tag active">${i18n.statusActive}</span>`
                                        : `<span class="status-tag inactive">${i18n.statusInactive}</span>`}
                                </p>
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


document.getElementById("createPitchForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const form = e.target;
    const fd = new FormData();

    // 🔹 Tạo JSON pitch
    const pitchJson = {
        branchId: form.branchId.value,
        name: form.name.value,
        location: form.location.value,
        description: form.description.value,
        pitchTypeId: form.pitchTypeId.value
    };

    // 🔹 ĐÚNG FORMAT BACKEND CẦN → "pitch": "{json}"
    fd.append("pitch", JSON.stringify(pitchJson));

    // 🔹 File ảnh
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
