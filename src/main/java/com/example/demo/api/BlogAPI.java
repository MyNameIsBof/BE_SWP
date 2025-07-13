package com.example.demo.api;

import com.example.demo.dto.request.BlogRatingRequest;
import com.example.demo.dto.request.BlogRequest;
import com.example.demo.dto.response.BlogRatingResponse;
import com.example.demo.dto.response.BlogResponse;
import com.example.demo.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class BlogAPI {

    private final BlogService blogService;

    @GetMapping("/get-all")
    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<BlogResponse>> getAllBlogsForAdmin() {
        return ResponseEntity.ok(blogService.getAllBlogsForAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BlogResponse> createBlog(@RequestBody BlogRequest blogRequest) {
        return ResponseEntity.ok(blogService.createBlog(blogRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogResponse> updateBlog(@PathVariable Long id, @RequestBody BlogRequest blogRequest) {
        return ResponseEntity.ok(blogService.updateBlog(id, blogRequest));
    }

    // Delete blog (soft delete)
    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá blog (chỉ đổi trạng thái sang DELETED)")
    public ResponseEntity<String> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok("Xoá blog thành công (đã đánh dấu DELETED)");
    }

    // Restore blog
    @PutMapping("/{id}/restore")
    @Operation(summary = "Khôi phục blog bị xoá")
    public ResponseEntity<String> restoreBlog(@PathVariable Long id) {
        blogService.restoreBlog(id);
        return ResponseEntity.ok("Khôi phục blog thành công");
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogResponse>> searchBlog(@RequestParam String keyword) {
        return ResponseEntity.ok(blogService.searchByTitle(keyword));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload ảnh")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = blogService.uploadImage(file);
        return ResponseEntity.ok(url);
    }


    @PostMapping("/{blogId}/ratings")
    public BlogRatingResponse rateBlog(@PathVariable Long blogId,
                                       @RequestBody BlogRatingRequest req,
                                       @RequestParam Long userId) { // hoặc lấy userId từ SecurityContext
        return blogService.rateBlog(blogId, userId, req);
    }
}