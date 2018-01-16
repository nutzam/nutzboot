package org.nutz.boot.starter.feign;

import java.lang.reflect.Type;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

public class NutzJsonEncoder implements Encoder {
    
    protected JsonFormat jf;
    
    public NutzJsonEncoder() {
        this(JsonFormat.full());
    }
    
    public NutzJsonEncoder(JsonFormat jf) {
        this.jf = jf == null ? JsonFormat.full() : jf;
    }

    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        template.body(Json.toJson(object, jf));
    }

}
