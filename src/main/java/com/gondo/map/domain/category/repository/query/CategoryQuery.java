package com.gondo.map.domain.category.repository.query;

import com.gondo.map.domain.category.entity.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.gondo.map.domain.category.entity.QCategory.category;

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

}
