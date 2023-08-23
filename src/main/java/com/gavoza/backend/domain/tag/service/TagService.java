package com.gavoza.backend.domain.tag.service;

import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.post.dto.LocationResponseDto;
import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
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
@Transactional(readOnly = true)
public class TagService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    //전체 - 태그 순위
    public List<String> allRankNumber() {
        List<Post> postList = postRepository.findAll();
        List<String> rankedIds = getHashTagsFromPosts(postList);

        return getTopRankedTags(rankedIds, 6);
    }

    //카테고리 - 태그 순위
    public List<String> categoryRankNumer (String category) {
        List<Post> postList = postRepository.findAllBycategory(category);
        List<String> rankedIds = getHashTagsFromPosts(postList);

        return getTopRankedTags(rankedIds, 6);
    }

    //전체 - 태그별 post
    public PostListResponse hashtagPostResponseDtos (int size, int page, String hashtagName, String type, User user
    ) {
        Pageable pageable = PageRequest.of(page, size);

        //인기순/최신순
        Page<Post> postPage = type.equals("popular")
                ? postRepository.findAllByHashtagContainingOrderByPostViewCountDesc(hashtagName, pageable)
                : postRepository.findAllByHashtagContainingOrderByCreatedAtDesc(hashtagName, pageable);

        if (!postPage.hasContent()) {
            throw new IllegalArgumentException("존재하지 않는 태그입니다.");
        }

        List<PostResultDto> postResultDtos =
                postPage.getContent().stream()
                        .map(post -> mapToPostResultDto(post,user))
                        .collect(Collectors.toList());
        return new PostListResponse("검색 조회 성공", postPage.getTotalPages(), postPage.getTotalElements(), size, postResultDtos);
    }

    //카테고리 - 태그별 포스트
    public PostListResponse categoryHashtagPostResponseDtos (int size, int page, String hashtagName, String category, String type, User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = type.equals("popular")
                ? postRepository.findAllByCategoryAndHashtagContainingOrderByPostViewCountDesc(category, hashtagName, pageable)
                : postRepository.findAllByCategoryAndHashtagContainingOrderByCreatedAtDesc(category, hashtagName, pageable);

        if (!postPage.hasContent()) {
            throw new IllegalArgumentException("존재하지 않는 태그 혹은 존재하지 않는 카테고리 입니다.");
        }

        List<PostResultDto> postResultDtos = postPage.stream()
                .map(post -> mapToPostResultDto(post,user))
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
        LocationResponseDto locationResponseDto = new LocationResponseDto(post.getGu(), post.getDong(), post.getLat(), post.getLng());
        if (Objects.isNull(user)){
            return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, false);
        }
        boolean hasLikedPost = postLikeRepository.existsLikeByPostAndUser(post, user);
        return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto,hasLikedPost);
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
