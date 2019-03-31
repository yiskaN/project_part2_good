package com.example.yiska.project_part2.model.datasource;

public interface NotifyDataChange<T> {

    void OnDataChanged(T obj);

    void onFailure(Exception exception);
}
