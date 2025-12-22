  const token = localStorage.getItem("accessToken");
  const authHeaders = token ? { "Authorization": "Bearer " + token } : {};
  let selectedVoucher = null;
  let calendar = null;
  let previewEvents = []; // 🔥 highlight preview
  let pendingBooking = null; // 🔥 giữ booking tạm
  console.log("✅ PITCH_ID:", window.PITCH_ID);

  // ✅ helper: format local datetime + offset (+07:00)
  function formatWithOffset(date) {
    const pad = n => n.toString().padStart(2, "0");
    const yyyy = date.getFullYear();
    const MM = pad(date.getMonth() + 1);
    const dd = pad(date.getDate());
    const HH = pad(date.getHours());
    const mm = pad(date.getMinutes());
    const ss = pad(date.getSeconds());

    // offset phút so với UTC (VN = -420)
    const offsetMinutes = date.getTimezoneOffset();
    const sign = offsetMinutes > 0 ? "-" : "+";
    const abs = Math.abs(offsetMinutes);
    const offH = pad(Math.floor(abs / 60));
    const offM = pad(abs % 60);

    return `${yyyy}-${MM}-${dd}T${HH}:${mm}:${ss}${sign}${offH}:${offM}`;
  }

  // 🔥 Hàm mở Booking Summary (redirect sang trang summary)
  function goToSummaryPage(bookingId) {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("Bạn chưa đăng nhập!");
      window.location.href = "/login";
      return;
    }

    // Lưu tạm bookingId vào localStorage (nếu cần dùng lại sau)
    localStorage.setItem("lastBookingId", bookingId);

    // ✅ Redirect trực tiếp tới trang summary với query param
    window.location.href = `/user/bookingsummary?id=${bookingId}`;
  }


  // Load vouchers
  async function loadVouchers() {
    const box = document.getElementById("voucherList");
    box.innerHTML = window.i18n.loading;
    try {
      const res = await fetch("/api/vouchers/available", { headers: authHeaders });
      if (!res.ok) {
        box.innerHTML = `<em>${window.i18n.mustLogin}</em>`;
        return;
      }
      const data = await res.json();
      if (!data.length) {
        box.innerHTML = `<em>${window.i18n.noVoucher}</em>`;
        return;
      }

      box.innerHTML = data.map(v => `
        <div class="voucher-item" data-code="${v.code}">
          <span class="voucher-code">${v.code}</span>
        </div>
      `).join("");
    } catch (err) {
      console.error(err);
      box.innerHTML = `<em>${window.i18n.error}</em>`;
    }
  }

  // Load slots + maintenance
  async function loadSlots() {
    const now = new Date();
    const from = new Date(now.getFullYear(), now.getMonth(), 1).toISOString();
    const to = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59).toISOString();

    const [mwRes, slotRes] = await Promise.all([
      fetch(`/api/pitches/${window.PITCH_ID}/maintenance-windows?from=${from}&to=${to}`),
      fetch(`/api/pitches/${window.PITCH_ID}/booking-slots?from=${from}&to=${to}`)
    ]);

    const [mws, slots] = await Promise.all([mwRes.json(), slotRes.json()]);
    console.log("📌 Maintenance windows:", mws);
    console.log("📌 Booking slots:", slots);
    calendar.removeAllEvents();

    mws.forEach(m => {
      const start = new Date(m.startAt);
      const end = new Date(m.endAt);

      calendar.addEvent({
        title: `${window.i18n.maintenance}: ${m.reason || ""}`,
        start: start.toISOString(),
        end: end.toISOString(),
        color: "#ef4444", // đỏ
        description: window.i18n.maintenance
      });
    });

    slots.forEach(s => {
      const start = new Date(s.startAt);
      const end = new Date(s.endAt);
      calendar.addEvent({
        title: `${window.i18n.booked} (${formatTime(s.startAt)} - ${formatTime(s.endAt)})`,
        start: start.toISOString(),
        end: end.toISOString(),
        color: "#f97316", // cam
        description: window.i18n.booked
      });
    });
  }

  // Format giờ HH:mm
  function formatTime(isoStr) {
    const d = new Date(isoStr);
    return d.getHours().toString().padStart(2,"0") + ":" + d.getMinutes().toString().padStart(2,"0");
  }

  // Init calendar
  function initCalendar() {
    const calendarEl = document.getElementById("calendar");
    calendar = new FullCalendar.Calendar(calendarEl, {
      initialView: "timeGridWeek",
      locale: document.documentElement.lang || "en",
      selectable: false,
      allDaySlot: false,
      slotMinTime: "00:00:00",
      slotMaxTime: "24:00:00",
      eventOverlap: false,
      height: "auto",

      eventDidMount: function(info) {
        if (info.event.extendedProps.description) {
          new bootstrap.Tooltip(info.el, {
            title: info.event.extendedProps.description,
            placement: "top",
            trigger: "hover",
            container: "body"
          });
        }
      }
    });
    calendar.render();
    loadSlots();
  }

  // Sinh danh sách mốc giờ 45 phút
  function generateOptions() {
    const times = [];
    let h = 0, m = 0;
    while (h < 24) {
      const label = String(h).padStart(2,'0') + ':' + String(m).padStart(2,'0');
      times.push(label);
      m += 45;
      if (m >= 60) {
        h += Math.floor(m/60);
        m = m % 60;
      }
    }
    return times;
  }

  // 🔥 thêm slot row vào form
  function addSlotRow() {
    const opts = generateOptions();
    const container = document.getElementById("slotContainer");

    const row = document.createElement("div");
    row.className = "slot-row mb-3 border p-2 rounded";

    const startSel = document.createElement("select");
    startSel.className = "form-select mb-2 startTime";
    startSel.required = true;

    const endSel = document.createElement("select");
    endSel.className = "form-select mb-2 endTime";
    endSel.required = true;

    opts.forEach(t => {
      const o1 = document.createElement("option");
      o1.value = t;
      o1.textContent = t;
      startSel.appendChild(o1);

      const o2 = document.createElement("option");
      o2.value = t;
      o2.textContent = t;
      endSel.appendChild(o2);
    });

    // filter endTime khi chọn start
    startSel.addEventListener("change", () => {
      const [sh, sm] = startSel.value.split(":").map(Number);
      const startMinutes = sh * 60 + sm;

      endSel.innerHTML = "";
      opts.forEach(t => {
        const [eh, em] = t.split(":").map(Number);
        const endMinutes = eh * 60 + em;
        if (endMinutes > startMinutes) {
          const o = document.createElement("option");
          o.value = t;
          o.textContent = t;
          endSel.appendChild(o);
        }
      });
    });

    // nút xoá slot
    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.className = "btn btn-sm btn-danger";
    removeBtn.textContent = window.i18n?.removeSlot || "Xóa slot";
    removeBtn.onclick = () => row.remove();

    row.appendChild(startSel);
    row.appendChild(endSel);
    row.appendChild(removeBtn);
    container.appendChild(row);
  }

  // 🔥 highlight preview slots
  function previewSlots(date, slots) {
    // clear preview cũ
    previewEvents.forEach(ev => ev.remove());
    previewEvents = [];

    slots.forEach(slot => {
      const start = new Date(`${date}T${slot.startTime}:00`);
      const end = new Date(`${date}T${slot.endTime}:00`);

      const ev = calendar.addEvent({
        title: "Preview",
        start: start.toISOString(),
        end: end.toISOString(),
        color: "green",
        description: "Preview"
      });
      previewEvents.push(ev);
    });
  }

  document.addEventListener("DOMContentLoaded", () => {
    loadVouchers();
    initCalendar();

    // render slot mặc định
    addSlotRow();

    document.getElementById("addSlotBtn").addEventListener("click", () => {
      addSlotRow();
    });

    // khi submit modal => chỉ preview + lưu pendingBooking
    const bookingForm = document.getElementById("bookingForm");
    bookingForm.addEventListener("submit", (e) => {
      e.preventDefault();
      const date = document.getElementById("bookingDate").value;
      const voucherCode = document.getElementById("voucherInput").value || null;
      const note = document.getElementById("bookingNote").value || ""; // 👈 lấy note

      const slots = [];
      document.querySelectorAll("#slotContainer .slot-row").forEach(row => {
        const start = row.querySelector(".startTime").value;
        const end = row.querySelector(".endTime").value;
        if (!start || !end) return;

        const startDate = new Date(`${date}T${start}:00`);
        const endDate = new Date(`${date}T${end}:00`);
        const diffMinutes = (endDate - startDate) / (1000 * 60);

        if (endDate <= startDate) {
          alert("Giờ kết thúc phải sau giờ bắt đầu!");
          return;
        }
        if (diffMinutes % 45 !== 0) {
          alert("Thời gian đặt sân phải là bội số của 45 phút (45, 90, 135...)");
          return;
        }
        slots.push({ startTime: start, endTime: end });
      });

      if (!date || slots.length === 0) {
        alert(window.i18n.inputRequired);
        return;
      }

      // preview highlight
      previewSlots(date, slots);

      // lưu pending booking
      pendingBooking = { date, slots, voucherCode, note }; // 👈 save note

      // bật nút NEXT
      document.getElementById("nextBtn").disabled = false;

      // đóng modal
      bootstrap.Modal.getInstance(document.getElementById("bookingModal")).hide();
    });

  // khi bấm NEXT => gọi API booking thật sự
  document.getElementById("nextBtn").addEventListener("click", async () => {
    if (!pendingBooking) return;

    const voucherCode = document.getElementById("voucherInput").value || null;
    const note = pendingBooking.note;

    const body = {
      pitchId: window.PITCH_ID,
      note: note,
      voucherCode: voucherCode,
      slots: pendingBooking.slots.map(s => {
        const startDate = new Date(`${pendingBooking.date}T${s.startTime}:00`);
        const endDate = new Date(`${pendingBooking.date}T${s.endTime}:00`);
        return {
          startAt: formatWithOffset(startDate),
          endAt: formatWithOffset(endDate)
        };
      })
    };

    try {
      const res = await fetch("/api/bookings", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...authHeaders
        },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        const booking = await res.json();
    localStorage.setItem("lastBooking", JSON.stringify(booking)); // 👉 save full object
    window.location.href = `/user/bookingsummary?id=${booking.id}`;

      } else {
        const errMsg = await res.text();
        alert("Booking thất bại! " + errMsg);
        console.error("Booking API error:", errMsg);
      }
    } catch (err) {
      console.error(err);
      alert("Lỗi kết nối server!");
    }
  });

  });
