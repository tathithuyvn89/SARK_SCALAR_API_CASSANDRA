package com.veho.repository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.exception.transaction.CommitException;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.UnknownTransactionStatusException;

import com.scalar.db.io.Key;
import com.scalar.db.io.TextValue;
import com.scalar.db.io.Value;
import com.scalar.db.service.TransactionModule;
import com.scalar.db.service.TransactionService;

import java.lang.reflect.Type;
import java.util.*;

import com.veho.exception.MeetingException;
import com.veho.model.Meeting;
import com.veho.model.Participant;
import org.apache.commons.lang3.RandomStringUtils;

public class MeetingRepository {
    private final TransactionService transactionService;
    protected static final String NAMESPACE = "meeting";
    protected static final String TABLENAME = "meetings";
    protected static final String ID = "id";
    protected Properties props;

    public MeetingRepository() {
        props = new Properties();
        props.setProperty("scalar.db.contact_points", "localhost");
        props.setProperty("scalar.db.username", "cassandra");
        props.setProperty("scalar.db.password", "cassandra");
        Injector injector = Guice.createInjector(new TransactionModule(new DatabaseConfig(props)));
        transactionService = injector.getInstance(TransactionService.class);
        transactionService.with(NAMESPACE, TABLENAME);
    }

    public Meeting insertMeetingDB(Meeting meeting) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException {
        DistributedTransaction tx = transactionService.start();
        String id = UUID.randomUUID().toString();
        String passcode = RandomStringUtils.randomNumeric(8);
        List<Value> values = new ArrayList<>();
        values.add(new TextValue("passcode",passcode));
        values.add(new TextValue("name", meeting.getName()));
        values.add(new TextValue("date",meeting.getDate()));
        values.add(new TextValue("description",meeting.getDescription()));
        values.add(new TextValue("timefrom",meeting.getTimefrom()));
        values.add(new TextValue("timeto",meeting.getTimeto()));
        values.add(new TextValue("url",meeting.getUrl()));
        values.add(new TextValue("participants","[]"));
        Put put  = new Put(new Key(new TextValue(ID, id))).withValues(values);
        tx.put(put);
        tx.commit();
        return findMeetingByIdDB(id);
    }
    public Meeting findMeetingByIdDB(String meeting_id) throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {

        DistributedTransaction tx = transactionService.start();
        Get get  = new Get(new Key(new TextValue(ID, meeting_id)));
        Optional<Result> result = tx.get(get);
        //
        if (!result.isPresent()) {
            throw new MeetingException("ID invalid");
        }
        Map<String, Value> values = result.get().getValues();
        Meeting meeting = new Meeting();
        meeting.setId(meeting_id);
        meeting.setPasscode(((TextValue)values.get("passcode")).getString().get());
        meeting.setName(((TextValue)values.get("name")).getString().get());
        meeting.setDate(((TextValue)values.get("date")).getString().get());
        meeting.setDescription(((TextValue)values.get("description")).getString().get());
        meeting.setTimefrom(((TextValue)values.get("timefrom")).getString().get());
        meeting.setTimeto(((TextValue)values.get("timeto")).getString().get());
        meeting.setUrl(((TextValue)values.get("url")).getString().get());
        return meeting;
    }

    public Meeting editMeetingDB(String meeting_id, Meeting meeting) throws CrudException, CommitException,
            UnknownTransactionStatusException, MeetingException {
        DistributedTransaction tx = transactionService.start();
        Get get = new Get(new Key(new TextValue(ID, meeting_id)));

        Optional<Result> result = tx.get(get);
        if (!result.isPresent()) {
            throw new MeetingException("ID invalid");
        }
        List<Value> values = new ArrayList<>();
        values.add(new TextValue("name", meeting.getName()));
        values.add(new TextValue("date", meeting.getDate()));
        values.add(new TextValue("description", meeting.getDescription()));
        values.add(new TextValue("timefrom", meeting.getTimefrom()));
        values.add(new TextValue("timeto", meeting.getTimeto()));
        values.add(new TextValue("url", meeting.getUrl()));

        Put put = new Put(new Key(new TextValue(ID,meeting_id))).withValues(values);
        tx.put(put);
        tx.commit();
        return findMeetingByIdDB(meeting_id);
    }

    public Participant insertParticipantIntoMeetingDB(String meeting_id, Participant participant)  throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {

        DistributedTransaction tx = transactionService.start();

        Get get = new Get(new Key(new TextValue(ID,meeting_id )));

        Optional<Result> result = tx.get(get);

        String string_participants = ((TextValue)result.get().getValue("participants").get()).getString().get();
        Gson gson = new Gson();
        List<Participant> participants =  gson.fromJson(string_participants, new TypeToken<List<Participant>>() {}.getType());

        if (participants==null){
            participants = new ArrayList<>();
        }

        participants.add(participant);
        String json_participants = gson.toJson(participants);
        Put put = new Put(new Key(new TextValue(ID, meeting_id))).withValue(new TextValue("participants",json_participants));
        tx.put(put);
        tx.commit();
        return findParticipantByEmail(meeting_id,participant.getEmail());
    }

    public List<Participant> selectParticipantsByMeetingID(String meeting_id)  throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {

        DistributedTransaction tx = transactionService.start();

        Get get = new Get(new Key(new TextValue(ID,meeting_id )));

        Optional<Result> result = tx.get(get);

        String string_participants = ((TextValue)result.get().getValue("participants").get()).getString().get();

        Gson gson = new Gson();
        List<Participant> participants =  gson.fromJson(string_participants, new TypeToken<List<Participant>>() {}.getType());
        System.out.println("This is list" + participants);
        return participants;
    }

    public Participant findParticipantByEmail(String meeting_id, String email)  throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {

        List<Participant> participants = selectParticipantsByMeetingID(meeting_id);

        if (participants == null) {
            throw  new MeetingException("Participant not exist in this meeting_id");
        }
        for (Participant participant: participants) {
            if (participant.getEmail().equals(email)){
                return participant;
            }
        }
        return null;
    }


    public Participant updateParticipantByEmail(String meeting_id, String email, Participant participantRes) throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {

        List<Participant> participants = selectParticipantsByMeetingID(meeting_id);
        if (participants == null) {
            throw  new MeetingException("Participant not exist in this meeting_id");
        }

        Map<String, Participant> participantMap = new HashMap<>();
        for (Participant participant: participants) {
            if (!participant.getEmail().equals(email)) {
                participantMap.put(participant.getEmail(),participant);
            } else {
                participantMap.put(participantRes.getEmail(), participantRes);
            }
        }
        createListParticipantsForMeeting(meeting_id, new ArrayList<>(participantMap.values()));
        return findParticipantByEmail(meeting_id,participantRes.getEmail());
    }

    public List<Participant> deleteParticipantByEmail(String meeting_id, String email) throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {
        List<Participant> participants = selectParticipantsByMeetingID(meeting_id);
        if (participants == null) {
            throw  new MeetingException("Participant not exist in this meeting_id");
        }

        Map<String, Participant> participantMap = new HashMap<>();
        for (Participant participant: participants) {
            if (!participant.getEmail().equals(email)) {
                participantMap.put(participant.getEmail(),participant);
            }
        }
        return createListParticipantsForMeeting(meeting_id, new ArrayList<>(participantMap.values()));
    }

    public List<Participant> createListParticipantsForMeeting(String meeting_id, List<Participant> participants) throws CrudException,
            CommitException, UnknownTransactionStatusException, MeetingException {
        DistributedTransaction tx = transactionService.start();

        Get get = new Get(new Key(new TextValue(ID,meeting_id )));

        Optional<Result> result = tx.get(get);

        Gson gson = new Gson();

        String json_participants = gson.toJson(participants);
        Put put = new Put(new Key(new TextValue(ID, meeting_id))).withValue(new TextValue("participants",json_participants));
        tx.put(put);
        tx.commit();
        return selectParticipantsByMeetingID(meeting_id);
    }
}
