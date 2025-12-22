let currentPage = 0;
let pageSize = 10;
let totalElements = 0;
let totalPages = 0;
let currentWindows = [];

// =================== Lấy filter ===================
function getFilterParams() {
  return {
    pitchName: document.getElementById("filterPitchName")?.value.trim() || "",
    startFrom: document.getElementById("filterStartDate")?.value || "",
    endTo: document.getElementById("filterEndDate")?.value || ""
  };
}

// =================== Chuyển local datetime sang OffsetDateTime (+07:00) ===================
function toOffsetDateTime(localValue) {
  if (!localValue) return "";
  try {
    // Nếu input dạng yyyy-MM-ddTHH:mm → thêm giây và timezone
    return localValue.length === 16
      ? localValue + ":00+07:00"
      : localValue + "+07:00";
  } catch {
    return "";
  }
}

// =================== Load danh sách ===================
async function loadMaintenanceHistory(page = 0) {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert(i18n.alert.notLoggedIn);
    window.location.href = "/login";
    return;
  }

  const filters = getFilterParams();

  const params = new URLSearchParams({
    page,
    size: pageSize,
    ...(filters.pitchName && { pitchName: filters.pitchName }),
    ...(filters.startFrom && { startFrom: toOffsetDateTime(filters.startFrom) }),
    ...(filters.endTo && { endTo: toOffsetDateTime(filters.endTo) })
  }).toString();

  try {
    const res = await fetch(`/api/admin/maintenance-windows/history?${params}`, {
      headers: { Authorization: "Bearer " + token }
    });
    if (!res.ok)
      throw new Error(i18n.error?.loadFail || "Không thể tải danh sách lịch bảo trì");

    const data = await res.json();
    currentWindows = data.content || [];
    currentPage = data.page ?? page;
    totalPages = data.totalPages ?? 0;
    totalElements = data.totalElements ?? currentWindows.length;

    renderMaintenanceRows(currentWindows);
    renderSummary();
    renderPagination(totalPages, currentPage);
  } catch (e) {
    alert(e.message);
  }
}

// =================== Render danh sách ===================
function renderMaintenanceRows(items) {
  const tbody = document.getElementById("maintenanceList");
  tbody.innerHTML = "";

  if (!items || items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#64748b;">${i18n.table.noData}</td></tr>`;
    return;
  }

  items.forEach((m, idx) => {
    const now = new Date();
    const startAt = new Date(m.startAt);
    const canDelete = startAt > now; // chỉ xoá khi ở tương lai
    const deleteBtn = canDelete
      ? `<button class="action-btn cancel" onclick="deleteMaintenance('${m.id}')">${i18n.action.delete}</button>`
      : "";

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${currentPage * pageSize + idx + 1}</td>
      <td>${m.pitchName || "-"}</td>
      <td>${m.pitchLocation || "-"}</td>
      <td>${formatDate(m.startAt)}</td>
      <td>${formatDate(m.endAt)}</td>
      <td>${m.reason || "-"}</td>
      <td>${deleteBtn}</td>
    `;
    tbody.appendChild(tr);
  });
}

// =================== Xoá lịch bảo trì ===================
async function deleteMaintenance(id) {
  if (!confirm(i18n.confirm.delete)) return;

  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert(i18n.alert.notLoggedIn);
    window.location.href = "/login";
    return;
  }

  try {
    const res = await fetch(`/api/admin/maintenance-windows/${id}`, {
      method: "DELETE",
      headers: { Authorization: "Bearer " + token }
    });

    if (!res.ok) throw new Error(i18n.alert.deleteFail);

    alert(i18n.alert.deleteSuccess);
    loadMaintenanceHistory(currentPage);
  } catch (e) {
    alert(e.message);
  }
}

// =================== Helper ===================
function renderSummary() {
  const from = totalElements === 0 ? 0 : currentPage * pageSize + 1;
  const to = Math.min((currentPage + 1) * pageSize, totalElements);
  document.getElementById("summaryText").textContent = `${from}–${to} / ${totalElements}`;
}

function renderPagination(total, current) {
  const el = document.getElementById("pagination");
  el.innerHTML = "";
  if (total <= 1) return;

  let html = `<button class="page-btn" ${current === 0 ? "disabled" : ""} onclick="loadMaintenanceHistory(${current - 1})">«</button>`;
  const windowSize = 7;
  let start = Math.max(0, current - Math.floor(windowSize / 2));
  let end = Math.min(total - 1, start + windowSize - 1);
  if (end - start + 1 < windowSize) start = Math.max(0, end - windowSize + 1);

  for (let i = start; i <= end; i++) {
    html += `<button class="page-btn ${i === current ? "active" : ""}" onclick="loadMaintenanceHistory(${i})">${i + 1}</button>`;
  }
  html += `<button class="page-btn" ${current === total - 1 ? "disabled" : ""} onclick="loadMaintenanceHistory(${current + 1})">»</button>`;
  el.innerHTML = html;
}

function formatDate(dateStr) {
  if (!dateStr) return "-";
  const d = new Date(dateStr);
  return d.toLocaleString("vi-VN", { timeZone: "Asia/Ho_Chi_Minh" });
}

function resetFilters() {
  document.getElementById("filterPitchName").value = "";
  document.getElementById("filterStartDate").value = "";
  document.getElementById("filterEndDate").value = "";
  loadMaintenanceHistory(0);
}

function applyFilters() {
  loadMaintenanceHistory(0);
}

function changePageSize(val) {
  pageSize = parseInt(val, 10) || 10;
  loadMaintenanceHistory(0);
}

// =================== Export ===================
window.loadMaintenanceHistory = loadMaintenanceHistory;
window.deleteMaintenance = deleteMaintenance;
window.applyFilters = applyFilters;
window.resetFilters = resetFilters;
window.changePageSize = changePageSize;
