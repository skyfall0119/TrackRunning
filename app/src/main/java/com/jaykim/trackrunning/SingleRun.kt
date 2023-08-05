package com.jaykim.trackrunning


// singleRun data 저장.
//휴식일때는 0,0,휴식분
//lapTime 은 0 제거하고 distance X laps. 구조를 좀더 생각해봐야됨.


data class SingleRun(val date : String, val distance: List<Integer>, val laps : List<Integer>,
                     val rest : List<Integer>, val lapTime : List<String>){
}





