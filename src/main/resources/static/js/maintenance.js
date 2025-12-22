document.addEventListener("DOMContentLoaded", async () => {
  const pitchId = window.PITCH_ID;
  const token = localStorage.getItem("accessToken");

  if (!pitchId || !token) {
    showToast("⚠️ Thiếu thông tin xác thực hoặc sân!", "error");
    return;
  }

  const authHeaders = { Authorization: "Bearer " + token };
  const calendarEl = document.getElementById("calendar");
  let calendar;

  /* ---------- Helper ---------- */
  const formatDate = (str) =>
    new Date(str).toLocaleString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
      day: "2-digit",
      month: "2-digit",
    });

  // ✅ Chuyển local time thành OffsetDateTime chuẩn (VD: +07:00)
  function toOffsetDateTimeLocal(datetime) {
    if (!datetime) return "";
    const date = new Date(datetime);
    const tzOffsetMin = -date.getTimezoneOffset();
    const sign = tzOffsetMin >= 0 ? "+" : "-";
    const absOffset = Math.abs(tzOffsetMin);
    const hoursOffset = String(Math.floor(absOffset / 60)).padStart(2, "0");
    const minutesOffset = String(absOffset % 60).padStart(2, "0");

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hour = String(date.getHours()).padStart(2, "0");
    const minute = String(date.getMinutes()).padStart(2, "0");
    const second = String(date.getSeconds()).padStart(2, "0");

    return `${year}-${month}-${day}T${hour}:${minute}:${second}${sign}${hoursOffset}:${minutesOffset}`;
  }

  // ✅ Hiển thị local time đúng trong calendar
  function convertToLocal(isoString) {
    if (!isoString) return isoString;
    const date = new Date(isoString);
    return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
      .toISOString()
      .slice(0, 19);
  }

  function showToast(msg, type = "success") {
    const box = document.createElement("div");
    box.className = `toast ${type}`;
    box.textContent = msg;
    document.getElementById("toast-container").appendChild(box);
    setTimeout(() => box.remove(), 3500);
  }

  /* ---------- Load Events ---------- */
  async function loadEvents() {
    try {
      const now = new Date();
      const from = new Date(now.getFullYear(), now.getMonth(), 1).toISOString();
      const to = new Date(
        now.getFullYear(),
        now.getMonth() + 1,
        0,
        23,
        59,
        59
      ).toISOString();

      const [mwRes, slotRes] = await Promise.all([
        fetch(`/api/pitches/${pitchId}/maintenance-windows?from=${from}&to=${to}`, {
          headers: authHeaders,
        }),
        fetch(`/api/pitches/${pitchId}/booking-slots?from=${from}&to=${to}`, {
          headers: authHeaders,
        }),
      ]);

      const mws = await mwRes.json();
      const slots = await slotRes.json();

      calendar.removeAllEvents();

      // 🟥 Maintenance
      mws.forEach((m) => {
        calendar.addEvent({
          id: m.id,
          title: m.reason,
          start: convertToLocal(m.startAt),
          end: convertToLocal(m.endAt),
          color: "#ef4444",
          description: `${window.i18n.maintenance}: ${m.reason}`,
        });
      });

      // 🟧 Booking
      slots.forEach((s) => {
        calendar.addEvent({
          id: s.id,
          title: s.userFullName || "Đã đặt",
          start: convertToLocal(s.startAt),
          end: convertToLocal(s.endAt),
          color: "#f59e0b",
          description: `Booking: ${formatDate(s.startAt)} → ${formatDate(
            s.endAt
          )}`,
        });
      });
    } catch (err) {
      console.error("❌ Lỗi load events:", err);
      showToast(window.i18n.error, "error");
    }
  }

  /* ---------- FullCalendar ---------- */
  calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "timeGridWeek",
    timeZone: "local",
    locale: document.documentElement.lang || "vi",
    height: 700,
    headerToolbar: {
      left: "prev,next today",
      center: "title",
      right: "dayGridMonth,timeGridWeek,timeGridDay",
    },
    eventTimeFormat: { hour: "2-digit", minute: "2-digit", hour12: false },
    eventDidMount(info) {
      new bootstrap.Tooltip(info.el, {
        title: info.event.extendedProps.description,
        placement: "top",
        trigger: "hover",
        container: "body",
      });
      info.el.style.opacity = "0";
      setTimeout(() => (info.el.style.opacity = "1"), 50);
    },
  });

  calendar.render();
  await loadEvents();

  /* ---------- Submit Form ---------- */
  const form = document.getElementById("maintenanceForm");
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const startInput = document.getElementById("startDate").value;
    const endInput = document.getElementById("endDate").value;
    const reason = document.getElementById("reason").value.trim();

    if (!startInput || !endInput || !reason) {
      showToast("⚠️ Vui lòng nhập đầy đủ thông tin!", "error");
      return;
    }

    const startAt = toOffsetDateTimeLocal(startInput);
    const endAt = toOffsetDateTimeLocal(endInput);

    try {
      // 🔍 Kiểm tra trùng booking
      const overlapRes = await fetch(
        `/api/pitches/${pitchId}/maintenance-windows/check-overlap?` +
          new URLSearchParams({ startAt, endAt }),
        { headers: authHeaders }
      );

      if (!overlapRes.ok) throw new Error("Check overlap failed");

      const overlapData = await overlapRes.json();
      if (overlapData.conflict && overlapData.overlaps.length > 0) {
        const list = overlapData.overlaps
          .map(
            (o) =>
              `• ${o.userName} (${formatDate(o.startAt)} → ${formatDate(
                o.endAt
              )})`
          )
          .join("\n");
        if (!confirm(`${window.i18n.confirmOverlap}\n\n${list}`)) return;
      }

      // ✅ Gửi request tạo maintenance
      const res = await fetch(`/api/pitches/${pitchId}/maintenance-windows`, {
        method: "POST",
        headers: { "Content-Type": "application/json", ...authHeaders },
        body: JSON.stringify({ pitchId, startAt, endAt, reason }),
      });

      if (!res.ok) {
        let msg = "❌ Đã xảy ra lỗi, vui lòng thử lại!";
        try {
          const errText = await res.text();
          console.error("❌ Server response:", errText);
          const errBody = JSON.parse(errText);
          msg = errBody.message || msg;
        } catch {
          if (res.status === 409) msg = "⚠️ Khung giờ bảo trì bị trùng!";
        }
        showToast(msg, "error");
        alert(msg); // ✅ fallback chắc chắn hiển thị
        return;
      }

      // ✅ Thành công
      bootstrap.Modal.getInstance(
        document.getElementById("maintenanceModal")
      ).hide();
      showToast("✅ " + window.i18n.success, "success");
      await loadEvents();
    } catch (err) {
      console.error("❌ Lỗi khi tạo maintenance:", err);
      showToast("❌ " + window.i18n.error, "error");
      alert("❌ " + window.i18n.error);
    }
  });
});
