package com.example.demo.controller;

import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.response.ChatCreateResponse;
import com.example.demo.dto.response.ChatGetMessagesResponse;
import com.example.demo.dto.response.ChatJoinResponse;
import com.example.demo.dto.response.ChatSendMessageResponse;
import com.example.demo.dto.CreateChatDto;
import com.example.demo.dto.Cursor;
import com.example.demo.dto.MessageDto;
import com.example.demo.dto.UserNameDto;
import com.example.demo.service.ChatService;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/chats")
@Validated
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * POST /v1/chats создать чат с именем chat_name
     *
     * @param createChatDto (required)
     * @return action was completed successfully (status code 201) or * &#x60;bad-parameters&#x60; -
     * неправильный формат входных параметров  (status code 400) or unexpected server error (status
     * code 200)
     */
    @PostMapping
    public ResponseEntity<ChatCreateResponse> createChatWithName(
            @RequestBody @Valid CreateChatDto createChatDto) {
        return new ResponseEntity<>(chatService.createChatWithName(createChatDto), HttpStatus.CREATED);
    }

    /**
     * POST /v1/chats/{chat_id}/users
     * добавить пользователя user_name в чат chat_id
     *
     * @param chatId id чата, полученное при создании чата (required)
     * @param userName  (required)
     * @return action was completed successfully (status code 201)
     *         or * &#x60;bad-parameters&#x60; - неправильный формат входных параметров  (status code 400)
     *         or * &#x60;chat-not-found&#x60; - указанный чат не существует  (status code 404)
     *         or unexpected server error (status code 200)
     */
    @PostMapping ("/{chat_id}/users")
    public ResponseEntity<ChatJoinResponse> joinUserToChat(
            @PathVariable("chat_id") String chatId,
            @RequestBody @Valid UserNameDto userName) {
        return new ResponseEntity<>(chatService.joinUserToChat(chatId, userName), HttpStatus.CREATED);
    }

    /**
     * GET /v1/chats/{chat_id}/messages
     * получить список сообщений из чата chat_id
     *
     * @param chatId  (required)
     * @param limit не больше стольки сообщений хотим получить в ответе (required)
     * @param from указатель для сервера, обозначающий место, с которого стоит продолжить получение сообщений; если не указан, то сервер должен вернуть limit сообщений, начиная с самого первого сообщения в чате (optional)
     * @return action was completed successfully (status code 200)
     *         or * &#x60;bad-parameters&#x60; - неправильный формат входных параметров  (status code 400)
     *         or * &#x60;chat-not-found&#x60; - указанный чат не существует  (status code 404)
     *         or unexpected server error (status code 200)
     */
    @GetMapping ("/{chat_id}/messages")
    public ResponseEntity<ChatGetMessagesResponse> getMessagesByChatId(
            @PathVariable("chat_id") String chatId,
            @RequestParam(value = "limit") @NotNull @Min(1) @Max(1000) Integer limit,
            @RequestParam Optional<String> from) {
        log.info("************* id: {}, limit: {}, from: {}", chatId, limit, from.orElse(null));
        return new ResponseEntity<>(chatService.getMessagesByChatId(chatId, limit, from.orElse(null)), HttpStatus.OK);
    }

    /**
     * POST /v1/chats/{chat_id}/messages
     * отправить в чат chat_id сообщение message
     *
     * @param chatId  (required)
     * @param userId  (required)
     * @param messageDto  (required)
     * @return action was completed successfully (status code 201)
     *         or * &#x60;bad-parameters&#x60; - неправильный формат входных параметров  (status code 400)
     *         or * &#x60;chat-not-found&#x60; - указанный чат не существует * &#x60;user-not-found&#x60; - в указанном чате нет указанного пользователя  (status code 404)
     *         or unexpected server error (status code 200)
     */
    @PostMapping ("/{chat_id}/messages")
    public ResponseEntity<ChatSendMessageResponse> sendMessageToChat(
            @PathVariable("chat_id") String chatId,
            @RequestParam(value = "user_id") @NotNull @Valid String userId,
            @RequestBody @Valid MessageDto messageDto) {
        return new ResponseEntity<>(chatService.sendMessageToChat(chatId, userId, messageDto), HttpStatus.CREATED);
    }
}
