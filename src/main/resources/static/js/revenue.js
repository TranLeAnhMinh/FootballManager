// =================== GLOBAL ===================
const API_DAILY = "/api/revenue/branch/daily";
const API_MONTHLY = "/api/revenue/branch/monthly";
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

// =================== DAILY REVENUE ===================
async function loadDailyRevenue() {
  const token = localStorage.getItem("accessToken");
  if (!token) return showAlert("Vui lòng đăng nhập lại");

  const dateInput = document.getElementById("rev-date");
  const dateValue = dateInput.value;
  const today = new Date();
  const selectedDate = dateValue ? new Date(dateValue) : today;

  // ✅ bổ sung lại
  const isToday = selectedDate.toDateString() === today.toDateString();
  const lang = document.documentElement.lang || "vi";

  const titleEl = document.getElementById("daily-title");
  const i18nToday = titleEl.dataset.i18nToday;
  const i18nOnDate = titleEl.dataset.i18nNetdate;

  let titleText;
  if (isToday) {
    titleText = i18nToday;
  } else {
    const formattedDate = selectedDate.toLocaleDateString(
      lang === "ja" ? "ja-JP" : lang === "en" ? "en-US" : "vi-VN"
    );
    titleText = i18nOnDate.replace("{0}", formattedDate);
  }
  titleEl.textContent = titleText;

  const body = dateValue ? { date: dateValue } : {};

  try {
    const res = await fetch(API_DAILY, {
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
    

// =================== MONTHLY REVENUE ===================
async function loadMonthlyRevenue() {
  const token = localStorage.getItem("accessToken");
  if (!token) return showAlert("Vui lòng đăng nhập lại");

  const year = document.getElementById("rev-year").value;
  const body = year ? { year: parseInt(year, 10) } : {};

  try {
    const res = await fetch(API_MONTHLY, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token,
      },
      body: JSON.stringify(body),
    });

    if (!res.ok) throw new Error("Không thể tải doanh thu theo tháng");

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

  if (items.length === 0) {
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
  const ctx = document.getElementById("monthly-chart").getContext("2d");
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

// =================== INIT ===================
function initYearOptions() {
  const yearSelect = document.getElementById("rev-year");
  const currentYear = new Date().getFullYear();
  for (let y = currentYear; y >= currentYear - 5; y--) {
    const opt = document.createElement("option");
    opt.value = y;
    opt.textContent = y;
    yearSelect.appendChild(opt);
  }
}

function initEvents() {
  document.getElementById("btn-load-daily").addEventListener("click", loadDailyRevenue);
  document.getElementById("btn-load-monthly").addEventListener("click", loadMonthlyRevenue);
}

document.addEventListener("DOMContentLoaded", () => {
  initYearOptions();
  initEvents();
  loadDailyRevenue();
  loadMonthlyRevenue();
});
