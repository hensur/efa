package de.nmichael.efa.api;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.nmichael.efa.data.LogbookRecord;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
    public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

        final ObjectMapper defaultObjectMapper;

        public ObjectMapperProvider() {
            defaultObjectMapper = createDefaultMapper();
        }

        @Override
        public ObjectMapper getContext(final Class<?> type) {
                return defaultObjectMapper;
        }

        private static ObjectMapper createDefaultMapper() {
            final ObjectMapper result = new ObjectMapper();
            result.enable(SerializationFeature.INDENT_OUTPUT);
            SimpleModule efaModule = new SimpleModule("efaModule",
                                          new Version(1,0,0,null, null, null));
            efaModule.addSerializer(LogbookRecord.class, new LogbookRecordSerializer());
            efaModule.addDeserializer(LogbookRecord.class, new LogbookRecordDeserializer());
            result.registerModule(efaModule);

            return result;
        }

    }
