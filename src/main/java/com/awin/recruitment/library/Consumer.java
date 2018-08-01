package com.awin.recruitment.library;

public interface Consumer<T> {

    void consume(
        Iterable<T> messages
    );
}