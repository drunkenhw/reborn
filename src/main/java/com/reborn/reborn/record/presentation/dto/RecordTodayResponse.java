package com.reborn.reborn.record.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
public class RecordTodayResponse implements Serializable {
    private long back;
    private long chest;
    private long lowerBody;
    private long core;

    public RecordTodayResponse(Long back, Long chest, Long lowerBody, Long core) {
        this.back = back;
        this.chest = chest;
        this.lowerBody = lowerBody;
        this.core = core;
    }


}
