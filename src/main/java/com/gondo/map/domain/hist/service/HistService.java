package com.gondo.map.domain.hist.service;

import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.category.service.CategoryService;
import com.gondo.map.domain.common.CommException;
import com.gondo.map.domain.hist.dto.YearHistDto;
import com.gondo.map.domain.hist.entity.History;
import com.gondo.map.domain.hist.record.HistRecord;
import com.gondo.map.domain.hist.record.HistSaveRecord;
import com.gondo.map.domain.hist.record.HistUpdateRecord;
import com.gondo.map.domain.hist.repository.HistRepository;
import com.gondo.map.domain.hist.repository.query.HistQuery;
import com.gondo.map.domain.site.record.SiteRecord;
import com.gondo.map.domain.site.service.SiteService;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HistService {

    private final HistQuery histQuery;
    private final HistRepository histRepository;
    private final CategoryService categoryService;
    private final SiteService siteService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${career.start}")
    private String careerStartYear;

    public List<HistRecord> getItems() {
        return findHistAllItems();
    }

    public List<YearHistDto> getYearItems() {
        List<HistRecord> histAllItems = findHistAllItems();
        List<YearHistDto> careerStartYearToNowYear = findCareerStartYearToNowYear();
        careerStartYearToNowYear.forEach(yearItem -> {
            List<HistRecord> findFilterYearHistItems = histAllItems.stream()
                    .filter(histRc -> histRc.startDtm().contains(yearItem.getYearLabel()) || histRc.endDtm().contains(yearItem.getYearLabel()))
                    .collect(Collectors.toList());
            yearItem.addHistRecord(findFilterYearHistItems);
        });
        return careerStartYearToNowYear;
    }

    private List<HistRecord> findHistAllItems() {
        return histQuery.findHistItems();
    }

    public HistRecord addItem(HistSaveRecord saveRecord) {
        if (Strings.isNullOrEmpty(saveRecord.categoryId()) || Strings.isNullOrEmpty(saveRecord.siteId())) {
            return null;
        }

        CategoryRecord findCategoryRecord = findCategoryRecordById(saveRecord.categoryId());
        if (Objects.isNull(findCategoryRecord)) {
            return null;
        }

        SiteRecord findSiteRecord = findSiteRecordById(saveRecord.siteId());
        if (Objects.isNull(findSiteRecord)) {
            return null;
        }

        return histRepository.save(saveRecord.toHistory()).toRecord(findCategoryRecord, findSiteRecord);
    }

    public HistRecord updateItem(HistUpdateRecord histUpdateRecord) {
        Optional<History> optionalHistory = histRepository.findById(histUpdateRecord.histId());
        if (optionalHistory.isEmpty()) {
            return null;
        } else {

            History history = optionalHistory.get();
            if (history.getHistIsLock()) {
                throw new CommException(405, "관리자의 잠금 설정으로 인해 수정이 불가합니다.");
            }

            History save = histRepository.save(history.updateHistory(histUpdateRecord));

            CategoryRecord findCategoryRecord = findCategoryRecordById(history.getCategoryId());
            if (Objects.isNull(findCategoryRecord)) {
                return null;
            }

            SiteRecord findSiteRecord = findSiteRecordById(save.getSiteId());
            if (Objects.isNull(findSiteRecord)) {
                return null;
            }

            return save.toRecord(findCategoryRecord, findSiteRecord);
        }
    }

    public boolean deleteItem(String histId) {
        Optional<History> optionalHistory = histRepository.findById(histId);
        if (optionalHistory.isEmpty()) {
            return false;
        } else {
            History history = optionalHistory.get();
            if (history.getHistIsLock()) {
                throw new CommException(405, "관리자의 잠금 설정으로 인해 삭제가 불가합니다.");
            }

            return histRepository.deleteByHistId(histId) > 0;
        }
    }

    private CategoryRecord findCategoryRecordById(String id) {
        return categoryService.findItem(id);
    }

    private SiteRecord findSiteRecordById(String id) {
        return siteService.findItem(id);
    }

    private List<YearHistDto> findCareerStartYearToNowYear() {
        int startYearInt = LocalDate.parse(careerStartYear, formatter).getYear();
        int currentYearInt = LocalDate.now().getYear();

        List<YearHistDto> yearList = new ArrayList<>();
        for (int year = startYearInt; year <= currentYearInt; year++) {
            yearList.add(YearHistDto.of(String.valueOf(year), new ArrayList<>()));
        }

        return yearList;
    }

}
