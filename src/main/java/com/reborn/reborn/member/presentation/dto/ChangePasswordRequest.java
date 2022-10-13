package com.reborn.reborn.member.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank
    @Length(min = 1, max = 20)
    private String rawPassword;
    @NotBlank
    @Length(min = 1,max = 20)
    private String changePassword;
}
