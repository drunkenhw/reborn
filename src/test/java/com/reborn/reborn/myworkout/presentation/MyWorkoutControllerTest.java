package com.reborn.reborn.myworkout.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reborn.reborn.config.ControllerConfig;
import com.reborn.reborn.myworkout.presentation.dto.MyWorkoutResponse;
import com.reborn.reborn.common.presentation.dto.Slice;
import com.reborn.reborn.member.domain.Member;
import com.reborn.reborn.member.domain.MemberRole;
import com.reborn.reborn.myworkout.application.MyWorkoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;

import static com.reborn.reborn.myworkout.presentation.dto.MyWorkoutResponse.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MyWorkoutControllerTest extends ControllerConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MyWorkoutService myWorkoutService;

    @Test
    @WithUserDetails(value = "email@naver.com")
    @DisplayName("내 운동 리스트 페이지 조회 : Get /api/v1/workouts/me")
    void getPagingWorkout() throws Exception {
        //given
        Member member = Member.builder().email("user").memberRole(MemberRole.USER).build();
        List<MyWorkoutResponse> list = new ArrayList<>();
        MyWorkoutResponse dto = new MyWorkoutResponse(1L, 1L, "운동명", "파일명");
        list.add(dto);
        given(myWorkoutService.getMyWorkoutList(any(), any())).willReturn(new Slice<>(list));

        //when
        mockMvc.perform(get("/api/v1/workouts/me")
                        .header("Authorization", "Bearer " + getToken(member))
                        .queryParam("id", "1").queryParam("category", "BACK")
                        .queryParam("title","벤치 프레스").queryParam("author","nickname"))
                .andExpect(status().isOk())
                .andDo(document("my-workout-getPagingList",
                        requestParameters(
                                parameterWithName("id").description("마지막으로 받은 운동 Id"),
                                parameterWithName("category").description("운동 카테고리"),
                                parameterWithName("title").description("운동 제목"),
                                parameterWithName("author").description("운동 작성자")
                        ),
                        responseFields(
                                fieldWithPath("page").type(ARRAY).description("페이지에 출력할 List"),
                                fieldWithPath("hasNext").type(BOOLEAN).description("출력할 내용이 더 있는지")
                        ).andWithPrefix("page[].",
                                fieldWithPath("workoutName").type(STRING).description("운동 이름"),
                                fieldWithPath("workoutId").type(NUMBER).description("운동 ID"),
                                fieldWithPath("myWorkoutId").type(NUMBER).description("내 운동 ID"),
                                fieldWithPath("uploadFileName").type(STRING).description("업로드 파일 이름")
                        )));
    }
    @Test
    @WithUserDetails(value = "email@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("내 운동 추가 : POST /api/v1/workouts/me/{workoutId}")
    void workoutCreate() throws Exception {
        //given
        Member member = Member.builder().email("user").memberRole(MemberRole.USER).build();
        given(myWorkoutService.addMyWorkout(any(), any())).willReturn(1L);

        //when
        mockMvc.perform(post("/api/v1/workouts/me/{workoutId}", 1L)
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isCreated())
                .andDo(document("my-workout-addMyWorkout",
                        pathParameters(
                                parameterWithName("workoutId").description("운동 정보 Id")
                        )
                ));
    }

    @Test
    @WithUserDetails(value = "email@naver.com")
    @DisplayName("내 운동 삭제 : Delete /api/v1/workouts/me/{workoutId}")
    void deleteWorkout() throws Exception {
        //given
        Member member = Member.builder().email("user").nickname("nickname").memberRole(MemberRole.USER).build();

        doNothing().when(myWorkoutService).deleteMyWorkout(any(), any());
        //when
        mockMvc.perform(delete("/api/v1/workouts/me/{workoutId}", 1L)
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isNoContent())
                .andDo(document("my-workout-delete",
                        pathParameters(
                                parameterWithName("workoutId").description("운동 정보 Id")
                        )
                ));
    }

    @Test
    @WithUserDetails(value = "email@naver.com")
    @DisplayName("내 운동 프로그램 : Get /api/v1/workouts/me/program")
    void getMyWorkoutProgram() throws Exception {
        //given
        Member member = Member.builder().email("user").memberRole(MemberRole.USER).build();
        List<MyWorkoutResponse> list = new ArrayList<>();
        MyWorkoutResponse myWorkout = builder().workoutId(1L).myWorkoutId(1L).workoutName("pull up")
                .uploadFileName("uuid.png")
                .build();
        list.add(myWorkout);
        given(myWorkoutService.getMyProgram(any(), any())).willReturn(new MyWorkoutList(list));

        //when
        mockMvc.perform(get("/api/v1/workouts/me/program")
                        .header("Authorization", "Bearer " + getToken(member))
                        .queryParam("category", "BACK"))
                .andExpect(status().isOk())
                .andDo(document("my-workout-getMyProgram",
                        requestParameters(
                                parameterWithName("category").description("운동 카테고리")
                        ),
                        responseFields(
                                fieldWithPath("list").type(ARRAY).description("페이지에 출력할 List")
                        ).andWithPrefix("list[].",
                                fieldWithPath("workoutName").type(STRING).description("운동 이름"),
                                fieldWithPath("myWorkoutId").type(NUMBER).description("운동 ID"),
                                fieldWithPath("workoutId").type(NUMBER).description("운동 ID"),
                                fieldWithPath("uploadFileName").type(STRING).description("업로드 파일 이름")
                        )));
    }
}