package com.shine.controller.gamble.xcsl.util

import org.junit.Test

object MultipleRate {

    fun award777(array: IntArray): Boolean {
        if (array.toSet().size == 1 && array[0] == 1) {
            return true
        }
        return false
    }

    fun awardRB(array: IntArray): Boolean {
        if (array.copyOfRange(0, 2).toSet().size == 1 && array[0] == 1 && array[2] == 2) {
            return true
        }
        return false
    }

    fun awardLd(array: IntArray): Int {
        if (array.toSet().size == 1 && array[0] == 3) {
            return 15
        }
        return 0
    }


    fun awardPt(array: IntArray): Int {
        if (array.toSet().size == 1 && array[0] == 4) {
            return 7
        }
        return 0
    }


    fun awardREPLAY(array: IntArray): Boolean {
        if (array.toSet().size == 1 && array[0] == 6) {
            return true
        }
        return false
    }
}
