package com.myapp.repository;

import com.myapp.domain.D;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the D entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DRepository extends R2dbcRepository<D, Long>, DRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<D> findAll();

    @Override
    Mono<D> findById(Long id);

    @Override
    <S extends D> Mono<S> save(S entity);
}

interface DRepositoryInternal {
    <S extends D> Mono<S> insert(S entity);
    <S extends D> Mono<S> save(S entity);
    Mono<Integer> update(D entity);

    Flux<D> findAll();
    Mono<D> findById(Long id);
    Flux<D> findAllBy(Pageable pageable);
    Flux<D> findAllBy(Pageable pageable, Criteria criteria);
}
