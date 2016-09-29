import android.app.Application;

import com.facebook.stetho.Stetho;

public class StethoUtils{

    public static void install(Application application){
        Stetho.initialize(
                Stetho.newInitializerBuilder(application)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(application))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(application))
                        .build());

    }
}