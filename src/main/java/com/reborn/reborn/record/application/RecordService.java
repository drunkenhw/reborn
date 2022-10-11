package com.reborn.reborn.record.application;

import com.reborn.reborn.myworkout.domain.MyWorkout;
import com.reborn.reborn.record.domain.Record;
import com.reborn.reborn.myworkout.exception.MyWorkoutNotFoundException;
import com.reborn.reborn.myworkout.domain.repository.MyWorkoutRepository;
import com.reborn.reborn.record.domain.repository.RecordRepository;
import com.reborn.reborn.record.presentation.dto.RecordTodayResponse;
import com.reborn.reborn.record.presentation.dto.RecordWeekResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.reborn.reborn.record.presentation.dto.RecordRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private final MyWorkoutRepository myWorkoutRepository;
    private final RecordRepository recordRepository;

    @Transactional
    public void create(RecordRequestList list) {
        List<Record> recordList = list.getRecordList().stream()
                .map(recordRequest -> {
                    MyWorkout myWorkout = myWorkoutRepository.findById(recordRequest.getMyWorkoutId())
                            .orElseThrow(() -> new MyWorkoutNotFoundException(recordRequest.getMyWorkoutId().toString()));
                    return new Record(myWorkout, recordRequest.getTotal(), recordRequest.getWorkoutCategory());
                })
                .collect(Collectors.toList());

        updateOrSaveRecord(recordList);

    }

    @Cacheable(value = "RecordWeekResponse", key = "{#memberId, #localDate}", condition = "#localDate != #localDate.now()")
    public RecordTodayResponse getTodayRecord(Long memberId, LocalDate localDate) {
        List<Record> records = recordRepository.findTodayRecordByMemberId(memberId, dateIfNullReturnNow(localDate));
        RecordTodayResponse response = new RecordTodayResponse();
        records.forEach(record -> addTotal(record, response));
        return response;
    }

    @Cacheable(value = "RecordWeekResponse", key = "{#memberId, #localDate}", condition = "#localDate != #localDate.now()")
    public RecordWeekResponse getWeekRecord(Long memberId, LocalDate localDate) {
        LocalDate date = dateIfNullReturnNow(localDate);
        return recordRepository.findWeekMyRecord(memberId, date).orElse(new RecordWeekResponse());
    }

    private void addTotal(Record record, RecordTodayResponse response) {
        switch (record.getWorkoutCategory()) {
            case BACK:
                response.setBack(response.getBack() + record.getTotal());
                break;
            case CHEST:
                response.setChest(response.getChest() + record.getTotal());
                break;
            case LOWER_BODY:
                response.setLowerBody(response.getLowerBody() + record.getTotal());
                break;
            case CORE:
                response.setCore(response.getCore() + record.getTotal());
                break;
        }
    }

    private LocalDate dateIfNullReturnNow(LocalDate date) {
        return date == null ? LocalDate.now() : date;
    }

    private void updateOrSaveRecord(List<Record> recordList) {
        recordList.forEach(record -> {
            Optional<Record> findToday = recordRepository.findTodayRecordByMyWorkoutId(record.getMyWorkout().getId());
            if (findToday.isPresent()) {
                findToday.get().addWeight(record.getTotal());
                return;
            }
            recordRepository.save(record);
        });
    }

}
