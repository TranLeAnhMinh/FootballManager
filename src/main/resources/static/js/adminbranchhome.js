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

    if (!selectedType) {
      container.innerHTML = `<p class="empty-text">${i18n.empty}</p>`;
      return;
    }

   let html = `
  <h3 class="pitch-type-title">${selectedType.name}</h3>
  <div class="admin-pitch-list">
    ${selectedType.pitches.map(p => `
      <div class="admin-pitch-card" onclick="goToMaintenance('${p.id}')">
        <div class="pitch-image">
          <img src="${p.coverImageUrl}" alt="${p.name}">
        </div>
        <div class="pitch-content">
          <h4>${p.name}</h4>
          <p class="location"><i class="fa-solid fa-location-dot"></i> ${p.location}</p>
          <p class="description">${p.description}</p>
          <p class="status">
            ${p.active
              ? `<span class="status-tag active"><i class="fa-solid fa-circle-check"></i> ${i18n.statusActive}</span>`
              : `<span class="status-tag inactive"><i class="fa-solid fa-circle-xmark"></i> ${i18n.statusInactive}</span>`}
          </p>
          <div class="rating">
            <i class="fa-solid fa-futbol"></i>
            <span>${p.averageRating.toFixed(1)} / 5</span>
          </div>
        </div>
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
function goToMaintenance(pitchId) {
  window.location.href = `/admin/maintenance/${pitchId}`;
}