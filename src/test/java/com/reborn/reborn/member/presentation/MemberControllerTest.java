package com.reborn.reborn.member.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.reborn.reborn.config.ControllerConfig;
import com.reborn.reborn.config.WithUserDetailsCustom;
import com.reborn.reborn.member.domain.Member;
import com.reborn.reborn.member.domain.MemberRole;
import com.reborn.reborn.member.application.MemberService;
import com.reborn.reborn.member.presentation.dto.ChangePasswordRequest;
import com.reborn.reborn.member.presentation.dto.MemberRequest;
import com.reborn.reborn.member.presentation.dto.MemberResponse;
import com.reborn.reborn.member.presentation.dto.MemberEditForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class MemberControllerTest extends ControllerConfig {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;


    @Test
    @DisplayName("회원 가입 : POST /api/v1/members")
    void joinTest() throws Exception {
        MemberRequest memberRequest = new MemberRequest("email@email.com", "password", "nickname", "phone", "postcode", "address", "detailAddress");

        when(memberService.registerMember(memberRequest)).thenReturn(1L);

        mockMvc.perform(post("/api/v1/members")
                        .content(objectMapper.writeValueAsBytes(memberRequest))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("join",
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일 주소"),
                                fieldWithPath("password").type(STRING).description("비밀번호"),
                                fieldWithPath("nickname").type(STRING).description("이름"),
                                fieldWithPath("phone").type(STRING).description("전화번호"),
                                fieldWithPath("zipcode").type(STRING).description("우편번호"),
                                fieldWithPath("roadName").type(STRING).description("주소"),
                                fieldWithPath("detailAddress").type(STRING).description("상세 주소")
                        )));
    }

    @Test
    @DisplayName("이메일 중복 확인 : GET /api/v1/email-check")
    void emailCheck() throws Exception {

        String email = "email";

        when(memberService.emailDuplicateCheck(email)).thenReturn(false);

        mockMvc.perform(get("/api/v1/email-check")
                        .queryParam("email", email))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("email-check",
                        requestParameters(
                                parameterWithName("email").description("중복 체크할 이메일")

                        )));
    }

    @Test
    @DisplayName("닉네임 중복 확인 : GET /api/v1/nickname-check")
    void nicknameCheck() throws Exception {

        String nickname = "nickname";

        when(memberService.emailDuplicateCheck(nickname)).thenReturn(false);

        mockMvc.perform(get("/api/v1/nickname-check")
                        .queryParam("nickname", nickname))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("nickname-check",
                        requestParameters(
                                parameterWithName("nickname").description("중복 체크할 닉네임")
                        )));
    }
    @Test
    @WithUserDetailsCustom
    @DisplayName("비밀번호 변경 : PATCH /api/v1/change-password")
    void changePassword() throws Exception {
        Member member = Member.builder().email("user").password("a").memberRole(MemberRole.USER).build();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("a", "b");

        willDoNothing().given(memberService).changePassword(member.getId(), changePasswordRequest);

        mockMvc.perform(patch("/api/v1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(changePasswordRequest))
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isNoContent())
                .andDo(document("change-password",
                        requestFields(
                                fieldWithPath("rawPassword").type(STRING).description("현재 비밀번호"),
                                fieldWithPath("changePassword").type(STRING).description("변경할 비밀번호")
                        )
                ));
    }
    @Test
    @WithUserDetailsCustom
    @DisplayName("회원정보 수정 : PATCH /api/v1/members/me")
    void modifyMember() throws Exception {
        Member member = Member.builder().email("user").password("a").memberRole(MemberRole.USER).build();
        MemberEditForm request = new MemberEditForm("nickname", "010-1234-1234", "zipcode", "roadName", "detail");
        willDoNothing().given(memberService).updateMember(member.getId(),request);

        mockMvc.perform(patch("/api/v1/members/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isNoContent())
                .andDo(document("modify-member",
                        requestFields(
                                fieldWithPath("nickname").type(STRING).description("닉네임"),
                                fieldWithPath("phone").type(STRING).description("전화번호"),
                                fieldWithPath("zipcode").type(STRING).description("우편번호"),
                                fieldWithPath("roadName").type(STRING).description("도로명 주소"),
                                fieldWithPath("detailAddress").type(STRING).description("상세 주소")
                        )
                ));
    }


    @Test
    @WithUserDetailsCustom
    @DisplayName("회원 정보 조회 : Get /api/v1/members/me")
    void getOne() throws Exception {
        //given
        MemberResponse response = new MemberResponse("email@naver.com", "nickname", "010-0000-0000", "zip", "road", "detail");
        given(memberService.getOne(any())).willReturn(response);

        //when
        mockMvc.perform(get("/api/v1/members/me"))
                .andExpect(status().isOk())
                .andDo(document("members-getOne"));
    }

    @Test
    @WithUserDetailsCustom
    @DisplayName("회원탈퇴 : DELETE /api/v1/members/me")
    void deleteMember() throws Exception {
        Member member = Member.builder().email("user").password("a").memberRole(MemberRole.USER).build();
        willDoNothing().given(memberService).deleteMember(member.getId());

        mockMvc.perform(delete("/api/v1/members/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isNoContent())
                .andDo(document("delete-member"));
    }
}