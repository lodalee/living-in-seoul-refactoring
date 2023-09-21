package com.gavoza.backend.domain.post.service;

import com.gavoza.backend.domain.post.repository.PostLikeRepository;
import com.gavoza.backend.domain.post.dto.response.*;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.post.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional //(readOnly = true)
public class PostNonUserService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final ReportRepository reportRepository;

    //유저 상세 게시물 조회
    public PostResponseDto getOnePost(Long postId, User user) {
        Post post = findPost(postId);
        post.increaseViewCount();

        PostUserDto postUserDto = new PostUserDto(post.getUser().getNickname(), post.getUser().getEmail(), post.getUser().getProfileImageUrl());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());

        if(Objects.isNull(user)){
            return new PostResponseDto("게시글 조회 성공", new PostResultDto(postUserDto, postInfoResponseDto, locationResponseDto,false,false, false));
        }

        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        boolean hasReported = reportRepository.existsReportByPostAndUser(post,user);
        return new PostResponseDto("게시글 조회 성공", new PostResultDto(postUserDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped, hasReported));
    }

    //게시물 전체 조회
    public PostsResponseDto getPost(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPages = postRepository.findAll(pageable);

        List<PostResultDto> postResultDtos = postPages.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostsResponseDto("게시물 조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
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

    //주어진 게시물 ID에 해당하는 게시물 조회
    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다."));
    }

    // 태그 인기순위
    public List<String> rankNumber(String category) {
        List<Post> postList;

        if (category == null || category.isEmpty()) {
            postList = postRepository.findAll();
        } else {
            postList = postRepository.findAllBycategory(category);
        }

        List<String> rankedIds = getHashTagsFromPosts(postList);
        return getTopRankedTags(rankedIds, 6);
    }

    // 태그별 포스트
    public PostsResponseDto tagsPosts(int size, int page, String hashtagName, String type, String category, User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage;

        if (category == null || category.isEmpty()) {
            postPage = type.equals("popular")
                    ? postRepository.findAllByHashtagContainingOrderByPostViewCountDesc(hashtagName, pageable)
                    : postRepository.findAllByHashtagContainingOrderByCreatedAtDesc(hashtagName, pageable);
        } else {
            postPage = type.equals("popular")
                    ? postRepository.findAllByCategoryAndHashtagContainingOrderByPostViewCountDesc(category, hashtagName, pageable)
                    : postRepository.findAllByCategoryAndHashtagContainingOrderByCreatedAtDesc(category, hashtagName, pageable);
        }

        if (!postPage.hasContent()) {
            throw new IllegalArgumentException("해당 게시물은 존재하지 않습니다.");
        }

        List<PostResultDto> postResultDtos = postPage.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());
        return new PostsResponseDto("검색 조회 성공", postPage.getTotalPages(), postPage.getTotalElements(), size, postResultDtos);
    }

    // 태그별 포스트 - +위치
    public PostsResponseDto postLocation(int size, int page, String gu, String category, User user) {
        System.out.println("gu " + gu);
        System.out.println("category " + category);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage;

        if (category == null || category.isEmpty()) {
            postPage = postRepository.findAllByGu(gu, pageable);
            System.out.println("카테고리 없을 때");
        } else {
            postPage = postRepository.findAllByGuAndCategory(gu, category, pageable);
            System.out.println("카테고리 있을 때");
        }

        List<PostResultDto> postResultDtos =
                postPage.getContent().stream()
                        .map(post -> mapToPostResultDto(post, user))
                        .collect(Collectors.toList());

        return new PostsResponseDto("검색 조회 성공", postPage.getTotalPages(), postPage.getTotalElements(), size, postResultDtos);
    }

    //주어진 리스트에서 상위 N개의 요소를 선택하는 메서드
    private List<String> getTopRankedTags(List<String> rankedIds, int limit) {
        return rankedIds.stream().limit(limit).collect(Collectors.toList());
    }

    //postList에서 해시태그를 추출하고 인기순으로 정렬된 태그 목록을 반환
    private List<String> getHashTagsFromPosts(List<Post> postList) {
        Map<String, Integer> idFrequencyMap = new HashMap<>();

        for (Post post : postList) {
            if (Objects.isNull(post.getHashtag())) {
                continue;
            }

            String hashTag = post.getHashtag();
            String[] hashTagList = hashTag.split("#");
            for (String tagName : hashTagList) {
                if (!tagName.isEmpty()) {
                    idFrequencyMap.put(tagName, idFrequencyMap.getOrDefault(tagName, 0) + 1);
                }
            }
        }

        return idFrequencyMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}


