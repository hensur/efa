package de.nmichael.efa.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeDistance;
import de.nmichael.efa.data.types.DataTypeTime;

import java.io.IOException;
import java.util.UUID;

public class LogbookRecordDeserializer extends JsonDeserializer<LogbookRecord> {

    @Override
    public LogbookRecord deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        LogbookRecord lr = new LogbookRecord();

        lr.setDate(DataTypeDate.parseDate(node.get("date").asText()));
        lr.setEndDate(DataTypeDate.parseDate(node.get("endDate").asText()));

        lr.setDistance(new DataTypeDistance(node.get("distance").longValue()));
        lr.setComments(node.get("comments").textValue());
        lr.setSessionType(node.get("sessionType").textValue());

        lr.setStartTime(DataTypeTime.parseTime(node.get("startTime").textValue()));
        lr.setEndTime(DataTypeTime.parseTime(node.get("endTime").textValue()));

        if (node.get("boat") != null) {
            lr.setBoatId(UUID.fromString(node.get("boat").get("id").textValue()));
        }

        if (node.get("crew") != null) {
            lr.setBoatCaptainPosition(node.get("crew").get("captain").intValue());
            lr.setCoxId(UUID.fromString(node.get("crew").get("cox").get("id").textValue()));
            for (int i = 1; i < lr.CREW_MAX; i++) {
                if (node.get("crew").get(Integer.toString(i)) != null) {
                    lr.setCrewId(i, UUID.fromString(node.get("crew").get(Integer.toString(i)).get("id").textValue()));
                }
            }
        }

        if (node.get("destination") != null) {
            lr.setDestinationId(UUID.fromString(node.get("destination").get("id").textValue()));
        }
        if (node.get("session_group") != null) {
            lr.setSessionGroupId(UUID.fromString(node.get("session_group").get("id").textValue()));
        }

        return lr;
    }
}