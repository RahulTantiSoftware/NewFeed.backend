package com.NewFeed.backend.modal.feed;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class NewFeedReply extends Replyable implements Votable {
    @ManyToOne
    private Replyable parent;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<NewFeedReply> replies;
}
