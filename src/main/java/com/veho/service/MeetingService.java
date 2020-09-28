package com.veho.service;

import com.scalar.db.exception.transaction.CommitException;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.UnknownTransactionStatusException;
import com.veho.exception.MeetingException;
import com.veho.model.Meeting;
import com.veho.model.Participant;

import java.util.List;
import java.util.Map;

public interface MeetingService {
    public Meeting insertMeeting(Meeting meeting) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException;
    public Meeting selectMeetingById(String meeting_id)
            throws CrudException, CommitException, UnknownTransactionStatusException,MeetingException;
    public Meeting updateMeeting(String meeting_id, Meeting meeting)
            throws CrudException, CommitException, UnknownTransactionStatusException, MeetingException;
    public List<Participant> selectParticipants(String meeting_id)
            throws CrudException, CommitException, UnknownTransactionStatusException,MeetingException;
    public Participant insertParticipant(String meeting_id, Participant participanta)
            throws CrudException, CommitException, UnknownTransactionStatusException, MeetingException;
    public Participant selectParticipant(String meeting_id, String email)
            throws CrudException, CommitException, UnknownTransactionStatusException, MeetingException;
    public boolean deleteParticipant(String meeting_id, String email) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException;
    public Participant updateParticipant(String meeting_id,String email, Participant participant )throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException;
    public boolean isExitParticipantWithEmail(String meeting_id,String email )throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException;
}
