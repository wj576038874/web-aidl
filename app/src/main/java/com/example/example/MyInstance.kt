package com.example.example

class MyInstance private constructor() {

    private var num = 0


    fun getNum(): Int {
        return num
    }

    fun setNum(num: Int) {
        this.num = num
    }



    companion object {

        private var instance: MyInstance? = null

        fun getInstance(): MyInstance {
            return instance ?: synchronized(MyInstance::class.java) {
                instance ?: MyInstance().also {
                    instance = it
                }
            }
        }
    }

}