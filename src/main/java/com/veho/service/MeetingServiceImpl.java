package com.veho.service;

import com.scalar.db.exception.transaction.CommitException;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.UnknownTransactionStatusException;
import com.veho.exception.MeetingException;
import com.veho.model.Meeting;
import com.veho.model.Participant;
import com.veho.repository.MeetingRepository;

import java.util.List;
import java.util.Map;

public class MeetingServiceImpl implements MeetingService{

    private final MeetingRepository meetingRepository;

    public MeetingServiceImpl() {
        meetingRepository = new MeetingRepository();
    }

    @Override
    public Meeting insertMeeting(Meeting meeting) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException {
        return meetingRepository.insertMeetingDB(meeting);
    }

    @Override
    public Meeting selectMeetingById(String meeting_id) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException {
        return meetingRepository.findMeetingByIdDB(meeting_id);
    }

    @Override
    public Meeting updateMeeting(String meeting_id, Meeting meeting) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException {
        return meetingRepository.editMeetingDB(meeting_id, meeting);
    }

    @Override
    public List<Participant> selectParticipants(String meeting_id) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException {
        return meetingRepository.selectParticipantsByMeetingID(meeting_id);
    }

    @Override
    public Participant insertParticipant(String meeting_id,Participant participant) throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {
        return meetingRepository.insertParticipantIntoMeetingDB(meeting_id,participant);
    }

    @Override
    public Participant selectParticipant(String meeting_id, String email) throws CrudException, CommitException, UnknownTransactionStatusException, MeetingException {
        return meetingRepository.findParticipantByEmail(meeting_id,email);
    }

    @Override
    public boolean deleteParticipant(String meeting_id, String email) throws CrudException, CommitException, UnknownTransactionStatusException, MeetingException {
        meetingRepository.deleteParticipantByEmail(meeting_id,email);
        Participant participant = meetingRepository.findParticipantByEmail(meeting_id,email);
        if (participant == null) {
            return true;
        }
        return false;
    }

    @Override
    public Participant updateParticipant(String meeting_id, String email, Participant participant) throws CrudException, CommitException, UnknownTransactionStatusException, MeetingException {
        return meetingRepository.updateParticipantByEmail(meeting_id,email,participant);
    }

    @Override
    public boolean isExitParticipantWithEmail(String meeting_id, String email) throws CrudException, CommitException, UnknownTransactionStatusException, MeetingException {
        Participant participant = meetingRepository.findParticipantByEmail(meeting_id,email);
        System.out.println(participant);
        if (participant == null) {
            return false;
        }
        return true;
    }
}
