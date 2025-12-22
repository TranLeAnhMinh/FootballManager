let currentPage = 0;
let pageSize = 10;
let totalElements = 0;
let totalPages = 0;
let currentBookings = [];

// =================== Lấy toàn bộ filter hiện tại ===================
function getFilterParams() {
  return {
    userKeyword: document.getElementById("searchKeyword")?.value.trim() || "",
    pitchName: document.getElementById("filterPitchName")?.value.trim() || "",
    status: document.getElementById("filterStatus")?.value || "",
    startDate: document.getElementById("filterStartDate")?.value || "",
    endDate: document.getElementById("filterEndDate")?.value || ""
  };
}

// =================== Load danh sách đặt sân (với filter đầy đủ) ===================
async function loadBranchBookings(page = 0) {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert(i18n.alert?.notLoggedIn || "You are not logged in");
    window.location.href = "/login";
    return;
  }

  const filters = getFilterParams();

  const params = new URLSearchParams({
    page,
    size: pageSize,
    ...(filters.userKeyword && { userKeyword: filters.userKeyword }),
    ...(filters.pitchName && { pitchName: filters.pitchName }),
    ...(filters.status && { status: filters.status }),
    ...(filters.startDate && { startDate: filters.startDate }),
    ...(filters.endDate && { endDate: filters.endDate })
  }).toString();

  try {
    const res = await fetch(`/api/bookings/branch?${params}`, {
      headers: { Authorization: "Bearer " + token }
    });
    if (!res.ok) throw new Error(i18n.error?.loadFail || "Failed to load booking list");

    const data = await res.json();
    const rows = data.content || [];

    currentBookings = rows;
    currentPage = data.number ?? page;
    totalPages = data.totalPages ?? 0;
    totalElements = data.totalElements ?? rows.length;

    renderBranchRows(rows);
    renderSummary();
    renderPagination(totalPages, currentPage);
  } catch (e) {
    alert(e.message);
  }
}

// =================== Khi thay đổi filter ===================
function applyFilters() {
  loadBranchBookings(0);
}

// =================== Render bảng danh sách ===================
function renderBranchRows(items) {
  const tbody = document.getElementById("branchBookingList");
  tbody.innerHTML = "";

  if (!items || items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;color:#64748b;">${i18n.table.noData}</td></tr>`;
    return;
  }

  items.forEach((b, idx) => {
    const tr = document.createElement("tr");
    const statusKey = b.status || "";
    const localizedStatus =
      i18n.status[statusKey] ||
      i18n.status[statusKey.toLowerCase()] ||
      statusKey;
    const statusClass = `status-${statusKey.replace(/\s+/g, "_")}`;
    const actions = getActionButtons(b);

    tr.innerHTML = `
      <td>${currentPage * pageSize + idx + 1}</td>
      <td>${b.pitchName || "-"}</td>
      <td>${b.pitchLocation || "-"}</td>
      <td>${formatDate(b.startAt) || "-"}</td>
      <td><span class="status-badge ${statusClass}">${localizedStatus}</span></td>
      <td>${(b.finalPrice ?? 0).toLocaleString("vi-VN")} đ</td>
      <td><button class="action-btn view" onclick="viewBooking('${b.bookingId}')">${i18n.table.view}</button></td>
      <td>${actions}</td>
    `;
    tbody.appendChild(tr);
  });
}

// =================== Render nút hành động ===================
function getActionButtons(b) {
  if (b.status === "WAITING_REFUND") {
    return `
      <button class="action-btn refund"
              onclick="updateBookingStatus('${b.bookingId}', 'WAITING_REFUND', 'REFUNDED')">
         ${i18n.action?.refund || "Hoàn tiền"}
      </button>`;
  }
  if (b.status === "APPROVED") {
    return `
      <button class="action-btn cancel"
              onclick="updateBookingStatus('${b.bookingId}', 'APPROVED', 'WAITING_REFUND')">
        ${i18n.action?.cancel || "Hủy"}
      </button>`;
  }
  return `<span style="color:#9ca3af;">-</span>`;
}

// =================== Gọi API cập nhật trạng thái ===================
async function updateBookingStatus(bookingId, oldStatus, newStatus) {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert(i18n.alert?.notLoggedIn || "You are not logged in");
    window.location.href = "/login";
    return;
  }

  const confirmText =
    newStatus === "REFUNDED"
      ? (i18n.confirm?.refund || "Are you sure to refund this booking?")
      : (i18n.confirm?.cancel || "Are you sure to cancel this booking?");
  if (!confirm(confirmText)) return;

  try {
    const body = {
      bookingId,
      oldStatus,
      newStatus,
      adminNote:
        newStatus === "REFUNDED"
          ? (i18n.note?.refund || "Refunded via customer's bank account.")
          : (i18n.note?.cancel || "Cancelled by branch admin.")
    };

    const res = await fetch(`/api/bookings/branch/update-status`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token
      },
      body: JSON.stringify(body)
    });

    if (!res.ok) throw new Error(i18n.error?.updateFail || "Failed to update booking status");

    const data = await res.json();
    alert(data.message || i18n.alert?.updateSuccess || "Update successful!");
    loadBranchBookings(currentPage);
  } catch (e) {
    alert(e.message);
  }
}

// =================== Mở modal chi tiết booking ===================
function viewBooking(bookingId) {
  const booking = currentBookings.find((b) => b.bookingId === bookingId);
  if (!booking) {
    alert(i18n.error?.notFound || "Booking not found");
    return;
  }

  const localizedStatus =
    i18n.status[booking.status] ||
    i18n.status[booking.status.toLowerCase()] ||
    booking.status;

  document.getElementById("modalPitch").innerText = booking.pitchName || "-";
  document.getElementById("modalBranch").innerText = booking.pitchLocation || "-";
  document.getElementById("modalUser").innerText = booking.userFullName || "-";
  document.getElementById("modalDate").innerText = formatDate(booking.startAt) || "-";
  document.getElementById("modalStatus").innerText = localizedStatus;
  document.getElementById("modalNote").innerText = booking.note || "-";

  document.getElementById("bookingModal").style.display = "flex";
}

// =================== Đóng modal ===================
function closeModal() {
  document.getElementById("bookingModal").style.display = "none";
}

// =================== Render tóm tắt phân trang ===================
function renderSummary() {
  const from = totalElements === 0 ? 0 : currentPage * pageSize + 1;
  const to = Math.min((currentPage + 1) * pageSize, totalElements);
  document.getElementById("summaryText").textContent = i18n.pagination
    .replace("{0}", from)
    .replace("{1}", to)
    .replace("{2}", totalElements);
}

// =================== Phân trang ===================
function renderPagination(total, current) {
  const el = document.getElementById("pagination");
  el.innerHTML = "";
  if (total <= 1) return;

  let html = `
    <button class="page-btn" ${current === 0 ? "disabled" : ""} onclick="loadBranchBookings(${current - 1})">«</button>
  `;

  const windowSize = 7;
  let start = Math.max(0, current - Math.floor(windowSize / 2));
  let end = Math.min(total - 1, start + windowSize - 1);
  if (end - start + 1 < windowSize) start = Math.max(0, end - windowSize + 1);

  for (let i = start; i <= end; i++) {
    html += `<button class="page-btn ${i === current ? "active" : ""}" onclick="loadBranchBookings(${i})">${i + 1}</button>`;
  }

  html += `
    <button class="page-btn" ${current === total - 1 ? "disabled" : ""} onclick="loadBranchBookings(${current + 1})">»</button>
  `;
  el.innerHTML = html;
}

// =================== Thay đổi page size ===================
function changePageSize(val) {
  pageSize = parseInt(val, 10) || 10;
  loadBranchBookings(0);
}

// =================== Format ngày giờ ===================
function formatDate(dateStr) {
  if (!dateStr) return "-";
  const d = new Date(dateStr);
  const locale = document.documentElement.lang || "vi-VN";
  return d.toLocaleString(locale, { timeZone: "Asia/Ho_Chi_Minh" });
}

// =================== Reset bộ lọc ===================
function resetFilters() {
  document.getElementById("searchKeyword").value = "";
  document.getElementById("filterPitchName").value = "";
  document.getElementById("filterStatus").value = "";
  document.getElementById("filterStartDate").value = "";
  document.getElementById("filterEndDate").value = "";
  loadBranchBookings(0);
}

// =================== Xuất các hàm global ===================
window.loadBranchBookings = loadBranchBookings;
window.applyFilters = applyFilters;
window.viewBooking = viewBooking;
window.closeModal = closeModal;
window.updateBookingStatus = updateBookingStatus;
window.changePageSize = changePageSize;
window.resetFilters = resetFilters;
