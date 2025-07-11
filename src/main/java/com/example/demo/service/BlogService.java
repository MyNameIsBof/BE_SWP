package com.example.demo.service;

import com.example.demo.dto.request.BlogRequest;
import com.example.demo.dto.response.BlogResponse;
import com.example.demo.entity.Blog;
import com.example.demo.entity.User;
import com.example.demo.enums.BlogStatus;
import com.example.demo.repository.BlogRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final AuthenticationService authenticationService;

    // Lấy tất cả blog còn hoạt động
    public List<BlogResponse> getAllBlogs() {
        return blogRepository.findAll().stream()
                .filter(blog -> blog.getStatus() == BlogStatus.ACTIVE)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy chi tiết blog theo ID nếu còn hoạt động
    public Optional<BlogResponse> getBlogById(Long id) {
        return blogRepository.findById(id)
                .filter(blog -> blog.getStatus() == BlogStatus.ACTIVE)
                .map(this::convertToResponse);
    }

    // Tạo blog mới
    public BlogResponse createBlog(BlogRequest request) {
        // Lấy user hiện tại
        User currentUser = authenticationService.getCurrentUser();

        Blog blog = convertToEntity(request);
        blog.setAuthor(currentUser.getFullName()); // Gán tên người dùng làm tác giả
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        blog.setStatus(BlogStatus.ACTIVE);

        Blog saved = blogRepository.save(blog);
        return convertToResponse(saved);
    }

    // Cập nhật blog
    public BlogResponse updateBlog(Long id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog với ID: " + id));
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setImg(request.getImg());
        blog.setUpdatedAt(LocalDateTime.now());
        Blog updated = blogRepository.save(blog);
        return convertToResponse(updated);
    }

    // Đánh dấu blog là đã xoá
    public void deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog để xoá"));
        blog.setStatus(BlogStatus.DELETED);
        blogRepository.save(blog);
    }

    // Tìm kiếm theo tiêu đề (chỉ blog ACTIVE)
    public List<BlogResponse> searchByTitle(String keyword) {
        return blogRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .filter(blog -> blog.getStatus() == BlogStatus.ACTIVE)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private Blog convertToEntity(BlogRequest request) {
        return Blog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .img(request.getImg())
                .build(); // author set riêng trong createBlog()
    }

    private BlogResponse convertToResponse(Blog blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .author(blog.getAuthor())
                .img(blog.getImg())
                .status(blog.getStatus())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }

    // Lấy tất cả blog không lọc trạng thái (dành cho Admin)
    public List<BlogResponse> getAllBlogsForAdmin() {
        return blogRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void restoreBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog để khôi phục"));
        blog.setStatus(BlogStatus.ACTIVE);
        blog.setUpdatedAt(LocalDateTime.now());
        blogRepository.save(blog);
    }

    public void updateBlogImage(Long id, String imageUrl) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog để cập nhật ảnh"));
        blog.setImg(imageUrl);
        blog.setUpdatedAt(LocalDateTime.now());
        blogRepository.save(blog);
    }

    public String uploadImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh", e);
        }
    }

}