package com.veho.controller;
import com.google.gson.Gson;
import com.veho.config.StandardResponse;
import com.veho.config.StatusResponse;
import com.veho.model.Meeting;
import com.veho.model.Participant;
import com.veho.service.MeetingService;
import com.veho.service.MeetingServiceImpl;
import spark.Spark;

import java.util.HashMap;
import java.util.List;


import static spark.Spark.*;
public class MeetingController {

    final MeetingService meetingService = new MeetingServiceImpl();

  public MeetingController() {

      post("/meetings", (request, response) -> {

          response.type("application/json");

          Meeting meeting = new Gson().fromJson(request.body(), Meeting.class);
          Meeting meetingDB = meetingService.insertMeeting(meeting);
          return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
          new Gson().toJsonTree(meetingDB)));
      });

      get("/meetings/:meeting_id",(request, response) -> {
          response.type("application/json");

          return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
                  new Gson().toJsonTree(meetingService.selectMeetingById(request.params(":meeting_id")))));
      });

      put("/meetings/:meeting_id", (request, response) -> {
          response.type("application/json");
         String meeting_id = request.params(":meeting_id");
          Meeting toEdit = new Gson().fromJson(request.body(), Meeting.class);
          Meeting meeting = meetingService.updateMeeting(meeting_id, toEdit);

          if (toEdit != null) {
              return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
                      new Gson().toJsonTree(meeting)));
          } else {
              return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,
                      new Gson().toJson("User not found or error in edit")));
          }
      });
      post("/meetings/:meeting_id/participants", (request, response) -> {

          response.type("application/json");

          String meeting_id = request.params(":meeting_id");


          Participant participant = new Gson().fromJson(request.body(), Participant.class);
          // If Participant exist response error
          if (meetingService.isExitParticipantWithEmail(meeting_id, participant.getEmail()))
          {
              return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,
                      new Gson().toJson("Email is exist")));
          }
          Participant participantDB = meetingService.insertParticipant(meeting_id, participant);

          if (participant != null) {
              return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
                      new Gson().toJsonTree(participantDB)));
          } else {
              return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,
                      new Gson().toJson("Participant not information")));
          }
      });

      get("/meetings/:meeting_id/participants", (request, response) -> {

          response.type("application/json");
          String meeting_id = request.params(":meeting_id");
          List<Participant> participants = meetingService.selectParticipants(meeting_id);
              return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
                      new Gson().toJsonTree(participants)));

      });
      put("/meetings/:meeting_id/participants/:email", (request, response) -> {

          response.type("application/json");
          String meeting_id = request.params(":meeting_id");
          String email = request.params(":email");
          Participant participant = new Gson().fromJson(request.body(), Participant.class);
          Participant participantDB = meetingService.updateParticipant(meeting_id,email,participant);
          return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
                  new Gson().toJsonTree(participantDB)));

      });

      delete("/meetings/:meeting_id/participants/:email", (request, response) -> {

          response.type("application/json");
          String meeting_id = request.params(":meeting_id");
          String email = request.params(":email");
          if (!meetingService.deleteParticipant(meeting_id,email)){
              return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,
                      new Gson().toJson("Email not any participant")));
          } else {
              return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
                      new Gson().toJson("Delete success")));
          }

      });
      get("/meetings/:meeting_id/participants/:email", (request, response) -> {

          response.type("application/json");
          String meeting_id = request.params(":meeting_id");
          String email = request.params(":email");
          Participant participant = meetingService.selectParticipant(meeting_id,email);
          if (participant != null) {
              return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS,
                      new Gson().toJsonTree(participant)));
          } else {
              return new Gson().toJson(new StandardResponse(StatusResponse.ERROR,
                      new Gson().toJson("Participant not information")));
          }
      });
      Spark.port(4576);
      addCORS();

  }
    private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();

    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }
    public final static void addCORS() {
        Spark.after((request, response) -> {
            corsHeaders.forEach((key, value) -> {
                response.header(key, value);
            });
        });
    }
}
