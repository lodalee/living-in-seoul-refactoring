package com.gavoza.backend.domain.tag.service;

import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.alarm.repository.SubscribeHashtagRepository;
import com.gavoza.backend.domain.post.dto.LocationResponseDto;
import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.scrap.repository.PostScrapRepository;
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import com.gavoza.backend.domain.user.all.entity.User;
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
@Transactional(readOnly = true)
public class TagService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final ReportRepository reportRepository;
    private final SubscribeHashtagRepository subscribeHashtagRepository;

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
    public PostListResponse tagsPosts(int size, int page, String hashtagName, String type, String category, User user) {
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
        return new PostListResponse("검색 조회 성공", postPage.getTotalPages(), postPage.getTotalElements(), size, postResultDtos);
    }

    // 태그별 포스트 - +위치
    public PostListResponse postLocation(int size, int page, String gu, String category, User user) {
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

        return new PostListResponse("검색 조회 성공", postPage.getTotalPages(), postPage.getTotalElements(), size, postResultDtos);
    }

    //주어진 리스트에서 상위 N개의 요소를 선택하는 메서드
    private List<String> getTopRankedTags(List<String> rankedIds, int limit) {
        return rankedIds.stream().limit(limit).collect(Collectors.toList());
    }

    //PostResultDto 객체로 변환하는 메서드
    private PostResultDto mapToPostResultDto(Post post, User user) {
        UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
        PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getLname(), post.getAddress(), post.getLat(), post.getLng(), post.getGu());
        if (Objects.isNull(user)){
            return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, false,false,false);
        }
        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        boolean hasScrapped = postScrapRepository.existsScrapByPostAndUser(post, user);
        boolean hasReported = reportRepository.existsReportByPostAndUser(post,user);
        return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto,hasLikedPost,hasScrapped,hasReported);
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
