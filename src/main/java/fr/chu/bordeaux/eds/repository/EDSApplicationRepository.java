package fr.chu.bordeaux.eds.repository;

import fr.chu.bordeaux.eds.domain.EDSApplication;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the EDSApplication entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EDSApplicationRepository extends ReactiveMongoRepository<EDSApplication, String> {}
