(() => {
  // map trạng thái backend → i18n
  const statusMap = {
    "PENDING": window.i18n["status.pending"],
    "APPROVED": window.i18n["status.approved"],
    "PAID": window.i18n["status.paid"],
    "CHECKED_IN": window.i18n["status.checked_in"],
    "CANCELLED": window.i18n["status.cancelled"],
    "NO_SHOW": window.i18n["status.no_show"]
  };

  function loadBooking() {
    const bookingStr = localStorage.getItem("lastBooking");
    const box = document.getElementById("summaryBox");

    if (!bookingStr) {
      box.innerHTML = `<p>${window.i18n.error}: Booking not found!</p>`;
      return;
    }

    let booking;
    try {
      booking = JSON.parse(bookingStr);
    } catch (err) {
      console.error("Parse booking error:", err);
      box.innerHTML = `<p>${window.i18n.error}: Error parsing booking!</p>`;
      return;
    }

    box.innerHTML = `
      <h2 class="summary-header">${window.i18n["booking.summary.title"]}</h2>

      <div class="summary-card">
        <div class="card-body">
          <p><strong>${window.i18n["booking.summary.pitch"]}:</strong> ${booking.pitchName}</p>
          <p><strong>${window.i18n["booking.summary.branch"]}:</strong> ${booking.branchName}</p>
          <p><strong>${window.i18n["booking.summary.user"]}:</strong> ${booking.userName}</p>
          <p><strong>${window.i18n["booking.summary.date"]}:</strong> ${booking.bookingDate}</p>
          <p>
            <strong>${window.i18n["booking.summary.status"]}:</strong> 
            <span class="status-badge">${statusMap[booking.status] || booking.status}</span>
          </p>
          <p><strong>${window.i18n["booking.summary.note"]}:</strong> ${booking.note || "---"}</p>
        </div>
      </div>

      <div class="summary-card">
        <div class="card-header">${window.i18n["booking.summary.slots"]}</div>
        <ul class="slot-list">
          ${booking.slots.map(s => `
            <li>
              <span>${new Date(s.startAt).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})}</span>
              →
              <span>${new Date(s.endAt).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})}</span>
            </li>
          `).join("")}
        </ul>
      </div>

      <div class="summary-card">
        <div class="card-header">${window.i18n["booking.summary.pricing"]}</div>
        <div class="card-body">
          <table class="pricing-table">
            <tr><th>${window.i18n["booking.summary.price.base"]}</th><td>${booking.pricing.basePrice} VND</td></tr>
            <tr><th>${window.i18n["booking.summary.price.discount"]}</th><td>- ${booking.pricing.voucherDiscount} VND</td></tr>
            <tr><th>${window.i18n["booking.summary.price.final"]}</th><td class="pricing-final">${booking.pricing.finalPrice} VND</td></tr>
          </table>
        </div>
      </div>

      <div class="payment-box">
        <form action="/payments/${booking.id}" method="post">
          <button type="submit" class="pay-btn">${window.i18n["booking.summary.payButton"]}</button>
        </form>
      </div>
    `;
  }

  document.addEventListener("DOMContentLoaded", loadBooking);
})();
