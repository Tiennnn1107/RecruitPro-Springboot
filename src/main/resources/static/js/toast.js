/**
 * Toast Notification System for J2PP
 * Provides beautiful, animated toast notifications
 */

const Toast = {
    container: null,
    
    init() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            this.container.id = 'toast-container';
            document.body.appendChild(this.container);
        }
    },
    
    show(type, title, message, duration = 3000) {
        this.init();
        
        const icons = {
            success: 'fas fa-check-circle',
            error: 'fas fa-times-circle',
            warning: 'fas fa-exclamation-triangle',
            info: 'fas fa-info-circle'
        };
        
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <div class="d-flex align-items-center p-3">
                <div class="toast-icon">
                    <i class="${icons[type] || icons.info}"></i>
                </div>
                <div class="flex-grow-1">
                    <div class="toast-title">${title}</div>
                    <div class="toast-message">${message}</div>
                </div>
                <button type="button" class="btn-close btn-close-white ms-3" onclick="Toast.hide(this.parentElement.parentElement)"></button>
            </div>
            <div class="toast-progress"></div>
        `;
        
        this.container.appendChild(toast);
        
        // Auto-hide after duration
        setTimeout(() => {
            this.hide(toast);
        }, duration);
        
        return toast;
    },
    
    hide(toast) {
        if (toast && toast.parentElement) {
            toast.classList.add('hiding');
            setTimeout(() => {
                if (toast.parentElement) {
                    toast.parentElement.removeChild(toast);
                }
            }, 400);
        }
    },
    
    success(title, message) {
        return this.show('success', title, message);
    },
    
    error(title, message) {
        return this.show('error', title, message);
    },
    
    warning(title, message) {
        return this.show('warning', title, message);
    },
    
    info(title, message) {
        return this.show('info', title, message);
    }
};

/**
 * Loading Overlay for async operations
 */
const Loading = {
    overlay: null,
    
    init() {
        if (!this.overlay) {
            this.overlay = document.createElement('div');
            this.overlay.className = 'loading-overlay';
            this.overlay.innerHTML = '<div class="loading-spinner"></div>';
            document.body.appendChild(this.overlay);
        }
    },
    
    show() {
        this.init();
        this.overlay.classList.add('active');
    },
    
    hide() {
        if (this.overlay) {
            this.overlay.classList.remove('active');
        }
    }
};

/**
 * Empty State Generator
 */
const EmptyState = {
    create(icon, title, message, actionText = null, actionUrl = null) {
        let actionHtml = '';
        if (actionText && actionUrl) {
            actionHtml = `<a href="${actionUrl}" class="btn btn-primary">${actionText}</a>`;
        } else if (actionText) {
            actionHtml = `<button class="btn btn-primary" onclick="${actionText}">${actionText}</button>`;
        }
        
        return `
            <div class="empty-state">
                <div class="empty-state-icon">
                    <i class="${icon}"></i>
                </div>
                <h3 class="empty-state-title">${title}</h3>
                <p class="empty-state-message">${message}</p>
                ${actionHtml}
            </div>
        `;
    }
};

/**
 * Pagination Generator
 */
const Pagination = {
    render(currentPage, totalPages, onPageChange) {
        if (totalPages <= 1) return '';
        
        let html = '<div class="pagination-container"><ul class="pagination">';
        
        // Previous button
        const prevDisabled = currentPage === 1 ? 'disabled' : '';
        html += `<li class="page-item ${prevDisabled}">
            <a class="page-link" href="#" onclick="Pagination.go(${currentPage - 1}); return false;">
                <i class="fas fa-chevron-left"></i>
            </a>
        </li>`;
        
        // Page numbers
        const maxVisible = 5;
        let startPage = Math.max(1, currentPage - Math.floor(maxVisible / 2));
        let endPage = Math.min(totalPages, startPage + maxVisible - 1);
        
        if (endPage - startPage < maxVisible - 1) {
            startPage = Math.max(1, endPage - maxVisible + 1);
        }
        
        if (startPage > 1) {
            html += `<li class="page-item"><a class="page-link" href="#" onclick="Pagination.go(1); return false;">1</a></li>`;
            if (startPage > 2) {
                html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }
        
        for (let i = startPage; i <= endPage; i++) {
            const active = i === currentPage ? 'active' : '';
            html += `<li class="page-item ${active}">
                <a class="page-link" href="#" onclick="Pagination.go(${i}); return false;">${i}</a>
            </li>`;
        }
        
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            html += `<li class="page-item"><a class="page-link" href="#" onclick="Pagination.go(${totalPages}); return false;">${totalPages}</a></li>`;
        }
        
        // Next button
        const nextDisabled = currentPage === totalPages ? 'disabled' : '';
        html += `<li class="page-item ${nextDisabled}">
            <a class="page-link" href="#" onclick="Pagination.go(${currentPage + 1}); return false;">
                <i class="fas fa-chevron-right"></i>
            </a>
        </li>`;
        
        html += '</ul></div>';
        
        // Store callback
        if (onPageChange) {
            Pagination.currentCallback = onPageChange;
        }
        
        return html;
    },
    
    go(page) {
        if (this.currentCallback) {
            this.currentCallback(page);
        }
    }
};

/**
 * Skeleton Loader for content
 */
const Skeleton = {
    jobCard() {
        return `
            <div class="card border-0 shadow-sm mb-3 p-4">
                <div class="d-flex">
                    <div class="skeleton skeleton-image me-3" style="width: 60px; height: 60px;"></div>
                    <div class="flex-grow-1">
                        <div class="skeleton skeleton-title"></div>
                        <div class="skeleton skeleton-text" style="width: 40%;"></div>
                        <div class="skeleton skeleton-text" style="width: 70%;"></div>
                    </div>
                </div>
            </div>
        `;
    },
    
    tableRow(count = 5) {
        let html = '';
        for (let i = 0; i < count; i++) {
            html += `
                <tr>
                    <td><div class="skeleton skeleton-text" style="width: 200px;"></div></td>
                    <td><div class="skeleton skeleton-text" style="width: 100px;"></div></td>
                    <td><div class="skeleton skeleton-text" style="width: 80px;"></div></td>
                    <td><div class="skeleton skeleton-text" style="width: 80px;"></div></td>
                </tr>
            `;
        }
        return html;
    }
};

// Initialize toast system when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    Toast.init();
    
    // Show any flash messages from server (if using session)
    const urlParams = new URLSearchParams(window.location.search);
    const success = urlParams.get('success');
    const error = urlParams.get('error');
    
    if (success) {
        Toast.success('Thành công', decodeURIComponent(success));
    }
    if (error) {
        Toast.error('Lỗi', decodeURIComponent(error));
    }
});

// Export for global use
window.Toast = Toast;
window.Loading = Loading;
window.EmptyState = EmptyState;
window.Pagination = Pagination;
window.Skeleton = Skeleton;