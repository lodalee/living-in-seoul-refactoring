package com.gavoza.backend.domain.comment.controller;

import com.gavoza.backend.domain.comment.dto.response.CommentListResponse;
import com.gavoza.backend.domain.comment.dto.request.CommentRequestDto;
import com.gavoza.backend.domain.comment.dto.request.ReCommentRequestDto;
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
    public CommentListResponse getOneComment(@PathVariable Long postId,
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

    //대댓글 댓글 작성
    @PostMapping("/re/{id}") // comment id
    public MessageResponseDto createReComment(@PathVariable Long id,
                                                @RequestBody ReCommentRequestDto requestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        commentService.createReComment(id, requestDto, user);

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

    //대댓글 수정
    @PutMapping("/re/{id}") //reComment id
    public MessageResponseDto updateReComment(@PathVariable Long id,
                                              @RequestBody ReCommentRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        commentService.updateReComment(id,requestDto,user);

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

    //대댓글 삭제
    @DeleteMapping("/re/{id}")
    public MessageResponseDto deleteReComment(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        commentService.deleteReComment(id, user);

        return new MessageResponseDto("댓글 삭제 성공");
    }
}
