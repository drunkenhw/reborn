package com.reborn.reborn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reborn.reborn.dto.*;
import com.reborn.reborn.entity.Member;
import com.reborn.reborn.entity.MemberRole;
import com.reborn.reborn.entity.Workout;
import com.reborn.reborn.entity.WorkoutCategory;
import com.reborn.reborn.service.WorkoutService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkoutControllerTest extends ControllerConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private WorkoutService workoutService;


    @Test
    @WithUserDetails(value = "email@naver.com")
    @DisplayName("운동 생성 : POST /api/v1/workout")
    void workoutCreate() throws Exception {
        //given
        Member member = Member.builder().email("user").memberRole(MemberRole.USER).build();
        List<FileDto> files = new ArrayList<>();
        files.add(new FileDto("원본 이름", "저장된 파일 이름"));
        WorkoutRequestDto workoutRequestDto = WorkoutRequestDto.builder()
                .workoutName("pull up")
                .content("광배 운동")
                .files(files)
                .workoutCategory("BACK").build();

        given(workoutService.createImage(any(), any())).willReturn(1L);
        given(workoutService.create(any(), any())).willReturn(Workout.builder().build());

        //when
        mockMvc.perform(post("/api/v1/workout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(workoutRequestDto))
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isCreated())
                .andDo(document("workout-create",
                        requestFields(
                                fieldWithPath("workoutName").type(STRING).description("운동 이름"),
                                fieldWithPath("content").type(STRING).description("운동 설명"),
                                fieldWithPath("workoutCategory").type(STRING).description("운동 부위"),
                                fieldWithPath("files").type(ARRAY).description("파일 정보"),
                                fieldWithPath("files[].originFileName").type(STRING).description("원본 파일 이름"),
                                fieldWithPath("files[].uploadFileName").type(STRING).description("저장된 파일 이름")
                        )
                ));
    }


    @Test
    @WithUserDetails(value = "email@naver.com")
    @DisplayName("운동 조회 : Get /api/v1/workout/{workoutId}")
    void getMyWorkout() throws Exception {
        //given
        Member member = Member.builder().email("user").nickname("nickname").memberRole(MemberRole.USER).build();
        List<FileDto> files = new ArrayList<>();
        files.add(new FileDto("원본 이름", "저장된 파일 이름"));
        WorkoutResponseDto workoutResponseDto = WorkoutResponseDto.builder()
                .id(1L)
                .workoutName("pull up")
                .workoutCategory(WorkoutCategory.BACK)
                .content("등 운동입니다.")
                .files(files)
                .memberId(1L)
                .memberNickname(member.getNickname())
                .build();

        given(workoutService.getWorkoutDetailDto(any(), any())).willReturn(workoutResponseDto);
        //when
        mockMvc.perform(get("/api/v1/workout/{workoutId}", 1L)
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isOk())
                .andDo(document("workout-getMyWorkout",
                        pathParameters(
                                parameterWithName("workoutId").description("운동 정보 Id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("운동 id"),
                                fieldWithPath("workoutName").type(STRING).description("운동 이름"),
                                fieldWithPath("content").type(STRING).description("운동 설명"),
                                fieldWithPath("files").type(ARRAY).description("파일 정보"),
                                fieldWithPath("files[].originFileName").type(STRING).description("원본 파일 이름"),
                                fieldWithPath("files[].uploadFileName").type(STRING).description("저장된 파일 이름"),
                                fieldWithPath("workoutCategory").type(STRING).description("운동 카테고리"),
                                fieldWithPath("memberId").type(NUMBER).description("작성자 Id"),
                                fieldWithPath("memberNickname").type(STRING).description("작성자 닉네임"),
                                fieldWithPath("author").type(BOOLEAN).description("작성자가 맞는지"),
                                fieldWithPath("isAuthor").type(BOOLEAN).description("작성자가 맞는지")
                        )

                ));
    }

    @Test
    @DisplayName("운동 리스트 페이지 조회 : Get /api/v1/workout")
    void getPagingWorkout() throws Exception {
        //given
        List<WorkoutListDto> list = new ArrayList<>();
        WorkoutListDto workoutResponseDto = WorkoutListDto.builder().workoutId(1L).workoutName("pull up")
                .uploadFileName("uuid.png")
                .build();
        list.add(workoutResponseDto);
        given(workoutService.pagingWorkoutWithSearchCondition(any())).willReturn(list);

        //when
        mockMvc.perform(get("/api/v1/workout")
                        .queryParam("id", "1").queryParam("category", "BACK"))
                .andExpect(status().isOk())
                .andDo(document("workout-getPagingList",
                        requestParameters(
                                parameterWithName("id").description("마지막으로 받은 운동 Id"),
                                parameterWithName("category").description("운동 카테고리")
                        ),
                        responseFields(
                                fieldWithPath("page").type(ARRAY).description("페이지에 출력할 List"),
                                fieldWithPath("hasNext").type(BOOLEAN).description("출력할 내용이 더 있는지")
                        ).andWithPrefix("page[].",
                                fieldWithPath("workoutName").type(STRING).description("운동 이름"),
                                fieldWithPath("workoutId").type(NUMBER).description("운동 ID"),
                                fieldWithPath("uploadFileName").type(STRING).description("업로드 파일 이름")
                        )));
    }

    @Test
    @WithUserDetails(value = "email@naver.com")
    @DisplayName("운동 삭제 : Delete /api/v1/workout/{workoutId}")
    void deleteWorkout() throws Exception {
        //given
        Member member = Member.builder().email("user").nickname("nickname").memberRole(MemberRole.USER).build();

        doNothing().when(workoutService).deleteWorkout(any(), any());
        //when
        mockMvc.perform(delete("/api/v1/workout/{workoutId}", 1L)
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isNoContent())
                .andDo(document("workout-delete",
                        pathParameters(
                                parameterWithName("workoutId").description("운동 정보 Id")
                        )
                ));
    }

    @Test
    @WithUserDetails(value = "email@naver.com")
    @DisplayName("운동 수정 : PATCH  /api/v1/workout/{workoutId}")
    void editWorkout() throws Exception {
        //given
        Member member = Member.builder().id(1L).email("user").nickname("nickname").memberRole(MemberRole.USER).build();
        List<FileDto> list = new ArrayList<>();
        FileDto file = new FileDto("upload", "uuid");
        list.add(file);
        WorkoutEditForm form = new WorkoutEditForm("수정된 이름", "내용", list);
        Workout workout = Workout.builder().member(member).build();

        when(workoutService.updateWorkout(member.getId(), 1L, form)).thenReturn(workout);
        //when
        mockMvc.perform(patch("/api/v1/workout/{workoutId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(form))
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isNoContent())
                .andDo(document("workout-update",
                        pathParameters(
                                parameterWithName("workoutId").description("운동 정보 Id")
                        ),
                        requestFields(
                                fieldWithPath("workoutName").type(STRING).description("수정할 이름"),
                                fieldWithPath("content").type(STRING).description("수정할 설명"),
                                fieldWithPath("files").type(ARRAY).description("파일 정보"),
                                fieldWithPath("files[].originFileName").type(STRING).description("원본 파일 이름"),
                                fieldWithPath("files[].uploadFileName").type(STRING).description("저장된 파일 이름")
                        )
                ));
    }
}