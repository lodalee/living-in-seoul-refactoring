package com.gavoza.backend.domain.alarm.dto.response;

import lombok.Getter;

import java.util.List;


@Getter
public class AlarmsResponseDto {
    private String msg;
    private AlarmPageable pageable;
    private List<AlarmResponseDto> alarmList;

    public AlarmsResponseDto(
            String msg,
            int totalPages,
            long totalElements,
            int size,
            List<AlarmResponseDto> alarmList
    ){
        this.msg = msg;
        this.pageable = new AlarmPageable(totalPages, totalElements, size);
        this.alarmList = alarmList;
    }

    @Getter
    private class AlarmPageable{
        private int totalPages;
        private long totalElements;
        private int size;

        public AlarmPageable(int totalPages, long totalElements, int size){
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.size = size;
        }
    }
}
