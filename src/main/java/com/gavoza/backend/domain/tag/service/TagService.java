package com.gavoza.backend.domain.tag.service;

import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.tag.dto.hashtagPostResponseDto;
import com.gavoza.backend.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final PostRepository postRepository;

    //인기 순위 태그 조회(전체)
    public List<String> allRankNumber() {

        Map<String, Integer> idFrequencyMap = new HashMap<>();
        List<String> hashTagResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            if (Objects.isNull(post.getHashtag())) {
                continue;
            }

            String hashTag = post.getHashtag(); //#안녕#시바견
            String[] hashTagList = hashTag.split("#");
            for (String tagName : hashTagList) {
                if (tagName == "") {
                    continue;
                }
                idFrequencyMap.put(tagName, idFrequencyMap.getOrDefault(tagName, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(idFrequencyMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<String> rankedIds = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            rankedIds.add(entry.getKey());
        }

        for (int i = 0; i < rankedIds.size(); i++) {
            if (i >= 6) {
                break;
            }
            hashTagResponseDtos.add(rankedIds.get(i));
        }
        return hashTagResponseDtos;
    }

    //카테고리별 인기 순위 태그 조회
    public List<String> categoryRankNumer(String category) {

        Map<String, Integer> idFrequencyMap = new HashMap<>();
        List<String> hashTagResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAllBycategory(category);
        for (Post post : postList) {
            if (Objects.isNull(post.getHashtag())) {
                continue;
            }

            String hashTag = post.getHashtag();
            String[] hashTagList = hashTag.split("#");
            for (String tagName : hashTagList) {
                if (tagName == "") {
                    continue;
                }
                idFrequencyMap.put(tagName, idFrequencyMap.getOrDefault(tagName, 0) + 1);
            }
        }
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(idFrequencyMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<String> rankedIds = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            rankedIds.add(entry.getKey());
        }

        for (int i = 0; i < rankedIds.size(); i++) {
            if (i >= 6) {
                break;
            }
            hashTagResponseDtos.add(rankedIds.get(i));
        }
        return hashTagResponseDtos;
    }


    //인기 순위 태그별 post 조회(전체)
    public List<hashtagPostResponseDto> hashtagPostResponseDtos(int limit, String hashtagName, String type) {
        List<hashtagPostResponseDto> hashtagPostResponseDtos = new ArrayList<>();

        List<Post> postList = type.equals("popular")
                    ?postRepository.findAllByHashtagContainingOrderByPostViewCountDesc(hashtagName)
                    :postRepository.findAllByHashtagContainingOrderByCreatedAtDesc(hashtagName);

        if (postList == null) {
            throw new IllegalArgumentException("존재하지 않는 태그입니다.");
        }

        // createdAt을 기준으로 내림차순 정렬
        postList.sort(Comparator.comparing(Post::getCreatedAt).reversed());

        for (Post checkLocationTagName : postList) {
            String[] checkhashTagNames = checkLocationTagName.getHashtag().split("#");

            //이제 확인해
            for (int i = 0; i < checkhashTagNames.length; i++) {
                if (hashtagName.equals(checkhashTagNames[i])) {
                    hashtagPostResponseDtos.add(new hashtagPostResponseDto(checkLocationTagName, hashtagName));
                    break;
                }
            }
            if (hashtagPostResponseDtos.size() >= limit) {
                break;
            }
        }
        return hashtagPostResponseDtos;
    }

    //카테고리별 인기 순위 태그 post 조회
    public List<hashtagPostResponseDto> categoryHashtagPostResponseDtos(int limit, String hashtagName, String category, String type) {

        List<hashtagPostResponseDto> hashtagPostResponseDtos = new ArrayList<>();

        List<Post> postList = type.equals("popular")
                ? postRepository.findAllByCategoryAndHashtagContainingOrderByPostViewCountDesc(category,hashtagName)
                : postRepository.findAllByCategoryAndHashtagContainingOrderByCreatedAtDesc(category,hashtagName);

        if (postList == null) {
            throw new IllegalArgumentException("존재하지 않는 태그 혹은 존재하지 않는 카테고리 입니다.");
        }

        for (Post checkhashtagName : postList) {
            String[] checkhashTagNames = checkhashtagName.getHashtag().split("#");

            //이제 확인해
            for (int i = 0; i < checkhashTagNames.length; i++) {
                if (hashtagName.equals(checkhashTagNames[i])) {
                    hashtagPostResponseDtos.add(new hashtagPostResponseDto(checkhashtagName, hashtagName));
                    break;
                }
            }
            if (hashtagPostResponseDtos.size() >= limit) {
                break;
            }
        }
        return hashtagPostResponseDtos;
    }
}

//        //커뮤니티 전체조회
//        public PostListResponse gethashtagPost ( int page, int size, String hashtagName){
//            // 페이지 요청을 생성하고, 날짜를 기준으로 내림차순 정렬 설정
//            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//            // 입력된 위치 태그를 포함하는 게시물을 페이지네이션하여 조회
//            Page<Post> postPages = postRepository.findAllByHashtagContaining(hashtagName, pageable);
//
//            if (postPages.isEmpty()) {
//                throw new IllegalArgumentException("존재하지 않는 태그입니다.");
//            }
//
//            // 조회 결과를 담을 리스트 초기화
//            List<PostResultDto> postResultDtos = new ArrayList<>();
//
//            // 조회된 게시물 페이지를 순회
//            for (Post post : postPages) {
//                // 게시물의 위치 태그를 '#' 문자를 기준으로 분리
//                String[] checkHashtagNames = post.getHashtag().split("#");
//
//                // 분리된 태그들 중 입력된 위치 태그와 일치하는지 확인
//                if (Arrays.asList(checkHashtagNames).contains(hashtagName)) {
//                    UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
//                    PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
//                    postResultDtos.add(new PostResultDto(userResponseDto, postInfoResponseDto));
//                }
//            }
//
//            // 최종적으로 검색 결과와 페이징 정보를 담은 응답을 생성하여 반환
//            return new PostListResponse("검색 조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
//        }
//    }
