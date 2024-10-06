package com.iftm

import android.content.ContentValues
import android.util.Log

class DAO(banco : Banco) {
    var banco : Banco

    init { this.banco = banco }

    fun insert(curso : Curso) {
        val db       = this.banco.writableDatabase
        val cvValues = ContentValues().apply {
            put("nome"   , curso.nome)
            put("nAlunos", curso.nAlunos)
            put("notaMEC", curso.notaMEC)
            put("area"   , curso.area)
        }

        val res = db.insert("CURSO", null, cvValues)
        Log.i("DataBase", "Inserção: $res")
    }

    fun getAll() : ArrayList<Curso> {
        val cursoList = ArrayList<Curso>()
        val db        = this.banco.readableDatabase
        val cursor    = db.rawQuery("SELECT * FROM CURSO", null)

        with(cursor) {
            while (moveToNext()) {
                val codigo  = getInt(getColumnIndexOrThrow("CODIGO"))
                val nome    = getString(getColumnIndexOrThrow("NOME"))
                val nAlunos = getInt(getColumnIndexOrThrow("NALUNOS"))
                val notaMEC = getFloat(getColumnIndexOrThrow("NOTAMEC"))
                val area    = getString(getColumnIndexOrThrow("AREA"))

                cursoList.add(Curso(codigo, nome, nAlunos, notaMEC, area))
            }
        }
        cursor.close()
        return(cursoList)
    }

    fun update(curso : Curso) {
        val db       = this.banco.writableDatabase
        val cvValues = ContentValues().apply {
            put("nome"   , curso.nome)
            put("nAlunos", curso.nAlunos)
            put("notaMEC", curso.notaMEC)
            put("area"   , curso.area)
        }
        val condicao = "CODIGO = ${curso.codigo}"

        db.update("CURSO", cvValues, condicao, null)
    }

    fun delete(codigo : String) {
        val db       = this.banco.writableDatabase
        val condicao = "CODIGO = $codigo"

        db.delete("CURSO", condicao, null)
    }

    fun getTotalStudents(): Int {
        val db     = this.banco.writableDatabase
        val cursor = db.rawQuery("SELECT SUM(NALUNOS) FROM CURSO", null)

        return if (cursor.moveToFirst()) {
            val total = cursor.getInt(0)
            cursor.close()
            total
        } else {
            cursor.close()
            0
        }
    }

    fun fetchElementById(id: Int): ArrayList<Curso> {
        val cursoList = ArrayList<Curso>()
        val db        = this.banco.readableDatabase
        val cursor    = db.rawQuery("SELECT * FROM CURSO WHERE CODIGO = $id", null)

        with(cursor) {
            while (moveToNext()) {
                val codigo  = getInt(getColumnIndexOrThrow("CODIGO"))
                val nome    = getString(getColumnIndexOrThrow("NOME"))
                val nAlunos = getInt(getColumnIndexOrThrow("NALUNOS"))
                val notaMEC = getFloat(getColumnIndexOrThrow("NOTAMEC"))
                val area    = getString(getColumnIndexOrThrow("AREA"))

                cursoList.add(Curso(codigo, nome, nAlunos, notaMEC, area))
            }
        }
        cursor.close()
        return(cursoList)
    }

    fun orderByNAlunos(): ArrayList<Curso> {
        val cursoList = ArrayList<Curso>()
        val db        = this.banco.readableDatabase
        val cursor    = db.rawQuery("SELECT * FROM CURSO ORDER BY NALUNOS DESC", null)

        with(cursor) {
            while (moveToNext()) {
                val codigo  = getInt(getColumnIndexOrThrow("CODIGO"))
                val nome    = getString(getColumnIndexOrThrow("NOME"))
                val nAlunos = getInt(getColumnIndexOrThrow("NALUNOS"))
                val notaMEC = getFloat(getColumnIndexOrThrow("NOTAMEC"))
                val area    = getString(getColumnIndexOrThrow("AREA"))

                cursoList.add(Curso(codigo, nome, nAlunos, notaMEC, area))
            }
        }
        cursor.close()
        return(cursoList)
    }
}