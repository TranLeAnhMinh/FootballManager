async function loadAdminVouchers() {
    const container = document.getElementById("voucherContainer");
    container.innerHTML = `<p class="loading-text">${i18n.loading}</p>`;

    const token = localStorage.getItem("accessToken");
    if (!token) {
        container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
        return;
    }

    try {
        const res = await fetch("/api/adminsystem/vouchers", {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!res.ok) {
            container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
            return;
        }

        const vouchers = await res.json();

        if (!vouchers || vouchers.length === 0) {
            container.innerHTML = `<p class="empty-text">${i18n.empty}</p>`;
            return;
        }

        const html = vouchers.map(v => `
            <div class="voucher-card">

                <!-- LEFT -->
                <div class="voucher-left">
                    <h4 class="voucher-code">${v.code}</h4>

                    <div class="voucher-info">
                        <div>
                            <strong>${i18n.voucherType}:</strong> ${v.type}
                        </div>
                        <div>
                            <strong>${i18n.voucherValue}:</strong> ${formatValue(v)}
                        </div>
                        <div>
                            <strong>${i18n.voucherMinOrder}:</strong> ${formatMoney(v.minOrder)}
                        </div>
                        <div>
                            <strong>${i18n.voucherMaxDiscount}:</strong> ${formatMoney(v.maxDiscount)}
                        </div>
                        <div>
                            <strong>${i18n.voucherTime}:</strong>
                            ${formatDate(v.startAt)} → ${formatDate(v.endAt)}
                        </div>
                    </div>
                </div>

                <!-- RIGHT -->
                <div class="voucher-right">
                    ${
                        v.active
                            ? `<span class="status-tag active">${i18n.statusActive}</span>`
                            : `<span class="status-tag inactive">${i18n.statusInactive}</span>`
                    }

                    ${
                        v.active
                            ? `<button class="btn-delete"
                                      title="${i18n.confirmDisable}"
                                      onclick="deleteVoucher('${v.id}')">
                                   <i class="fa fa-trash"></i>
                               </button>`
                            : ``
                    }
                </div>

            </div>
        `).join("");

        container.innerHTML = html;

    } catch (err) {
        console.error(err);
        container.innerHTML = `<p class="error-text">${i18n.error}</p>`;
    }
}

/* ================= DELETE (ACTIVE = FALSE) ================= */
async function deleteVoucher(id) {
    if (!confirm(i18n.confirmDisable)) return;

    const token = localStorage.getItem("accessToken");

    try {
        const res = await fetch(`/api/adminsystem/vouchers/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!res.ok) {
            alert(i18n.disableFail);
            return;
        }

        loadAdminVouchers();

    } catch (err) {
        console.error(err);
        alert(i18n.disableFail);
    }
}

/* ================= CREATE ================= */
document.getElementById("createVoucherForm")
    .addEventListener("submit", async (e) => {
        e.preventDefault();

        const form = e.target;
        const token = localStorage.getItem("accessToken");

        const payload = {
            code: form.code.value,
            type: form.type.value,
            value: Number(form.value.value),
            minOrder: Number(form.minOrder.value || 0),
            maxDiscount: Number(form.maxDiscount.value || 0),
            startAt: new Date(form.startAt.value).toISOString(),
            endAt: new Date(form.endAt.value).toISOString(),
            perUserLimit: Number(form.perUserLimit.value || 1)
        };

        try {
            const res = await fetch("/api/adminsystem/vouchers", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                alert(i18n.createFail);
                return;
            }

            bootstrap.Modal
                .getInstance(document.getElementById("createVoucherModal"))
                .hide();

            form.reset();
            loadAdminVouchers();

        } catch (err) {
            console.error(err);
            alert(i18n.createFail);
        }
    });

/* ================= HELPERS ================= */
function formatMoney(val) {
    if (val == null) return "-";
    return Number(val).toLocaleString("vi-VN");
}

function formatDate(iso) {
    if (!iso) return "-";
    const d = new Date(iso);
    return d.toLocaleDateString("vi-VN");
}

function formatValue(v) {
    if (v.type === "PERCENT") {
        return `${v.value}%`;
    }
    return formatMoney(v.value);
}
