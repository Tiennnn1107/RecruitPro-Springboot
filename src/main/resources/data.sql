-- =====================================================
-- Sample Data for RecruitPro Recruitment System
-- Database: recruitment_db
-- =====================================================

-- Clear existing data (in correct order due to foreign keys)
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM notifications;
DELETE FROM interviews;
DELETE FROM applications;
DELETE FROM job_skills;
DELETE FROM job_postings;
DELETE FROM candidate_profiles;
DELETE FROM recruiter_approval_requests;
DELETE FROM companies;
DELETE FROM blogs;
DELETE FROM users;
DELETE FROM job_fields;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- JOB FIELDS (Ngành nghề)
-- =====================================================
INSERT INTO job_fields (id, name, description, created_at, updated_at) VALUES
(1, 'Công nghệ thông tin', 'Phần mềm, IT, viễn thông, an ninh mạng', NOW(), NOW()),
(2, 'Kế toán - Tài chính', 'Kế toán, kiểm toán, tài chính, ngân hàng', NOW(), NOW()),
(3, 'Marketing - Quảng cáo', 'Marketing, digital marketing, PR, quảng cáo', NOW(), NOW()),
(4, 'Kinh doanh - Bán hàng', 'Kinh doanh, bán hàng, chăm sóc khách hàng', NOW(), NOW()),
(5, 'Thực phẩm - Đồ uống', 'Sản xuất, chế biến, kiểm soát chất lượng thực phẩm', NOW(), NOW()),
(6, 'Nhân sự - Hành chính', 'Nhân sự, hành chính, văn phòng', NOW(), NOW()),
(7, 'Kỹ thuật - Cơ khí', 'Kỹ thuật, cơ khí, điện, điện tử', NOW(), NOW()),
(8, 'Giáo dục - Đào tạo', 'Giáo dục, đào tạo, nghiên cứu', NOW(), NOW()),
(9, 'Y tế - Dược phẩm', 'Y tế, dược phẩm, chăm sóc sức khỏe', NOW(), NOW()),
(10, 'Thiết kế - Sáng tạo', 'Thiết kế đồ họa, nội thất, thời trang', NOW(), NOW()),
(11, 'Khoa học - Nghiên cứu', 'Nghiên cứu, khoa học, phát triển sản phẩm', NOW(), NOW()),
(12, 'Logistics - Vận tải', 'Kho vận, vận tải, xuất nhập khẩu', NOW(), NOW()),
(13, 'Bất động sản - Xây dựng', 'Bất động sản, xây dựng, kiến trúc', NOW(), NOW()),
(14, 'Du lịch - Khách sạn', 'Du lịch, khách sạn, nhà hàng', NOW(), NOW()),
(15, 'Pháp luật - Luật', 'Pháp luật, tư vấn pháp lý, thương mại', NOW(), NOW());

-- =====================================================
-- USERS (6 existing + 5 new recruiters = 11 users)
-- =====================================================
INSERT INTO users (id, email, password, role, is_active, created_at, updated_at) VALUES
(1, 'recruiter@techviet.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'RECRUITER', true, NOW(), NOW()),
(2, 'recruiter2@techviet.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'RECRUITER', true, NOW(), NOW()),
(3, 'candidate@email.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'CANDIDATE', true, NOW(), NOW()),
(4, 'candidate2@email.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'CANDIDATE', true, NOW(), NOW()),
(5, 'candidate3@email.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'CANDIDATE', true, NOW(), NOW()),
(6, 'admin@recruitpro.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'ADMIN', true, NOW(), NOW()),
(7, 'recruiter3@fpt.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'RECRUITER', true, NOW(), NOW()),
(8, 'recruiter4@viettel.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'RECRUITER', true, NOW(), NOW()),
(9, 'recruiter5@vng.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'RECRUITER', true, NOW(), NOW()),
(10, 'recruiter6@shopee.com', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'RECRUITER', true, NOW(), NOW()),
(11, 'recruiter7@maritime.vn', '$2a$10$HqO.tMvexaxjtfdV9pSQqOJCngBlzXFgQj6ou0Rh1cPHT6IwEzxi6', 'RECRUITER', true, NOW(), NOW());

-- Note: Password for all users is 'password123' (BCrypt hashed)

-- =====================================================
-- COMPANIES (7 companies)
-- =====================================================
INSERT INTO companies (id, user_id, name, logo_url, website, industry, company_size, description) VALUES
(1, 1, 'TechViet Solutions', 'https://logo.clearbit.com/techviet.com.vn', 'https://techviet.com.vn', 'Công nghệ thông tin', '201-500', 'TechViet Solutions là công ty hàng đầu về phát triển phần mềm và giải pháp công nghệ tại Việt Nam.'),
(2, 2, 'GreenLife Foods', 'https://logo.clearbit.com/greenlifefoods.vn', 'https://greenlifefoods.vn', 'Thực phẩm & Đồ uống', '51-200', 'GreenLife Foods chuyên sản xuất và phân phối các sản phẩm thực phẩm sạch, hữu cơ.'),
(3, 7, 'FPT Software', 'https://logo.clearbit.com/fptsoftware.com', 'https://fptsoftware.com', 'Công nghệ thông tin', '1000+', 'FPT Software - Đối tác công nghệ hàng đầu của nhiều tập đoàn lớn trên thế giới.'),
(4, 8, 'Viettel Solutions', 'https://logo.clearbit.com/viettel.com.vn', 'https://viettel.com.vn', 'Viễn thông', '1000+', 'Viettel - Tập đoàn viễn thông và công nghệ hàng đầu Việt Nam.'),
(5, 9, 'VNG Corporation', 'https://logo.clearbit.com/vng.com.vn', 'https://vng.com.vn', 'Công nghệ thông tin', '501-1000', 'VNG Corporation - Công ty game và công nghệ hàng đầu Đông Nam Á.'),
(6, 10, 'Shopee Vietnam', 'https://logo.clearbit.com/shopee.vn', 'https://shopee.vn', 'Thương mại điện tử', '1000+', 'Shopee - Nền tảng thương mại điện tử hàng đầu tại Việt Nam.'),
(7, 11, 'Viet Nam Maritime Bank', 'https://logo.clearbit.com/maritimebank.com.vn', 'https://maritimebank.com.vn', 'Tài chính - Ngân hàng', '501-1000', 'Maritime Bank - Ngân hàng thương mại cổ phần hàng hải Việt Nam.');

-- =====================================================
-- CANDIDATE PROFILES
-- =====================================================
INSERT INTO candidate_profiles (id, user_id, full_name, headline, phone, avatar_url, date_of_birth, gender, address, summary) VALUES
(1, 3, 'Nguyễn Văn Minh', 'Senior Java Developer', '0901234567', 'https://ui-avatars.com/api/?name=Nguyen+Van+Minh&background=6f42c1&color=fff&size=150', '1995-03-15', 'Nam', 'TP. Hồ Chí Minh', '5 năm kinh nghiệm phát triển Java. Kỹ năng Java Spring Boot, Hibernate, Microservices.'),
(2, 4, 'Trần Thị Lan', 'Frontend Developer', '0912345678', 'https://ui-avatars.com/api/?name=Tran+Thi+Lan&background=fd7e14&color=fff&size=150', '1997-07-22', 'Nữ', 'Hà Nội', '3 năm kinh nghiệm phát triển Frontend. Chuyên về ReactJS, Vue.js, TypeScript.'),
(3, 5, 'Lê Hoàng Nam', 'Full Stack Developer', '0923456789', 'https://ui-avatars.com/api/?name=Le+Hoang+Nam&background=20c997&color=fff&size=150', '1996-11-08', 'Nam', 'Đà Nẵng', '4 năm kinh nghiệm Full Stack. Kỹ năng Node.js, React, MongoDB, PostgreSQL.');

-- =====================================================
-- JOB POSTINGS (50 jobs)
-- =====================================================
INSERT INTO job_postings (id, company_id, job_field_id, title, description, requirements, location, salary_min, salary_max, job_type, status, expired_at, created_at) VALUES
-- TechViet Solutions (Company 1) - IT
(1, 1, 1, 'Senior Java Developer', 'Tuyển Senior Java Developer cho dự án khách hàng Nhật Bản. Tham gia phát triển các giải pháp Enterprise sử dụng Java Spring Boot, microservices architecture.', '5 năm kinh nghiệm Java, Spring Boot, Hibernate, Microservices. Có kinh nghiệm với Kubernetes, Docker, CI/CD.', 'TP. Hồ Chí Minh', 15000000, 25000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(2, 1, 1, 'Frontend Developer (ReactJS)', 'Phát triển giao diện người dùng cho các ứng dụng web hiện đại. Làm việc chặt chẽ với team Backend và UI/UX Designer.', '2 năm kinh nghiệm ReactJS, HTML, CSS, JavaScript. Biết TypeScript là lợi thế.', 'TP. Hồ Chí Minh', 10000000, 18000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(3, 1, 1, 'Backend Developer (Node.js)', 'Phát triển API và dịch vụ server cho nền tảng recruitment. Xây dựng các microservice bằng Node.js, Express, MongoDB.', '3 năm kinh nghiệm Node.js, Express, MongoDB. Có kinh nghiệm với Redis, RabbitMQ.', 'TP. Hồ Chí Minh', 12000000, 20000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(4, 1, 1, 'DevOps Engineer', 'Xây dựng và duy trì hệ thống CI/CD, quản lý infrastructure as code, monitor hệ thống 24/7.', '3 năm kinh nghiệm DevOps. Thành thạo Docker, Kubernetes, AWS, Terraform.', 'TP. Hồ Chí Minh', 15000000, 25000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 15 DAY), NOW()),
(5, 1, 1, 'Python Developer', 'Phát triển các giải pháp AI/ML, xây dựng data pipeline và automation scripts.', '2 năm kinh nghiệm Python. Kinh nghiệm với TensorFlow, PyTorch, Pandas.', 'Hà Nội', 12000000, 22000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(6, 1, 1, 'Mobile Developer (Flutter)', 'Phát triển ứng dụng di động đa nền tảng sử dụng Flutter. Làm việc với REST APIs, Firebase.', '2 năm kinh nghiệm Flutter, Dart. Có ứng dụng trên App Store hoặc Google Play.', 'TP. Hồ Chí Minh', 10000000, 20000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(7, 1, 1, 'QA Engineer', 'Thiết kế và thực hiện test plan, automated testing, performance testing.', '2 năm kinh nghiệm QA. Thành thạo Selenium, JUnit, JMeter.', 'TP. Hồ Chí Minh', 8000000, 15000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),

-- GreenLife Foods (Company 2) - Marketing, Finance, HR
(8, 2, 3, 'Nhân viên Marketing', 'Phát triển chiến lược marketing digital. Quản lý chiến dịch quảng cáo trên Facebook Ads, Google Ads.', '2 năm kinh nghiệm marketing, Facebook Ads, Google Ads. Biết SEO là lợi thế.', 'Hà Nội', 8000000, 15000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 15 DAY), NOW()),
(9, 2, 2, 'Kế toán viên', 'Quản lý hồ sơ kế toán, lập báo cáo tài chính. Thực hiện các nghiệp vụ kế toán.', 'Tốt nghiệp ĐH Kế toán, kinh nghiệm MISA, Fast. Có chứng chỉ CPA là lợi thế.', 'Hà Nội', 7000000, 12000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(10, 2, 6, 'Nhân viên Hành chính', 'Quản lý văn phòng, họp tác với các bộ phận, hỗ trợ quản lý nhân sự.', '1 năm kinh nghiệm hành chính. Kỹ năng Microsoft Office tốt.', 'Hà Nội', 6000000, 10000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(11, 2, 4, 'Nhân viên Kinh doanh', 'Tìm kiếm khách hàng mới, chăm sóc khách hàng hiện tại, phát triển kinh doanh.', '2 năm kinh nghiệm kinh doanh. Kỹ năng giao tiếp tốt.', 'Hà Nội', 7000000, 15000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(12, 2, 5, 'Kiểm soát chất lượng (QC)', 'Kiểm tra chất lượng sản phẩm, quản lý quy trình sản xuất.', '1 năm kinh nghiệm QC trong thực phẩm. Biết HACCP.', 'Hà Nội', 6000000, 11000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),

-- FPT Software (Company 3) - IT
(13, 3, 1, 'Java Senior Developer', 'Phát triển phần mềm cho khách hàng Nhật Bản. Làm việc với Spring Boot, Oracle.', '4 năm kinh nghiệm Java. Giao tiếp tiếng Nhật N3 là lợi thế.', 'Hà Nội', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(14, 3, 1, 'ReactJS Developer', 'Phát triển frontend cho các dự án outsource. Làm việc với team quốc tế.', '3 năm kinh nghiệm ReactJS, Redux, TypeScript.', 'TP. Hồ Chí Minh', 14000000, 26000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(15, 3, 1, '.NET Developer', 'Phát triển ứng dụng web sử dụng .NET Core, C#. Tham gia dự án cho khách hàng Âu Mỹ.', '3 năm kinh nghiệm .NET, C#, ASP.NET Core. Tiếng Anh tốt.', 'Hà Nội', 15000000, 28000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(16, 3, 1, 'SAP Consultant', 'Tư vấn và triển khai giải pháp SAP ERP cho doanh nghiệp.', '3 năm kinh nghiệm SAP. Chứng chỉ SAP là bắt buộc.', 'TP. Hồ Chí Minh', 20000000, 40000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(17, 3, 1, 'AI/ML Engineer', 'Xây dựng và triển khai các mô hình AI/ML cho các giải pháp doanh nghiệp.', '3 năm kinh nghiệm AI/ML. Thành thạo Python, TensorFlow, PyTorch.', 'Hà Nội', 20000000, 45000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(18, 3, 1, 'Embedded Software Engineer', 'Phát triển phần mềm nhúng cho các sản phẩm IoT, automotive.', '3 năm kinh nghiệm embedded systems. Thành thạo C, C++, RTOS.', 'Hà Nội', 16000000, 30000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(19, 3, 1, 'Data Engineer', 'Xây dựng data pipeline, data warehouse, ETL processes.', '3 năm kinh nghiệm data engineering. Thành thạo SQL, Python, Spark.', 'TP. Hồ Chí Minh', 17000000, 32000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(20, 3, 1, 'Security Engineer', 'Bảo mật ứng dụng, penetration testing, threat analysis.', '3 năm kinh nghiệm cybersecurity. Chứng chỉ CEH, OSCP là lợi thế.', 'Hà Nội', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),

-- Viettel Solutions (Company 4) - Telecom & IT
(21, 4, 1, 'Backend Developer (Java)', 'Phát triển hệ thống viễn thông, billing system, CRM.', '3 năm kinh nghiệm Java. Kinh nghiệm với PostgreSQL, Redis.', 'Hà Nội', 14000000, 25000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(22, 4, 1, 'Network Engineer', 'Quản lý và vận hành hệ thống mạng viễn thông. Troubleshooting.', '3 năm kinh nghiệm network. CCNA, CCNP là lợi thế.', 'Hà Nội', 13000000, 24000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(23, 4, 1, 'iOS Developer', 'Phát triển ứng dụng di động iOS cho các sản phẩm của Viettel.', '2 năm kinh nghiệm iOS. Thành thạo Swift, UIKit, SwiftUI.', 'Hà Nội', 15000000, 28000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(24, 4, 1, 'Android Developer', 'Phát triển ứng dụng Android cho các sản phẩm của Viettel.', '2 năm kinh nghiệm Android. Thành thạo Kotlin, Java, Jetpack Compose.', 'Hà Nội', 15000000, 28000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(25, 4, 6, 'Chuyên viên Tuyển dụng', 'Tuyển dụng nhân sự IT, xây dựng thương hiệu nhà tuyển dụng.', '2 năm kinh nghiệm tuyển dụng IT. Kỹ năng interview tốt.', 'Hà Nội', 10000000, 18000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(26, 4, 2, 'Chuyên viên Tài chính', 'Phân tích tài chính, lập kế hoạch tài chính, báo cáo.', '2 năm kinh nghiệm tài chính. CFA là lợi thế.', 'Hà Nội', 12000000, 22000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(27, 4, 1, 'Cloud Engineer (AWS/Azure)', 'Triển khai và quản lý hạ tầng cloud. Architecture design.', '3 năm kinh nghiệm cloud. Chứng chỉ AWS hoặc Azure.', 'Hà Nội', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(28, 4, 1, 'Telecom Engineer', 'Kỹ sư viễn thông, triển khai và vận hành hệ thống BTS, transmission.', '2 năm kinh nghiệm telecom. Hiểu biết về 4G/5G.', 'Hà Nội', 14000000, 26000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),

-- VNG Corporation (Company 5) - Gaming & Tech
(29, 5, 1, 'Game Developer (Unity)', 'Phát triển game mobile sử dụng Unity. Làm việc với C#, người chơi global.', '2 năm kinh nghiệm Unity. Có game đã release là lợi thế.', 'TP. Hồ Chí Minh', 15000000, 30000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(30, 5, 1, 'Game Developer (Unreal)', 'Phát triển game PC/console sử dụng Unreal Engine.', '3 năm kinh nghiệm Unreal. C++ proficiency.', 'TP. Hồ Chí Minh', 20000000, 40000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(31, 5, 1, 'Server Developer (C++)', 'Phát triển server game, hệ thống real-time, backend infrastructure.', '3 năm kinh nghiệm C++ server. Kinh nghiệm multiplayer là lợi thế.', 'TP. Hồ Chí Minh', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(32, 5, 1, 'UI/UX Designer', 'Thiết kế giao diện game và ứng dụng. User research, prototyping.', '2 năm kinh nghiệm UI/UX. Portfolio game projects là lợi thế.', 'TP. Hồ Chí Minh', 12000000, 22000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(33, 5, 3, 'Marketing Game', 'Lên kế hoạch marketing cho game, quản lý community, influencer marketing.', '2 năm kinh nghiệm gaming marketing. Hiểu biết về gaming industry.', 'TP. Hồ Chí Minh', 10000000, 20000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(34, 5, 1, 'DevOps (Game Infrastructure)', 'Quản lý hạ tầng game server, auto-scaling, monitoring.', '3 năm kinh nghiệm DevOps. Kinh nghiệm với game servers là lợi thế.', 'TP. Hồ Chí Minh', 17000000, 32000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(35, 5, 10, 'Motion Graphics Designer', 'Thiết kế motion graphics cho game trailer, promotional materials.', '2 năm kinh nghiệm motion graphics. After Effects, Cinema 4D.', 'TP. Hồ Chí Minh', 10000000, 20000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(36, 5, 1, 'Data Analyst (Gaming)', 'Phân tích dữ liệu người chơi, game metrics, A/B testing.', '2 năm kinh nghiệm data analysis. Thành thạo SQL, Python, Tableau.', 'TP. Hồ Chí Minh', 13000000, 24000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),

-- Shopee Vietnam (Company 6) - E-commerce & Tech
(37, 6, 1, 'Backend Engineer (Java/Go)', 'Phát triển hệ thống e-commerce platform, order management, inventory.', '3 năm kinh nghiệm Java hoặc Go. Kinh nghiệm với microservices.', 'TP. Hồ Chí Minh', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(38, 6, 1, 'Frontend Engineer (ReactJS)', 'Xây dựng giao diện web thương mại điện tử. Performance optimization.', '3 năm kinh nghiệm ReactJS. Biết Next.js là lợi thế.', 'TP. Hồ Chí Minh', 16000000, 30000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(39, 6, 1, 'Machine Learning Engineer', 'Xây dựng recommendation system, search ranking, fraud detection.', '3 năm kinh nghiệm ML. Thành thạo Python, TensorFlow, Kubernetes.', 'TP. Hồ Chí Minh', 22000000, 50000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(40, 6, 1, 'Data Engineer', 'Xây dựng data pipeline cho hệ thống e-commerce. ETL, data warehouse.', '3 năm kinh nghiệm data engineering. Thành thạo Spark, Airflow.', 'TP. Hồ Chí Minh', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(41, 6, 1, 'SRE Engineer', 'Site Reliability Engineering. Monitor, alert, incident response.', '3 năm kinh nghiệm SRE/DevOps. Kinh nghiệm với Kubernetes.', 'TP. Hồ Chí Minh', 20000000, 40000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(42, 6, 4, 'Business Intelligence Analyst', 'Phân tích dữ liệu kinh doanh, dashboard, báo cáo xu hướng.', '2 năm kinh nghiệm BI. Thành thạo SQL, Excel, Power BI.', 'TP. Hồ Chí Minh', 12000000, 22000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(43, 6, 4, 'Product Manager', 'Quản lý sản phẩm e-commerce. Roadmap, prioritization, stakeholder management.', '3 năm kinh nghiệm product management. MBA là lợi thế.', 'TP. Hồ Chí Minh', 25000000, 50000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(44, 6, 6, 'HR Specialist (Tech)', 'Tuyển dụng và phát triển nhân sự tech. Employer branding.', '2 năm kinh nghiệm HR. Kinh nghiệm tuyển dụng tech roles.', 'TP. Hồ Chí Minh', 10000000, 18000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(45, 6, 1, 'Android Engineer', 'Phát triển ứng dụng Shopee Android. Performance critical code.', '3 năm kinh nghiệm Android. Kotlin, Jetpack Compose.', 'TP. Hồ Chí Minh', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(46, 6, 1, 'iOS Engineer', 'Phát triển ứng dụng Shopee iOS. SwiftUI, UIKit.', '3 năm kinh nghiệm iOS. Swift expertise required.', 'TP. Hồ Chí Minh', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(47, 6, 3, 'Performance Marketing', 'Quản lý chiến dịch quảng cáo performance marketing. ROI optimization.', '2 năm kinh nghiệm performance marketing. Google Ads, Facebook Ads.', 'TP. Hồ Chí Minh', 12000000, 22000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(48, 6, 1, 'Security Engineer', 'Bảo mật cho platform e-commerce. Penetration testing, code review.', '3 năm kinh nghiệm security. OWASP, Application security.', 'TP. Hồ Chí Minh', 20000000, 40000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),

-- Viet Nam Maritime Bank (Company 7) - Finance & Banking
(49, 7, 2, 'Chuyên viên Quan hệ Khách hàng', 'Tư vấn sản phẩm ngân hàng, phát triển khách hàng VIP.', '2 năm kinh nghiệm ngân hàng. Kỹ năng sales tốt.', 'Hà Nội', 10000000, 20000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(50, 7, 2, 'Kế toán Thanh toán Quốc tế', 'Xử lý thanh toán L/C, T/T, chuyển tiền quốc tế.', '2 năm kinh nghiệm thanh toán quốc tế. Tiếng Anh tốt.', 'TP. Hồ Chí Minh', 12000000, 22000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(51, 7, 2, 'Chuyên viên Tín dụng', 'Đánh giá hồ sơ tín dụng, thẩm định dự án cho vay.', '2 năm kinh nghiệm tín dụng. Biết định giá tài sản đảm bảo.', 'Hà Nội', 13000000, 25000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(52, 7, 2, 'IT Banking Specialist', 'Quản lý hệ thống core banking, ATM, POS.', '3 năm kinh nghiệm IT ngân hàng. Biết Temenos là lợi thế.', 'Hà Nội', 16000000, 30000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(53, 7, 6, 'Nhân viên Hành chính Văn phòng', 'Quản lý văn phòng, họp tác nội bộ, quản lý tài sản.', '1 năm kinh nghiệm hành chính. Microsoft Office expert.', 'Hà Nội', 7000000, 12000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 20 DAY), NOW()),
(54, 7, 2, 'Risk Management Analyst', 'Phân tích và quản lý rủi ro tín dụng, rủi ro thị trường.', '2 năm kinh nghiệm risk management. Chứng chỉ FRM là lợi thế.', 'Hà Nội', 15000000, 28000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 25 DAY), NOW()),
(55, 7, 1, 'Cybersecurity Specialist', 'Bảo mật thông tin ngân hàng. Compliance, audit, penetration testing.', '3 năm kinh nghiệm cybersecurity. ISO 27001, PCIDSS.', 'Hà Nội', 18000000, 35000000, 'FULL_TIME', 'OPEN', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW());

-- =====================================================
-- APPLICATIONS
-- =====================================================
INSERT INTO applications (id, candidate_id, job_id, cover_letter, status, applied_at) VALUES
(1, 1, 1, 'Tôi rất quan tâm đến vị trí Senior Java Developer tại TechViet Solutions. Với 5 năm kinh nghiệm trong lĩnh vực này, tôi tin rằng mình có thể đóng góp tích cực cho đội ngũ.', 'APPLIED', NOW()),
(2, 2, 2, 'Tôi muốn ứng tuyển vị trí Frontend Developer tại TechViet. Tôi đã làm việc với ReactJS trong 3 năm qua và rất hứng thú với các dự án của công ty.', 'SHORTLISTED', NOW()),
(3, 3, 3, 'Tôi phù hợp với vị trí Backend Developer (Node.js) tại công ty. Kinh nghiệm với MongoDB và Node.js của tôi sẽ giúp ích cho team.', 'APPLIED', NOW()),
(4, 1, 2, 'Ngoài Java, tôi cũng có kiến thức về ReactJS và muốn thử sức với vị trí này.', 'REJECTED', NOW()),
(5, 2, 1, 'Dù vị trí này yêu cầu 5 năm kinh nghiệm, nhưng tôi tin kỹ năng của mình có thể bù đắp.', 'REJECTED', NOW()),
(6, 1, 13, 'Tôi muốn ứng tuyển vị trí Java Senior Developer tại FPT Software. Kinh nghiệm với Spring Boot và dự án Nhật Bản của tôi phù hợp với yêu cầu.', 'APPLIED', NOW()),
(7, 2, 14, 'Tôi quan tâm đến vị trí ReactJS Developer tại FPT Software. Đã làm việc với TypeScript và Redux trong 2 năm.', 'APPLIED', NOW()),
(8, 3, 29, 'Tôi muốn ứng tuyển Game Developer (Unity) tại VNG. Đã có 2 năm kinh nghiệm phát triển game mobile và có 1 game đã release trên App Store.', 'SHORTLISTED', NOW());

-- =====================================================
-- INTERVIEWS
-- =====================================================
INSERT INTO interviews (id, application_id, scheduled_at, type, location_or_link, interviewer_note, result) VALUES
(1, 1, DATE_ADD(NOW(), INTERVAL 3 DAY), 'ONLINE', 'https://meet.google.com/abc-defg-hij', 'Verify kỹ năng Java, Spring Boot, System Design', 'PENDING'),
(2, 2, DATE_ADD(NOW(), INTERVAL 5 DAY), 'OFFLINE', 'Tòa nhà TechViet Tower, Lầu 15, Quận 1, TP.HCM', 'Thực hành coding ReactJS, JavaScript', 'PENDING'),
(3, 3, DATE_ADD(NOW(), INTERVAL 7 DAY), 'ONLINE', 'https://zoom.us/j/123456789', 'Review portfolio, Node.js skills assessment', 'PENDING'),
(4, 6, DATE_ADD(NOW(), INTERVAL 4 DAY), 'ONLINE', 'https://meet.google.com/xyz-uvwx-abcd', 'Technical interview - Java, OOP, System Design', 'PENDING'),
(5, 8, DATE_ADD(NOW(), INTERVAL 6 DAY), 'OFFLINE', 'Tòa nhà VNG Tower, Quận 7, TP.HCM', 'Game portfolio review, Unity basics', 'PENDING');

-- =====================================================
-- NOTIFICATIONS
-- =====================================================
INSERT INTO notifications (id, user_id, title, message, is_read) VALUES
(1, 1, 'Ứng viên mới ứng tuyển', 'Nguyễn Văn Minh đã ứng tuyển vị trí Senior Java Developer', false),
(2, 1, 'Lịch phỏng vấn', 'Bạn có lịch phỏng vấn vào ngày mai với ứng viên Nguyễn Văn Minh', false),
(3, 3, 'Trạng thái đơn ứng tuyển', 'Đơn ứng tuyển Senior Java Developer của bạn đang được xem xét', false),
(4, 7, 'Ứng viên mới ứng tuyển', 'Nguyễn Văn Minh đã ứng tuyển vị trí Java Senior Developer tại FPT Software', false),
(5, 9, 'Ứng viên mới ứng tuyển', 'Nguyễn Văn Minh đã ứng tuyển vị trí Game Developer (Unity) tại VNG', false);

-- =====================================================
-- BLOGS
-- =====================================================
INSERT INTO blogs (id, title, summary, content, image_url, author_id, created_at, updated_at) VALUES
(1, 'Lộ trình làm chủ 10 Kỹ Năng "Sống Còn" cho Lập trình viên năm 2026',
 'Đứng trước làn sóng AI mạnh mẽ, lập trình viên cần trang bị những gì để không bị đào thải? Khám phá ngay bộ kỹ năng then chốt năm 2026.',
 '<h3>1. Hệ sinh thái TypeScript & Các Framework cốt lõi</h3>
 <p>Không chỉ dừng lại ở JavaScript cơ bản, TypeScript đã trở thành tiêu chuẩn bắt buộc cho các dự án lớn. Khả năng kiểm soát kiểu dữ liệu (Type-safe) của nó giúp giảm thiểu tối đa bug ngay từ lúc viết code. Bên cạnh đó, việc thành thạo ít nhất một framework hiện đại như React, Vue, hoặc Angular, kết hợp với các meta-framework như Next.js hay Nuxt.js là điều kiện tiên quyết để xây dựng các ứng dụng web phức tạp.</p>
 <h3>2. Thiết kế Cơ sở dữ liệu (Database Design) & Tối ưu hóa SQL</h3>
 <p>Dù có nhiều công nghệ mới, dữ liệu vẫn là trái tim của mọi ứng dụng. Một lập trình viên xuất sắc trong năm 2026 phải biết cách thiết kế database một cách chuẩn hóa, hiểu rõ về Indexing, và biết cách viết các câu query SQL tối ưu. Việc hiểu khi nào nên dùng Relational DB (SQL Server, PostgreSQL) và khi nào dùng NoSQL (MongoDB, Redis) sẽ quyết định hiệu năng của hệ thống.</p>
 <h3>3. Điện toán đám mây (Cloud Computing) & Microservices</h3>
 <p>Việc làm chủ AWS, Azure hay Google Cloud không còn là tùy chọn. Xu hướng Serverless Architecture và Microservices đang giúp doanh nghiệp tối ưu chi phí và tăng tốc độ triển khai ứng dụng. Bạn cần làm quen với việc chia nhỏ ứng dụng monolithic thành các services độc lập, giao tiếp với nhau qua REST API hoặc gRPC.</p>
 <h3>4. Kỹ năng cộng tác với AI (AI-Augmented Development)</h3>
 <p>Đừng sợ AI cướp việc, hãy sợ người biết dùng AI. Lập trình viên giỏi năm 2026 là người biết sử dụng GitHub Copilot, ChatGPT hoặc Gemini để sinh ra các đoạn boilerplate code, viết unit test tự động, và tìm ra các edge cases trong logic, từ đó dành 80% thời gian cho tư duy kiến trúc hệ thống thay vì gõ phím cơ học.</p>
 <h3>5. Quản lý dự án (Project Management) cho Developer</h3>
 <p>Không chỉ PM mới cần quản lý dự án. Việc lập trình viên tự biết cách chia nhỏ task, ước lượng thời gian (estimation) chính xác, và cập nhật tiến độ liên tục qua Jira/Trello giúp toàn bộ vòng đời phát triển phần mềm (SDLC) diễn ra trơn tru và chuyên nghiệp hơn.</p>',
 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800',
 6, NOW(), NOW()),

(2, 'Bí quyết viết CV IT "bách chiến bách thắng" năm 2026',
 'Làm thế nào để CV của bạn vượt qua hệ thống quét tự động (ATS) và lọt vào mắt xanh của các Tech Lead khó tính? Đây là câu trả lời.',
 '<h3>1. Tối ưu hóa cho hệ thống ATS (Applicant Tracking System)</h3>
 <p>Các công ty lớn hiện nay đều dùng AI để lọc CV trước khi tới tay con người. Để vượt qua vòng này, bạn cần sử dụng các từ khóa chuyên môn (Keywords) sát với Job Description. Tuyệt đối tránh các định dạng CV quá màu mè, có đồ thị dạng hình ảnh, bảng biểu lồng nhau hoặc chia quá nhiều cột gây nhiễu trình quét. Hãy giữ layout đơn giản, text-based là tốt nhất.</p>
 <h3>2. Áp dụng Công thức STAR trong phần Kinh nghiệm</h3>
 <p>Thay vì liệt kê các công việc nhàm chán như "Tham gia code backend", hãy viết theo cấu trúc: <strong>Situation (Tình huống) - Task (Nhiệm vụ) - Action (Hành động) - Result (Kết quả)</strong>.</p>
 <ul>
    <li><em>Ví dụ chưa tốt:</em> Có kinh nghiệm làm web bán hàng.</li>
    <li><em>Ví dụ chuẩn STAR:</em> Tối ưu hóa Database Design và viết lại các query SQL cho website bán bánh online (Action), giúp giải quyết tình trạng nghẽn cổ chai dữ liệu (Situation), kết quả làm giảm 40% thời gian tải trang và tăng 15% tỷ lệ chuyển đổi (Result).</li>
 </ul>
 <h3>3. Chứng minh năng lực qua GitHub Profile</h3>
 <p>Một link GitHub với các commit xanh đều đặn có giá trị hơn ngàn lời nói. Tuy nhiên, đừng chỉ để code "chết" ở đó. Mỗi repository nổi bật cần phải có một file README.md chỉn chu, bao gồm: Giới thiệu dự án, Công nghệ sử dụng (Java, C#, JavaScript...), Cách cài đặt (How to run), và một vài ảnh chụp màn hình hoặc link Demo trực tiếp.</p>
 <h3>4. Điều chỉnh Mục tiêu Nghề nghiệp (Objective) thật sắc nét</h3>
 <p>Đừng dùng chung một câu mục tiêu cho mọi công ty. Nếu bạn đang ứng tuyển vị trí thực tập, hãy viết rõ ràng: <em>"Là một sinh viên IT năm cuối với nền tảng vững chắc về Web Development, tôi mong muốn tìm kiếm cơ hội thực tập để áp dụng kiến thức OOP, quy trình Agile và đóng góp vào các sản phẩm thực tế của công ty."</em></p>',
 'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=800',
 6, NOW(), NOW()),

(3, 'Phân tích xu hướng tuyển dụng IT 2026: Kỷ nguyên của sự thích nghi',
 'Thị trường IT đang chuyển dịch từ "thừa số lượng" sang "tinh chất lượng". Những vị trí nào đang khát nhân lực nhất hiện nay?',
 '<h3>1. Sự lên ngôi của các hệ thống thông minh và Data</h3>
 <p>Nhu cầu về kỹ sư phần mềm biết tích hợp AI vào sản phẩm truyền thống đang bùng nổ. Các công ty không chỉ cần ứng dụng CRUD (Create, Read, Update, Delete) thông thường nữa, họ cần những hệ thống có khả năng phân tích dữ liệu người dùng, gợi ý nội dung (Recommendation Systems) và tự động hóa quy trình nghiệp vụ.</p>
 <h3>2. Chất lượng ứng viên thay thế cho Số lượng</h3>
 <p>Giai đoạn tăng trưởng nóng đã qua. Giờ đây, các công ty sẵn sàng trả lương rất cao cho một "10x Developer" (Lập trình viên xuất sắc) thay vì thuê 10 lập trình viên trung bình. Điều này đồng nghĩa với việc bạn phải nắm cực vững kiến thức nền tảng như Mạng máy tính (Networking), Cấu trúc dữ liệu, Hệ điều hành, thay vì chỉ biết dùng Framework một cách rập khuôn.</p>
 <h3>3. Chú trọng văn hóa DevOps và Kiểm thử tự động</h3>
 <p>Ranh giới giữa Developer (Phát triển) và Operation (Vận hành) ngày càng mờ nhạt. Nhà tuyển dụng ưu tiên những ứng viên có tư duy "You build it, you run it". Bạn cần biết cách viết Unit Test, Automation Test, và thiết lập luồng CI/CD (Continuous Integration / Continuous Deployment) để đưa code từ máy cá nhân lên server một cách an toàn và tự động.</p>
 <h3>4. Hybrid Work và Yêu cầu khắt khe về kỹ năng mềm</h3>
 <p>Khi mô hình làm việc kết hợp (Hybrid) trở thành tiêu chuẩn, khả năng giao tiếp bất đồng bộ (Asynchronous Communication) qua văn bản là tối quan trọng. Khả năng viết tài liệu kỹ thuật (Documentation) mạch lạc, mô tả bug rõ ràng và review code tinh tế sẽ giúp bạn tỏa sáng trong mắt nhà tuyển dụng.</p>',
 'https://images.unsplash.com/photo-1521737711867-e3b97375f902?w=800',
 6, NOW(), NOW()),

(4, 'Chinh phục phỏng vấn Technical: Từ thuật toán đến tư duy hệ thống',
 'Đừng để những câu hỏi hóc búa làm khó bạn. Hãy cùng bóc tách quy trình phỏng vấn tại các công ty công nghệ.',
 '<h3>1. Cửa ải Thuật toán và Cấu trúc dữ liệu (DSA)</h3>
 <p>Dù có nhiều tranh cãi về tính thực tiễn, DSA vẫn là thước đo tư duy logic cơ bản nhất được các tập đoàn lớn áp dụng. Bạn không cần phải học thuộc lòng các bài toán Olympic, nhưng bắt buộc phải hiểu rõ độ phức tạp thời gian/không gian (Big O Notation) của các thao tác trên Array, HashMap, Tree, và Graph. Hãy luyện tập thường xuyên trên LeetCode hoặc HackerRank.</p>
 <h3>2. Live Coding: Đừng vội vàng gõ code</h3>
 <p>Lỗi lớn nhất của ứng viên khi Live Coding là lập tức cắm mặt vào gõ code. Hãy nhớ, người phỏng vấn quan tâm đến <strong>cách bạn suy nghĩ và giải quyết vấn đề</strong> hơn là một đoạn code chạy được nhưng lộn xộn. Quy trình chuẩn nên là:</p>
 <ul>
    <li>Đặt câu hỏi làm rõ các ranh giới của bài toán (Edge cases).</li>
    <li>Trình bày hướng tiếp cận (Approach) và trao đổi với người phỏng vấn.</li>
    <li>Bắt đầu implement bằng ngôn ngữ thế mạnh của bạn (Java, C#, JS...).</li>
    <li>Tự nhẩm lại (Dry run) code của mình với một vài test case thực tế trước khi báo hoàn thành.</li>
 </ul>
 <h3>3. Kiến thức chuyên môn theo Stack (Domain Knowledge)</h3>
 <p>Nếu bạn phỏng vấn vị trí Backend, hãy chuẩn bị tinh thần bị hỏi xoáy đáp xoay về Database. Ví dụ: Sự khác biệt giữa N+1 Query là gì? Làm sao để tối ưu hóa nó? Cơ chế hoạt động của Garbage Collection trong Java/C#? Các giao thức mạng TCP/UDP khác nhau thế nào?</p>
 <h3>4. Phỏng vấn sự phù hợp văn hóa (Culture Fit)</h3>
 <p>Kỹ năng code giỏi đến mấy mà thái độ không hợp tác cũng sẽ bị loại. Hãy chuẩn bị các câu chuyện thực tế về cách bạn xử lý khi bất đồng quan điểm với đồng nghiệp, cách bạn vượt qua áp lực deadline, hoặc cách bạn chủ động học hỏi một công nghệ mới.</p>',
 'https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=800',
 6, NOW(), NOW()),

(5, 'Báo cáo lương IT Việt Nam 2026: Những con số biết nói',
 'Mức lương ngành lập trình tại Việt Nam đang có sự phân hóa mạnh mẽ. Đâu là ngách "hái ra tiền" trong năm nay?',
 '<h3>1. Bức tranh toàn cảnh thu nhập theo cấp bậc</h3>
 <p>Năm 2026 chứng kiến sự ổn định trở lại của thị trường lương thưởng sau những biến động. Theo các báo cáo mới nhất:</p>
 <ul>
    <li><strong>Fresher/Intern (Dưới 1 năm):</strong> 8.000.000 - 15.000.000 VNĐ. Sự cạnh tranh ở mức độ này cực kỳ gay gắt.</li>
    <li><strong>Junior (1-3 năm):</strong> 15.000.000 - 25.000.000 VNĐ. Mức lương bắt đầu tăng tốc nhanh nếu ứng viên chứng minh được khả năng làm việc độc lập.</li>
    <li><strong>Mid-level (3-5 năm):</strong> 25.000.000 - 45.000.000 VNĐ. Đây là lực lượng nòng cốt của các công ty.</li>
    <li><strong>Senior/Lead (Trên 5 năm):</strong> 50.000.000 - 100.000.000+ VNĐ. Đối với những chuyên gia có khả năng giải quyết các bài toán hóc búa về kiến trúc hệ thống quy mô lớn.</li>
 </ul>
 <h3>2. Sự phân hóa lương theo nền tảng công nghệ (Tech Stack)</h3>
 <p>Sự chênh lệch mức lương giữa các công nghệ không quá lớn ở level thấp, nhưng rõ rệt ở level cao. Lập trình viên thông thạo các ngôn ngữ strongly-typed dành cho hệ thống lớn như Java (Spring Boot) hoặc C# (.NET) thường có lộ trình thăng tiến ổn định trong môi trường Enterprise. Trong khi đó, các kỹ sư làm mảng Cloud/DevOps, Security và AI/Data Science đang nhận được mức đãi ngộ premium cao hơn khoảng 20-30% so với lập trình viên web thông thường.</p>
 <h3>3. Ngoại ngữ: "Bàn đạp" nhân đôi thu nhập</h3>
 <p>Sự thật mất lòng: Cùng một năng lực kỹ thuật, một lập trình viên có khả năng giao tiếp tiếng Anh trôi chảy (làm việc trực tiếp với khách hàng Âu Mỹ) hoặc tiếng Nhật (chuẩn N3, N2) có thể nhận mức lương cao gấp rưỡi so với người chỉ làm việc trong team nội địa. Đầu tư vào ngoại ngữ chính là khoản đầu tư có ROI (Tỷ suất hoàn vốn) cao nhất đối với dân IT.</p>',
 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=800',
 6, NOW(), NOW()),

(6, 'Remote Work vs Office: Đâu là lựa chọn tối ưu cho Developer?',
 'Làm việc tự do tại nhà hay đến văn phòng để kết nối? Cùng phân tích sâu về tác động đến hiệu suất, sức khỏe tinh thần và sự nghiệp của bạn.',
 '<h3>1. Mặt trái của sự tự do (Remote Work)</h3>
 <p>Làm việc từ xa (Remote work) thường được ca ngợi vì tính linh hoạt, tiết kiệm hàng giờ đồng hồ kẹt xe khói bụi mỗi ngày. Bạn có thể setup một góc làm việc hoàn hảo với 2 màn hình 4K và cà phê tự pha. Tuy nhiên, ranh giới giữa công việc và cuộc sống cá nhân (Work-life blur) rất dễ bị xóa nhòa. Nhiều lập trình viên cho biết họ thậm chí còn làm việc nhiều giờ hơn khi ở nhà. Sự thiếu hụt các tương tác xã hội (những cuộc tán gẫu nhỏ) cũng là nguyên nhân chính dẫn đến cảm giác cô đơn và burnout.</p>
 <h3>2. Sức mạnh của sự tương tác trực tiếp (Office Work)</h3>
 <p>Dù có nhiều công cụ họp trực tuyến, không gì thay thế được việc cùng nhau đứng trước bảng trắng (whiteboard) để vẽ và tranh luận về một sơ đồ Database Design phức tạp. Đối với Fresher/Junior, văn phòng là môi trường tuyệt vời nhất để học hỏi thụ động. Bạn học được cách các Senior phản ứng với lỗi hệ thống (production incident), cách họ giao tiếp qua điện thoại, và dễ dàng nhờ sự trợ giúp chỉ bằng cách gõ nhẹ vào vai họ.</p>
 <h3>3. Hybrid Model: Điểm cân bằng lý tưởng</h3>
 <p>Nhận thấy nhược điểm của cả hai thái cực, hầu hết các công ty công nghệ hàng đầu năm 2026 đang áp dụng mô hình Hybrid (Ví dụ: 3 ngày lên công ty, 2 ngày làm ở nhà). Những ngày ở nhà sẽ dành cho "Deep Work" – những công việc đòi hỏi sự tập trung cao độ như code một tính năng khó. Những ngày lên công ty sẽ dành cho các cuộc họp quan trọng, lên kế hoạch dự án (Sprint Planning), và kết nối tình cảm đồng đội.</p>',
 'https://images.unsplash.com/photo-1521898284481-a5ec348cb555?w=800',
 6, NOW(), NOW()),

(7, 'Xây dựng Portfolio "độc bản": Biến dự án cá nhân thành tấm vé vàng',
 'Một CV chỉ nói lên bạn là ai, nhưng một Portfolio chất lượng sẽ chứng minh năng lực thực chiến của bạn với nhà tuyển dụng.',
 '<h3>1. Chọn dự án "thực tế", đừng làm lại bản sao</h3>
 <p>Nhà tuyển dụng đã quá chán ngán với các dự án Todo List hay clone giao diện Instagram đơn giản. Hãy xây dựng những ứng dụng giải quyết một bài toán nghiệp vụ (business logic) cụ thể. Ví dụ:</p>
 <ul>
    <li><strong>Website Web xem phim trực tuyến:</strong> Xử lý tính năng stream video, phân quyền user (VIP/Free), xây dựng chức năng tìm kiếm phim theo nhiều bộ lọc, thiết kế database lưu trữ lịch sử xem phim.</li>
    <li><strong>Hệ thống bán hàng trực tuyến (VD: Web bán bánh):</strong> Tích hợp giỏ hàng phức tạp, quản lý tồn kho, xử lý luồng thanh toán, và xuất hóa đơn.</li>
    <li><strong>Ứng dụng Đặt lịch khám phòng khám:</strong> Xử lý logic trùng lịch hẹn, thông báo qua email/SMS, dashboard thống kê cho bác sĩ.</li>
 </ul>
 <h3>2. Trình bày Portfolio như một câu chuyện</h3>
 <p>Đừng chỉ vứt link web và link code. Mỗi dự án trong Portfolio cần có một trang giới thiệu chi tiết (Case Study), trong đó nêu rõ: Công nghệ sử dụng, Vai trò của bạn, Thách thức lớn nhất bạn gặp phải và Cách bạn vượt qua nó. Ví dụ: "Làm sao để hệ thống xử lý hàng ngàn đơn hàng cùng lúc trong dịp Flash Sale?". Câu trả lời của bạn sẽ cho thấy chiều sâu kỹ thuật.</p>
 <h3>3. Tinh tế trong từng dòng code (Clean Code)</h3>
 <p>Hãy nhớ rằng các Tech Lead sẽ thực sự mở mã nguồn của bạn ra đọc. Code phải sạch, biến và hàm phải được đặt tên có ý nghĩa (bằng tiếng Anh), tổ chức thư mục (Folder structure) chuẩn mực, và đặc biệt là phải có file README.md hướng dẫn cách khởi chạy dự án cực kỳ chi tiết.</p>',
 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800',
 6, NOW(), NOW()),

(8, 'Lộ trình thăng tiến: Từ gõ code đến dẫn dắt đội ngũ',
 'Sự nghiệp IT không chỉ có một đường thẳng. Bạn muốn trở thành một chuyên gia kỹ thuật bậc thầy hay một nhà quản lý tài ba?',
 '<h3>1. Giai đoạn Tích lũy (Junior đến Mid-Level)</h3>
 <p>Trong 1-3 năm đầu, mục tiêu số một của bạn là sự thuần thục về mặt kỹ thuật (Technical Mastery). Hãy code thật nhiều, nhưng phải code một cách có ý thức. Đừng chỉ copy-paste từ StackOverflow. Khi một đoạn code chạy được, hãy tìm hiểu tại sao nó chạy. Hãy học cách viết Unit Test, áp dụng các Design Patterns cơ bản, và thấu hiểu các nguyên lý SOLID. Đây là giai đoạn để xây dựng phần móng thật vững chắc.</p>
 <h3>2. Bước ngoặt Seniority: Không chỉ là kỹ thuật</h3>
 <p>Sự khác biệt lớn nhất giữa Mid-level và Senior không nằm ở việc ai biết nhiều syntax hơn. Một Senior Developer là người có khả năng nhìn thấy bức tranh tổng thể (Big Picture). Họ biết cách cân bằng giữa sự hoàn hảo của code và deadline của kinh doanh (Business value). Ở giai đoạn này, bạn sẽ tham gia nhiều hơn vào việc thiết kế kiến trúc, review code của các bạn Junior, và đánh giá tính khả thi của dự án.</p>
 <h3>3. Rẽ nhánh: Quản lý (Management) vs Chuyên gia (Individual Contributor - IC)</h3>
 <p>Khoảng năm thứ 5-7, bạn sẽ đứng trước một ngã rẽ quan trọng:</p>
 <ul>
    <li><strong>Track Quản lý (Engineering Manager/Tech Lead):</strong> Nếu bạn thích giao tiếp, thích giải quyết các vấn đề về quy trình, phát triển con người, phân bổ nguồn lực và đảm bảo team đi đúng hướng.</li>
    <li><strong>Track Chuyên gia (Staff/Principal Engineer):</strong> Nếu bạn vẫn đam mê giải quyết những bài toán kỹ thuật hóc búa nhất, thiết kế những hệ thống phục vụ hàng triệu user, và đóng vai trò cố vấn công nghệ cho toàn công ty.</li>
 </ul>',
 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=800',
 6, NOW(), NOW()),

(9, 'Soft Skills - "Vũ khí bí mật" của những Developer xuất chúng',
 'Tại sao những lập trình viên mang lại giá trị cao nhất cho công ty thường không phải là người code nhanh nhất?',
 '<h3>1. Kỹ năng giao tiếp và Dịch thuật "Ngôn ngữ Tech"</h3>
 <p>Code chỉ là công cụ để giải quyết bài toán của con người. Lập trình viên xuất sắc là người có khả năng giải thích một vấn đề kỹ thuật phức tạp (như lỗi bất đồng bộ của Database) cho một khách hàng, một người làm Marketing hay Giám đốc kinh doanh một cách dễ hiểu nhất, không dùng thuật ngữ (jargon), để họ hiểu được rủi ro và đồng tình với giải pháp.</p>
 <h3>2. Nghệ thuật Review Code và Làm việc nhóm</h3>
 <p>Code review không phải là nơi để thể hiện cái tôi hay chê bai người khác. Một lập trình viên có EQ cao sẽ để lại những comment mang tính chất xây dựng, gợi ý giải pháp thay vì chỉ trích lỗi sai. Đồng thời, họ cũng biết cách đón nhận feedback một cách cởi mở, không tự ái khi code của mình bị yêu cầu sửa đổi.</p>
 <h3>3. Quản lý kỳ vọng (Expectation Management) và Nói "Không"</h3>
 <p>Một trong những kỹ năng khó nhất là việc từ chối những tính năng vô lý hoặc những deadline bất khả thi từ phía Product Manager một cách chuyên nghiệp. Thay vì nói "Không làm được", hãy đưa ra sự lựa chọn: "Chúng ta có thể hoàn thành kịp deadline thứ 6 này, nhưng phải cắt giảm 2 tính năng phụ này, hoặc chúng ta cần thêm 1 tuần để hoàn thiện toàn bộ. Bạn muốn ưu tiên phương án nào?".</p>',
 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800',
 6, NOW(), NOW()),

(10, 'Luyện kỹ năng System Design: Tư duy như Kiến trúc sư',
 'Phân tích cách các "ông lớn" công nghệ thiết kế hệ thống chịu tải hàng triệu người dùng đồng thời.',
 '<h3>1. Khởi đầu với sự đơn giản trước khi mở rộng</h3>
 <p>Trong mọi buổi phỏng vấn thiết kế hệ thống, sai lầm phổ biến là cố gắng nhồi nhét Kafka, Kubernetes, Microservices vào ngay từ đầu. Hãy bắt đầu với một thiết kế Monolithic đơn giản nhất (1 Web Server + 1 Database Server) để giải quyết logic nghiệp vụ. Sau đó, dựa trên các yêu cầu về hiệu năng (QPS - Queries Per Second) và lưu lượng dữ liệu, mới bắt đầu bóc tách các điểm nghẽn (bottleneck).</p>
 <h3>2. Case Study thực tế: Nền tảng Web xem phim trực tuyến</h3>
 <p>Giả sử bạn phải thiết kế hệ thống giống Netflix. Đây là lúc tư duy kiến trúc lên tiếng:</p>
 <ul>
    <li><strong>Lưu trữ Video:</strong> Video file lớn không thể lưu trong Database thường. Bạn sẽ cần Object Storage (như Amazon S3) và kết hợp với mạng phân phối nội dung CDN (Content Delivery Network) để người dùng ở các khu vực địa lý khác nhau tải video nhanh nhất.</li>
    <li><strong>Database Design:</strong> Dữ liệu về metadata phim (tên, đạo diễn, năm sản xuất) có thể lưu ở cơ sở dữ liệu quan hệ (Relational DB) để dễ dàng query. Nhưng dữ liệu về lượt xem, lượt thích, lịch sử tương tác liên tục của hàng triệu user nên được đẩy vào NoSQL hoặc Cache (Redis) để đảm bảo tốc độ phản hồi tính bằng mili-giây.</li>
 </ul>
 <h3>3. Hiểu về các Trade-offs (Sự đánh đổi)</h3>
 <p>Trong System Design, không có giải pháp "đúng" hay "sai" tuyệt đối, chỉ có giải pháp "phù hợp" với bối cảnh kinh doanh. Định lý CAP (Consistency, Availability, Partition Tolerance) chỉ ra rằng trong một hệ thống phân tán, bạn phải đánh đổi. Ví dụ: Với hệ thống hiển thị lượt Like trên Facebook, hệ thống ưu tiên tính sẵn sàng (Availability) - người dùng có thể thấy số Like hơi chậm một chút cũng không sao. Nhưng với hệ thống giao dịch ngân hàng, tính nhất quán (Consistency) phải được đặt lên hàng đầu, dữ liệu phải chính xác tuyệt đối ở mọi thời điểm.</p>',
 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800',
 6, NOW(), NOW());