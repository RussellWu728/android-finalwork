package com.example.finals_new;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {UserInfo.class},version = 1,exportSchema = false)
public abstract class CourseDataBase  extends RoomDatabase {

    private static final String DB_NAME="android_finalwork.db";
    private static volatile CourseDataBase instance;

    static synchronized CourseDataBase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static CourseDataBase create(final Context context) {
        return Room.databaseBuilder(context, CourseDataBase.class, DB_NAME)
                .addMigrations(migration_1_2)
                .addMigrations(migration_2_3)
                .addMigrations(migration_3_4)
                .addMigrations(migration_4_5)
                .build();
    }

    //@{版本号升级后一定要加migration
    private static Migration migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user ADD certify INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static Migration migration_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE video_list ADD stage TEXT DEFAULT ''");
        }
    };
    private static Migration migration_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE video_list ADD views INTEGER NOT NULL DEFAULT 0");
        }
    };
    private static Migration migration_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user ADD partner INTEGER NOT NULL DEFAULT 0");
        }
    };
    //@}

    public abstract UserDao getUserDao();




}
