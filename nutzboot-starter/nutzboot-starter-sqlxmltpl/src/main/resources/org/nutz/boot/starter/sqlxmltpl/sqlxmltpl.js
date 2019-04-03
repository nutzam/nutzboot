var ioc = {
    sqlTplIocEventListener: {
        type: "com.github.threefish.nutz.sqltpl.SqlTplIocEventListener",
        args: [{refer: '$ioc'}]
    },
    beetlSqlTemplteEngineImpl: {
        type: "com.github.threefish.nutz.sqltpl.BeetlSqlTemplteEngineImpl",
        events: {
            create: "init"
        },
        fields: {
            statementStart: {java: "$conf.get('SqlXmlTpl.statementStart')"},
            statementEnd: {java: "$conf.get('SqlXmlTpl.statementEnd')"}
        }
    }
}
