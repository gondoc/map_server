package com.gondo.map.domain.hist.repository.query;

import com.gondo.map.domain.hist.record.HistRecord;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.gondo.map.domain.category.entity.QCategory.category;
import static com.gondo.map.domain.hist.entity.QHistory.history;
import static com.gondo.map.domain.site.entity.QSite.site;

@Slf4j
@Component
public class HistQuery {
    private final JPAQueryFactory query;

    public HistQuery(JPAQueryFactory jpaQueryFactory) {
        this.query = jpaQueryFactory;
    }

    public List<HistRecord> findHistItems() {
        return query.select(Projections.constructor(
                        HistRecord.class,
                        history.histId.as("id"),
                        history.histNm.as("histNm"),
                        history.startDtm.as("startDtm"),
                        history.endDtm.as("endDtm"),
                        history.histStaffCnt.as("staffCnt"),
                        history.histIsLock.as("isLock"),
//                        history.histCreateDtm.as("createDtm"),
                        history.histUpdateDtm.coalesce(history.histCreateDtm).as("lastUpdateDtm"),
                        category.categoryNm.as("categoryNm"),
                        category.categoryContent.as("categoryContent"),
                        site.siteId.as("siteId"),
                        site.siteNm.as("siteNm"),
                        site.siteLat.as("lat"),
                        site.siteLng.as("lng"),
                        site.siteLogoImgPath.as("logoImgPath")
                ))
                .from(history)
                .leftJoin(category)
                .on(history.categoryId.eq(category.categoryId))
                .leftJoin(site)
                .on(history.siteId.eq(site.siteId))
                .where(history.histUseYn.eq(true))
//                .orderBy(history.endDtm.desc())
                .orderBy(history.histUpdateDtm.coalesce(history.histCreateDtm).desc())
                .fetch();
    }

//    private BooleanExpression betweenEventTime(String startDtm, String endDtm) {
//        if (Strings.isNullOrEmpty(startDtm) || Strings.isNullOrEmpty(endDtm)) {
//            return null;
//        }
//        return history.startDtm.between(startDtm, endDtm);
//    }

}
