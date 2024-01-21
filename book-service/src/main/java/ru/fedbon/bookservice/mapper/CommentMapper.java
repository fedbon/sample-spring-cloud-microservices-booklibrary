package ru.fedbon.bookservice.mapper;


import org.springframework.stereotype.Component;
import ru.fedbon.bookservice.dto.CommentDto;
import ru.fedbon.bookservice.model.Comment;


@Component
public class CommentMapper {

    private CommentMapper() {

    }

    public static CommentDto mapCommentToDto(Comment comment) {

        var commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setBookId(comment.getBook().getId());

        return commentDto;
    }
}
