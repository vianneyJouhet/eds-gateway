package com.myapp.repository;

import com.myapp.domain.B;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the B entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BRepository extends R2dbcRepository<B, Long>, BRepositoryInternal {
    @Query("SELECT * FROM b entity WHERE entity.aa_id = :id")
    Flux<B> findByA(Long id);

    @Query("SELECT * FROM b entity WHERE entity.aa_id IS NULL")
    Flux<B> findAllWhereAIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<B> findAll();

    @Override
    Mono<B> findById(Long id);

    @Override
    <S extends B> Mono<S> save(S entity);
}

interface BRepositoryInternal {
    <S extends B> Mono<S> insert(S entity);
    <S extends B> Mono<S> save(S entity);
    Mono<Integer> update(B entity);

    Flux<B> findAll();
    Mono<B> findById(Long id);
    Flux<B> findAllBy(Pageable pageable);
    Flux<B> findAllBy(Pageable pageable, Criteria criteria);
}
