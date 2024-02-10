package ru.fedbon.voteservice.producer;


import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class VoteBookMessage {

    private String userId;

    private String bookId;

    private boolean isPositive;
}
