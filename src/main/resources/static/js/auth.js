// auth.js - Login and Registration logic with validation

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;

            // Validate login form
            const validationError = validateLoginForm(email, password);
            if (validationError) {
                showAlert('loginAlert', 'loginAlertMessage', validationError);
                return;
            }

            hideAlert('loginAlert');
            setLoading('loginBtn', true);

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();

                if (response.ok && data.token) {
                    // 1. Lưu JWT Token
                    localStorage.setItem('jwt_token', data.token);
                    setJwtCookie(data.token);

                    // 2. Chuẩn bị thông tin User
                    const userInfo = {
                        userId: data.userId,
                        email: data.email,
                        username: data.email.split('@')[0],
                        role: data.role
                    };
                    localStorage.setItem('user_info', JSON.stringify(userInfo));

                    // 3. CHUYỂN HƯỚNG THEO ROLE
                    const userRole = data.role.replace('ROLE_', '');

                    if (userRole === 'ADMIN') {
                        window.location.replace('/admin/dashboard');
                    } else if (userRole === 'RECRUITER' || userRole === 'COMPANY') {
                        await checkAndRedirectRecruiter();
                    } else if (userRole === 'CANDIDATE') {
                        window.location.replace('/');
                    } else {
                        window.location.replace('/');
                    }

                } else {
                    if (data.message) {
                        showAlert('loginAlert', 'loginAlertMessage', data.message);
                    } else {
                        showAlert('loginAlert', 'loginAlertMessage', 'Đăng nhập thất bại: Sai thông tin đăng nhập');
                    }
                }
            } catch (error) {
                console.error('Login error:', error);
                showAlert('loginAlert', 'loginAlertMessage', 'Có lỗi xảy ra, vui lòng thử lại sau');
            } finally {
                setLoading('loginBtn', false);
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const username = document.getElementById('username')?.value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const role = document.querySelector('input[name="role"]:checked')?.value;

            // Validate register form
            const validationError = validateRegisterForm(username, email, password, role);
            if (validationError) {
                showAlert('registerAlert', 'registerAlertMessage', validationError);
                return;
            }

            hideAlert('registerAlert');
            setLoading('registerBtn', true);

            try {
                // Step 1: Register user
                const registerData = { email, password, role };
                const registerResponse = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(registerData)
                });

                const registerResult = await registerResponse.json();

                if (!registerResponse.ok) {
                    showAlert('registerAlert', 'registerAlertMessage', registerResult.message || 'Đăng ký thất bại: Dữ liệu không hợp lệ');
                    return;
                }

                // Step 2: If recruiter, submit company info
                if (role === 'RECRUITER') {
                    const companyData = {
                        companyName: document.getElementById('companyName').value.trim(),
                        taxCode: document.getElementById('taxCode').value.trim(),
                        businessLicenseUrl: document.getElementById('businessLicenseUrl').value.trim(),
                        companyAddress: document.getElementById('companyAddress').value.trim(),
                        companyWebsite: document.getElementById('companyWebsite').value.trim(),
                        companyPhone: document.getElementById('companyPhone').value.trim(),
                        contactPersonName: document.getElementById('contactPersonName').value.trim(),
                        contactPersonPosition: document.getElementById('contactPersonPosition').value.trim(),
                        contactPersonPhone: document.getElementById('contactPersonPhone').value.trim()
                    };

                    // Get token for approval request
                    const loginResponse = await fetch('/api/auth/login', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ email, password })
                    });

                    if (loginResponse.ok) {
                        const loginData = await loginResponse.json();
                        localStorage.setItem('jwt_token', loginData.token);
                        setJwtCookie(loginData.token);
                    }

                    const approvalResponse = await fetch('/api/recruiter-approval', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
                        },
                        body: JSON.stringify(companyData)
                    });

                    if (!approvalResponse.ok) {
                        alert('Đăng ký thành công nhưng gửi thông tin công ty thất bại. Vui lòng liên hệ admin.');
                    }

                    alert('Đăng ký thành công! Tài khoản của bạn đang chờ phê duyệt. Vui lòng liên hệ admin để được duyệt.');
                    localStorage.removeItem('jwt_token');
                } else {
                    alert('Đăng ký thành công! Hãy đăng nhập để bắt đầu.');
                }

                window.location.href = '/login';
            } catch (error) {
                console.error('Registration error:', error);
                showAlert('registerAlert', 'registerAlertMessage', 'Có lỗi xảy ra, vui lòng thử lại sau');
            } finally {
                setLoading('registerBtn', false);
            }
        });
    }
});

function setJwtCookie(token) {
    const secureFlag = window.location.protocol === 'https:' ? '; Secure' : '';
    document.cookie = `jwt_token=${token}; path=/; max-age=86400; SameSite=Lax${secureFlag}`;
}

// ==================== VALIDATION FUNCTIONS ====================

function validateLoginForm(email, password) {
    // Email validation
    if (!email) {
        return 'Vui lòng nhập email';
    }
    if (!isValidEmail(email)) {
        return 'Email không hợp lệ';
    }

    // Password validation
    if (!password) {
        return 'Vui lòng nhập mật khẩu';
    }
    if (password.length < 6) {
        return 'Mật khẩu phải có ít nhất 6 ký tự';
    }

    return null;
}

function validateRegisterForm(username, email, password, role) {
    // Username validation
    if (!username) {
        return 'Vui lòng nhập họ và tên';
    }
    if (username.length < 2) {
        return 'Họ và tên phải có ít nhất 2 ký tự';
    }
    if (username.length > 100) {
        return 'Họ và tên không được vượt quá 100 ký tự';
    }
    if (!/^[\p{L}\s]+$/u.test(username)) {
        return 'Họ và tên chỉ được chứa chữ cái và khoảng trắng';
    }

    // Email validation
    if (!email) {
        return 'Vui lòng nhập email';
    }
    if (!isValidEmail(email)) {
        return 'Email không hợp lệ';
    }

    // Password validation
    if (!password) {
        return 'Vui lòng nhập mật khẩu';
    }
    if (password.length < 6) {
        return 'Mật khẩu phải có ít nhất 6 ký tự';
    }
    if (password.length > 50) {
        return 'Mật khẩu không được vượt quá 50 ký tự';
    }

    // Role validation
    if (!role) {
        return 'Vui lòng chọn vai trò';
    }

    // Recruiter specific validation
    if (role === 'RECRUITER') {
        const companyName = document.getElementById('companyName')?.value.trim();
        if (!companyName) {
            return 'Vui lòng nhập tên công ty';
        }
        if (companyName.length < 2) {
            return 'Tên công ty phải có ít nhất 2 ký tự';
        }

        // Validate tax code format if provided
        const taxCode = document.getElementById('taxCode')?.value.trim();
        if (taxCode && !/^[0-9]{10,13}$/.test(taxCode)) {
            return 'Mã số thuế phải là 10-13 chữ số';
        }

        // Validate company phone if provided
        const companyPhone = document.getElementById('companyPhone')?.value.trim();
        if (companyPhone && !isValidPhone(companyPhone)) {
            return 'Số điện thoại công ty không hợp lệ';
        }

        // Validate website URL if provided
        const companyWebsite = document.getElementById('companyWebsite')?.value.trim();
        if (companyWebsite && !isValidUrl(companyWebsite)) {
            return 'Website công ty không hợp lệ';
        }

        // Validate contact person phone if provided
        const contactPhone = document.getElementById('contactPersonPhone')?.value.trim();
        if (contactPhone && !isValidPhone(contactPhone)) {
            return 'Số điện thoại người đại diện không hợp lệ';
        }
    }

    // Check terms agreement
    const agreeTerms = document.getElementById('agreeTerms');
    if (agreeTerms && !agreeTerms.checked) {
        return 'Bạn phải đồng ý với Điều khoản và Chính sách bảo mật';
    }

    return null;
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function isValidPhone(phone) {
    // Vietnamese phone format: 0xxxxxxxxx, +84xxxxxxxxx, or 0xxxxxxxxxx (10-11 digits)
    const phoneRegex = /^(0[0-9]{9,10}|(\+84)[0-9]{9,10})$/;
    // Also allow simple format: digits only, 10-11 digits
    const simpleRegex = /^[0-9]{10,11}$/;
    return phoneRegex.test(phone.replace(/\s/g, '')) || simpleRegex.test(phone.replace(/\s/g, ''));
}

function isValidUrl(string) {
    try {
        new URL(string);
        return true;
    } catch (_) {
        // Try with https:// prefix if no protocol
        if (!string.startsWith('http://') && !string.startsWith('https://')) {
            try {
                new URL('https://' + string);
                return true;
            } catch (_) {
                return false;
            }
        }
        return false;
    }
}

// ==================== UI HELPER FUNCTIONS ====================

function showAlert(alertId, messageId, message) {
    const alert = document.getElementById(alertId);
    const messageEl = document.getElementById(messageId);
    if (alert && messageEl) {
        messageEl.textContent = message;
        alert.classList.remove('hidden');
    }
}

function hideAlert(alertId) {
    const alert = document.getElementById(alertId);
    if (alert) {
        alert.classList.add('hidden');
    }
}

function setLoading(btnId, isLoading) {
    const btn = document.getElementById(btnId);
    if (!btn) return;

    const btnText = btn.querySelector('span:not(.hidden)');
    const btnLoading = btn.querySelector('span.hidden');

    if (isLoading) {
        btn.disabled = true;
        if (btnText) btnText.classList.add('hidden');
        if (btnLoading) btnLoading.classList.remove('hidden');
    } else {
        btn.disabled = false;
        if (btnText) btnText.classList.remove('hidden');
        if (btnLoading) btnLoading.classList.add('hidden');
    }
}

// ==================== LOGIN GUIDE POPUP ====================

function showLoginGuide() {
    const popup = document.getElementById('loginGuidePopup');
    if (popup) {
        popup.classList.remove('hidden');
    }
}

function closeLoginGuide() {
    const popup = document.getElementById('loginGuidePopup');
    if (popup) {
        popup.classList.add('hidden');
    }
}

// ==================== GOOGLE GUIDE POPUP ====================

function showGoogleGuide() {
    const popup = document.getElementById('googleGuidePopup');
    if (popup) {
        // Set current redirect URI
        const redirectUriEl = document.getElementById('googleRedirectUri');
        if (redirectUriEl) {
            redirectUriEl.textContent = window.location.origin + '/login/oauth2/code/google';
        }
        popup.classList.remove('hidden');
    }
}

function closeGoogleGuide() {
    const popup = document.getElementById('googleGuidePopup');
    if (popup) {
        popup.classList.add('hidden');
    }
}

// Close popup when clicking overlay
document.addEventListener('DOMContentLoaded', function() {
    // Login guide popup
    const loginPopup = document.getElementById('loginGuidePopup');
    if (loginPopup) {
        const loginOverlay = loginPopup.querySelector('.login-guide-overlay');
        if (loginOverlay) {
            loginOverlay.addEventListener('click', closeLoginGuide);
        }
    }

    // Google guide popup
    const googlePopup = document.getElementById('googleGuidePopup');
    if (googlePopup) {
        const googleOverlay = googlePopup.querySelector('.login-guide-overlay');
        if (googleOverlay) {
            googleOverlay.addEventListener('click', closeGoogleGuide);
        }
    }

    // Show guide when clicking login button with empty fields
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        const loginBtn = document.getElementById('loginBtn');
        if (loginBtn) {
            loginBtn.addEventListener('click', function(e) {
                const email = document.getElementById('email').value.trim();
                const password = document.getElementById('password').value;

                // If fields are empty, show guide popup
                if (!email || !password) {
                    e.preventDefault();
                    showLoginGuide();
                }
            });
        }
    }

    // Show Google guide when clicking Google login button
    const googleBtn = document.querySelector('.btn-google');
    if (googleBtn) {
        googleBtn.addEventListener('click', function(e) {
            e.preventDefault();
            showGoogleGuide();
        });
    }
});

// ==================== OTHER FUNCTIONS ====================

async function checkAndRedirectRecruiter() {
    try {
        const response = await fetch('/api/recruiter-approval/status', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            }
        });

        if (response.ok) {
            const status = await response.json();

            if (!status.hasSubmitted || status.status === 'PENDING' || status.status === 'REJECTED') {
                window.location.replace('/recruiter/approval');
                return;
            }
        }

        window.location.replace('/recruiter/jobs');
    } catch (error) {
        console.error('Check approval status error:', error);
        window.location.replace('/recruiter/jobs');
    }
}
