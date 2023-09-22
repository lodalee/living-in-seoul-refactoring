package com.gavoza.backend.domain.comment.controller;

import com.gavoza.backend.domain.comment.dto.request.ReCommentRequestDto;
import com.gavoza.backend.domain.comment.service.ReCommentService;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import com.gavoza.backend.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recomment")
public class ReCommentController {

    private final ReCommentService reCommentService;

    //대댓글 댓글 작성
    @PostMapping("/{id}") // comment id
    public MessageResponseDto createReComment(@PathVariable Long id,
                                              @RequestBody ReCommentRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        reCommentService.createReComment(id, requestDto, user);

        return new MessageResponseDto("댓글 등록 성공");
    }

    //대댓글 수정
    @PutMapping("/{id}") //reComment id
    public MessageResponseDto updateReComment(@PathVariable Long id,
                                              @RequestBody ReCommentRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        reCommentService.updateReComment(id,requestDto,user);

        return new MessageResponseDto("댓글 수정 성공");
    }

    //대댓글 삭제
    @DeleteMapping("{id}")
    public MessageResponseDto deleteReComment(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        reCommentService.deleteReComment(id, user);

        return new MessageResponseDto("댓글 삭제 성공");
    }

    //대댓글 좋아요
    @PostMapping("/like/{id}") //recomment id
    public MessageResponseDto recommentLike(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        return reCommentService.recommentLike(id,user);
    }
}
