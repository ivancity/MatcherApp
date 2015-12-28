package io.pegacao.app.data;

import android.support.annotation.NonNull;
import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Created by ivanm on 12/3/15.
 * This class initialize and saves a static repository in the heap that will be accessed multiple
 * times. This repository is a wrapper class that enables interactions with a PeopleServiceApi
 * object. It will receive a PeopleServiceApi depending of the flavor build. A mock flavor will
 * pass a FakePeopleDataApiImpl, and the prod mock wiill pass a PeopleServiceApiImmpl. Either
 * PeopleServiceApi will be saved in the static PeopleRepository.
 */
public class PeopleRepositories {
    public PeopleRepositories() {
        //Nothing here.
    }

    //Repository that keeps data in the heap.
    private static PeopleRepository repository = null;

    public synchronized static PeopleRepository getInMemoryRepoInstance(@NonNull PeopleServiceApi peopleServiceApi) {
        checkNotNull(peopleServiceApi);
        if (null == repository) {
            //initialize a new repository using the PeopleServiceApi that this method receives.
            repository = new InMemoryPeopleRepository(peopleServiceApi);
        }
        return repository;
    }
}
