package de.nmichael.efa.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.nmichael.efa.data.PersonRecord;

import java.io.IOException;

public class PersonRecordSerializer extends JsonSerializer<PersonRecord> {

    @Override
    public void serialize(PersonRecord value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException,
            JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.getId().toString());
        jgen.writeStringField("firstName", value.getFirstName());
        jgen.writeStringField("lastName", value.getLastName());
        jgen.writeStringField("nameAffix", value.getNameAffix());
        jgen.writeStringField("title", value.getTitle());
        jgen.writeStringField("gender", value.getGender());
        jgen.writeStringField("birthday", value.getBirthday().getDateString("YYYY-MM-DD"));
        jgen.writeStringField("association", value.getAssocitation());
        jgen.writeObjectFieldStart("status");
        jgen.writeStringField("id", value.getStatusId().toString());
        jgen.writeStringField("name", value.getStatusName());
        jgen.writeEndObject();
        jgen.writeObjectFieldStart("address");
        jgen.writeStringField("street", value.getAddressStreet());
        jgen.writeStringField("zip", value.getAddressZip());
        jgen.writeEndObject();
        jgen.writeEndObject();
    }
}