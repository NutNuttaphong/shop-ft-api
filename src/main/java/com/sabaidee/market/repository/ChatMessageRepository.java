package com.sabaidee.market.repository;

import com.sabaidee.market.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query("{$or: [ {sender: ?0, receiver: ?1}, {sender: ?1, receiver: ?0} ]}")
    List<ChatMessage> findChatHistory(String user1, String user2);

    List<ChatMessage> findBySenderOrReceiver(String sender, String receiver);
}
