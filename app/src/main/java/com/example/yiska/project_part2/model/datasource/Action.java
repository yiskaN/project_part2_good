package com.example.yiska.project_part2.model.datasource;

/**
 * Interface action, declare function for use when insert, remove or update data from fireBase
 * @param <T>
 */

public interface Action<T> {
    void onSuccess(T obj);

    void onFailure(Exception exception);

    void onProgress(String status, double percent);
}
