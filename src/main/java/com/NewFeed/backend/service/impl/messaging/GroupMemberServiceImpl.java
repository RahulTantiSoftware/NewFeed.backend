package com.NewFeed.backend.service.impl.messaging;

import com.NewFeed.backend.dto.GroupMemberDto;
import com.NewFeed.backend.exception.MessageException;
import com.NewFeed.backend.modal.messaging.GroupConversation;
import com.NewFeed.backend.modal.messaging.GroupMember;
import com.NewFeed.backend.modal.user.UserProfile;
import com.NewFeed.backend.repository.messaging.ConversationRepository;
import com.NewFeed.backend.repository.messaging.GroupMemberRepository;
import com.NewFeed.backend.repository.messaging.UnreadGroupMessageRepository;
import com.NewFeed.backend.service.messaging.GroupMemberService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private UnreadGroupMessageRepository unreadGroupMessageRepository;

    @Autowired
    @Qualifier("messengerConfigModelMapper")
    private ModelMapper messengerModelMapper;
    @Transactional
    @Override
    public GroupMember groupMember(Long conversationId, UserProfile profile) {
        GroupConversation conversation = (GroupConversation) conversationRepository
                                            .findById(conversationId)
                                            .orElseThrow(() -> new MessageException("MessageException : conversation not fount with given id : " + conversationId));
        GroupMember groupMember = groupMemberRepository
                                    .findByConversationAndProfile(conversation, profile)
                                    .orElseThrow(() -> new MessageException("MessageException : groupMember not fount with given profile id : " + profile.getId()));
        unreadGroupMessageRepository.deleteByMember(groupMember);
        return groupMember;
    }

    @Transactional
    @Override
    public GroupMemberDto getGroupMember(Long conversationId, UserProfile profile) {
        return messengerModelMapper.map(groupMember(conversationId,profile), GroupMemberDto.class);
    }

}
