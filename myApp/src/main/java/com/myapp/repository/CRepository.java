package com.myapp.repository;

import com.myapp.domain.C;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the C entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CRepository extends R2dbcRepository<C, Long>, CRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<C> findAll();

    @Override
    Mono<C> findById(Long id);

    @Override
    <S extends C> Mono<S> save(S entity);
}

interface CRepositoryInternal {
    <S extends C> Mono<S> insert(S entity);
    <S extends C> Mono<S> save(S entity);
    Mono<Integer> update(C entity);

    Flux<C> findAll();
    Mono<C> findById(Long id);
    Flux<C> findAllBy(Pageable pageable);
    Flux<C> findAllBy(Pageable pageable, Criteria criteria);
}
