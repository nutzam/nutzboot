var ioc = {
    emailAuthenticator: {
        type: "org.apache.commons.mail.DefaultAuthenticator",
        args: [{java: "$conf.get('email.UserName')"}, {java: "$conf.get('email.Password')"}]
    },
    imageHtmlEmail: {
        type: "org.apache.commons.mail.ImageHtmlEmail",
        singleton: false,
        fields: {
            hostName: {java: "$conf.get('email.HostName')"},
            smtpPort: {java: "$conf.get('email.SmtpPort')"},
            authenticator: {refer: "emailAuthenticator"},
            SSLOnConnect: {java: "$conf.get('email.SSLOnConnect')"},
            from: {java: "$conf.get('email.From')"},
            charset: {java: "$conf.get('email.charset')"}
        }
    }
};