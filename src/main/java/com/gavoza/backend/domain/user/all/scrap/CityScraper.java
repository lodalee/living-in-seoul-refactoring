package com.gavoza.backend.domain.user.all.scrap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CityScraper {

    private static final String WIKIPEDIA_URL = "https://ko.wikipedia.org/wiki/%EB%8C%80%ED%95%9C%EB%AF%BC%EA%B5%AD%EC%9D%98_%EC%9D%B8%EA%B5%AC%EC%88%9C_%EB%8F%84%EC%8B%9C_%EB%AA%A9%EB%A1%9D";

    public List<String> getCities() {
        List<String> cities = new ArrayList<>();

        try {
            // Jsoup으로 웹 페이지 가져오기
            Document doc = Jsoup.connect(WIKIPEDIA_URL).get();

            // HTML 문서에서 테이블 찾기 (Selector를 사용하여 테이블 요소만 가져옴)
            Element table = doc.select("table.wikitable").first();

            // table 요소가 null 일 경우 처리
            if (table == null) {
                System.err.println("테이블을 찾을 수 없습니다.");
                return cities;
            }

            // 각 행을 반복하고 도시 이름을 목록에 추가
            Elements rows = table.select("tr");
            for (int i = 1; i < rows.size(); i++) { // 첫 행 (헤더)를 제외하고 시작
                Element row = rows.get(i);
                Elements cells = row.select("td");

                String cityName = cells.get(1).text(); // 인덱스 1: 도시 이름
                cities.add(cityName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cities;
    }

    public static void main(String[] args) {
        CityScraper cityScraper = new CityScraper();
        List<String> cities = cityScraper.getCities();

        // 결과 출력
        for (String city : cities) {
            System.out.println(city);
        }
    }
}
