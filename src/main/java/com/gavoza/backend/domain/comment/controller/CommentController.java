package com.gavoza.backend.domain.comment.controller;

import com.gavoza.backend.domain.comment.dto.request.CommentRequestDto;
import com.gavoza.backend.domain.comment.dto.response.CommentsResponseDto;
import com.gavoza.backend.domain.comment.service.CommentService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    //comment 조회
    @GetMapping("/get/{postId}")
    public CommentsResponseDto getOneComment(@PathVariable Long postId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                             int size,
                                             int page) {

        if(Objects.isNull(userDetails)){
            return commentService.getCommentByPostId(page-1, size, postId, null);
        }

        User user = userDetails.getUser();
        return commentService.getCommentByPostId(page-1, size, postId, user);
    }

    //댓글 작성
    @PostMapping("/{id}") // post id
    public MessageResponseDto createComment(@PathVariable Long id,
                                            @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        commentService.createComment(id, requestDto, user);

        return new MessageResponseDto("댓글 등록 성공");
    }

    //댓글 수정
    @PutMapping("/{id}")
    public MessageResponseDto updateComment(@PathVariable Long id,
                                            @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        commentService.updateComment(id,requestDto,user);

        return new MessageResponseDto("댓글 수정 성공");
    }

    //댓글 삭제
    @DeleteMapping("/{id}")
    public MessageResponseDto deleteComment(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        commentService.deleteComment(id, user);

        return new MessageResponseDto("댓글 삭제 성공");
    }

    //댓글 좋아요
    @PostMapping("/like/{id}") //comment id
    public MessageResponseDto commentLike(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return commentService.commentLike(id, user);
    }
}
