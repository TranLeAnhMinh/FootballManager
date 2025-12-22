// =================== GLOBAL ===================
const API_SYSTEM_DAILY = "/api/adminsystem/revenue/system/daily";
const API_SYSTEM_MONTHLY = "/api/adminsystem/revenue/system/monthly";
const API_BRANCHES = "/api/adminsystem/branches";

let monthlyChart = null;

// =================== UI UTILITIES ===================
function showAlert(msg, type = "error") {
  const el = document.getElementById("rev-alert");
  el.className = `alert alert-${type}`;
  el.textContent = msg;
  el.style.display = "block";
  setTimeout(() => (el.style.display = "none"), 4000);
}

function formatCurrency(num) {
  return Number(num || 0).toLocaleString("vi-VN") + " ₫";
}

// =================== LOAD BRANCH DROPDOWN ===================
async function loadBranchDropdown() {
  const token = localStorage.getItem("accessToken");
  if (!token) return;
 
  console.log("["+ I18N.branchAll +"]"); 
  try {
    const res = await fetch(API_BRANCHES, {
      headers: { Authorization: "Bearer " + token }
    });

    if (!res.ok) throw new Error("Không thể tải danh sách chi nhánh");

    const data = await res.json();
    const select = document.getElementById("branch-select");
    if (!select) return;

    select.innerHTML = `<option value="">${I18N.branchAll}</option>`;

    data.forEach(b => {
      const opt = document.createElement("option");
      opt.value = b.id;
      opt.textContent = b.name;
      select.appendChild(opt);
    });

  } catch (err) {
    console.error(err);
  }
}

// =================== DAILY ===================
async function loadDailyRevenue() {
  const token = localStorage.getItem("accessToken");
  if (!token) return showAlert("Vui lòng đăng nhập lại");

  const dateValue = document.getElementById("rev-date")?.value;
  const branchId = document.getElementById("branch-select")?.value;

  const today = new Date();
  const selectedDate = dateValue ? new Date(dateValue) : today;
  const isToday = selectedDate.toDateString() === today.toDateString();
  const lang = document.documentElement.lang || "vi";

  const titleEl = document.getElementById("daily-title");
  const i18nToday = titleEl.dataset.i18nToday;
  const i18nOnDate = titleEl.dataset.i18nNetdate;

  titleEl.textContent = isToday
    ? i18nToday
    : i18nOnDate.replace(
        "{0}",
        selectedDate.toLocaleDateString(
          lang === "ja" ? "ja-JP" : lang === "en" ? "en-US" : "vi-VN"
        )
      );

  const body = {};
  if (dateValue) body.date = dateValue;

  // CHỌN API
  const url = branchId
    ? `/api/adminsystem/revenue/branch/${branchId}/daily`
    : API_SYSTEM_DAILY;

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify(body),
    });

    if (!res.ok) throw new Error("Không thể tải doanh thu ngày");

    const data = await res.json();

    document.getElementById("daily-net").textContent = formatCurrency(data.netRevenue);
    document.getElementById("daily-approved").textContent = formatCurrency(data.approvedRevenue);
    document.getElementById("daily-cancelled").textContent = formatCurrency(data.cancelledOrRefundedAmount);

    const approvedPct = data.details?.[0]?.percentage?.toFixed(2) ?? 0;
    const cancelledPct = data.details?.[1]?.percentage?.toFixed(2) ?? 0;

    document.getElementById("daily-approved-pct").textContent = approvedPct + "%";
    document.getElementById("daily-cancelled-pct").textContent = cancelledPct + "%";

  } catch (err) {
    console.error(err);
    showAlert(err.message || "Lỗi tải doanh thu ngày");
  }
}

// =================== MONTHLY ===================
async function loadMonthlyRevenue() {
  const token = localStorage.getItem("accessToken");
  if (!token) return showAlert("Vui lòng đăng nhập lại");

  const yearValue = document.getElementById("rev-year")?.value;
  const branchId = document.getElementById("branch-select")?.value;

  const body = {};
  if (yearValue) body.year = parseInt(yearValue, 10);

  // CHỌN API
  const url = branchId
    ? `/api/adminsystem/revenue/branch/${branchId}/monthly`
    : API_SYSTEM_MONTHLY;

  try {
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify(body),
    });

    if (!res.ok) throw new Error("Không thể tải doanh thu tháng");

    const data = await res.json();

    document.getElementById("monthly-year").textContent = data.year;
    document.getElementById("monthly-total").textContent = formatCurrency(data.totalNetRevenue);

    renderMonthlyTable(data.monthlyRevenues);
    renderMonthlyChart(data.monthlyRevenues);

  } catch (err) {
    console.error(err);
    showAlert(err.message || "Lỗi tải doanh thu tháng");
  }
}

// =================== RENDER TABLE ===================
function renderMonthlyTable(items = []) {
  const tbody = document.getElementById("monthly-tbody");
  tbody.innerHTML = "";

  if (!items || items.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;color:#64748b;">${I18N.tableNoData}</td></tr>`;
    return;
  }

  items.forEach((m) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${m.month}</td>
      <td>${formatCurrency(m.approvedRevenue)}</td>
      <td>${formatCurrency(m.cancelledOrRefunded)}</td>
      <td><strong>${formatCurrency(m.netRevenue)}</strong></td>
    `;
    tbody.appendChild(tr);
  });
}

// =================== RENDER CHART ===================
function renderMonthlyChart(items = []) {
  const ctx = document.getElementById("monthly-chart")?.getContext("2d");
  if (!ctx) return;

  const labels = items.map((m) => `${I18N.monthPrefix} ${m.month}`);
  const approved = items.map((m) => m.approvedRevenue || 0);
  const cancelled = items.map((m) => m.cancelledOrRefunded || 0);
  const net = items.map((m) => m.netRevenue || 0);

  if (monthlyChart) monthlyChart.destroy();

  monthlyChart = new Chart(ctx, {
    type: "bar",
    data: {
      labels,
      datasets: [
        { label: I18N.labelApproved, data: approved, backgroundColor: "#22c55e", borderRadius: 4 },
        { label: I18N.labelCancelled, data: cancelled, backgroundColor: "#ef4444", borderRadius: 4 },
        { label: I18N.labelNet, data: net, backgroundColor: "#3b82f6", borderRadius: 4 },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { position: "top" },
        title: { display: true, text: I18N.chartTitle, font: { size: 16 } },
      },
      scales: { y: { beginAtZero: true } },
    },
  });
}

// =================== INIT UI ===================
function initYearOptions() {
  const yearSelect = document.getElementById("rev-year");
  if (!yearSelect) return;
  const currentYear = new Date().getFullYear();
  for (let y = currentYear; y >= currentYear - 5; y--) {
    const opt = document.createElement("option");
    opt.value = y;
    opt.textContent = y;
    yearSelect.appendChild(opt);
  }
}

function initEvents() {
  document.getElementById("btn-load-daily")?.addEventListener("click", loadDailyRevenue);
  document.getElementById("btn-load-monthly")?.addEventListener("click", loadMonthlyRevenue);

  document.getElementById("branch-select")?.addEventListener("change", () => {
    loadDailyRevenue();
    loadMonthlyRevenue();
  });
}

document.addEventListener("DOMContentLoaded", () => {
  initYearOptions();
  loadBranchDropdown();
  initEvents();
  loadDailyRevenue();
  loadMonthlyRevenue();
});
