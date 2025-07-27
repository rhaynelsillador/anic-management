package com.sillador.strecs.utility;


public class StudentIdGenerator {

    private StudentIdGenerator(){}

    public static String generateStudentId(String prefix, String lastStudentId){
        long number;
        if(lastStudentId == null || lastStudentId.isEmpty()){
            number = 1L;
        }else{
            number = Long.parseLong(lastStudentId.replace(prefix+"-", ""))+1;
        }
        return prefix+"-"+ generateStudentId(number);
    }

    public static String generateStudentId(Long sequence) {
        return String.format("%07d", sequence);
    }
}
