package storm.core;

import storm.query.Query;

/**
 * Created by Dimitry Ivanov on 20.12.2015.
 */
interface StormSimpleQueryDispatcher {

    int asInt(Storm storm, Query query, int defValue);
    long asLong(Storm storm, Query query, long defValue);
    float asFloat(Storm storm, Query query, float defValue);
    double asDouble(Storm storm, Query query, double defValue);
    String asString(Storm storm, Query query, String defValue);
    byte[] asByteArray(Storm storm, Query query, byte[] defValue);
}
