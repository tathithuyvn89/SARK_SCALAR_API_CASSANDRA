package com.veho;

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

import java.util.Optional;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import com.google.gson.Gson;

public class DBTest {
    private final TransactionService service;
    protected static final String NAMESPACE = "meeting";//databace name
    protected static final String TABLENAME = "meetings";//table name
    protected static final String ID = "id";//priority key colmn name
    protected Properties props;
    public DBTest() {
        props = new Properties();
        props.setProperty("scalar.db.contact_points", "localhost");
        props.setProperty("scalar.db.username", "cassandra");
        props.setProperty("scalar.db.password", "cassandra");
        Injector injector = Guice.createInjector(new TransactionModule(new DatabaseConfig(props)));
        service = injector.getInstance(TransactionService.class);
        service.with(NAMESPACE, TABLENAME);
    }
    public void test() throws CrudException, CommitException, UnknownTransactionStatusException
    {
        //this.insertMeeting();
        //this.selectMeeting("389759ef-e767-4fd2-903e-7d1a509d3870");
        //this.updateMeeting("12ea7a5e-6304-43b1-a6b1-8bfcfeeb27ee");
        this.insertOrUpdateParticipant("a48a7a4d-e513-4d52-9ba7-173f23ee540f","b@b.b");
        this.selectParticipants("a48a7a4d-e513-4d52-9ba7-173f23ee540f");
        //this.selectParticipant("a48a7a4d-e513-4d52-9ba7-173f23ee540f","a@a.a");
        //this.deleteParticipant("a48a7a4d-e513-4d52-9ba7-173f23ee540f","a@.a");
    }
    public void insertMeeting() throws CrudException, CommitException,
            UnknownTransactionStatusException
    {
        DistributedTransaction tx = service.start();
        String id = UUID.randomUUID().toString();
        String passcode = RandomStringUtils.randomNumeric(8);
        List<Value> values = new ArrayList<Value>();
        values.add(new TextValue("passcode",passcode));
        values.add(new TextValue("name","test1"));
        values.add(new TextValue("date","2020-09-23"));
        values.add(new TextValue("description","12:00"));
        values.add(new TextValue("timefrom","12:00"));
        values.add(new TextValue("timeto","13:00"));
        values.add(new TextValue("url","https://us02web.zoom.us/j/00000000000"));
        values.add(new TextValue("participants","{}"));
        Put put = new Put(new Key(new TextValue(ID, id))).withValues(values);
        tx.put(put);
        tx.commit();
    }
    public void selectMeeting(String id) throws CrudException, CommitException, UnknownTransactionStatusException
    {
        DistributedTransaction tx = service.start();
        Get get = new Get(new Key(new TextValue(ID, id)));
        Optional<Result> result = tx.get(get);
        Map<String,Value> values = result.get().getValues();
        System.out.println(((TextValue) values.get("passcode")).getString().get());
        System.out.println(((TextValue) values.get("name")).getString().get());
        System.out.println(((TextValue) values.get("date")).getString().get());
    }
    public void updateMeeting(String id) throws CrudException, CommitException, UnknownTransactionStatusException
    {
        DistributedTransaction tx = service.start();
        //you must Get. before update(put)
        Get get = new Get(new Key(new TextValue(ID, id)));
        Optional<Result> result = tx.get(get);

        List<Value> values = new ArrayList<Value>();
        values.add(new TextValue("name","test2"));
        values.add(new TextValue("date","2020-09-23"));
        Put put = new Put(new Key(new TextValue(ID, id))).withValues(values);
        tx.put(put);
        tx.commit();
    }
    public void insertOrUpdateParticipant(String meeting_id,String email) throws CrudException, CommitException, UnknownTransactionStatusException
    {
        DistributedTransaction tx = service.start();
        //you must Get. before update(put)
        Get get = new Get(new Key(new TextValue(ID, meeting_id)));
        Optional<Result> result = tx.get(get);
        String string_particpants = ((TextValue)result.get().getValue("participants").get()).getString().get();
        Gson gson = new Gson();

        Map<String, Map<String, String>> participants = gson.fromJson(string_particpants, Map.class);
        Map<String, String> participant = new HashMap<>();
        participant.put("email",email);
        participant.put("name","Name Example3");
        participant.put("company","'Example co.,ltd");
        participant.put("position","Designer");
        participants.put(email,participant);
        String json_participants = gson.toJson(participants);
        Put put = new Put(new Key(new TextValue(ID, meeting_id))).withValue(new TextValue("participants",json_participants));
        tx.put(put);
        tx.commit();
    }
    public void deleteParticipant(String meeting_id,String email) throws CrudException, CommitException, UnknownTransactionStatusException
    {
        DistributedTransaction tx = service.start();
        //you must Get. before update(put)
        Get get = new Get(new Key(new TextValue(ID, meeting_id)));
        Optional<Result> result = tx.get(get);
        String string_particpants = ((TextValue)result.get().getValue("participants").get()).getString().get();
        Gson gson = new Gson();

        Map<String, Map<String, String>> participants = gson.fromJson(string_particpants, Map.class);
        participants.remove(email);
        String json_participants = gson.toJson(participants);
        Put put = new Put(new Key(new TextValue(ID, meeting_id))).withValue(new TextValue("participants",json_participants));
        tx.put(put);
        tx.commit();
    }
    public void selectParticipants(String meeting_id) throws CrudException, CommitException, UnknownTransactionStatusException
    {
        DistributedTransaction tx = service.start();
        Get get = new Get(new Key(new TextValue(ID, meeting_id)));
        Optional<Result> result = tx.get(get);
        String string_particpants = ((TextValue)result.get().getValue("participants").get()).getString().get();
        Gson gson = new Gson();
        Map<String, Map<String, String>> participants = gson.fromJson(string_particpants, Map.class);
        System.out.println(participants);
    }
    public void selectParticipant(String meeting_id,String email) throws CrudException, CommitException, UnknownTransactionStatusException
    {
        DistributedTransaction tx = service.start();
        Get get = new Get(new Key(new TextValue(ID, meeting_id)));
        Optional<Result> result = tx.get(get);
        String string_particpants = ((TextValue)result.get().getValue("participants").get()).getString().get();
        Gson gson = new Gson();
        Map<String, Map<String, String>> participants = gson.fromJson(string_particpants, Map.class);
        Map<String, String> participant = participants.get(email);
        System.out.println(participant);
    }
    public void close() {
        service.close();
    }
}

