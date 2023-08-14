package com.gavoza.backend.domain.tag.service;

import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.tag.dto.LocationPostResponseDto;
import com.gavoza.backend.domain.tag.dto.LocationTagResponseDto;
import com.gavoza.backend.domain.tag.dto.PurposePostResponseDto;
import com.gavoza.backend.domain.tag.dto.PurposeTagResponseDto;
import lombok.RequiredArgsConstructor;
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
}