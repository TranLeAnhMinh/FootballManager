/* ============================================================================
   ADMIN JS — QUẢN LÝ BRANCH + PITCH
   Tất cả gom chung 1 file, gọn rõ ràng.
============================================================================ */


/* ============================================================================
   1) LOAD PITCHES THEO PITCH TYPE
============================================================================ */

async function loadAdminPitches(pitchTypeId) {
  const container = document.getElementById("pitchContainer");
  container.innerHTML = `<p class="loading-text">${i18n.loading}</p>`;

  const token = localStorage.getItem("accessToken");
  if (!token) {
    container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
    return;
  }

  try {
    const res = await fetch("/api/admin/pitches", {
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    });

    if (!res.ok) {
      container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
      return;
    }

    const data = await res.json();
    const selectedType = data.find(t => t.id === pitchTypeId);

    if (!selectedType || !selectedType.pitches || selectedType.pitches.length === 0) {
      container.innerHTML = `<p class="empty-text">${i18n.empty}</p>`;
      return;
    }

    let html = `
      <h3 class="pitch-type-title">${selectedType.name}</h3>
      <div class="admin-pitch-list">
        ${selectedType.pitches.map(p => {

          // Ảnh cover
          const coverImg = p.images?.find(img => img.cover === true);
          const coverUrl = coverImg?.url ?? "/images/default_pitch.jpg";

          return `
            <div class="admin-pitch-card" onclick="goToMaintenance('${p.id}')">

              <div class="pitch-image">
                <img src="${coverUrl}" alt="${p.name}">
              </div>

              <div class="pitch-content">
                <h4>${p.name}</h4>

                <p class="location">
                  <i class="fa-solid fa-location-dot"></i> ${p.location}
                </p>

                <p class="description">${p.description}</p>

                <p class="status">
                  ${p.active
                    ? `<span class="status-tag active"><i class="fa-solid fa-circle-check"></i> ${i18n.statusActive}</span>`
                    : `<span class="status-tag inactive"><i class="fa-solid fa-circle-xmark"></i> ${i18n.statusInactive}</span>`
                  }
                </p>

                <div class="rating">
                  <i class="fa-solid fa-futbol"></i>
                  <span>${p.averageRating?.toFixed(1) ?? "0.0"} / 5</span>
                </div>
              </div>

            </div>
          `;
        }).join("")}
      </div>
    `;

    container.innerHTML = html;

  } catch (err) {
    console.error("Error:", err);
    container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
  }
}



/* ============================================================================
   2) LOAD BRANCHES
============================================================================ */

async function loadAdminBranches() {
  const container = document.getElementById("branchContainer");
  container.innerHTML = `<p class="loading-text">${i18n.loading}</p>`;

  const token = localStorage.getItem("accessToken");
  if (!token) {
    container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
    return;
  }

  try {
    const res = await fetch("/api/admin/branches", {
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

    if (!branches || branches.length === 0) {
      container.innerHTML = `<p class="empty-text">${i18n.empty}</p>`;
      return;
    }

    let html = `
      <h3 class="branch-title">${i18n.branchTitle}</h3>
      <div class="admin-branch-list">
        ${branches.map(b => `
          <div class="admin-branch-card" onclick="goToBranchDetail('${b.id}')">

            <h4>${b.name}</h4>

            <p class="location">
              <i class="fa-solid fa-location-dot"></i> ${b.location}
            </p>

            <p class="description">${b.description || ""}</p>

          </div>
        `).join("")}
      </div>
    `;

    container.innerHTML = html;

  } catch (err) {
    console.error("Error:", err);
    container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
  }
}



/* ============================================================================
   3) CREATE NEW BRANCH
============================================================================ */

async function initCreateBranchForm() {
  const form = document.getElementById("branchForm");
  if (!form) return;

  form.addEventListener("submit", async function (e) {
    e.preventDefault();

    const name = document.getElementById("branchName").value.trim();
    const location = document.getElementById("branchLocation").value.trim();
    const description = document.getElementById("branchDescription").value.trim();
    const messageBox = document.getElementById("branchMessage");

    const token = localStorage.getItem("accessToken");

    if (!token) {
      showError(messageBox, i18n.error);
      return;
    }

    const payload = { name, location, description };

    try {
      const res = await fetch("/api/admin/branches", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        showError(messageBox, i18n.error);
        return;
      }

      const newBranch = await res.json();

      showSuccess(messageBox, i18n.createSuccess);

      setTimeout(() => {
        window.location.href = "/admin/branches";
      }, 1200);

    } catch (err) {
      console.error(err);
      showError(messageBox, i18n.error);
    }
  });
}



/* ============================================================================
   4) LOAD BRANCH DROPDOWN (dùng khi tạo pitch)
============================================================================ */

async function loadBranchDropdown() {
  const dropdown = document.getElementById("branchSelect");
  if (!dropdown) return;

  const token = localStorage.getItem("accessToken");
  if (!token) return;

  const res = await fetch("/api/admin/branches", {
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    }
  });

  const branches = await res.json();

  dropdown.innerHTML = branches
    .map(b => `<option value="${b.id}">${b.name} - ${b.location}</option>`)
    .join("");
}



/* ============================================================================
   HELPERS
============================================================================ */

function showError(el, text) {
  el.textContent = text;
  el.className = "error-text";
}

function showSuccess(el, text) {
  el.textContent = text;
  el.className = "success-text";
}


/* ============================================================================
   NAVIGATION
============================================================================ */

function goToMaintenance(pitchId) {
  window.location.href = `/admin/maintenance/${pitchId}`;
}

function goToBranchDetail(branchId) {
  window.location.href = `/admin/branches/${branchId}`;
}

