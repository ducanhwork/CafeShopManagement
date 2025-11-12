package com.group3.application.model.repository;

/**
 * A generic callback interface for asynchronous repository operations.
 * @param <T> The type of the result to be returned on completion.
 */
public interface RepositoryCallback<T> {
    void onComplete(T result);
}
