package com.reborn.reborn.controller;

import com.reborn.reborn.dto.ChangePasswordDto;
import com.reborn.reborn.dto.EmailCheckResponse;
import com.reborn.reborn.dto.JoinResponseDto;
import com.reborn.reborn.dto.MemberRequestDto;
import com.reborn.reborn.entity.Member;
import com.reborn.reborn.security.LoginMember;
import com.reborn.reborn.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<JoinResponseDto> join(@RequestBody MemberRequestDto memberRequestDto) {
        Long memberId = memberService.registerMember(memberRequestDto);
        log.info("aa");
        return ResponseEntity.created(URI.create("/api/v1/members/" + memberId)).body(new JoinResponseDto(memberId));
    }

    @GetMapping("/email-check")
    public ResponseEntity<EmailCheckResponse> emailCheck(@RequestParam String email) {
        boolean check = memberService.emailDuplicateCheck(email);

        return ResponseEntity.status(HttpStatus.OK).body(new EmailCheckResponse(check));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@LoginMember Long memberId, @RequestBody ChangePasswordDto changePasswordDto) {
        memberService.updatePassword(memberId, changePasswordDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


