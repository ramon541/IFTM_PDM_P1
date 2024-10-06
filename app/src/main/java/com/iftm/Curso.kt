package com.iftm

class Curso (codigo : Int, nome : String, nAlunos : Int, notaMEC : Float, area : String) {
    var codigo  : Int? = null
    var nome    : String
    var nAlunos : Int
    var notaMEC : Float
    var area    : String

    init {
        this.codigo  = codigo
        this.nome    = nome
        this.nAlunos = nAlunos
        this.notaMEC = notaMEC
        this.area    = area
    }

    override fun toString(): String {
        return("Código: ${this.codigo} - Nome: ${this.nome} - Número de Alunos: ${this.nAlunos} - Nota no MEC: ${this.notaMEC} - Área: ${this.area}")
    }

}