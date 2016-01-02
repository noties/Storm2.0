package storm.parser;

import storm.reflect.ReflectionInstanceCreator;

/**
 * Created by Dimitry Ivanov on 02.01.2016.
 */
public abstract class StormParserItemProviderBase<I extends StormParserItem> implements StormParserItemProvider<I> {

    private final StormParserAptClassNameBuilder mAptNameBuilder;

    public StormParserItemProviderBase(StormParserAptClassNameBuilder aptClassNameBuilder) {
        this.mAptNameBuilder = aptClassNameBuilder;
    }

    @Override
    public <T> T apt(Class<?> cl) {
        return aptClassInstance(cl);
    }

    protected <T> T aptClassInstance(Class<?> model) {
        Class<?> aptClass;
        try {
            aptClass = Class.forName(
                    mAptNameBuilder.fullName(
                            model.getPackage().getName(),
                            model.getSimpleName()
                    )
            );
        } catch (ClassNotFoundException e) {
            return null;
        }

        //noinspection unchecked
        return (T) ReflectionInstanceCreator.newInstance(aptClass);
    }
}
