package com.example.javaplayground.redis.views;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/posts")
@RequiredArgsConstructor
public class PostController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    public PostDetailResponse getPost(@PathVariable Long postId) {

        // 게시글 조회

        return new PostDetailResponse(null, null, null);
    }
}
