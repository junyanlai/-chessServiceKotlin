package com.shine.controller.gamble.laohuji.util

object MultipleRate {
    /**
     * 原赔率太高，需要正调整 调整结果如下
     * 0->5 减0.5
     * 1->5 减0.5
     * 2->5 减1
     * 3->5 减1
     * 4->5 减1
     * 5->5 减1
     * 6->5 减1
     * 7->5 减1
     * 9->5 减1
     * 10->5 减1
     */
    fun getRate(num: Int, count: Int): Double {
        when (num) {
            0 -> {
                when (count) {
                    5 -> return 1.0
                    4 -> return 0.25
                    3 -> return 0.12
                    2 -> return 0.05
                }
            }

            1 -> {
                when (count) {
                    5 -> return 1.0
                    4 -> return 0.25
                    3 -> return 0.12
                }
            }

            2 -> {
                when (count) {
                    5 -> return 1.0
                    4 -> return 0.5
                    3 -> return 0.12
                }
            }

            3 -> {
                when (count) {
                    5 -> return 1.0
                    4 -> return 0.5
                    3 -> return 0.12
                }
            }

            4 -> {
                when (count) {
                    5 -> return 1.0
                    4 -> return 0.12
                    3 -> return 0.25
                }
            }

            5 -> {
                when (count) {
                    5 -> return 1.0
                    4 -> return 1.25
                    3 -> return 0.25
                }
            }

            6 -> {
                when (count) {
                    5 -> return 2.0
                    4 -> return 4.0
                    3 -> return 0.5
                }
            }

            7 -> {
                when (count) {
                    5 -> return 2.0
                    4 -> return 2.0
                    3 -> return 0.5
                }
            }

            8 -> {
                when (count) {
                    5 -> return 2.75
                    4 -> return 2.5
                    3 -> return 1.25
                }
            }

            9 -> {
                when (count) {
                    5 -> return 2.75
                    4 -> return 2.5
                    3 -> return 1.25
                }
            }

            10 -> {
                when (count) {
                    5 -> return 6.75
                    4 -> return 2.5
                    3 -> return 1.25
                    2 -> return 0.25
                }
            }
            else -> return 0.0
        }

        return 0.0
    }

    fun lineOfone(list: ArrayList<ArrayList<Pair<Int, Int>>>, num: Int): ArrayList<String> {
        var line = ArrayList<String>()
        list[0].forEach {
            var str = StringBuffer()

            str.append("${num},0_")     // 初始化坐标
            str.append("${it.first},${it.second}")
            line.add("${str}")
        }
        return line
    }


    fun lineOftwo(list: ArrayList<ArrayList<Pair<Int, Int>>>, num: Int): ArrayList<String> {
        var line = ArrayList<String>()
        list[0].forEach {
            var str = StringBuffer()

            str.append("${num},0_")     // 初始化坐标
            str.append("${it.first},${it.second}")
            list[1].forEach {
                line.add("${str}_${it.first},${it.second}")
            }
        }
        return line
    }

    fun lineOfthree(list: ArrayList<ArrayList<Pair<Int, Int>>>, num: Int): ArrayList<String> {
        var line = ArrayList<String>()
        list[0].forEach {
            var str_1 = StringBuffer()

            str_1.append("${num},0_")     // 初始化坐标
            str_1.append("${it.first},${it.second}")
            str_1.append("_")
            list[1].forEach {
                var str_2 = StringBuffer()
                str_2.append("${str_1}${it.first},${it.second}")
                list[2].forEach {
                    line.add("${str_2}_${it.first},${it.second}")
                }
            }
        }
        return line
    }

    fun lineOFour(list: ArrayList<ArrayList<Pair<Int, Int>>>, num: Int): ArrayList<String> {
        var line = ArrayList<String>()
        list[0].forEach {
            var str_1 = StringBuffer()

            str_1.append("${num},0_")     // 初始化坐标
            str_1.append("${it.first},${it.second}")
            str_1.append("_")
            list[1].forEach {
                var str_2 = StringBuffer()
                str_2.append("${str_1}${it.first},${it.second}")
                str_2.append("_")
                list[2].forEach {
                    var str_3 = StringBuffer()
                    str_3.append("${str_2}${it.first},${it.second}")
                    str_3.append("_")

                    list[3].forEach {
                        line.add("${str_3}${it.first},${it.second}")
                    }
                }
            }
        }
        return line
    }
}
