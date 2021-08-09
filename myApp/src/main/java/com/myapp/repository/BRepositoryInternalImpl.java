package com.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.myapp.domain.B;
import com.myapp.repository.rowmapper.ARowMapper;
import com.myapp.repository.rowmapper.BRowMapper;
import com.myapp.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the B entity.
 */
@SuppressWarnings("unused")
class BRepositoryInternalImpl implements BRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ARowMapper aMapper;
    private final BRowMapper bMapper;

    private static final Table entityTable = Table.aliased("b", EntityManager.ENTITY_ALIAS);
    private static final Table aTable = Table.aliased("a", "a");

    public BRepositoryInternalImpl(R2dbcEntityTemplate template, EntityManager entityManager, ARowMapper aMapper, BRowMapper bMapper) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.aMapper = aMapper;
        this.bMapper = bMapper;
    }

    @Override
    public Flux<B> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<B> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<B> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = BSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ASqlHelper.getColumns(aTable, "a"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(aTable)
            .on(Column.create("aa_id", entityTable))
            .equals(Column.create("id", aTable));

        String select = entityManager.createSelect(selectFrom, B.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<B> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<B> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private B process(Row row, RowMetadata metadata) {
        B entity = bMapper.apply(row, "e");
        entity.setA(aMapper.apply(row, "a"));
        return entity;
    }

    @Override
    public <S extends B> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends B> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update B with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(B entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class BSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));

        columns.add(Column.aliased("aa_id", table, columnPrefix + "_aa_id"));
        return columns;
    }
}
