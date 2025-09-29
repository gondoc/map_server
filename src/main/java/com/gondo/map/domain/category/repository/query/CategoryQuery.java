package com.gondo.map.domain.category.repository.query;

import com.gondo.map.domain.category.entity.Category;
import com.gondo.map.domain.common.CommHomeStat;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.gondo.map.domain.category.entity.QCategory.category;
import static com.gondo.map.domain.hist.entity.QHistory.history;

@Slf4j
@Component
public class CategoryQuery {

    private final JPAQueryFactory query;

    public CategoryQuery(JPAQueryFactory jpaQueryFactory) {
        this.query = jpaQueryFactory;
    }

    public Category findItem(String id) {
        return query.selectFrom(category)
                .where(category.categoryId.eq(id))
                .fetchFirst();
    }

    public CommHomeStat findCommHomeStat() {
        Long activeCntLong = query
                .select(history.categoryId.countDistinct())
                .from(history)
                .where(history.histUseYn.isTrue())
                .fetchOne();

        int activeCnt = Optional.ofNullable(activeCntLong).orElse(0L).intValue();

        Long inActiveCntLong = query
                .select(category.categoryId.count())
                .from(category)
                .where(category.categoryUseYn.isTrue()
                        .and(category.categoryId.notIn(
                                JPAExpressions
                                        .select(history.categoryId)
                                        .from(history)
                                        .where(history.histUseYn.isTrue())
                        )))
                .fetchOne();

        int inActiveCnt = Optional.ofNullable(inActiveCntLong).orElse(0L).intValue();

        return new CommHomeStat(activeCnt, inActiveCnt);
    }
}
