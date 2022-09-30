package com.reborn.reborn.comment.application;

import com.reborn.reborn.comment.domain.Comment;
import com.reborn.reborn.comment.domain.repository.CommentRepository;
import com.reborn.reborn.comment.presentation.dto.CommentEditForm;
import com.reborn.reborn.comment.presentation.dto.CommentRequestDto;
import com.reborn.reborn.comment.presentation.dto.CommentResponseDto;
import com.reborn.reborn.member.domain.Member;
import com.reborn.reborn.member.domain.repository.MemberRepository;
import com.reborn.reborn.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public Comment create(Long memberId, CommentRequestDto commentRequestDto) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId.toString()));
        Comment comment = Comment.builder()
                .member(member)
                .content(commentRequestDto.getContent())
                .build();

        Comment saveComment = commentRepository.save(comment);
        return saveComment;
    }
    
    @Transactional(readOnly = true)
    public CommentResponseDto getCommentDetail(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        CommentResponseDto dto= CommentResponseDto.of(comment);
        return dto;
    }

    public Comment updateComment(Long authorId, Long commentId, CommentEditForm form) {
        Comment comment = getComment(commentId);
        validIsAuthor(authorId, comment);
        comment.modifyComment(form.getContent());

        return comment;
    }

    private void validIsAuthor(Long authorId, Comment comment) {
        if(comment.getMember().getId() != authorId){
            throw new RuntimeException("권한이 없음");
        }
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow();
    }

    public void deleteComment(Long authorId, Long commentId) {
        Comment comment = getComment(commentId);
        validIsAuthor(authorId, comment);
        commentRepository.delete(comment);
    }
}
