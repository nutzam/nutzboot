package org.nutz.boot.starter.email;

import org.nutz.ioc.loader.json.JsonLoader;

public class EmailIocLoader extends JsonLoader {

    public EmailIocLoader() {
        super("org/nutz/boot/starter/email/email.js");
    }
}
