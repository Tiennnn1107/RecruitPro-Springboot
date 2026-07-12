// main.js - Xử lý giao diện dùng chung và xác thực

/**
 * Lấy thông tin người dùng từ localStorage
 */
function getUserInfo() {
    const info = localStorage.getItem('user_info');
    try {
        return info ? JSON.parse(info) : null;
    } catch (e) {
        console.error("Lỗi parse user_info:", e);
        return null;
    }
}

/**
 * Kiểm tra xem người dùng đã đăng nhập chưa
 */
function isLoggedIn() {
    return localStorage.getItem('jwt_token') !== null;
}

/**
 * Lấy role hiện tại (đã loại bỏ prefix ROLE_)
 */
function getCurrentRole() {
    const user = getUserInfo();
    return user && user.role ? user.role.replace('ROLE_', '') : '';
}

/**
 * Kiểm tra user có phải là RECRUITER/COMPANY không
 */
function isRecruiter() {
    const role = getCurrentRole();
    return role === 'RECRUITER' || role === 'COMPANY';
}

/**
 * Kiểm tra user có phải là CANDIDATE không
 */
function isCandidate() {
    return getCurrentRole() === 'CANDIDATE';
}

/**
 * Kiểm tra user có phải là ADMIN không
 */
function isAdmin() {
    return getCurrentRole() === 'ADMIN';
}

/**
 * Cập nhật thanh điều hướng (Navbar) dựa trên trạng thái đăng nhập
 */
function updateNavbar() {
    const loginBtn = document.getElementById('nav-login-btn');
    const registerBtn = document.getElementById('nav-register-btn');
    const userDropdown = document.getElementById('nav-user-dropdown');
    const userNameDisplay = document.getElementById('nav-username');

    const recruiterMenu = document.getElementById('nav-recruiter-menu');
    const candidateMenu = document.getElementById('nav-candidate-menu');
    const adminMenu = document.getElementById('nav-admin-menu');

    const user = getUserInfo();
    const token = localStorage.getItem('jwt_token');

    if (token && user) {
        // Đã đăng nhập: Ẩn nút đăng nhập/đăng ký
        if (loginBtn) loginBtn.classList.add('hidden');
        if (registerBtn) registerBtn.classList.add('hidden');

        // Hiện dropdown người dùng và tên
        if (userDropdown) userDropdown.classList.remove('hidden');
        if (userNameDisplay) {
            userNameDisplay.textContent = user.username || user.email.split('@')[0];
        }

        // Kiểm tra Role để hiển thị Menu tương ứng (xử lý cả ROLE_ prefix)
        const role = user.role ? user.role.replace('ROLE_', '') : '';

        // Ẩn tất cả menu trước
        if (recruiterMenu) recruiterMenu.classList.add('hidden');
        if (candidateMenu) candidateMenu.classList.add('hidden');
        if (adminMenu) adminMenu.classList.add('hidden');

        // Ẩn tất cả dropdown items theo role
        document.querySelectorAll('.dropdown-item-recruiter').forEach(el => el.classList.add('hidden'));
        document.querySelectorAll('.dropdown-item-candidate').forEach(el => el.classList.add('hidden'));
        document.querySelectorAll('.dropdown-item-admin').forEach(el => el.classList.add('hidden'));

        if (role === 'RECRUITER' || role === 'COMPANY') {
            // Hiện recruiter menu
            if (recruiterMenu) recruiterMenu.classList.remove('hidden');
            // Hiện recruiter dropdown items
            document.querySelectorAll('.dropdown-item-recruiter').forEach(el => el.classList.remove('hidden'));
            // Ẩn candidate-specific dropdown items (trong user dropdown)
            document.querySelectorAll('.dropdown-item-candidate').forEach(el => el.classList.add('hidden'));
        } else if (role === 'CANDIDATE') {
            // Hiện candidate menu
            if (candidateMenu) candidateMenu.classList.remove('hidden');
            // Hiện candidate dropdown items
            document.querySelectorAll('.dropdown-item-candidate').forEach(el => el.classList.remove('hidden'));
        } else if (role === 'ADMIN') {
            // Hiện admin menu, KHÔNG hiện recruiter menu cho admin
            if (adminMenu) adminMenu.classList.remove('hidden');
            // Hiện admin dropdown items
            document.querySelectorAll('.dropdown-item-admin').forEach(el => el.classList.remove('hidden'));
        }

        // Fetch unread notification count
        fetchUnreadNotificationCount();
    } else {
        // Chưa đăng nhập: Hiện nút đăng nhập/đăng ký
        if (loginBtn) loginBtn.classList.remove('hidden');
        if (registerBtn) registerBtn.classList.remove('hidden');

        // Ẩn các menu chức năng
        if (userDropdown) userDropdown.classList.add('hidden');
        if (recruiterMenu) recruiterMenu.classList.add('hidden');
        if (candidateMenu) candidateMenu.classList.add('hidden');
        if (adminMenu) adminMenu.classList.add('hidden');
    }
}

/**
 * Ẩn các nút/chức năng không được phép dựa trên role
 * Gọi sau khi trang load xong
 */
function applyRoleBasedPermissions() {
    const role = getCurrentRole();

    // Ẩn nút "Ứng tuyển ngay" với recruiter và người chưa đăng nhập
    if (isRecruiter() || !isLoggedIn()) {
        document.querySelectorAll('.btn-apply-job').forEach(el => el.classList.add('hidden'));
    }

    // Ẩn nút "Lưu tin" với recruiter và người chưa đăng nhập
    if (isRecruiter() || !isLoggedIn()) {
        document.querySelectorAll('.btn-save-job').forEach(el => el.classList.add('hidden'));
    }

    // Ẩn nút "Đăng tin tuyển dụng" với candidate
    if (isCandidate()) {
        document.querySelectorAll('.btn-post-job').forEach(el => el.classList.add('hidden'));
    }

    // Ẩn sidebar recruiter với candidate
    if (isCandidate()) {
        document.querySelectorAll('.sidebar-recruiter').forEach(el => el.classList.add('hidden'));
    }

    // Ẩn sidebar candidate với recruiter
    if (isRecruiter()) {
        document.querySelectorAll('.sidebar-candidate').forEach(el => el.classList.add('hidden'));
    }

    // Ẩn các menu items theo data-permission attribute
    document.querySelectorAll('[data-permission]').forEach(el => {
        const requiredPermission = el.getAttribute('data-permission');
        let allowed = false;

        switch (requiredPermission) {
            case 'RECRUITER':
                allowed = isRecruiter();
                break;
            case 'CANDIDATE':
                allowed = isCandidate();
                break;
            case 'ADMIN':
                allowed = isAdmin();
                break;
            case 'RECRUITER_OR_COMPANY':
                allowed = isRecruiter();
                break;
            case 'AUTHENTICATED':
                allowed = isLoggedIn();
                break;
        }

        if (!allowed) {
            el.classList.add('hidden');
        }
    });
}

/**
 * Fetch số thông báo chưa đọc và hiển thị badge
 */
async function fetchUnreadNotificationCount() {
    const badge = document.getElementById('notificationBadge');
    if (!badge) return;

    const token = localStorage.getItem('jwt_token');
    if (!token) return;

    try {
        const response = await fetch('/api/notifications/unread-count', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) return;

        const data = await response.json();
        const count = data.count || 0;

        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.style.display = 'block';
        } else {
            badge.style.display = 'none';
        }
    } catch (error) {
        console.error('Error fetching notification count:', error);
    }
}

/**
 * Đăng xuất
 */
function logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_info');
    document.cookie = 'jwt_token=; path=/; max-age=0; SameSite=Lax';
    document.cookie = 'jwt_token=; path=/; max-age=0; SameSite=Lax; Secure';
    window.location.replace('/login');
}

/**
 * API Helper: Fetch có đính kèm Token tự động
 */
async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('jwt_token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401) {
        logout(); // Token hết hạn hoặc không hợp lệ
    }
    return response;
}

/**
 * Lấy header xác thực (chỉ có Authorization)
 */
function getAuthHeaders() {
    const token = localStorage.getItem('jwt_token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

// Chạy cập nhật Navbar ngay khi trang web tải xong
document.addEventListener('DOMContentLoaded', function() {
    updateNavbar();
    applyRoleBasedPermissions();
});
