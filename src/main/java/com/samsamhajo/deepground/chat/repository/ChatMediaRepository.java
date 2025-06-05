package com.samsamhajo.deepground.chat.repository;

import com.samsamhajo.deepground.chat.entity.ChatMedia;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMediaRepository extends MongoRepository<ChatMedia, String> {

    @Query("{ '_id': { $in: ?0 }, 'file_status': 'PENDING' }")
    List<ChatMedia> findAllByIdAndStatusPending(List<String> ids);
}
