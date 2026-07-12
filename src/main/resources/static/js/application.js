// application.js - Job, Posting and Company logic

/**
 * Lưu/Bỏ lưu tin tuyển dụng
 */
async function toggleSaveJob(jobId, btnElement) {
    if (!isLoggedIn()) {
        window.location.href = '/login';
        return;
    }

    const icon = btnElement.querySelector('i');
    const isSaved = icon.classList.contains('fas');

    try {
        if (isSaved) {
            // Unsave
            const response = await fetchWithAuth(`/api/saved-jobs/${jobId}`, { method: 'DELETE' });
            if (response.ok) {
                icon.classList.remove('fas');
                icon.classList.add('far');
                btnElement.classList.remove('btn-warning');
                btnElement.classList.add('btn-outline-warning');
                showToast('Đã bỏ lưu tin tuyển dụng', 'success');
            }
        } else {
            // Save
            const response = await fetchWithAuth(`/api/saved-jobs/${jobId}`, { method: 'POST' });
            if (response.ok) {
                icon.classList.remove('far');
                icon.classList.add('fas');
                btnElement.classList.remove('btn-outline-warning');
                btnElement.classList.add('btn-warning');
                showToast('Đã lưu tin tuyển dụng!', 'success');
            }
        }
    } catch (error) {
        console.error('Save job error:', error);
        showToast('Có lỗi xảy ra', 'error');
    }
}

/**
 * Kiểm tra trạng thái lưu của job và cập nhật UI
 */
async function checkAndUpdateSaveStatus(jobId, btnElement) {
    try {
        const response = await fetchWithAuth(`/api/saved-jobs/check/${jobId}`);
        if (response.ok) {
            const isSaved = await response.json();
            const icon = btnElement.querySelector('i');
            if (isSaved) {
                icon.classList.remove('far');
                icon.classList.add('fas');
                btnElement.classList.remove('btn-outline-warning');
                btnElement.classList.add('btn-warning');
            }
        }
    } catch (error) {
        // Ignore errors for status check
    }
}

/**
 * Ứng tuyển (Ứng viên)
 */
async function applyForJob(jobId) {
    if (!isLoggedIn()) {
        window.location.href = '/login';
        return;
    }

    const coverLetter = prompt("Vui lòng nhập thư giới thiệu của bạn (tùy chọn):");
    if (coverLetter === null) return;

    try {
        const response = await fetchWithAuth('/api/applications', {
            method: 'POST',
            body: JSON.stringify({ jobId, coverLetter })
        });

        if (response.ok) {
            alert('Ứng tuyển thành công!');
            location.reload();
        } else {
            const data = await response.json();
            alert('Lỗi: ' + (data.message || 'Không thể gửi đơn'));
        }
    } catch (error) {
        console.error('Apply error:', error);
    }
}

/**
 * Nạp danh sách tin của Nhà tuyển dụng (Manage Jobs)
 */
async function loadMyJobs() {
    const container = document.getElementById('my-jobs-container');
    if (!container) return;

    try {
        const response = await fetchWithAuth('/api/jobs/my-jobs');
        const jobs = await response.json();

        // Cập nhật stats
        const totalJobs = jobs.length;
        const activeJobs = jobs.filter(j => j.status === 'OPEN').length;

        // Đếm tổng applications từ tất cả jobs
        let totalApplications = 0;
        jobs.forEach(job => {
            totalApplications += job.applicationCount || 0;
        });

        // Cập nhật UI
        const totalJobsEl = document.getElementById('totalJobs');
        const totalApplicationsEl = document.getElementById('totalApplications');
        const activeJobsEl = document.getElementById('activeJobs');
        if (totalJobsEl) totalJobsEl.textContent = totalJobs;
        if (totalApplicationsEl) totalApplicationsEl.textContent = totalApplications;
        if (activeJobsEl) activeJobsEl.textContent = activeJobs;

        if (jobs.length === 0) {
            container.innerHTML = `<tr><td colspan="5" class="text-center py-5"><p class="text-muted mb-0">Bạn chưa đăng tin nào.</p></td></tr>`;
            return;
        }

        container.innerHTML = jobs.map(job => `
            <tr>
                <td class="ps-4">
                    <h6 class="mb-0 fw-bold text-primary">${job.title}</h6>
                    <small class="text-muted"><i class="fas fa-map-marker-alt me-1"></i>${job.location}</small>
                </td>
                <td>${new Date(job.createdAt || Date.now()).toLocaleDateString('vi-VN')}</td>
                <td class="text-center">
                    <a href="/recruiter/applications/job/${job.id}" class="text-decoration-none">
                        <span class="badge bg-info">${job.applicationCount || 0} ứng viên</span>
                    </a>
                </td>
                <td><span class="badge ${job.status === 'OPEN' ? 'bg-success' : 'bg-secondary'}">${job.status}</span></td>
                <td class="text-center pe-4">
                    <div class="btn-group">
                        <a href="/recruiter/jobs/edit/${job.id}" class="btn btn-sm btn-outline-secondary" title="Sửa"><i class="fas fa-edit"></i></a>
                        <a href="/jobs/${job.id}" class="btn btn-sm btn-outline-secondary" title="Xem" target="_blank"><i class="fas fa-eye"></i></a>
                        <button onclick="deleteJob(${job.id})" class="btn btn-sm btn-outline-danger" title="Xóa"><i class="fas fa-trash"></i></button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Load jobs error:', error);
        container.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Lỗi kết nối server</td></tr>`;
    }
}

/**
 * Xóa tin đăng
 */
async function deleteJob(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa tin tuyển dụng này?')) return;

    try {
        const response = await fetchWithAuth(`/api/jobs/${id}`, { method: 'DELETE' });
        if (response.ok) {
            alert('Xóa thành công!');
            loadMyJobs();
        } else {
            alert('Không thể xóa tin tuyển dụng');
        }
    } catch (error) {
        console.error('Delete error:', error);
    }
}

/**
 * Lưu Tin Tuyển Dụng
 */
async function saveJobPosting(e) {
    e.preventDefault();
    const jobId = document.getElementById('jobId').value;
    const data = {
        title: document.getElementById('title').value,
        jobType: document.getElementById('jobType').value,
        location: document.getElementById('location').value,
        salaryMin: document.getElementById('salaryMin').value,
        salaryMax: document.getElementById('salaryMax').value,
        description: document.getElementById('description').value,
        requirements: document.getElementById('requirements').value,
        skills: document.getElementById('skills').value.split(',').map(s => s.trim()).filter(s => s !== '')
    };

    const url = jobId ? `/api/jobs/${jobId}` : '/api/jobs';
    const method = jobId ? 'PUT' : 'POST';

    try {
        const response = await fetchWithAuth(url, { method, body: JSON.stringify(data) });
        if (response.ok) {
            alert(jobId ? 'Cập nhật thành công!' : 'Đăng tin thành công!');
            window.location.href = '/recruiter/jobs';
        } else {
            const errData = await response.json();
            alert('Lỗi: ' + (errData.message || 'Lỗi hệ thống'));
        }
    } catch (error) {
        console.error('Save job error:', error);
        alert('Lỗi kết nối khi gửi tin đăng');
    }
}

/**
 * Lưu Thông Tin Công Ty
 */
async function saveCompanyInfo(e) {
    e.preventDefault();
    const companyId = document.getElementById('companyId').value;
    const data = {
        name: document.getElementById('companyName').value,
        website: document.getElementById('companyWebsite').value,
        industry: document.getElementById('companyIndustry')?.value || '',
        companySize: document.getElementById('companySize')?.value || '',
        description: document.getElementById('companyDescription').value
    };

    const url = companyId ? `/api/companies/${companyId}` : '/api/companies';
    const method = companyId ? 'PUT' : 'POST';

    try {
        const response = await fetchWithAuth(url, { method, body: JSON.stringify(data) });
        if (response.ok) {
            alert('Cập nhật thông tin công ty thành công!');
            // Không reload ngay lập tức để tránh tranh chấp với alert
            setTimeout(() => location.reload(), 100);
        } else {
            const errData = await response.json();
            alert('Lỗi: ' + (errData.message || 'Lỗi hệ thống'));
        }
    } catch (error) {
        console.error('Save company error:', error);
        // Kiểm tra xem thực tế server có phản hồi thành công không trước khi báo lỗi kết nối
        alert('Có lỗi xảy ra khi lưu thông tin. Hãy kiểm tra kết nối.');
    }
}

/**
 * Nạp danh sách tin đã ứng tuyển (Candidate)
 */
async function loadMyApplications() {
    const container = document.getElementById('applications-container');
    if (!container) return;

    try {
        const response = await fetchWithAuth('/api/applications/my-applications');
        const applications = await response.json();

        if (applications.length === 0) {
            container.innerHTML = '<div class="alert alert-info">Bạn chưa ứng tuyển công việc nào.</div>';
            return;
        }

        container.innerHTML = applications.map(app => `
            <div class="card mb-3 shadow-sm border-0">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h5 class="card-title fw-bold text-primary">${app.jobTitle}</h5>
                            <p class="card-text text-muted mb-1">${app.companyName}</p>
                            <small class="text-muted">Ngày nộp: ${new Date(app.appliedAt).toLocaleDateString()}</small>
                        </div>
                        <span class="badge ${getStatusBadgeClass(app.status)}">${app.status}</span>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Load applications error:', error);
    }
}

function getStatusBadgeClass(status) {
    switch (status) {
        case 'APPLIED': return 'bg-warning text-dark';
        case 'SHORTLISTED': return 'bg-info';
        case 'INTERVIEWING': return 'bg-primary';
        case 'OFFERED': return 'bg-success';
        case 'REJECTED': return 'bg-danger';
        default: return 'bg-secondary';
    }
}

/**
 * Cập nhật trạng thái đơn ứng tuyển (Recruiter)
 */
async function updateApplicationStatus(applicationId, status) {
    try {
        const response = await fetchWithAuth(`/api/applications/${applicationId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });

        if (response.ok) {
            alert('Cập nhật trạng thái thành công!');
        } else {
            const errData = await response.json();
            alert('Lỗi: ' + (errData.message || 'Không thể cập nhật trạng thái'));
            location.reload();
        }
    } catch (error) {
        console.error('Update status error:', error);
        alert('Có lỗi xảy ra khi cập nhật trạng thái');
        location.reload();
    }
}

/**
 * Lưu hồ sơ ứng viên
 */
async function saveCandidateProfile(e) {
    e.preventDefault();
    console.log("Đang bắt đầu lưu hồ sơ...");

    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalBtnText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Đang lưu...';

    let resumeUrl = null;
    const cvFile = document.getElementById('edit-cv-file')?.files[0];
    if (cvFile) {
        const formData = new FormData();
        formData.append('file', cvFile);
        const token = localStorage.getItem('jwt_token');
        try {
            const uploadRes = await fetch('/api/files/upload', {
                method: 'POST',
                headers: token ? { 'Authorization': 'Bearer ' + token } : {},
                body: formData
            });
            if (uploadRes.ok) {
                const uploadData = await uploadRes.json();
                resumeUrl = uploadData.url;
            } else {
                let errMsg = 'Tải CV lên thất bại. Vui lòng thử lại.';
                try {
                    const errObj = await uploadRes.json();
                    if (errObj.error) errMsg += ` Lỗi: ${errObj.error}`;
                } catch(e) {}
                alert(errMsg);
                submitBtn.disabled = false;
                submitBtn.textContent = originalBtnText;
                return;
            }
        } catch (e) {
            console.error('Lỗi upload file:', e);
            alert('Lỗi kết nối khi tải CV.');
            submitBtn.disabled = false;
            submitBtn.textContent = originalBtnText;
            return;
        }
    }

    const data = {
        fullName: document.getElementById('edit-fullName').value,
        headline: document.getElementById('edit-headline').value,
        phone: document.getElementById('edit-phone').value,
        address: document.getElementById('edit-address').value,
        summary: document.getElementById('edit-summary').value
    };
    
    if (resumeUrl) {
        data.resumeUrl = resumeUrl;
    }

    try {
        const response = await fetchWithAuth('/api/candidate/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Cập nhật hồ sơ thành công!');
            // Đóng modal
            const modalElement = document.getElementById('editProfileModal');
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) modal.hide();

            location.reload();
        } else {
            let errorMessage = 'Không thể cập nhật hồ sơ';
            try {
                const errData = await response.json();
                errorMessage = errData.message || errorMessage;
            } catch (e) { }
            alert('Lỗi: ' + errorMessage);
        }
    } catch (error) {
        console.error('Save profile error:', error);
        alert('Lỗi kết nối khi cập nhật hồ sơ. Vui lòng thử lại sau.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = originalBtnText;
    }
}

/**
 * Tạo hồ sơ ứng viên mới
 */
async function createCandidateProfile(e) {
    e.preventDefault();
    console.log("Đang bắt đầu tạo hồ sơ...");

    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalBtnText = submitBtn.textContent;
    submitBtn.disabled = true;
    submitBtn.textContent = 'Đang tạo...';

    let resumeUrl = null;
    const cvFile = document.getElementById('create-cv-file')?.files[0];
    if (cvFile) {
        const formData = new FormData();
        formData.append('file', cvFile);
        const token = localStorage.getItem('jwt_token');
        try {
            const uploadRes = await fetch('/api/files/upload', {
                method: 'POST',
                headers: token ? { 'Authorization': 'Bearer ' + token } : {},
                body: formData
            });
            if (uploadRes.ok) {
                const uploadData = await uploadRes.json();
                resumeUrl = uploadData.url;
            } else {
                let errMsg = 'Tải CV lên thất bại. Vui lòng thử lại.';
                try {
                    const errObj = await uploadRes.json();
                    if (errObj.error) errMsg += ` Lỗi: ${errObj.error}`;
                } catch(e) {}
                alert(errMsg);
                submitBtn.disabled = false;
                submitBtn.textContent = originalBtnText;
                return;
            }
        } catch (e) {
            console.error('Lỗi upload file:', e);
            alert('Lỗi kết nối khi tải CV.');
            submitBtn.disabled = false;
            submitBtn.textContent = originalBtnText;
            return;
        }
    }

    const data = {
        fullName: document.getElementById('create-fullName').value,
        headline: document.getElementById('create-headline').value,
        phone: document.getElementById('create-phone').value,
        address: document.getElementById('create-address').value,
        summary: document.getElementById('create-summary').value,
        resumeUrl: resumeUrl
    };

    try {
        const response = await fetchWithAuth('/api/candidate/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Tạo hồ sơ thành công!');
            // Đóng modal
            const modalElement = document.getElementById('createProfileModal');
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) modal.hide();

            location.reload();
        } else {
            let errorMessage = 'Không thể tạo hồ sơ';
            try {
                const errData = await response.json();
                errorMessage = errData.message || errorMessage;
            } catch (e) { }
            alert('Lỗi: ' + errorMessage);
        }
    } catch (error) {
        console.error('Create profile error:', error);
        alert('Lỗi kết nối khi tạo hồ sơ. Vui lòng thử lại sau.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = originalBtnText;
    }
}


/**
 * Nạp danh sách việc làm (Trang chủ) - AJAX Search
 */
let searchTimeout = null;

async function loadAllJobs(title = '', location = '', fieldId = '') {
    const container = document.getElementById('jobs-list-container');
    if (!container) return;

    // Show loading state
    container.innerHTML = `
        <div class="col-12 text-center py-5">
            <div class="spinner-border spinner-primary mb-3" role="status" style="width: 3rem; height: 3rem;"></div>
            <p class="text-muted">Đang tìm kiếm việc làm phù hợp...</p>
        </div>`;

    let url = '/api/jobs/search';
    const params = new URLSearchParams();
    if (title) params.append('title', title);
    if (location) params.append('location', location);
    if (fieldId) params.append('fieldId', fieldId);
    if (params.toString()) url += '?' + params.toString();

    try {
        const response = await fetch(url);
        const jobs = await response.json();

        if (jobs.length === 0) {
            container.innerHTML = `
                <div class="col-12 text-center py-5">
                    <div class="mb-4">
                        <i class="fas fa-search fa-4x text-muted opacity-50"></i>
                    </div>
                    <h5 class="text-muted mb-2">Không tìm thấy việc làm</h5>
                    <p class="text-muted mb-0">Thử thay đổi từ khóa tìm kiếm hoặc xem tất cả việc làm.</p>
                    <button onclick="loadAllJobs()" class="btn btn-outline-primary mt-3">
                        <i class="fas fa-refresh me-2"></i>Xem tất cả
                    </button>
                </div>`;
            return;
        }

        container.innerHTML = `
            <div class="col-12 mb-3">
                <p class="text-muted mb-0">
                    <i class="fas fa-check-circle text-success me-1"></i>
                    Tìm thấy <strong>${jobs.length}</strong> việc làm phù hợp
                </p>
            </div>
            ${jobs.map(job => `
            <div class="col-md-6 col-lg-4">
                <div class="card job-card h-100 border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <div class="flex-grow-1">
                                <h5 class="card-title text-primary fw-bold mb-1">${job.title}</h5>
                                <h6 class="card-subtitle text-muted">${job.companyName}</h6>
                            </div>
                            <span class="badge bg-light text-dark border px-3 py-2 flex-shrink-0">${job.jobType}</span>
                        </div>
                        
                        <div class="mb-3">
                            <div class="d-flex align-items-center text-muted mb-2">
                                <i class="fas fa-map-marker-alt me-2 text-primary"></i>
                                <small>${job.location || 'Nơi làm việc'}</small>
                            </div>
                            ${job.jobFieldName ? `
                            <div class="d-flex align-items-center text-muted mb-2">
                                <i class="fas fa-briefcase me-2 text-primary"></i>
                                <small>${job.jobFieldName}</small>
                            </div>
                            ` : ''}
                            <div class="d-flex align-items-center text-muted">
                                <i class="fas fa-money-bill-wave me-2 text-primary"></i>
                                <small>${job.salaryMin ? job.salaryMin + ' - ' + job.salaryMax : 'Thỏa thuận'}</small>
                            </div>
                        </div>
                        
                        <p class="card-text text-muted text-truncate-2 small">${job.description || 'Mô tả công việc'}</p>
                    </div>
                    <div class="card-footer bg-transparent border-0 pt-0">
                        <div class="d-flex gap-2">
                            <button onclick="toggleSaveJob(${job.id}, this)" class="btn btn-outline-warning btn-save btn-save-job" title="Lưu tin">
                                <i class="far fa-bookmark"></i>
                            </button>
                            <a href="/jobs/${job.id}" class="btn btn-outline-primary flex-grow-1">
                                <i class="fas fa-eye me-2"></i>Chi tiết
                            </a>
                            <button onclick="applyForJob(${job.id})" class="btn btn-primary flex-grow-1 btn-apply-job">
                                <i class="fas fa-paper-plane me-2"></i>Ứng tuyển
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `).join('')}`;

        // Apply role-based permissions after rendering
        if (typeof applyRoleBasedPermissions === 'function') {
            applyRoleBasedPermissions();
        }
    } catch (error) {
        console.error('Load all jobs error:', error);
        container.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="fas fa-exclamation-triangle fa-4x text-danger mb-3"></i>
                <h5 class="text-danger">Đã xảy ra lỗi</h5>
                <p class="text-muted">Không thể tải danh sách việc làm. Vui lòng thử lại.</p>
                <button onclick="loadAllJobs()" class="btn btn-primary">
                    <i class="fas fa-refresh me-2"></i>Thử lại
                </button>
            </div>`;
    }
}

/**
 * Debounced search - tìm kiếm tự động sau khi ngừng gõ
 */
function debouncedSearch() {
    if (searchTimeout) {
        clearTimeout(searchTimeout);
    }
    searchTimeout = setTimeout(() => {
        const title = document.getElementById('searchTitle')?.value || '';
        const field = document.getElementById('searchField')?.value || '';
        const location = document.getElementById('searchLocation')?.value || '';
        loadAllJobs(title, location, field);
    }, 500); // 500ms debounce
}

// ==================== INTERVIEW FUNCTIONS ====================

/**
 * Nạp danh sách phỏng vấn (Recruiter)
 */
async function loadInterviews() {
    const container = document.getElementById('interviews-container');
    if (!container) return;

    try {
        const response = await fetchWithAuth('/api/interviews/my');
        const interviews = await response.json();

        if (interviews.length === 0) {
            container.innerHTML = '<tr><td colspan="6" class="text-center py-5"><p class="text-muted">Chưa có lịch phỏng vấn nào.</p></td></tr>';
            return;
        }

        container.innerHTML = interviews.map(interview => `
            <tr>
                <td class="ps-4">
                    <h6 class="mb-0 fw-bold">${interview.candidateName}</h6>
                    <small class="text-muted">${interview.candidateEmail}</small>
                </td>
                <td>${interview.jobTitle}</td>
                <td><span class="text-primary fw-bold">${formatDateTime(interview.scheduledAt)}</span></td>
                <td><span class="badge bg-light text-dark border">${interview.type}</span></td>
                <td><span class="badge ${getInterviewResultBadge(interview.result)}">${interview.result}</span></td>
                <td class="text-center pe-4">
                    <div class="btn-group">
                        <button onclick="openInterviewDetail(${interview.id})" class="btn btn-sm btn-outline-secondary" title="Chi tiết"><i class="fas fa-info-circle"></i></button>
                        <button onclick="cancelInterview(${interview.id})" class="btn btn-sm btn-outline-danger" title="Hủy bỏ"><i class="fas fa-trash"></i></button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Load interviews error:', error);
    }
}

function getInterviewResultBadge(result) {
    switch (result) {
        case 'PASSED': return 'bg-success';
        case 'FAILED': return 'bg-danger';
        case 'PENDING': return 'bg-warning text-dark';
        default: return 'bg-secondary';
    }
}

function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '';
    const date = new Date(dateTimeStr);
    return date.toLocaleString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Mở modal chi tiết phỏng vấn
 */
async function openInterviewDetail(interviewId) {
    try {
        const response = await fetchWithAuth(`/api/interviews/${interviewId}`);
        if (response.ok) {
            const interview = await response.json();
            alert(`Chi tiết phỏng vấn:\n\nỨng viên: ${interview.candidateName}\nVị trí: ${interview.jobTitle}\nThời gian: ${formatDateTime(interview.scheduledAt)}\nLoại: ${interview.type}\nĐịa điểm/Link: ${interview.locationOrLink}\nGhi chú: ${interview.interviewerNote || 'Không có'}`);
        }
    } catch (error) {
        console.error('Open interview detail error:', error);
    }
}

/**
 * Hủy bỏ phỏng vấn
 */
async function cancelInterview(interviewId) {
    if (!confirm('Bạn có chắc chắn muốn hủy lịch phỏng vấn này?')) return;

    try {
        const response = await fetchWithAuth(`/api/interviews/${interviewId}`, { method: 'DELETE' });
        if (response.ok) {
            alert('Đã hủy lịch phỏng vấn!');
            loadInterviews();
        } else {
            alert('Không thể hủy lịch phỏng vấn');
        }
    } catch (error) {
        console.error('Cancel interview error:', error);
    }
}

/**
 * Tạo lịch phỏng vấn mới
 */
async function scheduleInterview(e) {
    e.preventDefault();

    const data = {
        applicationId: document.getElementById('interviewApplicationId').value,
        scheduledAt: document.getElementById('interviewDateTime').value,
        type: document.getElementById('interviewType').value,
        locationOrLink: document.getElementById('interviewLocation').value,
        interviewerNote: document.getElementById('interviewNote').value
    };

    try {
        const response = await fetchWithAuth('/api/interviews', {
            method: 'POST',
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Tạo lịch phỏng vấn thành công!');
            const modal = bootstrap.Modal.getInstance(document.getElementById('scheduleInterviewModal'));
            if (modal) modal.hide();
            loadInterviews();
        } else {
            const errData = await response.json();
            alert('Lỗi: ' + (errData.message || 'Không thể tạo lịch phỏng vấn'));
        }
    } catch (error) {
        console.error('Schedule interview error:', error);
        alert('Lỗi kết nối khi tạo lịch phỏng vấn');
    }
}

/**
 * Cập nhật kết quả phỏng vấn
 */
async function updateInterviewResult(interviewId, result) {
    const note = prompt('Nhập ghi chú (tùy chọn):');

    try {
        const response = await fetchWithAuth(`/api/interviews/${interviewId}/result?result=${result}&note=${encodeURIComponent(note || '')}`, {
            method: 'PATCH'
        });

        if (response.ok) {
            alert('Cập nhật kết quả thành công!');
            loadInterviews();
        } else {
            alert('Không thể cập nhật kết quả');
        }
    } catch (error) {
        console.error('Update interview result error:', error);
    }
}

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', function () {
    const jobForm = document.getElementById('jobPostingForm');
    if (jobForm) jobForm.addEventListener('submit', saveJobPosting);

    const companyForm = document.getElementById('companyForm');
    if (companyForm) companyForm.addEventListener('submit', saveCompanyInfo);

    const profileForm = document.getElementById('candidateProfileForm');
    if (profileForm) profileForm.addEventListener('submit', saveCandidateProfile);

    const createProfileForm = document.getElementById('createProfileForm');
    if (createProfileForm) createProfileForm.addEventListener('submit', createCandidateProfile);

    const interviewForm = document.getElementById('scheduleInterviewForm');
    if (interviewForm) interviewForm.addEventListener('submit', scheduleInterview);

    const applyBtn = document.getElementById('btn-apply-job');
    if (applyBtn) {
        applyBtn.addEventListener('click', function () {
            const jobId = this.getAttribute('data-job-id');
            applyForJob(jobId);
        });
    }

    // Load data for different pages
    if (document.getElementById('applications-container')) loadMyApplications();
    if (document.getElementById('my-jobs-container')) loadMyJobs();
    if (document.getElementById('interviews-container')) loadInterviews();
});
