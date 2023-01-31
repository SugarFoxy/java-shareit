package ru.practicum.shareit.item.comment.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {
    private final Item item = Item.builder().id(1L).build();
    private final User user = User.builder().id(1L).name("name").build();

    @Test
    public void toCommentDto() {
        Comment comment = new Comment(
                1L,
                "text",
                item,
                user,
                LocalDateTime.now()
        );

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    public void toComment() {
        CommentDto dto = new CommentDto(
                1L,
                "text",
                "author",
                LocalDateTime.now()
        );
        Comment comment = CommentMapper.toComment(dto, user, item);

        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getText(), comment.getText());
        assertEquals(user, comment.getAuthor());
        assertEquals(item, comment.getItem());
        assertEquals(dto.getCreated(), comment.getCreated());
    }
}