package io.pegacao.app.data;

import android.support.v4.util.ArrayMap;

/**
 * Created by ivanm on 12/11/15.
 * End point for data that is going to be displayed on the main screen. We have here all the people
 * that are going to be presented.
 * We can get this DATA in ArrayMap coming from SQL or from network.
 */
public class PeopleServiceApiEndpoint {

    //TODO: Create a network connection and request to Server API
    static {
        DATA = new ArrayMap<>(2);
        addPerson("11111", "Barabara", 25, null, null, "2", "1");
        addPerson("22222", "Nelly", 30, null, null, "5", "3");
    }

    private final static ArrayMap<String, Person> DATA;

    /**
     * Adding a Person object to the ArrayMap collection. Important to note that this method
     * will create a new instance of a Person object every time is it called. Thus it has
     * some memory creation overhead when called.
     * @param facebookId id coming from Facebook API me endpoint.
     * @param name name of the person.
     * @param imageUrls array of urls that we can find each picture from the network.
     * @param profileMessage Message to display for this Person profile.
     */
    private static void addPerson(String facebookId,
                                  String name,
                                  int age,
                                  String[] imageUrls,
                                  String profileMessage,
                                  String distance,
                                  String lastTimeSeen) {
        //debug implementation
        //TODO use the full constructor erase this line
        Person person = new Person(facebookId, name, age, imageUrls, profileMessage, distance, lastTimeSeen);
        DATA.put(person.facebookId, person);

    }

    /**
     * Send to the method caller a Collection of Person objects.
     * @return the Notes to show when starting the app.
     */
    public static ArrayMap<String, Person> loadPersistedNotes() {
        return DATA;
    }
}
