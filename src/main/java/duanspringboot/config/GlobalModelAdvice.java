package duanspringboot.config;

import duanspringboot.dto.blog.BlogResponse;
import duanspringboot.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final BlogService blogService;

    @ModelAttribute
    public void addFeaturedBlogs(Model model) {
        List<BlogResponse> allBlogs = blogService.getAll();
        List<BlogResponse> featuredBlogs = allBlogs.size() > 5 
            ? allBlogs.subList(0, 5) 
            : allBlogs;
        model.addAttribute("featuredBlogs", featuredBlogs);
    }
}
