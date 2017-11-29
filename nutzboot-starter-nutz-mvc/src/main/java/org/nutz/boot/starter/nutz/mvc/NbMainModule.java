package org.nutz.boot.starter.nutz.mvc;

import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.annotation.Localization;

@LoadingBy(NbMvcLoading.class)
@Localization("locales/")
public class NbMainModule {

}
