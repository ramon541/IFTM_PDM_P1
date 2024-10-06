package com.iftm

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Banco(context: Context) : SQLiteOpenHelper(context, "iftmDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {

        val sqlCreate = """
            CREATE TABLE IF NOT EXISTS  CURSO (
                CODIGO       INTEGER     PRIMARY KEY     AUTOINCREMENT,
                NOME         TEXT        NOT NULL,
                NALUNOS      INTEGER     NOT NULL,
                NOTAMEC      REAL        NOT NULL,
                AREA         TEXT        NOT NULL
            )
        """.trimIndent()

        db.execSQL(sqlCreate)

        }
    }

    override fun onUpgrade(db: SQLiteDatabase, versaoAntiga: Int, novaVersao: Int) {
    }
}