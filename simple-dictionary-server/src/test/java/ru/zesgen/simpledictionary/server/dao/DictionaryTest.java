package ru.zesgen.simpledictionary.server.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListSet;


class DictionaryTest {

    static final String WORD_STUB = "Some word";
    static final String MEANING1_STUB = "Some meaning 1";
    static final String MEANING2_STUB = "Some meaning 2";
    static final String MEANING3_STUB = "Some meaning 3";
    static final String MEANING4_STUB = "Some meaning 4";
    static final String MEANING5_STUB = "Some meaning 5";

    Dictionary dictionaryUnderTest = new Dictionary();
    Set<String> meaningsStub = new ConcurrentSkipListSet<>();

    @BeforeEach
    void setUp() {
        meaningsStub.add(MEANING1_STUB);
        meaningsStub.add(MEANING2_STUB);
        meaningsStub.add(MEANING3_STUB);
    }

    @Test
    void constructor_shouldExecuteCorrectly() {
        assertThat(dictionaryUnderTest.getDictionaryMap()).isEmpty();
    }

    @Test
    void add_shouldExecuteCorrectly_ifThereIsNoWordInTheDictionary() {
        dictionaryUnderTest.add(WORD_STUB, meaningsStub);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(meaningsStub);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isNotSameAs(meaningsStub);
    }

    @Test
    void add_shouldExecuteCorrectly_ifTheWordIsInTheDictionaryAndNoNewMeaningsAdded() {
        dictionaryUnderTest.add(WORD_STUB, meaningsStub);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(meaningsStub);
        dictionaryUnderTest.add(WORD_STUB, meaningsStub);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(meaningsStub);
    }

    @Test
    void add_shouldExecuteCorrectly_ifTheWordIsInTheDictionaryAndSeveralNewMeaningsAdded() {
        dictionaryUnderTest.add(WORD_STUB, meaningsStub);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(meaningsStub);
        Set<String> newMeaningsToAdd = new LinkedHashSet<>();
        newMeaningsToAdd.add(MEANING1_STUB);
        newMeaningsToAdd.add(MEANING2_STUB);
        newMeaningsToAdd.add(MEANING3_STUB);
        newMeaningsToAdd.add(MEANING4_STUB);
        newMeaningsToAdd.add(MEANING5_STUB);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isNotEqualTo(newMeaningsToAdd);
        dictionaryUnderTest.add(WORD_STUB, newMeaningsToAdd);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(newMeaningsToAdd);
    }

    @Test
    void get_shouldReturnCorrectSet_ifThereIsAWordInTheDictionary() {
        dictionaryUnderTest.getDictionaryMap().put(WORD_STUB, meaningsStub);

        Set<String> actualMeanings = dictionaryUnderTest.get(WORD_STUB);

        assertThat(actualMeanings).isEqualTo(meaningsStub);
        assertThat(actualMeanings).isNotSameAs(meaningsStub);
    }

    @Test
    void get_shouldReturnUnmodifiableSet_ifThereIsAWordInTheDictionary() {
        dictionaryUnderTest.getDictionaryMap().put(WORD_STUB, meaningsStub);

        Set<String> actualMeanings = dictionaryUnderTest.get(WORD_STUB);

        assertThatThrownBy(() -> actualMeanings.add(MEANING2_STUB))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> actualMeanings.remove(MEANING1_STUB))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(actualMeanings::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void get_shouldReturnEmptyUnmodifiableSet_ifThereIsNoWordInTheDictionary() {
        Set<String> actualMeanings = dictionaryUnderTest.get(WORD_STUB);

        assertThat(actualMeanings).isEmpty();
        assertThatThrownBy(() -> actualMeanings.add(MEANING2_STUB))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> actualMeanings.remove(MEANING1_STUB))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(actualMeanings::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void delete_shouldExecuteCorrectlyAndReturnTrue_ifThereIsAWordAndAllMeaningsAreBeingDeletedFromTheDictionary() {
        dictionaryUnderTest.getDictionaryMap().put(WORD_STUB, meaningsStub);

        boolean operationResult = dictionaryUnderTest.deleteMeanings(WORD_STUB, meaningsStub);

        assertThat(operationResult).isTrue();
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isNull();
    }

    @Test
    void delete_shouldExecuteCorrectlyAndReturnTrue_ifThereIsAWordAndNotAllMeaningsAreBeingDeletedFromTheDictionary() {
        dictionaryUnderTest.getDictionaryMap().put(WORD_STUB, meaningsStub);
        Set<String> meaningsToDelete = new ConcurrentSkipListSet<>(meaningsStub);
        meaningsToDelete.remove(MEANING1_STUB);
        Set<String> expectedMeanings = Collections.singleton(MEANING1_STUB);

        boolean operationResult = dictionaryUnderTest.deleteMeanings(WORD_STUB, meaningsToDelete);

        assertThat(operationResult).isTrue();
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(expectedMeanings);
    }

    @Test
    void delete_shouldExecuteCorrectlyAndReturnFalse_ifThereIsAWordAndNotAllMeaningsToDeleteAreInTheDictionary() {
        dictionaryUnderTest.getDictionaryMap().put(WORD_STUB, meaningsStub);
        Set<String> meaningsToDelete = new ConcurrentSkipListSet<>(meaningsStub);
        meaningsToDelete.add(MEANING4_STUB);

        boolean operationResult = dictionaryUnderTest.deleteMeanings(WORD_STUB, meaningsToDelete);

        assertThat(operationResult).isFalse();
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(meaningsStub);
    }

    @Test
    void delete_shouldExecuteCorrectlyAndReturnFalse_ifThereIsNoWordInTheDictionary() {
        dictionaryUnderTest.getDictionaryMap().put(WORD_STUB, meaningsStub);
        Set<String> meaningsToDelete = new ConcurrentSkipListSet<>(meaningsStub);
        String nonExistingWord = "SomeWord";

        boolean operationResult = dictionaryUnderTest.deleteMeanings(nonExistingWord, meaningsToDelete);

        assertThat(operationResult).isFalse();
        assertThat(dictionaryUnderTest.getDictionaryMap().get(WORD_STUB)).isEqualTo(meaningsStub);
        assertThat(dictionaryUnderTest.getDictionaryMap().get(nonExistingWord)).isNull();
    }

}