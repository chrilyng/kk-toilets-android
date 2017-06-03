package dk.siit.kktoilets;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ToiletAppModule.class, NetworkModule.class})
public interface NetworkComponent {
    void inject(DownloadService service);
}
