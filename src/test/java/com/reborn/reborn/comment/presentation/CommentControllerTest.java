package com.reborn.reborn.comment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reborn.reborn.comment.application.CommentService;
import com.reborn.reborn.comment.presentation.dto.CommentRequestDto;
import com.reborn.reborn.config.ControllerConfig;
import com.reborn.reborn.config.WithUserDetailsCustom;
import com.reborn.reborn.member.domain.Member;
import com.reborn.reborn.member.domain.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CommentControllerTest extends ControllerConfig {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentService commentService;


    @Test
    @WithUserDetailsCustom
    @DisplayName("댓글 등록 : POST /api/v1/articles/{articleId}/comments")
    void createComment() throws Exception {
        Member member = Member.builder().email("user").memberRole(MemberRole.USER).build();
        CommentRequestDto request = new CommentRequestDto("댓글 내용");
        when(commentService.create(1L, 1L, request)).thenReturn(1L);

        mockMvc.perform(post("/api/v1/articles/{articleId}/comments",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header("Authorization", "Bearer " + getToken(member)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("comment-create",
                        pathParameters(
                                parameterWithName("articleId").description("게시글 Id")
                        ),
                        requestFields(
                                fieldWithPath("content").type(STRING).description("댓글 내용")
                        )));
    }
}