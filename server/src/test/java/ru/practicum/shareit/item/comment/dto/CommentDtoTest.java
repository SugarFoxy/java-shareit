package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(
                1L,
                "Жевала я в крот такие задания",
                "Авилова Оксана",
                LocalDateTime.now()
        );

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Жевала я в крот такие задания");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Авилова Оксана");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
    }
}