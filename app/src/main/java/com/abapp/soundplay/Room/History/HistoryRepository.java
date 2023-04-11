package com.abapp.soundplay.Room.History;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HistoryRepository {
    private final HistoryDao myDao;
    private final Executor executor;

    public HistoryRepository(MyDatabaseHistory myDatabase) {
        myDao = myDatabase.myDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(TableHistory tableHistory) {
        executor.execute(() -> myDao.insert(tableHistory));
    }
}
