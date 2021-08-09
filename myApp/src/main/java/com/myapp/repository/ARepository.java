package com.myapp.repository;

import com.myapp.domain.A;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the A entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ARepository extends R2dbcRepository<A, Long>, ARepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<A> findAll();

    @Override
    Mono<A> findById(Long id);

    @Override
    <S extends A> Mono<S> save(S entity);
}

interface ARepositoryInternal {
    <S extends A> Mono<S> insert(S entity);
    <S extends A> Mono<S> save(S entity);
    Mono<Integer> update(A entity);

    Flux<A> findAll();
    Mono<A> findById(Long id);
    Flux<A> findAllBy(Pageable pageable);
    Flux<A> findAllBy(Pageable pageable, Criteria criteria);
}
