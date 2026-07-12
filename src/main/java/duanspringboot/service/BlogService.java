package duanspringboot.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import duanspringboot.dto.blog.BlogRequest;
import duanspringboot.dto.blog.BlogResponse;
import duanspringboot.entity.Blog;
import duanspringboot.entity.User;
import duanspringboot.repository.BlogRepository;
import duanspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public BlogResponse create(BlogRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Blog blog = Blog.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .author(user)
                .build();

        return mapToResponse(blogRepository.save(blog));
    }

    public List<BlogResponse> getAll() {
        return blogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BlogResponse getById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        return mapToResponse(blog);
    }

    public BlogResponse update(Long id, BlogRequest request, Long userId) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        blog.setTitle(request.getTitle());
        blog.setSummary(request.getSummary());
        blog.setContent(request.getContent());
        blog.setImageUrl(request.getImageUrl());

        return mapToResponse(blogRepository.save(blog));
    }

    public void delete(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        blogRepository.delete(blog);
    }

    private BlogResponse mapToResponse(Blog blog) {
        String email = blog.getAuthor().getEmail();
        String authorName = email.substring(0, email.indexOf('@'));
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .summary(blog.getSummary())
                .content(blog.getContent())
                .imageUrl(blog.getImageUrl())
                .authorName(authorName)
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }
}
