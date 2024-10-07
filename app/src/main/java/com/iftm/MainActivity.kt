package com.iftm

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    lateinit var et_codigo         : EditText
    lateinit var et_nome           : EditText
    lateinit var et_nAlunos        : EditText
    lateinit var et_notaMEC        : EditText
    lateinit var rg_areas          : RadioGroup
    lateinit var bt_inserir        : Button
    lateinit var bt_atualizar      : Button
    lateinit var bt_excluir        : Button
    lateinit var lv_cursos         : ListView
    lateinit var et_filterCodigo   : EditText
    lateinit var ib_filterCodigo   : ImageButton
    lateinit var bt_ordenar        : Button
    lateinit var bt_limparFiltros  : Button
    lateinit var tv_totAlunos      : TextView
    lateinit var bt_criarBackup    : Button
    lateinit var bt_carregarBackup : Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_codigo         = findViewById(R.id.et_codigo)
        et_nome           = findViewById(R.id.et_nome)
        et_nAlunos        = findViewById(R.id.et_nAlunos)
        et_notaMEC        = findViewById(R.id.et_notaMEC)
        rg_areas          = findViewById(R.id.rg_areas)
        bt_inserir        = findViewById(R.id.bt_inserir)
        bt_atualizar      = findViewById(R.id.bt_atualizar)
        bt_excluir        = findViewById(R.id.bt_excluir)
        lv_cursos         = findViewById(R.id.lv_cursos)
        et_filterCodigo   = findViewById(R.id.et_filterCodigo)
        ib_filterCodigo   = findViewById(R.id.ib_filterCodigo)
        bt_ordenar        = findViewById(R.id.bt_ordenar)
        bt_limparFiltros  = findViewById(R.id.bt_limparFiltros)
        tv_totAlunos      = findViewById(R.id.tv_totAlunos)
        bt_criarBackup    = findViewById(R.id.bt_criarBackup)
        bt_carregarBackup = findViewById(R.id.bt_carregarBackup)

        val myDataBase = Banco(applicationContext)
        val dao        = DAO(myDataBase)
        showCursos(dao)
        insertMode()

        //--------------------------------------------------------------------------------------
        bt_inserir.setOnClickListener {
            if (isInputsValid()) {
                val selectedOption = rg_areas.checkedRadioButtonId
                val rbArea         = findViewById<RadioButton>(selectedOption)

                val curso = Curso(
                    0
                    , et_nome   .text.toString()
                    , et_nAlunos.text.toString().toInt()
                    , et_notaMEC.text.toString().toFloat()
                    , rbArea    .text.toString()
                )

                dao.insert(curso)
                showCursos(dao)
                clearInputs()

                Toast.makeText(
                      applicationContext
                    , "Curso inserido!"
                    , Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    applicationContext
                    , "Preencha todos os campos para poder inserir!"
                    , Toast.LENGTH_LONG
                ).show()
            }
        }

        //--------------------------------------------------------------------------------------
        lv_cursos.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            editMode()
            val text : String = parent.getItemAtPosition(position).toString()
            val separateData  = text.split(" - ").toTypedArray()

            val tagToFind = separateData[4].split(": ")[1][0]
            for (i in 0 until rg_areas.childCount) {
                val radioButton = rg_areas.getChildAt(i) as RadioButton

                if (radioButton.tag.equals(tagToFind.toString())) {
                    rg_areas.check(radioButton.id)
                    break
                }
            }

            et_codigo .setText(separateData[0].split(": ")[1])
            et_nome   .setText(separateData[1].split(": ")[1])
            et_nAlunos.setText(separateData[2].split(": ")[1])
            et_notaMEC.setText(separateData[3].split(": ")[1])
        }

        //--------------------------------------------------------------------------------------
        bt_atualizar.setOnClickListener {
            if (isInputsValid()) {
                val selectedOption = rg_areas.checkedRadioButtonId
                val rbArea = findViewById<RadioButton>(selectedOption)
                val curso = Curso(
                      et_codigo .text.toString().toInt()
                    , et_nome   .text.toString()
                    , et_nAlunos.text.toString().toInt()
                    , et_notaMEC.text.toString().toFloat()
                    , rbArea    .text.toString()
                )

                dao.update(curso)
                showCursos(dao)
                clearInputs()

                Toast.makeText(
                      applicationContext
                    , "Curso editado!"
                    , Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                      applicationContext
                    , "Preencha todos os campos para poder editar!"
                    , Toast.LENGTH_LONG
                ).show()
            }
        }

        //--------------------------------------------------------------------------------------
        bt_excluir.setOnClickListener {
            dao.delete(et_codigo.text.toString())
            showCursos(dao)
            clearInputs()

            Toast.makeText(
                  applicationContext
                , "Curso deletado!"
                , Toast.LENGTH_LONG
            ).show()
        }

        //--------------------------------------------------------------------------------------
        ib_filterCodigo.setOnClickListener {
            if (et_filterCodigo.text.toString().isNotEmpty()) {
                showCursoById(dao)
            } else {
                Toast.makeText(
                    applicationContext
                    , "Informe um código para filtrar!"
                    , Toast.LENGTH_LONG
                ).show()
            }

        }

        //--------------------------------------------------------------------------------------
        bt_ordenar.setOnClickListener {
            orderByTotAlunos(dao)
            Toast.makeText(
                applicationContext
                , "Ordenado pelo número de Alunos."
                , Toast.LENGTH_LONG
            ).show()
        }

        //--------------------------------------------------------------------------------------
        bt_limparFiltros.setOnClickListener {
            showCursos(dao)
            Toast.makeText(
                applicationContext
                , "Filtros limpos!"
                , Toast.LENGTH_LONG
            ).show()
        }

        //--------------------------------------------------------------------------------------
        bt_criarBackup.setOnClickListener {
            dbBackup(dao)
        }

        //--------------------------------------------------------------------------------------
        bt_carregarBackup.setOnClickListener {
            carregarBackup(dao)
        }
    }

    //--------------------------------------------------------------------------------------
    private fun showCursos(dao : DAO) {
        var cursosList    = dao.getAll()
        var adapter       = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, cursosList)
        lv_cursos.adapter = adapter

        setTotAlunos(dao)
    }

    //--------------------------------------------------------------------------------------
    private fun clearInputs() {
        et_codigo .text.clear()
        et_nome   .text.clear()
        et_nAlunos.text.clear()
        et_notaMEC.text.clear()
        rg_areas  .clearCheck()

        insertMode()
    }

    //--------------------------------------------------------------------------------------
    private fun insertMode() {
        et_codigo.isEnabled = false

        bt_inserir  .setBackgroundColor(ContextCompat.getColor(this, R.color.purple))
        bt_inserir  .isEnabled = true
        bt_inserir  .isClickable = true

        bt_atualizar.setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.material_on_background_disabled))
        bt_atualizar.isEnabled = false

        bt_excluir  .setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.material_on_background_disabled))
        bt_excluir  .isEnabled = false

    }

    //--------------------------------------------------------------------------------------
    private fun editMode() {
        bt_inserir  .setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.material_on_background_disabled))
        bt_inserir  .isEnabled = false

        bt_atualizar.setBackgroundColor(ContextCompat.getColor(this, R.color.purple))
        bt_atualizar.isEnabled = true

        bt_excluir  .setBackgroundColor(ContextCompat.getColor(this, R.color.purple))
        bt_excluir  .isEnabled = true
    }

    //--------------------------------------------------------------------------------------
    private fun isInputsValid(): Boolean {
        val selectedOption = rg_areas.checkedRadioButtonId

        val hasNome    = et_nome.text.toString().isNotEmpty()
        val hasNAlunos = et_nAlunos.text.toString().isNotEmpty()
        val hasNotaMEC = et_notaMEC.text.toString().isNotEmpty()
        val hasArea    = selectedOption != -1

        return hasArea && hasNome && hasNAlunos && hasNotaMEC
    }

    //--------------------------------------------------------------------------------------
    private fun setTotAlunos(dao : DAO) {
        val totAlunos = dao.getTotalStudents();

        tv_totAlunos.setText("Total de alunos: $totAlunos")
    }

    //--------------------------------------------------------------------------------------
    private fun showCursoById(dao : DAO) {
        var cursosList    = dao.fetchElementById(et_filterCodigo.text.toString().toInt())
        var adapter       = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, cursosList)
        lv_cursos.adapter = adapter

        et_filterCodigo.text.clear()
    }

    //--------------------------------------------------------------------------------------
    private fun orderByTotAlunos(dao : DAO) {
        var cursosList    =  dao.orderByNAlunos()
        var adapter       = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, cursosList)
        lv_cursos.adapter = adapter
    }

    //--------------------------------------------------------------------------------------
    private fun dbBackup(dao : DAO) {
        try {
            val backupFile = File(applicationContext.filesDir, "backup_cursos.txt")
            val cursos = dao.getAll()

            if (backupFile.exists()) {
                backupFile.writeText("") // Limpa o conteúdo do arquivo
            }

            val fileOutputStream = FileOutputStream(backupFile, true)

            for (curso in cursos) {
                fileOutputStream.write("$curso\n".toByteArray())
            }

            fileOutputStream.flush()
            fileOutputStream.close()

            Toast.makeText(
                applicationContext
                , "Backup criado com sucesso!"
                , Toast.LENGTH_LONG
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext
                , "Erro ao criar backup!"
                , Toast.LENGTH_LONG
            ).show()
        }
    }

    //--------------------------------------------------------------------------------------
    private fun carregarBackup(dao : DAO) {
        val backupFile = File(applicationContext.filesDir, "backup_cursos.txt")

        if (backupFile.exists()) {
            val lines = backupFile.readLines()

            for (line in lines) {
                val separateData  = line.split(" - ").toTypedArray()

                val codigo  = separateData[0].split(": ")[1].toInt()
                val nome    = separateData[1].split(": ")[1]
                val nAlunos = separateData[2].split(": ")[1].toInt()
                val notaMEC = separateData[3].split(": ")[1].toFloat()
                val area    = separateData[4].split(": ")[1]

                dao.updateBkp(Curso(codigo, nome, nAlunos, notaMEC, area))
            }

            showCursos(dao)

            Toast.makeText(
                applicationContext
                , "Cursos atualizados com sucesso!"
                , Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                applicationContext
                , "Arquivo de backup não encontrado!"
                , Toast.LENGTH_LONG
            ).show()
        }
    }
 }