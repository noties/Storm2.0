package storm.sample.serialize;

/**
 * Created by Dimitry Ivanov on 13.02.2016.
 */
class SerializeJsonItem {

    private long id;
    private String text;

    SerializeJsonItem() {

    }

    SerializeJsonItem(long id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return "SerializeJsonItem{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
