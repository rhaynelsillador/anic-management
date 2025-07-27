package com.sillador.strecs.strecs.utility;


import com.sillador.strecs.utility.StudentIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StudentIdGeneratorTest {

    private static  final String PREFIX = "MFS";
    @Test
    public void generateStudentIdWithNullTest(){
        Assertions.assertEquals(PREFIX+"-0000001", StudentIdGenerator.generateStudentId(PREFIX, null));

        Assertions.assertEquals(PREFIX+"-0000002", StudentIdGenerator.generateStudentId(PREFIX, PREFIX+"-0000001"));

        Assertions.assertEquals(PREFIX+"-0000011", StudentIdGenerator.generateStudentId(PREFIX, PREFIX+"-0000010"));
    }

}
