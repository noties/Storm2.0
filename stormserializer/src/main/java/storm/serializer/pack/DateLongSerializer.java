package storm.serializer.pack;

import java.util.Date;

import storm.serializer.StormSerializer;

/**
 * Created by Dimitry Ivanov on 12.12.2015.
 */
public class DateLongSerializer implements StormSerializer<Date, Long> {

    @Override
    public Long serialize(Date date) {
        return date != null ? date.getTime() : -1L;
    }

    @Override
    public Date deserialize(Long aLong) {
        return aLong == null || aLong == -1L ? null : new Date(aLong);
    }
}
