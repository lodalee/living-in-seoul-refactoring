package com.gavoza.backend.domain.tag.service;

import com.gavoza.backend.domain.post.dto.LocationResponseDto;
import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.tag.dto.hashtagPostResponseDto;
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
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
    public List<String> allRankNumber(String gu, String dong) {

        Map<String, Integer> idFrequencyMap = new HashMap<>();
        List<String> hashTagResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAllByGuAndDong(gu,dong);
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
        //엔트리의 값을 내림차순으로 비교합니다. entry2의 값이 entry1의 값보다 크면 양수를 반환하고, 반대의 경우 음수를 반환하며 같으면 0을 반환합니다.
        //따라서, 이 정렬을 통해 언급 횟수가 큰 엔트리가 리스트의 앞쪽으로 오게 됩니다.
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
    public List<String> categoryRankNumer(String category, String gu, String dong) {

        Map<String, Integer> idFrequencyMap = new HashMap<>();
        List<String> hashTagResponseDtos = new ArrayList<>();

        List<Post> postList = postRepository.findAllBycategoryAndGuAndDong(category, gu, dong);
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
    public PostListResponse hashtagPostResponseDtos(int size, int page, String hashtagName,String type, String gu, String dong) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<hashtagPostResponseDto> hashtagPostResponseDtos = new ArrayList<>();

        List<PostResultDto> postResultDtos = new ArrayList<>();

        Page<Post> postPage = type.equals("popular")
                    ?postRepository.findAllByHashtagContainingAndGuAndDongOrderByPostViewCountDesc(hashtagName,pageable,gu, dong)
                    :postRepository.findAllByHashtagContainingAndGuAndDongOrderByCreatedAtDesc(hashtagName,pageable, gu, dong);

        if (postPage == null) {
            throw new IllegalArgumentException("존재하지 않는 태그입니다.");
        }

        for (Post checkHashTagName : postPage) {
            String[] checkHashTagNames = checkHashTagName.getHashtag().split("#");
            UserResponseDto userResponseDto = new UserResponseDto(checkHashTagName.getUser());
            PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(checkHashTagName);
            LocationResponseDto locationResponseDto = new LocationResponseDto(checkHashTagName.getGu(),checkHashTagName.getDong(),checkHashTagName.getLat(),checkHashTagName.getLng());

            postResultDtos.add(new PostResultDto(userResponseDto, postInfoResponseDto,locationResponseDto));

            //이제 확인해
            for (int i = 0; i < checkHashTagNames.length; i++) {
                if (hashtagName.equals(checkHashTagNames[i])) {
                    hashtagPostResponseDtos.add(new hashtagPostResponseDto(checkHashTagName, hashtagName));
                    break;
                }
            }
        }
        return new PostListResponse("검색 조회 성공", postPage.getTotalPages(), postPage.getTotalElements(), size , postResultDtos);
    }

    //카테고리별 인기 순위 태그 post 조회
    public PostListResponse categoryHashtagPostResponseDtos(int size, int page, String hashtagName, String category, String type, String gu, String dong) {
        // 페이지 및 사이즈 계산
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<hashtagPostResponseDto> hashtagPostResponseDtos = new ArrayList<>();

        List<PostResultDto> postResultDtos = new ArrayList<>();

        Page<Post> postPage = type.equals("popular")
                ? postRepository.findAllByCategoryAndHashtagContainingAndGuAndDongOrderByPostViewCountDesc(category,hashtagName,pageable,gu, dong)
                : postRepository.findAllByCategoryAndHashtagContainingAndGuAndDongOrderByCreatedAtDesc(category,hashtagName, pageable, gu, dong);

        if (postPage == null) {
            throw new IllegalArgumentException("존재하지 않는 태그 혹은 존재하지 않는 카테고리 입니다.");
        }

        for (Post checkhashtagName : postPage) {
            String[] checkhashTagNames = checkhashtagName.getHashtag().split("#");
            UserResponseDto userResponseDto = new UserResponseDto(checkhashtagName.getUser());
            PostInfoResponseDto postInfoResponseDto = new PostInfoResponseDto(checkhashtagName);
            LocationResponseDto locationResponseDto = new LocationResponseDto(checkhashtagName.getGu(),checkhashtagName.getDong(),checkhashtagName.getLat(),checkhashtagName.getLng());

            postResultDtos.add(new PostResultDto(userResponseDto, postInfoResponseDto,locationResponseDto));


            for (int i = 0; i < checkhashTagNames.length; i++) {
                if (hashtagName.equals(checkhashTagNames[i])) {
                    hashtagPostResponseDtos.add(new hashtagPostResponseDto(checkhashtagName, hashtagName));
                    break;
                }
            }
        }
        return new PostListResponse("검색 조회 성공", postPage.getTotalPages(), postPage.getTotalElements(), size , postResultDtos);
    }
}
