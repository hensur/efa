package de.nmichael.efa.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.nmichael.efa.data.LogbookRecord;

import java.io.IOException;

public class LogbookRecordSerializer extends JsonSerializer<LogbookRecord> {

    @Override
    public void serialize(LogbookRecord value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException,
            JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.getEntryId().intValue());
        jgen.writeStringField("date", value.getDate().getDateString("YYYY-MM-DD"));
        jgen.writeStringField("end_date", value.getDate().getDateString("YYYY-MM-DD"));

        jgen.writeNumberField("distance", value.getDistance().getValueInMeters());
        jgen.writeStringField("comments", value.getComments());
        jgen.writeStringField("session_type", value.getSessionType());

        jgen.writeStringField("start_time", value.getStartTime().toString());
        jgen.writeStringField("end_time", value.getEndTime().toString());

        jgen.writeObjectFieldStart("boat");
        jgen.writeStringField("id", value.getBoatId().toString());
        jgen.writeNumberField("variant", value.getBoatVariant());
        jgen.writeStringField("name", value.getBoatAsName());
        jgen.writeEndObject();

        jgen.writeObjectFieldStart("crew");
        jgen.writeNumberField("captain", value.getBoatCaptainPosition());

        jgen.writeObjectFieldStart("cox");
        jgen.writeStringField("id", value.getCoxId().toString());
        jgen.writeStringField("name", value.getCoxAsName());
        jgen.writeEndObject();

        for (int i = 1; i < value.CREW_MAX; i++) {
            if (value.getCrewId(i) != null) {
                jgen.writeObjectFieldStart(String.valueOf(i));
                jgen.writeStringField("id", value.getCrewId(i).toString());
                jgen.writeStringField("name", value.getCrewAsName(i));
                jgen.writeEndObject();
            }
        }
        jgen.writeEndObject();

        jgen.writeObjectFieldStart("destination");
        jgen.writeStringField("id", value.getDestinationId().toString());
        jgen.writeStringField("variant", value.getDestinationAndVariantName(-1, false, false));
        jgen.writeEndObject();

        if (value.getSessionGroupId() != null) {
            jgen.writeObjectFieldStart("session_group");
            jgen.writeStringField("id", value.getSessionGroupId().toString());
            jgen.writeStringField("name", value.getSessionGroupAsName());
            jgen.writeEndObject();
        }
        jgen.writeEndObject();
    }
}
