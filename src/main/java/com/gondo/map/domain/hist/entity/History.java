package com.gondo.map.domain.hist.entity;

import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.hist.record.HistRecord;
import com.gondo.map.domain.hist.record.HistUpdateRecord;
import com.gondo.map.domain.site.record.SiteRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Table(name = "tbl_project_hist", schema = "project")
@Entity
public class History {

    @Id
    @Column(name = "hist_id")
    private String histId;

    @Column(name = "hist_nm")
    private String histNm;

    @Column(name = "hist_staff_cnt")
    private Integer histStaffCnt;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "site_id")
    private String siteId;

    @Column(name = "hist_start_dtm")
    private String startDtm;

    @Column(name = "hist_end_dtm")
    private String endDtm;

    @Column(name = "hist_create_dtm")
    private LocalDateTime histCreateDtm;

    @Column(name = "hist_update_dtm")
    private LocalDateTime histUpdateDtm;

    @Column(name = "hist_is_lock")
    private Boolean histIsLock;

    @Column(name = "hist_use_yn")
    private Boolean histUseYn;

    public HistRecord toRecord(CategoryRecord categoryRecord, SiteRecord siteRecord) {
        return HistRecord.of(
                this.histId,
                this.histNm,
                this.startDtm,
                this.endDtm,
                this.histStaffCnt,
                this.histIsLock,
//                this.histCreateDtm,
                this.histUpdateDtm,
                categoryRecord.nm(),
                categoryRecord.content(),
                siteRecord.id(),
                siteRecord.nm(),
                siteRecord.lat(),
                siteRecord.lng(),
                siteRecord.logoImgPath()
        );
    }

    public History updateHistory(HistUpdateRecord histUpdateRecord) {
        this.histNm = histUpdateRecord.histNm();
        this.categoryId = histUpdateRecord.categoryId();
        this.siteId = histUpdateRecord.siteId();
        this.startDtm = histUpdateRecord.startDtm();
        this.endDtm = histUpdateRecord.endDtm();
        this.histUpdateDtm = LocalDateTime.now();
        this.histStaffCnt = histUpdateRecord.staffCnt();
        return this;
    }
}
