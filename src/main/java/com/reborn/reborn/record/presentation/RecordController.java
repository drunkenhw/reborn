package com.reborn.reborn.record.presentation;

import com.reborn.reborn.record.application.RecordService;
import com.reborn.reborn.record.presentation.dto.RecordTodayResponse;
import com.reborn.reborn.security.domain.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.reborn.reborn.record.presentation.dto.RecordRequest.*;

@RestController
@RequestMapping("/api/v1/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<Void> createRecord(@RequestBody @Valid RecordRequestList list) {
        recordService.create(list);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/today")
    public ResponseEntity<RecordTodayResponse> getTodayRecord(@LoginMember Long memberId) {
        RecordTodayResponse response = recordService.getTodayRecord(memberId);
        return ResponseEntity.ok(response);
    }
}
