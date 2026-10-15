const API_BASE = "http://localhost:8080/api";

function formatMoney(value) {
    return Number(value).toLocaleString("en-ZA", {
        style: "currency",
        currency: "ZAR"
    });
}

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

document.getElementById("load-btn").addEventListener("click", loadPortfolio);
loadPortfolio();
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
    } catch (error) {
        message.textContent = "Could not reach the server. Is the backend running?";
        message.className = "error";
    }
}

document.getElementById("withdrawal-form").addEventListener("submit", submitWithdrawal);