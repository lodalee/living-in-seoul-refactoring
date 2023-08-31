package com.gavoza.backend.domain.search.service;

import com.gavoza.backend.domain.Like.repository.PostLikeRepository;
import com.gavoza.backend.domain.post.dto.LocationResponseDto;
import com.gavoza.backend.domain.post.dto.PostInfoResponseDto;
import com.gavoza.backend.domain.post.dto.PostResultDto;
import com.gavoza.backend.domain.post.entity.Post;
import com.gavoza.backend.domain.post.repository.PostRepository;
import com.gavoza.backend.domain.post.response.PostListResponse;
import com.gavoza.backend.domain.report.repository.ReportRepository;
import com.gavoza.backend.domain.scrap.repository.PostScrapRepository;
import com.gavoza.backend.domain.search.entity.SearchLog;
import com.gavoza.backend.domain.search.repository.SearchRepository;
import com.gavoza.backend.domain.user.ToPost.UserResponseDto;
import com.gavoza.backend.domain.user.all.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final SearchRepository searchRepository;
    private final PostScrapRepository postScrapRepository;
    private final ReportRepository reportRepository;

    // 게시글 검색
    public PostListResponse searchPosts(int page, int size, String keyword, String category, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPages;

        if (category.isEmpty() || category == null) {
            postPages = keyword.contains("#")
                    ? postRepository.findAllByHashtagContaining(keyword, pageable)
                    : postRepository.findAllByContentContaining(keyword, pageable);
        } else {
            postPages = keyword.contains("#")
                    ? postRepository.findAllByHashtagContainingAndCategory(keyword, category, pageable)
                    : postRepository.findAllByContentContainingAndCategory(keyword, category, pageable);
        }

        List<PostResultDto> postResultDtos = postPages.stream()
                .map(post -> mapToPostResultDto(post, user))
                .collect(Collectors.toList());

        return new PostListResponse("검색 조회 성공", postPages.getTotalPages(), postPages.getTotalElements(), size, postResultDtos);
    }

    //검색 내용 수집
    public void saveSearch(String query) {
        SearchLog searchLog = new SearchLog();
        searchLog.setQuery(query);
        searchLog.setSearchTime(new Date()); // 검색 시간을 기록

        searchRepository.save(searchLog);
    }

    //하루 지나면 검색 내용 삭제
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void deleteOldSearches() {
        // 현재 날짜를 얻습니다.
        Date currentDate = new Date();

        // 오늘의 시작 시간을 구합니다.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date todayStartTime = calendar.getTime();

        // 오늘 이전의 검색 내용을 삭제합니다.
        searchRepository.deleteBySearchTimeBefore(todayStartTime);
    }

    //오늘의 가장 많이 검색된 태그 조회
    public List<String> todayRankNumber() {
        List<SearchLog> searchLogs = searchRepository.findAll();

        // 검색어(query) 빈도수를 계산합니다.
        Map<String, Integer> queryFrequencyMap = new HashMap<>();
        for (SearchLog log : searchLogs) {
            String query = log.getQuery();
            queryFrequencyMap.put(query, queryFrequencyMap.getOrDefault(query, 0) + 1);
        }

        // 빈도수에 따라 내림차순으로 정렬합니다.
        List<Map.Entry<String, Integer>> sortedEntries = queryFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // 가장 많이 검색된 태그를 추출합니다.
        List<String> rankedTags = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            rankedTags.add(entry.getKey());
        }

        return rankedTags;
    }


    //PostResultDto 타입으로 반환
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
        return new PostResultDto(userResponseDto, postInfoResponseDto, locationResponseDto, hasLikedPost,hasScrapped,hasReported);
    }
}
