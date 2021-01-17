package ru.zesgen.simpledictionary.server.dao;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dictionary {

    private final ConcurrentHashMap<String, Set<String>> dictionaryMap = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, Set<String>> getDictionaryMap() {
        return dictionaryMap;
    }

    public void add(String word, Set<String> meanings) {
        ConcurrentSkipListSet<String> newSet = new ConcurrentSkipListSet<>(meanings);
        dictionaryMap.merge(word, newSet, (existingMeanings, newMeanings) -> {
            existingMeanings.addAll(newMeanings);
            return existingMeanings;
        });
    }

    public Set<String> get(String word) {
        Set<String> meanings = dictionaryMap.get(word);
        if (meanings == null) {
            return Collections.unmodifiableSet(new ConcurrentSkipListSet<>());
        } else {
            return Collections.unmodifiableSet(meanings);
        }
    }

    public boolean deleteMeanings(String word, Set<String> meanings) {
        AtomicBoolean isDeletingSuccessful = new AtomicBoolean(false);
        dictionaryMap.compute(word, (keyWord, existingMeanings) -> {
            if (existingMeanings != null && existingMeanings.containsAll(meanings)) {
                existingMeanings.removeAll(meanings);
                if (existingMeanings.isEmpty())
                    existingMeanings = null;
                isDeletingSuccessful.set(true);
            }
            return existingMeanings;
        });
        return isDeletingSuccessful.get();
    }
}
