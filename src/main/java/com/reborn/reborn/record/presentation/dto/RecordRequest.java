package com.reborn.reborn.record.presentation.dto;

import com.reborn.reborn.workout.domain.WorkoutCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
public class RecordRequest {

    @NotBlank
    private Long myWorkoutId;
    @NotBlank
    private Long total;
    @NotNull
    private WorkoutCategory workoutCategory;

    public RecordRequest(Long myWorkoutId, Long total, WorkoutCategory workoutCategory) {
        this.myWorkoutId = myWorkoutId;
        this.total = total;
        this.workoutCategory = workoutCategory;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordRequestList {

        private List<RecordRequest> recordList;
    }

}
