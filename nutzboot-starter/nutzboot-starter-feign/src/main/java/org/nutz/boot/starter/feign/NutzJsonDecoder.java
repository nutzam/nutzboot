package org.nutz.boot.starter.feign;

import java.io.IOException;
import java.lang.reflect.Type;

import org.nutz.json.Json;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

public class NutzJsonDecoder implements Decoder {

    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        return Json.fromJson(type, response.body().asReader());
    }

}
