package com.gondo.map.domain.site.repository.query;

import com.gondo.map.domain.site.entity.Site;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.gondo.map.domain.site.entity.QSite.site;

@Slf4j
@Component
public class SiteQuery {

    private final JPAQueryFactory query;

    public SiteQuery(JPAQueryFactory jpaQueryFactory) {
        this.query = jpaQueryFactory;
    }


    public Site findItem(String id) {
        return query.selectFrom(site)
                .where(site.siteId.eq(id))
                .fetchFirst();
    }
}
