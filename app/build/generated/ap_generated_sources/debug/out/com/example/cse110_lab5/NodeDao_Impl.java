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
public final class NodeDao_Impl implements NodeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Node> __insertionAdapterOfNode;

  private final EntityDeletionOrUpdateAdapter<Node> __deletionAdapterOfNode;

  private final EntityDeletionOrUpdateAdapter<Node> __updateAdapterOfNode;

  public NodeDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNode = new EntityInsertionAdapter<Node>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `nodes` (`id`,`kind`,`name`,`tags`) VALUES (?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Node value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
        if (value.kind == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.kind);
        }
        if (value.name == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.name);
        }
        final String _tmp = Converters.fromArrayList(value.tags);
        if (_tmp == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, _tmp);
        }
      }
    };
    this.__deletionAdapterOfNode = new EntityDeletionOrUpdateAdapter<Node>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `nodes` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Node value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
      }
    };
    this.__updateAdapterOfNode = new EntityDeletionOrUpdateAdapter<Node>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `nodes` SET `id` = ?,`kind` = ?,`name` = ?,`tags` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Node value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
        if (value.kind == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.kind);
        }
        if (value.name == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.name);
        }
        final String _tmp = Converters.fromArrayList(value.tags);
        if (_tmp == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, _tmp);
        }
        if (value.id == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.id);
        }
      }
    };
  }

  @Override
  public long insert(final Node node) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfNode.insertAndReturnId(node);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Long> insertAll(final List<Node> nodes) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      List<Long> _result = __insertionAdapterOfNode.insertAndReturnIdsList(nodes);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final Node node) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__deletionAdapterOfNode.handle(node);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final Node node) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__updateAdapterOfNode.handle(node);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public Node get(final String id) {
    final String _sql = "SELECT * FROM `nodes` WHERE `id`=?";
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
      final int _cursorIndexOfKind = CursorUtil.getColumnIndexOrThrow(_cursor, "kind");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
      final Node _result;
      if(_cursor.moveToFirst()) {
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpKind;
        if (_cursor.isNull(_cursorIndexOfKind)) {
          _tmpKind = null;
        } else {
          _tmpKind = _cursor.getString(_cursorIndexOfKind);
        }
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final List<String> _tmpTags;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfTags)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfTags);
        }
        _tmpTags = Converters.fromString(_tmp);
        _result = new Node(_tmpId,_tmpKind,_tmpName,_tmpTags);
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
  public List<Node> getAll() {
    final String _sql = "SELECT * FROM `nodes`";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfKind = CursorUtil.getColumnIndexOrThrow(_cursor, "kind");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
      final List<Node> _result = new ArrayList<Node>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Node _item;
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        final String _tmpKind;
        if (_cursor.isNull(_cursorIndexOfKind)) {
          _tmpKind = null;
        } else {
          _tmpKind = _cursor.getString(_cursorIndexOfKind);
        }
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final List<String> _tmpTags;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfTags)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfTags);
        }
        _tmpTags = Converters.fromString(_tmp);
        _item = new Node(_tmpId,_tmpKind,_tmpName,_tmpTags);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<Node>> getAllLive() {
    final String _sql = "SELECT * FROM `nodes`";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"nodes"}, false, new Callable<List<Node>>() {
      @Override
      public List<Node> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfKind = CursorUtil.getColumnIndexOrThrow(_cursor, "kind");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final List<Node> _result = new ArrayList<Node>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Node _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpKind;
            if (_cursor.isNull(_cursorIndexOfKind)) {
              _tmpKind = null;
            } else {
              _tmpKind = _cursor.getString(_cursorIndexOfKind);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final List<String> _tmpTags;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfTags);
            }
            _tmpTags = Converters.fromString(_tmp);
            _item = new Node(_tmpId,_tmpKind,_tmpName,_tmpTags);
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
