package com.gavoza.backend.domain.tag.service;

import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.tag.dto.LocationPostResponseDto;
import com.gavoza.backend.domain.tag.dto.LocationTagResponseDto;
import com.gavoza.backend.domain.tag.dto.PurposePostResponseDto;
import com.gavoza.backend.domain.tag.dto.PurposeTagResponseDto;
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

    //인기 순위 태그 조회(위치)
    public List<LocationTagResponseDto> rankNumber() {

        Map<String, Integer> idFrequencyMap = new HashMap<>();
        List<LocationTagResponseDto> locationTagResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            if (Objects.isNull(post.getLocationTag())) {
                continue;
            }

            String locationTag = post.getLocationTag(); //#안녕#시바견
            String[] locationTagList = locationTag.split("#");
            for (String tagName : locationTagList) {
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
            locationTagResponseDtos.add(new LocationTagResponseDto(rankedIds.get(i)));
        }
        return locationTagResponseDtos;
    }

    //인기 순위 태그 조회(목적)
    public List<PurposeTagResponseDto> prankNumber() {
        Map<String, Integer> idFrequencyMap = new HashMap<>();
        List<PurposeTagResponseDto> purposeTagResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            if (Objects.isNull(post.getPurposeTag())) {
                continue;
            }
            String purposeTag = post.getPurposeTag();
            String[] purposeTagList = purposeTag.split("#");
            for (String tagName : purposeTagList) {
                if (tagName == "") {
                    continue;
                }
                idFrequencyMap.put(tagName, idFrequencyMap.getOrDefault(tagName, 0) + 1);
            }
        }

        //뽑아낸 <태그이름 , 빈도수> 를 정렬
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(idFrequencyMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        List<String> rankedIds = new ArrayList<>();
        //key, 태그의 이름들을 rankedIds 에 넣어준다.
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            rankedIds.add(entry.getKey());
        }

        //6개의 태그 이름들만 인기 순대로 보낸다.
        for (int i = 0; i < rankedIds.size(); i++) {
            if (i >= 6) {
                break;
            }
            purposeTagResponseDtos.add(new PurposeTagResponseDto(rankedIds.get(i)));
        }
        return purposeTagResponseDtos;
    }

    //인기 순위 태그별 post 조회(위치)
    public List<LocationPostResponseDto> locationPostResponseDtos(int limit, String locationTagName) {
        List<LocationPostResponseDto> locationPostResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAllByLocationTagContaining(locationTagName);

        if (postList == null) {
            throw new IllegalArgumentException("존재하지 않는 태그입니다.");
        }

        // createdAt을 기준으로 내림차순 정렬
        Collections.sort(postList, (post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));

        for (Post checkLocationTagName : postList) {
            String[] checkLocationTagNames = checkLocationTagName.getLocationTag().split("#");

            //이제 확인해
            for (int i = 0; i < checkLocationTagNames.length; i++) {
                if (locationTagName.equals(checkLocationTagNames[i])) {
                    locationPostResponseDtos.add(new LocationPostResponseDto(checkLocationTagName, locationTagName));
                    break;
                }
            }
            if (locationPostResponseDtos.size() >= limit) {
                break;
            }
        }
        return locationPostResponseDtos;
    }

    //인기 순위 태그별 post 조회(목적)
    public List<PurposePostResponseDto> purposePostResponseDtos(int limit, String purposeTagName) {
        List<PurposePostResponseDto> purposePostResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAllByPurposeTagContaining(purposeTagName);

        if (postList == null) {
            throw new IllegalArgumentException("존재하지 않는 태그입니다.");
        }

        // createdAt을 기준으로 내림차순 정렬
        Collections.sort(postList, (post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));

        for (Post checkPurposeTagName : postList) {
            String[] checkPurposeTagNames = checkPurposeTagName.getPurposeTag().split("#");

            for (int i = 0; i < checkPurposeTagNames.length; i++) {
                if (purposeTagName.equals(checkPurposeTagNames[i])) {
                    purposePostResponseDtos.add(new PurposePostResponseDto(checkPurposeTagName, purposeTagName));
                    break;
                }
            }
            if (purposePostResponseDtos.size() >= limit) {
                break;
            }
        }
        return purposePostResponseDtos;
    }

    //커뮤니티 전체조회(위치 태그)
    public PostListResponse getLocationPost(int page, int size, String locationTagName) {
        // 페이지 요청을 생성하고, 날짜를 기준으로 내림차순 정렬 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 입력된 위치 태그를 포함하는 게시물을 페이지네이션하여 조회
        Page<Post> postPages = postRepository.findAllByLocationTagContaining(locationTagName, pageable);

        if (postPages.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 태그입니다.");
        }

        // 조회 결과를 담을 리스트 초기화
        List<PostResultDto> postResultDtos = new ArrayList<>();

        // 조회된 게시물 페이지를 순회
        for (Post post : postPages) {
            // 게시물의 위치 태그를 '#' 문자를 기준으로 분리
            String[] checkLocationTagNames = post.getLocationTag().split("#");

            // 분리된 태그들 중 입력된 위치 태그와 일치하는지 확인
            if (Arrays.asList(checkLocationTagNames).contains(locationTagName)) {
                UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
                PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
                postResultDtos.add(new PostResultDto(userResponseDto, postInfoResponseDto));
            }
        }

        // 최종적으로 검색 결과와 페이징 정보를 담은 응답을 생성하여 반환
        return new PostListResponse("검색 조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
    }

    //커뮤니티 전체조회(목적 태그)
    public PostListResponse getPurposePost(int page, int size, String purposeTagName) {
        Pageable pageable= PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPages = postRepository.findAllByPurposeTagContaining(purposeTagName, pageable);

        if (postPages == null) {
            throw new IllegalArgumentException("존재하지 않는 태그입니다.");
        }

        List<PostResultDto> postResultDtos = new ArrayList<>();

        for (Post post : postPages) {
            String[] checkPurposeNames = post.getPurposeTag().split("#");

            if (Arrays.asList(checkPurposeNames).contains(purposeTagName)) {
                UserResponseDto userResponseDto = new UserResponseDto(post.getUser());
                PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(post);
                postResultDtos.add(new PostResultDto(userResponseDto, postInfoResponseDto));
            }
        }
        return new PostListResponse("검색 조회 성공",postPages.getTotalPages(),postPages.getTotalElements(), size, postResultDtos);
    }
}