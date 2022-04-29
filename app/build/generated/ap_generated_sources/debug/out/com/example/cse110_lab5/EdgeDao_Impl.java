package com.example.cse110_lab5;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class EdgeDao_Impl implements EdgeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Edge> __insertionAdapterOfEdge;

  private final EntityDeletionOrUpdateAdapter<Edge> __deletionAdapterOfEdge;

  private final EntityDeletionOrUpdateAdapter<Edge> __updateAdapterOfEdge;

  public EdgeDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEdge = new EntityInsertionAdapter<Edge>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `edges` (`id`,`street`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Edge value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
        if (value.street == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.street);
        }
      }
    };
    this.__deletionAdapterOfEdge = new EntityDeletionOrUpdateAdapter<Edge>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `edges` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Edge value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
      }
    };
    this.__updateAdapterOfEdge = new EntityDeletionOrUpdateAdapter<Edge>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `edges` SET `id` = ?,`street` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Edge value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
        if (value.street == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.street);
        }
        if (value.id == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.id);
        }
      }
    };
  }

  @Override
  public long insert(final Edge edge) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfEdge.insertAndReturnId(edge);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Long> insertAll(final List<Edge> edge) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      List<Long> _result = __insertionAdapterOfEdge.insertAndReturnIdsList(edge);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final Edge edge) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__deletionAdapterOfEdge.handle(edge);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final Edge edge) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__updateAdapterOfEdge.handle(edge);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Edge get(final String id) {
    final String _sql = "SELECT * FROM `edges` WHERE `id`=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfStreet = CursorUtil.getColumnIndexOrThrow(_cursor, "street");
      final Edge _result;
      if(_cursor.moveToFirst()) {
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpStreet;
        if (_cursor.isNull(_cursorIndexOfStreet)) {
          _tmpStreet = null;
        } else {
          _tmpStreet = _cursor.getString(_cursorIndexOfStreet);
        }
        _result = new Edge(_tmpId,_tmpStreet);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Edge> getAll() {
    final String _sql = "SELECT * FROM `edges`";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfStreet = CursorUtil.getColumnIndexOrThrow(_cursor, "street");
      final List<Edge> _result = new ArrayList<Edge>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Edge _item;
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpStreet;
        if (_cursor.isNull(_cursorIndexOfStreet)) {
          _tmpStreet = null;
        } else {
          _tmpStreet = _cursor.getString(_cursorIndexOfStreet);
        }
        _item = new Edge(_tmpId,_tmpStreet);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<Edge>> getAllLive() {
    final String _sql = "SELECT * FROM `edges`";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"edges"}, false, new Callable<List<Edge>>() {
      @Override
      public List<Edge> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStreet = CursorUtil.getColumnIndexOrThrow(_cursor, "street");
          final List<Edge> _result = new ArrayList<Edge>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Edge _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpStreet;
            if (_cursor.isNull(_cursorIndexOfStreet)) {
              _tmpStreet = null;
            } else {
              _tmpStreet = _cursor.getString(_cursorIndexOfStreet);
            }
            _item = new Edge(_tmpId,_tmpStreet);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
