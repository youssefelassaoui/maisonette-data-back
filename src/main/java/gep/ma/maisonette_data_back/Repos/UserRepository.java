package gep.ma.maisonette_data_back.Repos;

import gep.ma.maisonette_data_back.Models.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

    @Query("SELECT * FROM users WHERE email = ?0 LIMIT 1 ALLOW FILTERING")
    Optional<User> findByEmail(String email);

    Optional<User> findByToken(String token);
}
