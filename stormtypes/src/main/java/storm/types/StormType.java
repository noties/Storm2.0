package storm.types;

public enum StormType {

    INT,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    BYTE_ARRAY;

    public static StormType forValue(Class<?> fieldType) {

        if (Integer.TYPE.equals(fieldType)
                || Integer.class.equals(fieldType)) {
            return StormType.INT;
        }

        if (Long.TYPE.equals(fieldType)
                || Long.class.equals(fieldType)) {
            return StormType.LONG;
        }

        if (Float.TYPE.equals(fieldType)
                || Float.class.equals(fieldType)) {
            return StormType.FLOAT;
        }

        if (Double.TYPE.equals(fieldType)
                || Double.class.equals(fieldType)) {
            return StormType.DOUBLE;
        }

        if (String.class.equals(fieldType)) {
            return StormType.STRING;
        }

        if (byte[].class.equals(fieldType)) {
            return StormType.BYTE_ARRAY;
        }

        return null;
    }
}
