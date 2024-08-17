package com.mycompany.jobscheduler.Utils;

import java.util.stream.Stream;

public abstract class AJob<K,V>{

    public abstract Stream<Pair<K,V>> execute();

}

