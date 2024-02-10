package ru.fedbon.voteservice.producer;


import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class VoteCommentMessage {

    private String userId;

    private String commentId;

    private boolean isPositive;
}
