package com.reborn.reborn.workout.presentation.dto;

import com.reborn.reborn.common.presentation.dto.FileDto;
import com.reborn.reborn.workout.domain.WorkoutCategory;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {

    @NotBlank
    private String workoutName;
    @NotBlank
    private String content;
    @NotNull
    private WorkoutCategory workoutCategory;
    @Builder.Default
    private List<FileDto> files = new ArrayList<>();

}
