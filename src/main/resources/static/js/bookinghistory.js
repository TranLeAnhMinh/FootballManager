let currentPage = 0;
let pageSize = 10;
let totalElements = 0;
let totalPages = 0;

// =================== Load danh sách đặt sân ===================
async function loadBookingHistory(page = 0) {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert("Bạn chưa đăng nhập");
    window.location.href = "/login";
    return;
  }

  try {
    const res = await fetch(`/api/bookings/history?page=${page}&size=${pageSize}`, {
      headers: { Authorization: "Bearer " + token }
    });
    if (!res.ok) throw new Error("Không thể tải lịch sử đặt sân");

    const data = await res.json(); // Spring Page<BookingHistoryResponse>

    const rows = data.content || [];
    currentPage = data.number ?? page;
    totalPages = data.totalPages ?? 0;
    totalElements = data.totalElements ?? rows.length;

    renderRows(rows);
    renderSummary();
    renderPagination(totalPages, currentPage);
  } catch (e) {
    alert(e.message);
  }
}

// =================== Render bảng danh sách ===================
function renderRows(items) {
  const tbody = document.getElementById("bookingList");
  tbody.innerHTML = "";

  if (!items || items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#64748b;">${bookingNoDataText}</td></tr>`;
    return;
  }

  items.forEach((b, idx) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${currentPage * pageSize + idx + 1}</td>
      <td>${b.pitchName}</td>
      <td>${b.branchName}</td>
      <td>${formatDate(b.createdAt)}</td>
      <td><span class="status ${b.status?.toLowerCase().replace('_', '-')}">${mapStatus(b.status)}</span></td>
      <td>${(b.finalPrice ?? 0).toLocaleString("vi-VN")} đ</td>
      <td>
        <a class="detail-link" href="#" onclick="showBookingDetail('${b.bookingId}')">${bookingViewText}</a>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

// =================== Gọi API chi tiết + mở modal ===================
async function showBookingDetail(bookingId) {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    alert("Bạn chưa đăng nhập");
    window.location.href = "/login";
    return;
  }

  try {
    const res = await fetch(`/api/bookings/${bookingId}`, {
      headers: { Authorization: "Bearer " + token }
    });
    if (!res.ok) throw new Error("Không thể tải chi tiết đặt sân");

    const data = await res.json();

    // ✅ Gán dữ liệu vào modal
    document.getElementById("modalPitchName").innerText = data.pitchName || "-";
    document.getElementById("modalBranchName").innerText = data.branchName || "-";
    document.getElementById("modalUserName").innerText = data.userName || "-";
    document.getElementById("modalBookingDate").innerText = data.bookingDate || "-";
    document.getElementById("modalStatus").innerText = mapStatus(data.status) || "-";
    document.getElementById("modalNote").innerText = data.note || "-";

    // ✅ Gán danh sách khung giờ
    const slotsList = document.getElementById("modalSlotsList");
    slotsList.innerHTML = "";
    (data.slots || []).forEach(slot => {
      const li = document.createElement("li");
      li.textContent = `${formatDate(slot.startAt)} - ${formatDate(slot.endAt)} ${slot.checkedIn ? "(đã check-in)" : ""}`;
      slotsList.appendChild(li);
    });

    document.getElementById("bookingModal").style.display = "flex";
  } catch (e) {
    alert(e.message);
  }
}

// =================== Đóng modal ===================
function closeModal() {
  document.getElementById("bookingModal").style.display = "none";
}

// =================== Tóm tắt phân trang ===================
function renderSummary() {
  const from = totalElements === 0 ? 0 : currentPage * pageSize + 1;
  const to = Math.min((currentPage + 1) * pageSize, totalElements);

  // ✅ thay {0} {1} {2} trong chuỗi i18n
  document.getElementById("summaryText").textContent =
    paginationRangeText
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
    <button class="page-btn" ${current === 0 ? "disabled" : ""} onclick="loadBookingHistory(${current - 1})">«</button>
  `;

  const windowSize = 7;
  let start = Math.max(0, current - Math.floor(windowSize / 2));
  let end = Math.min(total - 1, start + windowSize - 1);
  if (end - start + 1 < windowSize) start = Math.max(0, end - windowSize + 1);

  for (let i = start; i <= end; i++) {
    html += `<button class="page-btn ${i === current ? "active" : ""}" onclick="loadBookingHistory(${i})">${i + 1}</button>`;
  }

  html += `
    <button class="page-btn" ${current === total - 1 ? "disabled" : ""} onclick="loadBookingHistory(${current + 1})">»</button>
  `;
  el.innerHTML = html;
}

// =================== Thay đổi pageSize ===================
function changePageSize(val) {
  pageSize = parseInt(val, 10) || 10;
  loadBookingHistory(0);
}

// =================== Format ngày giờ ===================
function formatDate(dateStr) {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return d.toLocaleString("vi-VN", { timeZone: "Asia/Ho_Chi_Minh" });
}

// =================== Map trạng thái sang text chuẩn ===================
function mapStatus(status) {
  if (!status) return "-";
  return statusTextMap[status] || status;
}
