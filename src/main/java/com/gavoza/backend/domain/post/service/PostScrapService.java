package com.gavoza.backend.domain.post.service;

import com.gavoza.backend.domain.post.dto.response.*;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostLikeRepository;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.entity.PostScrap;
import com.gavoza.backend.domain.post.repository.PostScrapRepository;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.user.entity.User;
import com.gavoza.backend.global.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final ReportRepository reportRepository;

    //post 스크랩
    public MessageResponseDto postScrap(Long postId, User user){
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
        );

        if (!postScrapRepository.existsScrapByPostAndUser(post, user)){
            LocalDateTime scrapedAt = LocalDateTime.now(); // 현재 시간을 스크랩 시간으로 설정
            PostScrap scrap = new PostScrap(post, user, scrapedAt) ;
            postScrapRepository.save(scrap);
            return new MessageResponseDto("스크랩");
        }

        PostScrap scrap = postScrapRepository.findByPostAndUser(post, user).orElseThrow(
                ()-> new IllegalArgumentException("스크랩에 대한 정보가 존재하지 않습니다."));
        postScrapRepository.delete(scrap);
        return new MessageResponseDto("스크랩 취소");
    }

    // 사용자가 스크랩한 글 조회
    public PostsResponseDto getMyScrap(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scrapedAt"));
        Page<PostScrap> scrapPages = postScrapRepository.findAllByUser(user, pageable);

        // 스크랩한 글을 가져와서 리스트로 변환
        List<Post> myScrapPosts = scrapPages.getContent().stream()
                .map(PostScrap::getPost)
                .collect(Collectors.toList());

        // 가져온 글을 PostResultDto로 매핑하여 리스트로 변환
        List<PostResultDto> postResultDtos = myScrapPosts.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostsResponseDto("스크랩한 글 조회 성공", scrapPages.getTotalPages(), scrapPages.getTotalElements(), size, postResultDtos);
    }

    //PostResultDto 타입으로 반환
    private PostResultDto mapToPostResultDto(Post post, User user) {
        PostUserDto postUserDto = new PostUserDto(post.getUser());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());
        if (Objects.isNull(user)) {
            return new PostResultDto(postUserDto, postInfoResponseDto, locationResponseDto, false,false,false);
        }
        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        boolean hasReported = reportRepository.existsReportByPostAndUser(post,user);
        return new PostResultDto(postUserDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped, hasReported);
    }
}