package com.framework;

import java.util.stream.Stream;

import com.framework.Utils.Pair;

public abstract class AJob<K,V>{

    public abstract Stream<Pair<K,V>> execute();

}

