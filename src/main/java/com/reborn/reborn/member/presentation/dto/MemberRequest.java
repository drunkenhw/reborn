package com.reborn.reborn.member.presentation.dto;

import com.reborn.reborn.member.domain.Address;
import com.reborn.reborn.member.domain.Member;
import com.reborn.reborn.member.domain.MemberRole;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 1,max = 20)
    private String password;
    @NotBlank
    @Length(min = 1)
    private String nickname;
    private String phone;
    private String zipcode;
    private String roadName;
    private String detailAddress;


    public static Member toEntity(MemberRequest memberRequest){
        return Member.builder()
                .nickname((memberRequest.getNickname()))
                .phone(memberRequest.getPhone())
                .email(memberRequest.getEmail())
                .address(new Address(memberRequest.getRoadName(), memberRequest.getDetailAddress(), memberRequest.getZipcode()))
                .memberRole(MemberRole.USER)
                .build();
    }
}
