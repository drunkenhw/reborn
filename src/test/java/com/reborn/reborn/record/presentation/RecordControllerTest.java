package com.reborn.reborn.record.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reborn.reborn.config.ControllerConfig;
import com.reborn.reborn.config.WithUserDetailsCustom;
import com.reborn.reborn.member.domain.Member;
import com.reborn.reborn.record.presentation.dto.RecordRequest;
import com.reborn.reborn.record.presentation.dto.RecordTodayResponse;
import com.reborn.reborn.record.presentation.dto.RecordWeekResponse;
import com.reborn.reborn.workout.domain.Workout;
import com.reborn.reborn.record.application.RecordService;
import com.reborn.reborn.workout.domain.WorkoutCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.reborn.reborn.record.presentation.dto.RecordRequest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecordControllerTest extends ControllerConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RecordService recordService;


    @Test
    @WithUserDetailsCustom
    @DisplayName("기록 생성 : POST /api/v1/records")
    void recordCreate() throws Exception {
        //given
        Member member = getMember();
        Workout workout = Workout.builder().member(member).build();
        List<RecordRequest> list = new ArrayList<>();
        list.add(new RecordRequest(1L, 10L, WorkoutCategory.BACK));

        RecordRequestList request = new RecordRequestList(list);
        willDoNothing().given(recordService).create(request);

        //when
        mockMvc.perform(post("/api/v1/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isCreated())
                .andDo(document("record-create"
                ));
    }


    @Test
    @WithUserDetailsCustom
    @DisplayName("오늘 기록 조회 : GET /api/v1/records/day")
    void getTodayRecord() throws Exception {
        //given
        Member member = getMember();
        RecordTodayResponse response = new RecordTodayResponse(10L, 20L, 30L, 40L);

        given(recordService.getTodayRecord(any(), any())).willReturn(response);
        //when
        mockMvc.perform(get("/api/v1/records/day")
                        .queryParam("date", "2022-10-07")
                        .header("Authorization", "Bearer " + getToken(member))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("record-get-today",
                        responseFields(
                                fieldWithPath("back").type(NUMBER).description("back total"),
                                fieldWithPath("chest").type(NUMBER).description("chest total"),
                                fieldWithPath("lowerBody").type(NUMBER).description("lower body total"),
                                fieldWithPath("core").type(NUMBER).description("core total")
                        )));
    }

    @Test
    @WithUserDetailsCustom
    @DisplayName("주간 기록 조회 : GET /api/v1/records/week")
    void getWeekRecord() throws Exception {
        //given
        Member member = getMember();
        RecordWeekResponse response = new RecordWeekResponse(10, 20, 10, 30, 40, 50, 60);
        given(recordService.getWeekRecord(any(),any())).willReturn(response);
        //when
        mockMvc.perform(get("/api/v1/records/week")
                        .queryParam("date", "2022-10-07"))
                .andExpect(status().isOk())
                .andDo(document("record-get-week",
                        requestParameters(
                                parameterWithName("date").description("날짜")
                        ),
                        responseFields(
                                fieldWithPath("mon").type(NUMBER).description("월요일 통계"),
                                fieldWithPath("tue").type(NUMBER).description("화요일 통계"),
                                fieldWithPath("wed").type(NUMBER).description("수요일 통계"),
                                fieldWithPath("thu").type(NUMBER).description("목요일 통계"),
                                fieldWithPath("fri").type(NUMBER).description("금요일 통계"),
                                fieldWithPath("sat").type(NUMBER).description("토요일 통계"),
                                fieldWithPath("sun").type(NUMBER).description("일요일 통계")
                        )));
    }
}