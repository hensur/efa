package de.nmichael.efa.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.nmichael.efa.data.BoatRecord;

import java.io.IOException;

public class BoatRecordSerializer extends JsonSerializer<BoatRecord> {

    @Override
    public void serialize(BoatRecord value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException,
            JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("id", value.getId().toString());
        jgen.writeStringField("name", value.getName());

        jgen.writeObjectFieldStart("types");
        for (int i = 1; i <= value.getNumberOfVariants(); i++) {
            int idx = value.getVariantIndex(i);
            jgen.writeObjectFieldStart(Integer.toString(value.getTypeVariant(idx)));
            jgen.writeStringField("description", value.getTypeDescription(idx));
            jgen.writeStringField("rigging", value.getTypeRigging(idx));
            jgen.writeStringField("type", value.getTypeType(idx));
            jgen.writeStringField("seats", value.getTypeSeats(idx));
            jgen.writeStringField("coxing", value.getTypeCoxing(idx));
            jgen.writeEndObject();
        }
        jgen.writeEndObject();
        jgen.writeEndObject();
    }
}