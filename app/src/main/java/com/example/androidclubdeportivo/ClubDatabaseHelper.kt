package com.example.androidclubdeportivo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ClubDatabaseHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object {
        private val DATABASE_NAME = "HORARIOS.db"
        private val DATABASE_VERSION= 1
        private val TABLE_TIMES = "Horarios"
      //HAY QUE FIJARSE ESTE ID, LO HAGO COPIANDO LO QUE HIZO KEVIN
        private val COLUMN_ID = "id"
        //ACÁ ALGUNAS COLUMNAS QUE SERÍAN PARA NUESTRA BD DE HORARIOS
        private val COLUMN_CLUB = "sede"
        private val COLUMN_DAY = "día"
        private val COLUMN_TIME = "horario"
        private val COLUMN_TEACHER = "profesor"
    }

    override fun onCreate(db: SQLiteDatabase) {
       val createTable= ("CREATE TABLE " + TABLE_TIMES + "("
               + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
               + COLUMN_CLUB + " TEXT, "
               + COLUMN_DAY + " TEXT, "
               + COLUMN_TIME + " INTEGER, "
               + COLUMN_TEACHER + " TEXT) ")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TIMES)
        onCreate(db)
    }



}