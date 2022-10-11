package com.reborn.reborn.record.presentation.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class RecordWeekResponse implements Serializable {
    private long mon;
    private long tue;
    private long wed;
    private long thu;
    private long fri;
    private long sat;
    private long sun;

    @QueryProjection
    public RecordWeekResponse(long mon, long tue, long wed, long thu, long fri, long sat, long sun) {
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
    }
}
