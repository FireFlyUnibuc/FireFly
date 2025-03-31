package ro.unibuc.hello.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ro.unibuc.hello.entity.MoneyRequest;

import java.util.List;

@Repository
public interface MoneyRequestRepository extends MongoRepository<MoneyRequest, String> {
    @Query("{ 'toAccountId' : ?0 }")
    List<MoneyRequest> findByToAccountId(String toAccountId); 
    List<MoneyRequest> findByFromAccountId(String fromAccountId); 
}
