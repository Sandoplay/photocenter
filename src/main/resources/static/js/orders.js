let ordersModal;

document.addEventListener('DOMContentLoaded', function() {
    ordersModal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));

    // Встановлюємо обробники подій для фільтрів
    document.getElementById('statusFilter').addEventListener('change', applyFilters);
    document.getElementById('urgencyFilter').addEventListener('change', applyFilters);
    document.getElementById('searchInput').addEventListener('input', debounce(applyFilters, 500));
});

function startProcessing(orderId) {
    if (confirm('Почати обробку замовлення?')) {
        updateOrderStatus(orderId, 'IN_PROGRESS');
    }
}

function completeOrder(orderId) {
    if (confirm('Позначити замовлення як виконане?')) {
        updateOrderStatus(orderId, 'COMPLETED');
    }
}

function updateOrderStatus(orderId, status) {
    fetch(`/api/orders/${orderId}/status`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: status })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            window.location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Помилка при оновленні статусу замовлення');
        });
}

let ordersModal;

document.addEventListener('DOMContentLoaded', function() {
    ordersModal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
});

function viewOrderDetails(orderId) {
    fetch(`/api/orders/${orderId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(order => {
            document.getElementById('orderDetailId').textContent = order.id;
            document.getElementById('clientName').textContent = order.client.name;
            document.getElementById('clientEmail').textContent = order.client.email;
            document.getElementById('clientPhone').textContent = order.client.phoneNumber || 'Не вказано';

            const orderDate = new Date(order.orderDate).toLocaleString('uk-UA');
            document.getElementById('orderDate').textContent = orderDate;

            const statusElement = document.getElementById('orderStatus');
            statusElement.textContent = order.status;
            statusElement.className = 'status-badge status-' + order.status;

            document.getElementById('orderBranch').textContent = order.branch.name;

            const itemsContainer = document.getElementById('orderItems');
            itemsContainer.innerHTML = '';

            order.orderDetails.forEach(detail => {
                const item = document.createElement('div');
                item.className = 'order-details';

                const itemName = detail.product ?
                    detail.product.name :
                    detail.service.name;

                item.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <strong>${itemName}</strong>
                            <div class="text-muted">
                                Кількість: ${detail.quantity}
                            </div>
                        </div>
                        <div class="text-end">
                            <div>${detail.price}₴</div>
                        </div>
                    </div>
                `;

                itemsContainer.appendChild(item);
            });

            document.getElementById('orderTotal').textContent = order.totalCost;

            // Використовуємо збережену змінну модального вікна
            ordersModal.show();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Помилка при завантаженні деталей замовлення');
        });
}

function applyFilters() {
    const status = document.getElementById('statusFilter').value;
    const urgent = document.getElementById('urgencyFilter').value;
    const search = document.getElementById('searchInput').value;

    // Збираємо параметри для фільтрації
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (urgent) params.append('urgent', urgent);
    if (search) params.append('search', search);

    // Оновлюємо URL і перезавантажуємо сторінку з фільтрами
    window.location.href = `/admin/orders?${params.toString()}`;
}

function refreshOrders() {
    window.location.reload();
}

// Допоміжна функція для debounce
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}
