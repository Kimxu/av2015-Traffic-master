package com.hinsty;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {

    public static void main(String... args) throws Exception {
        Schema schema = new Schema(1, "com.hinsty.traffic.dao");
        addTraffic(schema);
        addAppDayTraffic(schema);
        schema.enableKeepSectionsByDefault();
        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    public static void addAppDayTraffic(Schema schema) {
        Entity entity = schema.addEntity("AppDayTraffic");
        Property packageName = entity.addStringProperty("packageName").getProperty();
        Property year =entity.addIntProperty("year").getProperty();
        Property month =entity.addIntProperty("month").getProperty();
        Property day =entity.addIntProperty("day").getProperty();
        entity.addStringProperty("name");
        entity.addLongProperty("rx");
        entity.addLongProperty("tx");
        Index index=new Index();
        index.addProperty(packageName);
        index.addProperty(year);
        index.addProperty(month);
        index.addProperty(day);
        index.makeUnique();
        entity.addIndex(index);
    }

    public static void addTraffic(Schema schema) {
        Entity entity = schema.addEntity("Traffic");
        Property year = entity.addIntProperty("year").getProperty();
        Property month = entity.addIntProperty("month").getProperty();
        Property day = entity.addIntProperty("day").getProperty();
        entity.addLongProperty("rx");
        entity.addLongProperty("tx");
        Index index=new Index();
        index.addProperty(year);
        index.addProperty(month);
        index.addProperty(day);
        index.makeUnique();
        entity.addIndex(index);
    }
}
