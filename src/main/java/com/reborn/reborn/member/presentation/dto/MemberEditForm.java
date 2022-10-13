package com.reborn.reborn.member.presentation.dto;

import com.reborn.reborn.member.domain.Address;
import com.reborn.reborn.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEditForm {
    @NotBlank
    private String nickname;
    private String phone;
    private String zipcode;
    private String roadName;
    private String detailAddress;

    public static Member of(MemberEditForm request) {
        return Member.builder()
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .address(new Address(request.getRoadName(), request.getDetailAddress(), request.getZipcode()))
                .build();
    }
}
