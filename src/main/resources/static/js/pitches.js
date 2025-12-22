async function loadBranches(pitchTypeId) {
    const container = document.getElementById("branchContainer");
    container.innerHTML = `<p>${i18n.loading}</p>`; // ✅ i18n

    try {
        const res = await fetch("/api/pitches/by-type", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ pitchTypeId })
        });

        if (!res.ok) {
            container.innerHTML = `<p>${i18n.error}</p>`; // ✅ i18n
            return;
        }

        const data = await res.json();

        let html = "";
        data.branches.forEach((branch, index) => {
            html += `
                <div class="branch-section">
                    <div class="branch-header" onclick="toggleBranch(${index})">
                        <h3>${i18n.pitchBranch} ${branch.name}</h3>
                        <button class="toggle-btn" id="toggle-${index}">${i18n.collapse}</button>
                    </div>
                    <div class="branch-pitches" id="branch-pitches-${index}">
                        ${branch.pitches.map(p => `
                            <div class="pitch-item" onclick="goToPitchDetail('${p.id}')">
                                ${p.name}
                            </div>
                        `).join("")}
                    </div>
                </div>
            `;
        });

        container.innerHTML = html;

    } catch (err) {
        console.error("Error:", err);
        container.innerHTML = `<p>${i18n.fetchFailed}</p>`; // ✅ i18n
    }
}

// Toggle branch list
function toggleBranch(index) {
    const pitchList = document.getElementById(`branch-pitches-${index}`);
    const btn = document.getElementById(`toggle-${index}`);

    if (pitchList.classList.contains("collapsed")) {
        pitchList.classList.remove("collapsed");
        btn.textContent = i18n.collapse; // ✅ dùng i18n
    } else {
        pitchList.classList.add("collapsed");
        btn.textContent = i18n.expand;   // ✅ dùng i18n
    }
}

// ✅ thêm hàm redirect sang trang chi tiết sân
function goToPitchDetail(pitchId) {
    window.location.href = `/user/pitch/${pitchId}`;
}
