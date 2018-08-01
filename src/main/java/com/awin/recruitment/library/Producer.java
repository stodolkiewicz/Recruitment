package com.awin.recruitment.library;

public interface Producer<T> {

    void produce(
        Iterable<T> messages
    );
}
