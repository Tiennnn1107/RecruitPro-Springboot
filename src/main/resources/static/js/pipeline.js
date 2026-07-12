// pipeline.js - Enhanced Pipeline Kanban with Quick View Sidebar

let searchTimeout = null;
let draggedAppId = null;
let currentQVAppId = null;

document.addEventListener('DOMContentLoaded', function() {
    initPipelineDragDrop();
    initQuickViewEvents();
});

function initQuickViewEvents() {
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') closeQuickView();
    });
}

function initPipelineDragDrop() {
    const columns = document.querySelectorAll('.pipeline-column .card-body');
    
    columns.forEach(column => {
        column.addEventListener('dragover', handleDragOver);
        column.addEventListener('dragleave', handleDragLeave);
        column.addEventListener('drop', handleDrop);
    });

    const draggableApps = document.querySelectorAll('.app-card');
    draggableApps.forEach(card => {
        card.addEventListener('dragstart', handleDragStart);
        card.addEventListener('dragend', handleDragEnd);
        card.addEventListener('click', handleCardClick);
    });
}

function handleDragStart(e) {
    const appId = e.currentTarget.getAttribute('data-id');
    const status = e.currentTarget.getAttribute('data-status');

    // Không cho kéo ứng viên đã ở trạng thái cuối cùng (OFFERED/REJECTED)
    if (status === 'OFFERED' || status === 'REJECTED') {
        e.preventDefault();
        showToast('Ứng viên đã có kết quả tuyển dụng, không thể di chuyển', 'error');
        return;
    }

    draggedAppId = appId;
    e.dataTransfer.setData('applicationId', appId);
    e.currentTarget.classList.add('dragging');
}

function handleDragEnd(e) {
    e.currentTarget.classList.remove('dragging');
    document.querySelectorAll('.drag-over').forEach(el => el.classList.remove('drag-over'));
}

function handleDragOver(e) {
    e.preventDefault();
    e.currentTarget.classList.add('drag-over');
}

function handleDragLeave(e) {
    e.currentTarget.classList.remove('drag-over');
}

function handleDrop(e) {
    e.preventDefault();
    e.currentTarget.classList.remove('drag-over');

    const appId = e.dataTransfer.getData('applicationId');
    const newStatus = getStatusFromColumnId(e.currentTarget.id);
    const currentStatus = document.querySelector(`[data-id="${appId}"]`)?.getAttribute('data-status');

    if (!appId || !newStatus) return;

    // Không cho kéo vào cột kết quả từ các cột trước đó (chỉ drag được từ cột phỏng vấn)
    if (newStatus === 'OFFERED' && currentStatus !== 'INTERVIEWING') {
        showToast('Chỉ có thể chuyển vào Kết quả từ cột Phỏng vấn', 'error');
        return;
    }

    if (currentStatus !== newStatus) {
        moveApplication(appId, newStatus);
    }
}

function handleCardClick(e) {
    if (e.target.closest('a')) return;
    
    const btn = e.target.closest('button');
    if (btn) {
        const onclick = btn.getAttribute('onclick');
        if (onclick && onclick.includes('openQuickView')) {
            return;
        }
        if (onclick && onclick.includes('scheduleInterviewQuick')) {
            return;
        }
        if (onclick && onclick.includes('rejectApplication')) {
            return;
        }
        if (onclick && onclick.includes('markAsOffered')) {
            return;
        }
    }

    const appId = e.currentTarget.getAttribute('data-id');
    openQuickView(appId);
}

function getStatusFromColumnId(columnId) {
    switch (columnId) {
        case 'column-pending': return 'APPLIED';
        case 'column-reviewing': return 'SHORTLISTED';
        case 'column-interviewing': return 'INTERVIEWING';
        case 'column-final': return 'OFFERED';
        default: return null;
    }
}

function getColumnIdFromStatus(status) {
    switch (status) {
        case 'APPLIED': return 'column-pending';
        case 'SHORTLISTED': return 'column-reviewing';
        case 'INTERVIEWING': return 'column-interviewing';
        case 'OFFERED':
        case 'REJECTED': return 'column-final';
        default: return null;
    }
}

async function openQuickView(applicationId) {
    console.log('openQuickView called with ID:', applicationId);
    currentQVAppId = applicationId;
    
    const overlay = document.getElementById('qvOverlay');
    const panel = document.getElementById('qvPanel');
    const body = document.getElementById('qvBody');
    const detailLink = document.getElementById('qvDetailLink');
    
    console.log('overlay:', overlay);
    console.log('panel:', panel);
    console.log('body:', body);
    
    detailLink.href = `/recruiter/applications/${applicationId}`;
    
    body.innerHTML = `
        <div class="qv-loading">
            <div class="spinner-border text-primary" role="status"></div>
        </div>
    `;
    
    overlay.classList.add('active');
    panel.classList.add('active');
    document.body.style.overflow = 'hidden';
    
    try {
        const response = await fetchWithAuth(`/api/applications/${applicationId}/detail`);
        if (response.ok) {
            const data = await response.json();
            renderQuickViewContent(data);
        } else {
            body.innerHTML = `<div class="text-center text-danger py-5"><i class="fas fa-exclamation-triangle fa-2x mb-3"></i><p>Không thể tải thông tin</p></div>`;
        }
    } catch (error) {
        console.error('Quick view error:', error);
        body.innerHTML = `<div class="text-center text-danger py-5"><i class="fas fa-exclamation-triangle fa-2x mb-3"></i><p>Có lỗi xảy ra</p></div>`;
    }
}

function renderQuickViewContent(data) {
    const body = document.getElementById('qvBody');
    const actions = document.getElementById('qvActions');
    
    // Store app ID for later use (e.g., in schedule modal)
    let appIdInput = document.getElementById('interviewApplicationId');
    if (!appIdInput) {
        appIdInput = document.createElement('input');
        appIdInput.type = 'hidden';
        appIdInput.id = 'interviewApplicationId';
        body.appendChild(appIdInput);
    }
    appIdInput.value = data.id;
    
    const initials = data.candidateName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    const avatarHtml = data.candidateAvatarUrl 
        ? `<img src="${escapeHtml(data.candidateAvatarUrl)}" alt="${escapeHtml(data.candidateName)}">`
        : initials;
    
    const skillsHtml = data.skills && data.skills.length > 0
        ? data.skills.map(s => `<span class="qv-skill-badge">${escapeHtml(s.skillName)}</span>`).join('')
        : '<span class="text-muted">Chưa cập nhật</span>';
    
    const expHtml = data.experiences && data.experiences.length > 0
        ? data.experiences.map(e => `
            <div class="qv-exp-item">
                <div class="position">${escapeHtml(e.position)}</div>
                <div class="company">${escapeHtml(e.companyName)}</div>
                <div class="dates">${formatDate(e.startDate)} - ${e.endDate ? formatDate(e.endDate) : 'Hiện tại'}</div>
            </div>
        `).join('')
        : '<span class="text-muted">Chưa cập nhật kinh nghiệm</span>';
    
    const coverLetterHtml = data.coverLetter 
        ? `<div class="qv-cover-letter">${escapeHtml(data.coverLetter)}</div>`
        : '<span class="text-muted">Không có thư giới thiệu</span>';
    
    const resumeHtml = data.resumeUrl
        ? `<a href="${escapeHtml(data.resumeUrl)}" target="_blank" class="qv-resume-link"><i class="fas fa-file-pdf me-2"></i>Xem CV</a>`
        : '<span class="text-muted">Chưa có CV</span>';
    
    body.innerHTML = `
        <div class="text-center mb-4">
            <div class="qv-candidate-avatar">${avatarHtml}</div>
            <h4 class="fw-bold mb-1">${escapeHtml(data.candidateName)}</h4>
            <p class="text-muted mb-1">${escapeHtml(data.candidateHeadline || 'Ứng viên')}</p>
            <span class="badge ${getStatusBadgeClass(data.status)}">${getStatusLabel(data.status)}</span>
        </div>
        
        <div class="qv-section">
            <div class="qv-section-title">Liên hệ</div>
            <div class="qv-info-row">
                <i class="fas fa-envelope"></i>
                <span>${escapeHtml(data.candidateEmail)}</span>
            </div>
            <div class="qv-info-row">
                <i class="fas fa-phone"></i>
                <span>${data.candidatePhone || '<span class="text-muted">Chưa cập nhật</span>'}</span>
            </div>
            <div class="qv-info-row">
                <i class="fas fa-map-marker-alt"></i>
                <span>${data.candidateAddress ? escapeHtml(data.candidateAddress) : '<span class="text-muted">Chưa cập nhật</span>'}</span>
            </div>
        </div>
        
        <div class="qv-section">
            <div class="qv-section-title">Vị trí ứng tuyển</div>
            <div class="qv-info-row">
                <i class="fas fa-briefcase"></i>
                <span class="fw-medium">${escapeHtml(data.jobTitle)}</span>
            </div>
            <div class="qv-info-row">
                <i class="fas fa-building"></i>
                <span>${escapeHtml(data.companyName)}</span>
            </div>
            <div class="qv-info-row">
                <i class="fas fa-clock"></i>
                <span>Ứng tuyển ${data.daysSinceApplied === 0 ? 'hôm nay' : Math.abs(data.daysSinceApplied) + ' ngày'}</span>
            </div>
            ${data.hasInterview ? `
            <div class="qv-info-row">
                <i class="fas fa-calendar-check text-success"></i>
                <span class="text-success fw-medium">Đã có lịch phỏng vấn</span>
            </div>
            ` : ''}
        </div>
        
        <div class="qv-section">
            <div class="qv-section-title">Kỹ năng</div>
            <div>${skillsHtml}</div>
        </div>
        
        <div class="qv-section">
            <div class="qv-section-title">Kinh nghiệm</div>
            ${expHtml}
        </div>
        
        <div class="qv-section">
            <div class="qv-section-title">Thư giới thiệu</div>
            ${coverLetterHtml}
        </div>
        
        <div class="qv-section">
            <div class="qv-section-title">CV</div>
            ${resumeHtml}
        </div>
    `;

    // Build action buttons based on application status
    let actionButtons = `<a href="/recruiter/applications/${data.id}" class="btn btn-primary"><i class="fas fa-external-link-alt me-2"></i>Xem chi tiết</a>`;

    if (data.status === 'OFFERED' || data.status === 'REJECTED') {
        // Final status - no actions available
        actionButtons += `<button disabled class="btn btn-secondary"><i class="fas fa-check me-2"></i>Đã có kết quả</button>`;
    } else if (data.hasInterview) {
        // Already has interview - show offer/reject buttons for INTERVIEWING
        if (data.status === 'INTERVIEWING') {
            actionButtons += `<button onclick="markAsOffered(${data.id})" class="btn btn-success"><i class="fas fa-check-double me-2"></i>Chốt Offer</button>`;
        }
        actionButtons += `<button onclick="rejectFromQV()" class="btn btn-outline-danger"><i class="fas fa-times me-2"></i>Từ chối</button>`;
    } else {
        // No interview yet - show schedule button for APPLIED/SHORTLISTED
        if (data.status === 'APPLIED' || data.status === 'SHORTLISTED') {
            actionButtons += `<button onclick="scheduleInterviewFromQV()" class="btn btn-success"><i class="fas fa-calendar-plus me-2"></i>Lên lịch</button>`;
        }
        if (data.status === 'INTERVIEWING') {
            actionButtons += `<button onclick="markAsOffered(${data.id})" class="btn btn-success"><i class="fas fa-check-double me-2"></i>Chốt Offer</button>`;
        }
        actionButtons += `<button onclick="rejectFromQV()" class="btn btn-outline-danger"><i class="fas fa-times me-2"></i>Từ chối</button>`;
    }

    actions.innerHTML = actionButtons;
}

function closeQuickView() {
    console.log('closeQuickView called');
    const overlay = document.getElementById('qvOverlay');
    const panel = document.getElementById('qvPanel');
    
    overlay.classList.remove('active');
    panel.classList.remove('active');
    document.body.style.overflow = '';
    currentQVAppId = null;
}

function scheduleInterviewFromQV() {
    if (!currentQVAppId) return;

    const appCard = document.querySelector(`[data-id="${currentQVAppId}"]`);
    const status = appCard?.getAttribute('data-status');

    // Chỉ được lên lịch cho APPLIED hoặc SHORTLISTED
    if (status !== 'APPLIED' && status !== 'SHORTLISTED') {
        showToast('Chỉ có thể lên lịch phỏng vấn cho ứng viên đang ở trạng thái Mới hoặc Xem xét', 'error');
        return;
    }

    // Kiểm tra đã có lịch chưa
    if (appCard && appCard.querySelector('.interview-badge')) {
        showToast('Ứng viên này đã có lịch phỏng vấn', 'error');
        return;
    }

    closeQuickView();
    setTimeout(() => scheduleInterviewQuick(currentQVAppId), 300);
}

function rejectFromQV() {
    const appId = document.getElementById('interviewApplicationId')?.value || currentQVAppId;
    const status = document.querySelector(`[data-id="${appId}"]`)?.getAttribute('data-status');

    if (status === 'OFFERED') {
        showToast('Không thể từ chối ứng viên đã được tuyển', 'error');
        closeQuickView();
        return;
    }

    closeQuickView();
    setTimeout(() => {
        if (appId && confirm('Bạn có chắc chắn muốn từ chối ứng viên này?')) {
            if (typeof rejectApplication === "function") {
                rejectApplication(appId);
            }
        }
    }, 300);
}

async function moveApplication(applicationId, newStatus) {
    try {
        const response = await fetchWithAuth(`/api/applications/${applicationId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });

        if (response.ok) {
            showToast('Đã cập nhật trạng thái ứng viên', 'success');
            filterPipeline();
            if (currentQVAppId === applicationId.toString()) {
                closeQuickView();
            }
        } else {
            const error = await response.json();
            showToast('Lỗi: ' + (error.message || 'Không thể cập nhật'), 'error');
        }
    } catch (error) {
        console.error('Move application error:', error);
        showToast('Có lỗi xảy ra', 'error');
    }
}

function debounceSearch() {
    if (searchTimeout) clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        filterPipeline();
    }, 400);
}

async function filterPipeline() {
    const jobId = document.getElementById('jobFilter')?.value || '';
    const search = document.getElementById('searchInput')?.value || '';

    showLoading();

    try {
        let url = '/api/applications/pipeline?';
        const params = new URLSearchParams();
        if (jobId) params.append('jobId', jobId);
        if (search) params.append('search', search);
        url += params.toString();

        const response = await fetchWithAuth(url);
        if (response.ok) {
            const data = await response.json();
            updatePipelineUI(data);
        } else {
            showToast('Không thể tải dữ liệu pipeline', 'error');
        }
    } catch (error) {
        console.error('Filter pipeline error:', error);
        showToast('Có lỗi xảy ra khi lọc dữ liệu', 'error');
    }
}

function showLoading() {
    const container = document.getElementById('pipelineContainer');
    if (!container) return;
    
    const columns = container.querySelectorAll('.pipeline-column');
    columns.forEach(col => {
        const cardBody = col.querySelector('.card-body');
        if (cardBody) {
            cardBody.innerHTML = `
                <div class="text-center py-5">
                    <div class="spinner-border text-primary" role="status"></div>
                    <p class="mt-2 text-muted">Đang tải...</p>
                </div>
            `;
        }
    });
}

function updatePipelineUI(data) {
    updateColumn('column-pending', data.pendingApps, 'pending');
    updateColumn('column-reviewing', data.reviewingApps, 'reviewing');
    updateColumn('column-interviewing', data.interviewingApps, 'interviewing');
    updateColumn('column-final', data.finalApps, 'final');
    
    updateColumnCount('column-pending', data.counts?.pending || 0);
    updateColumnCount('column-reviewing', data.counts?.reviewing || 0);
    updateColumnCount('column-interviewing', data.counts?.interviewing || 0);
    updateColumnCount('column-final', data.counts?.final || 0);

    initPipelineDragDrop();
}

function updateColumn(columnId, apps, type) {
    const column = document.getElementById(columnId);
    if (!column) return;

    if (!apps || apps.length === 0) {
        const icons = { pending: 'inbox', reviewing: 'user-check', interviewing: 'comments', final: 'flag-checkered' };
        const messages = { pending: 'Chưa có ứng viên mới', reviewing: 'Chưa có ứng viên nào được xem xét', interviewing: 'Chưa có ứng viên nào phỏng vấn', final: 'Chưa có kết quả tuyển dụng' };
        column.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-${icons[type]}"></i>
                <p class="mb-0 mt-2">${messages[type]}</p>
            </div>
        `;
        return;
    }

    const statusClassMap = { pending: 'column-pending', reviewing: 'column-reviewing', interviewing: 'column-interviewing', final: 'column-final' };
    const statusClass = statusClassMap[type];

    column.innerHTML = apps.map(app => {
        const daysText = app.daysSinceApplied === 0 ? 'Hôm nay' : `${Math.abs(app.daysSinceApplied)} ngày`;
        const statusBadge = getStatusBadgeHtml(app.status);

        return `
            <div class="card border-0 shadow-sm mb-2 app-card ${statusClass}"
                 data-id="${app.id}" data-status="${app.status}" draggable="true">
                <div class="card-body p-3 position-relative">
                    ${app.hasInterview ? '<div class="interview-badge"><span class="badge bg-success rounded-circle p-2" title="Đã lên lịch phỏng vấn"><i class="fas fa-calendar-check"></i></span></div>' : ''}
                    <div class="d-flex justify-content-between align-items-start mb-1">
                        <h6 class="fw-bold text-dark text-truncate mb-0">${escapeHtml(app.candidateName)}</h6>
                        ${statusBadge}
                    </div>
                    <p class="small text-secondary mb-2 text-truncate">${escapeHtml(app.jobTitle)}</p>
                    <div class="d-flex align-items-center small text-muted mb-3 text-truncate">
                        <i class="fas fa-envelope me-2 opacity-50"></i>
                        <span class="text-truncate">${escapeHtml(app.candidateEmail)}</span>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mt-2 pt-2 border-top">
                        <span class="small fw-medium text-muted"><i class="fas fa-clock me-1"></i>${daysText}</span>
                        <div class="quick-actions">
                            <button class="btn btn-sm btn-light text-primary border" title="Xem nhanh" onclick="event.stopPropagation(); openQuickView(${app.id})"><i class="fas fa-expand"></i></button>
                            ${app.hasInterview ? '' : (type === 'pending' || type === 'reviewing') ? `<button class="btn btn-sm btn-light text-success border" onclick="event.stopPropagation(); scheduleInterviewQuick(${app.id})" title="Lên lịch phỏng vấn"><i class="fas fa-calendar-plus"></i></button>` : ''}
                            ${type === 'interviewing' ? `<button class="btn btn-sm btn-light text-success border" onclick="event.stopPropagation(); markAsOffered(${app.id})" title="Chốt Offer"><i class="fas fa-check-double"></i></button>` : ''}
                            ${app.status !== 'OFFERED' && app.status !== 'REJECTED' ? `<button class="btn btn-sm btn-light text-danger border" onclick="event.stopPropagation(); rejectApplication(${app.id})" title="Từ chối"><i class="fas fa-times"></i></button>` : ''}
                            ${app.status === 'REJECTED' ? `<button class="btn btn-sm btn-light text-success border" onclick="event.stopPropagation(); markAsOffered(${app.id})" title="Chốt Offer"><i class="fas fa-check-double"></i></button>` : ''}
                            ${app.status === 'OFFERED' ? `<button class="btn btn-sm btn-light text-danger border" onclick="event.stopPropagation(); rejectApplication(${app.id})" title="Từ chối"><i class="fas fa-times"></i></button>` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function updateColumnCount(columnId, count) {
    const column = document.querySelector(`#${columnId}`)?.closest('.pipeline-column');
    if (!column) return;
    const badge = column.querySelector('.column-count');
    if (badge) badge.textContent = count;
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', { month: 'short', year: 'numeric' });
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

function getStatusLabel(status) {
    switch (status) {
        case 'APPLIED': return 'Mới ứng tuyển';
        case 'SHORTLISTED': return 'Đang xem xét';
        case 'INTERVIEWING': return 'Phỏng vấn';
        case 'OFFERED': return 'Đã tuyển';
        case 'REJECTED': return 'Từ chối';
        default: return status;
    }
}

function getStatusBadgeHtml(status) {
    const badgeClass = getStatusBadgeClass(status);
    const label = getStatusLabel(status);
    return `<span class="badge ${badgeClass} rounded-pill small flex-shrink-0">${label}</span>`;
}

function scheduleInterviewQuick(applicationId) {
    // Kiểm tra ứng viên đã có lịch phỏng vấn chưa
    const appCard = document.querySelector(`[data-id="${applicationId}"]`);
    if (appCard && appCard.querySelector('.interview-badge')) {
        showToast('Ứng viên này đã có lịch phỏng vấn', 'error');
        return;
    }

    // Kiểm tra trạng thái - chỉ được lên lịch cho APPLIED hoặc SHORTLISTED
    const status = appCard?.getAttribute('data-status');
    if (status !== 'APPLIED' && status !== 'SHORTLISTED') {
        showToast('Chỉ có thể lên lịch phỏng vấn cho ứng viên đang ở trạng thái Mới hoặc Xem xét', 'error');
        return;
    }

    document.getElementById('interviewApplicationId').value = applicationId;
    document.getElementById('interviewDateTime').value = '';
    document.getElementById('interviewType').value = 'ONLINE';
    document.getElementById('interviewLocation').value = '';
    document.getElementById('interviewNote').value = '';

    const modal = new bootstrap.Modal(document.getElementById('scheduleInterviewModal'));
    modal.show();
}

async function submitScheduleInterview(e) {
    e.preventDefault();

    const data = {
        applicationId: document.getElementById('interviewApplicationId').value,
        scheduledAt: document.getElementById('interviewDateTime').value,
        type: document.getElementById('interviewType').value,
        locationOrLink: document.getElementById('interviewLocation').value,
        interviewerNote: document.getElementById('interviewNote').value
    };

    // Validate: thời gian phỏng vấn phải là tương lai
    if (data.scheduledAt) {
        const interviewDate = new Date(data.scheduledAt);
        const now = new Date();
        if (interviewDate <= now) {
            showToast('Thời gian phỏng vấn phải là ngày trong tương lai', 'error');
            return;
        }
    }

    // Validate: nếu là online thì cần có link
    if (data.type === 'ONLINE' && !data.locationOrLink) {
        showToast('Vui lòng nhập link cuộc họp cho cuộc phỏng vấn online', 'error');
        return;
    }

    try {
        const response = await fetchWithAuth('/api/interviews', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showToast('Lên lịch phỏng vấn thành công!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('scheduleInterviewModal'))?.hide();
            
            const statusRes = await fetchWithAuth(`/api/applications/${data.applicationId}/status`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: 'INTERVIEWING' })
            });
            
            filterPipeline();
        } else {
            const err = await response.json();
            showToast('Lỗi: ' + (err.message || 'Không thể lên lịch'), 'error');
        }
    } catch (error) {
        console.error('Schedule interview error:', error);
        showToast('Có lỗi xảy ra khi lên lịch', 'error');
    }
}

async function rejectApplication(applicationId) {
    // Không cho từ chối ứng viên đã được tuyển
    const appCard = document.querySelector(`[data-id="${applicationId}"]`);
    const status = appCard?.getAttribute('data-status');

    if (status === 'OFFERED') {
        showToast('Không thể từ chối ứng viên đã được tuyển', 'error');
        return;
    }

    if (!confirm('Bạn có chắc chắn muốn từ chối ứng viên này?')) return;

    try {
        const response = await fetchWithAuth(`/api/applications/${applicationId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'REJECTED' })
        });

        if (response.ok) {
            showToast('Đã từ chối ứng viên', 'success');
            filterPipeline();
            if (currentQVAppId === applicationId.toString()) {
                closeQuickView();
            }
        } else {
            const err = await response.json();
            showToast('Lỗi: ' + (err.message || 'Không thể từ chối'), 'error');
        }
    } catch (error) {
        console.error('Reject application error:', error);
        showToast('Có lỗi xảy ra', 'error');
    }
}

async function markAsOffered(applicationId) {
    // Chỉ cho phép chốt offer từ cột phỏng vấn hoặc ứng viên đã bị từ chối
    const appCard = document.querySelector(`[data-id="${applicationId}"]`);
    const status = appCard?.getAttribute('data-status');

    if (status !== 'INTERVIEWING' && status !== 'REJECTED') {
        showToast('Chỉ có thể chốt offer cho ứng viên đang trong vòng phỏng vấn hoặc đã bị từ chối', 'error');
        return;
    }

    if (!confirm('Xác nhận chốt offer cho ứng viên này?')) return;

    try {
        const response = await fetchWithAuth(`/api/applications/${applicationId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'OFFERED' })
        });

        if (response.ok) {
            showToast('Đã chốt offer thành công!', 'success');
            filterPipeline();
            if (currentQVAppId === applicationId.toString()) {
                closeQuickView();
            }
        } else {
            const err = await response.json();
            showToast('Lỗi: ' + (err.message || 'Không thể cập nhật'), 'error');
        }
    } catch (error) {
        console.error('Mark as offered error:', error);
        showToast('Có lỗi xảy ra', 'error');
    }
}

function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toastContainer') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'error' ? 'danger' : type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    toastContainer.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast, { delay: 3000 });
    bsToast.show();
    toast.addEventListener('hidden.bs.toast', () => toast.remove());
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    document.body.appendChild(container);
    return container;
}

async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('jwt_token');
    const headers = {
        ...options.headers
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return fetch(url, { ...options, headers });
}