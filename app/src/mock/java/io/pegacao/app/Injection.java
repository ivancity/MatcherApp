package io.pegacao.app;

import io.pegacao.app.data.FakePeopleDataApiImpl;
import io.pegacao.app.data.PeopleRepositories;
import io.pegacao.app.data.PeopleRepository;

/**
 * Created by ivanm on 12/3/15.
 * Class created for injecting a new PeopleRepository that will save in the heap the ArrayMap
 * collection of all People gotten by the Server API.
 */
public class Injection {
    /*
    public static ImageFile provideImageFile() {
        return new FakeImageFileImpl();
    }
*/
    public static PeopleRepository providePeopleRepository() {
        return PeopleRepositories.getInMemoryRepoInstance(new FakePeopleDataApiImpl());
    }
}
