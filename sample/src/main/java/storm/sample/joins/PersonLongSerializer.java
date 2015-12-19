package storm.sample.joins;

import storm.query.Selection;
import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 19.12.2015.
 */
class PersonLongSerializer implements StormSerializer<Person, Long> {

    @Override
    public Long serialize(Person person) {
        return null;
    }

    @Override
    public Person deserialize(Long aLong) {
        return JoinsSample.sStorm.query(Person.class)
                .where(new Selection().equals("id", aLong))
                .asOne();
    }
}
