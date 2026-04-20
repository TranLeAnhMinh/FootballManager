/* ============================================================
   KHỞI TẠO
============================================================ */
let currentPitch = null;
let currentWeeklyGrid = null;
let currentEditingCell = null;

document.addEventListener("DOMContentLoaded", () => {
    if (pitchId) {
        loadPitchSummary();
        loadWeeklyGrid();
    }

    initBulkTimeOptions();

    const backBtn = document.getElementById("backBtn");
    if (backBtn) {
        backBtn.textContent = i18n.back;
        backBtn.href = `/adminsystem/pitches/${pitchId}`;
    }

    bindEditPriceModalEvents();
    bindBulkApplyEvents();
});


/* ============================================================
   1. LOAD TÓM TẮT THÔNG TIN SÂN
============================================================ */
async function loadPitchSummary() {
    const container = document.getElementById("pitchSummaryContainer");
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

        const pitch = await res.json();
        currentPitch = pitch;

        const cover =
            pitch.images?.find(img => img.cover)?.url ||
            pitch.images?.[0]?.url ||
            "/images/no-image.png";

        container.innerHTML = `
            <div class="pitch-summary-card">
                <img src="${cover}" alt="${pitch.name}" class="pitch-summary-img">

                <div class="pitch-summary-info">
                    <h3>${pitch.name}</h3>
                    <p><strong>${i18n.branch}</strong> ${pitch.branchName ?? ""}</p>
                    <p><strong>${i18n.location}</strong> ${pitch.location ?? ""}</p>
                    <p><strong>${i18n.type}</strong> ${pitch.pitchTypeName ?? ""}</p>
                    <p>
                        <strong>${i18n.status}</strong>
                        <span style="color:${pitch.active ? "#10b981" : "#6b7280"}; font-weight:600;">
                            ${pitch.active ? i18n.active : i18n.inactive}
                        </span>
                    </p>
                </div>
            </div>
        `;
    } catch (err) {
        console.error(err);
        container.innerHTML = `<p class="error">${i18n.error}</p>`;
    }
}


/* ============================================================
   2. LOAD WEEKLY GRID
============================================================ */
async function loadWeeklyGrid() {
    const container = document.getElementById("priceGridContainer");
    if (!container) return;

    container.innerHTML = `<p>${i18n.loading}</p>`;

    try {
        const res = await fetch(`/api/adminsystem/base-prices/pitches/${pitchId}/weekly-grid`, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            }
        });

        if (!res.ok) {
            container.innerHTML = `<p class="error">${i18n.error}</p>`;
            return;
        }

        const grid = await res.json();
        currentWeeklyGrid = grid;

        renderWeeklyGrid(grid);

    } catch (err) {
        console.error(err);
        container.innerHTML = `<p class="error">${i18n.error}</p>`;
    }
}


/* ============================================================
   3. RENDER WEEKLY GRID
============================================================ */
function renderWeeklyGrid(grid) {
    const container = document.getElementById("priceGridContainer");
    if (!container) return;

    if (!grid || !grid.rows || grid.rows.length === 0) {
        container.innerHTML = `<p>${i18n.noPriceData}</p>`;
        return;
    }

    const dayHeaders = [
        { value: 1, label: i18n.day1 },
        { value: 2, label: i18n.day2 },
        { value: 3, label: i18n.day3 },
        { value: 4, label: i18n.day4 },
        { value: 5, label: i18n.day5 },
        { value: 6, label: i18n.day6 },
        { value: 7, label: i18n.day7 }
    ];

    const headerHtml = `
        <thead>
            <tr>
                <th>${i18n.time}</th>
                ${dayHeaders.map(day => `<th>${day.label}</th>`).join("")}
            </tr>
        </thead>
    `;

    const bodyHtml = `
        <tbody>
            ${grid.rows.map(row => `
                <tr>
                    <td class="time-cell">
                        ${formatTime(row.timeStart)} - ${formatTime(row.timeEnd)}
                    </td>

                    ${row.cells.map(cell => {
                        const cellClasses = [
                            "price-cell",
                            cell.configured ? "configured" : "not-configured"
                        ].join(" ");

                        const displayPrice = cell.configured
                            ? formatPrice(cell.price)
                            : i18n.notConfigured;

                        const tooltip = `
${formatTime(row.timeStart)} - ${formatTime(row.timeEnd)}
${i18n.priceTooltip}: ${cell.configured ? formatPrice(cell.price) + " VND" : i18n.notConfigured}
`;

                        return `
                            <td 
                                class="${cellClasses}"
                                data-day-of-week="${cell.dayOfWeek}"
                                data-time-start="${row.timeStart}"
                                data-time-end="${row.timeEnd}"
                                data-price="${cell.price ?? ""}"
                                data-configured="${cell.configured}"
                                title="${tooltip.trim()}"
                            >
                                ${displayPrice}
                            </td>
                        `;
                    }).join("")}
                </tr>
            `).join("")}
        </tbody>
    `;

    container.innerHTML = `
        <div class="price-grid-wrapper">
            <table class="price-grid-table">
                ${headerHtml}
                ${bodyHtml}
            </table>
        </div>
    `;

    bindPriceCellEvents();
}


/* ============================================================
   4. BIND CELL EVENTS
============================================================ */
function bindPriceCellEvents() {
    const cells = document.querySelectorAll(".price-cell");

    cells.forEach(cell => {
        cell.addEventListener("click", () => {
            const dayOfWeek = Number(cell.dataset.dayOfWeek);
            const timeStart = cell.dataset.timeStart;
            const timeEnd = cell.dataset.timeEnd;
            const price = cell.dataset.price ? Number(cell.dataset.price) : null;
            const configured = cell.dataset.configured === "true";

            openEditPriceModal({
                pitchId,
                dayOfWeek,
                timeStart,
                timeEnd,
                price,
                configured
            });
        });
    });
}


/* ============================================================
   5. OPEN EDIT PRICE MODAL
============================================================ */
function openEditPriceModal(cellData) {
    currentEditingCell = cellData;

    const dayLabel = getDayLabel(cellData.dayOfWeek);

    const dayEl = document.getElementById("editPriceDay");
    const timeEl = document.getElementById("editPriceTime");
    const valueEl = document.getElementById("editPriceValue");
    const stateEl = document.getElementById("editPriceState");

    if (dayEl) dayEl.textContent = dayLabel;
    if (timeEl) timeEl.textContent = `${formatTime(cellData.timeStart)} - ${formatTime(cellData.timeEnd)}`;
    if (valueEl) valueEl.value = cellData.price ?? "";
    if (stateEl) stateEl.textContent = cellData.configured ? i18n.configured : i18n.notConfigured;

    const modalEl = document.getElementById("editPriceModal");
    if (!modalEl) return;

    new bootstrap.Modal(modalEl).show();
}


/* ============================================================
   6. BIND MODAL EVENTS - EDIT ONE CELL
============================================================ */
function bindEditPriceModalEvents() {
    const saveBtn = document.getElementById("savePriceBtn");
    if (!saveBtn) return;

    saveBtn.addEventListener("click", async () => {
        if (!currentEditingCell) return;

        const priceInput = document.getElementById("editPriceValue");
        const newPrice = Number(priceInput?.value);

        if (!newPrice || newPrice <= 0) {
            alert(i18n.invalidPrice);
            return;
        }

        const payload = {
            pitchId: currentEditingCell.pitchId,
            dayOfWeek: currentEditingCell.dayOfWeek,
            timeStart: currentEditingCell.timeStart,
            timeEnd: currentEditingCell.timeEnd,
            price: newPrice
        };

        try {
            const res = await fetch(`/api/adminsystem/base-prices/cell`, {
                method: "PATCH",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errorText = await safeReadError(res);
                alert(errorText || i18n.updateFailed);
                return;
            }

            const result = await res.json();
            console.log("Update base price success:", result);

            const modalEl = document.getElementById("editPriceModal");
            const modalInstance = bootstrap.Modal.getInstance(modalEl);
            if (modalInstance) {
                modalInstance.hide();
            }

            currentEditingCell = null;
            await loadWeeklyGrid();

        } catch (err) {
            console.error(err);
            alert(i18n.updateError);
        }
    });
}


/* ============================================================
   7. BIND MODAL EVENTS - BULK APPLY
============================================================ */
function bindBulkApplyEvents() {
    const saveBulkBtn = document.getElementById("saveBulkApplyBtn");
    if (!saveBulkBtn) return;

    saveBulkBtn.addEventListener("click", async () => {
        const checkedDays = Array.from(document.querySelectorAll(".bulk-day-checkbox:checked"))
            .map(input => Number(input.value));

        const startTime = document.getElementById("bulkStartTime")?.value;
        const endTime = document.getElementById("bulkEndTime")?.value;
        const price = Number(document.getElementById("bulkPriceValue")?.value);

        if (!checkedDays.length) {
            alert(i18n.bulkApplyInvalidDays);
            return;
        }

        if (!startTime || !endTime || startTime === endTime) {
            alert(i18n.bulkApplyInvalidTime);
            return;
        }

        if (!price || price <= 0) {
            alert(i18n.bulkApplyInvalidPrice);
            return;
        }

        const payload = {
            pitchIds: [pitchId],
            dayOfWeeks: checkedDays,
            startTime: normalizeBulkTime(startTime),
            endTime: normalizeBulkTime(endTime),
            price: price
        };

        try {
            const res = await fetch(`/api/adminsystem/base-prices/apply-template`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("accessToken")}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errorText = await safeReadError(res);
                alert(errorText || i18n.bulkApplyFailed);
                return;
            }

            const result = await res.json();
            console.log("Bulk apply success:", result);

            const modalEl = document.getElementById("bulkApplyModal");
            const modalInstance = bootstrap.Modal.getInstance(modalEl);
            if (modalInstance) {
                modalInstance.hide();
            }

            resetBulkApplyForm();
            await loadWeeklyGrid();

        } catch (err) {
            console.error(err);
            alert(i18n.bulkApplyError);
        }
    });
}


/* ============================================================
   8. INIT BULK TIME OPTIONS
============================================================ */
function initBulkTimeOptions() {
    const startSelect = document.getElementById("bulkStartTime");
    const endSelect = document.getElementById("bulkEndTime");

    if (!startSelect || !endSelect) return;

    const times = [
        "00:00", "00:45", "01:30", "02:15", "03:00", "03:45",
        "04:30", "05:15", "06:00", "06:45", "07:30", "08:15",
        "09:00", "09:45", "10:30", "11:15", "12:00", "12:45",
        "13:30", "14:15", "15:00", "15:45", "16:30", "17:15",
        "18:00", "18:45", "19:30", "20:15", "21:00", "21:45",
        "22:30", "23:15"
    ];

    startSelect.innerHTML = `
        <option value="">-- ${i18n.startTime ?? "Start time"} --</option>
        ${times.map(time => `<option value="${time}">${time}</option>`).join("")}
    `;

    endSelect.innerHTML = `
        <option value="">-- ${i18n.endTime ?? "End time"} --</option>
        <option value="00:00">00:00</option>
        ${times.map(time => `<option value="${time}">${time}</option>`).join("")}
    `;
}


/* ============================================================
   9. RESET BULK APPLY FORM
============================================================ */
function resetBulkApplyForm() {
    document.querySelectorAll(".bulk-day-checkbox").forEach(input => {
        input.checked = false;
    });

    const startEl = document.getElementById("bulkStartTime");
    const endEl = document.getElementById("bulkEndTime");
    const priceEl = document.getElementById("bulkPriceValue");

    if (startEl) startEl.value = "";
    if (endEl) endEl.value = "";
    if (priceEl) priceEl.value = "";
}


/* ============================================================
   10. HELPERS
============================================================ */
function formatTime(timeStr) {
    if (!timeStr) return "";
    return timeStr.substring(0, 5);
}

function formatPrice(price) {
    if (price === null || price === undefined || price === "") return "";
    return Number(price).toLocaleString("vi-VN");
}

function getDayLabel(dayOfWeek) {
    switch (dayOfWeek) {
        case 1: return i18n.day1;
        case 2: return i18n.day2;
        case 3: return i18n.day3;
        case 4: return i18n.day4;
        case 5: return i18n.day5;
        case 6: return i18n.day6;
        case 7: return i18n.day7;
        default: return "";
    }
}

function normalizeBulkTime(timeStr) {
    if (!timeStr) return "";
    return timeStr.length === 5 ? `${timeStr}:00` : timeStr;
}

async function safeReadError(res) {
    try {
        const data = await res.json();
        return data.message || data.error || null;
    } catch {
        return null;
    }
}