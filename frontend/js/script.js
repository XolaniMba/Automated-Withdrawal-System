const API_BASE = "http://localhost:8080/api";

function formatMoney(value) {
    return Number(value).toLocaleString("en-ZA", {
        style: "currency",
        currency: "ZAR"
    });
}

// ---------- Portfolio dashboard ----------

async function loadPortfolio() {
    const investorId = document.getElementById("investor-id").value;
    const errorBox = document.getElementById("portfolio-error");
    const details = document.getElementById("portfolio-details");
    const tbody = document.querySelector("#products-table tbody");

    errorBox.textContent = "";
    details.textContent = "";
    tbody.innerHTML = "";

    try {
        const response = await fetch(`${API_BASE}/investors/${investorId}/portfolio`);
        const data = await response.json();

        if (!response.ok) {
            errorBox.textContent = data.message;
            return;
        }

        details.textContent =
            `${data.investorName} (age ${data.investorAge}) - Total balance: ${formatMoney(data.balance)}`;

        data.products.forEach(product => {
            const row = document.createElement("tr");
            [product.name, product.type, formatMoney(product.balance)].forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });
            tbody.appendChild(row);
        });
    } catch (error) {
        errorBox.textContent = "Could not reach the server. Is the backend running?";
    }
}

// ---------- Withdrawal history ----------

async function loadHistory() {
    const investorId = document.getElementById("investor-id").value;
    const message = document.getElementById("history-message");
    const tbody = document.querySelector("#history-table tbody");

    message.textContent = "";
    tbody.innerHTML = "";

    try {
        const response = await fetch(`${API_BASE}/investors/${investorId}/withdrawals`);
        const data = await response.json();

        if (!response.ok) {
            message.textContent = data.message;
            return;
        }

        if (data.length === 0) {
            message.textContent = "No withdrawals yet.";
            return;
        }

        data.forEach(w => {
            const row = document.createElement("tr");
            [
                w.id,
                w.type,
                formatMoney(w.amount),
                w.status,
                new Date(w.dateRequested).toLocaleString("en-ZA")
            ].forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });
            tbody.appendChild(row);
        });
    } catch (error) {
        message.textContent = "Could not reach the server. Is the backend running?";
    }
}

// ---------- Withdrawal form ----------

async function submitWithdrawal(event) {
    event.preventDefault();

    const investorId = Number(document.getElementById("investor-id").value);
    const amount = Number(document.getElementById("amount").value);
    const type = document.getElementById("type").value;
    const message = document.getElementById("withdrawal-message");

    message.className = "";
    message.textContent = "";

    // UI validation: catch obvious mistakes before calling the server
    if (!amount || amount <= 0) {
        message.textContent = "Enter an amount greater than zero.";
        message.className = "error";
        return;
    }
    if (!type) {
        message.textContent = "Select a withdrawal type.";
        message.className = "error";
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/withdrawals`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ investorId, amount, type })
        });
        const data = await response.json();

        if (!response.ok) {
            message.textContent = data.message;
            message.className = "error";
            return;
        }

        message.textContent =
            `Withdrawal #${data.id} submitted: ${formatMoney(data.amount)} (${data.status})`;
        message.className = "success";
        document.getElementById("withdrawal-form").reset();
        loadHistory();
    } catch (error) {
        message.textContent = "Could not reach the server. Is the backend running?";
        message.className = "error";
    }
}
// ---------- CSV download ----------

async function downloadCsv() {
    const investorId = document.getElementById("investor-id").value;
    const type = document.getElementById("csv-type").value;
    const from = document.getElementById("csv-from").value;
    const to = document.getElementById("csv-to").value;
    const message = document.getElementById("csv-message");

    message.className = "";
    message.textContent = "";

    if (from && to && from > to) {
        message.textContent = "The 'From' date must not be after the 'To' date.";
        message.className = "error";
        return;
    }

    const params = new URLSearchParams();
    if (type) params.append("type", type);
    if (from) params.append("from", from);
    if (to) params.append("to", to);

    const query = params.toString();
    const url = `${API_BASE}/investors/${investorId}/withdrawals/export${query ? "?" + query : ""}`;

    try {
        const response = await fetch(url);

        if (!response.ok) {
            const data = await response.json();
            message.textContent = data.message;
            message.className = "error";
            return;
        }

        const blob = await response.blob();

        const fileUrl = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = fileUrl;
        link.download = "withdrawals.csv";
        document.body.appendChild(link);
        link.click();
        link.remove();
        URL.revokeObjectURL(fileUrl);

        message.textContent = "Statement downloaded.";
        message.className = "success";
    } catch (error) {
        message.textContent = "Could not reach the server. Is the backend running?";
        message.className = "error";
    }
}

// ---------- Wire everything up ----------

document.getElementById("load-btn").addEventListener("click", () => {
    loadPortfolio();
    loadHistory();
});

document.getElementById("withdrawal-form").addEventListener("submit", submitWithdrawal);

document.getElementById("csv-btn").addEventListener("click", downloadCsv);

// Load data when the page opens
loadPortfolio();
loadHistory();